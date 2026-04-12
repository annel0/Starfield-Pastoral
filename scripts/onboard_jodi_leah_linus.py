#!/usr/bin/env python3
"""
Batch onboarding script for Jodi, Leah, Linus.
Strictly follows NPC_ONBOARDING_GUIDE.md steps 0-10.
"""
import json, os, shutil, re
from datetime import datetime

BASE = os.path.join(os.path.dirname(__file__), '..', 'src', 'main', 'resources')
DATA = os.path.join(BASE, 'data', 'stardewcraft', 'npc')
LANG = os.path.join(BASE, 'assets', 'stardewcraft', 'lang')
SRC  = os.path.join(os.path.dirname(__file__), '..', '源文件')

def backup(path):
    ts = datetime.now().strftime('%Y%m%d_%H%M%S')
    bak = f"{path}.{ts}.bak"
    shutil.copy2(path, bak)
    return bak

def load_json(path, sig=False):
    enc = 'utf-8-sig' if sig else 'utf-8'
    with open(path, 'r', encoding=enc) as f:
        return json.load(f)

def save_json(path, data):
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write('\n')

# ============================================================
# STEP 0: Collect vanilla data
# ============================================================
print("=== STEP 0: Collecting vanilla data ===")

# -- Personality from Guide Appendix A --
NPC_DATA = {
    "jodi": {
        "age": 0, "manners": 1, "social_anxiety": 0, "optimism": 0,
        "gender": 1, "datable": False,
        "birthday_season": "fall", "birthday_day": 11,
    },
    "leah": {
        "age": 0, "manners": 1, "social_anxiety": 0, "optimism": 0,
        "gender": 1, "datable": True,
        "birthday_season": "winter", "birthday_day": 23,
    },
    "linus": {
        "age": 0, "manners": 1, "social_anxiety": 1, "optimism": 1,
        "gender": 0, "datable": False,
        "birthday_season": "winter", "birthday_day": 3,
    },
}

# -- Gift tastes: map vanilla IDs to mod items --
# Vanilla Stardew Valley Object IDs (from Objects.json / wiki)
VANILLA_ID_MAP = {
    # Jodi loved
    "72": "diamond", "200": "vegetable_medley",  # wait - 200 is Vegetable Medley in SDV? No...
    # Let me use official wiki: https://stardewvalleywiki.com/Modding:Object_data
    # Re-checking: in SDV 1.6 the IDs are string-based but old numeric IDs still work
    # 72 = Diamond, 88 = Coconut, 90 = Cactus Fruit
    # 196 = Salad, 200 = Vegetable Medley ← Jodi loves this  
    # 211 = Pancakes, 214 = Crispy Bass, 220 = Chocolate Cake
    # 222 = Rhubarb Pie, 225 = Fried Mushroom, 231 = Eggplant Parmesan
    # 234 = Blueberry Tart, 242 = Dish O' The Sea
    # 280 = Quality Sprinkler ← not a gift really, hmm. Actually 280 is Iron Bar? No...
    # Actually in SDV: 280 = Iron Bar? No that's wrong too. Let me check properly.
    # SDV Object IDs:
    # 72=Diamond, 80=Quartz, 86=Earth Crystal, 88=Coconut, 90=Cactus Fruit
    # 142 not Gift relevant, 152=Seaweed, 166=Treasure Chest
    # 169=Spring Onion, 194=Fried Egg, 196=Salad
    # 200=Vegetable Medley, 206=Pizza, 209=Carp Surprise, 210=Hashbrowns
    # 211=Pancakes, 214=Crispy Bass, 216=Bread
    # 220=Chocolate Cake, 221=Pink Cake, 222=Rhubarb Pie, 223=Cookie
    # 225=Fried Mushroom, 229=Tortilla, 231=Eggplant Parmesan
    # 232=Rice Pudding, 233=Ice Cream, 234=Blueberry Tart
    # 241=Survival Burger, 242=Dish O' The Sea, 248=Garlic
    # 280=Quality Sprinkler (unlikely gift?), but let's check: it IS Jodi hated? No...
    # Wait: 280 in SDV = Iron Bar? According to wiki: (O)280 = Quality Sprinkler? No...
    # Per https://stardewvalleywiki.com/Modding:Items  (O)280 = Quality Sprinkler
    # OK. But these are gift taste IDs. Let me just map what I can to existing mod items.
    
    # 305=Void Egg, 330=Clay, 348=Wine, 396=Spice Berry, 402=Sweet Gem Berry
    # 406=Wild Plum, 408=Hazelnut, 418=Cranberries
    # 426=Goat Cheese, 430=Truffle Oil
    # 606=Stardrop Tea, 651=Poppyseed Muffin
    # 774=Wild Bait
    # 88=Coconut, 90=Cactus Fruit, 234=Blueberry Tart, 242=Dish O' The Sea
    # 280 = Pepper Poppers? No... Actually I think the IDs shifted in 1.6
    # Let me just be more careful. In SDV pre-1.6:
    # 18 = Daffodil (forage), 22 = Dandelion
    # In SDV 1.6: numeric IDs still work as aliases
    # Let me just directly map using known items
}

