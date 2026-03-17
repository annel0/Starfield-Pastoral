# Overnight Atlas Reference (for 1:1 Parity Checks)

This file lists the extracted atlas crops used to verify source-rect mapping against Stardew source code.

## Exported crops

- [ui_spec/overnight_atlas_crops/menu_box_0_256_60_60.png](ui_spec/overnight_atlas_crops/menu_box_0_256_60_60.png)
- [ui_spec/overnight_atlas_crops/menu_box_noshadow_0_64_64_64.png](ui_spec/overnight_atlas_crops/menu_box_noshadow_0_64_64_64.png)
- [ui_spec/overnight_atlas_crops/menu_tiles_preview_0_320_256_256.png](ui_spec/overnight_atlas_crops/menu_tiles_preview_0_320_256_256.png)
- [ui_spec/overnight_atlas_crops/mouse_slot_293_360_24_24.png](ui_spec/overnight_atlas_crops/mouse_slot_293_360_24_24.png)
- [ui_spec/overnight_atlas_crops/mouse_plus_392_361_10_11.png](ui_spec/overnight_atlas_crops/mouse_plus_392_361_10_11.png)
- [ui_spec/overnight_atlas_crops/mouse_back_352_495_12_11.png](ui_spec/overnight_atlas_crops/mouse_back_352_495_12_11.png)
- [ui_spec/overnight_atlas_crops/mouse_next_365_495_12_11.png](ui_spec/overnight_atlas_crops/mouse_next_365_495_12_11.png)
- [ui_spec/overnight_atlas_crops/mouse_ok_128_256_64_64.png](ui_spec/overnight_atlas_crops/mouse_ok_128_256_64_64.png)

## Source mapping notes

- Stardew `IClickableMenu.drawTextureBox(...)` default source rect is `(0,256,60,60)` on `Game1.menuTexture`.
- Stardew `drawHorizontalPartition(...)` and `drawVerticalIntersectingPartition(...)` read tiles from `Game1.menuTexture` tile indices `4/6/7` and `59/63/62`.
- Shipping summary row box (`drawShadow:false`) uses `Game1.mouseCursors` source rect `(384,373,18,18)`.
- Shipping row controls and icons use `Game1.mouseCursors` rects such as `(392,361,10,11)`, `(293,360,24,24)`, `(352,495,12,11)`, `(365,495,12,11)`, `(128,256,64,64)`.

## Atlas identity checks

- `cursors.png` and `mouse_cursors.png` are NOT identical (different SHA-256), so overnight UI is bound to `mouse_cursors.png`.
- `cursors2.png` and `mouse_cursors2.png` are identical in this repository.
