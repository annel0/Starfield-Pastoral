#!/usr/bin/env python3
"""
Extract ALL art assets needed for the GeodeMenu screen from SDV source spritesheets.

SDV GeodeMenu.cs asset map (逐行对照源码):
═══════════════════════════════════════════════════════════════

1) BACKGROUND — Cursors.png at (0, 512, 140, 78) scaled 4×
   = Anvil table scene with geode slot

2) CLINT ANIMATION — Characters/Clint.png
   Frame layout: 32×48 per frame, cols = textureWidth/32 (Clint.png: 64px wide = 2 cols)
   Hammering animation uses frames 8–12
   We extract frames 8,9,10,11,12 individually

3) GEODE DESTRUCTION ANIMATIONS — TileSheets/animations.png
   - Regular geode: (0, 448, 64, 64), 8 frames horizontal
   - Frozen geode:  (0, 512, 64, 64), 8 frames horizontal
   - Magma geode:   (0, 576, 64, 64), 8 frames horizontal
   Each strip is 64×64 × 8 frames = 512×64

4) ARTIFACT TROVE DESTRUCTION — LooseSprites/temporary_sprites_1.png
   (388, 123, 18, 21), 6 frames horizontal = 108×21

5) MYSTERY BOX DESTRUCTION — LooseSprites/Cursors_1_6.png
   - Regular:  (0, 27, 24, 24), 8 frames = 192×24
   - Golden: (256, 27, 24, 24), 8 frames = 192×24

6) SPARKLE EFFECT — TileSheets/animations.png
   (0, 640, 64, 64), 8 frames = 512×64

7) FLUFF PARTICLES:
   - Cursors.png at (372, 1956, 10, 10) — smoke/dust
   - temporary_sprites_1.png at (499, 132, 5, 5) — artifact trove shards
   - Cursors_1_6.png at (0, 52, 5, 5), (5, 52, 5, 5), (10, 52, 5, 5) — mystery box particles
   - Cursors_1_6.png at (15, 52, 5, 5), (20, 52, 5, 5), (25, 52, 5, 5) — golden mystery box particles
"""

from PIL import Image
import os

SRC = "/Users/jiayuhan/游戏制作/StardewCraft/源文件/Content"
DST = "/Users/jiayuhan/游戏制作/StardewCraft/src/main/resources/assets/stardewcraft/textures/gui/geode"

def crop_save(src_path, x, y, w, h, out_name, desc=""):
    img = Image.open(src_path)
    sprite = img.crop((x, y, x + w, y + h))
    out = os.path.join(DST, out_name)
    sprite.save(out)
    print(f"  ✓ {out_name} ({w}×{h}) — {desc}")
    return sprite

def extract_strip(src_path, x, y, fw, fh, frames, out_name, desc=""):
    """Extract a horizontal animation strip."""
    img = Image.open(src_path)
    strip = img.crop((x, y, x + fw * frames, y + fh))
    out = os.path.join(DST, out_name)
    strip.save(out)
    print(f"  ✓ {out_name} ({fw * frames}×{fh}, {frames} frames) — {desc}")

def extract_clint_frames(src_path):
    """Extract Clint hammering animation frames 8-12 from character sheet.
    SDV AnimatedSprite: cols = textureWidth / spriteWidth.
    Clint.png is 64×352 → 2 columns of 32px frames.
    Frame N: col = N % COLS, row = N // COLS
    """
    img = Image.open(src_path)
    FW, FH = 32, 48
    COLS = img.size[0] // FW  # Auto-detect columns from image width
    for frame in [8, 9, 10, 11, 12]:
        col = frame % COLS
        row = frame // COLS
        x = col * FW
        y = row * FH
        sprite = img.crop((x, y, x + FW, y + FH))
        out_name = f"clint_frame_{frame}.png"
        out = os.path.join(DST, out_name)
        sprite.save(out)
        print(f"  ✓ {out_name} ({FW}×{FH}) — Clint frame {frame}")

    # Also save the full strip (frames 8-12) as a single spritesheet for easier rendering
    strip = Image.new("RGBA", (FW * 5, FH))
    for i, frame in enumerate([8, 9, 10, 11, 12]):
        col = frame % COLS
        row = frame // COLS
        x = col * FW
        y = row * FH
        sprite = img.crop((x, y, x + FW, y + FH))
        strip.paste(sprite, (i * FW, 0))
    out = os.path.join(DST, "clint_hammering.png")
    strip.save(out)
    print(f"  ✓ clint_hammering.png ({FW * 5}×{FH}, 5 frames) — Clint animation strip")


