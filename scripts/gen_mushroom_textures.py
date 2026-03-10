from __future__ import annotations

from pathlib import Path
from PIL import Image # pyright: ignore[reportMissingImports]

ROOT = Path(r"C:/Users/23169/Desktop/stardewcraft-template-1.21.1")
SRC = ROOT / "源文件" / "Content" / "Maps" / "springobjects.png"
OUT_DIR = ROOT / "src" / "main" / "resources" / "assets" / "stardewcraft" / "textures" / "item" / "forage"

# Object sprite indices from Content/Data/Objects.json
SPRITES = {
    "common_mushroom": 404,
    "red_mushroom": 420,
    "purple_mushroom": 422,
    "morel": 257,
    "chanterelle": 281,
    "magma_cap": 851,
}


def crop_sprite(img: Image.Image, index: int) -> Image.Image:
    cols = img.width // 16
    if cols <= 0:
        raise ValueError("Invalid spritesheet width")
    x = (index % cols) * 16
    y = (index // cols) * 16
    return img.crop((x, y, x + 16, y + 16))


def main() -> None:
    if not SRC.exists():
        print(f"Missing source sheet: {SRC}")
        print("Please add vanilla springobjects.png to that path, then re-run.")
        return

    OUT_DIR.mkdir(parents=True, exist_ok=True)
    sheet = Image.open(SRC).convert("RGBA")

    for name, index in SPRITES.items():
        tile = crop_sprite(sheet, index)
        dst = OUT_DIR / f"{name}.png"
        tile.save(dst)
        print(f"Wrote: {dst}")


if __name__ == "__main__":
    main()
