#!/usr/bin/env python3
"""
Master audio extraction for StardewCraft mod.
Maps all needed sounds from wiki indices, extracts from XWB, converts to OGG,
and places them in the correct asset directories.

Sound index data from: https://zh.stardewvalleywiki.com/模组:音频
"""
import subprocess
import os
import sys
import shutil

# Path configuration
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
SOUNDS_DIR = os.path.join(PROJECT_ROOT, "src/main/resources/assets/stardewcraft/sounds")
WAVEBANK_MAIN = os.path.join(PROJECT_ROOT, "源文件/音频文件/Wave Bank.xwb")
WAVEBANK_14 = os.path.join(PROJECT_ROOT, "源文件/音频文件/Wave Bank(1.4).xwb")
FFMPEG = os.path.expanduser("~/bin/ffmpeg")
TMP_DIR = "/tmp/stardew_audio_extract"

# ─── Sound mapping: { output_ogg_path (relative to sounds/): (wavebank, index) } ───
# Wavebank: "main" = Wave Bank.xwb, "1.4" = Wave Bank(1.4).xwb
# Multiple entries for same cue name = random variants (index from wiki)
SOUND_MAP = {
    # ========== Fishing ==========
    "fishing/sin_wave":          ("main", 245),
    "fishing/cast":              ("main", 246),
    "fishing/fish_bite":         ("main", 26),
    # fishBite_alternate uses same OGG (already mapped in sounds.json to fish_bite)
    "fishing/fish_hit":          ("main", 251),
    "fishing/fishing_rod_bend1": ("main", 254),
    "fishing/fishing_rod_bend2": ("main", 255),
    "fishing/fishing_rod_bend3": ("main", 256),
    "fishing/shiny4":            ("main", 19),
    "fishing/fast_reel":         ("main", 248),
    "fishing/slow_reel":         ("main", 247),
    "fishing/jingle1":           ("main", 252),
    "fishing/fish_escape":       ("main", 253),
    "fishing/tiny_whip":         ("main", 249),
    "fishing/water_slosh1":      ("main", 257),
    "fishing/water_slosh2":      ("main", 258),
    "fishing/water_slosh3":      ("main", 259),
    "fishing/dwop":              ("main", 234),
    "fishing/pull_item_from_water": ("main", 28),
    "fishing/drop_item_in_water":   ("main", 10),
    "fishing/open_chest":        ("main", 161),

    # ========== Utility / Machines ==========
    "utility/open_chest":        ("main", 161),
    "utility/door_creak":        ("main", 319),
    "utility/door_creak_reverse":("main", 322),
    "utility/select":            ("main", 148),
    "utility/ship1":             ("main", 96),
    "utility/ship2":             ("main", 97),
    "utility/bubbles1":          ("main", 235),
    "utility/bubbles2":          ("main", 236),
    "utility/siptea":            ("main", 193),
    "utility/yoba":              ("main", 202),
    "utility/fishslap":          ("main", 260),
    "utility/furnace":           ("main", 38),
    "utility/openbox":           ("main", 14),
    "utility/fireball":          ("main", 71),

    # ========== UI ==========
    "ui/cancel":                 ("main", 353),
    "ui/small_select":           ("main", 20),
    "ui/drumkit6":               ("main", 105),
    "ui/new_recipe":             ("main", 214),
    "ui/money":                  ("main", 61),

    # ========== General ==========
    "woody_step":                ("main", 29),
    "backpack_in":               ("main", 133),
    "shwip":                     ("main", 317),
    "big_select":                ("main", 3),
    "big_deselect":              ("main", 2),
    "breathin":                  ("main", 84),
    "breathout":                 ("main", 83),
    "cowboy_gunshot":            ("main", 266),
    "dialogue_character":        ("main", 7),
    "dialogue_character_close":  ("main", 8),
    "shadow_die":                ("main", 194),
    "thud_step":                 ("main", 24),
    "stone_step":                ("main", 23),
    "give_gift":                 ("main", 348),
    "trashcanlid":               ("main", 210),
    "throw_down_item":           ("main", 21),
    "money_dial":                ("main", 237),
    "money":                     ("main", 61),

    # ========== New Recipe / Artifact ==========
    "new_recipe":                ("main", 214),
    "new_artifact":              ("main", 211),

    # ========== Tree ==========
    "tree/tree_crack":           ("main", 140),
    "tree/tree_thud":            ("main", 139),

    # ========== Animals ==========
    "animals/cluck_1":           ("main", 31),
    "animals/cluck_2":           ("main", 32),
    "animals/cluck_3":           ("main", 33),
    "animals/duck":              ("main", 231),
    "animals/rabbit":            ("main", 74),
    "animals/ostrich":           ("main", 367),
    "animals/cow_1":             ("main", 80),
    "animals/cow_2":             ("main", 81),
    "animals/cow_3":             ("main", 82),
    "animals/goat_1":            ("main", 78),
    "animals/goat_2":            ("main", 79),
    "animals/sheep":             ("main", 232),
    "animals/pig_1":             ("main", 130),
    "animals/pig_2":             ("main", 131),

    # ========== Trash can extras (explosion for Mega/DoubleMega) ==========
    "trashcan":                  ("main", 209),
    "explosion":                 ("main", 35),
    "crit":                      ("main", 352),

    # ========== Level up ==========
    "level_up":                  ("main", 214),  # reuse newRecipe sound

    # ========== Weapon (meow for cat weapon not in XWB, custom) ==========

    # ========== Extra sounds that may be needed ==========
    "crafting":                  ("main", 36),
    "purchase":                  ("main", 145),
    "sell":                      ("main", 242),
    "eat":                       ("main", 25),
    "axe":                       ("main", 1),
    "hoe_hit":                   ("main", 13),
    "seeds":                     ("main", 17),
    "watering_can":              ("main", 153),
    "cut":                       ("main", 6),
    "harvest":                   ("main", 326),
    "leafrustle":                ("main", 142),
    "hammer":                    ("main", 134),
    "coin":                      ("main", 5),
    "stonecrack1":               ("main", 75),
    "stonecrack2":               ("main", 76),
    "hit_enemy":                 ("main", 56),
    "swordswipe1":               ("main", 58),
    "swordswipe2":               ("main", 162),
    "daggerswipe":               ("main", 163),
    "clubhit":                   ("main", 159),
    "clubswipe":                 ("main", 160),
    "clubsmash":                 ("main", 174),
    "slingshot":                 ("main", 207),
    "pickupitem":                ("main", 15),
    "dooropen":                  ("main", 320),
    "doorclose":                 ("main", 9),
    "achievement":               ("main", 103),
    "questcomplete":             ("main", 128),
    "objectivecomplete":         ("main", 132),
    "select":                    ("main", 148),
    "stardrop":                  ("main", 351),
    "powerup":                   ("main", 39),
    "reward":                    ("main", 212),
    "getNewSpecialItem":         ("main", 223),
    "debuffhit":                 ("main", 151),
    "debuffspell":               ("main", 152),
    "healsound":                 ("main", 196),
    "death":                     ("main", 70),
    "monsterdead":               ("main", 158),
    "ghost":                     ("main", 11),
    "slime":                     ("main", 57),
    "bouldercrack":              ("main", 4),
    "boulderbreak":              ("main", 238),
    "stumpcrack":                ("main", 217),
    "thunder":                   ("main", 114),
    "rain":                      ("main", 116),
    "wind":                      ("main", 85),
    "crickets":                  ("main", 117),
    "rooster":                   ("main", 329),
    "dog_bark":                  ("main", 331),
    "cat1":                      ("main", 332),
    "cat2":                      ("main", 333),
    "furnace":                   ("main", 38),
    "detector":                  ("main", 37),
    "crystal":                   ("main", 143),
    "secret1":                   ("main", 218),
    "barrelbreak":               ("main", 310),
    "stairsdown":                ("main", 313),
    "cavedrip":                  ("main", 129),
    "discovermineral":           ("main", 208),
    "bob":                       ("main", 30),
    "toolcharge":                ("main", 62),
    "toolswap":                  ("main", 27),
}

