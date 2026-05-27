# Trout Derby Implementation Plan

This plan is intentionally implementation-first only after confirmation. Current scope is source-ledger, parity gap analysis, and a minimal staged plan for StardewCraft / Starfield Pastoral.

## 1. Current Conclusion

Trout Derby should be implemented as a lightweight passive fishing festival, not as an active festival session. It should reuse the existing passive festival lifecycle introduced for Desert Festival and the existing fishing result flow, with a small Trout Derby service/handler for Golden Tags, booth exchange, sign/booth interactions, optional ambient presentation, and source-text routing.

The most important source correction is that vanilla `PassiveFestivals.json` has no map replacement for Trout Derby, but vanilla `Forest.cs` still applies local Forest map overrides for the sign/booth and spawns simple background contestants. In StardewCraft terms, this argues for a Trout Derby-specific local interaction/presentation service, not a new active festival framework and not a full Forest temporary map.

## 2. Original Fact Summary

See `TROUT_DERBY_SOURCE_LEDGER.md` for the detailed source ledger. The baseline facts for implementation are:

- Passive festival id: `TroutDerby`.
- Date: Summer 20-21.
- Open time: 6:10.
- Start message: `TroutDerby_NowOpen`, shown only on day 1.
- No active festival entry, no temporary map switch, no time freeze, no ready gate, no end cutscene, no forced warp.
- Generic passive map replacement is null, but Forest locally changes presentation:
  - Summer 17-19: derby sign.
  - Summer 20-21: derby booth, lights, and ambient fishing contestants.
- Calendar behavior is split: `ShowOnCalendar=false`, but `Billboard.cs` hardcodes Fishing Derby icons/names for Summer 20 and 21.
- Willy uses ordinary dialogue and a `TroutDerby` schedule key.
- Forest fishing has an `IS_PASSIVE_FESTIVAL_OPEN TroutDerby` Rainbow Trout spawn entry.
- Golden Tag source roll happens after a successful Rainbow Trout catch on Summer 20-21, not from fish pond, chance `0.33 * numberOfFishCaught`, max one tag per catch resolution.
- If treasure is caught, the tag is added to the treasure menu; otherwise it uses a special tag award animation/path.
- Booth exchange consumes one `TroutDerbyTag`; first turn-in gives Tent Kit; later turn-ins use a deterministic 10-item rotating pool keyed by per-player `GoldenTagsTurnedIn`.
- Rewards and tag counts are per-player; festival open state and Forest presentation are world state.

## 3. StardewCraft Current State

### Reusable Capabilities Already Present

| Area | Existing capability | Reuse decision |
| --- | --- | --- |
| Passive festival registry | `FestivalRegistry` already registers `TroutDerby` as passive Summer 20-21, start 610, no overlay, first-day-only message. | Keep and verify against source; no new registry style needed. |
| Passive lifecycle | `FestivalService` supports active passive festival ids, `isPassiveFestivalDay`, `isPassiveFestivalOpen`, `getDayOfPassiveFestival`, first-day-only start messages, and no-overlay passive sessions. | Reuse directly. |
| Passive handler layer | `PassiveFestivalHandler` has `onNewDay`, `onOpen`, `tick`, `onCleanup`; `PassiveFestivalHandlers` dispatches registered handlers. | Add a `TroutDerbyHandler`, rather than putting derby logic into `FestivalService`. |
| Fishing selection | Forest fishing data already contains a `stardewcraft:rainbow_trout` rule conditioned on `IS_PASSIVE_FESTIVAL_OPEN TroutDerby`. | Keep; verify behavior during testing. |
| Fishing catch settlement | `FishingSessionManager.handleResult` centralizes successful catch delivery, multi-catch count, treasure handling, XP, quest event, and item popup. | Add a small hook/service call here when confirmed. |
| Quest/fish caught events | `StardewQuestEvents.fireFishCaught` and `PlayerStardewDataAPI.addFishCatchCount` already receive fish id/count. | Do not overload quest events for Golden Tags; use a dedicated derby service because source logic belongs to fishing rod settlement. |
| Prize Ticket item/UI | Prize Ticket and prize machine already exist. | Not the Trout Derby exchange. Mentioned only as adjacent reward infrastructure. |
| Item pickup HUD | Existing `ItemPickupHudPacket` can show awarded items. | Reuse for Golden Tags and rewards if no custom UI is added. |
| Mail system | Readable mail and mail flags exist. | Source found no Trout Derby mail; do not add mail unless later requested. |
| NPC runtime schedules | Passive festival schedule keys are considered in `NpcScheduleRuntimeService`; Willy dialogue keys already exist in lang. | Reuse for Willy schedule/dialogue once schedule data mapping is verified. |
| Debug festival support | `FestivalDebugCommand` can start/restore passive festivals generally. | Trout Derby handler should work under debug passive festival mode. |

### Current Gaps

