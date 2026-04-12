#!/usr/bin/env python3
"""
Batch patch for Haley / Harvey / Jas:
1. Fix base_profiles.json personality attributes
2. Remove darkroom schedule entries from haley schedule
3. Add 31 route points to npc_route_points.json
4. Add route profiles for all 3 NPCs to npc_route_profiles.json
"""
import json, os, shutil, re
from datetime import datetime

BASE = os.path.join(os.path.dirname(__file__), '..', 'src', 'main', 'resources')
DATA = os.path.join(BASE, 'data', 'stardewcraft', 'npc')

def backup(path):
    ts = datetime.now().strftime('%Y%m%d_%H%M%S')
    bak = f"{path}.{ts}.bak"
    shutil.copy2(path, bak)
    return bak

def load_json(path):
    with open(path, 'r', encoding='utf-8') as f:
        return json.load(f)

def save_json(path, data):
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write('\n')

# ========== 1. Fix base_profiles.json ==========
print("=== 1. Fixing base_profiles.json ===")
bp_path = os.path.join(DATA, 'capabilities', 'base_profiles.json')
backup(bp_path)
bp = load_json(bp_path)
fixes = {
    'haley':  {'optimism': 1},       # was 0, should be 1 (negative)
    'harvey': {'optimism': 1},       # was 0, should be 1 (negative)
    'jas':    {'manners': 1},        # was 0, should be 1 (polite)
}
for npc_entry in bp.get('npcs', []):
    nid = npc_entry.get('id', '')
    if nid in fixes:
        for k, v in fixes[nid].items():
            old = npc_entry.get(k)
            npc_entry[k] = v
            print(f"  {nid}.{k}: {old} -> {v}")
save_json(bp_path, bp)
print("  [OK] base_profiles.json saved")

# ========== 2. Remove darkroom from haley schedule ==========
print("\n=== 2. Removing darkroom from haley schedule ===")
hs_path = os.path.join(DATA, 'schedules', 'haley.json')
backup(hs_path)
hs = load_json(hs_path)
removed = 0
for season_key, schedule in hs.items():
    if not isinstance(schedule, dict):
        continue
    to_remove = []
    for time_key, value in schedule.items():
        if isinstance(value, str) and 'darkroom' in value:
            to_remove.append(time_key)
    for tk in to_remove:
        del schedule[tk]
        removed += 1
        print(f"  Removed {season_key}.{tk}")
save_json(hs_path, hs)
print(f"  [OK] Removed {removed} darkroom entries")

# ========== 3. Add route points ==========
print("\n=== 3. Adding route points ===")
rp_path = os.path.join(DATA, 'events', 'npc_route_points.json')
backup(rp_path)
rp = load_json(rp_path)
pts = rp.setdefault('points', {})