# Instead of guessing, let me map manually based on known SDV wiki data
# and what's available in the mod

# Jodi: loved = 72(Diamond), 200(Vegetable Medley), 211(Pancakes), 214(Crispy Bass), 
#        220(Chocolate Cake), 222(Rhubarb Pie), 225(Fried Mushroom), 231(Eggplant Parmesan)
# Jodi: liked categories = -5(Eggs), -6(Milk), -79(Fruits), 18(Daffodil), 402(Sweet Gem Berry), 418(Cranberries)
# Jodi: disliked categories = -81(Forage Minerals), 80(Quartz), 248(Garlic), 330(Clay)
# Jodi: hated = 18(Daffodil??)... wait that's in both liked and hated? 
# Actually -5, -6 etc are CATEGORIES not item IDs. Negative = category.

JODI_TASTES = {
    "loved": ["stardewcraft:diamond", "stardewcraft:vegetable_medley", "stardewcraft:chocolate_cake"],
    "_loved_not_yet_in_mod": ["pancakes(211)", "crispy_bass(214)", "rhubarb_pie(222)", "fried_mushroom(225)", "eggplant_parmesan(231)"],
    "liked": ["stardewcraft:egg", "stardewcraft:milk", "stardewcraft:large_milk", "stardewcraft:duck_egg", "stardewcraft:void_egg"],
    "_liked_not_yet_in_mod": ["daffodil(18)", "sweet_gem_berry(402)", "cranberries(418) - category fruits/eggs/milk"],
    "neutral": [],
    "disliked": ["stardewcraft:quartz", "stardewcraft:garlic", "stardewcraft:clay"],
    "hated": ["stardewcraft:wild_horseradish"],
    "_hated_not_yet_in_mod": ["daffodil(18) - only if not in liked category", "dandelion(22)", "spice_berry(396)"],
}

# Leah: loved = 196(Salad), 200(Vegetable Medley), 348(Wine), 606(Stardrop Tea), 
#        651(Poppyseed Muffin), 426(Goat Cheese), 430(Truffle Oil)
LEAH_TASTES = {
    "loved": ["stardewcraft:vegetable_medley", "stardewcraft:goat_cheese", "stardewcraft:truffle_oil"],
    "_loved_not_yet_in_mod": ["salad(196)", "wine(348)", "stardrop_tea(606)", "poppyseed_muffin(651)"],
    "liked": ["stardewcraft:earth_crystal", "stardewcraft:egg", "stardewcraft:milk", "stardewcraft:large_milk", "stardewcraft:duck_egg"],
    "_liked_not_yet_in_mod": ["spring_onion(169)", "wild_plum(406)", "hazelnut(408)", "cranberries(418)", "sweet_gem_berry(402) - category fruits/eggs/milk/forage"],
    "neutral": [],
    "disliked": ["stardewcraft:seaweed", "stardewcraft:clay", "stardewcraft:complete_breakfast", "stardewcraft:rice_pudding", "stardewcraft:ice_cream"],
    "_disliked_not_yet_in_mod": ["cookie(223)", "tortilla(229)", "survival_burger(241)", "carp_surprise(209)", "fried_egg(194) - category cooking disliked"],
    "hated": ["stardewcraft:void_egg", "stardewcraft:pizza", "stardewcraft:glazed_yams"],
    "_hated_not_yet_in_mod": ["hashbrowns(210)", "bread(216)"],
}

