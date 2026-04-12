#!/usr/bin/env python3
"""Verify Jodi/Leah/Linus onboarding completeness."""
import json, os

DATA = "src/main/resources/data/stardewcraft/npc"
LANG = "src/main/resources/assets/stardewcraft/lang"

def lj(path):
    with open(path, encoding="utf-8-sig") as f:
        return json.load(f)

print("=== FILE CONTENT VERIFICATION ===\n")

for npc in ["jodi", "leah", "linus"]:
    print(f"--- {npc.upper()} ---")
    d = lj(f"{DATA}/dialogue/{npc}.json")
    print(f"  dialogue: {len(d.get('entries',{}))} entries")
    d = lj(f"{DATA}/tastes/{npc}.json")
    print(f"  tastes: loved={len(d.get('loved',[]))}, liked={len(d.get('liked',[]))}, disliked={len(d.get('disliked',[]))}, hated={len(d.get('hated',[]))}")
    d = lj(f"{DATA}/schedules/{npc}.json")
    seasons = [k for k in d if k not in ("npc_id","override_vanilla","_format")]
    print(f"  schedules: {seasons}")

en = lj(f"{LANG}/en_us.json")
zh = lj(f"{LANG}/zh_cn.json")
for npc in ["jodi", "leah", "linus"]:
    en_c = len([k for k in en if f".npc.{npc}." in k or k == f"entity.stardewcraft.npc.{npc}"])
    zh_c = len([k for k in zh if f".npc.{npc}." in k or k == f"entity.stardewcraft.npc.{npc}"])
    print(f"  {npc} translations: en={en_c}, zh={zh_c}")

bd = lj(f"{DATA}/events/npc_birthdays.json")
ds = lj(f"{DATA}/events/default_spawns.json")
bp = lj(f"{DATA}/capabilities/base_profiles.json")

print("\n=== PROFILE / BIRTHDAY / SPAWN ===")
for npc in ["jodi", "leah", "linus"]:
    entry = next((n for n in bp["npcs"] if n["id"] == npc), None)
    b = bd["birthdays"].get(npc)
    s = ds["spawns"].get(npc)
    print(f"  {npc}: profile={'OK' if entry else 'MISSING'}, birthday={b}, spawn={s}")
    if entry:
        print(f"    age={entry['age']}, manners={entry['manners']}, anxiety={entry['social_anxiety']}, optimism={entry['optimism']}, gender={entry['gender']}, datable={entry['datable']}")

print("\n=== BUILDING ENTRY POINTS ===")
rp = lj(f"{DATA}/events/npc_route_points.json")
pts = rp.get("points", {})
for k in ["samhouse_outdoor_door", "samhouse_indoor_entry", "leahhouse_outdoor_door", "leahhouse_indoor_entry"]:
    print(f"  {k}: {'EXISTS' if k in pts else 'MISSING'}")

print("\n=== ART ASSETS ===")
art_base = "src/main/resources/assets/stardewcraft"
for npc in ["jodi", "leah", "linus"]:
    checks = {
        "entity": f"{art_base}/textures/entity/npc/{npc}.png",
        "portrait": f"{art_base}/textures/portraits/{npc}.png",
        "mugshot": f"{art_base}/textures/mugshots/{npc}.png",
        "geo": f"{art_base}/geo/entity/npc/{npc}.geo.json",
        "anim": f"{art_base}/animations/entity/npc/{npc}.animation.json",
    }
    missing = [k for k, v in checks.items() if not os.path.exists(v)]
    if missing:
        print(f"  {npc}: MISSING {missing}")
    else:
        print(f"  {npc}: all art assets present")

# Check Guide Appendix A correctness
print("\n=== PERSONALITY vs GUIDE APPENDIX A ===")
expected = {
    "jodi": {"age": 0, "manners": 1, "social_anxiety": 0, "optimism": 0, "gender": 1, "datable": False},
    "leah": {"age": 0, "manners": 1, "social_anxiety": 0, "optimism": 0, "gender": 1, "datable": True},
    "linus": {"age": 0, "manners": 1, "social_anxiety": 1, "optimism": 1, "gender": 0, "datable": False},
}
for npc, exp in expected.items():
    entry = next((n for n in bp["npcs"] if n["id"] == npc), None)
    if not entry:
        print(f"  {npc}: MISSING from profiles!")
        continue
    errors = []
    for k, v in exp.items():
        actual = entry.get(k)
        if actual != v:
            errors.append(f"{k}: expected={v}, got={actual}")
    if errors:
        print(f"  {npc}: ERRORS - {errors}")
    else:
        print(f"  {npc}: OK")
