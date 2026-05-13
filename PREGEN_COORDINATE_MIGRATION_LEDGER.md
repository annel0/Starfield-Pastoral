# Pregen 地图坐标迁移总账本

用途：这个文档是更换星露谷主地图 pregen 后的坐标总账本。你可以直接把新地图里测到的坐标填进“新 X / 新 Y / 新 Z”列，我后面就按这张表去改代码和 JSON，避免靠聊天记录记忆导致漏改。

## 状态说明

- `待测`：还没去新地图测坐标
- `已测`：新坐标已经填了，但还没改代码
- `已应用`：代码或数据已经改成新坐标
- `已验证`：进游戏测试通过
- `跳过`：确认不用改

## 填表规则

- 玩家、NPC、实体落点优先填中心坐标，例如 `150.5, -12, 119.5`。
- 方块、图腾柱、门触发块、轨道、区域角点优先填整数 `BlockPos`。
- 室内亚空间坐标通常不用改，尤其是 `x/z > 10000` 的室内点。除非后面证明确实有 bug。
- 不要删旧坐标。旧坐标用于对照和回滚。
- 如果是区域，在“备注”里写：`min=(x,y,z), max=(x,y,z)`。
- 如果需要朝向，在“朝向/Yaw”里写，比如 `NORTH`、`SOUTH`、`90.0f`。
- 农场没有主地图固定点。`farm` 传送目标必须来自玩家所属农场实例；玩家没有农场时，轮盘按钮置灰不可点击，服务端也拒绝传送。
- 主地图不放置农场系统图腾柱；农场图腾柱只属于玩家自己的农场实例。

## Pregen 资源替换清单

| 项目 | 当前路径 / 当前值 | 新值 | 状态 | 备注 |
|---|---|---|---|---|
| pregen manifest | `src/main/resources/pregen/stardew_valley/region_manifest.txt` |  | 待测 | 必须和新的 `.mca` 文件列表一致。 |
| pregen region 文件 | `src/main/resources/pregen/stardew_valley/region/*.mca` |  | 待测 | 替换旧地图 region。 |
| pregen 安装版本 | `StardewValleyPrebuiltRegionInstaller.CURRENT_PREGEN_VERSION = 3` |  | 待测 | 新地图打包后必须 +1，否则老存档不会覆盖安装。 |
| 主地图 schem fallback | `data/stardewcraft/structures/stardew_valley/main.schem` |  | 待测 | 决定是否继续保留 fallback。 |
| 矿井 schem fallback | `data/stardewcraft/structures/mine/main.schem` |  | 跳过 | 矿井是另一套维度，除非矿井入口也一起改。 |

## 核心传送点

这些会影响传送魔杖、系统图腾、晕倒回家、农场返回等核心流程。

| ID | 含义 | 旧 X | 旧 Y | 旧 Z | 新 X | 新 Y | 新 Z | 朝向/Yaw | 状态 | 要改的文件 | 备注 |
|---|---|---:|---:|---:|---:|---:|---:|---|---|---|---|
| town_warp | 城镇传送落点 | -159.5 | -18.0 | 54.5 |  |  |  |  | 待测 | `WarpDestinations.java` | 现在大致在 Pierre/种子店附近。 |
| mountain_warp | 山区传送落点 | -290.5 | -14.0 | 256.5 |  |  |  |  | 待测 | `WarpDestinations.java` |  |
| beach_warp | 海滩传送落点 | -189.5 | -14.0 | -142.5 |  |  |  |  | 待测 | `WarpDestinations.java` |  |
| mountain_totem_pole | 山区系统图腾柱方块 | -290 | -14 | 256 |  |  |  | NORTH | 待测 | `SystemTotemManager.java` |  |
| beach_totem_pole | 海滩系统图腾柱方块 | -189 | -14 | -142 |  |  |  | NORTH | 待测 | `SystemTotemManager.java` |  |
| desert_totem_pole | 沙漠系统图腾柱方块 | -270 | -41 | 1389 |  |  |  | SOUTH | 待测 | `SystemTotemManager.java` | 看新地图是否包含沙漠入口/沙漠区。 |

## 农场动态传送规则

