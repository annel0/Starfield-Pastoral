# Trout Derby Source Ledger

This ledger records source-of-truth facts for Stardew Valley's Trout Derby before any StardewCraft implementation work. It intentionally does not assign StardewCraft world coordinates.

## Source Files Checked

| Source | Relevant facts |
| --- | --- |
| `源文件/Content/Data/PassiveFestivals.json` | `TroutDerby` passive festival definition: Summer 20-21, start time 610, first-day-only start message, no map replacement, no daily setup, no cleanup method. |
| `源文件/Content/Data/Locations.json` | Forest fishing has a special Rainbow Trout spawn entry conditioned by `IS_PASSIVE_FESTIVAL_OPEN TroutDerby`, item `(O)138`, chance `1.0`, `IgnoreFishDataRequirements=true`. |
| `源文件/StardewValley.Locations/Forest.cs` | Summer 17-19 applies a derby sign map override; Summer 20-21 applies the derby booth map override, lights, and 10 simple non-villager fishing contestants; cleanup removes the override and contestants. |
| `源文件/StardewValley.Tools/FishingRod.cs` | Rainbow Trout caught on Summer 20-21 can produce a `TroutDerbyTag`; tag is delivered through the no-treasure tag animation or included in the treasure menu. |
| `源文件/StardewValley/GameLocation.cs` | `TroutDerbyBooth` dialogue and reward exchange logic; `FishingDerbySign` opens a letter-style sign. |
| `源文件/Content/Strings/1_6_Strings*.json` | Sign, booth intro, explanation, no-tags, bag-full, exclamation, and start-message text. |
| `源文件/Content/Strings/Objects*.json` | `TroutDerbyTag` display name and description. |
| `源文件/Content/Data/Objects.json` | Object data for Rainbow Trout `(O)138`, `TroutDerbyTag`, reward objects, and `PrizeTicket`. |
| `源文件/Content/Characters/Dialogue/Willy.json` | Willy Summer 19/20/21 dialogue mentions the derby and his reduced shop hours. |
| `源文件/Content/Characters/schedules/Willy.json` | `TroutDerby` schedule key sends Willy to beach, shop briefly, Forest, then FishShop late. |
| `源文件/StardewValley/Object.cs` | Tent/object placement warning covers Forest before Trout Derby, similar to map replacement safety. |
| `源文件/StardewValley.Menus/Billboard.cs` | Calendar/billboard hardcodes Fishing Derby icons for Summer 20 and 21 even though `ShowOnCalendar=false` in `PassiveFestivals.json`. |
| `源文件/StardewValley/Game1.cs` and `源文件/StardewValley/Utility.cs` | Passive festival active/open/day-of-festival lifecycle and start-message broadcast. |
| `源文件/StardewValley/NPC.cs` | NPC pathing can replace locations for passive festivals with map replacements; not directly used by Trout Derby because its map replacements are null. |

## Original Facts Baseline

### Date And Festival Type

- `TroutDerby` is a passive festival, not an active festival event.
- It is scheduled for Summer 20 through Summer 21.
- It opens at 6:10 (`StartTime: 610`).
- `OnlyShowMessageOnFirstDay` is true, so the global start message only appears on Summer 20.
- `ShowOnCalendar` is false in `PassiveFestivals.json`, but `Billboard.cs` still draws a Fishing Derby event icon/name on Summer 20 and 21.

### Map, Time, And NPC Semantics

- `PassiveFestivals.json` has `MapReplacements: null` for Trout Derby. It does not switch Forest to a separate passive festival location through the generic passive-festival map replacement system.
- `Forest.cs` still applies local map overrides:
  - Summer 17-19: pre-event derby sign override.
  - Summer 20-21: derby booth override, light sources, and simple contestant NPCs.
  - Outside those dates: revert override and remove `derby_contestent*` NPCs.
