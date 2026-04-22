#!/usr/bin/env python3
"""Migrate mod fishing JSONs to full SDV parity.

For each fish entry in src/main/resources/data/stardewcraft/fishing/locations/*.json:
- chance         <- SDV Locations[loc].Fish[?].Chance               (mostly 1.0)
- precedence     <- SDV Locations[loc].Fish[?].Precedence           (mostly 0)
- spawnRate      <- SDV Fish.json[id][10]                            (the per-fish base rate)
- maxDepth       <- SDV Fish.json[id][9]
- depthMultiplier<- SDV Fish.json[id][11]
- minFishingLevel<- SDV Fish.json[id][12]                            (NOT Locations[?].MinFishingLevel,
                                                                       which is location-extra requirement)
- difficulty     <- SDV Fish.json[id][1]
- timeRanges     <- SDV Fish.json[id][5] parsed pairs
- weather        <- SDV Fish.json[id][7]   ("both" -> "any")
- seasons        <- SDV Fish.json[id][6] split

If a fish has a Locations.MinFishingLevel that's higher than the Fish.json one, the Locations one wins
(SDV checks both: the depth formula uses Fish.json minLvl, but the location filter uses Locations.MinFishingLevel).
We keep the SDV semantic: store Locations.MinFishingLevel in `minFishingLevel`, and Fish.json minLvl
will be re-derived in the depth formula step (we just ensure the rule's minFishingLevel >= max(loc, fish)).

Preserves existing fields like biomeTags, motionType, biomes, fishAreaId, condition, isBossFish, catchLimit.
"""
from __future__ import annotations
import json, re, sys
from pathlib import Path
from collections import OrderedDict

ROOT = Path(__file__).resolve().parents[1]
MOD_DIR = ROOT / "src/main/resources/data/stardewcraft/fishing/locations"
SDV_LOC = ROOT / "源文件/Content/Data/Locations.json"
SDV_FISH = ROOT / "源文件/Content/Data/Fish.json"
SDV_OBJ = ROOT / "src/main/resources/data/stardewcraft/npc/vanilla/data/Objects.json"

MOD_TO_SDV_LOC = {
    "beach.json": ["Beach"],
    "town.json": ["Town"],
    "forest.json": ["Forest"],
    "mountain.json": ["Mountain"],
    "desert.json": ["Desert"],
    "sewer.json": ["Sewer"],
    "woods.json": ["Woods"],
    "witchswamp.json": ["WitchSwamp"],
    "bugland.json": ["MutantBugLair", "BugLand"],
    "submarine.json": ["Submarine"],
    "caldera.json": ["Caldera"],
    "undergroundmine.json": ["UndergroundMine"],
    "islandnorth.json": ["IslandNorth"],
    "islandsouth.json": ["IslandSouth"],
    "islandsoutheast.json": ["IslandSouthEast"],
    "islandsoutheastcave.json": ["IslandSouthEastCave"],
    "islandwest.json": ["IslandWest"],
    "beachnightmarket.json": ["BeachNightMarket"],
    # default.json contains universal entries (jellies + trash). They use Default location.
    "default.json": ["Default"],
    # overworld.json + stardew_valley.json — mod umbrellas; map to Default fallback
    "overworld.json": ["Default"],
    "stardew_valley.json": ["Default"],
}

def snake_to_pascal(s: str) -> str:
    return ''.join(p.capitalize() for p in s.split('_'))

def snake_to_sdv_name(s: str) -> str:
    """Convert snake_case mod id to SDV Name (space-separated, capitalized)."""
    # Special-case mappings where SDV name doesn't follow simple pattern
    overrides = {
        "ms_angler": "Ms. Angler",
        "son_of_crimsonfish": "Son of Crimsonfish",
        "glacierfish_jr": "Glacierfish Jr.",
        "legend_ii": "Legend II",
        "ice_pip": "Ice Pip",
    }
    if s in overrides:
        return overrides[s]
    return ' '.join(p.capitalize() for p in s.split('_'))

def pascal_to_snake(s: str) -> str:
    s = re.sub(r'(?<!^)(?=[A-Z])', '_', s).lower()
    return s

def normalize_sdv_id(item_id: str) -> str:
    m = re.match(r'^\([A-Z]\)(.+)$', item_id)
    return m.group(1) if m else item_id