| ID | 规则 | 状态 | 要改的文件 | 备注 |
|---|---|---|---|---|
| farm_warp_dynamic | 农场传送不使用主地图绝对坐标，目标来自 `FarmInstanceRegistry.getFarmForPlayer(playerUUID)` | 已应用 | `WarpDestinations.java`, `WarpEffects.java`, `WarpWandTeleportPayload.java` | 无农场时服务端拒绝。 |
| farm_warp_disabled_without_farm | 玩家没有自己的农场时，传送轮盘里的农场扇区置灰且不可点击 | 已应用 | `ClientPlayerDataCache.java`, `PlayerDataEventHandler.java`, `WarpWheelScreen.java` | 客户端读取 `HasFarm` 同步字段。 |
| farm_system_totem_removed | 星露谷主地图不自动放置农场系统图腾柱，旧系统柱在加载时清理 | 已应用 | `SystemTotemManager.java` | 玩家农场实例内的农场图腾柱保留。 |

## 矿车站点

| ID | 含义 | 旧 X | 旧 Y | 旧 Z | 新 X | 新 Y | 新 Z | 状态 | 要改的文件 | 备注 |
|---|---|---:|---:|---:|---:|---:|---:|---|---|---|
| minecart_town | 城镇矿车站 | -312 | -17 | -14 |  |  |  | 待测 | `MinecartStationManager.java`, `MinecartMenuService.java` | 实体站点和菜单目标都要对齐。 |
| minecart_bus | 巴士站矿车站 | 85 | -12 | 223 |  |  |  | 待测 | `MinecartStationManager.java`, `MinecartMenuService.java` |  |
| minecart_quarry | 采石场矿车站 | -471 | -13 | 293 |  |  |  | 待测 | `MinecartStationManager.java`, `MinecartMenuService.java` |  |
| minecart_mines | 矿井矿车站 | -7 | 66 | -12 |  |  |  | 跳过 | `MinecartStationManager.java`, `MinecartMenuService.java` | 矿井维度，通常不用跟主地图一起改。 |

## 室外建筑门点和传送锚点

室内点一般先不动。室外门点必须匹配新 pregen 地图。

| ID | 地点 | 旧室外 X | 旧室外 Y | 旧室外 Z | 新室外 X | 新室外 Y | 新室外 Z | 对应室内点 | 状态 | 要改的文件 | 备注 |
|---|---|---:|---:|---:|---:|---:|---:|---|---|---|---|
| seedshop_outdoor_door | Pierre / 种子店 | -159.0 | -18.0 | 54.0 |  |  |  | `12038.5,71.0,12038.5` | 待测 | `npc_route_points.json`, `InteriorSubspaceManager.java`, 商店柜台判定 |  |
| manorhouse_outdoor_door | 镇长家 | -197.0 | -17.0 | -22.0 |  |  |  | `14786.5,71.0,14789.5` | 待测 | `npc_route_points.json`, `InteriorSubspaceManager.java` |  |
| saloon_outdoor_door | 酒吧 | -164.0 | -17.0 | 15.0 |  |  |  | `14210.5,71.0,14225.5` | 待测 | `npc_route_points.json`, `InteriorSubspaceManager.java` |  |
| blacksmith_outdoor_door | 铁匠铺 | -288.0 | -18.0 | -17.0 |  |  |  | `13635.5,71.0,13640.5` | 待测 | `npc_route_points.json`, `InteriorSubspaceManager.java` |  |
| museum_outdoor_door | 博物馆 | -309.0 | -17.0 | -36.0 |  |  |  | `13066.5,71.0,13061.5` | 待测 | `npc_route_points.json`, `InteriorSubspaceManager.java` |  |
| clinic_outdoor_door | 诊所 | -146.0 | -18.0 | 60.0 |  |  |  | `15362.5,71.0,15371.5` | 待测 | `npc_route_points.json`, `InteriorSubspaceManager.java` |  |
| sciencehouse_outdoor_door | 木匠家 / Robin 家 | -213.0 | -12.0 | 219.0 |  |  |  | `16525.5,75.0,16519.5` | 待测 | `npc_route_points.json`, `InteriorSubspaceManager.java` |  |
| fishshop_outer | 鱼店 | -237.0 | -15.0 | -212.0 |  |  |  |  | 待测 | `npc_route_points.json`, `InteriorSubspaceManager.java` |  |
| communitycenter_outer | 社区中心 | -190.0 | -10.0 | 138.0 |  |  |  |  | 待测 | `npc_route_points.json`, 社区中心奖励/过场 |  |

## NPC 室外路线点

先填高频室外点。后面如果 `npc_route_points.json` 里还有别的室外点，再继续加行。