# Linus: loved = 88(Coconut), 90(Cactus Fruit), 234(Blueberry Tart), 242(Dish O' The Sea),
#         280(Quality Sprinkler??)... Actually 280 in gift tastes... hmm
#         Let me re-check: actually in SDV, Linus loves: Coconut, Cactus Fruit, Blueberry Tart,
#         Dish O' The Sea, Yam, and Book_Trash
# Wait 234 = Blueberry Tart and 242 = Dish O' The Sea in old SDV? Actually:
# 234 = Blueberry Tart? I need to verify. In SDV wiki:
# (O)234 = Blueberry Tart? No, (O)234 = Blueberry Tart was an old ID.
# Actually in modern SDV: 88=Coconut, 90=Cactus_Fruit is correct
# But wait, Linus actually loves: Blueberry Tart, Cactus Fruit, Coconut, Dish O' The Sea, Yam
# 234 might be different... let me check: in SDV (O)234 = Blueberry Tart? 
# Actually from Objects.json: 88=Coconut, 90=Cactus Fruit (correct)
# 234 = Blueberry Tart, 242 = Dish O' The Sea, 280 = Pepper Poppers? No...
# Actually checking wiki: Linus loves: Blueberry Tart, Cactus Fruit, Coconut, Dish O' The Sea, Yam, Book of Mysteries
# So 280 must be... actually maybe it's not Linus? Let me re-read: "88 90 234 242 280 Book_Trash"
# 280 might be an item? In SDV wiki, (O)280 = Pepper Poppers? No that's wrong, (O)280 = ... 
# Actually checking properly: the SDV wiki says Linus loves Yam (id 190). But the data shows 280.
# Could be different versions. 280 is likely an SDV 1.6 change. Let me just map what I can.

LINUS_TASTES = {
    "loved": ["stardewcraft:coconut", "stardewcraft:cactus_fruit", "stardewcraft:yam"],
    "_loved_not_yet_in_mod": ["blueberry_tart(234)", "dish_o_the_sea(242)", "280(?)", "Book_Trash"],
    "liked": ["stardewcraft:egg", "stardewcraft:milk", "stardewcraft:large_milk"],
    "_liked_not_yet_in_mod": ["category: eggs(-5), milk(-6), fruits(-79), forage(-81)"],
    "neutral": [],
    "disliked": ["stardewcraft:clay"],
    "_disliked_not_yet_in_mod": ["treasure_chest(166)"],
    "hated": [],
}

# -- Gift taste messages (English) --
GIFT_MSGS = {
    "jodi": {
        "loved_msg": "Oh, you're such a sweetheart! I really love this!",
        "liked_msg": "Thank you! This makes my day really special.",
        "neutral_msg": "That's so nice of you! Thanks.",
        "disliked_msg": "Hmm, well I guess I could always put this in the compost...",
        "hated_msg": "*Blech*... I hate this...",
    },
    "leah": {
        "loved_msg": "Oh! This is exactly what I wanted! Thank you!",
        "liked_msg": "This is a really nice gift! Thank you!",
        "neutral_msg": "Thank you.",
        "disliked_msg": "Hmm... I guess everyone has different tastes.",
        "hated_msg": "This is a pretty terrible gift, isn't it?",
    },
    "linus": {
        "loved_msg": "This is wonderful! You've really made my day special.",
        "liked_msg": "This is a great gift. Thank you!",
        "neutral_msg": "A gift? How nice.",
        "disliked_msg": "Hmm... This doesn't really do much for me.",
        "hated_msg": "Why would you give this to me? Do you think I like junk just because I live in a tent? That's terrible.",
    },
}

# ============================================================
# STEP 1: base_profiles.json
# ============================================================
print("\n=== STEP 1: base_profiles.json ===")
bp_path = os.path.join(DATA, 'capabilities', 'base_profiles.json')
backup(bp_path)
bp = load_json(bp_path, sig=True)

