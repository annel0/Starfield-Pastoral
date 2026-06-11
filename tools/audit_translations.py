#!/usr/bin/env python3
import argparse
import json
import re
from collections import OrderedDict, defaultdict
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
LANG_DIR = ROOT / "src/main/resources/assets/stardewcraft/lang"
DATA_DIR = ROOT / "src/main/resources/data/stardewcraft"
JAVA_DIR = ROOT / "src/main/java"
CONTENT_DIR = ROOT / "源文件/Content"

KEY_PREFIXES = (
    "advancements.stardewcraft.",
    "block.stardewcraft.",
    "container.stardewcraft.",
    "entity.stardewcraft.",
    "event.stardewcraft.",
    "gui.stardewcraft.",
    "item.stardewcraft.",
    "message.stardewcraft.",
    "recipe.stardewcraft.",
    "stardewcraft.",
)

SKIP_PREFIXES = (
    # These are dynamic item names. SmokedFishItem builds the displayed name from
    # stardewcraft.preserve.smoked_fish.flavored_name + the source fish name.
    "item.stardewcraft.smoked_",
    "stardewcraft.type.",
)

SKIP_KEYS = {
    # Examples/debug/system-property keys, not user-facing translations.
    "block.stardewcraft.xxx",
    "item.stardewcraft.xxx.desc",
    "stardewcraft.eagerPregenBiomeMigration",
    "stardewcraft.event.some_message",
    "stardewcraft.event.test.line1",
    "stardewcraft.key",
    "stardewcraft.npcMovementDebug",
    "stardewcraft.secret_woods_open",
}

SKIP_BLOCK_SUFFIXES = (
    # Internal helper blocks rendered by block entities.
    "_top_render",
)

STATIC_STRING_RE = re.compile(r'"((?:[^"\\]|\\.)*)"')
JAVA_TRANSLATABLE_RE = re.compile(r'(?:Component|TextComponent)?\.?translatable\(\s*"((?:[^"\\]|\\.)*)"')
TEXT_JSON_TRANSLATE_RE = re.compile(r'\\"translate\\":\\"([^"\\]+)\\"')


STRING_KEY_MAP = {
    "block.stardewcraft.beach_artifact_spot": ("Strings/Objects", "ArtifactSpot_Name"),
    "block.stardewcraft.large_boulder": ("Strings/BigCraftables", "Boulder_Name"),
    "stardewcraft.mail.pamNewChannel": ("Data/mail", "pamNewChannel"),
    "stardewcraft.menu.community_center": ("Strings/UI", "GameMenu_JunimoNote_Hover"),
    "stardewcraft.trout_derby.booth.intro": ("Strings/1_6_Strings", "FishingDerbyBooth_Intro"),
    "stardewcraft.trout_derby.booth.explanation": ("Strings/1_6_Strings", "FishingDerbyBooth_Explanation"),
    "stardewcraft.trout_derby.booth.explanation.choice": ("Strings/1_6_Strings", "FishingDerbyBooth_Explanation"),
    "stardewcraft.trout_derby.booth.no_tags": ("Strings/1_6_Strings", "FishingDerbyBooth_NoTags"),
    "stardewcraft.trout_derby.booth.get_rewards": ("Strings/1_6_Strings", "GetRewards"),
    "stardewcraft.trout_derby.booth.leave": ("Strings/1_6_Strings", "Leave"),
    "stardewcraft.trout_derby.booth.bag_full": ("Strings/UI", "Forge_noroom"),
}

OBJECT_NAME_KEYS_BY_BLOCK = {
    "fall_wild_seed_crop": "FallSeeds_Name",
    "rice_crop": "Rice_Name",
    "spring_wild_seed_crop": "SpringSeeds_Name",
    "summer_wild_seed_crop": "SummerSeeds_Name",
    "winter_wild_seed_crop": "WinterSeeds_Name",
}

GIANT_CROP_OBJECT_KEYS = {
    "giant_cauliflower": "Cauliflower_Name",
    "giant_melon": "Melon_Name",
    "giant_powdermelon": "Powdermelon_Name",
    "giant_pumpkin": "Pumpkin_Name",
}


def load_json(path):
    with path.open(encoding="utf-8") as f:
        return json.load(f, object_pairs_hook=OrderedDict)