| 点位 ID | 含义 | 旧 X | 旧 Y | 旧 Z | 新 X | 新 Y | 新 Z | 状态 | 要改的文件 | 备注 |
|---|---|---:|---:|---:|---:|---:|---:|---|---|---|
| towngarden | 城镇中心花园 | -206.0 | -18.0 | -24.0 |  |  |  | 待测 | `npc_route_points.json` |  |
| abigail_town_stay | Abigail 城镇停留点 | -167.0 | -18.0 | -27.0 |  |  |  | 待测 | `npc_route_points.json` |  |
| seedshop_outer | 种子店旧室外点 | -159.0 | -18.0 | 54.0 |  |  |  | 待测 | `npc_route_points.json`, `NpcRoutePlanner.java` fallback | 要和 seedshop_outdoor_door 保持一致。 |
| manorhouse_outer | 镇长家旧室外点 | -196.0 | -17.0 | -22.0 |  |  |  | 待测 | `npc_route_points.json`, `NpcRoutePlanner.java` fallback |  |
| saloon_outer | 酒吧旧室外点 | -163.0 | -17.0 | 14.0 |  |  |  | 待测 | `npc_route_points.json`, `NpcRoutePlanner.java` fallback |  |
| communitycenter_outer | 社区中心室外点 | -190.0 | -10.0 | 138.0 |  |  |  | 待测 | `npc_route_points.json`, `NpcRoutePlanner.java` fallback |  |
| fishshop_outer | 鱼店室外点 | -237.0 | -15.0 | -212.0 |  |  |  | 待测 | `npc_route_points.json`, `NpcRoutePlanner.java` fallback |  |

## 农场和主地图连接点

`FarmType.java` 里很多坐标是农场模板内部坐标，不一定跟主地图 pregen 一起改。但农场出口、回家、晕倒、睡醒这些连接必须验证。

| ID | 含义 | 旧值 | 新值 | 状态 | 要改的文件 | 备注 |
|---|---|---|---|---|---|---|
| farm_spawn_by_type | 不同农场类型的出生点 | 看 `FarmType.java` |  | 待测 | `FarmType.java`, `FarmInstanceInitializer.java` | 如果农场模板没变，可能不用动。 |
| farm_to_valley_exit | 农场出口到主地图 |  |  | 待测 | `FarmType.java`, `CrossDimensionTeleporter.java`, `DimensionEventHandler.java` | 填精确触发区和落点。 |
| pass_out_return | 晕倒回家 |  |  | 待测 | `PassOutService.java`, `DimensionEventHandler.java` | 跟农场实例归属有关。 |
| bed_spawn_return | 睡觉/醒来返回 |  |  | 待测 | `SleepInteractionHandler.java`, `StardewTimeManager.java` |  |

## 区域型系统

区域用“备注”填 `min=(x,y,z), max=(x,y,z)`。这些通常不是简单坐标平移，而是要按新地图重新画区域。

| ID | 系统 | 旧坐标来源 | 新区域 / 新点位 | 状态 | 要改的文件 | 备注 |
|---|---|---|---|---|---|---|
| artifact_spots | 古物蚯蚓点 | service 里硬编码/数据 |  | 待测 | `ArtifactSpotSpawnService.java`, `ArtifactDropService.java` | 需要合法可挖表面。 |
| forage_spawns | 野外采集物 | service 里硬编码/数据 |  | 待测 | `ForageSpawnService.java` | 按季节刷新的区域。 |
| quarry_spawns | 采石场生成 | service 里硬编码/数据 |  | 待测 | `QuarrySpawnService.java`, `QuarryAccessManager.java` | 要和采石场解锁区域一致。 |
| coal_forest_clumps | 煤矿森林大块资源 | service 里硬编码/数据 |  | 待测 | `CoalForestClumpSpawnService.java` |  |
| fishing_town | 城镇钓鱼 | fishing 数据 + 判定逻辑 |  | 待测 | `fishing/locations/town.json`, fishing runtime | 新地图水域定下来后填。 |
| fishing_beach | 海滩钓鱼 | fishing 数据 + 判定逻辑 |  | 待测 | `fishing/locations/beach.json`, fishing runtime |  |
| fishing_mountain | 山湖钓鱼 | fishing 数据 + 判定逻辑 |  | 待测 | `fishing/locations/mountain.json`, fishing runtime |  |
| fishing_forest | 森林/秘密森林钓鱼 | fishing 数据 + 判定逻辑 |  | 待测 | `fishing/locations/forest.json`, `woods.json` |  |
| community_center_unlocks | 社区中心修复区域 | 过场/奖励放置逻辑 |  | 待测 | `AreaCompletionService.java`, 社区中心过场 | 要验证每个修复区域。 |

