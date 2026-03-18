# Decoration Asset Intake

Use this folder to hand off new decoration assets.

## What you provide

- A JSON file based on `decor_intake_template.json`.
- Source model files in any raw folder path (for now just fill `sourceFile`).
- For Blockbench JSON decoration models, use `models/decor/...` paths.
- Do not put decoration intake models under `assets/stardewcraft/geo/...`; `geo` is reserved for geo-entity style assets.
- Only these decisions are required from you:
  - `usageArea` (where used, e.g. `pierre_shop`)
  - `purpose` (what it is for)
  - `mountType` (`wall`, `floor`, `rug`, `ceiling`)

## Field guide

- `assetPackId`: Batch id for one delivery, e.g. `pierre_shop_v2`.
- `sourceFile`: Relative source location for the model.
- `usageArea`: Target scene/room domain.
- `purpose`: Human-readable purpose key.
- `mountType`:
  - `wall`: attached to wall
  - `floor`: placed on floor
  - `rug`: floor-aligned thin decoration
  - `ceiling`: attached to ceiling
- `sizeClass`:
  - `single`: usually <= 1 block footprint
  - `multi`: spans multiple blocks
- `collisionProfile`:
  - `none`: no collision
  - `thin`: thin collision (board/poster)
  - `solid`: full gameplay collision
- `interactive`: true if it needs interaction logic later.

## Naming policy (managed by code side)

You do not need to pre-name final registry ids.
Code side will map each entry to stable ids in this pattern:

`<usageArea>.<mountType>.<purpose>`

Example:

`pierre_shop.wall.price_board_small`

## Next step after intake

After receiving intake JSON, code side will:

1. Normalize naming and tags.
2. Assign mount constraints and default transform.
3. Generate runtime decoration definition files.
4. Bind optional portal/interaction data if needed.
