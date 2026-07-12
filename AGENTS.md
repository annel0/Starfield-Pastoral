# Fork invariants

## Purpose

This fork of [ChangQingElysium/Starfield-Pastoral](https://github.com/ChangQingElysium/Starfield-Pastoral)
adds a **Russian localization** on top of the upstream mod. It intentionally
changes only text and text-bearing assets — no gameplay logic, balance, or
features are modified relative to upstream.

## What this fork changes vs. upstream

1. `src/main/resources/assets/stardewcraft/lang/ru_ru.json` — full Russian
   translation (~9,500 keys) sitting alongside upstream's `en_us.json` and
   `zh_cn.json`. This file does not exist upstream; it is purely additive.

2. `src/main/resources/data/stardewcraft/mail/*.json` and
   `src/main/resources/data/stardewcraft/quests.json` — the `text`/`description`
   fields are translated to Russian in place (upstream ships these with
   Chinese text baked in, there is no separate lang-keyed version).

3. A set of Java source files contain **hardcoded UI strings that are not
   read from any lang file** (validation/status messages, shop tooltips,
   enum display names, etc.). These were audited and translated to Russian
   directly in the source. Known files with translated hardcoded strings:
   - `block/utility/BarnManagerBlock.java`, `CoopManagerBlock.java`,
     `SiloManagerBlock.java`, `FishPondManagerBlock.java`
   - `animal/service/BarnManagerValidationService.java`,
     `CoopManagerValidationService.java`, `SiloManagerValidationService.java`
   - `fishpond/service/FishPondManagerValidationService.java`
   - `client/gui/FarmAdminScreen.java`, `AnimalPurchaseScreen.java`,
     `AnimalQueryScreen.java`, `AnimalMoveHomeSelectScreen.java`,
     `DecorationSelectionScreen.java`, `ShopScreen.java`,
     `CookingPotScreen.java`, `menu/StardewGameMenuScreen.java`
   - `client/deco/PaintbrushModeIndicator.java`,
     `client/render/LadderHighlightRenderer.java`
   - `combat/buff/CombatBuffType.java`, `combat/debuff/DebuffType.java`
   - `player/ProfessionType.java`, `player/SkillType.java`
   - `item/weapon/WeaponRarity.java`, `block/utility/totem/TotemType.java`,
     `totem/SystemTotemManager.java`
   - `network/payload/FarmAdminPayload.java`, `FarmSelectionSubmitPayload.java`
   - `interior/CrossDimensionTeleporter.java` (first-join welcome messages),
     `interior/OrangeSisterWelcomeService.java`
   - `event/DimensionEventHandler.java`, `event/InteriorPortalInteractionEvents.java`
   - `farm/FarmCaveAPI.java`, `farm/FarmInstanceInitializer.java`
   - `festival/EggFestivalService.java`, `festival/FestivalOfIceService.java`
     (scoreboard sidebar labels only — these two files also contain
     admin-only debug strings behind `hasPermission(2)` that were
     deliberately left untranslated, see below)
   - `integration/jade/CoopAnimalJadeProvider.java`, `player/PlayerDataEventHandler.java`

   **Deliberately left untranslated (do not translate these on sync):**
   - Any string only reachable through a command gated by
     `.requires(source -> source.hasPermission(2))` or `player.hasPermissions(2)`
     (operator/debug-only commands, e.g. everything under `command/*Debug*.java`,
     `MoneyContractService.openDebugActionMenu`, festival "start debug festival"
     status strings).
   - Any string only reachable when `SharedConstants.IS_RUNNING_IN_IDE` /
     a `*_DEV_HINTS` config flag is true (IDE-only dev diagnostics).
   - Locale-gated content pools where the code already does
     `lang.startsWith("zh") ? POOL_ZH : POOL_EN` (e.g. animal/totem name
     suggestion pools) — Russian clients fall through to the English pool
     already, so the Chinese pool is dead code for us and untouched.
   - `getCropDisplayName()` in `block/crop/*CropBlock.java` — declared
     abstract, overridden ~42 times, but has zero callers anywhere in the
     codebase (verified dead code). Do not spend effort translating it.

4. `src/main/resources/assets/stardewcraft/textures/gui/billboard/calendar_background.png`
   and `daily_quest_background.png` — hand-edited pixel-art textures with
   Russian text baked in (weekday headers, "Требуется помощь", seed ad).
   These are **binary assets, not text** — a text-based merge cannot resolve
   them.

5. `README.md` — translated to Russian, with attribution to the upstream
   project and author at the top. Do not remove or shrink the attribution
   block when adapting README changes from upstream.

## Upstream synchronization rules

When adapting an upstream merge (via automated sync or manually):

- **Never revert a Russian translation back to Chinese/English** in a file
  listed above just because upstream touched that file. If upstream changed
  or added lines in an already-translated area, translate the *new/changed*
  portion into Russian in the same style and tone as the surrounding text;
  keep everything already translated exactly as it is unless upstream's
  change requires it to change too (e.g. a renamed placeholder token).
- If upstream adds a **new** hardcoded UI string in Java source (same
  pattern as section 3), translate it into Russian following the same
  rules used for the existing strings in that file — but first check it
  isn't behind an op-permission/debug/IDE-only gate as described above;
  those stay in the original language.
- If upstream modifies `calendar_background.png` or
  `daily_quest_background.png`, **do not silently take upstream's binary
  version** (this would revert the Russian text). Flag it for human review
  in the PR description instead — pixel-art text edits cannot be auto-merged.
- Preserve all Stardew-specific dialogue markers exactly: `%s`/`%1$s`
  printf placeholders, `{0}`/`{FishType:Text}` opaque placeholders,
  `${word^word}$` inline gender tokens (translate the words inside, keep
  the frame), `@`/`#`/`$`/`^` structural dialogue control characters. Do
  not add, remove, or reorder these.
- Do not perform unrelated refactors, renames, or style changes while
  adapting an upstream sync — keep the diff focused on making upstream's
  change compile and behave correctly in this fork.
- Remove all git conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`) before
  finishing — a build with conflict markers left in is a failed sync, not
  a partial success.
- After adapting, run `./gradlew build` and fix any compile errors caused
  by the merge. If you cannot get the build green, leave clear TODO/FIXME
  comments at the failure points and say so plainly in the PR description
  — do not silently comment out or delete failing code to make the build
  pass.
