# Barn Tier Conditions Draft

## Goal
Define Barn Tier 1/2/3 validation requirements by reusing coop manager logic, with:
- Slightly larger interior space than coop.
- Feed trough / auto-feed trough / hay hopper counts kept close to coop.
- No incubator requirement for any barn tier.

## Baseline (Current Coop Defaults)
- T1: feed 4, auto 0, hopper 1, incubator 0, min interior 216
- T2: feed 8, auto 0, hopper 1, incubator 1, min interior 288
- T3: feed 0, auto 12, hopper 1, incubator 1, min interior 360

## Proposed Barn Defaults
- T1: feed 4, auto 0, hopper 1, incubator 0, min interior 252
- T2: feed 8, auto 0, hopper 1, incubator 0, min interior 336
- T3: feed 0, auto 12, hopper 1, incubator 0, min interior 420

## Why These Numbers
- Facility counts stay aligned with coop progression, so players do not relearn utility placement.
- Interior thresholds are about 16-17% larger than coop at each tier:
  - 216 -> 252
  - 288 -> 336
  - 360 -> 420
- This matches the expectation that barn spaces should feel roomier for larger animals.

## Example Interior Sizes (for player guidance)
- T1: 7 x 6 x 6 = 252
- T2: 8 x 7 x 6 = 336
- T3: 10 x 7 x 6 = 420

## Validation Rule Parity With Coop
Use the same shell checks as coop manager:
- Enclosed shell required (configurable)
- Boundary door/gate required (configurable)
- Minimum door/gate count required (configurable)

## Config Key Suggestion (Barn)
Add a parallel config section `barnManager`:
- `BARN_SCAN_RANGE_XZ / UP / DOWN`
- `BARN_REQUIRE_ENCLOSED`
- `BARN_REQUIRE_DOOR`
- `BARN_MIN_DOOR_COUNT`
- `BARN_T1_FEED_TROUGH / AUTOFEED_TROUGH / HAY_HOPPER / INCUBATOR / MIN_INTERIOR_BLOCKS`
- `BARN_T2_*`
- `BARN_T3_*`

Recommended incubator defaults for barn:
- `BARN_T1_INCUBATOR = 0`
- `BARN_T2_INCUBATOR = 0`
- `BARN_T3_INCUBATOR = 0`

## Implementation Note
Barn can directly mirror `CoopManagerValidationService` by extracting shared building-scan logic into a common validator and swapping tier requirement source.