## 事件和过场锚点

| ID | 含义 | 旧来源 | 新坐标 | 状态 | 要改的文件 | 备注 |
|---|---|---|---|---|---|---|
| lewis_cc_tour | Lewis 社区中心参观 | cutscene event JSON / commands |  | 待测 | `cutscene_events/lewis_cc_tour.json`, cutscene runtime | 摄像机、玩家、NPC 锚点都可能要改。 |
| marlon_mine_intro | 矿井介绍 | cutscene event JSON / commands |  | 待测 | `cutscene_events/marlon_mine_intro.json` | 依赖矿井入口。 |
| willy_fishing_rod | Willy 送鱼竿 | cutscene event JSON / commands |  | 待测 | `cutscene_events/willy_fishing_rod.json` | 海滩/鱼店锚点。 |
| wizard_intro | Wizard 初见 | cutscene event JSON / commands |  | 待测 | `cutscene_events/wizard_intro.json` | 法师塔多半是 overworld/interior，但来源/返回点要验证。 |
| morning_events | 晨间事件 | 玩家/农场本地 |  | 待测 | `WakeUpEventScheduler.java`, event JSON | 通常和主地图绝对坐标关系较弱。 |

## 迁移执行清单

1. 替换 `src/main/resources/pregen/stardew_valley/region/*.mca`。
2. 重新生成 `src/main/resources/pregen/stardew_valley/region_manifest.txt`。
3. 增加 `StardewValleyPrebuiltRegionInstaller.CURRENT_PREGEN_VERSION`。
4. 进游戏测点，把新坐标填进本账本。
5. 先改集中坐标表：
   - `WarpDestinations.java`
   - `SystemTotemManager.java`
   - `MinecartStationManager.java`
   - `npc_route_points.json`
   - `InteriorSubspaceManager.java` 里的室外 portal 点
6. 再改区域系统：采集、古物、采石场、钓鱼、社区中心。
7. 再改过场锚点和事件触发条件。
8. 跑 `./gradlew classes --console=plain`。
9. 进游戏按下面清单验证。

## 游戏内验证清单

| 检查项 | 状态 | 备注 |
|---|---|---|
| 新存档能安装新的 pregen region | 待测 |  |
| 老存档能在版本号提升后覆盖旧 pregen | 待测 |  |
| 玩家能进入星露谷维度 | 待测 |  |
| 农场传送落点安全 | 待测 |  |
| 无农场玩家的农场传送按钮置灰且不可点击 | 待测 |  |
| 无农场玩家发送 farm warp 请求会被服务端拒绝 | 待测 |  |
| 星露谷主地图不会自动放置农场系统图腾柱 | 待测 |  |
| 城镇传送落点安全 | 待测 |  |
| 山区传送落点安全 | 待测 |  |
| 海滩传送落点安全 | 待测 |  |
| 沙漠图腾/传送可用 | 待测 |  |
| 系统图腾柱放在正确位置 | 待测 |  |
| 矿车站点生成并可正确旅行 | 待测 |  |
| Pierre/种子店双向传送正常 | 待测 |  |
| 酒吧双向传送正常 | 待测 |  |
| 诊所双向传送正常 | 待测 |  |
| 铁匠铺双向传送正常 | 待测 |  |
| 博物馆双向传送正常 | 待测 |  |
| 社区中心入口和奖励区域正常 | 待测 |  |
| NPC 能走到室外日程点 | 待测 |  |
| NPC 能进出室内 | 待测 |  |
| 古物点只刷在合法表面 | 待测 |  |
| 野外采集物刷在合理区域 | 待测 |  |
| 钓鱼地点识别正常 | 待测 |  |
| 采石场生成/解锁正常 | 待测 |  |
| 晕倒后安全回家 | 待测 |  |
| 睡觉/醒来返回安全 | 待测 |  |
| 过场能在正确锚点触发 | 待测 |  |

## 变更记录

| 日期 | 变更 | 作者 | 备注 |
|---|---|---|---|
| 2026-05-11 | 创建坐标迁移账本 | Copilot | 初版中文表格，用于新 pregen 地图坐标替换。 |
| 2026-05-11 | 农场改为玩家实例动态目标 | Copilot | 移除主地图固定农场传送点和农场系统图腾柱。 |
