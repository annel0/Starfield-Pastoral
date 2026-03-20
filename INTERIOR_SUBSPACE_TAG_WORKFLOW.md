# Interior Subspace Workflow (Same Dimension)

This workflow uses same-dimension remote coordinates for interiors.
Players right-click interaction entities with tags and get teleported.

## Fixed Subspace Region

Interior subspace is now a fixed managed region in Stardew dimension:

- center: `(10000, 10000)` (X,Z)
- size: `2048 x 2048`
- manager class: `InteriorSubspaceManager`

Inside this region:

- non-creative block breaking is blocked
- explosion block damage is blocked
- creative mode is unrestricted

## Tag Protocol

Supported entity tags (recommended on interaction entities):

- `sdv_portal_target:<id>`
- `sdv_portal_to:<x>,<y>,<z>`
- `sdv_portal_rot:<yaw>,<pitch>`
- `sdv_portal_mode:entrance|exit|none`

Resolution order:

1. If `sdv_portal_target:<id>` exists, coordinates come from `InteriorPortalRegistry`.
2. Otherwise, parser uses `sdv_portal_to` (+ optional `sdv_portal_rot`).

Mode behavior:

- `entrance` sets player interior flag `true`.
- `exit` sets player interior flag `false`.
- `none` does not change interior flag.

## Why NPC-Compatible

While any player is marked as "in interior", the system force-loads a chunk square
around the main Stardew area center `(150, 119)` with radius `6` chunks.

This keeps core main-area simulation alive while the player is in remote interior space.

## Structure Loading Policy

Structure set is fixed and coordinate-locked for easier management.

Auto load triggers:

1. player enters Stardew dimension (`PlayerChangedDimensionEvent`)
2. player first enters interior region (`first_enter_interior_region`)

Load logic is versioned in `InteriorSubspaceManager` (`LAYOUT_VERSION`).
When structure list or fixed coordinates change significantly, bump version to re-apply.

## Recommended Build Process

1. Build each room in a remote coordinate zone in Stardew dimension.
2. Place paired interaction entities for each door:
   - outside entity: mode `entrance`
   - inside entity: mode `exit`
3. Start with direct tags (`sdv_portal_to`) for fast iteration.
4. After room coordinates are finalized, migrate to `sdv_portal_target:<id>`
   and register all final coordinates in `InteriorPortalRegistry`.
5. Add room NBT files and fixed origins in `InteriorSubspaceManager.register(...)`.

## Entity Choice (Important)

Primary choice: `minecraft:interaction`.

- No model clutter, no extra collision body, purpose-built for click interaction.
- Better map editing ergonomics than armor stand markers.
- Current portal event listens to entity interaction and reads tags from the clicked entity,
  so interaction entities work directly.

Armor stand is only a temporary fallback for debugging, not the recommended production setup.

## Example Commands

Create entrance interaction (outside):

```
/summon minecraft:interaction 160 -13 130 {width:0.9f,height:2.0f,response:1b,Tags:["sdv_portal_to:9000,75,9000","sdv_portal_rot:180,0","sdv_portal_mode:entrance"]}
```

Create exit interaction (inside):

```
/summon minecraft:interaction 9000 75 9002 {width:0.9f,height:2.0f,response:1b,Tags:["sdv_portal_to:162,-13,132","sdv_portal_rot:0,0","sdv_portal_mode:exit"]}
```

Use registry id instead of raw coords:

```
/tag @e[type=minecraft:interaction,sort=nearest,limit=1] add sdv_portal_target:house_pierre_entrance
```