| Gap | Source requirement | Suggested owner |
| --- | --- | --- |
| Golden Tag item | Vanilla has `TroutDerbyTag`; StardewCraft resource data has source object data, but no `ModItems` registration was found. | Add item/content when implementation is approved. |
| Golden Tag catch hook | Source rolls after catching Rainbow Trout on Summer 20-21, chance `0.33 * numberOfFishCaught`, max one tag. | `TroutDerbyService` called from `FishingSessionManager.handleResult`. |
| Treasure integration | Source adds tag to treasure menu if treasure was caught. | Needs careful integration with `generateAndGiveTreasure` / pending treasure. |
| Booth interaction | Source has `TroutDerbyBooth` with rewards/explanation/leave. | Trout-specific interaction handler, likely using portal trigger or confirmed interaction zone. |
| Reward counter | Source uses per-player `GoldenTagsTurnedIn`. | Add per-player field to `PlayerStardewData`, not world/team state. |
| Reward pool mapping | Some source rewards exist; Tent Kit, Bucket Hat, Mounted Trout painting, Triple Shot Espresso were not found in `ModItems`. | Decide whether to add missing items/assets or defer those rewards explicitly. |
| Sign interaction | Source shows sign Summer 17-19; no StardewCraft sign zone exists. | Needs user-confirmed coordinates and optional letter UI asset. |
| Forest presentation | Source applies local sign/booth map overrides and ambient contestants. | Needs confirmed coordinates/assets; can be phased after mechanics. |
| Ambient contestant NPCs | Source spawns 10 simple fishers and exclamation text. | Optional parity phase; requires confirmed StardewCraft points/models. |
| Calendar/billboard icon | Source hardcodes icons for Summer 20-21 despite `ShowOnCalendar=false`. | Check current calendar system; add if present. |
| Willy derby schedule | Source schedule limits shop to one hour, then Willy fishes in Forest. | Verify StardewCraft schedule data and current shop-open gating. |
| Object placement warning | Source warns/prevents tent-like placement near upcoming derby Forest area. | Probably later parity unless StardewCraft has placeable tents or conflicting objects. |

## 4. Minimal Source-Accurate First Version

The first implementation pass should avoid map/coordinate work unless you confirm the required points. A clean minimum can be:

1. Register a `TroutDerbyHandler` under `PassiveFestivalHandlers`.
2. Add/enable `TroutDerbyTag` as an item with source name/description and texture mapping.
3. Add per-player `GoldenTagsTurnedIn` state.
4. Hook successful Rainbow Trout catch settlement to award a Golden Tag with source probability.
5. If treasure was caught, include the tag with the treasure rewards; otherwise add it through inventory/HUD after fish delivery.
6. Add booth reward exchange only after you confirm `trout_derby_booth_zone`.
7. Use source booth text keys in lang; do not invent new explanatory flow.
8. Keep time passing, no active festival entry, no NPC freeze, no end warp.

This first version gives the real gameplay loop: catch Rainbow Trout during the passive festival, obtain Golden Tags, redeem them at the booth. It does not require full Forest map presentation on day one.

## 5. Recommended Phases

### Phase A: Mechanics Core

- Add Golden Tag content.
- Add per-player Golden Tags turned in counter.
- Add `TroutDerbyService` and `TroutDerbyHandler`.
- Hook catch settlement for Rainbow Trout and multi-catch chance scaling.
- Integrate tag with treasure/non-treasure result paths without duplicating fish rewards or breaking bait/tackle consumption.
- Add debug/test logging only if consistent with existing debug style.

Verification:

- `./gradlew classes`
- Manual debug passive festival open check.
- Catch Rainbow Trout with/without treasure and verify max one Golden Tag per catch resolution.

### Phase B: Booth Exchange

- Add confirmed booth interaction zone.
- Implement source choices: rewards, explanation, leave.
- Implement first reward Tent Kit if item exists or after adding it.
- Implement rotating reward pool with per-player deterministic index.
- Preserve source bag-full behavior: do not consume a tag when reward cannot fit and player has more than one tag.

Verification:

- `./gradlew classes`
- JSON/lang parse if lang keys are added.
- Manual exchange tests for no tags, bag full, first reward, later rewards.

### Phase C: Source Presentation

- Add Summer 17-19 sign only after confirming sign coordinates and visual/interaction approach.
- Add Summer 20-21 booth presentation only after confirming booth coordinates and cleanup bounds.
- Optionally add ambient contestant fishers after you provide each point and desired model/skin handling.
- Add source exclamation text behavior for ambient contestants if contestants exist.

Verification:

- `./gradlew classes`
- Manual date roll from Summer 16 to 22 to verify sign/booth spawn and cleanup.

### Phase D: Schedule, Calendar, And Polish

