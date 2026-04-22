#!/usr/bin/env python3
"""
Regenerate mod fishing location JSONs from SDV truth.
Adds MISSING fish entries (in addition to updating existing ones via migrate script).
Reads existing mod file to preserve biomeTags per location, then adds any SDV fish
not yet present, mapping by InternalName -> snake_case mod item id.

This script is INSERT-only for missing entries. Use migrate_fishing_to_sdv.py for
updating values of existing entries.
"""
import json, os, re
from collections import OrderedDict

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SDV_DIR = os.path.join(ROOT, '源文件/Content/Data')
MOD_DIR = os.path.join(ROOT, 'src/main/resources/data/stardewcraft/fishing/locations')
ITEMS_JAVA = os.path.join(ROOT, 'src/main/java/com/stardew/craft/item/ModItems.java')

# Map mod file -> SDV location key (singular)
LOC_FILE_MAP = {
    'town.json': 'Town',
    'beach.json': 'Beach',
    'mountain.json': 'Mountain',
    'forest.json': 'Forest',
    'woods.json': 'Woods',
    'desert.json': 'Desert',
    'undergroundmine.json': 'UndergroundMine',
    'sewer.json': 'Sewer',
    'witchswamp.json': 'WitchSwamp',
    'bugland.json': 'BugLand',
    'caldera.json': 'Caldera',
    'submarine.json': 'Submarine',
    'beachnightmarket.json': 'BeachNightMarket',
    'islandnorth.json': 'IslandNorth',
    'islandsouth.json': 'IslandSouth',
    'islandsoutheast.json': 'IslandSouthEast',
    'islandsoutheastcave.json': 'IslandSouthEastCave',
    'islandwest.json': 'IslandWest',
}

# Default biomeTags fallback per location (used when mod file empty or no anchor entry)
DEFAULT_TAGS = {
    'Town': ['#stardewcraft:is_town_river'],
    'Beach': ['#stardewcraft:is_ocean'],
    'Mountain': ['#stardewcraft:is_mountain_lake'],
    'Forest': ['#stardewcraft:is_forest_pond', '#stardewcraft:is_forest_river'],
    'Woods': ['#stardewcraft:is_secret_woods'],
    'Desert': ['#stardewcraft:is_desert'],
    'UndergroundMine': ['#stardewcraft:is_mines_20', '#stardewcraft:is_mines_60', '#stardewcraft:is_mines_100'],
    'Sewer': ['#stardewcraft:is_sewers'],
    'WitchSwamp': ['#stardewcraft:is_witch_swamp'],
    'BugLand': ['#stardewcraft:is_mutant_bug_lair'],
    'Caldera': ['#stardewcraft:is_volcano'],
    'Submarine': ['#stardewcraft:is_night_market'],
    'BeachNightMarket': ['#stardewcraft:is_night_market'],
    'IslandNorth': ['#stardewcraft:is_ginger_island_river'],
    'IslandSouth': ['#stardewcraft:is_ginger_island_ocean'],
    'IslandSouthEast': ['#stardewcraft:is_ginger_island_ocean'],
    'IslandSouthEastCave': ['#stardewcraft:is_pirate_cove'],
    'IslandWest': ['#stardewcraft:is_ginger_island_ocean', '#stardewcraft:is_ginger_island_river'],
}

NAME_OVERRIDES = {
    'Ms. Angler': 'ms_angler',
    'Glacierfish Jr.': 'glacierfish_jr',
    'Legend II': 'legend_ii',
    'Son of Crimsonfish': 'son_of_crimsonfish',
    'Ice Pip': 'ice_pip',
}

def to_snake(name: str) -> str:
    if name in NAME_OVERRIDES:
        return NAME_OVERRIDES[name]
    s = re.sub(r'[^A-Za-z0-9]+', '_', name).strip('_').lower()
    return s

def parse_time_pairs(s: str):
    out = []
    if not s: return out
    nums = [int(x) for x in s.split() if x.isdigit()]
    for i in range(0, len(nums)-1, 2):
        out.append([nums[i], nums[i+1]])
    return out

def parse_weather(s: str) -> str:
    s = (s or '').strip().lower()
    if s in ('both','any',''): return 'any'
    if s == 'rainy': return 'rainy'
    if s == 'sunny': return 'sunny'
    return 'any'

