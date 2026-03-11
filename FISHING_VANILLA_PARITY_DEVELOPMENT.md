# Fishing Vanilla Parity Development

## Goal
Make StardewCraft fishing behavior match Stardew Valley vanilla fishing behavior as closely as possible, using biome tags as the map-water abstraction layer.

Core principle: our only objective is maximum code-level restoration of Stardew Valley behavior; all implementations must be based on vanilla source logic, with no invented mechanics.

## Scope Lock
- Fishing runs only in:
  - `stardewcraft:stardew_valley`
  - `stardewcraft:stardew_mining`
- Pool selection is biome-driven:
  - biome/tag -> vanilla location pool key -> fish rules
- User-approved exclusions (do not implement now):
  - `ancient_doll`
  - `fossilized_spine`
  - `pearl`
  - `qi_bean`
  - `snake_skull`
  - `void_mayonnaise`

## Vanilla Baseline (from `Content/Data/Locations.json`)
Locations with fish pools and fish-area usage:

- `Backwoods` (11)
- `Beach` (23)
- `BeachNightMarket` (2)
- `BoatTunnel` (1)
- `BugLand` (4)
- `Caldera` (9)
- `Default` (3)
- `Desert` (5), fish areas: `BottomPond`, `TopPond`
- `DesertFestival` (1)
- `Farm_Beach` (4), fish area: `Ocean`
- `Farm_Forest` (2)
- `Farm_FourCorners` (2)
- `Farm_Hilltop` (1)
- `Farm_MeadowlandsFarm` (1), fish area: `RiverAndLargePond`
- `Farm_Riverland` (2)
- `Farm_Wilderness` (1)
- `fishingGame` (7)
- `Forest` (23), fish areas: `Lake`, `River`
- `IslandFarmCave` (1)
- `IslandNorth` (7)
- `IslandSouth` (6)
- `IslandSouthEast` (6)
- `IslandSouthEastCave` (7)
- `IslandWest` (12), fish areas: `Freshwater`, `Ocean`
- `Mountain` (14)
- `Railroad` (1)
- `Sewer` (5)
- `Submarine` (9)
- `Temp` (11)
- `Town` (18), fish area: `Fountain`
- `UndergroundMine` (4)
- `WitchSwamp` (5)
- `Woods` (5)

## Current Biome -> Vanilla Location Mapping (implemented)
Source: `src/main/java/com/stardew/craft/fishing/data/FishingDataManager.java`

- `#stardewcraft:is_beach` or `#stardewcraft:is_ocean` -> `Beach`
- `#stardewcraft:is_town_river` or `#stardewcraft:is_jojamart_bridge` -> `Town`
- `#stardewcraft:is_mountain_lake` -> `Mountain`
- `#stardewcraft:is_forest_pond` or `#stardewcraft:is_forest_river` or `#stardewcraft:is_forest_waterfall` -> `Forest`
- `#stardewcraft:is_secret_woods` -> `Woods`
- `#stardewcraft:is_sewers` -> `Sewer`
- `#stardewcraft:is_desert` -> `Desert`
- `#stardewcraft:is_mines_20` or `#stardewcraft:is_mines_60` or `#stardewcraft:is_mines_100` -> `UndergroundMine`
- `#stardewcraft:is_volcano` -> `Caldera`
- `#stardewcraft:is_witch_swamp` -> `WitchSwamp`
- `#stardewcraft:is_mutant_bug_lair` -> `BugLand`
- `#stardewcraft:is_night_market` -> `BeachNightMarket`, `Submarine`
- `#stardewcraft:is_pirate_cove` -> `IslandSouthEastCave`
- `#stardewcraft:is_ginger_island_ocean` -> `IslandSouth`, `IslandSouthEast`, `IslandWest`
- `#stardewcraft:is_ginger_island_river` -> `IslandNorth`, `IslandWest`
- `#stardewcraft:is_ginger_island_pond` -> `IslandWest`

## Current Biome -> FishArea Mapping (implemented)
Source: `resolveVanillaFishAreaId(...)`

- beach/ocean tags -> `Ocean`
- town river / river tags -> `River`
- mountain lake / forest pond tags -> `Lake`
- island pond/river tags -> `Freshwater`
- mutant bug lair tag -> `Marsh`
- desert tag -> `TopPond` (temporary)

## Gaps To Reach Full Vanilla Behavior
- Data shape still not fully vanilla-native:
  - Most rules are still read from unified `stardewcraft:stardew_valley` pool.
  - Need per-location pool files aligned to vanilla location keys.
