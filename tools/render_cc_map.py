#!/usr/bin/env python3
"""
渲染社区中心 TMX 地图为 PNG 图片。
用法: python render_cc_map.py
输出: CommunityCenter_Ruins.png 和 CommunityCenter_Refurbished.png
"""

import xml.etree.ElementTree as ET
from pathlib import Path
from PIL import Image

SCRIPT_DIR = Path(__file__).parent
ROOT = SCRIPT_DIR.parent
MAPS_DIR = ROOT / "源文件" / "Content" / "Maps"
OUTPUT_DIR = SCRIPT_DIR

TILE_W = 16
TILE_H = 16


def load_tilesets(tmx_root):
    """解析 TMX 中的 tileset 定义，返回 [(firstgid, image, columns)] 列表"""
    tilesets = []
    for ts in tmx_root.findall("tileset"):
        firstgid = int(ts.get("firstgid"))
        columns = int(ts.get("columns"))
        img_elem = ts.find("image")
        img_source = img_elem.get("source")  # e.g. "townInterior"
        # TMX 中 source 可能没有 .png 后缀
        img_path = MAPS_DIR / img_source
        if not img_path.suffix:
            img_path = img_path.with_suffix(".png")
        if not img_path.exists():
            print(f"  警告: 贴图 {img_path} 不存在，跳过此 tileset")
            tilesets.append((firstgid, None, columns))
        else:
            tilesets.append((firstgid, Image.open(img_path).convert("RGBA"), columns))
    # 按 firstgid 降序排列，方便查找
    tilesets.sort(key=lambda t: t[0], reverse=True)
    return tilesets


def get_tile_image(gid, tilesets):
    """根据全局 tile ID 从正确的 tileset 中裁剪出 16×16 的 tile 图片"""
    if gid == 0:
        return None
    for firstgid, img, columns in tilesets:
        if gid >= firstgid:
            if img is None:
                return None
            local_id = gid - firstgid
            sx = (local_id % columns) * TILE_W
            sy = (local_id // columns) * TILE_H
            return img.crop((sx, sy, sx + TILE_W, sy + TILE_H))
    return None


def parse_layer_data(layer_elem):
    """解析 CSV 格式的 layer data，返回二维列表 [row][col]"""
    data_elem = layer_elem.find("data")
    if data_elem is None or data_elem.get("encoding") != "csv":
        return []
    text = data_elem.text.strip()
    rows = []
    for line in text.split("\n"):
        line = line.strip().rstrip(",")
        if line:
            rows.append([int(x) for x in line.split(",")])
    return rows


def render_map(tmx_path, output_path):
    """渲染单个 TMX 地图为 PNG"""
    print(f"渲染: {tmx_path.name}")
    tree = ET.parse(tmx_path)
    root = tree.getroot()

    map_w = int(root.get("width"))
    map_h = int(root.get("height"))
    print(f"  地图大小: {map_w}×{map_h} tiles ({map_w * TILE_W}×{map_h * TILE_H} px)")

    tilesets = load_tilesets(root)

    # 创建画布（黑色背景）
    canvas = Image.new("RGBA", (map_w * TILE_W, map_h * TILE_H), (0, 0, 0, 255))

    # 按顺序渲染各层: Back -> Buildings -> Front -> Front2 (如果有)
    render_layers = ["Back", "Buildings", "Front", "Front2"]
    for layer_name in render_layers:
        layer = None
        for l in root.findall("layer"):
            if l.get("name") == layer_name:
                layer = l
                break
        if layer is None:
            continue

        print(f"  渲染层: {layer_name}")
        data = parse_layer_data(layer)
        for y, row in enumerate(data):
            for x, gid in enumerate(row):
                tile_img = get_tile_image(gid, tilesets)
                if tile_img is not None:
                    canvas.paste(tile_img, (x * TILE_W, y * TILE_H), tile_img)

    # 放大 3 倍方便查看
    scale = 3
    canvas_scaled = canvas.resize(
        (map_w * TILE_W * scale, map_h * TILE_H * scale),
        Image.NEAREST
    )
    canvas_scaled.save(output_path)
    print(f"  已保存: {output_path} ({canvas_scaled.size[0]}×{canvas_scaled.size[1]} px)")


def render_area_comparison(ruins_path, refurbished_path, output_path):
    """并排渲染废墟和修复版，方便对比"""
    print("生成对比图...")
    ruins_img = Image.open(ruins_path)
    refurb_img = Image.open(refurbished_path)

    w = ruins_img.width
    h = ruins_img.height
    gap = 20

    comparison = Image.new("RGBA", (w * 2 + gap, h), (40, 40, 40, 255))
    comparison.paste(ruins_img, (0, 0))
    comparison.paste(refurb_img, (w + gap, 0))
    comparison.save(output_path)
    print(f"  已保存对比图: {output_path}")


def main():
    ruins_tmx = MAPS_DIR / "CommunityCenter_Ruins.tmx"
    refurb_tmx = MAPS_DIR / "CommunityCenter_Refurbished.tmx"

    ruins_png = OUTPUT_DIR / "CommunityCenter_Ruins.png"
    refurb_png = OUTPUT_DIR / "CommunityCenter_Refurbished.png"
    compare_png = OUTPUT_DIR / "CommunityCenter_Comparison.png"

    render_map(ruins_tmx, ruins_png)
    render_map(refurb_tmx, refurb_png)
    render_area_comparison(ruins_png, refurb_png, compare_png)

    print("\n完成！生成了以下文件:")
    print(f"  废墟版: {ruins_png}")
    print(f"  修复版: {refurb_png}")
    print(f"  并排对比: {compare_png}")


if __name__ == "__main__":
    main()