def main():
    sdv_loc = json.load(open(os.path.join(SDV_DIR, 'Locations.json'), encoding='utf-8-sig'))
    sdv_fish = json.load(open(os.path.join(SDV_DIR, 'Fish.json'), encoding='utf-8-sig'))
    sdv_objs = json.load(open(os.path.join(SDV_DIR, 'Objects.json'), encoding='utf-8-sig'))

    # Build name -> Fish.json id (numeric)
    name_to_fish_id = {}
    for fid, raw in sdv_fish.items():
        nm = raw.split('/', 1)[0]
        name_to_fish_id[nm] = fid

    # Mod registered item IDs
    items_src = open(ITEMS_JAVA, encoding='utf-8').read()
    mod_items = set(re.findall(r'ITEMS\.register\(\s*"([a-z0-9_]+)"', items_src))

    skipped_no_item = []
    inserted_total = 0

    for fname, sdv_key in LOC_FILE_MAP.items():
        path = os.path.join(MOD_DIR, fname)
        if not os.path.exists(path):
            continue
        mod_data = json.load(open(path, encoding='utf-8-sig'))
        existing_ids = {r['id'] for r in mod_data.get('fish', [])}

        # Determine biomeTags fallback from first existing rule (if any) else default
        rules = mod_data.get('fish', [])
        anchor_tags = None
        for r in rules:
            if r.get('biomeTags'):
                anchor_tags = list(r['biomeTags'])
                break
        if anchor_tags is None:
            anchor_tags = DEFAULT_TAGS.get(sdv_key, [])

        sdv_entries = sdv_loc.get(sdv_key, {}).get('Fish', []) or []
        inserted_for_loc = []
        for entry in sdv_entries:
            iid = (entry.get('ItemId') or entry.get('Id') or '').strip()
            if not iid.startswith('(O)'):
                continue
            raw = iid[3:].split('|')[0]
            obj = sdv_objs.get(raw)
            if not isinstance(obj, dict):
                continue
            obj_name = obj.get('Name')
            if obj_name in ('Wood', 'Seaweed', 'Green Algae', 'White Algae'):
                # forageables / trash; we keep these too if mod has items, otherwise skip
                pass
            snake = to_snake(obj_name) if obj_name else None
            if not snake or snake not in mod_items:
                skipped_no_item.append(f'{fname}: {obj_name} (snake={snake})')
                continue
            if snake in existing_ids:
                continue  # already present, migrate script handles values
            # Build new rule
            fish_raw = sdv_fish.get(name_to_fish_id.get(obj_name, ''))
            new = OrderedDict()
            new['id'] = snake
            new['precedence'] = int(entry.get('Precedence', 0) or 0)
            new['item'] = f'stardewcraft:{snake}'
            new['chance'] = float(entry.get('Chance', 1.0) or 1.0)
            # Default fields from Fish.json
            difficulty = 20
            motion_id = 0
            timeRanges = [[600, 2600]]
            weather = 'any'
            maxDepth = 4
            depthMultiplier = 0.0
            spawnRate = 1.0
            if fish_raw:
                parts = fish_raw.split('/')
                # 0=name 1=difficulty 2=motion 3=minSize 4=maxSize 5=times 6=seasons(unused) 7=weather 8=light 9=maxDepth 10=spawnRate 11=depthMult 12=minLvl 13=tutorial
                try: difficulty = int(parts[1])
                except: pass
                motion_str = parts[2].lower() if len(parts) > 2 else ''
                motion_id = {'mixed':0,'dart':1,'smooth':2,'sinker':3,'floater':4}.get(motion_str, 0)
                timeRanges = parse_time_pairs(parts[5]) or [[600, 2600]]
                weather = parse_weather(parts[7])
                try: maxDepth = int(parts[9])
                except: pass
                try: spawnRate = float(parts[10])
                except: pass
                try: depthMultiplier = float(parts[11])
                except: pass
            new['difficulty'] = difficulty
            new['motionType'] = motion_id
            loc_min = int(entry.get('MinFishingLevel', 0) or 0)
            try: fish_min = int(parts[12]) if fish_raw else 0
            except: fish_min = 0
            new['minFishingLevel'] = max(loc_min, fish_min)
            # Season from Locations only
            loc_season = entry.get('Season')
            if loc_season:
                new['seasons'] = [loc_season.lower()]
            new['timeRanges'] = timeRanges
            new['weather'] = weather
            new['maxDepth'] = maxDepth
            new['depthMultiplier'] = depthMultiplier
            new['spawnRate'] = spawnRate
            new['biomeTags'] = list(anchor_tags)
            # Optional fields
            if entry.get('FishAreaId'):
                new['fishAreaId'] = entry['FishAreaId']
            if entry.get('Condition'):
                new['condition'] = entry['Condition']
            cl = entry.get('CatchLimit')
            if cl is not None and cl > 0:
                new['catchLimit'] = int(cl)
            if entry.get('IsBossFish'):
                new['isBossFish'] = True
            if entry.get('RequireMagicBait'):
                new['requireMagicBait'] = True
            if entry.get('ApplyDailyLuck'):
                new['applyDailyLuck'] = True
            cb = entry.get('CuriosityLureBuff')
            if cb is not None and cb > -1:
                new['curiosityLureBuff'] = float(cb)
            if entry.get('CanBeInherited') is False:
                new['canBeInherited'] = False
            inserted_for_loc.append(new)

        if inserted_for_loc:
            mod_data['fish'] = rules + inserted_for_loc
            with open(path, 'w', encoding='utf-8') as f:
                json.dump(mod_data, f, indent=2, ensure_ascii=False)
                f.write('\n')
            inserted_total += len(inserted_for_loc)
            print(f'  {fname}: +{len(inserted_for_loc)} ({", ".join(r["id"] for r in inserted_for_loc)})')

    print(f'\nTotal inserted: {inserted_total}')
    if skipped_no_item:
        print('\nSkipped (no mod item):')
        for s in skipped_no_item:
            print(f'  - {s}')

if __name__ == '__main__':
    main()
