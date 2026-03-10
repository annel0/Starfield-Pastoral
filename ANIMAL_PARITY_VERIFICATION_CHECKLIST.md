# Animal Parity Verification Checklist (v1)

Scope: coop + barn animals, behavior parity with Stardew 1.6 sources.
Reference sources: `源文件/StardewValley/FarmAnimal.cs`, `源文件/Content/Data/FarmAnimals.json`.

## 1. Sound Parity

- [x] Pet interaction sound should play once, not repeated burst.
Current: `BaseCoopAnimalEntity.playPetFeedback` plays one shot only (`src/main/java/com/stardew/craft/entity/animal/BaseCoopAnimalEntity.java:417`).
- [x] Ambient vocalization should be random chance based, not fixed interval.
Current: `AMBIENT_SOUND_CHANCE_PER_TICK = 0.001` and random gate in `tryPlayAmbientAnimalSound` (`src/main/java/com/stardew/craft/entity/animal/BaseCoopAnimalEntity.java:94`).
- [x] Animal sound IDs should match FarmAnimals data mapping.
Current: `mapPetSoundEvent` covers coop + barn IDs (`src/main/java/com/stardew/craft/entity/animal/BaseCoopAnimalEntity.java:573`).
- [ ] Verify in-game perceived frequency (singleplayer + multiplayer proximity).
Method: 2 in-game sessions, 10 minutes each, record calls/minute by type.

## 2. Grass Eating Parity

- [x] Grass search trigger condition parity: fullness < 195 and random < 0.002.
Current: `EatPastureGrassGoal.canUse` matches this (`src/main/java/com/stardew/craft/entity/animal/BaseCoopAnimalEntity.java:1132`).
- [x] Eating grass sets fullness to 255 and grants happiness/friendship in non-rain, non-mood 5/6 case.
Current: `applyEatState` aligns (`src/main/java/com/stardew/craft/entity/animal/BaseCoopAnimalEntity.java:1229`).
- [x] Grass consumption unit parity set to single clump object.
Current: `requiredGrassClumps` returns `1` (`src/main/java/com/stardew/craft/entity/animal/BaseCoopAnimalEntity.java:1248`).
- [ ] Validate edge cases: blue grass bonus and crowded pasture with many animals.
Method: scripted pasture setup with 20+ animals and separate blue-grass pen.

## 3. Feeding / Daily Update Parity

- [x] Nightly hay consumption only when animal treated as at-home state.
Current: trough consume branch in `applyDayUpdate` (`src/main/java/com/stardew/craft/manager/AnimalGrowthManager.java:321`).
- [x] No-pet penalty + daily reset flags parity.
Current: pet/auto-pet penalty and reset in `applyDayUpdate` (`src/main/java/com/stardew/craft/manager/AnimalGrowthManager.java:308`).
- [x] 18:00+ per-ten-minute happiness updates for outdoor/winter/rain.
Current: `updatePerTenMinutes` branch (`src/main/java/com/stardew/craft/manager/AnimalGrowthManager.java:145`).
- [ ] Validate left-outside-night behavior with door open/closed permutations.
Method: run 4 scenarios (door open/closed x indoors/outdoors at night) and verify morning mood/fullness/happiness.

## 4. UI/Icon Parity

- [x] Animal query icon rendering uses source-rect parity logic from Stardew dimensions.
Current: `AnimalQueryScreen.drawAnimalIcon` uses per-type source rect rules (`src/main/java/com/stardew/craft/client/gui/AnimalQueryScreen.java`).
- [x] Icon resource files regenerated from original source assets.
Current files: `src/main/resources/assets/stardewcraft/textures/gui/animal_query/icon_*.png`.
- [ ] Visual QA pass at 100%, 125%, 150% GUI scale.
Method: screenshot and compare silhouette/pose against Stardew UI references.

## 5. Execution Plan (Next)

1. Run the in-game behavior matrix for 2 full in-game days across clear + rainy weather.
2. Record mismatches in a new section "Findings" with exact observed vs expected values.
3. Fix only confirmed mismatches, then rerun `gradle classes` and the same matrix.

## Findings Log

- (empty)