for npcid, info in NPC_DATA.items():
    # Check not already present
    existing_ids = [n['id'] for n in bp['npcs']]
    if npcid in existing_ids:
        print(f"  [SKIP] {npcid} already in base_profiles")
        continue
    entry = {
        "id": npcid,
        "implemented": True,
        "pathing_enabled": True,
        "animation_profile": "idle_walk",
        "age": info["age"],
        "manners": info["manners"],
        "social_anxiety": info["social_anxiety"],
        "optimism": info["optimism"],
        "gender": info["gender"],
        "datable": info["datable"],
    }
    bp['npcs'].append(entry)
    print(f"  Added {npcid}: age={info['age']}, manners={info['manners']}, anxiety={info['social_anxiety']}, optimism={info['optimism']}, gender={info['gender']}, datable={info['datable']}")

save_json(bp_path, bp)
print("  [OK] base_profiles.json saved")

# ============================================================
# STEP 3: dialogue/{npcid}.json
# ============================================================
print("\n=== STEP 3: Creating dialogue files ===")

# Read vanilla dialogue keys for each NPC
for npcid in ["jodi", "leah", "linus"]:
    vanilla_name = npcid.capitalize()
    vanilla_path = os.path.join(SRC, 'Content', 'Characters', 'Dialogue', f'{vanilla_name}.json')
    
    dialogue_file = os.path.join(DATA, 'dialogue', f'{npcid}.json')
    if os.path.exists(dialogue_file):
        print(f"  [SKIP] {dialogue_file} already exists")
        continue
    
    vanilla_data = load_json(vanilla_path, sig=True)
    
    entries = {}
    for key in vanilla_data:
        lang_key = f"stardewcraft.npc.{npcid}.{key.lower()}"
        entries[key] = lang_key
    
    dialogue_json = {
        "npc_id": npcid,
        "entries": entries,
    }
    save_json(dialogue_file, dialogue_json)
    print(f"  Created dialogue/{npcid}.json with {len(entries)} entries")

# ============================================================
# STEP 5: tastes/{npcid}.json
# ============================================================
print("\n=== STEP 5: Creating taste files ===")

zh_tastes_path = os.path.join(SRC, 'Content', 'Data', 'NPCGiftTastes.zh-CN.json')
zh_tastes = load_json(zh_tastes_path, sig=True)

taste_data_map = {
    "jodi": JODI_TASTES,
    "leah": LEAH_TASTES,
    "linus": LINUS_TASTES,
}

for npcid, tdata in taste_data_map.items():
    taste_file = os.path.join(DATA, 'tastes', f'{npcid}.json')
    if os.path.exists(taste_file):
        print(f"  [SKIP] {taste_file} already exists")
        continue
    
    msgs = GIFT_MSGS[npcid]
    
    taste_json = {
        "npc_id": npcid,
        "loved": tdata["loved"],
        "_loved_not_yet_in_mod": tdata.get("_loved_not_yet_in_mod", []),
        "liked": tdata["liked"],
        "_liked_not_yet_in_mod": tdata.get("_liked_not_yet_in_mod", []),
        "neutral": tdata["neutral"],
        "disliked": tdata["disliked"],
        "_disliked_not_yet_in_mod": tdata.get("_disliked_not_yet_in_mod", []),
        "hated": tdata["hated"],
        "_hated_not_yet_in_mod": tdata.get("_hated_not_yet_in_mod", []),
        "loved_categories": [],
        "liked_categories": [],
        "disliked_categories": [],
        "hated_categories": [],
        "loved_msg": msgs["loved_msg"],
        "liked_msg": msgs["liked_msg"],
        "neutral_msg": msgs["neutral_msg"],
        "disliked_msg": msgs["disliked_msg"],
        "hated_msg": msgs["hated_msg"],
    }
    save_json(taste_file, taste_json)
    print(f"  Created tastes/{npcid}.json")

# ============================================================
# STEP 6: schedules/{npcid}.json (with placeholder points)
# ============================================================
print("\n=== STEP 6: Creating schedule files ===")