- The event does not use active-festival temporary maps, does not call `Game1.isFestival()`, and does not freeze time.
- Normal NPC schedules continue. Willy has a dedicated `TroutDerby` schedule key; general passive-festival schedule-key selection can pick `TroutDerby_1`, `TroutDerby_2`, then `TroutDerby`, but the vanilla data found here only shows a `TroutDerby` Willy schedule.
- Since Trout Derby has no map replacement, generic NPC route map replacement is not part of the derby behavior. The visible extra fishers are simple Forest local NPCs spawned by `Forest.cs`, not normal villager schedule actors.

### Entry, Announcement, Signs, And Dialogue

- There is no active festival entry prompt and no venue transition.
- The global message at 6:10 on Summer 20 is `Strings\\1_6_Strings:TroutDerby_NowOpen`.
- The pre-event sign text is `Strings\\1_6_Strings:FishingDerbySign`; it uses letter background `LooseSprites\\troutDerbyLetterBG`.
- The booth action is `TroutDerbyBooth`, opening a three-choice question dialogue: rewards, explanation, leave.
- Explanation text is `FishingDerbyBooth_Explanation`; no-tags and bag-full fallbacks are `FishingDerbyBooth_NoTags` and `FishingDerbyBooth_BagFull`.
- Willy has ordinary NPC dialogue keys for Summer 19, 20, and 21. These are normal dialogue/schedule interactions, not active-festival actor dialogue.
- Source mail search found no Trout Derby mail entry. The original player-facing lead-ins are calendar/billboard, sign, Willy dialogue, and the day-one global start message.

### Fishing And Golden Tags

- Rainbow Trout is object `(O)138`; `TroutDerbyTag` is the Golden Tag object.
- Forest has an additional Rainbow Trout spawn rule conditioned by `IS_PASSIVE_FESTIVAL_OPEN TroutDerby`. That rule ignores fish data requirements and is restricted to Summer/River context in the source data.
- The Golden Tag roll in `FishingRod.doneHoldingFish` is source-coded, not data-driven:
  - not from a fish pond;
  - season is summer;
  - caught fish qualified id is `(O)138`;
  - day of month is 20 or 21;
  - random chance is `0.33 * numberOfFishCaught`.
- The tag chance scales with multi-catch count but produces at most one Golden Tag because the source state is a boolean `gotTroutDerbyTag`.
- If no treasure was caught and a tag was rolled, the normal fish handoff switches into a tag animation and then awards the Golden Tag plus the fish if needed.
- If treasure was caught and a tag was rolled, the tag is added into the treasure menu alongside other treasure loot.
- If neither treasure nor tag was rolled, the fish is delivered through the normal catch path.
- The source date check for the tag roll is Summer 20-21; it does not directly call `Utility.IsPassiveFestivalOpen`. In practice, the special Forest spawn rule is open-gated, but Magic Bait or other ways to catch Rainbow Trout on those dates may still satisfy the source tag-roll condition.

### Reward Exchange

- The booth requires at least one `TroutDerbyTag` in the current player's inventory.
- Reward state uses `Game1.stats.Get("GoldenTagsTurnedIn")`; `Game1.stats` resolves to the current player's stats, so turn-in count is per-player.
- First ever turn-in gives `(O)TentKit`.
- Later rewards use `rewardIndex = (CreateRandom(uniqueIDForThisGame).Next(10) + GoldenTagsTurnedIn) % 10`.
- The repeating pool is:
  - `(H)BucketHat`
  - `(O)710` Crab Pot
  - `(O)MysteryBox` x3
  - `(O)72` Diamond
  - `(F)MountedTrout_Painting`
  - `(O)DeluxeBait` x20
  - `(O)253` Triple Shot Espresso x2
  - `(O)621` Quality Sprinkler
  - `(O)688` Warp Totem: Farm x3
  - `(O)749` Omni Geode x3
- On successful exchange, source increments `GoldenTagsTurnedIn`, removes one `TroutDerbyTag`, holds up the reward, then adds it to inventory.
- If the reward cannot fit and the player is not spending their last tag, source shows the bag-full dialogue instead of consuming a tag.
- No daily reset was found for `GoldenTagsTurnedIn`; it is a persistent per-player stat. No cleanup was found for unspent Golden Tags.

