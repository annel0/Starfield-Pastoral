#!/usr/bin/env python3
"""Fix 603 .desc entries in en_us.json that have placeholder values like 'Chub.desc'."""
import json, re, os

BASE = 'src/main/resources/assets/stardewcraft/lang'
with open(f'{BASE}/en_us.json') as f:
    en = json.load(f)
with open(f'{BASE}/zh_cn.json') as f:
    zh = json.load(f)

# ── Load ALL Stardew Valley descriptions ──
sv_desc = {}

def add_desc(name, desc):
    n = name.lower().replace(' ', '').replace('_', '').replace("'", '').replace('-', '')
    if desc and not desc.startswith('['):
        sv_desc[n] = desc

def load_string_descs(filepath):
    if not os.path.exists(filepath): return
    with open(filepath) as f:
        data = json.load(f)
    for k, v in data.items():
        if k.endswith('_Description'):
            add_desc(k[:-len('_Description')], v)

for sf in ['Objects', 'BigCraftables', 'Weapons', 'Tools', 'Furniture', '1_6_Strings']:
    load_string_descs(f'源文件/Content/Strings/{sf}.json')

for df in ['Weapons', 'Tools', 'BigCraftables', 'Objects']:
    fp = f'源文件/Content/Data/{df}.json'
    if not os.path.exists(fp): continue
    with open(fp) as f:
        data = json.load(f)
    for k, v in data.items():
        if isinstance(v, dict):
            add_desc(v.get('Name', k), v.get('Description', ''))

print(f'Loaded {len(sv_desc)} Stardew descriptions')