- Missing location pools in mapping layer:
  - `Backwoods`, `Railroad`, `BoatTunnel`, `Farm_*` variants, `DesertFestival`, `fishingGame`, `Temp`, `IslandFarmCave`.
- FishArea handling is incomplete:
  - `BottomPond`, `Fountain`, `RiverAndLargePond`, farm-specific areas not fully mapped.
- Rule semantics not fully implemented yet:
  - `CatchLimit`
  - `CanBeInherited`
  - full targeted bait retry behavior
  - complete requirement chain equivalent to vanilla `CheckGenericFishRequirements`
- Economy and rewards still need full parity:
  - XP formula
  - treasure chance and treasure contents
- Minigame constants/edge branches still need strict parity pass.

## Step-by-Step Delivery Plan

### Step 1: Freeze Data Contract to Vanilla Fields
- Keep and use rule fields:
  - `fishAreaId`, `canBeInherited`, `requireMagicBait`, `catchLimit`
- Add any missing vanilla fields needed by selection logic.
- Acceptance:
  - All vanilla selection fields have a direct representation in rule schema.

### Step 2: Split Fishing Data by Vanilla Location Key
- Replace unified data dependency with per-location files:
  - `Default.json`, `Beach.json`, `Town.json`, `Forest.json`, `Mountain.json`, ...
- Keep one migration compatibility pass, then remove fallback.
- Acceptance:
  - Runtime loads all required vanilla location keys.
  - No hard dependency on `stardewcraft:stardew_valley` mega-pool.

### Step 3: Complete Region -> Biome Tag Coverage
- Add/adjust biome tags so every active map water belongs to exactly one intended vanilla location pool route.
- Add explicit handling for:
  - farm maps (`Farm_Beach`, `Farm_Forest`, `Farm_FourCorners`, `Farm_Hilltop`, `Farm_MeadowlandsFarm`, `Farm_Riverland`, `Farm_Wilderness`)
  - `Backwoods`, `Railroad`, `BoatTunnel` where relevant
- Acceptance:
  - Every fishable biome resolves to a deterministic location pool key set.

### Step 4: Complete FishAreaId Coverage
- Add fish-area resolution for all used area IDs:
  - `Ocean`, `River`, `Lake`, `Freshwater`, `Marsh`, `Fountain`, `BottomPond`, `TopPond`, `RiverAndLargePond`.
- Acceptance:
  - Area-specific fish from vanilla data can be selected in correct map water zones.

### Step 5: Finish Vanilla Rule Chain
- Implement full equivalent behavior in selection pipeline:
  - `catchLimit`
  - inheritance gating (`canBeInherited`)
  - targeted bait retries
  - full generic fish requirement checks
- Acceptance:
  - Rule-pass behavior matches vanilla expectations in sampled scenarios.

### Step 6: XP, Treasure, and Minigame Strict Parity
- XP formula parity.
- Treasure chance and treasure pool parity.
- Minigame constants and branch parity audit.
- Acceptance:
  - Side-by-side behavior checks match vanilla reference outcomes.

### Step 7: Validation Matrix and Sign-off
- Build parity test cases by location, season, weather, time, distance, bait/tackle, and fish area.
- Run deterministic seed tests where possible.
- Acceptance:
  - All required parity checks pass except explicit exclusions.

## Execution Notes
- Keep all parity changes in small, reviewable commits by step.
- Do not mix fishing parity edits with unrelated systems.
- Maintain a running delta log in this file after each completed step.

## Progress Log
- `2026-03-11`:
  - Dimension gate restricted to Stardew + Mining dimensions.
  - Biome-driven location mapping implemented.
  - `FishAreaId` field and filtering path introduced.
  - `RequireMagicBait` field and filtering path introduced.
  - Added hard parity principle line: always implement by vanilla code logic, never invent mechanics.
  - Added `FishingDataManager` rule eligibility extension hook for future special-order/legendary-family gating.
  - Added persisted per-player special-order rule state (`ActiveSpecialOrderRules`) in player save data.
  - Wired default fish rule hook to vanilla `LEGENDARY_FAMILY` behavior: original legendary fish only when inactive, legendary family fish only when active.
  - Added generic `condition` field support in fish rules and evaluation for `PLAYER_SPECIAL_ORDER_RULE_ACTIVE Current <RuleId>` (with `!` negation), moving special-order gating to vanilla-style data conditions.
  - Added full explicit location-to-biome-ID document for map authoring: `FISHING_VANILLA_LOCATION_TO_BIOME_ID_MAP.md`.
  - Applied `canBeInherited` gating on inherited candidate pools (`Default` and compatibility pool) during fish selection.