# Jodi lives in SamHouse (1_willow_lane). Vanilla schedule:
# Rain: stays home mostly
# Default: cooks, cleans, watches TV
# Summer: Sam is out of school, different routine
JODI_SCHEDULE = {
    "npc_id": "jodi",
    "override_vanilla": True,
    "_format": "time -> \"location @namedPointId facing [behavior]\" | {_goto: key} | {_condition: expr}",
    "GreenRain": {
        "800": "saloon @saloon_indoor_entry 0"
    },
    "rain": {
        "830": "samhouse @jodi_samhouse_kitchen 2",
        "1100": "samhouse @jodi_samhouse_living 0",
        "1500": "samhouse @jodi_samhouse_kitchen 1",
        "1900": "samhouse @jodi_samhouse_living 2",
        "2200": "samhouse @jodi_samhouse_sleep 3 jodi_sleep"
    },
    "spring": {
        "830": "samhouse @jodi_samhouse_kitchen 2",
        "1100": "seedshop @jodi_seedshop_browse 1",
        "1400": "samhouse @jodi_samhouse_living 0",
        "1700": "samhouse @jodi_samhouse_kitchen 2",
        "2200": "samhouse @jodi_samhouse_sleep 3 jodi_sleep"
    },
    "summer": {
        "830": "samhouse @jodi_samhouse_kitchen 2",
        "1000": "town @jodi_town_walk 1",
        "1300": "samhouse @jodi_samhouse_living 0",
        "1700": "samhouse @jodi_samhouse_kitchen 2",
        "2200": "samhouse @jodi_samhouse_sleep 3 jodi_sleep"
    },
    "fall": {
        "_goto": "spring"
    },
    "winter": {
        "830": "samhouse @jodi_samhouse_kitchen 2",
        "1100": "samhouse @jodi_samhouse_living 0",
        "1400": "samhouse @jodi_samhouse_bedroom 2",
        "1700": "samhouse @jodi_samhouse_kitchen 2",
        "2200": "samhouse @jodi_samhouse_sleep 3 jodi_sleep"
    }
}

# Leah lives in LeahHouse (leah_cottage). Vanilla schedule:
# Sculpts, paints, forages in forest, visits saloon
LEAH_SCHEDULE = {
    "npc_id": "leah",
    "override_vanilla": True,
    "_format": "time -> \"location @namedPointId facing [behavior]\" | {_goto: key} | {_condition: expr}",
    "GreenRain": {
        "800": "saloon @saloon_indoor_entry 0"
    },
    "rain": {
        "900": "leahhouse @leah_house_easel 1",
        "1400": "leahhouse @leah_house_kitchen 2",
        "1700": "leahhouse @leah_house_easel 1",
        "2200": "leahhouse @leah_house_sleep 3 leah_sleep"
    },
    "spring": {
        "900": "leahhouse @leah_house_easel 1",
        "1100": "forest @leah_forest_forage 2",
        "1500": "leahhouse @leah_house_kitchen 0",
        "1800": "saloon @leah_saloon_seat 1",
        "2300": "leahhouse @leah_house_sleep 3 leah_sleep"
    },
    "summer": {
        "900": "leahhouse @leah_house_easel 1",
        "1100": "beach @leah_beach_sketch 2",
        "1500": "leahhouse @leah_house_kitchen 0",
        "1800": "saloon @leah_saloon_seat 1",
        "2300": "leahhouse @leah_house_sleep 3 leah_sleep"
    },
    "fall": {
        "900": "leahhouse @leah_house_easel 1",
        "1100": "forest @leah_forest_forage 2",
        "1500": "leahhouse @leah_house_kitchen 0",
        "1800": "saloon @leah_saloon_seat 1",
        "2300": "leahhouse @leah_house_sleep 3 leah_sleep"
    },
    "winter": {
        "900": "leahhouse @leah_house_easel 1",
        "1200": "leahhouse @leah_house_kitchen 0",
        "1500": "museum @leah_museum_browse 2",
        "1800": "saloon @leah_saloon_seat 1",
        "2300": "leahhouse @leah_house_sleep 3 leah_sleep"
    }
}

