# Decor Asset Workflow (Artist -> Integration)

This project uses data-driven decor intake.

## Fast handoff

1. Duplicate `src/main/resources/data/stardewcraft/deco/intake/decor_intake_template.json`.
2. Fill one `entries[]` item per model.
3. Keep only required decisions:
   - `usageArea`
   - `purpose`
   - `mountType`
4. Save as `decor_intake_<pack>.json` in the same folder.

## Required decision checklist

- Where is it used? (`usageArea`)
- What is it for? (`purpose`)
- How is it attached? (`mountType`)

Everything else can be rough and will be normalized during integration.

## Collision guidance

- Posters, stickers, blackboards: `collisionProfile = none` or `thin`
- Rugs: `collisionProfile = none`
- Counters, cabinets, shelves that block movement: `collisionProfile = solid`

## Multi-block assets

Set `sizeClass = multi`.
Footprint and transform fine-tuning are handled in integration stage.