# ── Manual mappings ──
MANUAL = {
    # Mod blocks
    'block.stardewcraft.autofeed_trough.desc': 'Stores hay and auto-refills. Consumes hay from silos when empty.',
    'block.stardewcraft.barn_manager.desc': 'The core of the barn. View barn status and manage daily feeding.',
    'block.stardewcraft.cooking_pot.desc': 'A sturdy iron stove for cooking and decoration.',
    'block.stardewcraft.coop_manager.desc': 'The core of the coop. View coop status and manage daily feeding.',
    'block.stardewcraft.feed_trough.desc': 'Stores hay for feeding animals in coops and barns.',
    'block.stardewcraft.flooring_block.desc': 'Used to decorate room floors.',
    'block.stardewcraft.hay_hopper.desc': 'Dispenses hay from your silo for manual feeding.',
    'block.stardewcraft.mine_barrier.desc': 'A barrier found in the mines.',
    'block.stardewcraft.mine_exit.desc': 'An exit point in the mines.',
    'block.stardewcraft.mine_ladder.desc': 'A ladder leading deeper into the mines.',
    'block.stardewcraft.shipping_bin.desc': 'Place items inside to sell them overnight.',
    'block.stardewcraft.wallpaper_block.desc': 'Used to decorate room walls.',
    'block.stardewcraft.wooden_chest.desc': 'A handmade wooden chest for storing items.',
    # Fishing rods
    'item.stardewcraft.advanced_iridium_rod.desc': 'An advanced iridium rod. Can use bait and two tackles.',
    'item.stardewcraft.fiberglass_rod.desc': 'A step up from the bamboo pole. Can use bait.',
    'item.stardewcraft.fishing_rod.desc': 'A basic fishing rod made of bamboo.',
    'item.stardewcraft.iridium_rod.desc': 'The best fishing rod money can buy. Can use bait and tackle.',
    'item.stardewcraft.training_rod.desc': 'Makes it easier to catch fish. Great for beginners.',
    # Tools
    'item.stardewcraft.axe.desc': 'Used to chop wood.',
    'item.stardewcraft.copper_axe.desc': 'A copper-upgraded axe. Chops faster.',
    'item.stardewcraft.steel_axe.desc': 'A steel-upgraded axe. Chops even faster.',
    'item.stardewcraft.gold_axe.desc': 'A gold-upgraded axe. Very efficient at chopping.',
    'item.stardewcraft.iridium_axe.desc': 'The best axe available. Maximum chopping efficiency.',
    'item.stardewcraft.hoe.desc': 'Used to dig and till soil.',
    'item.stardewcraft.copper_hoe.desc': 'A copper-upgraded hoe. Can till multiple tiles.',
    'item.stardewcraft.steel_hoe.desc': 'A steel-upgraded hoe. Tills a wider area.',
    'item.stardewcraft.gold_hoe.desc': 'A gold-upgraded hoe. Tills a very wide area.',
    'item.stardewcraft.iridium_hoe.desc': 'The best hoe available. Maximum tilling range.',
    'item.stardewcraft.pickaxe.desc': 'Used to break stones.',
    'item.stardewcraft.copper_pickaxe.desc': 'A copper-upgraded pickaxe. Breaks stones faster.',
    'item.stardewcraft.steel_pickaxe.desc': 'A steel-upgraded pickaxe. Breaks stones even faster.',
    'item.stardewcraft.gold_pickaxe.desc': 'A gold-upgraded pickaxe. Very efficient.',
    'item.stardewcraft.iridium_pickaxe.desc': 'The best pickaxe available. Maximum mining efficiency.',
    'item.stardewcraft.watering_can.desc': 'Used to water crops.',
    'item.stardewcraft.copper_watering_can.desc': 'A copper-upgraded watering can. Waters multiple tiles.',
    'item.stardewcraft.steel_watering_can.desc': 'A steel-upgraded watering can. Waters a wider area.',
    'item.stardewcraft.gold_watering_can.desc': 'A gold-upgraded watering can. Waters a very wide area.',
    'item.stardewcraft.iridium_watering_can.desc': 'The best watering can. Maximum watering range.',
    'item.stardewcraft.scythe.desc': 'Used to harvest hay and cut weeds.',
    'item.stardewcraft.iridium_scythe.desc': 'An iridium scythe with a wider cutting radius.',
    'item.stardewcraft.milk_pail.desc': 'Used to collect milk from cows and goats.',
    'item.stardewcraft.shears.desc': 'Used to collect wool from sheep and rabbits.',
    # Eggs/Milk
    'item.stardewcraft.egg_white.desc': 'A white chicken egg.',
    'item.stardewcraft.egg_brown.desc': 'A brown chicken egg.',
    'item.stardewcraft.large_egg_white.desc': 'A large white chicken egg.',
    'item.stardewcraft.large_egg_brown.desc': 'A large brown chicken egg.',
    'item.stardewcraft.large_goat_milk.desc': 'A large bucket of creamy goat milk.',
    # Seeds
    'item.stardewcraft.ancient_fruit_seeds.desc': 'Plant in spring. Takes 28 days to grow. Regrows after harvest.',
    'item.stardewcraft.blue_jazz_seeds.desc': 'Plant in spring. Takes 7 days to grow.',
    'item.stardewcraft.coffee_bean_seeds.desc': 'Plant in spring or summer. Takes 10 days. Regrows.',
    'item.stardewcraft.fairy_rose_seeds.desc': 'Plant in fall. Takes 12 days to grow.',
    'item.stardewcraft.grape_seeds.desc': 'Plant in fall. Takes 10 days. Regrows after harvest.',
    'item.stardewcraft.hops_seeds.desc': 'Plant in summer. Takes 11 days. Regrows after harvest.',
    'item.stardewcraft.hot_pepper_seeds.desc': 'Plant in summer. Takes 5 days. Regrows after harvest.',
    'item.stardewcraft.summer_spangle_seeds.desc': 'Plant in summer. Takes 8 days to grow.',
    'item.stardewcraft.tulip_seeds.desc': 'Plant in spring. Takes 6 days to grow.',
    # Misc items
    'item.stardewcraft.cranberry.desc': 'These tart red berries are an excellent source of vitamin C.',
    'item.stardewcraft.targeted_bait.desc': 'Specialized bait crafted from a specific fish.',
    'item.stardewcraft.limestone_mineral.desc': 'A sedimentary rock composed mostly of calcium carbonate.',
    'item.stardewcraft.golden_mystery_box.desc': 'What could be inside? Bring it to the blacksmith to find out.',
    'item.stardewcraft.arcade_machine.desc': 'A classic arcade game console.',
    'item.stardewcraft.paintbrush.desc': 'Used to paint buildings and structures.',
    'item.stardewcraft.flooring_icon.desc': 'Browse and apply flooring styles.',
    'item.stardewcraft.wallpaper_icon.desc': 'Browse and apply wallpaper styles.',
    'item.stardewcraft.furniture_catalogue.desc': 'Provides an unlimited supply of all furniture.',
    'item.stardewcraft.photo_frame.desc': 'A decorative frame for displaying photos.',
    'item.stardewcraft.dining_chair_iron.desc': 'A sturdy iron dining chair.',
    'item.stardewcraft.dining_chair_wood.desc': 'A classic wooden dining chair.',
    'item.stardewcraft.stool.desc': 'A simple stool for sitting.',
    'item.stardewcraft.table_lamp.desc': 'A small lamp that provides warm light.',
    'item.stardewcraft.tv_1.desc': 'Check weather, fortune, and cooking tips.',
    'item.stardewcraft.tv_2.desc': 'A flat-screen TV. Check weather, fortune, and cooking tips.',
    'item.stardewcraft.wind_spire.desc': 'A decorative wind chime that sways in the breeze.',
    'item.stardewcraft.autofeed_trough_upgrader.desc': 'Upgrades a feed trough to auto-feed.',
    'item.stardewcraft.wood_normal.desc': 'A piece of ordinary wood. Useful for crafting.',
    'item.stardewcraft.wood_hard.desc': 'A piece of hardwood. Used for premium crafting.',
    # Weeds
    'item.stardewcraft.wild_weeds_spring_0.desc': 'Wild weeds found in spring.',
    'item.stardewcraft.wild_weeds_spring_1.desc': 'Wild weeds found in spring.',
    'item.stardewcraft.wild_weeds_spring_2.desc': 'Wild weeds found in spring.',
    'item.stardewcraft.wild_weeds_summer_0.desc': 'Wild weeds found in summer.',
    'item.stardewcraft.wild_weeds_summer_1.desc': 'Wild weeds found in summer.',
    'item.stardewcraft.wild_weeds_summer_2.desc': 'Wild weeds found in summer.',
    'item.stardewcraft.wild_weeds_fall_0.desc': 'Wild weeds found in fall.',
    'item.stardewcraft.wild_weeds_fall_1.desc': 'Wild weeds found in fall.',
    'item.stardewcraft.wild_weeds_fall_2.desc': 'Wild weeds found in fall.',
    'item.stardewcraft.wild_weeds_winter_0.desc': 'Hardy weeds that survive the winter cold.',
    # Weapons
    'item.stardewcraft.pirate_sword.desc': "A cutlass beloved by sea raiders. Its blade has tasted both blood and gold.",
}

