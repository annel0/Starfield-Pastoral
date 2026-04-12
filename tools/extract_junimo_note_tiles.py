#!/usr/bin/env python3
"""
从 townInterior.png 中截取祝尼魔笔记（Junimo Note）交互方块的 tile，
以及星星牌匾相关 tile，输出放大版 PNG。
"""

from pathlib import Path
from PIL import Image

ROOT = Path(__file__).parent.parent
MAPS_DIR = ROOT / "源文件" / "Content" / "Maps"
OUTPUT_DIR = Path(__file__).parent

TILE_W = 16
TILE_H = 16
COLUMNS = 32  # townInterior.png 是 32 列


def get_tile(img, tile_id):
    """从 tileset 中截取指定 tile"""
    x = (tile_id % COLUMNS) * TILE_W
    y = (tile_id // COLUMNS) * TILE_H
    return img.crop((x, y, x + TILE_W, y + TILE_H))


def save_strip(img, tile_ids, output_path, label, scale=4):
    """将一组 tile 横排拼接并放大保存"""
    n = len(tile_ids)
    strip = Image.new("RGBA", (n * TILE_W, TILE_H), (0, 0, 0, 0))
    for i, tid in enumerate(tile_ids):
        tile = get_tile(img, tid)
        strip.paste(tile, (i * TILE_W, 0))
    # 放大
    strip_scaled = strip.resize((strip.width * scale, strip.height * scale), Image.NEAREST)
    strip_scaled.save(output_path)
    print(f"  {label}: {output_path.name} ({strip_scaled.width}×{strip_scaled.height} px)")


def save_single(img, tile_id, output_path, label, scale=6):
    """截取单个 tile 并放大保存"""
    tile = get_tile(img, tile_id)
    tile_scaled = tile.resize((tile.width * scale, tile.height * scale), Image.NEAREST)
    tile_scaled.save(output_path)
    print(f"  {label}: {output_path.name} ({tile_scaled.width}×{tile_scaled.height} px)")


def save_grid(img, tile_ids_rows, output_path, label, scale=4):
    """将 tile 按多行排列并放大保存"""
    max_cols = max(len(row) for row in tile_ids_rows)
    n_rows = len(tile_ids_rows)
    grid = Image.new("RGBA", (max_cols * TILE_W, n_rows * TILE_H), (0, 0, 0, 0))
    for r, row in enumerate(tile_ids_rows):
        for c, tid in enumerate(row):
            tile = get_tile(img, tid)
            grid.paste(tile, (c * TILE_W, r * TILE_H))
    grid_scaled = grid.resize((grid.width * scale, grid.height * scale), Image.NEAREST)
    grid_scaled.save(output_path)
    print(f"  {label}: {output_path.name} ({grid_scaled.width}×{grid_scaled.height} px)")


def main():
    tileset_path = MAPS_DIR / "townInterior.png"
    print(f"源贴图: {tileset_path}")
    img = Image.open(tileset_path).convert("RGBA")
    print(f"  尺寸: {img.width}×{img.height}, 列数: {COLUMNS}")

    print("\n=== 截取祝尼魔笔记方块 ===")

    # 普通房间的 Junimo Note 动画帧 (Buildings 层)
    # tile 1824-1833，共10帧动画循环
    normal_note_ids = list(range(1824, 1834))  # 1824..1833
    save_strip(img, normal_note_ids, OUTPUT_DIR / "junimo_note_normal_frames.png",
               "普通笔记动画帧 (1824-1833)")

    # 单独保存最常见的那个亮闪闪状态
    save_single(img, 1833, OUTPUT_DIR / "junimo_note_glow.png",
                "笔记发光状态 (1833)")
    save_single(img, 1824, OUTPUT_DIR / "junimo_note_frame0.png",
                "笔记动画第0帧 (1824)")

    # 公告栏的 Junimo Note (Front 层)
    # tile 1741, 1773, 1805
    bulletin_note_ids = [1741, 1773, 1805]
    save_strip(img, bulletin_note_ids, OUTPUT_DIR / "junimo_note_bulletin_frames.png",
               "公告栏笔记帧 (1741, 1773, 1805)")

    # 也单独截取公告栏各帧
    for tid in bulletin_note_ids:
        save_single(img, tid, OUTPUT_DIR / f"junimo_note_bulletin_{tid}.png",
                    f"公告栏笔记 tile {tid}")

    print("\n=== 截取星星牌匾 ===")
    # 星星牌匾在 junimo_furniture.png 中
    # 但在 townInterior.png 中也有相关装饰 tile
    # 先从 CommunityCenter.cs 找 addStarToPlaque 用到的位置
    # 牌匾位置是 (32,9) 和 (33,9)，修复后地图那里的 tile
    # 从 Refurbished TMX Back层看，(32,9) = 541, (33,9) = 541

    # junimo_furniture.png 中的星星牌匾
    jf_path = ROOT / "源文件" / "Content" / "TileSheets" / "junimo_furniture.png"
    if jf_path.exists():
        jf_img = Image.open(jf_path).convert("RGBA")
        jf_cols = jf_img.width // TILE_W
        print(f"\njunimo_furniture.png: {jf_img.width}×{jf_img.height}, 列数: {jf_cols}")

        # 截取整张图的下半部分（包含星星牌匾和宝珠）
        # 宝珠在大约 y=160 区域，牌匾在更下方
        # 直接截取整个下半部分
        lower_half = jf_img.crop((0, jf_img.height // 2, jf_img.width, jf_img.height))
        scale = 4
        lower_scaled = lower_half.resize(
            (lower_half.width * scale, lower_half.height * scale), Image.NEAREST)
        out_path = OUTPUT_DIR / "junimo_furniture_lower_half.png"
        lower_scaled.save(out_path)
        print(f"  junimo_furniture 下半部分: {out_path.name}")

        # 裁出宝珠那一行 (彩色球)
        # 扫描找到宝珠 - 大约在 y=10 tile 行
        orb_row = jf_img.crop((0, 10 * TILE_H, 8 * TILE_W, 11 * TILE_H))
        orb_scaled = orb_row.resize((orb_row.width * scale, orb_row.height * scale), Image.NEAREST)
        out_orb = OUTPUT_DIR / "junimo_color_orbs.png"
        orb_scaled.save(out_orb)
        print(f"  彩色宝珠: {out_orb.name}")

    # 从 Junimo.png 截取星星和捆包精灵
    junimo_path = ROOT / "源文件" / "Content" / "Characters" / "Junimo.png"
    if junimo_path.exists():
        j_img = Image.open(junimo_path).convert("RGBA")
        print(f"\nJunimo.png: {j_img.width}×{j_img.height}")

        # 捆包物品: (0, 96, 16, 13)
        bundle_sprite = j_img.crop((0, 96, 16, 96 + 13))
        scale = 6
        bundle_scaled = bundle_sprite.resize(
            (bundle_sprite.width * scale, bundle_sprite.height * scale), Image.NEAREST)
        out_bundle = OUTPUT_DIR / "junimo_held_bundle.png"
        bundle_scaled.save(out_bundle)
        print(f"  祝尼魔手持捆包: {out_bundle.name}")

        # 星星: (0, 109, 16, 19)
        star_sprite = j_img.crop((0, 109, 16, 109 + 19))
        star_scaled = star_sprite.resize(
            (star_sprite.width * scale, star_sprite.height * scale), Image.NEAREST)
        out_star = OUTPUT_DIR / "junimo_held_star.png"
        star_scaled.save(out_star)
        print(f"  祝尼魔手持星星: {out_star.name}")

    print("\n完成！")


if __name__ == "__main__":
    main()