# Also add building entry points for animalshop
new_points = {
    # -- AnimalShop building points --
    "_comment_animalshop": {"note": "=== AnimalShop (Marnie Ranch) 建筑点 ==="},
    "animalshop_outdoor_door": {"x": 178.0, "y": -14.0, "z": -4.0, "note": "AnimalShop (marnie_ranch) 室外入口门点"},
    "animalshop_indoor_entry": {"x": 17090.5, "y": 71.0, "z": 18255.5, "indoor": True, "note": "AnimalShop (marnie_ranch) 室内传送落点（origin(17088,70,18240)+offset(2,1,15)）"},

    # -- Haley indoor (HaleyHouse, origin 17088,70,17664) --
    "_comment_haley": {"note": "=== Haley 专属点位 ==="},
    "haley_haleyhouse_mirror":  {"x": 17106.0, "y": 71.0, "z": 17671.0, "indoor": True, "note": "Haley 卧室梳妆台前（朝西）"},
    "haley_haleyhouse_closet":  {"x": 17109.0, "y": 71.0, "z": 17671.0, "indoor": True, "note": "Haley 卧室衣柜前（朝东）"},
    "haley_haleyhouse_living":  {"x": 17095.0, "y": 71.0, "z": 17669.0, "indoor": True, "note": "Haley 客厅（朝西）"},
    "haley_haleyhouse_kitchen": {"x": 17087.0, "y": 71.0, "z": 17688.0, "indoor": True, "note": "Haley 厨房（朝北）"},
    "haley_haleyhouse_sleep":   {"x": 17110.0, "y": 71.0, "z": 17667.0, "indoor": True, "note": "Haley 床铺（朝西）"},

    # -- Haley outdoor --
    "haley_town_square":   {"x": -145.0, "y": -17.0, "z": 36.0,   "note": "Haley 镇广场（朝北）"},
    "haley_beach_sunbathe":{"x": -282.0, "y": -14.0, "z": -175.0, "note": "Haley 沙滩晒太阳（朝北）"},
    "haley_forest_photo":  {"x": 204.0,  "y": -15.0, "z": -139.0, "note": "Haley 森林拍照（朝西）"},

    # -- Harvey indoor (Clinic, origin 15360,70,15360) --
    "_comment_harvey": {"note": "=== Harvey 专属点位 ==="},
    "harvey_clinic_desk":  {"x": 15366.0, "y": 71.0,  "z": 15367.0, "indoor": True, "note": "Harvey 诊所办公桌（朝西）"},
    "harvey_clinic_exam":  {"x": 15377.0, "y": 71.0,  "z": 15361.0, "indoor": True, "note": "Harvey 诊所检查室（朝西）"},
    "harvey_clinic_lobby": {"x": 15366.0, "y": 71.0,  "z": 15375.0, "indoor": True, "note": "Harvey 诊所大厅（朝西）"},
    "harvey_room_main":    {"x": 15382.0, "y": 75.0,  "z": 15370.0, "indoor": True, "note": "Harvey 房间主区域（Clinic二楼）"},
    "harvey_room_sleep":   {"x": 15390.0, "y": 75.0,  "z": 15375.0, "indoor": True, "note": "Harvey 床铺（朝西，Clinic二楼）"},
    "harvey_room_radio":   {"x": 15390.0, "y": 75.0,  "z": 15371.0, "indoor": True, "note": "Harvey 听广播位置（朝西，Clinic二楼）"},
    "harvey_room_read":    {"x": 15389.0, "y": 75.0,  "z": 15369.0, "indoor": True, "note": "Harvey 阅读位置（朝东，Clinic二楼）"},

    # -- Harvey other buildings --
    "harvey_seedshop_browse": {"x": 12051.0, "y": 71.0, "z": 12045.0, "indoor": True, "note": "Harvey 种子店内逛（朝西）"},
    "harvey_museum_browse":   {"x": 13081.0, "y": 71.0, "z": 13076.0, "indoor": True, "note": "Harvey 博物馆内逛（朝东）"},
    "harvey_saloon_seat":     {"x": 14210.0, "y": 71.0, "z": 14251.0, "indoor": True, "note": "Harvey 酒吧座位（朝东）"},

    # -- Harvey outdoor --
    "harvey_town_walk":  {"x": -135.0, "y": -17.0, "z": 32.0,  "note": "Harvey 镇上散步（朝北）"},
    "harvey_town_bench": {"x": -165.0, "y": -18.0, "z": -3.0,  "note": "Harvey 镇上长椅（朝北）"},

    # -- Jas indoor (AnimalShop/Marnie Ranch, origin 17088,70,18240) --
    "_comment_jas": {"note": "=== Jas 专属点位 ==="},
    "jas_animalshop_kitchen": {"x": 17093.0, "y": 71.0, "z": 18272.0, "indoor": True, "note": "Jas 牧场厨房（朝北）"},
    "jas_animalshop_living":  {"x": 17092.0, "y": 71.0, "z": 18242.0, "indoor": True, "note": "Jas 牧场客厅（朝东）"},
    "jas_animalshop_read":    {"x": 17094.0, "y": 71.0, "z": 18274.0, "indoor": True, "note": "Jas 牧场阅读角（朝北）"},
    "jas_animalshop_bedroom": {"x": 17102.0, "y": 71.0, "z": 18244.0, "indoor": True, "note": "Jas 卧室（朝西）"},
    "jas_animalshop_sleep":   {"x": 17105.0, "y": 71.0, "z": 18243.0, "indoor": True, "note": "Jas 床铺（朝西）"},

    # -- Jas other buildings --
    "jas_museum_read": {"x": 13072.0, "y": 71.0, "z": 13075.0, "indoor": True, "note": "Jas 博物馆阅读（朝东）"},

    # -- Jas outdoor --
    "jas_town_playground":  {"x": -98.0,  "y": -10.0, "z": 148.0,  "note": "Jas 镇上游乐场（朝北）"},
    "jas_town_walk":        {"x": -193.0, "y": -10.0, "z": 128.0,  "note": "Jas 镇上散步（朝南）"},
    "jas_town_jumprope":    {"x": -140.0, "y": -17.0, "z": 26.0,   "note": "Jas 镇上跳绳（朝东）"},
    "jas_forest_jumprope":  {"x": 263.0,  "y": -13.0, "z": -2.0,   "note": "Jas 森林跳绳（朝北）"},
    "jas_beach_play":       {"x": -230.0, "y": -15.0, "z": -156.0, "note": "Jas 沙滩玩耍（朝北）"},
}