### Multiplayer Notes

- `ActivePassiveFestivals` is world state, so festival day/open status is shared.
- Fishing catch, tag inventory, and `GoldenTagsTurnedIn` are per-player.
- Booth exchange checks `Game1.player.Items`, so each player turns in their own tags.
- The Forest map override and simple derby contestants are location/world presentation state. Spawning is guarded by a mutex in source.
- There is no multiplayer ready gate, no shared score, and no team reward pool for Trout Derby.

### End, Cross-Day, And Continuation

- Summer 20 and 21 both use the same passive festival id; `GetDayOfPassiveFestival` returns 1 then 2.
- At 6:10 on Summer 20, the global message is shown; Summer 21 is silent because first-day-only is true.
- There is no active end cutscene, no forced warp, and no time jump.
- At the next date outside Summer 20-21, `Forest.cs` reverts the derby booth override and removes contestant NPCs.
- There is no source cleanup method for Golden Tags or `GoldenTagsTurnedIn`.

## StardewCraft Source Parity Notes

- `FestivalRegistry` already registers `TroutDerby` as passive Summer 20-21, start 610, no overlay, first-day-only message, location `Forest`, mechanic id `trout_derby`.
- `FestivalService` already supports passive festival day/open/day-of-festival queries and first-day-only start messages.
- `PassiveFestivalHandlers` currently registers only `DesertFestivalHandler`; Trout Derby has no handler yet.
- Forest fishing data already includes a `stardewcraft:rainbow_trout` rule conditioned by `IS_PASSIVE_FESTIVAL_OPEN TroutDerby`.
- `FishingSessionManager.handleResult` is the current best hook point for awarding Golden Tags after a successful Rainbow Trout catch.
- `ModItems` currently has `PRIZE_TICKET`, `MYSTERY_BOX`, `DELUXE_BAIT`, `CRAB_POT`, `QUALITY_SPRINKLER`, `DIAMOND`, `OMNI_GEODE`, and `WARP_TOTEM_FARM`, but no registered Golden Tag, Tent Kit, Bucket Hat, Mounted Trout painting, or Triple Shot Espresso was found in `ModItems`.
- `PrizeTicketRewardService` and the prize machine are unrelated to Trout Derby source reward exchange, except that they prove per-player reward counters and prize UI payloads already exist.

## Must-Confirm Coordinates And Assets

Do not infer these from original tiles, TMX, schematics, visuals, or pregenerated regions.

| Key | Needed for | Status |
| --- | --- | --- |
| `trout_derby_sign_zone` | Summer 17-19 sign interaction / letter text | Needs user confirmation |
| `trout_derby_booth_zone` | Summer 20-21 booth reward/explanation interaction | Needs user confirmation |
| `trout_derby_booth_display` | Optional visible booth/block/model placement | Needs user confirmation |
| `trout_derby_contestant_points` | Optional source-style background fishing contestants | Needs user confirmation per point/NPC |
| `trout_derby_light_or_decoration_points` | Optional booth lights/decorations | Needs user confirmation |
| `trout_derby_forest_bounds` | If a local Forest overlay/patch is used for sign or booth | Needs user confirmation |
| `trout_derby_safe_cleanup_bounds` | If spawned display entities or interaction blocks need cleanup | Needs user confirmation |

Asset requirements to confirm before implementation:

- Golden Tag item texture from `TileSheets\\Objects_2`, sprite index 13.
- Trout Derby sign letter background `LooseSprites\\troutDerbyLetterBG` if the letter UI is implemented visually.
- Booth/sign visual assets from `Forest_FishingDerbySign` and `Forest_FishingDerby` map override sources, if StardewCraft wants visible world presentation.
- Background contestant sprites/models if source-style ambient fishers are implemented.
- Reward assets/items for Tent Kit, Bucket Hat, Mounted Trout painting, and Triple Shot Espresso, or explicit first-version fallback rules.