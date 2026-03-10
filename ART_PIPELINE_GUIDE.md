# Art Pipeline Guide (StardewCraft)

This document defines a consistent art pipeline: naming, folders, sizes, and review checks.
It is optimized for pixel art and Minecraft resource packs while keeping gameplay unchanged.

## 1) File and Folder Conventions

Root:
- assets/stardewcraft/
  - blockstates/
  - models/
    - block/
    - item/
    - entity/
  - textures/
    - block/
    - item/
    - entity/
    - gui/
    - particle/
  - sounds.json
  - sounds/
  - shaders/ (if any)

Rules:
- File names: lower_snake_case, no spaces, no uppercase.
- One asset = one file. Avoid multi-purpose sprites.
- Derivatives must be explicit: _stage, _silver, _gold, _iridium, _overlay, _base.

## 2) Texture Sizes

Default:
- Block textures: 16x16
- Item textures: 16x16
- GUI icons: follow exact existing sizes (see HUD specs)
- Particles: 8x8 or 16x16 (match existing system)

Never resize at runtime to avoid blurring.

## 3) Pixel Art Rules

- Use hard edges, avoid semi-transparent pixels at boundaries.
- Palette: keep per-family palette consistent (crops, ores, machines, UI).
- Avoid gradients unless it is a 2-3 step banded gradient.

## 4) Models and Overrides

Items with quality:
- Base item model: models/item/<item>.json
- Quality variants: models/item/<item>_silver.json, _gold.json, _iridium.json
- Use custom_model_data overrides in base model.

Crops:
- Blockstates map stages to models
- models/block/crops/<season>/<crop>_stage0..n.json
- textures/block/crops/<season>/<crop>_stage0..n.png

Machines:
- Block model should only use block textures
- If using animated textures, place in textures/block/ and name *_anim

## 5) Sounds

- All sounds are registered in assets/stardewcraft/sounds.json
- IDs are lower_snake_case
- Subtitle key format: stardewcraft.subtitle.<sound_id>

## 6) Shaders and Special Effects

Create uses heavy render-tech (Flywheel, custom shaders, instancing). For our scope:
- Use shaders only when a clear effect is needed (glow, overlay, screen UI)
- Keep shader configs in assets/stardewcraft/shaders/
- Document any custom shader in a short note (purpose + inputs)

## 7) Review Checklist (Art)

- Naming: matches conventions
- Size: correct dimensions
- Pixel grid: no sub-pixel blurring
- Variants: quality overlays present where required
- Blockstate/model/texture references valid
- Sounds: sound event declared and file exists

## 8) Existing Specs

- HUD texture spec: STARDEW_HUD_TEXTURES.md
- Crop example: TEXTURE_GUIDE_PARSNIP.md
- Weather icons: WEATHER_ICONS_NEEDED.txt and WEATHER_ICONS_TODO.md

## 9) Near-Term Cleanup Targets

- Normalize crop texture paths and names
- Normalize ore/block texture families
- Fill missing weather icons
- Audit GUI icon sizes to ensure consistency