# ─── Helper ───
sys.path.insert(0, SCRIPT_DIR)
from extract_xwb import extract_xwb


def main():
    os.makedirs(TMP_DIR, exist_ok=True)

    # Collect all needed indices per wavebank
    main_indices = set()
    v14_indices = set()
    for ogg_name, (bank, idx) in SOUND_MAP.items():
        if bank == "main":
            main_indices.add(idx)
        elif bank == "1.4":
            v14_indices.add(idx)

    # Extract from main wave bank
    print("=" * 60)
    print(f"Extracting {len(main_indices)} entries from Wave Bank.xwb ...")
    main_out = os.path.join(TMP_DIR, "main")
    main_results = extract_xwb(WAVEBANK_MAIN, main_out, main_indices)

    # Extract from 1.4 wave bank if needed
    v14_results = {}
    if v14_indices:
        print(f"\nExtracting {len(v14_indices)} entries from Wave Bank(1.4).xwb ...")
        v14_out = os.path.join(TMP_DIR, "v14")
        v14_results = extract_xwb(WAVEBANK_14, v14_out, v14_indices)

    # Convert WAV to OGG and place in assets
    print("\n" + "=" * 60)
    print("Converting to OGG and placing in assets ...")
    success = 0
    failed = 0

    for ogg_name, (bank, idx) in sorted(SOUND_MAP.items()):
        results = main_results if bank == "main" else v14_results
        wav_path = results.get(idx)
        if wav_path is None or not os.path.exists(wav_path):
            print(f"  MISS: {ogg_name} (bank={bank}, idx={idx})")
            failed += 1
            continue

        ogg_path = os.path.join(SOUNDS_DIR, ogg_name + ".ogg")
        os.makedirs(os.path.dirname(ogg_path), exist_ok=True)

        # Convert with ffmpeg
        cmd = [
            FFMPEG, "-y", "-i", wav_path,
            "-c:a", "libvorbis", "-q:a", "4",
            ogg_path
        ]
        result = subprocess.run(cmd, capture_output=True, text=True)
        if result.returncode != 0:
            print(f"  FAIL: {ogg_name} — {result.stderr[-200:]}")
            failed += 1
        else:
            size = os.path.getsize(ogg_path)
            print(f"  OK: {ogg_name}.ogg ({size} bytes)")
            success += 1

    print(f"\nDone: {success} converted, {failed} failed")

    # Cleanup
    shutil.rmtree(TMP_DIR, ignore_errors=True)


if __name__ == "__main__":
    main()
