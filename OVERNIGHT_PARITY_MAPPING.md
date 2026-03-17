# Overnight UI Parity Mapping (Stardew -> NeoForge)

This file tracks one-to-one rendering/logic mapping for overnight settlement UI.

## Coordinate System
- Stardew base: pixel coords with common scale 4.0 in SpriteBatch.
- MC base: GUI-scaled coords.
- Mapping used in code: `px(v) = round(v / guiScale)`, `s4 = 4 / guiScale`.

## ShippingMenu.cs Mapping
- `centerY - 300 + i * 27 * 4`
  - Implemented: `centerY + px(-300 + i * 27 * 4)`
- `IClickableMenu.drawTextureBox(... Rectangle(384,373,18,18), drawShadow:false)`
  - Implemented: `drawTextureBoxNoShadow(...)` in `StardewGuiUtil`
- Category row slot frame `Rectangle(293,360,24,24), scale=4`
  - Implemented in `ShippingMenuScreen`
- Intro category pop timing `introTimer` 500ms sections
  - Implemented in `ShippingMenuScreen.render`
- Intro acceleration while left mouse held (`*3`)
  - Implemented in `ShippingMenuScreen.render`
- Outro fade `800ms` and close
  - Implemented with `outro/outroFadeTimer` in `ShippingMenuScreen`

## IClickableMenu.cs Mapping
- `drawTextureBox(Game1.menuTexture, sourceRect=0,256,60,60)`
  - Implemented in `StardewGuiUtil.drawTextureBox` via `textures/gui/animal_query/menu_tiles.png`
- `drawHorizontalPartition(... small=false)`
  - Implemented in `StardewGuiUtil.drawHorizontalPartition` via menu tile indices `4,6,7`
- `drawVerticalIntersectingPartition(...)`
  - Implemented in `StardewGuiUtil.drawVerticalIntersectingPartition` via menu tile indices `59,63,62`

## Atlas Validation Artifacts
- Exported source-rect crops for manual pixel verification:
  - `ui_spec/overnight_atlas_crops/menu_box_0_256_60_60.png`
  - `ui_spec/overnight_atlas_crops/menu_box_noshadow_0_64_64_64.png`
  - `ui_spec/overnight_atlas_crops/mouse_plus_392_361_10_11.png`
  - `ui_spec/overnight_atlas_crops/mouse_slot_293_360_24_24.png`
  - `ui_spec/overnight_atlas_crops/mouse_ok_128_256_64_64.png`
  - `ui_spec/overnight_atlas_crops/mouse_back_352_495_12_11.png`
  - `ui_spec/overnight_atlas_crops/mouse_next_365_495_12_11.png`

## LevelUpMenu.cs Mapping
- Header ribbon `mouseCursors (363,87,58,22) scale=4`
  - Implemented in `LevelUpMenuScreen.render`
- Title icon positions based on `spaceToClearSideBorder` + `borderWidth`
  - Implemented via `SPACE_SIDE` and `BORDER_WIDTH`
- Profession split partitions
  - Replaced temporary fill with partition helpers from `StardewGuiUtil`
- OK button position `x + width + 4`, `y + height - 64 - borderWidth`
  - Implemented and GUI-scale converted

## MoneyDial.cs Mapping
- Digit sprite `(286, 502 - digit*8, 5, 8)`
  - Implemented in `MoneyDial.draw`
- Digit step `24`
  - Implemented as `round(24 / guiScale)`
- Shine scale bonus `+0.3`
  - Implemented as `+0.3 / guiScale`
- Million wobble in ShippingMenu
  - Implemented using sin-time offset

## Audio Mapping (Overnight)
- `bigSelect`, `bigDeSelect`, `shwip`, `money`, `moneyDial`, `stoneStep`, `thudStep`, `shadowDie`
  - Sound events registered in `ModSounds`
  - `sounds.json` entries bound under `assets/stardewcraft/sounds/`
