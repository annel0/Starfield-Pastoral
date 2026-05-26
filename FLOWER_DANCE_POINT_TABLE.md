# Flower Dance Point Table

All coordinates below must come from the user or the original Stardew Valley source. Do not infer them from the overlay schematic, tile visuals, or nearby layout.

## Implemented Area

| Item | Value | Status |
| --- | --- | --- |
| Overlay id | `Forest-FlowerFestival` | Implemented |
| Overlay schematic | `data/stardewcraft/structures/festivals/flower_dance_forest.schem` | Implemented |
| Overlay origin | `(-250, 60, 101)` | User-provided |
| Overlay bounds min | `(-250, 60, 101)` | User-provided |
| Overlay bounds max | `(-170, 65, 138)` | User-provided |
| Overlay size | `81 x 6 x 38` | Verified from schem |

## Source Rules

| Rule | Source | Status |
| --- | --- | --- |
| Festival id | `spring24` | Verified from `Content/Data/Festivals/spring24.json` |
| Entry window | `Forest/900 1400` | Verified from `spring24.json` |
| Free phase map | `changeToTemporaryMap Forest-FlowerFestival` | Verified from `spring24.json` |
| Main event music | `playMusic FlowerDance` | Verified from `spring24.json` |
| Invitation trigger | First normal festival dialogue, then second interaction ask-to-dance | Verified from `Farmer.cs` and `Event.cs` |
| NPC accept rule | Spouse/roommate auto accept, otherwise friendship >= 1000 and not married | Verified from `Event.cs` |
| Player-player invitation | ProposalType.Dance via `FarmerTeam` | Verified from `Farmer.cs` / `FarmerTeam.cs` |
| Shop id | `Festival_FlowerDance_Pierre` | Verified from `Event.cs` |
| Danceable NPC rule | `FlowerDanceCanDance == true`, or unset + datable, or current spouse; divorced blocks the branch | Verified from `Event.cs` |
| NPC already partnered rule | True only when an online farmer has that villager in `dancePartner` | Verified from `NPC.cs` |
| Main dance expansion | `warp Girls/Guys` expands across X using `spacing = max(1, 10 / (dancers.Count - 1))` | Verified from `Event.cs` |

## Phase 1 Required Points

| Key | Purpose | Required Data | Status |
| --- | --- | --- | --- |
| `venue_bounds_min` | Free-phase containment and entry/exit protection | BlockPos | Next allowed scope; needs user-confirmed coordinates, do not infer from overlay bounds |
| `venue_bounds_max` | Free-phase containment and entry/exit protection | BlockPos | Next allowed scope; needs user-confirmed coordinates, do not infer from overlay bounds |
| `entry_trigger_min` | Where players are prompted to enter the Flower Dance | `(-283, 58, 3)` | Confirmed by user; implemented |
| `entry_trigger_max` | Where players are prompted to enter the Flower Dance | `(-56, 80, 157)` | Confirmed by user; implemented |
| `entry_spawn` | Player position after accepting entry | `(-176, 64, 106)`, west/yaw `90` | Confirmed by user; implemented |
| `exit_trigger_min` | Where players are prompted to leave during free phase | BlockPos | Next allowed scope; needs user-confirmed coordinates |
| `exit_trigger_max` | Where players are prompted to leave during free phase | BlockPos | Next allowed scope; needs user-confirmed coordinates |
| `exit_return` | Optional return point if not using farm warp on early exit | Vec3 + yaw + pitch | Optional; current finish flow returns to farm |
| `pierre_shop_zone_min` | Zone where Pierre opens the Flower Dance shop | `(-224, 59, 135)` | Confirmed by user; implemented |
| `pierre_shop_zone_max` | Zone where Pierre opens the Flower Dance shop | `(-219, 63, 137)` | Confirmed by user; implemented |
| `pierre_actor` | Pierre festival actor standing point | `(-221, 60, 133)`, S | Confirmed by user; implemented |
| `lewis_actor` | Lewis free-phase host standing point | `(-235, 60, 109)`, S | Confirmed by user; implemented |

## NPC Free-Phase Points

Only confirmed user points are used.

