# Stardew <-> Minecraft GUI Rendering Mapping

This document defines the mapping method used for 1:1 UI replication.

## Core Differences

- Stardew Valley UI logic assumes pixel-space coordinates and `pixelZoom = 4`.
- Minecraft GUI rendering uses scaled GUI-space coordinates (`window pixels / guiScale`).
- Stardew atlases are sampled in source pixels, then scaled at draw time.
- Minecraft `GuiGraphics` often mixes integer blits and pose scaling.

## Mapping Rules

1. Coordinate Mapping
- Formula: `mcGui = round(sdvPx / guiScale)`.
- Implemented by `StardewRenderMapping.ui(...)`.

2. PixelZoom Mapping
- Formula: `sdv pixelZoom(4) => 4 / guiScale` in MC GUI space.
- Implemented by `StardewRenderMapping.s4()`.

3. Anchor Mapping
- Center anchor: `x = screenWidth/2 - width/2`.
- Bottom anchor: `y = screenHeight - height - ui(bottomMarginSdvPx)`.
- Implemented by `StardewRenderMapping.centerX(...)` and `bottomY(...)`.

4. Source Rect Mapping
- Use Stardew source rectangles exactly (same u/v/w/h in atlas space).
- Never reauthor border geometry when a source rect already exists.

5. State Mapping
- Keep Stardew state transitions in order (opening -> input-ready -> closing).
- Input gating is part of visual parity (e.g., safety timer, typing completion before options).

## Implementation

- Mapping utility: `src/main/java/com/stardew/craft/client/gui/common/StardewRenderMapping.java`
- Current consumer: `src/main/java/com/stardew/craft/client/gui/common/StardewConfirmDialogScreen.java`

## Validation Checklist

- Same anchor behavior at multiple GUI scales.
- Same opening/closing timing feel.
- Same option highlight geometry and offsets.
- Same icon source rects and animation cadence.
- Same input gates and answer dispatch timing.