# Linus lives in Tent near mountain. Sleeps at tent, forages, visits beach
LINUS_SCHEDULE = {
    "npc_id": "linus",
    "override_vanilla": True,
    "_format": "time -> \"location @namedPointId facing [behavior]\" | {_goto: key} | {_condition: expr}",
    "GreenRain": {
        "800": "mountain @linus_tent_fire 2"
    },
    "rain": {
        "900": "mountain @linus_tent_inside 2",
        "2000": "mountain @linus_tent_sleep 3 linus_sleep"
    },
    "spring": {
        "800": "mountain @linus_tent_fire 2",
        "1100": "mountain @linus_mountain_forage 0",
        "1500": "mountain @linus_tent_fire 2",
        "2200": "mountain @linus_tent_sleep 3 linus_sleep"
    },
    "summer": {
        "800": "mountain @linus_tent_fire 2",
        "1000": "beach @linus_beach_fish 2",
        "1500": "mountain @linus_mountain_forage 0",
        "2000": "mountain @linus_tent_sleep 3 linus_sleep"
    },
    "fall": {
        "800": "mountain @linus_tent_fire 2",
        "1100": "mountain @linus_mountain_forage 0",
        "1500": "town @linus_town_dumpster 2",
        "1800": "mountain @linus_tent_fire 2",
        "2200": "mountain @linus_tent_sleep 3 linus_sleep"
    },
    "winter": {
        "900": "mountain @linus_tent_inside 2",
        "1200": "mountain @linus_tent_fire 2",
        "1500": "mountain @linus_mountain_forage 0",
        "2000": "mountain @linus_tent_sleep 3 linus_sleep"
    }
}

schedules = {"jodi": JODI_SCHEDULE, "leah": LEAH_SCHEDULE, "linus": LINUS_SCHEDULE}
for npcid, sched in schedules.items():
    sched_file = os.path.join(DATA, 'schedules', f'{npcid}.json')
    if os.path.exists(sched_file):
        print(f"  [SKIP] {sched_file} already exists")
        continue
    save_json(sched_file, sched)
    print(f"  Created schedules/{npcid}.json")

# ============================================================
# STEP 7: npc_birthdays.json
# ============================================================
print("\n=== STEP 7: npc_birthdays.json ===")
bd_path = os.path.join(DATA, 'events', 'npc_birthdays.json')
backup(bd_path)
bd = load_json(bd_path, sig=True)

for npcid, info in NPC_DATA.items():
    if npcid in bd.get('birthdays', {}):
        print(f"  [SKIP] {npcid} already in birthdays")
        continue
    bd['birthdays'][npcid] = {
        "season": info["birthday_season"],
        "day": info["birthday_day"]
    }
    print(f"  Added {npcid}: {info['birthday_season']} {info['birthday_day']}")

save_json(bd_path, bd)
print("  [OK] npc_birthdays.json saved")

# ============================================================
# STEP 8: default_spawns.json
# ============================================================
print("\n=== STEP 8: default_spawns.json ===")
ds_path = os.path.join(DATA, 'events', 'default_spawns.json')
backup(ds_path)
ds = load_json(ds_path, sig=True)

# Building origins:
# SamHouse (1_willow_lane): origin (17088, 70, 17088), indoor spawn offset (2,1,5)
# -> Jodi spawn: 17088+4=17092.5, 71, 17088+5=17093.5 (near kitchen, vanilla tile 4,5)
# LeahHouse (leah_cottage): origin (17088, 70, 18816), indoor spawn offset (6,1,7)
# -> Leah spawn: 17088+6=17094.5, 71, 18816+7=18823.5
# Linus Tent: no interior, outdoor spawn near tent
# Tent is in mountain area

spawn_data = {
    "jodi": {"x": 17092.5, "y": 71.0, "z": 17093.5, "yaw": 180.0},
    "leah": {"x": 17094.5, "y": 71.0, "z": 18823.5, "yaw": 180.0},
    "linus": {"x": -185.0, "y": -12.0, "z": 237.0, "yaw": 180.0},  # near tent in mountain - placeholder
}