| NPC | Free-phase data | Status |
| --- | --- | --- |
| Abigail | `(-244, 60, 109)`, S | Confirmed; implemented |
| Alex | `(-234, 60, 126)`, N | Confirmed; implemented |
| Caroline | `(-247, 60, 133)`, E | Confirmed; implemented |
| Clint | `(-246, 60, 120)`, E | Confirmed; implemented |
| Demetrius | `(-239, 60, 134)`, W | Confirmed; implemented |
| Elliott | `(-245, 60, 112)`, N | Confirmed; implemented |
| Emily | Route `(-241,60,118) N` <-> `(-241,60,114) S` | Confirmed; implemented |
| Evelyn | `(-227, 60, 122)`, W | Confirmed; implemented |
| George | `(-228, 60, 123)`, W | Confirmed; implemented |
| Gus | Route `(-234,60,132) N` <-> `(-236,60,132) N` | Confirmed; implemented |
| Haley | Route `(-236,60,120) N` -> `(-236,60,118) N` -> `(-239,60,118) W` -> `(-236,60,118) N` -> back | Confirmed; implemented with explicit return through middle point |
| Harvey | `(-231, 60, 131)`, E | Confirmed; implemented |
| Jodi | `(-245, 60, 133)`, W | Confirmed; implemented |
| Jas | `(-232, 60, 112)`, rotating N/E/W/S | Confirmed; implemented |
| Kent | Not provided | Not implemented |
| Leah | `(-246, 60, 110)`, S | Confirmed; implemented |
| Lewis | `(-235, 60, 109)`, S | Confirmed; implemented |
| Linus | `(-225, 60, 113)`, W | Confirmed; implemented |
| Marnie | `(-234, 60, 109)`, S | Confirmed; implemented |
| Marlon | `(-247, 60, 136)`, S | Confirmed; implemented |
| Maru | `(-229, 60, 131)`, W | Confirmed; implemented |
| Pam | `(-238, 60, 129)`, E | Confirmed; implemented |
| Penny | `(-227, 60, 116)`, W | Confirmed; implemented |
| Pierre | `(-221, 60, 133)`, S | Confirmed; implemented |
| Robin | `(-246, 60, 132)`, S | Confirmed; implemented |
| Sam | `(-227, 60, 117)`, W | Confirmed; implemented |
| Sebastian | `(-242, 60, 108)`, S | Confirmed; implemented |
| Shane | `(-227, 60, 108)`, S | Confirmed; implemented |
| Vincent | Loop `(-229,60,119) N` -> `(-229,60,116) N` -> `(-231,60,116) W` -> `(-231,60,119) S` -> `(-229,60,119) E` | Confirmed; implemented |
| Willy | `(-236, 60, 136)`, S | Confirmed; implemented |
| Wizard | `(-237, 64, 103)`, S | Confirmed; implemented |

## Phase 2 Dance Partner Points

| Key | Purpose | Required Data | Status |
| --- | --- | --- | --- |
| `invite_rule` | Partner invitations | First festival dialogue, then second NPC interaction opens NPC invite confirmation; player-player proposal uses right-click player ask + target confirmation | NPC invite implemented; player-player implemented; no coordinate requirement |
| `player_wait_spots` | Separate pre-dance waiting stage | Ordered Vec3 + yaw list | Not required for current source-style flow; main event cutscene places dancers directly |
| `npc_wait_spots` | Separate pre-dance waiting stage | Ordered Vec3 + yaw list | Not required for current source-style flow; main event cutscene places dancers directly |

## Phase 3 Dance Formation Points

| Key | Purpose | Required Data | Status |
| --- | --- | --- | --- |
| `dance_nondancer_points` | Non-dancers during main dance | See list below | Confirmed; implemented in cutscene actor command |
| `dance_pairs` | Pair formation positions | 3 rows x 6 pairs, female S side, male N side | Confirmed; implemented in cutscene actor command |
| `spectator_spots` | Non-dancing players/fallback observers | Area `(-239,60,132)` to `(-231,60,135)`, face N, one-block X/Z gap | Confirmed; implemented up to 10 player/NPC spectators |
| `lewis_host_stage` | Lewis position for opening/closing speech | Same as free point unless changed | User said Lewis/Marnie unchanged |
| `camera_anchor_desktop` | Optional client cutscene framing anchor | `x=-225.957,y=68.369,z=129.878,yaw=138.0,pitch=41.5,relative=false` | Confirmed; implemented in `flower_dance_main_event` |
| `camera_anchor_mobile_safe` | Optional tighter framing anchor if needed | Vec3 + yaw/pitch | Needs later design |