- Verify Willy's `TroutDerby` schedule data in StardewCraft and whether shop availability follows NPC presence/hours.
- Add or verify calendar/billboard Fishing Derby icon/name for Summer 20-21.
- Add letter-style sign UI with `troutDerbyLetterBG` if the project has the matching UI pipeline.
- Consider source placement warning only if StardewCraft has player-placeable objects in the affected public Forest area.

## 6. Reward Pool Mapping

| Source reward | Count | StardewCraft status found during planning | First-version recommendation |
| --- | ---: | --- | --- |
| `(O)TentKit` | 1 | Not found in `ModItems` search. | Add real item or block booth reward until added; do not substitute silently. |
| `(H)BucketHat` | 1 | Not found in `ModItems` search. | Add hat/item later or mark deferred. |
| `(O)710` Crab Pot | 1 | `CRAB_POT` exists. | Can map. |
| `(O)MysteryBox` | 3 | `MYSTERY_BOX` exists. | Can map. |
| `(O)72` Diamond | 1 | `DIAMOND` exists. | Can map. |
| `(F)MountedTrout_Painting` | 1 | Not found in `ModItems` search. | Add furniture later or mark deferred. |
| `(O)DeluxeBait` | 20 | `DELUXE_BAIT` exists. | Can map. |
| `(O)253` Triple Shot Espresso | 2 | Not found in `ModItems` search. | Add item later or mark deferred. |
| `(O)621` Quality Sprinkler | 1 | `QUALITY_SPRINKLER` exists. | Can map. |
| `(O)688` Warp Totem: Farm | 3 | `WARP_TOTEM_FARM` exists. | Can map. |
| `(O)749` Omni Geode | 3 | `OMNI_GEODE` exists. | Can map. |

Do not replace missing rewards with approximate items without explicit approval. If implementation starts before missing rewards are available, the exchange service should either skip unavailable rewards in a documented way or block exchange with a clear development message; source parity favors adding the missing content.

## 7. Coordinate Confirmation Table

No new StardewCraft coordinates are assigned here. The following must come from you before any code writes point values, interaction bounds, NPC positions, display entities, or cleanup areas.

| Key | Required data | Why needed | Phase |
| --- | --- | --- | --- |
| `trout_derby_sign_zone_min` / `max` | BlockPos bounds | Open pre-event sign letter on Summer 17-19. | C |
| `trout_derby_sign_display` | Optional BlockPos / model placement | Visible sign presentation. | C |
| `trout_derby_booth_zone_min` / `max` | BlockPos bounds | Open rewards/explanation booth on Summer 20-21. | B |
| `trout_derby_booth_display` | Optional BlockPos / facing / model placement | Visible booth presentation. | C |
| `trout_derby_cleanup_bounds_min` / `max` | BlockPos bounds | Remove temporary triggers/displays safely. | C |
| `trout_derby_contestant_01..10` | Position, facing, optional model/skin | Source-style ambient fishers. | C |
| `trout_derby_light_points` | Optional positions and light style | Source-style booth lights. | C |
| `trout_derby_fishing_focus_area` | Optional bounds | If ambient contestant effects should be limited to a local area. | C |

## 8. Open Questions For Confirmation

- Should the first implementation add the missing source reward items now, or should booth rewards wait until Tent Kit / Bucket Hat / Mounted Trout / Triple Shot Espresso exist?
- For the Golden Tag without treasure, is a normal item pickup HUD acceptable for first version, or do you want a source-style special animation/UI before implementation starts?
- Should the booth be mechanically available in Phase B even if the visible booth model/overlay is deferred, provided you confirm an interaction zone?
- Should ambient contestants be part of the first visible pass, or should they wait until after mechanics and booth exchange are stable?
- Do you want StardewCraft to preserve the source behavior that tags are not cleaned up after the festival, allowing turn-in at a later year's booth?

## 9. Non-Goals For The First Pass

- Do not convert Trout Derby into an active festival.
- Do not freeze time or block normal schedules globally.
- Do not create a full Forest temporary map.
- Do not infer booth/sign/NPC coordinates from vanilla tiles, TMX, schematics, pregenerated areas, or visuals.
- Do not add fake substitute rewards for missing source items without approval.
- Do not add mail; source search found no Trout Derby mail entry.

## 10. Verification Plan Once Implementation Begins

- Run `./gradlew classes` on macOS, not the VS Code `gradlew.bat` task.
- If lang or JSON files change, parse them with a JSON parser after editing.
- Test normal non-derby Rainbow Trout catches to ensure no tags outside Summer 20-21.
- Test Summer 20 and 21 after 6:10, plus before 6:10 if possible, because source tag roll is date-based but the data spawn rule is passive-open-based.
- Test multi-catch bait behavior: chance scales with `numberOfFishCaught`, but only one Golden Tag can be awarded per catch resolution.
- Test treasure and non-treasure catch paths separately.
- Test multiplayer with two players to confirm per-player tags and per-player `GoldenTagsTurnedIn`.