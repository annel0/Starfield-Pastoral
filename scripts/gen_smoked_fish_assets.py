from __future__ import annotations

import json
from pathlib import Path
from typing import Iterable, Tuple

from PIL import Image, ImageChops  # pyright: ignore[reportMissingImports]

ROOT = Path(r"C:/Users/23169/Desktop/stardewcraft-template-1.21.1")
MODEL_DIR = ROOT / "src" / "main" / "resources" / "assets" / "stardewcraft" / "models" / "item"
TEXTURE_ROOT = ROOT / "src" / "main" / "resources" / "assets" / "stardewcraft" / "textures" / "item"
SMOKED_TEXTURE_DIR = TEXTURE_ROOT / "fish" / "smoked"
PUFF_OUT = TEXTURE_ROOT / "artisan" / "smoke_puff.png"

CURSORS = ROOT / "源文件" / "Content" / "LooseSprites" / "Cursors.png"
PUFF_RECT = (372, 1956, 10, 10)

FISH_TEXTURE_PREFIX = "stardewcraft:item/fish/"
EXCLUDE_SEGMENTS = ("/misc/", "/trash/")



def iter_fish_models() -> Iterable[Tuple[str, str]]:
    for path in MODEL_DIR.glob("*.json"):
        name = path.stem
        if name.startswith("smoked_"):
            continue
        if name.endswith("_silver") or name.endswith("_gold") or name.endswith("_iridium"):
            continue
        data = json.loads(path.read_text(encoding="utf-8"))
        textures = data.get("textures") or {}
        layer0 = textures.get("layer0")
        if not layer0 or not layer0.startswith(FISH_TEXTURE_PREFIX):
            continue
        if any(seg in layer0 for seg in EXCLUDE_SEGMENTS):
            continue
        yield name, layer0


def texture_path_from_layer(layer0: str) -> Path:
    rel = layer0.replace("stardewcraft:item/", "")
    return TEXTURE_ROOT / f"{rel}.png"


def load_puff() -> Image.Image:
    if not CURSORS.exists():
        raise FileNotFoundError(f"Missing Cursors.png at {CURSORS}")
    sheet = Image.open(CURSORS).convert("RGBA")
    x, y, w, h = PUFF_RECT
    return sheet.crop((x, y, x + w, y + h))


def tint_layer(base: Image.Image, color: tuple[int, int, int], alpha: float) -> Image.Image:
    color_layer = Image.new("RGBA", base.size, color + (255,))
    tinted = ImageChops.multiply(base, color_layer)
    alpha_layer = tinted.getchannel("A").point(lambda v: int(v * alpha))
    tinted.putalpha(alpha_layer)
    return tinted


def apply_smoke(base: Image.Image) -> Image.Image:
    result = base.copy()

    dark_overlay = tint_layer(base, (80, 30, 10), 0.6)
    result = Image.alpha_composite(result, dark_overlay)
    return result


def write_models(item_name: str) -> None:
    be_model = {
        "parent": "minecraft:builtin/entity",
        "textures": {
            "particle": f"stardewcraft:item/fish/smoked/{item_name}"
        },
        "display": {
            "thirdperson_righthand": {
                "rotation": [0, 0, 0],
                "translation": [0, 3, 1],
                "scale": [0.55, 0.55, 0.55]
            },
            "thirdperson_lefthand": {
                "rotation": [0, 0, 0],
                "translation": [0, 3, 1],
                "scale": [0.55, 0.55, 0.55]
            },
            "firstperson_righthand": {
                "rotation": [0, -90, 25],
                "translation": [1.13, 3.2, 1.13],
                "scale": [0.68, 0.68, 0.68]
            },
            "firstperson_lefthand": {
                "rotation": [0, -90, 25],
                "translation": [1.13, 3.2, 1.13],
                "scale": [0.68, 0.68, 0.68]
            },
            "ground": {
                "rotation": [0, 0, 0],
                "translation": [0, 2, 0],
                "scale": [0.5, 0.5, 0.5]
            }
        }
    }

    base_model = {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"stardewcraft:item/fish/smoked/{item_name}"
        },
        "overrides": [
            {"predicate": {"custom_model_data": 1}, "model": f"stardewcraft:item/smoked_{item_name}_base_silver"},
            {"predicate": {"custom_model_data": 2}, "model": f"stardewcraft:item/smoked_{item_name}_base_gold"},
            {"predicate": {"custom_model_data": 3}, "model": f"stardewcraft:item/smoked_{item_name}_base_iridium"}
        ]
    }

    base_silver = {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"stardewcraft:item/fish/smoked/{item_name}",
            "layer1": "stardewcraft:item/quality/silver_star"
        }
    }

    base_gold = {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"stardewcraft:item/fish/smoked/{item_name}",
            "layer1": "stardewcraft:item/quality/gold_star"
        }
    }

    base_iridium = {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"stardewcraft:item/fish/smoked/{item_name}",
            "layer1": "stardewcraft:item/quality/iridium_star"
        }
    }

    (MODEL_DIR / f"smoked_{item_name}.json").write_text(
        json.dumps(be_model, ensure_ascii=True, indent=2) + "\n",
        encoding="utf-8",
    )
    (MODEL_DIR / f"smoked_{item_name}_base.json").write_text(
        json.dumps(base_model, ensure_ascii=True, indent=2) + "\n",
        encoding="utf-8",
    )
    (MODEL_DIR / f"smoked_{item_name}_base_silver.json").write_text(
        json.dumps(base_silver, ensure_ascii=True, indent=2) + "\n",
        encoding="utf-8",
    )
    (MODEL_DIR / f"smoked_{item_name}_base_gold.json").write_text(
        json.dumps(base_gold, ensure_ascii=True, indent=2) + "\n",
        encoding="utf-8",
    )
    (MODEL_DIR / f"smoked_{item_name}_base_iridium.json").write_text(
        json.dumps(base_iridium, ensure_ascii=True, indent=2) + "\n",
        encoding="utf-8",
    )

    


def main() -> None:
    SMOKED_TEXTURE_DIR.mkdir(parents=True, exist_ok=True)
    PUFF_OUT.parent.mkdir(parents=True, exist_ok=True)
    puff = load_puff()
    puff.save(PUFF_OUT)

    for item_name, layer0 in iter_fish_models():
        src_path = texture_path_from_layer(layer0)
        if not src_path.exists():
            print(f"Missing base texture: {src_path}")
            continue
        base = Image.open(src_path).convert("RGBA")
        smoked = apply_smoke(base)
        out_path = SMOKED_TEXTURE_DIR / f"{item_name}.png"
        smoked.save(out_path)
        write_models(item_name)
        print(f"Wrote: {out_path}")


if __name__ == "__main__":
    main()
