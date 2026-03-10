# Optimization Plan (StardewCraft)

This document lists concrete optimization tasks and an execution order. Each step is small and verifiable.

## Goals
- Reduce code duplication in registrations and render setup.
- Make artisan machine logic data-driven to improve extensibility.
- Reduce server tick overhead for long-running production blocks.
- Improve maintainability without changing gameplay behavior.

## Scope (current focus)
- Java code under src/main/java/com/stardew/craft
- Data/recipes/tags under src/main/resources (new files to be added)

## Phase 0: Baseline & Safety (no behavior change)
1) Create a light test checklist
   - Load dev world, place Keg/Preserves Jar/Tapper, verify production and harvest.
   - Verify cutout render for crops and saplings.
   - Verify creative tab shows all expected items.
2) Add a small dev note (optional)
   - Document how to validate a production cycle in a short time (e.g., debug advance days).

## Phase 1: Registration De-dup (low risk)
Goal: reduce repeated registration code while keeping the same IDs.

1) Create helper methods for blocks
   - Add helper functions in ModBlocks for slab/stairs/wall variants.
   - Keep each registry name unchanged.
2) Create helper methods for block items
   - Add helper functions in ModItems to register BlockItem with shared properties.
3) (Optional) Group stone family definitions
   - Use an enum or small data class to define base block + strength + map color.

Verification:
- All blocks/items still load with the same registry IDs.
- No missing textures or broken block states.

## Phase 2: Client Render Layer Batch (low risk)
Goal: remove long lists of ItemBlockRenderTypes calls.

1) Add a ModRenderLayers helper
   - Provide registerCutout(Iterable<Block>) or registerCutout(Block...)
2) Create a list (or tag) of cutout blocks
   - Use a static list in code first.
   - Optionally move to a block tag later.

Verification:
- Crops, saplings, and transparent blocks render correctly.

## Phase 3: Unified Production Base (medium risk)
Goal: remove duplicated timer/state logic across artisan block entities.

1) Create a TimedProductionBlockEntity base class
   - Fields: input, product, readyAtAbsMinute, ready
   - Methods: computeReady, startWork, harvestOne, getRemainingTime
2) Refactor KegBlockEntity to extend base
3) Refactor PreservesJarBlockEntity to extend base
4) Refactor TapperBlockEntity to extend base (no input)

Verification:
- Existing behavior unchanged.
- NBT data still loads (migration if needed).

## Phase 4: Data-driven Artisan Recipes (medium/high risk)
Goal: move hard-coded rules to JSON for easier content expansion.

1) Define a JSON schema for artisan recipes
   - Type: stardewcraft:artisan
   - Fields: input (item or tag), output, time, consume, optional conditions
2) Create a data loader and cache
   - Load on server start / resource reload
   - Provide query API by item id
3) Migrate Keg rules to JSON
4) Migrate Preserves rules to JSON
5) Add tag-based rules where possible

Verification:
- Existing recipes still work.
- New data pack recipes can be added without code changes.

## Phase 5: Tick Optimization (optional after refactor)
Goal: reduce per-tick checks for long-running machines.

1) Only check readiness when needed
   - If no product and no active work, skip.
2) Check on time change rather than every tick
   - Use a 20-tick or 40-tick interval, or hook into time manager events if available.

Verification:
- Machines still finish at the expected time.

## Suggested Implementation Order (step-by-step)
1) Phase 1
2) Phase 2
3) Phase 3
4) Phase 4
5) Phase 5

## Notes
- Keep registry IDs stable in all refactors.
- Any NBT field renames require migration logic.
- Avoid gameplay changes until after Phase 4 is stable.
