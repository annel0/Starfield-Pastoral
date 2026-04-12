#!/usr/bin/env python3
"""
从官方中文翻译文件中提取 Haley / Harvey / Jas 的中文对话，
注入到项目的 zh_cn.json 中。

- 自动备份原始文件
- 严格使用 UTF-8 编码读写
- 逐 key 映射：en_us key → vanilla dialogue key → 官方中文文本
"""

import json
import os
import shutil
import sys
from datetime import datetime
from pathlib import Path

# ─── 路径 ─────────────────────────────────────────────
PROJECT = Path(__file__).resolve().parent.parent
LANG_DIR = PROJECT / "src" / "main" / "resources" / "assets" / "stardewcraft" / "lang"
ZH_CN_PATH = LANG_DIR / "zh_cn.json"
EN_US_PATH = LANG_DIR / "en_us.json"

VANILLA_DIR = PROJECT / "源文件" / "Content"
DIALOGUE_DIR = VANILLA_DIR / "Characters" / "Dialogue"


def load_json(path: Path) -> dict:
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)


def save_json(path: Path, data: dict):
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=4)
    # 验证写入结果
    with open(path, "r", encoding="utf-8") as f:
        json.load(f)
    print(f"  ✓ 已保存并验证: {path}")


def backup(path: Path):
    ts = datetime.now().strftime("%Y%m%d_%H%M%S")
    bak = path.with_suffix(f".{ts}.bak")
    shutil.copy2(path, bak)
    print(f"  ✓ 备份: {bak.name}")
    return bak


# ─── 官方中文数据源 ────────────────────────────────────
def build_vanilla_zh_map():
    """
    加载所有需要的官方中文 JSON，返回:
    {
      "haley": { "Introduction": "噢……你就是...", ... },
      "harvey": { ... },
      "jas": { ... },
      "marriage_haley": { ... },
      "marriage_harvey": { ... },
      "gift_tastes": { "Haley": "...", "Harvey": "...", "Jas": "..." },
      "npc_names": { "Haley": "海莉", ... },
    }
    """
    data = {}
    for npc in ("Haley", "Harvey", "Jas"):
        data[npc.lower()] = load_json(DIALOGUE_DIR / f"{npc}.zh-CN.json")

    for npc in ("Haley", "Harvey"):
        data[f"marriage_{npc.lower()}"] = load_json(
            DIALOGUE_DIR / f"MarriageDialogue{npc}.zh-CN.json"
        )

    data["gift_tastes"] = load_json(VANILLA_DIR / "Data" / "NPCGiftTastes.zh-CN.json")
    data["npc_names"] = load_json(VANILLA_DIR / "Strings" / "NPCNames.zh-CN.json")
    return data


# ─── key 映射规则 ──────────────────────────────────────
def our_key_to_vanilla_key(our_key: str):
    """
    将项目 key 转换为 vanilla key + 来源。

    例:
      stardewcraft.npc.haley.introduction  → ("Introduction", "haley")
      stardewcraft.npc.haley.mon           → ("Mon", "haley")
      stardewcraft.npc.haley.summer_sat_12 → ("summer_Sat_12", "haley")
      entity.stardewcraft.npc.haley        → (None, "entity_haley")
      stardewcraft.npc.haley.gift_taste.loved → (None, "gift_haley_loved")

    返回 (vanilla_key, source_category)
    """
    # 实体名
    if our_key.startswith("entity.stardewcraft.npc."):
        npc = our_key.split(".")[-1]
        return (None, f"entity_{npc}")

    # 注释 key
    if "_comment_" in our_key:
        return (None, "comment")

    # 必须是 stardewcraft.npc.<name>.xxx
    parts = our_key.split(".")
    if len(parts) < 4 or parts[0] != "stardewcraft" or parts[1] != "npc":
        return (None, "unknown")

    npc = parts[2]  # haley / harvey / jas
    suffix = ".".join(parts[3:])  # e.g. "gift_taste.loved" or "introduction"

    # gift taste
    if suffix.startswith("gift_taste."):
        taste = suffix.split(".")[-1]  # loved/liked/neutral/disliked/hated
        return (None, f"gift_{npc}_{taste}")

    return (suffix, npc)


# vanilla dialogue files 中 key 的大小写不一致，需要做不区分大小写查找
def find_in_vanilla(vanilla_data: dict, source: str, vanilla_key_lower: str):
    """在 vanilla_data[source] 中做大小写不敏感的 key 查找"""
    if source not in vanilla_data:
        return None
    src = vanilla_data[source]
    # 先精确匹配
    if vanilla_key_lower in src:
        return src[vanilla_key_lower]
    # 不区分大小写
    key_map = {k.lower(): k for k in src}
    actual = key_map.get(vanilla_key_lower)
    if actual:
        return src[actual]
    return None


def parse_gift_taste_string(raw: str, index: int):
    """
    NPCGiftTastes 格式: "loved_msg/loved_ids/liked_msg/liked_ids/disliked_msg/disliked_ids/hated_msg/hated_ids/neutral_msg/neutral_ids/"
    index: 0=loved, 2=liked, 4=disliked, 6=hated, 8=neutral (每 2 个一组，msg 在偶数位)
    """
    parts = raw.split("/")
    if index < len(parts):
        return parts[index]
    return None


