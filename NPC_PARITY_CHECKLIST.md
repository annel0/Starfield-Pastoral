# NPC Method-Level Parity Checklist

Reference rule: Stardew Valley source code behavior is authoritative.

| Vanilla Method | Mod Target | Current Status | Notes |
|---|---|---|---|
| NPC.parseMasterScheduleImpl | npc schedule parser module (Phase B) | In Progress | Parser supports baseline `location x y facing`; route/end-behavior token parity still pending. |
| NPC.TryLoadSchedule | npc schedule loader (Phase B) | In Progress | Lewis key selection now follows wiki chain order with candidate/rejection trace; full matrix still pending (festival keys/runtime festival context, heart-qualified edge branches). |
| NPC.checkSchedule | runtime schedule tick bridge (Phase B) | In Progress | Runtime now persists active schedule key + checkpoint + node index; full conditional branches and map-transition parity pending. |
| NPC.pathfindToNextScheduleLocation | path bridge executor (Phase B) | In Progress | Custom path runtime is online for Lewis pilot (no mob navigation), with no_path deterministic replan + bounded fallback sync and path metrics trace; full route-end semantics parity still pending. |
| NPC.getRouteEndBehaviorFunction | route-end behavior resolver (Phase B) | Not Started | No custom shortcut behavior allowed. |
| NPC.loadCurrentDialogue | dialogue resolver (Phase C) | In Progress | Candidate rejection trace + same-day dialogue segment consumption are online; full context priority chain and special-state matrix pending. |
| NPC.receiveGift | friendship service (Phase C) | In Progress | Weekly/day gift gates, birthday multiplier, and trace logs are online; full vanilla exception branches pending. |
| NPC.getGiftTasteForThisItem | taste resolver (Phase C) | In Progress | NPC taste + universal fallback are online; loader now logs dropped taste tokens; full vanilla exception matrix pending. |
| NPC.grantConversationFriendship | daily talk mutation service (Phase C) | In Progress | Daily first-talk mutation is online and now gated by valid dialogue output; vanilla exact gating parity pending. |
| DialogueBox.draw / drawPortrait / update | StardewNpcDialogueScreen (Phase C UI parity) | In Progress | Box rendering and transition flow ported; `#$e#`/`$e` continuation now paginates within one interaction. SpriteText-level pixel/font parity and full input branch parity pending. |
| Event actor command flow (Event.cs) | story event runtime (Phase D) | Data Pipeline Ready | event files load; interpreter not implemented. |

## Data Contract Coverage (Phase A)

- capability data: Ready
- dialogue data: Ready
- schedule data: Ready
- taste data: Ready
- event data: Ready

## Compatibility Rules (Project-First Filter)

- Vanilla-first import: source-like NPC preferences and schedules are loaded first.
- Item compatibility filter: taste entries that do not map to existing project items are removed at load-time.
- Animation compatibility filter: NPC pathing is automatically disabled when walk animation clip is missing.
- Location compatibility filter: schedule entries targeting unmapped locations are removed at load-time.
- Indoor endpoint coordinates for Lewis are explicitly deferred in current batch; missing mapped endpoint now triggers hard warning + movement pause instead of fallback guessing.

## Runtime Baseline (Phase A)

- implemented NPC roster filtering: Ready
- idle-only no-path enforcement at runtime state level: Ready
- runtime state persistence: Ready
- cross-file missing-data diagnostics for implemented NPCs: Ready
- walk-animation auto detection and pathing gate: Ready
- taste item availability filtering: Ready
- schedule location mapping filtering: Ready

## Phase C Immediate Execution Slice (Current)

Target: full vanilla semantic parity for dialogue, gift, and friendship flows (not UI-only parity).

Execution order:

1. `NPC.loadCurrentDialogue`
2. `NPC.getGiftTasteForThisItem`
3. `NPC.receiveGift`
4. `NPC.grantConversationFriendship`

Acceptance checklist:

- Dialogue candidate trace and selected key reason are logged for each interaction.
- Same-day repeated talks retain daily gate state and per-talk trace visibility.
- In-dialogue continuation is now client-paginated by `#$e#`/`$e` and requires additional advance clicks before close.
- Weekly gift cap behavior matches vanilla for gift #1/#2/#3 in same week.
- Birthday gift multiplier path matches vanilla branch and friendship delta.
- Daily talk friendship bonus applies once per day with correct reset at day transition.
- Save/load keeps weekly gift counters and talked-today state without drift.
- Loader logs unresolved/dropped taste tokens per NPC category for data correction feedback.
