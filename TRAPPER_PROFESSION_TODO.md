# TRAPPER Profession TODO

Status: crafting/manufacturing system has not started yet, so this file intentionally records TODO only and does not implement runtime behavior.

## Current blocker
- `ProfessionType.TRAPPER` exists, but there is no active crab pot crafting pipeline to hook.
- No standard datapack recipe path is present for crab pot (`data/*/recipes/*.json`).
- No custom recipe loader currently consumes `src/main/resources/data/stardewcraft/recipe/*` for utility crafting.

## Vanilla parity target
- Trapper should change crab pot crafting cost from:
  - `40x wood + 3x iron bar`
- to:
  - `25x wood + 2x copper bar`

## Recommended implementation order
1. Add a real crab pot recipe source (choose one):
   - Standard datapack recipe under `data/stardewcraft/recipes/crab_pot.json`.
   - Or a custom utility recipe system that is actually consumed at runtime.
2. Apply profession-aware cost swap:
   - At recipe selection/build stage (preferred): emit recipe variant by profession.
   - Or at craft submit stage: validate profession and consume alternate ingredients.
3. Keep mariner/luremaster behavior in `CrabPotBlockEntity` unchanged.

## Why not ItemCraftedEvent refund
- Post-craft refund is fragile and can desync with shift-craft/batch craft behavior.
- It also cannot cleanly express iron->copper replacement semantics.

## Validation checklist
- Non-trapper can craft crab pot only with base cost.
- Trapper can craft crab pot only with discounted cost.
- Shift-craft consumes exact expected ingredients.
- JEI/recipe UI displays the correct cost path for each case.
- Server authoritative validation rejects forged client requests.