print("=" * 60)
print("Extracting GeodeMenu assets from SDV source spritesheets")
print("=" * 60)

# ── 1) Background ──
print("\n[1] Background — Cursors.png (0, 512, 140, 78)")
crop_save(f"{SRC}/LooseSprites/Cursors.png", 0, 512, 140, 78,
          "geode_bg.png", "Anvil/table background")

# ── 2) Clint animation ──
print("\n[2] Clint hammering — Characters/Clint.png frames 8-12")
extract_clint_frames(f"{SRC}/Characters/Clint.png")

# ── 3) Geode destruction animations ──
print("\n[3] Geode destruction — TileSheets/animations.png")
extract_strip(f"{SRC}/TileSheets/animations.png", 0, 448, 64, 64, 8,
              "geode_break_regular.png", "Regular geode break")
extract_strip(f"{SRC}/TileSheets/animations.png", 0, 512, 64, 64, 8,
              "geode_break_frozen.png", "Frozen geode break")
extract_strip(f"{SRC}/TileSheets/animations.png", 0, 576, 64, 64, 8,
              "geode_break_magma.png", "Magma geode break")

# ── 4) Artifact trove destruction ──
print("\n[4] Artifact trove — temporary_sprites_1.png (388, 123, 18×21, 6 frames)")
extract_strip(f"{SRC}/LooseSprites/temporary_sprites_1.png", 388, 123, 18, 21, 6,
              "artifact_trove_break.png", "Artifact trove break")

# ── 5) Mystery box destruction ──
print("\n[5] Mystery box — Cursors_1_6.png")
extract_strip(f"{SRC}/LooseSprites/Cursors_1_6.png", 0, 27, 24, 24, 8,
              "mystery_box_break.png", "Mystery box break")
extract_strip(f"{SRC}/LooseSprites/Cursors_1_6.png", 256, 27, 24, 24, 8,
              "golden_mystery_box_break.png", "Golden mystery box break")

# ── 6) Sparkle effect ──
print("\n[6] Sparkle — TileSheets/animations.png (0, 640, 64×64, 8 frames)")
extract_strip(f"{SRC}/TileSheets/animations.png", 0, 640, 64, 64, 8,
              "sparkle.png", "Sparkle effect")

# ── 7) Fluff particles ──
print("\n[7] Fluff particles")
crop_save(f"{SRC}/LooseSprites/Cursors.png", 372, 1956, 10, 10,
          "fluff_smoke.png", "Smoke/dust particle")
crop_save(f"{SRC}/LooseSprites/temporary_sprites_1.png", 499, 132, 5, 5,
          "fluff_artifact_shard.png", "Artifact trove shard")
# Mystery box particles (3 colors)
for i in range(3):
    crop_save(f"{SRC}/LooseSprites/Cursors_1_6.png", i * 5, 52, 5, 5,
              f"fluff_mystery_{i}.png", f"Mystery box particle color {i}")
# Golden mystery box particles (3 colors)
for i in range(3):
    crop_save(f"{SRC}/LooseSprites/Cursors_1_6.png", 15 + i * 5, 52, 5, 5,
              f"fluff_golden_mystery_{i}.png", f"Golden mystery box particle color {i}")

print("\n" + "=" * 60)
print("Done! All assets saved to:")
print(f"  {DST}")
print("=" * 60)
