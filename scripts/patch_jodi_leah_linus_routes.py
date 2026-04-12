#!/usr/bin/env python3
"""Add 19 route points + route profiles for Jodi, Leah, Linus."""
import json, pathlib

BASE = pathlib.Path(__file__).resolve().parent.parent
ROUTES = BASE / "src/main/resources/data/stardewcraft/npc/events"

def load(p):
    return json.loads(p.read_text("utf-8-sig"))

def save(p, d):
    p.write_text(json.dumps(d, indent=2, ensure_ascii=False) + "\n", "utf-8")

# ── 1. Route Points ──────────────────────────────────────
rp = load(ROUTES / "npc_route_points.json")
pts = rp["points"]

NEW_POINTS = {
    "_comment_jodi": {"note": "=== Jodi 专属停留点 ==="},
    "jodi_samhouse_kitchen":   {"x": 17106.0, "y": 71.0, "z": 17094.0, "indoor": True, "note": "Jodi SamHouse 厨房做饭（朝东）"},
    "jodi_samhouse_living":    {"x": 17098.0, "y": 71.0, "z": 17090.0, "indoor": True, "note": "Jodi SamHouse 客厅休息（朝南）"},
    "jodi_samhouse_sleep":     {"x": 17110.0, "y": 71.0, "z": 17114.0, "indoor": True, "note": "Jodi SamHouse 睡觉位（朝西）"},
    "jodi_samhouse_bedroom":   {"x": 17109.0, "y": 71.0, "z": 17111.0, "indoor": True, "note": "Jodi SamHouse 卧室日常（朝西）"},
    "jodi_seedshop_browse":    {"x": 12035.0, "y": 71.0, "z": 12051.0, "indoor": True, "note": "Jodi SeedShop 采购（朝北）"},
    "jodi_town_walk":          {"x": -117.0, "y": -17.0, "z": 48.0, "note": "Jodi 镇上散步（朝东）"},

    "_comment_leah": {"note": "=== Leah 专属停留点 ==="},
    "leah_house_easel":        {"x": 17097.0, "y": 71.0, "z": 18821.0, "indoor": True, "note": "Leah 画架前创作（朝东）"},
    "leah_house_kitchen":      {"x": 17093.0, "y": 71.0, "z": 18828.0, "indoor": True, "note": "Leah 厨房（朝西）"},
    "leah_house_sleep":        {"x": 17099.0, "y": 71.0, "z": 18818.0, "indoor": True, "note": "Leah 睡觉位（朝西）"},
    "leah_forest_forage":      {"x": 178.0, "y": -15.0, "z": -149.0, "note": "Leah 森林觅食（朝南）"},
    "leah_beach_sketch":       {"x": -287.0, "y": -14.0, "z": -174.0, "note": "Leah 海滩写生（朝北）"},
    "leah_museum_browse":      {"x": 13072.0, "y": 71.0, "z": 13068.0, "indoor": True, "note": "Leah 博物馆浏览（朝北）"},
    "leah_saloon_seat":        {"x": 14211.0, "y": 71.0, "z": 14220.0, "indoor": True, "note": "Leah 酒吧座位（朝南）"},

    "_comment_linus": {"note": "=== Linus 专属停留点 ==="},
    "linus_tent_fire":         {"x": -217.0, "y": -2.0, "z": 266.0, "note": "Linus 帐篷旁篝火（朝西）"},
    "linus_tent_inside":       {"x": -218.0, "y": -1.0, "z": 275.0, "note": "Linus 帐篷内部（朝南）"},
    "linus_tent_sleep":        {"x": -217.0, "y": -1.0, "z": 282.0, "note": "Linus 帐篷睡觉位（朝东）"},
    "linus_mountain_forage":   {"x": -277.0, "y": -12.0, "z": 205.0, "note": "Linus 山区觅食（朝东）"},
    "linus_beach_fish":        {"x": -260.0, "y": -15.0, "z": -220.0, "note": "Linus 海滩钓鱼（朝北）"},
    "linus_town_dumpster":     {"x": -171.0, "y": -18.0, "z": 17.0, "note": "Linus 镇上翻垃圾桶（朝东）"},
}

for k, v in NEW_POINTS.items():
    if k in pts:
        print(f"  [SKIP] {k} already exists")
    else:
        pts[k] = v
        print(f"  [ADD]  {k}")

save(ROUTES / "npc_route_points.json", rp)
print("✓ npc_route_points.json saved")

# ── 2. Route Profiles ────────────────────────────────────
rpf = load(ROUTES / "npc_route_profiles.json")
profiles = rpf["profiles"]

def walk(pt):
    return {"mode": "walk", "point": pt}
def warp(pt):
    return {"mode": "warp", "point": pt}

def indoor_profile(outdoor_door, indoor_entry, target):
    return [walk(outdoor_door), warp(indoor_entry), walk(target)]

JODI_PROFILES = {
    "samhouse": indoor_profile("samhouse_outdoor_door", "samhouse_indoor_entry", "jodi_samhouse_kitchen"),
    "seedshop": indoor_profile("seedshop_outdoor_door", "seedshop_indoor_entry", "jodi_seedshop_browse"),
    "pierreshop": indoor_profile("seedshop_outdoor_door", "seedshop_indoor_entry", "jodi_seedshop_browse"),
    "saloon": indoor_profile("saloon_outdoor_door", "saloon_indoor_entry", "saloon_indoor_entry"),
    "town": [walk("jodi_town_walk")],
}

LEAH_PROFILES = {
    "leahhouse": indoor_profile("leahhouse_outdoor_door", "leahhouse_indoor_entry", "leah_house_easel"),
    "saloon": indoor_profile("saloon_outdoor_door", "saloon_indoor_entry", "leah_saloon_seat"),
    "museum": indoor_profile("museum_outdoor_door", "museum_indoor_entry", "leah_museum_browse"),
    "archaeologyhouse": indoor_profile("museum_outdoor_door", "museum_indoor_entry", "leah_museum_browse"),
    "forest": [walk("leah_forest_forage")],
    "beach": [walk("leah_beach_sketch")],
    "town": [walk("leah_forest_forage")],
}

LINUS_PROFILES = {
    "mountain": [walk("linus_tent_fire")],
    "tent": [walk("linus_tent_fire")],
    "beach": [walk("linus_beach_fish")],
    "town": [walk("linus_town_dumpster")],
}

for npc_id, prof in [("jodi", JODI_PROFILES), ("leah", LEAH_PROFILES), ("linus", LINUS_PROFILES)]:
    if npc_id in profiles:
        print(f"  [SKIP] {npc_id} profiles already exist")
    else:
        profiles[npc_id] = prof
        print(f"  [ADD]  {npc_id} profiles ({len(prof)} locations)")

save(ROUTES / "npc_route_profiles.json", rpf)
print("✓ npc_route_profiles.json saved")
print("\nDone! Run ./gradlew classes to verify.")