def write_json(path, data):
    path.write_text(
        json.dumps(data, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )


def is_translation_key(value):
    if not isinstance(value, str):
        return False
    if value in SKIP_KEYS:
        return False
    if value.endswith(".") or value.endswith("_"):
        return False
    if ":" in value or " " in value or "\n" in value:
        return False
    if value.startswith(SKIP_PREFIXES):
        return False
    if value.startswith("block.stardewcraft.") and value.endswith(SKIP_BLOCK_SUFFIXES):
        return False
    return value.startswith(KEY_PREFIXES)


def unescape_java_string(value):
    try:
        return bytes(value, "utf-8").decode("unicode_escape")
    except UnicodeDecodeError:
        return value


def add_ref(refs, key, path, detail):
    if is_translation_key(key):
        refs[key].add(f"{path}:{detail}")


def collect_java_refs(refs):
    for path in JAVA_DIR.rglob("*.java"):
        rel = path.relative_to(ROOT)
        text = path.read_text(encoding="utf-8", errors="ignore")
        for match in JAVA_TRANSLATABLE_RE.finditer(text):
            add_ref(refs, unescape_java_string(match.group(1)), rel, "translatable")
        for match in TEXT_JSON_TRANSLATE_RE.finditer(text):
            add_ref(refs, match.group(1), rel, "text-json")
        for match in STATIC_STRING_RE.finditer(text):
            value = unescape_java_string(match.group(1))
            if value.startswith(KEY_PREFIXES):
                add_ref(refs, value, rel, "string")


def walk_json_strings(node, callback, path=()):
    if isinstance(node, dict):
        for key, value in node.items():
            walk_json_strings(value, callback, path + (str(key),))
    elif isinstance(node, list):
        for index, value in enumerate(node):
            walk_json_strings(value, callback, path + (str(index),))
    elif isinstance(node, str):
        callback(node, path)


def collect_data_refs(refs):
    for path in DATA_DIR.rglob("*.json"):
        rel = path.relative_to(ROOT)
        try:
            root = load_json(path)
        except json.JSONDecodeError:
            continue

        def visit(value, json_path):
            key_name = json_path[-1] if json_path else ""
            if key_name == "translate":
                add_ref(refs, value, rel, ".".join(json_path))
            elif is_translation_key(value):
                add_ref(refs, value, rel, ".".join(json_path))

        walk_json_strings(root, visit)


def collect_registry_refs(refs):
    blocks = ROOT / "src/main/java/com/stardew/craft/block/ModBlocks.java"
    items = ROOT / "src/main/java/com/stardew/craft/item/ModItems.java"
    register_re = re.compile(r'\bregister\(\s*"([a-z0-9_./-]+)"\s*,')
    if blocks.exists():
        for name in register_re.findall(blocks.read_text(encoding="utf-8", errors="ignore")):
            add_ref(refs, f"block.stardewcraft.{name}", blocks.relative_to(ROOT), "registry")
    if items.exists():
        for name in register_re.findall(items.read_text(encoding="utf-8", errors="ignore")):
            add_ref(refs, f"item.stardewcraft.{name}", items.relative_to(ROOT), "registry")


def source_json_pair(base):
    en = CONTENT_DIR / f"{base}.json"
    zh = CONTENT_DIR / f"{base}.zh-CN.json"
    if en.exists() and zh.exists():
        return load_json(en), load_json(zh)
    return None, None


def build_string_sources():
    cache = {}
    for base, _key in set(STRING_KEY_MAP.values()):
        cache[base] = source_json_pair(base)
    cache["Strings/Objects"] = source_json_pair("Strings/Objects")
    return cache


def vanilla_dialogue_file(npc_id):
    dialogue_dir = CONTENT_DIR / "Characters/Dialogue"
    if not dialogue_dir.exists():
        return None
    wanted = npc_id.lower()
    for path in dialogue_dir.glob("*.json"):
        stem = path.stem
        if stem.endswith(".zh-CN"):
            continue
        if stem.lower() == wanted:
            return stem
    return None


def collect_autofill_values():
    autofill = {}
    sources = {}

    string_sources = build_string_sources()
    for lang_key, (base, source_key) in STRING_KEY_MAP.items():
        en, zh = string_sources.get(base, (None, None))
        if en and zh and source_key in en and source_key in zh:
            autofill[lang_key] = (en[source_key], zh[source_key])
            sources[lang_key] = f"Content/{base}: {source_key}"

    objects_en, objects_zh = string_sources.get("Strings/Objects", (None, None))
    if objects_en and objects_zh:
        for block_id, source_key in OBJECT_NAME_KEYS_BY_BLOCK.items():
            if source_key in objects_en and source_key in objects_zh:
                lang_key = f"block.stardewcraft.{block_id}"
                autofill[lang_key] = (f"{objects_en[source_key]} Crop", f"{objects_zh[source_key]}作物")
                sources[lang_key] = f"Content/Strings/Objects: {source_key} + crop block suffix"

        for block_id, source_key in GIANT_CROP_OBJECT_KEYS.items():
            if source_key in objects_en and source_key in objects_zh:
                lang_key = f"block.stardewcraft.{block_id}"
                autofill[lang_key] = (f"Giant {objects_en[source_key]}", f"巨型{objects_zh[source_key]}")
                sources[lang_key] = f"Content/Strings/Objects: {source_key} + giant crop prefix"

    extra_dialogue_en, extra_dialogue_zh = source_json_pair("Data/ExtraDialogue")

    dialogue_dir = DATA_DIR / "npc/dialogue"
    if dialogue_dir.exists():
        for path in dialogue_dir.glob("*.json"):
            data = load_json(path)
            npc_id = data.get("npc_id", path.stem)
            entries = data.get("entries", {})
            source_stem = vanilla_dialogue_file(npc_id)
            if source_stem:
                en_path = CONTENT_DIR / f"Characters/Dialogue/{source_stem}.json"
                zh_path = CONTENT_DIR / f"Characters/Dialogue/{source_stem}.zh-CN.json"
                if en_path.exists() and zh_path.exists():
                    en = load_json(en_path)
                    zh = load_json(zh_path)
                    for source_key, lang_key in entries.items():
                        if not isinstance(lang_key, str):
                            continue
                        if source_key in en and source_key in zh:
                            autofill[lang_key] = (en[source_key], zh[source_key])
                            sources[lang_key] = f"Content/Characters/Dialogue/{source_stem}: {source_key}"

            if extra_dialogue_en and extra_dialogue_zh:
                for source_key, lang_key in entries.items():
                    if not isinstance(lang_key, str):
                        continue
                    if lang_key in autofill:
                        continue
                    if source_key in extra_dialogue_en and source_key in extra_dialogue_zh:
                        autofill[lang_key] = (extra_dialogue_en[source_key], extra_dialogue_zh[source_key])
                        sources[lang_key] = f"Content/Data/ExtraDialogue: {source_key}"

    return autofill, sources


def main():
    parser = argparse.ArgumentParser(description="Audit StardewCraft translation keys.")
    parser.add_argument("--fix", action="store_true", help="write missing auto-fillable keys to lang JSON files")
    parser.add_argument("--report", default=".tmp/missing_translations.json", help="report path")
    args = parser.parse_args()

    en_lang = load_json(LANG_DIR / "en_us.json")
    zh_lang = load_json(LANG_DIR / "zh_cn.json")

    refs = defaultdict(set)
    collect_java_refs(refs)
    collect_data_refs(refs)
    collect_registry_refs(refs)

    autofill, autofill_sources = collect_autofill_values()
    all_keys = sorted(refs)
    effective_en = set(en_lang)
    effective_zh = set(zh_lang)

    # BlockItem display names resolve through the block description id.  If the
    # matching block key exists, the item.* registry id is already covered.
    for key in all_keys:
        prefix = "item.stardewcraft."
        if not key.startswith(prefix):
            continue
        block_key = "block.stardewcraft." + key[len(prefix):]
        if block_key in en_lang:
            effective_en.add(key)
        if block_key in zh_lang:
            effective_zh.add(key)

    missing_en = [key for key in all_keys if key not in effective_en]
    missing_zh = [key for key in all_keys if key not in effective_zh]
    missing_any = sorted(set(missing_en) | set(missing_zh))
    auto_keys = [key for key in missing_any if key in autofill]
    unresolved = [key for key in missing_any if key not in autofill]

    if args.fix:
        for key in auto_keys:
            en_value, zh_value = autofill[key]
            if key not in en_lang:
                en_lang[key] = en_value
            if key not in zh_lang:
                zh_lang[key] = zh_value
        write_json(LANG_DIR / "en_us.json", en_lang)
        write_json(LANG_DIR / "zh_cn.json", zh_lang)

    report = {
        "referenced_key_count": len(all_keys),
        "missing_en_count": len(missing_en),
        "missing_zh_count": len(missing_zh),
        "auto_fillable_count": len(auto_keys),
        "unresolved_count": len(unresolved),
        "auto_fillable": [
            {
                "key": key,
                "source": autofill_sources.get(key, ""),
                "refs": sorted(refs[key]),
            }
            for key in auto_keys
        ],
        "unresolved": [
            {
                "key": key,
                "refs": sorted(refs[key]),
            }
            for key in unresolved
        ],
    }

    report_path = ROOT / args.report
    report_path.parent.mkdir(parents=True, exist_ok=True)
    write_json(report_path, report)

    print(f"Referenced keys: {len(all_keys)}")
    print(f"Missing en_us: {len(missing_en)}")
    print(f"Missing zh_cn: {len(missing_zh)}")
    print(f"Auto-fillable: {len(auto_keys)}")
    print(f"Unresolved: {len(unresolved)}")
    print(f"Report: {report_path.relative_to(ROOT)}")
    if args.fix:
        print(f"Wrote {len(auto_keys)} auto-fillable keys to lang files.")


if __name__ == "__main__":
    main()