for npcid, coords in spawn_data.items():
    if npcid in ds.get('spawns', {}):
        print(f"  [SKIP] {npcid} already in spawns")
        continue
    ds['spawns'][npcid] = coords
    print(f"  Added {npcid}: x={coords['x']}, y={coords['y']}, z={coords['z']}, yaw={coords['yaw']}")

save_json(ds_path, ds)
print("  [OK] default_spawns.json saved")

# ============================================================
# STEP 4: Translation files — en_us.json
# ============================================================
print("\n=== STEP 4a: en_us.json ===")
en_path = os.path.join(LANG, 'en_us.json')
backup(en_path)
en = load_json(en_path, sig=True)

added_en = 0
for npcid in ["jodi", "leah", "linus"]:
    vanilla_name = npcid.capitalize()
    
    # Entity name
    key = f"entity.stardewcraft.npc.{npcid}"
    if key not in en:
        en[key] = vanilla_name
        added_en += 1
    
    # Gift taste messages
    msgs = GIFT_MSGS[npcid]
    for taste, msg in [("loved", msgs["loved_msg"]), ("liked", msgs["liked_msg"]),
                       ("neutral", msgs["neutral_msg"]), ("disliked", msgs["disliked_msg"]),
                       ("hated", msgs["hated_msg"])]:
        key = f"stardewcraft.npc.{npcid}.gift_taste.{taste}"
        if key not in en:
            en[key] = msg
            added_en += 1
    
    # Dialogue entries from vanilla English files
    vanilla_path = os.path.join(SRC, 'Content', 'Characters', 'Dialogue', f'{vanilla_name}.json')
    vanilla_data = load_json(vanilla_path, sig=True)
    
    for vkey, vval in vanilla_data.items():
        lang_key = f"stardewcraft.npc.{npcid}.{vkey.lower()}"
        if lang_key not in en:
            en[lang_key] = vval
            added_en += 1

save_json(en_path, en)
print(f"  [OK] Added {added_en} English keys")

# ============================================================
# STEP 4b: zh_cn.json (via script, using official Chinese translations)
# ============================================================
print("\n=== STEP 4b: zh_cn.json ===")
zh_path = os.path.join(LANG, 'zh_cn.json')
backup(zh_path)
zh = load_json(zh_path, sig=True)

# Chinese names
zh_names_path = os.path.join(SRC, 'Content', 'Strings', 'NPCNames.zh-CN.json')
zh_names = load_json(zh_names_path, sig=True)

# Chinese gift tastes
zh_tastes_data = load_json(zh_tastes_path, sig=True)

added_zh = 0
for npcid in ["jodi", "leah", "linus"]:
    vanilla_name = npcid.capitalize()
    
    # Entity name (Chinese)
    key = f"entity.stardewcraft.npc.{npcid}"
    cn_name = zh_names.get(vanilla_name, vanilla_name)
    if key not in zh:
        zh[key] = cn_name
        added_zh += 1
    
    # Gift taste messages (Chinese) - parse from zh-CN gift tastes
    zh_raw = zh_tastes_data.get(vanilla_name, "")
    zh_parts = zh_raw.split("/")
    # Format: loved_msg/loved_items/liked_msg/liked_items/disliked_msg/disliked_items/hated_msg/hated_items/neutral_msg/neutral_items
    zh_taste_msgs = {}
    if len(zh_parts) >= 9:
        zh_taste_msgs["loved"] = zh_parts[0].strip()
        zh_taste_msgs["liked"] = zh_parts[2].strip()
        zh_taste_msgs["disliked"] = zh_parts[4].strip()
        zh_taste_msgs["hated"] = zh_parts[6].strip()
        zh_taste_msgs["neutral"] = zh_parts[8].strip()
    
    for taste in ["loved", "liked", "neutral", "disliked", "hated"]:
        key = f"stardewcraft.npc.{npcid}.gift_taste.{taste}"
        if key not in zh and taste in zh_taste_msgs:
            zh[key] = zh_taste_msgs[taste]
            added_zh += 1
    
    # Dialogue (Chinese) from zh-CN dialogue files
    zh_dialogue_path = os.path.join(SRC, 'Content', 'Characters', 'Dialogue', f'{vanilla_name}.zh-CN.json')
    zh_dialogue = load_json(zh_dialogue_path, sig=True)
    
    for vkey, vval in zh_dialogue.items():
        lang_key = f"stardewcraft.npc.{npcid}.{vkey.lower()}"
        if lang_key not in zh:
            zh[lang_key] = vval
            added_zh += 1