TASTE_INDEX = {
    "loved": 0,
    "liked": 2,
    "disliked": 4,
    "hated": 6,
    "neutral": 8,
}


def build_translations(vanilla_data: dict, en_us: dict) -> dict:
    """
    遍历 en_us 中 haley/harvey/jas 相关 key，
    从 vanilla 中文数据中查找对应翻译。
    返回 { our_key: zh_text }
    """
    translations = {}
    missing = []

    target_npcs = {"haley", "harvey", "jas"}

    for our_key, en_val in en_us.items():
        # 判断是否属于目标 NPC
        is_entity = our_key.startswith("entity.stardewcraft.npc.")
        is_npc_key = our_key.startswith("stardewcraft.npc.")

        if is_entity:
            npc = our_key.split(".")[-1]
            if npc not in target_npcs:
                continue
        elif is_npc_key:
            parts = our_key.split(".")
            if len(parts) < 4:
                continue
            npc = parts[2]
            if npc not in target_npcs:
                continue
        else:
            continue

        # 注释 key 直接跳过（不需要翻译）
        if "_comment_" in our_key:
            continue

        vanilla_key, source = our_key_to_vanilla_key(our_key)

        # ── 实体名 ──
        if source.startswith("entity_"):
            npc_name = npc.capitalize()
            if npc_name == "Jas":
                npc_name = "Jas"
            zh = vanilla_data["npc_names"].get(npc_name)
            if zh:
                translations[our_key] = zh
            else:
                missing.append((our_key, "npc_names", npc_name))
            continue

        # ── 礼物口味 ──
        if source.startswith("gift_"):
            taste = source.split("_")[-1]
            npc_name = npc.capitalize()
            if npc_name == "Jas":
                npc_name = "Jas"
            raw = vanilla_data["gift_tastes"].get(npc_name)
            if raw and taste in TASTE_INDEX:
                zh = parse_gift_taste_string(raw, TASTE_INDEX[taste])
                if zh:
                    translations[our_key] = zh
                    continue
            missing.append((our_key, "gift_tastes", f"{npc_name}/{taste}"))
            continue

        # ── 普通对话 ──
        if vanilla_key is None:
            missing.append((our_key, source, "no_vanilla_key"))
            continue

        # 尝试从主对话文件查找
        zh = find_in_vanilla(vanilla_data, npc, vanilla_key)
        if zh:
            translations[our_key] = zh
            continue

        # 尝试从婚后对话文件查找（married_haley 下的 key 映射）
        # 但我们的 key 都来自主对话文件，婚后对话已经在主文件中
        # 再尝试 marriage_ 文件
        marriage_src = f"marriage_{npc}"
        if marriage_src in vanilla_data:
            zh = find_in_vanilla(vanilla_data, marriage_src, vanilla_key)
            if zh:
                translations[our_key] = zh
                continue

        missing.append((our_key, npc, vanilla_key))

    return translations, missing


def inject_into_zh_cn(zh_cn: dict, translations: dict, en_us: dict) -> dict:
    """
    将翻译注入 zh_cn，保持与 en_us 中 key 相同的相对顺序。
    新 key 插入到 zh_cn 末尾（在最后一个 } 之前）。
    """
    for key, zh_val in translations.items():
        zh_cn[key] = zh_val
    return zh_cn


def main():
    print("=" * 60)
    print("  Haley / Harvey / Jas 中文翻译注入脚本")
    print("=" * 60)

    # 1. 备份
    print("\n[1/5] 备份 zh_cn.json ...")
    backup(ZH_CN_PATH)

    # 2. 加载数据
    print("\n[2/5] 加载官方中文数据 ...")
    vanilla = build_vanilla_zh_map()
    for k, v in vanilla.items():
        if isinstance(v, dict):
            print(f"  - {k}: {len(v)} 条")

    print("\n[3/5] 加载项目 en_us.json ...")
    en_us = load_json(EN_US_PATH)
    print(f"  - en_us: {len(en_us)} 条")

    print("\n[4/5] 匹配翻译 ...")
    translations, missing = build_translations(vanilla, en_us)
    print(f"  ✓ 匹配成功: {len(translations)} 条")

    if missing:
        print(f"  ⚠ 未匹配: {len(missing)} 条:")
        for key, src, vkey in missing:
            print(f"      {key}  ←  [{src}] {vkey}")

    # 3. 注入
    print("\n[5/5] 注入到 zh_cn.json ...")
    zh_cn = load_json(ZH_CN_PATH)
    existing = len(zh_cn)
    zh_cn = inject_into_zh_cn(zh_cn, translations, en_us)
    added = len(zh_cn) - existing
    print(f"  + 新增 {added} 条 (总计 {len(zh_cn)} 条)")

    save_json(ZH_CN_PATH, zh_cn)

    print("\n" + "=" * 60)
    print(f"  完成！共注入 {len(translations)} 条中文翻译。")
    print("=" * 60)


if __name__ == "__main__":
    main()