## Main Dance Notes

| Item | Data | Status |
| --- | --- | --- |
| Lewis | Unchanged from free phase | Confirmed |
| Marnie | Unchanged from free phase | Confirmed |
| Marlon | `(-228, 60, 109)`, S | Confirmed |
| Jas | `(-231, 60, 110)`, S | Confirmed |
| Pierre | `(-233, 60, 110)`, S | Confirmed |
| Caroline | `(-234, 60, 110)`, S | Confirmed |
| Clint | `(-239, 60, 108)`, S | Confirmed |
| Linus | `(-247, 60, 111)`, E | Confirmed |
| Evelyn | `(-245, 60, 116)`, E | Confirmed |
| George | `(-246, 60, 117)`, E | Confirmed |
| Willy | `(-227, 60, 114)`, W | Confirmed |
| Gus | `(-227, 60, 117)`, W | Confirmed |
| Pam | `(-225, 60, 123)`, W | Confirmed |
| Penny | `(-227, 60, 125)`, W | Confirmed |
| Pair row 1 | S side `z=114`, N side `z=116`; pair X positions `-241, -239, -237, -235, -233, -231` | Confirmed |
| Pair row 2 | S side `z=119`, N side `z=121`; pair X positions `-241, -239, -237, -235, -233, -231` | Confirmed |
| Pair row 3 | S side `z=124`, N side `z=126`; pair X positions `-241, -239, -237, -235, -233, -231` | Confirmed by same row spacing |
| Pair side rule | Female NPCs use S-facing positions; male NPCs use N-facing positions | Confirmed |

## Resolved Rule Decisions

| Rule | Final rule |
| --- | --- |
| Main dance grid mapping | Use source-ordered pair list, then fill the user-provided MC grid row-major: row 1 six pairs, row 2 six pairs, row 3 six pairs. Within each row, X advances by 2 blocks from `-241` to `-231`. |
| Pair capacity | Only the first 18 source-ordered pairs are displayed. Pairs beyond 18 are not shown. |
| Unpaired players/NPC dancers | Assign unpaired players and default dance NPCs whose partner was taken to spectator seats. If spectator seats are full, extras are not shown in the main dance scene. |
| Dance animation budget | Follow the existing plan's A-tier low-budget choreography: fixed formation, face partner, small server-driven step offsets/yaw changes/jumps, music/camera/fade; no custom model animations. |
| Kent | Do not spawn or place Kent for this Flower Dance implementation unless later explicitly requested. |
| Source priority | When not contradicted by user-provided MC constraints, follow the original source behavior from `spring24.json`, `Event.cs`, `Farmer.cs`, `FarmerTeam.cs`, `NPC.cs`, and `NetDancePartner.cs`. |

## Remaining Implementation Work

| Work | Status |
| --- | --- |
| Player-player proposal UI/state | Right-click player opens source-style ask/receive confirmation; accept links both players as dance partners | Implemented首版 |
| NPC partner occupied branch | Once an NPC accepts another player, later players cannot invite them; occupied NPCs use per-NPC `flowerdance_taken` text instead of opening another confirmation | Implemented |
| Free-phase venue bounds / exit trigger | Prompt free-phase exit and keep participants inside the venue | Next allowed scope; blocked on user-confirmed coordinates |
| Main dance cutscene service | Implemented首版: Lewis start confirm, service-selected pairs, client cutscene actors, confirmed camera, end-home flow |
| Flower Dance music asset/sound event | Implemented: `FlowerDance=0000012d`, `music_flower_dance`, `music/flower_dance.ogg`, cutscene track `flower_dance` |