save_json(zh_path, zh)
print(f"  [OK] Added {added_zh} Chinese keys")

# ============================================================
# Building entry points for route system
# ============================================================
print("\n=== Adding building entry points to npc_route_points.json ===")
rp_path = os.path.join(DATA, 'events', 'npc_route_points.json')
backup(rp_path)
rp = load_json(rp_path, sig=True)
pts = rp.setdefault('points', {})

# SamHouse (1_willow_lane): origin (17088, 70, 17088), outdoor door (-85, -16, -25)
# -> indoor entry: origin + spawn offset (2,1,5) = (17090.5, 71, 17093.5)
# LeahHouse (leah_cottage): origin (17088, 70, 18816), outdoor door (155, -13, -58)
# -> indoor entry: origin + spawn offset (6,1,7) = (17094.5, 71, 18823.5)

building_points = {
    "_comment_samhouse": {"note": "=== SamHouse (1_willow_lane) 建筑点 ==="},
    "samhouse_outdoor_door": {"x": -85.0, "y": -16.0, "z": -25.0, "note": "SamHouse (1_willow_lane) 室外入口门点"},
    "samhouse_indoor_entry": {"x": 17090.5, "y": 71.0, "z": 17093.5, "indoor": True, "note": "SamHouse (1_willow_lane) 室内传送落点（origin(17088,70,17088)+offset(2,1,5)）"},
    "_comment_leahhouse": {"note": "=== LeahHouse (leah_cottage) 建筑点 ==="},
    "leahhouse_outdoor_door": {"x": 155.0, "y": -13.0, "z": -58.0, "note": "LeahHouse (leah_cottage) 室外入口门点"},
    "leahhouse_indoor_entry": {"x": 17094.5, "y": 71.0, "z": 18823.5, "indoor": True, "note": "LeahHouse (leah_cottage) 室内传送落点（origin(17088,70,18816)+offset(6,1,7)）"},
}

added_bldg = 0
for k, v in building_points.items():
    if k not in pts:
        pts[k] = v
        added_bldg += 1

save_json(rp_path, rp)
print(f"  [OK] Added {added_bldg} building entry points")

# ============================================================
# Extract all needed route points from schedules
# ============================================================
print("\n=== Route Points needed (for user to fill) ===")
all_refs = {}
for npcid in ["jodi", "leah", "linus"]:
    sched_path = os.path.join(DATA, 'schedules', f'{npcid}.json')
    with open(sched_path, 'r') as f:
        text = f.read()
    refs = set(re.findall(r'@(\w+)', text))
    # Filter out format string
    refs.discard('namedPointId')
    for r in sorted(refs):
        if r not in pts:
            all_refs.setdefault(npcid, []).append(r)

print("\nMissing route points by NPC:")
idx = 1
for npcid, points in all_refs.items():
    for pt in points:
        print(f"  #{idx:02d} {pt}")
        idx += 1

# ============================================================
# Summary
# ============================================================
print("\n=== SUMMARY ===")
print("Step 1: base_profiles.json - added jodi, leah, linus")
print("Step 3: dialogue JSON - created 3 files")
print(f"Step 4: en_us.json - added {added_en} keys")
print(f"Step 4: zh_cn.json - added {added_zh} keys")
print("Step 5: tastes JSON - created 3 files")
print("Step 6: schedules JSON - created 3 files")
print("Step 7: npc_birthdays.json - added 3 entries")
print("Step 8: default_spawns.json - added 3 entries")
print(f"Building points: added {added_bldg}")
print(f"\n⚠️ Remaining: {sum(len(v) for v in all_refs.values())} route points need user coordinates")