# ── Fix all bad .desc entries ──
bad_descs = [(k, v) for k, v in sorted(en.items())
             if k.endswith('.desc') and isinstance(v, str) and v.endswith('.desc')]

fixed = 0
still_bad = []

for key, val in bad_descs:
    # 1. Manual mapping
    if key in MANUAL:
        en[key] = MANUAL[key]
        fixed += 1
        continue

    # 2. Stardew data lookup
    parts = key.split('.')
    item_id = '.'.join(parts[2:-1])
    normalized = item_id.lower().replace('_', '').replace("'", '').replace('-', '')

    if normalized in sv_desc:
        en[key] = sv_desc[normalized]
        fixed += 1
        continue

    # 3. Wine pattern
    if item_id.endswith('_wine'):
        base = item_id[:-5].replace('_', ' ').title()
        en[key] = f'Aged {base} wine. Has a complex flavor.'
        fixed += 1
        continue

    # 4. Juice pattern
    if item_id.endswith('_juice'):
        base = item_id[:-6].replace('_', ' ').title()
        en[key] = f'A fresh glass of {base} juice.'
        fixed += 1
        continue

    still_bad.append((key, item_id))

print(f'Fixed: {fixed} / {len(bad_descs)}')
print(f'Still unmatched: {len(still_bad)}')

if still_bad:
    for k, i in still_bad:
        print(f'  {k}: ZH={zh.get(k, "?")}')
    print('\nNOT saving - still has unmatched items')
else:
    # Ensure no bare % in new descriptions (for Component.translatable safety)
    for key in [k for k, _ in bad_descs]:
        v = en[key]
        # Replace any bare % that's not %% or %s or %N$s
        fixed_v = re.sub(r'%(?!%|s|\d+\$s)', '%%', v)
        en[key] = fixed_v

    sorted_en = dict(sorted(en.items()))
    with open(f'{BASE}/en_us.json', 'w') as f:
        json.dump(sorted_en, f, indent=2, ensure_ascii=False)
    print(f'\nSaved! Total keys: {len(sorted_en)}')
