# Art Pipeline Cleanup Checklist

This is a focused cleanup plan based on current assets and the new pipeline guide.
It lists high-impact issues first and keeps gameplay unchanged.

## 1) Snapshot (Current State)

- Total PNG files: 1026
- Block textures not 16x16: 175
- Item textures not 16x16: 510

These are not errors by themselves, but they indicate inconsistent resolution across families.

## 2) Documentation Conflicts (Must Resolve)

1) Weather icon size
- WEATHER_ICONS_TODO.md says 12x8
- WEATHER_ICONS_NEEDED.txt says 16x16
- Actual assets are 12x8

2) HUD textures
- STARDEW_HUD_TEXTURES.md says stardew_bars.png is 256x64
- Actual asset is 108x12
- stardew_hud_icons.png is specified but not present in textures/gui

3) Crop guide vs textures
- TEXTURE_GUIDE_PARSNIP.md assumes 16x16
- Many crop textures are 64x64

Action: pick a single standard for each category and update docs to match.

## 3) High-Priority Normalization Targets

A) Crops (block textures)
- Many crop stages are 64x64 under textures/block/crops
- Decide: keep 64x64 HD crop art or downscale to 16x16
- If keep HD, document it explicitly and keep all crop stages the same size

B) Items (item textures)
- Many item families use 48x48 (animal_product, artifact, etc.)
- Decide: keep 48x48 item art or normalize to 16x16
- If keep HD, enforce per-family consistency

C) GUI icons
Current gui sizes:
- Weather icons: 12x8
- energy_icon/health_icon/gold_icon: 16x16
- time_icon: 48x48
- stardew_bars: 108x12
- bar_content: 102x6

Action: document exact sizes for each GUI texture family and lock them.

## 4) Missing / Incomplete Assets

Weather icons present:
- sunny, rainy, stormy, snowy, windy_spring, windy_fall
Missing:
- windy.png (referenced in WEATHER_ICONS_TODO.md)

Action: either add windy.png or remove reference and update docs.

## 5) Proposed Resolution Policy (Pick One)

Option A: Classic pixel fidelity
- All blocks/items: 16x16
- GUI: only as needed (12x8 for weather, 16x16 icons, etc.)

Option B: HD items/crops
- Crops: 64x64
- Items: 48x48
- Blocks: 16x16
- GUI: as currently used

## 6) Next Concrete Steps

1) Decide resolution policy for crops/items
2) Align docs to the chosen standard
3) Create a per-family audit list (crops, animal_product, artifacts, ores)
4) Fix missing windy.png or remove it from docs
5) Add stardew_hud_icons.png or remove the spec line

## 7) Quick Audit Samples

Block texture mismatches (sample):
- textures/block/crops/fall/*_stage0..3.png => 64x64

Item texture mismatches (sample):
- textures/item/animal_product/* => 48x48
- textures/item/artifact/* => 48x48

GUI sizes detected:
- sunny/rainy/stormy/snowy/windy_spring/windy_fall: 12x8
- stardew_bars: 108x12
- bar_content: 102x6
- time_icon: 48x48

These samples indicate consistent HD families; the main issue is that docs and standards do not reflect reality.