added = 0
for k, v in new_points.items():
    if k not in pts:
        pts[k] = v
        added += 1
    else:
        print(f"  [SKIP] {k} already exists")
save_json(rp_path, rp)
print(f"  [OK] Added {added} new points (total {len(pts)})")

# ========== 4. Add route profiles ==========
print("\n=== 4. Adding route profiles ===")
rpf_path = os.path.join(DATA, 'events', 'npc_route_profiles.json')
backup(rpf_path)
rpf = load_json(rpf_path)
profiles = rpf.setdefault('profiles', {})

def walk(pt):
    return {"mode": "walk", "point": pt}
def warp(pt):
    return {"mode": "warp", "point": pt}

# Haley route profiles
profiles["haley"] = {
    "haleyhouse": [
        walk("haleyhouse_outdoor_door"),
        warp("haleyhouse_indoor_entry"),
        walk("haley_haleyhouse_living")
    ],
    "town": [
        walk("haley_town_square")
    ],
    "beach": [
        walk("haley_beach_sunbathe")
    ],
    "forest": [
        walk("haley_forest_photo")
    ],
    "saloon": [
        walk("saloon_outdoor_door"),
        warp("saloon_indoor_entry"),
        walk("saloon_indoor_entry")  # stay at entry
    ]
}

# Harvey route profiles
profiles["harvey"] = {
    "hospital": [
        walk("clinic_outdoor_door"),
        warp("clinic_indoor_entry"),
        walk("harvey_clinic_desk")
    ],
    "clinic": [
        walk("clinic_outdoor_door"),
        warp("clinic_indoor_entry"),
        walk("harvey_clinic_desk")
    ],
    "harveyroom": [
        walk("clinic_outdoor_door"),
        warp("clinic_indoor_entry"),
        walk("harvey_room_main")
    ],
    "seedshop": [
        walk("seedshop_outdoor_door"),
        warp("seedshop_indoor_entry"),
        walk("harvey_seedshop_browse")
    ],
    "museum": [
        walk("museum_outdoor_door"),
        warp("museum_indoor_entry"),
        walk("harvey_museum_browse")
    ],
    "archaeologyhouse": [
        walk("museum_outdoor_door"),
        warp("museum_indoor_entry"),
        walk("harvey_museum_browse")
    ],
    "saloon": [
        walk("saloon_outdoor_door"),
        warp("saloon_indoor_entry"),
        walk("harvey_saloon_seat")
    ],
    "town": [
        walk("harvey_town_walk")
    ]
}

# Jas route profiles
profiles["jas"] = {
    "animalshop": [
        walk("animalshop_outdoor_door"),
        warp("animalshop_indoor_entry"),
        walk("jas_animalshop_living")
    ],
    "museum": [
        walk("museum_outdoor_door"),
        warp("museum_indoor_entry"),
        walk("jas_museum_read")
    ],
    "archaeologyhouse": [
        walk("museum_outdoor_door"),
        warp("museum_indoor_entry"),
        walk("jas_museum_read")
    ],
    "town": [
        walk("jas_town_walk")
    ],
    "forest": [
        walk("jas_forest_jumprope")
    ],
    "beach": [
        walk("jas_beach_play")
    ]
}

save_json(rpf_path, rpf)
print(f"  [OK] Added profiles for haley, harvey, jas (total {len(profiles)} NPCs)")

# ========== Summary ==========
print("\n=== SUMMARY ===")
print("1. base_profiles.json: Fixed haley.optimism=1, harvey.optimism=1, jas.manners=1")
print(f"2. haley schedule: Removed {removed} darkroom entries")
print(f"3. npc_route_points.json: Added {added} new points")
print("4. npc_route_profiles.json: Added haley, harvey, jas profiles")
