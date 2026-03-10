from __future__ import annotations

from pathlib import Path
import random
from PIL import Image # pyright: ignore[reportMissingImports]

ROOT = Path(r"C:/Users/23169/Desktop/stardewcraft-template-1.21.1/src/main/resources/assets/stardewcraft")
TEX_DIR = ROOT / "textures" / "block" / "utility"

TARGETS = {
    "keg": "keg_particle",
    "cask": "cask_particle",
    "cheese_press": "cheese_press_particle",
    "crab_pot": "crab_pot_particle",
    "bee_house": "bee_house_particle",
    "bee_house_full": "bee_house_full_particle",
    "tapper": "tapper_particle",
    "loom": "loom_particle",
    "loom_full": "loom_full_particle",
    "preserves_jar": "preserves_jar_particle",
    "dehydrator": "dehydrator_particle",
    "sprinkler":"sprinkler_particle",
    "quality_sprinkler": "quality_sprinkler_particle",
    "iridium_sprinkler": "iridium_sprinkler_particle",
    "fish_smoker_empty": "fish_smoker_empty_particle",
    "fish_smoker_working": "fish_smoker_working_particle",
    "crystalarium": "crystalarium_particle",
    "seed_maker": "seed_maker_particle",
    "mayonnaise_machine": "mayonnaise_machine_particle",
    "oil_maker": "oil_maker_particle",
    "worm_bin": "worm_bin_particle",
    "deluxe_worm_bin": "deluxe_worm_bin_particle",
    "furnace": "furnace_particle",
    "furnace_working": "furnace_working_particle",
    "lightning_rod": "lightning_rod_particle",
    "solar_panel": "solar_panel_particle"

}

random.seed(1337)


def average_rgb(img: Image.Image) -> tuple[int, int, int]:
    data = img.getdata()
    total_r = 0
    total_g = 0
    total_b = 0
    count = 0
    for r, g, b, a in data:
        if a >= 16:
            total_r += r
            total_g += g
            total_b += b
            count += 1
    if count == 0:
        return (127, 127, 127)
    return (total_r // count, total_g // count, total_b // count)


def generate_noise_tile(base_rgb: tuple[int, int, int]) -> Image.Image:
    out = Image.new("RGBA", (16, 16))
    px = out.load()
    for y in range(16):
        for x in range(16):
            n = random.randint(-12, 12)
            r = max(0, min(255, base_rgb[0] + n))
            g = max(0, min(255, base_rgb[1] + n))
            b = max(0, min(255, base_rgb[2] + n))
            px[x, y] = (r, g, b, 255)
    return out


def main() -> None:
    TEX_DIR.mkdir(parents=True, exist_ok=True)
    for base_name, particle_name in TARGETS.items():
        src = TEX_DIR / f"{base_name}.png"
        if not src.exists():
            print(f"SKIP (missing): {src}")
            continue
        img = Image.open(src).convert("RGBA")
        avg = average_rgb(img)
        out = generate_noise_tile(avg)
        dst = TEX_DIR / f"{particle_name}.png"
        out.save(dst)
        print(f"Wrote: {dst}")


if __name__ == "__main__":
    main()