def load_objects_id_to_name() -> dict[str, str]:
    """Build {numericIdString or InternalName -> InternalName} map."""
    with SDV_OBJ.open(encoding="utf-8-sig") as f:
        data = json.load(f)
    out = {}
    for key, val in data.items():
        if not isinstance(val, dict): continue
        name = val.get("Name") or key
        out[str(key)] = name
        out[name] = name
    return out

def load_fish_data() -> dict[str, list]:
    """Build {numericId -> split parts} from Fish.json."""
    with SDV_FISH.open(encoding="utf-8-sig") as f:
        raw = json.load(f)
    out = {}
    for nid, line in raw.items():
        out[str(nid)] = line.split("/")
    return out

def load_sdv_locations() -> dict[str, list[dict]]:
    with SDV_LOC.open(encoding="utf-8-sig") as f:
        data = json.load(f)
    out = {}
    for name, entry in data.items():
        if isinstance(entry, dict) and entry.get("Fish") is not None:
            out[name] = entry["Fish"]
    return out

def parse_time_pairs(raw: str) -> list[list[int]]:
    """SDV Fish.json[5]: 'startA endA startB endB ...' -> [[startA,endA],...]."""
    if not raw or raw.strip() == "":
        return []
    parts = raw.split()
    pairs = []
    for i in range(0, len(parts) - 1, 2):
        try:
            pairs.append([int(parts[i]), int(parts[i+1])])
        except ValueError:
            pass
    return pairs

def parse_seasons(raw: str) -> list[str]:
    if not raw or raw.strip() == "" or raw.strip() == "any":
        return []
    seasons = raw.split()
    # SDV uses lower-case; strip non-season tokens
    valid = {"spring","summer","fall","winter"}
    return [s for s in seasons if s.lower() in valid]

def parse_weather(raw: str) -> str:
    raw = (raw or "").strip().lower()
    if raw in ("", "both", "any"):
        return "any"
    return raw  # "sunny" / "rainy"

def find_id_for_mod_item(mod_item_id: str, id_to_name: dict[str, str], fish_data: dict[str, list], objects: dict) -> tuple[str | None, str | None]:
    """Given mod item like 'stardewcraft:sardine', return (numeric_fish_id, internal_name).

    objects: full Objects.json dict for reverse lookup name->numeric id.
    """
    short = mod_item_id.split(":")[-1]
    pascal = snake_to_pascal(short)
    # Find numeric id via Objects.json by Name match
    for key, val in objects.items():
        if isinstance(val, dict) and val.get("Name") == pascal:
            if str(key) in fish_data:
                return str(key), pascal
            return None, pascal
    return None, pascal

