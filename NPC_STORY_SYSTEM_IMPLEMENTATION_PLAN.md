# NPC + Story System Implementation Plan (Strict 1:1 Parity)

## 1. Scope and Non-Negotiable Rules

- Highest principle (must always win):
- The repository folder source files is fully authorized by the original author, including source code and assets.
- If vanilla has it and this project can support it, implement it directly as final parity in one step. Do not ship temporary approximation stages for that feature.
- Do not wait for additional confirmation between parity substeps. Continue implementing to completion once the target is identified.
- When vanilla Stardew Valley has a GUI, this mod must reproduce it exactly at code and pixel level, including layout, effects, sounds, and component animations.
- Fullscreen-to-fullscreen comparison must be visually identical, including one-pixel alignment.
- Required art assets must be sourced from source files and extracted from Content with exact fidelity before integration.
- UI rendering adaptation must account for Minecraft and Stardew rendering differences and must use StardewRenderMapping as the mapping baseline.

- Compatibility fallback (mandatory when host content is missing):
- If vanilla references items, locations, maps, festivals, or logic that do not exist in this project yet, loader-time compatibility filtering must be applied.
- Gift tastes must only keep items that exist in this project runtime item registry.
- Schedule routes must only keep locations that exist in current project location mapping.
- Festival-specific branches must be ignored until festival runtime exists.
- Compatibility filtering is loader behavior only and must not alter vanilla logic for supported content.

- Packaging and dependency rule:
- Final runtime must not depend on repository source files folder paths.
- Vanilla data needed by runtime must be copied into src resources and loaded from project assets only.
- Source files folder is reference/import input only, never runtime dependency.
- Source-level vanilla parameter files are development-stage scaffolding only.
- Before release hardening, all runtime parameter files must be converted into project-native schema files under src resources.
- Release artifacts must not ship temporary import-format files when equivalent project-native files exist.

- Scope is src only. No datapack-first implementation path.
- Stardew Valley vanilla source code is the single source of truth.
- No intentional behavior deviation is allowed.
- Before each implementation session, this MD must be re-read in full and treated as the active execution contract.
- NPC rollout is incremental: only the currently implemented subset is in active runtime scope.
- NPCs without walk animation are not allowed to run path movement loops.
- Existing mod architecture (dimension-only runtime, fixed map, interior subspace) is host infrastructure only.
- If host constraints conflict with vanilla behavior, adapter layer must absorb the conflict without changing gameplay logic.
- Story scripts, dialogue text, schedule paths, and NPC gift preferences must all be data-driven.
- The data schema must be extension-ready: adding new NPCs and new content must not require core runtime rewrites.

## 2. Target Outcome

Deliver a code-level equivalent NPC and story runtime in this mod with matching behavior for:

- NPC schedule parsing and execution
- Pathfinding and route end behaviors
- Dialogue selection, queue, and day-state gating
- Friendship state and gift taste effects
- Story event actor orchestration
- Save/load persistence and multiplayer sync behavior

Additional rollout constraints:

- Only NPCs declared as implemented are loaded into runtime.
- Idle-only NPCs can participate in dialogue and story state but must use non-walking presence mode.
- Data assets define capability flags (for example: pathing enabled or disabled) per NPC.
- Prototype scope override: story event runtime is temporarily deferred.
- Prototype must deliver only daily dialogue, friendship progression, and gift handling for a small NPC subset.
- Compatibility-first fallback: if vanilla references items or paths not present in this project, they are filtered out during data load.

## 3. Source-of-Truth Mapping Baseline

Primary vanilla references:

- StardewValley/NPC.cs
- StardewValley.Pathfinding/PathFindController.cs
- StardewValley/Event.cs
- StardewValley/GameLocation.cs
- StardewValley/Game1.cs
- StardewValley/Friendship.cs
- StardewValley/DataLoader.cs

Initial method parity anchors:

- NPC.loadCurrentDialogue
- NPC.checkSchedule
- NPC.parseMasterScheduleImpl
- NPC.TryLoadSchedule
- NPC.pathfindToNextScheduleLocation
- NPC.getRouteEndBehaviorFunction
- NPC.receiveGift
- NPC.getGiftTasteForThisItem
- NPC.grantConversationFriendship

## 4. Architecture (Do Not Change Vanilla Business Semantics)

### 4.0 Data-Driven Contract (Mandatory)

Purpose:

- Ensure story, dialogue, schedule, and taste logic are configured from data in the same spirit as vanilla data loading.

Required data groups:

- Characters profile data
- Dialogue text and keyed variants
- Schedule data and route segments
- Gift taste and preference data
- Story event scripts and actor bindings
- NPC capability flags (implemented, pathing enabled, animation profile)

Rules:

- Runtime logic reads from data loaders and never hardcodes per-NPC content.
- New NPC onboarding requires data additions first, with no branching explosion in runtime code.