def main():
    objects_dict = json.load(SDV_OBJ.open(encoding="utf-8-sig"))
    id_to_name = load_objects_id_to_name()
    fish_data = load_fish_data()
    sdv_locs = load_sdv_locations()

    print(f"Loaded {len(fish_data)} Fish.json entries, {len(sdv_locs)} SDV locations\n")

    total_updated = 0
    total_skipped = 0
    files_changed = []

    for mod_file in sorted(MOD_DIR.glob("*.json")):
        sdv_keys = MOD_TO_SDV_LOC.get(mod_file.name)
        if sdv_keys is None:
            print(f"SKIP {mod_file.name} (no mapping)")
            continue

        # Aggregate SDV fish entries by InternalName for this location
        sdv_by_name: dict[str, dict] = {}
        for k in sdv_keys:
            for entry in sdv_locs.get(k, []):
                raw = normalize_sdv_id(entry.get("ItemId") or entry.get("Id") or "")
                # Strip random-pool suffix "(O)X|(O)Y" by taking first
                raw = raw.split("|")[0]
                raw = re.sub(r'^\([A-Z]\)', '', raw)
                # raw could be numeric id OR internal pascal name; resolve to Name field
                obj = objects_dict.get(raw)
                if isinstance(obj, dict) and obj.get("Name"):
                    name = obj["Name"]
                else:
                    name = raw
                sdv_by_name.setdefault(name, entry)

        with mod_file.open(encoding="utf-8-sig") as f:
            data = json.load(f, object_pairs_hook=OrderedDict)

        rules_key = "fish" if "fish" in data else ("rules" if "rules" in data else None)
        if not rules_key:
            print(f"SKIP {mod_file.name} (no fish/rules array)")
            continue

        local_updated = 0
        local_skipped = []
        for rule in data[rules_key]:
            mod_item = rule.get("item", "")
            short = mod_item.split(":")[-1]
            sdv_name = snake_to_sdv_name(short)
            sdv_entry = sdv_by_name.get(sdv_name)

            # Look up Fish.json by numeric id (may exist even if location has no entry, e.g. mod's universal jellies)
            fish_id = None
            for key, val in objects_dict.items():
                if isinstance(val, dict) and val.get("Name") == sdv_name and str(key) in fish_data:
                    fish_id = str(key)
                    break
            fparts = fish_data.get(fish_id) if fish_id else None

            if sdv_entry is None and fparts is None:
                local_skipped.append(short + " (no SDV match)")
                continue

            # Apply SDV Locations fields
            if sdv_entry is not None:
                if sdv_entry.get("Chance") is not None:
                    rule["chance"] = float(sdv_entry["Chance"])
                if sdv_entry.get("Precedence") is not None:
                    rule["precedence"] = int(sdv_entry["Precedence"])
                # location-level minFishingLevel (overrides Fish.json if higher)
                loc_min = sdv_entry.get("MinFishingLevel", 0) or 0
                # SDV season filter is from Locations.Season ONLY (Fish.json[6] is not used by game logic)
                loc_season = sdv_entry.get("Season")
                if loc_season:
                    rule["seasons"] = [loc_season.lower()]
                else:
                    rule.pop("seasons", None)
            else:
                loc_min = 0

            # Apply Fish.json fields
            fish_min = 0
            if fparts is not None and len(fparts) >= 13:
                # Field positions confirmed from CheckGenericFishRequirements
                try: rule["difficulty"] = int(fparts[1])
                except: pass
                pairs = parse_time_pairs(fparts[5])
                if pairs:
                    rule["timeRanges"] = pairs
                # NOTE: Fish.json[6] season is NOT used by SDV game logic (see CheckGenericFishRequirements).
                # Only Locations.Season filters by season. We don't write seasons from Fish.json here.
                rule["weather"] = parse_weather(fparts[7])
                try: rule["maxDepth"] = int(fparts[9])
                except: pass
                try: rule["spawnRate"] = float(fparts[10])
                except: pass
                try: rule["depthMultiplier"] = float(fparts[11])
                except: pass
                try: fish_min = int(fparts[12])
                except: pass
                # field [13] = isTutorialFish (bool)

            rule["minFishingLevel"] = max(int(loc_min), fish_min)

            # Pull through useful Locations.json metadata
            if sdv_entry is not None:
                if sdv_entry.get("CatchLimit") is not None and sdv_entry["CatchLimit"] != -1:
                    rule["catchLimit"] = int(sdv_entry["CatchLimit"])
                if sdv_entry.get("RequireMagicBait"):
                    rule["requireMagicBait"] = True
                if sdv_entry.get("ApplyDailyLuck"):
                    rule["applyDailyLuck"] = True
                if sdv_entry.get("CuriosityLureBuff", -1.0) != -1.0:
                    rule["curiosityLureBuff"] = float(sdv_entry["CuriosityLureBuff"])
                if sdv_entry.get("FishAreaId"):
                    rule["fishAreaId"] = sdv_entry["FishAreaId"]
                if sdv_entry.get("IsBossFish"):
                    rule["isBossFish"] = True
                if sdv_entry.get("CanBeInherited") is False:
                    rule["canBeInherited"] = False
            local_updated += 1

        if local_updated:
            files_changed.append(mod_file.name)
            with mod_file.open("w", encoding="utf-8") as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
                f.write("\n")
        total_updated += local_updated
        total_skipped += len(local_skipped)
        if local_skipped:
            print(f"  {mod_file.name}: updated={local_updated}  skipped={len(local_skipped)}")
            for s in local_skipped[:8]:
                print(f"     - {s}")
        else:
            print(f"  {mod_file.name}: updated={local_updated}")

    print(f"\n=== Total: updated={total_updated}, skipped={total_skipped}, files changed={len(files_changed)} ===")

if __name__ == "__main__":
    main()