### 4.1 Domain Core (Vanilla Logic Port)

Purpose:

- Own all NPC/story business rules.
- No direct dependency on Minecraft world APIs.

Core modules:

- NpcRuntimeState
- NpcScheduleState
- DialogueState
- FriendshipState
- StoryEventState

Rules:

- Field names and transitions should mirror vanilla semantics.
- Priority order and branching must follow vanilla code paths.
- If pathing capability is disabled for an NPC, schedule state remains valid but movement execution is gated by capability data.

### 4.2 Adapter Layer (World and Engine Bridge)

Purpose:

- Convert domain operations into mod runtime operations.

Core modules:

- StardewLocationResolver: vanilla location name to fixed-map/interior coordinates.
- StardewTimeBridge: hooks into existing time progression to trigger schedule checkpoints.
- StardewPathBridge: tile passability, transitions, and path route realization.
- InteriorPortalBridge: preserve location semantics across indoor/outdoor subspace transitions.

Rules:

- Adapter can translate coordinates and world APIs.
- Adapter cannot change domain decisions.

### 4.3 Entity and Rendering Bridge

Purpose:

- Visualize domain NPC state and expose interaction entry points.

Core modules:

- ServerNpcEntity
- NpcEntitySyncController
- NpcGeoRenderer registration

Rules:

- Server-side domain state is authoritative.
- Missing animation clips are visual fallback only and cannot alter logic timing.

## 5. Implementation Phases

## Phase A - Runtime Skeleton and Persistence

Deliverables:

- New npc domain package and base state containers.
- Per-world NPC manager service for tick ownership.
- Save/load schema for NPC runtime state.
- Network sync channel for essential NPC state replication.
- Data loader skeleton for NPC profile, dialogue, schedule, gift taste, and story event resources.
- NPC capability model including implemented-scope and pathing-enabled flags.

Acceptance:

- Server restart preserves NPC identity and core runtime state.
- Multiplayer clients see consistent NPC existence and position.
- Runtime loads only NPCs marked implemented.

## Phase B - Schedule and Path Parity

Deliverables:

- Schedule parser equivalent to vanilla parsing rules.
- Time-triggered schedule execution chain.
- Route generation and movement loop with route end behavior support.
- Indoor/outdoor location transition through portal bridge.
- Non-pathing NPCs keep schedule timeline state but skip walking execution by capability gate.

Acceptance:

- At parity test times, NPC target locations match expected vanilla outcomes.
- Route completion triggers the same next-step behavior category as vanilla.
- NPCs flagged as no-path produce deterministic stationary behavior without path tick side effects.

### Phase B.1 - Lewis Single-NPC Hard Gate (Must Pass Before Multi-NPC Rollout)

Scope:

- Freeze active schedule/path runtime to Lewis only.
- Treat [LEWIS_SCHEDULE_REPLAN.md](LEWIS_SCHEDULE_REPLAN.md) as the authoritative execution contract for Lewis path/schedule behavior.

Required behavior gates:

- Key selection order must match wiki/vanilla priority chain exactly, including season/day, weekday, rain/rain2, and GOTO recursion with loop guard.
- Schedule endpoint resolution must be explicit and data-driven; no hidden anchor guessing and no implicit tile offset guessing.
- If endpoint mapping for current schedule node is missing: do not move Lewis and emit a hard warning.
- Runtime must keep exactly one live Lewis entity in world state.
- Lewis must not remain in `entity=<missing>` during normal schedule execution.
- Spawn/respawn target must resolve to active schedule target (or explicit mapped fallback), never uncontrolled world-spawn fallback.
- Path failure (`no_path`) must be recoverable by deterministic replan + bounded fallback sync policy.

Lewis-specific data blockers (must be closed):

- CommunityCenter_Hall indoor coordinate.
- Saloon_Table indoor coordinate.
- FishShop_InnerEntry coordinate.
- FishShop_Counter coordinate.
- Final confirmation of indoor exit points (exit equals entry) for each building used by Lewis routes.

Current execution override (Lewis-only batch, approved):

- Indoor endpoint coordinates are temporarily deferred.
- Runtime must keep explicit endpoint mapping and hard warning behavior using currently approved outdoor substitutes from LEWIS_SCHEDULE_REPLAN.
- Deferred indoor coordinates are not a blocker for this batch's 2-7 completion gate, but remain mandatory before full indoor parity sign-off.

Lewis evidence package (required for gate pass):

- Spring day 1 and day 2 checkpoint timeline logs.
- Key-candidate trace with chosen key and rejection reasons.
- Node-level endpoint ID and world coordinate trace.
- Path metrics trace (path length, next waypoint, replan cause, fallback usage).
- Save/load continuity check proving Lewis uniqueness and no persistent missing state.

## Phase C - Dialogue and Friendship Parity

Deliverables:

- Dialogue load stack and context resolution.
- Daily talked state, weekly gift counters, and friendship point deltas.
- Gift taste lookup and resulting friendship mutation.

Acceptance:

- Same interaction sequence produces same friendship deltas as vanilla.
- Dialogue branch choice follows equivalent condition priority.

## Phase D - Story Event Runtime

Deliverables:

- Event script parser and command dispatcher.
- Actor lifecycle (spawn/use/remove), movement, facing, emote, and dialogue triggers.
- Event start, progression, lock-state, and completion transitions.

Acceptance:

- Supported event scripts produce matching actor flow and completion state.
- Player control lock/unlock timing matches expected vanilla behavior class.

## Phase E - Parity Audit and Hardening

Deliverables:

- Method-by-method parity checklist with status.
- Regression test matrix for schedule, friendship, dialogue, and event flows.
- Multiplayer sync edge-case pass.

Acceptance:

- No known intentional deviation remains.
- Checklist status is complete for target release slice.

## 6. Test Strategy

### 6.1 Parity Test Table

Create and maintain a table with columns:

- Vanilla method
- Mod implementation location
- Input setup
- Expected vanilla output
- Actual output
- Pass or fail

### 6.2 Mandatory Regression Scenarios

- Day boundary transitions and schedule re-evaluation
- Weather and day-type conditional schedule branches
- Indoor/outdoor transition route continuity
- Dialogue repeat conditions and daily reset behavior
- Gift weekly cap and friendship update edge cases
- Save/load at mid-route and mid-event states
- Multiplayer join-in-progress synchronization
- Mixed roster test: implemented pathing NPC + implemented idle-only NPC + not-yet-implemented NPC
- Data extension test: adding one new NPC through data only without changing core logic

### 6.3 Data-Driven Validation

- Schema validation for required keys and value domains
- Missing data diagnostics with explicit file and key paths
- Backward-compatible parser behavior for future fields

## 7. Current Repo Constraints and How They Are Handled

- Dimension-only behavior: keep NPC runtime active only in Stardew dimension manager.
- Fixed map: location resolver maps vanilla location semantics to fixed coordinates.
- Interior subspace: portal bridge preserves location semantics while using current interior system.
- Existing geo and animation assets: renderer bridge uses current asset folders without logic coupling.

## 8. Work Breakdown Structure (Execution Order)

1. Scaffold domain state and NPC manager runtime.
2. Add persistence schema and sync packets.
3. Implement schedule parser and executor.
4. Implement path bridge and route end behavior handlers.
5. Implement dialogue resolver and friendship mutation service.
6. Implement event runtime command set (minimum viable set first, then full set).
7. Run parity audit and close all red cases.

## 9. Definition of Done

All conditions must be true:

- Source-level parity checklist for targeted feature set is complete.
- gradle classes succeeds.
- No unresolved IDE compile problems in touched files.
- No known behavior difference remains without an explicitly approved exception.
- All NPC/story/dialogue/path/taste content in current scope is loaded through data pipelines.
- Capability flags correctly enforce no-path behavior for idle-only NPCs.

## 10. Immediate Next Action

Schedule/path prerequisite gate (must finish first for Lewis):

- close all Lewis Phase B.1 behavior gates and data blockers.
- update parity checklist entries for schedule/path methods with evidence-based status.
- only after Lewis schedule/path gate passes, continue full Phase C sprint below.

Temporary gate note for current execution window:

- Indoor coordinate blockers are excluded from this batch by explicit scope decision.
- All non-indoor Lewis gates (key order, endpoint hard warning, no_path recovery, uniqueness/no-missing) must still pass with evidence.

Start Phase C strict parity sprint now (schedule/path baseline is already in active verification):

- implement vanilla-equivalent dialogue resolver priority chain (`NPC.loadCurrentDialogue`)
- implement vanilla gift taste resolver exact rules (`NPC.getGiftTasteForThisItem`)
- implement vanilla gift receive flow and weekly/day gates (`NPC.receiveGift`)
- implement vanilla daily talk friendship gate (`NPC.grantConversationFriendship`)
- add method-level trace logs for dialogue/gift/friendship decisions
- run `gradle classes` and update checklist entries with pass/fail evidence

## 11. Gift Data Pitfalls (Postmortem Rules)

These rules are mandatory for all future gift-taste updates:

1. Do not treat offline snapshots (for example `items.txt`) as the source of truth for item availability; use runtime registry filtering as final truth.
2. Do not validate only explicit item IDs from vanilla taste strings; category tokens (for example `-80`, `-6`) must be expanded and reviewed.
3. Universal preferences are required in runtime taste resolution. At minimum, universal love must be present in shared fallback data.
4. Loader filtering is allowed, but every dropped taste token must produce a warning log so data drift is visible.
5. Name normalization must account for punctuation and token differences (for example apostrophes and spacing) to avoid silent misses.
