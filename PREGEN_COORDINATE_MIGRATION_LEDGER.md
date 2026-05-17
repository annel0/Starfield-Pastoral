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
- 新地图已经把小镇固定室内空间内嵌在主地图下方。固定室内不再运行时放置 `interior/*.schem`，旧的 `x/z > 10000` 坐标只作为对照，全部室内门点/室内落点都要按新地图重测。
- 沙漠已经包含在同一张 pregen 地图里，不再运行时放置 `desert.schem`。沙漠相关传送点、图腾柱、银河柱、区域判定仍要按新地图重测。
- 小镇 pregen 版本、固定室内布局版本、沙漠布局版本统一使用 `StardewValleyPrebuiltRegionInstaller.CURRENT_PREGEN_VERSION` 管理。
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
| pregen 安装版本 | `StardewValleyPrebuiltRegionInstaller.CURRENT_PREGEN_VERSION = 5` |  | 已应用 | 新地图打包后必须 +1，否则老存档不会覆盖安装。 |
| 固定室内 schem 放置 | `InteriorSubspaceManager.FIXED_STRUCTURES` / `interior/*.schem` | pregen 内嵌，不再运行时放置 | 已应用 | 只保留传送触发方块补放和目标注册；坐标待重测。 |
| 沙漠 schem 放置 | `data/stardewcraft/structures/desert.schem` | pregen 内嵌，不再运行时放置 | 已应用 | `DesertMapBootstrap` 只补放沙漠传送触发方块；坐标待重测。 |
| 室内/沙漠布局版本 | `InteriorSubspaceManager.LAYOUT_VERSION = 32`, `DesertMapBootstrap.DESERT_VERSION = 1` | 跟随 `CURRENT_PREGEN_VERSION` | 已应用 | 子系统不再单独维护地图版本号。 |
| 主地图旧 schem 备份 | `data/stardewcraft/structures/stardew_valley/main.schem` |  | 跳过 | 现在进入星露谷依赖 pregen region；旧 schem 不参与启动路径。 |
| 矿井旧 schem 备份 | `data/stardewcraft/structures/mine/main.schem` |  | 跳过 | 矿井是另一套维度，除非矿井入口也一起改。 |

## 核心传送点

这些会影响传送魔杖、系统图腾、晕倒回家、农场返回等核心流程。

| ID | 含义 | 旧 X | 旧 Y | 旧 Z | 新 X | 新 Y | 新 Z | 朝向/Yaw | 状态 | 要改的文件 | 备注 |
|---|---|---:|---:|---:|---:|---:|---:|---|---|---|---|
| town_warp | 城镇传送落点 | -159.5 | -18.0 | 54.5 | 3 | 64 | 8 | N | 待测 | `WarpDestinations.java` | 现在大致在 Pierre/种子店附近。 |
| mountain_warp | 山区传送落点 | -290.5 | -14.0 | 256.5 | 75 | 81 | -104 | S | 待测 | `WarpDestinations.java` |  |
| beach_warp | 海滩传送落点 | -189.5 | -14.0 | -142.5 | 44 | 60 | 94 | S | 待测 | `WarpDestinations.java` |  |
| mountain_totem_pole | 山区系统图腾柱方块 | -290 | -14 | 256 | 75 | 81 | -105 | S | 待测 | `SystemTotemManager.java` |  |
| beach_totem_pole | 海滩系统图腾柱方块 | -189 | -14 | -142 | 44 | 60 | 93 | S | 待测 | `SystemTotemManager.java` |  |
| desert_totem_pole | 沙漠系统图腾柱方块 | -270 | -41 | 1389 | -203 | 64 | -157 | SOUTH | 待测 | `SystemTotemManager.java` | 看新地图是否包含沙漠入口/沙漠区。 |

## 图腾柱可放置区域

玩家放置图腾柱时会校验所在区域。主地图更新后，非农场区域边界也要按新地图重画；农场边界如果农场模板没变可以跳过。

| ID | 含义 | 旧区域 | 新区域 | 状态 | 要改的文件 | 备注 |
|---|---|---|---|---|---|---|
| totem_place_mountain_bounds | 山区图腾柱可放置范围 | `(-537,-33,172)` 到 `(-174,41,372)` | min=(-52,77,-233), max=(140,112,-91) | 待测 | `TotemPoleBlock.java` | `PlacementBounds.of(-174, 41, 172, -537, -33, 372)`。 |
| totem_place_beach_bounds | 海滩图腾柱可放置范围 | `(-464,32,-244)` 到 `(-175,-33,-98)` | min=(-15,59,82), max=(174,76,174) | 待测 | `TotemPoleBlock.java` | `PlacementBounds.of(-464, 32, -98, -175, -33, -244)`。 |
| totem_place_desert_bounds | 沙漠图腾柱可放置范围 | `(-350,36,1300)` 到 `(-200,-50,1470)` | min=(-302,56,-240), max=(-156,86,-112) | 待测 | `TotemPoleBlock.java` | 跟新沙漠区域一致。 |
| totem_place_farm_bounds | 农场图腾柱可放置范围 | `FarmType`/农场模板内部 |  | 跳过 | `TotemPoleBlock.java`, `FarmType.java` | 农场相关坐标通常不跟主地图一起改。 |

## 农场动态传送规则

| ID | 规则 | 状态 | 要改的文件 | 备注 |
|---|---|---|---|---|
| farm_warp_dynamic | 农场传送不使用主地图绝对坐标，目标来自 `FarmInstanceRegistry.getFarmForPlayer(playerUUID)` | 已应用 | `WarpDestinations.java`, `WarpEffects.java`, `WarpWandTeleportPayload.java` | 无农场时服务端拒绝。 |
| farm_warp_disabled_without_farm | 玩家没有自己的农场时，传送轮盘里的农场扇区置灰且不可点击 | 已应用 | `ClientPlayerDataCache.java`, `PlayerDataEventHandler.java`, `WarpWheelScreen.java` | 客户端读取 `HasFarm` 同步字段。 |
| farm_system_totem_removed | 星露谷主地图不自动放置农场系统图腾柱，旧系统柱在加载时清理 | 已应用 | `SystemTotemManager.java` | 玩家农场实例内的农场图腾柱保留。 |

## 矿车站点

| ID | 含义 | 旧 X | 旧 Y | 旧 Z | 新 X | 新 Y | 新 Z | 状态 | 要改的文件 | 备注 |
|---|---|---:|---:|---:|---:|---:|---:|---|---|---|
| minecart_town | 城镇矿车站 | -312 | -17 | -14 | 123 | 64 | 26 | 待测 | `MinecartStationManager.java`, `MinecartMenuService.java` | 实体站点和菜单目标都要对齐。 |
| minecart_bus | 巴士站矿车站 | 85 | -12 | 223 | -76 | 64 | -70 | 待测 | `MinecartStationManager.java`, `MinecartMenuService.java` |  |
| minecart_quarry | 采石场矿车站 | -471 | -13 | 293 | 187 | 81 | -141 | 待测 | `MinecartStationManager.java`, `MinecartMenuService.java` |  |
| minecart_mines | 矿井矿车站 | -7 | 66 | -12 |  |  |  | 跳过 | `MinecartStationManager.java`, `MinecartMenuService.java` | 矿井维度，通常不用跟主地图一起改。 |

## 沙漠点位和区域

沙漠已内嵌到同一张 pregen 地图，不再放置 `desert.schem`，但这些运行时传送块、区域判定、银河柱和音乐/采集识别仍然依赖坐标。

| ID | 含义 | 旧 X | 旧 Y | 旧 Z | 新 X | 新 Y | 新 Z | 状态 | 要改的文件 | 备注 |
|---|---|---:|---:|---:|---:|---:|---:|---|---|---|
| desert_bounds | 沙漠整体 XZ 区域 | -466..-169 |  | 1160..1483 | -302..-156 | 56..86 | -240..-112 | 待测 | `DesertConstants.java`, `StardewBiomePatcher.java`, `StardewMusicManager.java` | 备注填 `min=(-302,56,-240), max=(-156,86,-112)`；影响沙漠天气/音乐/生物群系/区域识别。 |
| desert_bus_arrival | 坐公交到沙漠落点 | -350 | -43 | 1322 | -225 | 64 | -177 | 待测 | `DesertConstants.java`, `DesertBusService.java` | 旧值来自 `DESERT_ORIGIN + ARRIVAL_OFFSET`。 |
| desert_bus_direction_threshold | 公交方向判断规则 | x < -100 |  |  |  |  |  | 跳过 | `DesertBusService.java` | 已改为根据 `desert_bounds` 判断玩家是否在沙漠内，不再需要录入 X 阈值。 |
| desert_bus_portal_town_side | 鹈鹕镇公交牌去沙漠触发区 | 81 | -12 | 202 | -66..-65 | 64..65 | -60..-59 | 待测 | `DesertConstants.java`, `DesertMapBootstrap.java` | 旧区域 size `4x4x4`；hint 由实际 PortalTrigger 方块区域自动生成。 min=(-66,64,-60), max=(-65,65,-59) |
| desert_bus_return_portal | 沙漠返程公交触发区 | -351 | -42 | 1316 | -225..-225 | 65..66 | -179..-179 | 待测 | `DesertConstants.java`, `DesertMapBootstrap.java` | 旧区域 size `2x2x1`；hint 由实际 PortalTrigger 方块区域自动生成。 min=(-225,65,-179), max=(-225,66,-179) |
| desert_town_return_arrival | 沙漠返程到鹈鹕镇落点 | 71 | -12 | 207 | -60 | 64 | -61 | 已应用 | `DesertConstants.java`, `DesertBusService.java` | 朝南，yaw=0.0F |
| skull_cavern_exit_desert | 骷髅矿大厅退出到沙漠落点 | -339 | -42 | 1268 | -246 | 64 | -213 | 待测 | `DesertConstants.java`, `InteriorPortalInteractionEvents.java`, `MineExitActionPayload.java` | 朝南 |
| desert_mine_portal | 沙漠骷髅矿入口触发区 | -340 | -42 | 1266 | -247..-246 | 64..64 | -215..-215 | 待测 | `DesertConstants.java`, `DesertMapBootstrap.java` | 旧区域 size `3x3x1`；hint 由实际 PortalTrigger 方块区域自动生成。 min=(-247,64,-215), max=(-246,64,-215) |
| oasis_outdoor_portal | Oasis 室外入口触发区 | -360 | -40 | 1414 | -251..-251 | 64..65 | -142..-142 | 待测 | `DesertConstants.java`, `DesertMapBootstrap.java` | 旧区域 size `2x2x1`；hint 由实际 PortalTrigger 方块区域自动生成。 min=(-251,64,-142), max=(-251,65,-142) |
| oasis_indoor_spawn | Oasis 室内落点 | 18242 | 71 | 17668 | -252.5 | 30.5 | -147.5 | 待测 | `DesertConstants.java`, `InteriorSubspaceManager.java`, `base_locations.json` | 朝北 |
| oasis_indoor_exit_portal | Oasis 室内出口触发区 | 18241 | 71 | 17668 | -253..-251 | 30..32 | -146..-146 | 待测 | `DesertConstants.java` | 旧区域 size `1x2x1`；hint 由实际 PortalTrigger 方块区域自动生成。 min=(-253,30,-146), max=(-251,32,-146); outdoor landing=(-251,64,-141), yaw=SOUTH |
| desert_galaxy_pillars | 银河柱三根柱子和仪式触发点 | -244/-241/-247 | -43 | 1339/1344/1342 | -201/-198/-195/-198 | 64/64/64/64 | -200/-205/-200/-202 | 待测 | `DesertGalaxyPillarBootstrap.java` | 要分别填 north/east/west/trigger 四个点。 north=(-201,64,-200), east=(-198,64,-205), west=(-195,64,-200), trigger=(-198,64,-202) |

## 固定建筑门点、室内落点和提示点

固定室内现在内嵌在主地图下方，所以室外门点、室内入口落点、室内出口触发块、客户端传送提示和 NPC base location 都要对齐新地图。这里不是“室内锚点/室内区域”概念，只是固定建筑自身的门点和落点。

NPC 进出固定建筑时复用这些玩家门点：先走到本表的室外 portal/exit landing，传送到同一 `portal_target` 的室内落点，再走向 NPC 专属停留点。不要再为 NPC 共用门点单独录一套坐标。

社区中心不按固定室内迁移处理：社区中心仍是一人一个的玩家室内实例，不受这轮小镇固定室内新坐标影响，社区中心系统坐标不在本表重填。NPC 在社区中心外面的普通室外日程点仍按下方 `npc_route_points` 明细逐项处理。

| ID | 归属/用途 | 地点 | 旧室外 X | 旧室外 Y | 旧室外 Z | 新室外 X | 新室外 Y | 新室外 Z | 对应室内点 | 状态 | 要改的文件 | 备注 |
| --- | --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | --- | --- | --- | --- |
| fixed_building_portal_hints_full | 汇总/跳过 | 全部建筑传送提示点 | 见文件 |  |  |  |  |  | 逐项录入 | 跳过 | 已删除 `PortalHintPositions.java` | hint 改为扫描实际 PortalTrigger 方块区域自动生成，不再维护独立提示坐标表。 |
| fixed_building_music_full | 音乐区域汇总 | 固定室内音乐识别点/区域汇总 | 旧 `x/z=12032..18816` |  |  |  |  |  | 看下方“固定室内音乐区域” | 跳过 | `StardewMusicManager.java` | 这是汇总提醒，不用填。真正要测的是下方每个 `music_region_*` 的 `新区域`。 |
| npc_base_locations_full | NPC anchor 汇总 | NPC 基础地点 anchors 汇总 | 见文件 |  |  |  |  |  | 看下方“NPC 基础地点 anchors” | 跳过 | `npc/location_mappings/base_locations.json` | 这是汇总提醒，不用填；下方只有状态仍为“待测”的非 portal anchor 需要填。 |
| debug_indoor_teleport | 调试命令 | 调试传送到内嵌室内测试点 | 12032.5 | 70.0 | 12032.5 | 21.6 | 36.5 | -13.5 | 建议填 Pierre/种子店室内可站点 | 待测 | `StardewTeleportCommand.java` | 只填一个点，用来替换 `/stardew tp interior origin` 旧坐标；不是区域，也不用逐项录入。 |
| seedshop_outdoor_door | 通用：SeedShop | Pierre / 种子店 | -159.0 | -18.0 | 54.0 | 27..27 | 65..66 | -8..-8 | entry=(21,36,-12), entryYaw=NORTH; exitTrigger min=(20,36,-11), max=(22,38,-11); outdoorLanding=(27,64,-6), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java`, 商店柜台判定 | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(27,65,-8), max=(27,66,-8) |
| manorhouse_outdoor_door | 通用：ManorHouse | 镇长家 | -197.0 | -17.0 | -22.0 | 50..51 | 66..67 | 34..34 | entry=(54,50,29), entryYaw=NORTH; exitTrigger min=(53,50,30), max=(55,52,30); outdoorLanding=(50,64,37), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(50,66,34), max=(51,67,34) |
| saloon_outdoor_door | 通用：Saloon | 酒吧 | -164.0 | -17.0 | 15.0 | 29..30 | 66..67 | 14..14 | entry=(26,36,18), entryYaw=NORTH; exitTrigger min=(25,36,19), max=(27,38,19); outdoorLanding=(30,64,16), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(29,66,14), max=(30,67,14) |
| blacksmith_outdoor_door | 通用：Blacksmith | 铁匠铺 | -288.0 | -18.0 | -17.0 | 108..108 | 64..65 | 29..29 | entry=(107,46,30), entryYaw=NORTH; exitTrigger min=(106,46,31), max=(108,48,31); outdoorLanding=(108,64,30), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(108,64,29), max=(108,65,29) |
| museum_outdoor_door | 通用：Museum | 博物馆 | -309.0 | -17.0 | -36.0 | 124..124 | 64..65 | 42..42 | entry=(112,38,46), entryYaw=NORTH; exitTrigger min=(111,38,47), max=(113,40,47); outdoorLanding=(124,64,43), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(124,64,42), max=(124,65,42) |
| clinic_outdoor_door | 通用：Clinic | 诊所 | -146.0 | -18.0 | 60.0 | 13..13 | 65..66 | -8..-8 | entry=(9,43,-11), entryYaw=NORTH; exitTrigger min=(8,43,-10), max=(10,45,-10); outdoorLanding=(13,64,-6), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(13,65,-8), max=(13,66,-8) |
| riverroad1_outdoor_door | 通用：JoshHouse | 1 River Road / Alex 家 | -195.0 | -18.0 | 32.0 | 48..48 | 64..65 | 1..1 | entry=(48,22,4), entryYaw=NORTH; exitTrigger min=(47,22,5), max=(49,24,5); outdoorLanding=(48,64,2), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java`, `base_locations.json` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(48,64,1), max=(48,65,1) |
| sciencehouse_outdoor_door | 通用：ScienceHouse | 木匠家 / Robin 家 | -213.0 | -12.0 | 219.0 | 28..29 | 85..86 | -115..-115 | entry=(30,51,-117), entryYaw=NORTH; exitTrigger min=(29,51,-116), max=(31,53,-116); outdoorLanding=(29,85,-113), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(28,85,-115), max=(29,86,-115) |
| samhouse_outdoor_door | 通用：SamHouse | 1 Willow Lane / Sam 家 | -85.0 | -16.0 | -25.0 | -29..-28 | 65..66 | 39..39 | entry=(-28,38,43), entryYaw=NORTH; exitTrigger min=(-29,38,44), max=(-27,40,44); outdoorLanding=(-28,64,42), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java`, `base_locations.json` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(-29,65,39), max=(-28,66,39) |
| haleyhouse_outdoor_door | 通用：HaleyHouse | 2 Willow Lane / Haley 家 | -115.0 | -17.0 | -27.0 | -11..-10 | 64..65 | 39..39 | entry=(-10,24,40), entryYaw=NORTH; exitTrigger min=(-11,24,41), max=(-9,26,41); outdoorLanding=(-10,64,40), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java`, `base_locations.json` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(-11,64,39), max=(-10,65,39) |
| animalshop_outdoor_door | 通用：AnimalShop | Marnie 牧场 | 178.0 | -14.0 | -4.0 | -92..-91 | 64..65 | 21..21 | entry=(-86,34,25), entryYaw=NORTH; exitTrigger min=(-87,34,26), max=(-85,36,26); outdoorLanding=(-92,64,23), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java`, `base_locations.json` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(-92,64,21), max=(-91,65,21) |
| leahhouse_outdoor_door | 通用：LeahHouse | Leah 小屋 | 155.0 | -13.0 | -58.0 | -83..-82 | 64..64 | 52..52 | entry=(-83,38,51), entryYaw=NORTH; exitTrigger min=(-84,38,52), max=(-82,40,52); outdoorLanding=(-83,64,53), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java`, `base_locations.json` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(-83,64,52), max=(-82,64,52) |
| adventurer_guild_outdoor_door | 通用：AdventurerGuild | 冒险者公会 | -335.0 | -13.0 | 312.0 | 106..106 | 81..82 | -142..-142 | entry=(106,60,-143), entryYaw=NORTH; exitTrigger min=(105,60,-142), max=(107,62,-142); outdoorLanding=(106,81,-141), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java`, `base_locations.json` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(106,81,-142), max=(106,82,-142) |
| fishshop_outer | 通用：FishShop | 鱼店 / Willy 店 | -237.0 | -15.0 | -212.0 | 64..65 | 60..61 | 150..150 | entry=(65,31,147), entryYaw=NORTH; exitTrigger min=(64,31,148), max=(66,33,148); outdoorLanding=(64,60,151), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java`, `base_locations.json` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域；录入时统一门区和 NPC anchor。 min=(64,60,150), max=(65,61,150) |
| elliott_cabin_outdoor_door | 通用：ElliottHouse | Elliott 小屋 | -267.0 | -13.0 | -152.0 | 80..81 | 60..61 | 101..101 | entry=(75,45,102), entryYaw=NORTH; exitTrigger min=(74,45,103), max=(76,47,103); outdoorLanding=(80,60,102), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java`, `base_locations.json` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(80,60,101), max=(81,61,101) |
| wizard_tower_outdoor_door | 通用：WizardTower | 法师塔外门/星露谷入口 | 340.0 | -1.0 | -42.0 | -179..-179 | 69..70 | 50..50 | entry=(-178,34,63), entryYaw=NORTH; exitTrigger min=(-179,34,64), max=(-177,36,64); outdoorLanding=(-179,69,51), outdoorYaw=SOUTH; overworldReturnTrigger min=(-175,34,54), max=(-173,36,56) | 待测 | `InteriorSubspaceManager.java`, `CrossDimensionTeleporter.java`, `base_locations.json`, wizard 过场 | portal 触发区；hint 自动跟随 PortalTrigger 方块区域；还要核对 overworld 法师塔往返。 min=(-179,69,50), max=(-179,70,50) |
| joja_mart_outdoor_door | 通用：JojaMart | Joja Mart | -294.0 | -16.0 | 59.0 | 108..109 | 65..66 | -17..-17 | entry=(108,45,-19), entryYaw=NORTH; exitTrigger min=(108,45,-17), max=(109,47,-17); outdoorLanding=(109,65,-15), outdoorYaw=SOUTH | 待测 | `InteriorSubspaceManager.java`, `base_locations.json` | portal 触发区；hint 自动跟随 PortalTrigger 方块区域。 min=(108,65,-17), max=(109,66,-17) |
| trailer_outdoor_door | 通用：Trailer | Pam / Penny 拖车 | -231.0 | -18.0 | 20.0 | 72 | 64 | 9 | 由下方 `trailer_indoor_entry` 录入 | 待测 | `InteriorSubspaceManager.java`, `base_locations.json` | 拖车室外 portal 触发区；坐标来自 MD 录入，填完室内落点后再同步到运行时代码。 |
| communitycenter_outer | 通用：CommunityCenter | 社区中心 | -190.0 | -10.0 | 138.0 | 54..54 | 67..68 | -49..-49 | `18833,70,18853` | 跳过 | 不改社区中心玩家实例系统 | 社区中心仍是一人一个的玩家室内实例，不纳入固定室内坐标迁移；NPC 室外活动点另看 `npc_route_points`。 |
| mine_entrance_outdoor | 矿井入口 | 普通矿井入口触发区 | -287 | -13 | 314 | 83..84 | 81..83 | -146..-146 | 矿井维度入口 | 待测 | `InteriorPortalInteractionEvents.java`, `MineEntranceBootstrap.java`, `marlon_mine_intro.json` | 旧提示区 size `4x3x1`；hint 由实际 PortalTrigger 方块区域自动生成。 min=(83,81,-146), max=(84,83,-146) |
| mine_exit_to_town | 矿井返回 | 普通矿井退出回主地图落点 | -285.5 | -12.0 | 314.5 | 84 | 81 | -145 | `180.0f` | 待测 | `InteriorPortalInteractionEvents.java`, `MineExitMenu.java` | 从矿井维度返回星露谷主地图。 |

## Trailer 固定室内传送点

这些行专门给拖车统一地图室内录入传送相关点位。不要从原版 tile、region 或旧室外点推算实际坐标；所有 `新 X/Y/Z` 都由你在 MD 面板里统一填写，填完后再同步到运行时代码和 JSON。

| ID | 用途 | 地点 | 旧 X | 旧 Y | 旧 Z | 新 X | 新 Y | 新 Z | 状态 | 要改的文件 | 备注 |
| --- | --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | --- | --- | --- |
| trailer_outdoor_landing | 室外返回落点 | Pam / Penny 拖车 | -231.0 | -18.0 | 20.0 | 73 | 64 | 9 | 待测 | `InteriorSubspaceManager.java` | 从拖车室内出来后落在室外的站位；由 MD 录入。 |
| trailer_indoor_entry | 室内进入落点 | Pam / Penny 拖车 |  |  |  | 72 | 35 | 4 | 待测 | `InteriorSubspaceManager.java`, `base_locations.json` | 从室外进入拖车后的室内站位；`npc_base_trailer` 后续从 `trailer_enter` 派生，不单独猜坐标。 |
| trailer_indoor_exit_trigger | 室内出口触发区 | Pam / Penny 拖车 |  |  |  | 71..73 | 35..37 | 5..5 | 待测 | `InteriorSubspaceManager.java` | 室内门/出口触发块区域；可填单点或范围，全部由 MD 录入。 min=(71,35,5), max=(73,37,5) |

## 固定室内音乐区域

这些不是门点，是玩家站在室内时用于判断音乐/静音的区域。每行填这个室内空间的完整可活动边界，在面板里用“粘贴区域两角点”。如果原版音乐是“无”，也要测区域；命中后会停止室外/季节音乐，保持原版室内无音乐逻辑。

| ID | 地点 | 旧区域 | 新区域 | 原版音乐 | 状态 | 要改的文件 | 备注 |
|---|---|---|---|---|---|---|---|
| music_region_pierre_house | Pierre / 种子店 | min=(12032,0,12032), max=(12607,255,12607) | min=(16,35,-44), max=(60,40,-7) | 无 | 待测 | `StardewMusicManager.java` | 原版 `SeedShop MusicDefault=null`，不是 `seeds` 音效。 |
| music_region_museum | 博物馆 | min=(13056,0,13056), max=(13631,255,13631) | min=(108,37,29), max=(178,41,56) | `libraryTheme` | 待测 | `StardewMusicManager.java` |  |
| music_region_blacksmith | 铁匠铺 | min=(13632,0,13632), max=(14207,255,14207) | min=(100,45,11), max=(115,50,31) | 无 | 待测 | `StardewMusicManager.java` |  |
| music_region_saloon | 酒吧 | min=(14208,0,14208), max=(14783,255,14783) | min=(15,35,-2), max=(58,40,19) | `Saloon1`，17:00 后 | 待测 | `StardewMusicManager.java` | 17:00 前按原版不强制室内音乐。 |
| music_region_mayor_house | 镇长家 | min=(14784,0,14784), max=(15359,255,15359) | min=(48,50,20), max=(74,57,32) | 无 | 待测 | `StardewMusicManager.java` |  |
| music_region_clinic | 诊所 | min=(15360,0,15360), max=(15935,255,15935) | min=(0,43,-37), max=(28,51,-10) | `distantBanjo` | 待测 | `StardewMusicManager.java` | 不要填成 `Hospital_Ambient`。 |
| music_region_1_river_road | 1 River Road / Alex 家 | min=(15936,0,15936), max=(16511,255,16511) | min=(37,22,-19), max=(64,26,5) | 无 | 待测 | `StardewMusicManager.java` |  |
| music_region_carpenter_shop | 木匠家 / Robin 家 | min=(16512,0,16512), max=(17087,255,17087) | min=(20,45,-132), max=(63,61,-102) | `marnieShop` | 待测 | `StardewMusicManager.java` | 包含 Sebastian 房间时先按同一室内区域处理。 |
| music_region_1_willow_lane | 1 Willow Lane / Sam 家 | min=(17088,0,17088), max=(17663,255,17663) | min=(-32,38,18), max=(-6,42,44) | 无 | 待测 | `StardewMusicManager.java` |  |
| music_region_2_willow_lane | 2 Willow Lane / Haley 家 | min=(17088,0,17664), max=(17663,255,18239) | min=(-11,23,16), max=(14,29,42) | 无 | 待测 | `StardewMusicManager.java` |  |
| music_region_marnie_ranch | Marnie 牧场 | min=(17088,0,18240), max=(17663,255,18815) | min=(-101,32,8), max=(-66,39,26) | `marnieShop` | 待测 | `StardewMusicManager.java` |  |
| music_region_leah_cottage | Leah 小屋 | min=(17088,0,18816), max=(17663,255,19391) | min=(-90,37,46), max=(-75,42,57) | `distantBanjo` | 待测 | `StardewMusicManager.java` |  |
| music_region_adventurer_guild | 冒险者公会 | min=(17664,0,17088), max=(18239,255,17663) | min=(101,60,-158), max=(113,64,-142) | `MarlonsTheme` | 待测 | `StardewMusicManager.java` |  |
| music_region_fish_shop | 鱼店 / Willy 店 | min=(17664,0,17664), max=(18239,255,18239) | min=(52,25,122), max=(76,41,150) | 无 | 待测 | `StardewMusicManager.java` |  |
| music_region_elliott_cabin | Elliott 小屋 | min=(17664,0,18240), max=(18239,255,18815) | min=(73,44,96), max=(86,48,103) | `communityCenter` | 待测 | `StardewMusicManager.java` | 原版该地点映射如此，先按源数据保留。 |
| music_region_wizard_tower | 法师塔 | min=(18240,0,17088), max=(18815,255,17663) | min=(-186,33,43), max=(-172,40,65) | `WizardSong` | 待测 | `StardewMusicManager.java` |  |
| music_region_oasis | Oasis | min=(18240,0,17664), max=(18815,255,18239) | min=(-255,29,-151), max=(-237,34,-146) | `distantBanjo` | 待测 | `StardewMusicManager.java` |  |
| music_region_joja_mart | Joja Mart | min=(18240,0,18240), max=(18815,255,18815) | min=(96,43,-42), max=(123,50,-17) | `Hospital_Ambient` | 待测 | `StardewMusicManager.java` |  |

## NPC 基础地点 anchors

这些是 `base_locations.json` 的地点级 `anchors`，不是“某个 NPC 的日程点”。实际日程里带 `@点位ID` 的条目会优先使用下方 `npc_route_points` 明细；NPC 路线点表的“朝向/Yaw”已从 schedule 里的 `facing` 数字反查展示，地点级 anchor 本身不填朝向。固定建筑 anchor 现在由 `portal_target` 的玩家 portal/landing 自动派生，状态为“跳过”的 `npc_base_*` 不用手填；只有没有 portal 的室外大区代表点还需要填新地图安全站位。

| ID | 归属/用途 | 地点 | 怎么填 | 实际使用 | 旧 X | 旧 Y | 旧 Z | 新 X | 新 Y | 新 Z | 朝向/Yaw | 室内 | 状态 | 要改的文件 | 备注 |
| --- | --- | --- | --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | --- | --- | --- | --- | --- |
| npc_base_town | 地点级 anchor（多人共用） | town | 需要填：城镇室外代表安全站位；不是具体 NPC 停留点。 | 直接无@日程=无；@点位日程=23；profile=abigail/alex/caroline/clint/demetrius 等27人 | -221.5 | -18.0 | -34.5 | 3 | 64 | 8 | 不适用（地点级 anchor） | false | 待测 | `npc/location_mappings/base_locations.json` | 城镇代表点；使用地面高度。常规停留点看 npc_route_points。 |
| npc_base_seedshop | portal 派生 anchor（不用填） | seedshop | 不用填；运行时从 `pierre_house_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=12；profile=abigail/caroline/elliott/emily/george 等12人 | 12032.5 | 71.0 | 12032.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`pierre_house_enter`；door/entry 也走玩家 portal。 |
| npc_base_manorhouse | portal 派生 anchor（不用填） | manorhouse | 不用填；运行时从 `mayor_house_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=1；profile=lewis | 14786.5 | 71.0 | 14789.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`mayor_house_enter`；door/entry 也走玩家 portal。 |
| npc_base_hospital | portal 派生 anchor（不用填） | hospital | 不用填；运行时从 `clinic_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=4；profile=abigail/caroline/clint/harvey/lewis 等6人 | 15362.5 | 71.0 | 15371.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`clinic_enter`；door/entry 也走玩家 portal。 |
| npc_base_animalshop | portal 派生 anchor（不用填） | animalshop | 不用填；运行时从 `marnie_ranch_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=4；profile=jas/lewis/marnie/shane | 17090.5 | 71.0 | 18255.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`marnie_ranch_enter`；door/entry 也走玩家 portal。 |
| npc_base_archaeologyhouse | portal 派生 anchor（不用填） | archaeologyhouse | 不用填；运行时从 `museum_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=11；profile=abigail/caroline/elliott/harvey/jas 等10人 | 13066.5 | 71.0 | 13061.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`museum_enter`；door/entry 也走玩家 portal。 |
| npc_base_adventurer_guild | portal 派生 anchor（不用填） | adventurer_guild | 不用填；运行时从 `adventurer_guild_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=1；profile=无 | 17664.5 | 71.0 | 17088.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`adventurer_guild_enter`。 |
| npc_base_oasis | portal 派生 anchor（不用填） | oasis | 不用填；运行时从 `oasis_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=1；profile=无 | 18245.5 | 71.0 | 17666.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`oasis_enter`。 |
| npc_base_sciencehouse | portal 派生 anchor（不用填） | sciencehouse | 不用填；运行时从 `carpenter_shop_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=6；profile=abigail/demetrius/lewis/maru/robin 等6人 | 16525.5 | 75.0 | 16519.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`carpenter_shop_enter`；door/entry 也走玩家 portal。 |
| npc_base_sebastianroom | portal 派生 anchor（不用填） | sebastianroom | 不用填；运行时从 `carpenter_shop_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=1；profile=abigail/sebastian | 16519.5 | 71.0 | 16535.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`carpenter_shop_enter`；具体房间停留点看 npc_route_points。 |
| npc_base_communitycenter | 地点级 anchor（多人共用） | communitycenter | 需要填：社区中心室外代表安全站位；不是门触发区。 | 直接无@日程=无；@点位日程=6；profile=caroline/clint/emily/evelyn/gus 等6人 | -190.0 | -10.0 | 138.0 | 54 | 66 | -45 | 不适用（地点级 anchor） | false | 待测 | `npc/location_mappings/base_locations.json` | 社区中心每玩家实例另算；这里只是 NPC 室外代表点。 |
| npc_base_railroad | 地点级 anchor（多人共用） | railroad | 需要填：铁路室外代表安全站位。 | 直接无@日程=无；@点位日程=1；profile=abigail | -93.0 | -13.0 | 341.0 | 5 | 85 | -193 | 不适用（地点级 anchor） | false | 待测 | `npc/location_mappings/base_locations.json` | 使用地面高度。 |
| npc_base_saloon | portal 派生 anchor（不用填） | saloon | 不用填；运行时从 `saloon_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=21；profile=abigail/alex/clint/demetrius/elliott 等23人 | 14210.5 | 71.0 | 14225.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`saloon_enter`；door/entry 也走玩家 portal。 |
| npc_base_mountain | 地点级 anchor（多人共用） | mountain | 需要填：山区室外代表安全站位。 | 直接无@日程=无；@点位日程=4；profile=abigail/demetrius/linus/sebastian | -284.0 | -11.0 | 225.0 | 84 | 81 | -103 | 不适用（地点级 anchor） | false | 待测 | `npc/location_mappings/base_locations.json` | 使用地面高度。 |
| npc_base_beach | 地点级 anchor（多人共用） | beach | 需要填：海滩室外代表安全站位。 | 直接无@日程=无；@点位日程=13；profile=abigail/alex/caroline/clint/elliott 等16人 | -196.0 | -15.0 | -158.0 | 73 | 60 | 109 | 不适用（地点级 anchor） | false | 待测 | `npc/location_mappings/base_locations.json` | 使用地面高度。 |
| npc_base_forest | 地点级 anchor（多人共用） | forest | 需要填：森林室外代表安全站位。 | 直接无@日程=无；@点位日程=9；profile=abigail/elliott/haley/jas/leah 等9人 | 177.0 | -16.0 | -148.0 | -109 | 64 | 83 | 不适用（地点级 anchor） | false | 待测 | `npc/location_mappings/base_locations.json` | 使用地面高度。 |
| npc_base_busstop | 地点级 anchor（多人共用） | busstop | 需要填：公交站室外代表安全站位。 | 直接无@日程=无；@点位日程=2；profile=abigail/pam | 75.0 | -12.0 | 201.0 | -70 | 64 | -59 | 不适用（地点级 anchor） | false | 待测 | `npc/location_mappings/base_locations.json` | 使用地面高度。 |
| npc_base_fishshop | portal 派生 anchor（不用填） | fishshop | 不用填；运行时从 `fish_shop_exit` 派生室外门外落点。 | 直接无@日程=无；@点位日程=2；profile=lewis/willy | -237.0 | -15.0 | -212.0 |  |  |  | 不适用（地点级 anchor） | false | 跳过 | `npc/location_mappings/base_locations.json` | portal=`fish_shop_enter`；NPC 具体停留点看 npc_route_points。 |
| npc_base_samhouse | portal 派生 anchor（不用填） | samhouse | 不用填；运行时从 `1_willow_lane_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=4；profile=jodi/sam/sebastian/vincent | 17090.5 | 71.0 | 17093.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`1_willow_lane_enter`；door/entry 也走玩家 portal。 |
| npc_base_jojamart | portal 派生 anchor（不用填） | jojamart | 不用填；运行时从 `joja_mart_exit` 派生室外门外落点。 | 直接无@日程=无；@点位日程=0；profile=无 | -292.0 | -16.0 | 59.0 |  |  |  | 不适用（地点级 anchor） | false | 跳过 | `npc/location_mappings/base_locations.json` | portal=`joja_mart_enter`；当前未见日程引用。 |
| npc_base_blacksmith | portal 派生 anchor（不用填） | blacksmith | 不用填；运行时从 `blacksmith_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=2；profile=clint/lewis | 13635.5 | 71.0 | 13640.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`blacksmith_enter`；door/entry 也走玩家 portal。 |
| npc_base_elliotthouse | portal 派生 anchor（不用填） | elliotthouse | 不用填；运行时从 `elliott_cabin_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=1；profile=elliott | 17666.5 | 71.0 | 18243.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`elliott_cabin_enter`；door/entry 也走玩家 portal。 |
| npc_base_haleyhouse | portal 派生 anchor（不用填） | haleyhouse | 不用填；运行时从 `2_willow_lane_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=3；profile=alex/emily/haley | 17092.5 | 71.0 | 17666.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`2_willow_lane_enter`；door/entry 也走玩家 portal。 |
| npc_base_joshhouse | portal 派生 anchor（不用填） | joshhouse | 不用填；运行时从 `1_river_road_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=3；profile=alex/evelyn/george | 15939.5 | 71.0 | 15948.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`1_river_road_enter`；door/entry 也走玩家 portal。 |
| npc_base_trailer | 地点级 anchor（多人共用） | trailer | 需要填：拖车室外代表安全站位。 | 直接无@日程=无；@点位日程=2；profile=pam/penny | -231.0 | -18.0 | 20.0 | 72 | 64 | 9 | 不适用（地点级 anchor） | false | 待测 | `npc/location_mappings/base_locations.json` | 使用地面高度。 |
| npc_base_leahhouse | portal 派生 anchor（不用填） | leahhouse | 不用填；运行时从 `leah_cottage_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=1；profile=leah | 17094.5 | 71.0 | 18823.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`leah_cottage_enter`；door/entry 也走玩家 portal。 |
| npc_base_wizard_tower | portal 派生 anchor（不用填） | wizard_tower | 不用填；运行时从 `wizard_tower_enter` 派生室内落点。 | 直接无@日程=无；@点位日程=1；profile=无 | 18249.5 | 71.0 | 17095.5 |  |  |  | 不适用（地点级 anchor） | true | 跳过 | `npc/location_mappings/base_locations.json` | portal=`wizard_tower_enter`；overworld 往返另看固定建筑门点。 |

## NPC 路线点和基础地点

`npc_route_points.json` 里的点位已经拆成明细行。固定建筑的共用进出门点由玩家 portal/landing 派生，不出现在本表；其余 NPC 专属停留点、室外活动点仍在本表逐项填写。“朝向/Yaw”来自 `npc/schedules/*.json` 的 `facing`：`0=北/N`、`1=东/E`、`2=南/S`、`3=西/W`；同一点被多个日程用不同朝向时会列出多个值。

| 点位 ID | 归属 NPC/用途 | 含义 | 旧 X | 旧 Y | 旧 Z | 新 X | 新 Y | 新 Z | 朝向/Yaw | 状态 | 要改的文件 | 备注 |
| --- | --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | --- | --- | --- | --- |
| npc_route_points_full_file | 汇总/跳过 | NPC 路线点全文件汇总 | 见文件 |  |  |  |  |  | 不适用 | 跳过 | `npc/events/npc_route_points.json` | 汇总提醒，不用填；本文件点位已拆成下方明细行，部分门点在固定建筑门点表中填写。 |
| npc_base_locations_full_file | 汇总/跳过 | NPC 地点 anchors 全文件汇总 | 见文件 |  |  |  |  |  | 不适用 | 跳过 | `npc/location_mappings/base_locations.json` | 汇总提醒，不用填；只有上方状态仍为“待测”的非 portal anchor 需要填。 |
| towngarden | 通用：Town | 城镇中心花园 | -206.0 | -18.0 | -24.0 | 27 | 64 | 27 | S | 待测 | `npc_route_points.json` |  |
| abigail_town_stay | Abigail | Abigail 城镇停留点 | -167.0 | -18.0 | -27.0 | 14 | 64 | 51 | N | 待测 | `npc_route_points.json` |  |
| desert_npc_points | Sandy / 沙漠 | 沙漠 NPC / Oasis 路线点 | 见文件 |  |  | -254 | 30 | -150 | S | 待测 | `npc_route_points.json`, `base_locations.json` | Sandy/Oasis/沙漠路线点跟新沙漠一起测。 |
| seedshop_counter | 通用：SeedShop | SeedShop 室内柜台目标 | 12049.5 | 71 | 12038.5 | 22 | 36 | -25 | S | 待测 | `npc/events/npc_route_points.json` | SeedShop 室内柜台目标；indoor=true |
| abigail_seedshop_indoor_target | Abigail | Abigail SeedShop 白天室内停留点（玩家实测） | 12065.5 | 71 | 12049.5 | 32 | 37 | -39 | S | 待测 | `npc/events/npc_route_points.json` | Abigail SeedShop 白天室内停留点（玩家实测）；indoor=true |
| abigail_seedshop_gaming | Abigail | Abigail 游戏机位置（玩家实测） | 12065.5 | 71 | 12037.5 | 21 | 37 | -38 | N | 待测 | `npc/events/npc_route_points.json` | Abigail 游戏机位置（玩家实测）；indoor=true |
| abigail_seedshop_sleep | Abigail | Abigail 睡觉位置（玩家实测） | 12060.5 | 71 | 12036.5 | 20 | 37 | -33 | N | 待测 | `npc/events/npc_route_points.json` | Abigail 睡觉位置（玩家实测）；indoor=true |
| abigail_saloon_indoor_stay | Abigail | Abigail Saloon 室内停留点（玩家实测） | 14214.5 | 71 | 14212.5 | 32 | 37 | -39 | 2 南/S | 待测 | `npc/events/npc_route_points.json` | Abigail Saloon 室内停留点（玩家实测）；indoor=true |
| abigail_museum_indoor_stay | Abigail | Abigail 博物馆室内停留点（玩家实测） | 13062.5 | 71 | 13068.5 | 123 | 38 | 51 | N | 待测 | `npc/events/npc_route_points.json` | Abigail 博物馆室内停留点（玩家实测）；indoor=true |
| abigail_clinic_indoor_stay | Abigail | Abigail 诊所室内停留点（玩家实测） | 15375.5 | 71 | 15362.5 | 0 | 43 | -23 | N | 待测 | `npc/events/npc_route_points.json` | Abigail 诊所室内停留点（玩家实测）；indoor=true |
| abigail_sciencehouse_indoor_stay | Abigail | Abigail ScienceHouse（Sebastian 房间）室内停留点（玩家实测） | 16514.5 | 71.375 | 16526.5 | 39 | 46 | -107 | 2 南/S | 待测 | `npc/events/npc_route_points.json` | Abigail ScienceHouse（Sebastian 房间）室内停留点（玩家实测）；indoor=true |
| abigail_mountain_flute | Abigail | Abigail 山区吹笛点（玩家实测） | -313 | -14 | 266 | 87 | 81 | -117 | 2 南/S | 待测 | `npc/events/npc_route_points.json` | Abigail 山区吹笛点（玩家实测） |
| abigail_forest_stay | Abigail | Abigail 森林停留点（玩家实测） | 186 | -15 | -145 | -128 | 64 | 81 | S | 待测 | `npc/events/npc_route_points.json` | Abigail 森林停留点（玩家实测） |
| abigail_beach_stay | Abigail | Abigail 海滩停留点（玩家实测） | -233 | -15 | -147 | 29 | 60 | 100 | S | 待测 | `npc/events/npc_route_points.json` | Abigail 海滩停留点（玩家实测） |
| abigail_railroad_stay | Abigail | Abigail 铁路山区停留点（玩家实测） | -95 | -13 | 338 | 2 | 85 | -176 | S | 待测 | `npc/events/npc_route_points.json` | Abigail 铁路山区停留点（玩家实测） |
| abigail_busstop_stay | Abigail | Abigail 公交站停留点（玩家实测） | 78 | -12 | 202 | -56 | 64 | -53 | N | 待测 | `npc/events/npc_route_points.json` | Abigail 公交站停留点（玩家实测） |
| alex_joshhouse_bedroom | Alex | Alex JoshHouse 卧室/睡觉点（玩家实测） | 15962.5 | 71 | 15960.5 | 59 | 22 | -18 | S | 待测 | `npc/events/npc_route_points.json` | Alex JoshHouse 卧室/睡觉点（玩家实测）；indoor=true |
| alex_joshhouse_kitchen | Alex | Alex JoshHouse 厨房/生活区（玩家实测） | 15942.5 | 71 | 15938.5 | 38 | 22 | 1 | E | 待测 | `npc/events/npc_route_points.json` | Alex JoshHouse 厨房/生活区（玩家实测）；indoor=true |
| alex_joshhouse_weights | Alex | Alex JoshHouse 举重区（玩家实测） | 15960.5 | 71 | 15953.5 | 51 | 22 | -18 | S | 待测 | `npc/events/npc_route_points.json` | Alex JoshHouse 举重区（玩家实测）；indoor=true |
| alex_haleyhouse_visit | Alex | Alex HaleyHouse 拜访停留点（玩家实测） | 17092.5 | 71 | 17668.5 | -5 | 24 | 38 | W | 待测 | `npc/events/npc_route_points.json` | Alex HaleyHouse 拜访停留点（玩家实测）；indoor=true |
| alex_town_football | Alex | Alex 镇上踢球点（玩家实测） | -202 | -18 | 28 | 54 | 64 | 3 | 2 南/S | 待测 | `npc/events/npc_route_points.json` | Alex 镇上踢球点（玩家实测） |
| alex_town_hangout | Alex | Alex 镇上闲逛点（玩家实测） | -186 | -18 | 13 | 42 | 64 | 15 | N | 待测 | `npc/events/npc_route_points.json` | Alex 镇上闲逛点（玩家实测） |
| alex_beach_swim | Alex | Alex 海滩游泳点（玩家实测） | -196 | -15 | -158 | 42 | 60 | 121 | S | 待测 | `npc/events/npc_route_points.json` | Alex 海滩游泳点（玩家实测） |
| alex_saloon_seat | Alex | Alex Saloon 看球/喝酒座位（玩家实测） | 14216.5 | 71 | 14254.5 | 56 | 36 | 15 | S | 待测 | `npc/events/npc_route_points.json` | Alex Saloon 看球/喝酒座位（玩家实测）；indoor=true |
| clint_blacksmith_anvil | Clint | Clint 铁匠铺工作台（日常工作） | 13642.5 | 71 | 13637.5 | 104 | 46 | 22 | S | 待测 | `npc/events/npc_route_points.json` | Clint 铁匠铺工作台（日常工作）；indoor=true |
| clint_blacksmith_hammer | Clint | Clint 铁匠铺锤子区（傍晚打铁） | 13643.5 | 71 | 13644.5 | 112 | 46 | 21 | N | 待测 | `npc/events/npc_route_points.json` | Clint 铁匠铺锤子区（傍晚打铁）；indoor=true |
| clint_blacksmith_sleep | Clint | Clint 铁匠铺睡觉位 | 13653.5 | 71 | 13644.5 | 111 | 46 | 11 | S | 待测 | `npc/events/npc_route_points.json` | Clint 铁匠铺睡觉位；indoor=true |
| clint_saloon_seat | Clint | Clint Saloon 座位 | 14210.5 | 71 | 14229.5 | 15 | 36 | 17 | E | 待测 | `npc/events/npc_route_points.json` | Clint Saloon 座位；indoor=true |
| clint_town_cc | Clint | Clint 社区中心外溜达（周五） | -207 | -10 | 142 | 44 | 66 | -37 | S | 待测 | `npc/events/npc_route_points.json` | Clint 社区中心外溜达（周五） |
| clint_beach_stay | Clint | Clint 海滩（冬15看海） | -246 | -15 | -181 | 37 | 60 | 159 | 2 南/S | 待测 | `npc/events/npc_route_points.json` | Clint 海滩（冬15看海） |
| caroline_seedshop_kitchen | Caroline | Caroline 种子店厨房（早上做事） | 12068.5 | 71 | 12072.5 | 57 | 37 | -43 | 0 北/N | 待测 | `npc/events/npc_route_points.json` | Caroline 种子店厨房（早上做事）；indoor=true |
| caroline_seedshop_living | Caroline | Caroline 种子店客厅区 | 12057.5 | 71 | 12059.5 | 39 | 36 | -26 | S | 待测 | `npc/events/npc_route_points.json` | Caroline 种子店客厅区；indoor=true |
| caroline_seedshop_read | Caroline | Caroline 种子店看书位 | 12065.5 | 71 | 12054.5 | 38 | 37 | -41 | N | 待测 | `npc/events/npc_route_points.json` | Caroline 种子店看书位；indoor=true |
| caroline_seedshop_exercise | Caroline | Caroline 种子店锻炼区（朝东） | 12046.5 | 71 | 12061.5 | 42 | 36 | -23 | S | 待测 | `npc/events/npc_route_points.json` | Caroline 种子店锻炼区（朝东）；indoor=true |
| caroline_seedshop_display | Caroline | Caroline 种子店展示区 | 12036.5 | 71 | 12053.5 | 36 | 36 | -7 | N | 待测 | `npc/events/npc_route_points.json` | Caroline 种子店展示区；indoor=true |
| caroline_seedshop_bedroom | Caroline | Caroline 种子店卧室/睡觉 | 12069.5 | 71 | 12057.5 | 44 | 37 | -41 | S | 待测 | `npc/events/npc_route_points.json` | Caroline 种子店卧室/睡觉；indoor=true |
| caroline_seedshop_aerobics | Caroline | Caroline 有氧运动区（同锻炼区） | 12046.5 | 71 | 12061.5 | 42 | 36 | -23 | S | 待测 | `npc/events/npc_route_points.json` | Caroline 有氧运动区（同锻炼区）；indoor=true |
| caroline_seedshop_diary | Caroline | Caroline 日记区 | 12065.5 | 71 | 12061.5 | 41 | 37 | -38 | 1 东/E | 待测 | `npc/events/npc_route_points.json` | Caroline 日记区；indoor=true |
| caroline_seedshop_mirror | Caroline | Caroline 镜子位 | 12062.5 | 71 | 12072.5 | 46 | 37 | -39 | E | 待测 | `npc/events/npc_route_points.json` | Caroline 镜子位；indoor=true |
| caroline_town_walk | Caroline | Caroline 镇上散步点 | -225 | -18 | 33 | 77 | 64 | 14 | E | 待测 | `npc/events/npc_route_points.json` | Caroline 镇上散步点 |
| caroline_town_square | Caroline | Caroline 镇广场（周日下午） | -118 | -17 | 14 | 27 | 64 | 1 | S | 待测 | `npc/events/npc_route_points.json` | Caroline 镇广场（周日下午） |
| caroline_museum_read | Caroline | Caroline 博物馆看书 | 13062.5 | 71 | 13076.5 | 123 | 38 | 48 | N | 待测 | `npc/events/npc_route_points.json` | Caroline 博物馆看书；indoor=true |
| caroline_beach_stay | Caroline | Caroline 海滩 | -270 | -14 | -181 | 83 | 60 | 129 | S | 待测 | `npc/events/npc_route_points.json` | Caroline 海滩 |
| lewis_manorhouse_living | Lewis | Lewis 市长家客厅/日常（朝西） | 14792.5 | 71 | 14789.5 | 56 | 50 | 21 | S | 待测 | `npc/events/npc_route_points.json` | Lewis 市长家客厅/日常（朝西）；indoor=true |
| lewis_manorhouse_evening | Lewis | Lewis 市长家晚间活动区 | 14790.5 | 71 | 14810.5 | 66 | 53 | 22 | S | 待测 | `npc/events/npc_route_points.json` | Lewis 市长家晚间活动区；indoor=true |
| lewis_manorhouse_bedroom | Lewis | Lewis 市长家卧室/睡觉 | 14793.5 | 71 | 14808.5 | 71 | 53 | 20 | S | 待测 | `npc/events/npc_route_points.json` | Lewis 市长家卧室/睡觉；indoor=true |
| lewis_saloon_seat | Lewis | Lewis Saloon 喝酒座位（朝东） | 14214.5 | 71 | 14320.5 | 18 | 36 | 13 | S | 待测 | `npc/events/npc_route_points.json` | Lewis Saloon 喝酒座位（朝东）；indoor=true |
| lewis_seedshop_visit | Lewis | Lewis 拜访 Pierre 柜台 | 12049.5 | 71 | 12037.5 | 22 | 36 | -23 | N | 待测 | `npc/events/npc_route_points.json` | Lewis 拜访 Pierre 柜台；indoor=true |
| lewis_blacksmith_visit | Lewis | Lewis 视察铁匠铺 | 13640.5 | 71 | 13638.5 | 103 | 46 | 24 | N | 待测 | `npc/events/npc_route_points.json` | Lewis 视察铁匠铺；indoor=true |
| lewis_museum_visit | Lewis | Lewis 博物馆视察 | 13072.5 | 71 | 13062.5 | 111 | 38 | 41 | N | 待测 | `npc/events/npc_route_points.json` | Lewis 博物馆视察；indoor=true |
| lewis_sciencehouse_visit | Lewis | Lewis 木匠铺拜访 Robin | 16527.5 | 75 | 16519.5 | 31 | 51 | -120 | N | 待测 | `npc/events/npc_route_points.json` | Lewis 木匠铺拜访 Robin；indoor=true |
| lewis_clinic_visit | Lewis | Lewis 诊所拜访 | 15364.5 | 71 | 15364.5 | 4 | 43 | -13 | N | 待测 | `npc/events/npc_route_points.json` | Lewis 诊所拜访；indoor=true |
| lewis_towngarden | Lewis | Lewis 花园/浇花（朝东） | -211 | -18 | -23 | 55 | 64 | 37 | N | 待测 | `npc/events/npc_route_points.json` | Lewis 花园/浇花（朝东） |
| lewis_town_bench | Lewis | Lewis 镇上长椅（朝北） | -164 | -18 | -3 | 28 | 64 | 24 | W | 待测 | `npc/events/npc_route_points.json` | Lewis 镇上长椅（朝北） |
| lewis_town_plaza | Lewis | Lewis 广场巡视 | -117 | -17 | 30 | 3 | 64 | 2 | S | 待测 | `npc/events/npc_route_points.json` | Lewis 广场巡视 |
| lewis_town_statue | Lewis | Lewis 镇上雕像区（周五） | -228 | -18 | -54 | 55 | 64 | 19 | W | 待测 | `npc/events/npc_route_points.json` | Lewis 镇上雕像区（周五） |
| lewis_communitycenter_stay | Lewis | Lewis 社区中心外巡视 | -193 | -10 | 137 | 54 | 66 | -44 | N | 待测 | `npc/events/npc_route_points.json` | Lewis 社区中心外巡视 |
| lewis_fishshop_visit | Lewis | Lewis 鱼店外（朝北） | -239 | -15 | -217 | 74 | 60 | 152 | S | 待测 | `npc/events/npc_route_points.json` | Lewis 鱼店外（朝北） |
| lewis_animalshop_visit | Lewis | Lewis 牧场外（朝南） | 178 | -13 | -19 | -87 | 64 | 30 | N | 待测 | `npc/events/npc_route_points.json` | Lewis 牧场外（朝南） |
| lewis_beach_stay | Lewis | Lewis 夏季海边（朝北） | -248 | -14 | -147 | 55 | 60 | 107 | S | 待测 | `npc/events/npc_route_points.json` | Lewis 夏季海边（朝北） |
| lewis_forest_stay | Lewis | Lewis 秋季森林 | 163 | -15 | -137 | -110 | 64 | 83 | S | 待测 | `npc/events/npc_route_points.json` | Lewis 秋季森林 |
| demetrius_sciencehouse_lab | Demetrius | Demetrius 实验台前工作（朝南） | 16527.5 | 75 | 16537.5 | 45 | 51 | -119 | E | 待测 | `npc/events/npc_route_points.json` | Demetrius 实验台前工作（朝南）；indoor=true |
| demetrius_sciencehouse_notes | Demetrius | Demetrius 写笔记/读书（朝东） | 16531.5 | 75 | 16532.5 | 39 | 51 | -117 | S | 待测 | `npc/events/npc_route_points.json` | Demetrius 写笔记/读书（朝东）；indoor=true |
| demetrius_sciencehouse_kitchen | Demetrius | Demetrius 厨房区域（朝北） | 16537.5 | 77 | 16544.5 | 53 | 58 | -116 | N | 待测 | `npc/events/npc_route_points.json` | Demetrius 厨房区域（朝北）；indoor=true |
| demetrius_sciencehouse_bedroom | Demetrius | Demetrius 卧室站立（朝西） | 16546.5 | 77 | 16533.5 | 42 | 58 | -125 | N | 待测 | `npc/events/npc_route_points.json` | Demetrius 卧室站立（朝西）；indoor=true |
| demetrius_sciencehouse_sleep | Demetrius | Demetrius 睡觉位（朝西） | 16546.5 | 77 | 16533.5 | 45 | 58 | -126 | S | 待测 | `npc/events/npc_route_points.json` | Demetrius 睡觉位（朝西）；indoor=true |
| demetrius_saloon_seat | Demetrius | Demetrius 酒馆座位 | 14213.5 | 71 | 14212.5 | 19 | 36 | 15 | N | 待测 | `npc/events/npc_route_points.json` | Demetrius 酒馆座位；indoor=true |
| demetrius_mountain_research | Demetrius | Demetrius 山区室外研究（朝南） | -256 | -9 | 205 | 97 | 81 | -121 | S | 待测 | `npc/events/npc_route_points.json` | Demetrius 山区室外研究（朝南） |
| demetrius_town_square | Demetrius | Demetrius 镇上闲逛 | -163 | -17 | -51 | 30 | 66 | -46 | N | 待测 | `npc/events/npc_route_points.json` | Demetrius 镇上闲逛 |
| elliott_house_piano | Elliott | Elliott 弹钢琴（朝东） | 17670.5 | 71 | 18247.5 | 79 | 45 | 98 | N | 待测 | `npc/events/npc_route_points.json` | Elliott 弹钢琴（朝东）；indoor=true |
| elliott_house_write | Elliott | Elliott 写作桌（朝东） | 17670.5 | 71 | 18247.5 | 73 | 45 | 97 | N | 待测 | `npc/events/npc_route_points.json` | Elliott 写作桌（朝东）；indoor=true |
| elliott_house_read | Elliott | Elliott 壁炉旁读书（朝东） | 17670.5 | 71 | 18247.5 | 85 | 45 | 101 | S | 待测 | `npc/events/npc_route_points.json` | Elliott 壁炉旁读书（朝东）；indoor=true |
| elliott_house_sleep | Elliott | Elliott 睡觉位（朝西） | 17672.5 | 71 | 18251.5 | 83 | 45 | 96 | S | 待测 | `npc/events/npc_route_points.json` | Elliott 睡觉位（朝西）；indoor=true |
| elliott_saloon_seat | Elliott | Elliott 酒馆座位 | 14218.5 | 71 | 14239.5 | 23 | 36 | 17 | N | 待测 | `npc/events/npc_route_points.json` | Elliott 酒馆座位；indoor=true |
| elliott_beach_walk | Elliott | Elliott 海滩散步（朝北） | -196 | -15 | -151 | 80 | 60 | 117 | S | 待测 | `npc/events/npc_route_points.json` | Elliott 海滩散步（朝北） |
| elliott_museum_read | Elliott | Elliott 博物馆阅读（朝东） | 13082.5 | 71 | 13069.5 | 119 | 38 | 38 | N | 待测 | `npc/events/npc_route_points.json` | Elliott 博物馆阅读（朝东）；indoor=true |
| elliott_seedshop_visit | Elliott | Elliott 种子店购物（朝南） | 12040.5 | 71 | 12043.5 | 31 | 36 | -14 | E | 待测 | `npc/events/npc_route_points.json` | Elliott 种子店购物（朝南）；indoor=true |
| elliott_town_walk | Elliott | Elliott 镇上闲逛（朝西） | -145 | -17 | 35 | 43 | 64 | 53 | N | 待测 | `npc/events/npc_route_points.json` | Elliott 镇上闲逛（朝西） |
| elliott_forest_walk | Elliott | Elliott 森林散步 | 238 | -13 | -124 | -115 | 64 | 117 | S | 待测 | `npc/events/npc_route_points.json` | Elliott 森林散步 |
| evelyn_joshhouse_stove | Evelyn | Evelyn 灶台做饭（朝东） | 15946.5 | 71 | 15940.5 | 40 | 22 | -3 | N | 待测 | `npc/events/npc_route_points.json` | Evelyn 灶台做饭（朝东）；indoor=true |
| evelyn_joshhouse_fridge | Evelyn | Evelyn 冰箱旁（朝东） | 15946.5 | 71 | 15942.5 | 42 | 22 | -3 | N | 待测 | `npc/events/npc_route_points.json` | Evelyn 冰箱旁（朝东）；indoor=true |
| evelyn_joshhouse_table | Evelyn | Evelyn 餐桌（朝北） | 15941.5 | 71 | 15942.5 | 41 | 22 | 0 | S | 待测 | `npc/events/npc_route_points.json` | Evelyn 餐桌（朝北）；indoor=true |
| evelyn_joshhouse_sofa | Evelyn | Evelyn 客厅沙发（朝北） | 15943.5 | 71 | 15960.5 | 60 | 22 | 3 | N | 待测 | `npc/events/npc_route_points.json` | Evelyn 客厅沙发（朝北）；indoor=true |
| evelyn_joshhouse_sleep | Evelyn | Evelyn 睡觉位（朝西） | 15961.5 | 71 | 15939.5 | 39 | 22 | -18 | S | 待测 | `npc/events/npc_route_points.json` | Evelyn 睡觉位（朝西）；indoor=true |
| evelyn_town_bench | Evelyn | Evelyn 镇上长椅（朝东） | -167 | -18 | -5 | 25 | 64 | 24 | S | 待测 | `npc/events/npc_route_points.json` | Evelyn 镇上长椅（朝东） |
| evelyn_town_garden | Evelyn | Evelyn 花园浇花（朝南） | -208 | -18 | 34 | 61 | 64 | 2 | N | 待测 | `npc/events/npc_route_points.json` | Evelyn 花园浇花（朝南） |
| evelyn_communitycenter_visit | Evelyn | Evelyn 社区中心（朝北） | -178 | -10 | 115 | 42 | 66 | -33 | N | 待测 | `npc/events/npc_route_points.json` | Evelyn 社区中心（朝北） |
| emily_haleyhouse_room | Emily | Emily 卧室起居（朝西） | 17110.5 | 71 | 17681.5 | 6 | 24 | 25 | N | 待测 | `npc/events/npc_route_points.json` | Emily 卧室起居（朝西）；indoor=true |
| emily_haleyhouse_sewing | Emily | Emily 缝纫机旁（朝东） | 17090.5 | 71 | 17680.5 | 6 | 24 | 39 | S | 待测 | `npc/events/npc_route_points.json` | Emily 缝纫机旁（朝东）；indoor=true |
| emily_haleyhouse_kitchen | Emily | Emily 厨房（朝南） | 17097.5 | 71 | 17684.5 | 11 | 24 | 36 | N | 待测 | `npc/events/npc_route_points.json` | Emily 厨房（朝南）；indoor=true |
| emily_haleyhouse_sleep | Emily | Emily 睡觉位（朝西） | 17111.5 | 71 | 17683.5 | 8 | 24 | 21 | S | 待测 | `npc/events/npc_route_points.json` | Emily 睡觉位（朝西）；indoor=true |
| emily_saloon_work | Emily | Emily 酒吧工作位（朝西） | 14216.5 | 71 | 14230.5 | 30 | 36 | 11 | S | 待测 | `npc/events/npc_route_points.json` | Emily 酒吧工作位（朝西）；indoor=true |
| emily_saloon_exercise | Emily | Emily 酒吧健身区（朝西） | 14218.5 | 71 | 14234.5 | 49 | 36 | 6 | N | 待测 | `npc/events/npc_route_points.json` | Emily 酒吧健身区（朝西）；indoor=true |
| emily_seedshop_browse | Emily | Emily 杂货店闲逛（朝南） | 12035.5 | 71 | 12043.5 | 32 | 36 | -10 | E | 待测 | `npc/events/npc_route_points.json` | Emily 杂货店闲逛（朝南）；indoor=true |
| emily_seedshop_exercise | Emily | Emily 杂货店运动区（朝南） | 12050.5 | 71 | 12058.5 | 39 | 36 | -19 | E | 待测 | `npc/events/npc_route_points.json` | Emily 杂货店运动区（朝南）；indoor=true |
| emily_communitycenter_visit | Emily | Emily 社区中心室外（朝南） | -186 | -10 | 136 | 58 | 66 | -44 | N | 待测 | `npc/events/npc_route_points.json` | Emily 社区中心室外（朝南） |
| emily_town_walk | Emily | Emily 镇上散步（朝南） | -152 | -10 | 141 | 62 | 66 | -27 | S | 待测 | `npc/events/npc_route_points.json` | Emily 镇上散步（朝南） |
| emily_beach_relax | Emily | Emily 海滩放松（朝西） | -204 | -15 | -169 | 71 | 60 | 113 | S | 待测 | `npc/events/npc_route_points.json` | Emily 海滩放松（朝西） |
| george_joshhouse_tv | George | George 看电视位（朝东） | 15940.5 | 71 | 15956.5 | 56 | 22 | 3 | N | 待测 | `npc/events/npc_route_points.json` | George 看电视位（朝东）；indoor=true |
| george_joshhouse_kitchen | George | George 厨房桌旁（朝北） | 15942.5 | 71 | 15942.5 | 42 | 22 | 2 | W | 待测 | `npc/events/npc_route_points.json` | George 厨房桌旁（朝北）；indoor=true |
| george_joshhouse_stove | George | George 火炉旁（朝北） | 15940.5 | 71 | 15962.5 | 44 | 22 | -12 | N | 待测 | `npc/events/npc_route_points.json` | George 火炉旁（朝北）；indoor=true |
| george_joshhouse_sleep | George | George 睡觉位（朝西） | 15960.5 | 71 | 15940.5 | 42 | 22 | -17 | S | 待测 | `npc/events/npc_route_points.json` | George 睡觉位（朝西）；indoor=true |
| george_town_bench | George | George 镇上长凳（朝北） | -164 | -18 | -8 | 26 | 64 | 24 | S | 待测 | `npc/events/npc_route_points.json` | George 镇上长凳（朝北） |
| george_seedshop_visit | George | George 杂货店购物（朝南） | 12040.5 | 71 | 12046.5 | 28 | 36 | -14 | E | 待测 | `npc/events/npc_route_points.json` | George 杂货店购物（朝南）；indoor=true |
| george_saloon_visit | George | George 酒吧座位（朝北） | 14212.5 | 71 | 14241.5 | 30 | 36 | 18 | W | 待测 | `npc/events/npc_route_points.json` | George 酒吧座位（朝北）；indoor=true |
| george_beach_spot | George | George 海滩散步（朝南） | -201 | -15 | -189 | 47 | 60 | 118 | S | 待测 | `npc/events/npc_route_points.json` | George 海滩散步（朝南） |
| gus_saloon_counter | Gus | Gus 吧台工作位（朝西） | 14216.5 | 71 | 14223.5 | 25 | 36 | 11 | S | 待测 | `npc/events/npc_route_points.json` | Gus 吧台工作位（朝西）；indoor=true |
| gus_saloon_kitchen | Gus | Gus 厨房备餐（朝东） | 14219.5 | 71 | 14228.5 | 30 | 36 | 9 | N | 待测 | `npc/events/npc_route_points.json` | Gus 厨房备餐（朝东）；indoor=true |
| gus_saloon_clean | Gus | Gus 清洁打扫（朝东） | 14212.5 | 71 | 14235.5 | 38 | 36 | 14 | S | 待测 | `npc/events/npc_route_points.json` | Gus 清洁打扫（朝东）；indoor=true |
| gus_saloon_sit | Gus | Gus 坐下休息（朝南） | 14217.5 | 71 | 14233.5 | 26 | 36 | 10 | S | 待测 | `npc/events/npc_route_points.json` | Gus 坐下休息（朝南）；indoor=true |
| gus_seedshop_visit | Gus | Gus 杂货店购物（朝南） | 12049.5 | 71 | 12044.5 | 38 | 36 | -12 | W | 待测 | `npc/events/npc_route_points.json` | Gus 杂货店购物（朝南）；indoor=true |
| gus_communitycenter_visit | Gus | Gus 社区中心室外（朝南） | -180 | -10 | 121 | 58 | 66 | -36 | N | 待测 | `npc/events/npc_route_points.json` | Gus 社区中心室外（朝南） |
| gus_town_walk | Gus | Gus 镇上散步（朝北） | -227 | -18 | -51 | 74 | 64 | -3 | E | 待测 | `npc/events/npc_route_points.json` | Gus 镇上散步（朝北） |
| gus_beach_spot | Gus | Gus 海滩散步（朝西） | -217 | -14 | -136 | 86 | 60 | 119 | E | 待测 | `npc/events/npc_route_points.json` | Gus 海滩散步（朝西） |
| haley_haleyhouse_mirror | Haley | Haley 卧室梳妆台前（朝西） | 17106.5 | 71 | 17671.5 | -4 | 24 | 23 | N | 待测 | `npc/events/npc_route_points.json` | Haley 卧室梳妆台前（朝西）；indoor=true |
| haley_haleyhouse_closet | Haley | Haley 卧室衣柜前（朝东） | 17109.5 | 71 | 17671.5 | -6 | 24 | 22 | N | 待测 | `npc/events/npc_route_points.json` | Haley 卧室衣柜前（朝东）；indoor=true |
| haley_haleyhouse_living | Haley | Haley 客厅（朝西） | 17095.5 | 71 | 17669.5 | -5 | 24 | 31 | S | 待测 | `npc/events/npc_route_points.json` | Haley 客厅（朝西）；indoor=true |
| haley_haleyhouse_kitchen | Haley | Haley 厨房（朝北） | 17087.5 | 71 | 17688.5 | 9 | 24 | 34 | E | 待测 | `npc/events/npc_route_points.json` | Haley 厨房（朝北）；indoor=true |
| haley_haleyhouse_sleep | Haley | Haley 床铺（朝西） | 17110.5 | 71 | 17667.5 | -9 | 24 | 22 | S | 待测 | `npc/events/npc_route_points.json` | Haley 床铺（朝西）；indoor=true |
| haley_town_square | Haley | Haley 镇广场（朝北） | -145 | -17 | 36 | -10 | 64 | 2 | N | 待测 | `npc/events/npc_route_points.json` | Haley 镇广场（朝北） |
| haley_beach_sunbathe | Haley | Haley 沙滩晒太阳（朝北） | -282 | -14 | -175 | 59 | 60 | 102 | S | 待测 | `npc/events/npc_route_points.json` | Haley 沙滩晒太阳（朝北） |
| haley_forest_photo | Haley | Haley 森林拍照（朝西） | 204 | -15 | -139 | -103 | 64 | 64 | S | 待测 | `npc/events/npc_route_points.json` | Haley 森林拍照（朝西） |
| harvey_clinic_desk | Harvey | Harvey 诊所办公桌（朝西） | 15366.5 | 71 | 15367.5 | 6 | 43 | -13 | N | 待测 | `npc/events/npc_route_points.json` | Harvey 诊所办公桌（朝西）；indoor=true |
| harvey_clinic_exam | Harvey | Harvey 诊所检查室（朝西） | 15377.5 | 71 | 15361.5 | 1 | 43 | -23 | N | 待测 | `npc/events/npc_route_points.json` | Harvey 诊所检查室（朝西）；indoor=true |
| harvey_clinic_lobby | Harvey | Harvey 诊所大厅（朝西） | 15366.5 | 71 | 15375.5 | 10 | 43 | -13 | W | 待测 | `npc/events/npc_route_points.json` | Harvey 诊所大厅（朝西）；indoor=true |
| harvey_room_main | Harvey | Harvey 房间主区域（Clinic二楼） | 15382.5 | 75 | 15370.5 | 3..27 | 46..51 | -38..-28 | S | 待测 | `npc/events/npc_route_points.json` | Harvey 房间主区域（Clinic二楼）；indoor=true min=(3,46,-38), max=(27,51,-28) |
| harvey_room_sleep | Harvey | Harvey 床铺（朝西，Clinic二楼） | 15390.5 | 75 | 15375.5 | 15 | 47 | -37 | S | 待测 | `npc/events/npc_route_points.json` | Harvey 床铺（朝西，Clinic二楼）；indoor=true |
| harvey_room_radio | Harvey | Harvey 听广播位置（朝西，Clinic二楼） | 15390.5 | 75 | 15371.5 | 8 | 47 | -36 | N | 待测 | `npc/events/npc_route_points.json` | Harvey 听广播位置（朝西，Clinic二楼）；indoor=true |
| harvey_room_read | Harvey | Harvey 阅读位置（朝东，Clinic二楼） | 15389.5 | 75 | 15369.5 | 8 | 47 | -34 | W | 待测 | `npc/events/npc_route_points.json` | Harvey 阅读位置（朝东，Clinic二楼）；indoor=true |
| harvey_seedshop_browse | Harvey | Harvey 种子店内逛（朝西） | 12051.5 | 71 | 12045.5 | 30 | 36 | -19 | N | 待测 | `npc/events/npc_route_points.json` | Harvey 种子店内逛（朝西）；indoor=true |
| harvey_museum_browse | Harvey | Harvey 博物馆内逛（朝东） | 13081.5 | 71 | 13076.5 | 127 | 38 | 32 | N | 待测 | `npc/events/npc_route_points.json` | Harvey 博物馆内逛（朝东）；indoor=true |
| harvey_saloon_seat | Harvey | Harvey 酒吧座位（朝东） | 14210.5 | 71 | 14251.5 | 28 | 36 | 13 | N | 待测 | `npc/events/npc_route_points.json` | Harvey 酒吧座位（朝东）；indoor=true |
| harvey_town_walk | Harvey | Harvey 镇上散步（朝北） | -135 | -17 | 32 | 41 | 64 | 16 | E | 待测 | `npc/events/npc_route_points.json` | Harvey 镇上散步（朝北） |
| harvey_town_bench | Harvey | Harvey 镇上长椅（朝北） | -165 | -18 | -3 | 28 | 64 | 24 | W | 待测 | `npc/events/npc_route_points.json` | Harvey 镇上长椅（朝北） |
| jas_animalshop_kitchen | Jas | Jas 牧场厨房（朝北） | 17093.5 | 71 | 18272.5 | -72 | 34 | 20 | N | 待测 | `npc/events/npc_route_points.json` | Jas 牧场厨房（朝北）；indoor=true |
| jas_animalshop_living | Jas | Jas 牧场客厅（朝东） | 17092.5 | 71 | 18242.5 | -97 | 34 | 22 | E | 待测 | `npc/events/npc_route_points.json` | Jas 牧场客厅（朝东）；indoor=true |
| jas_animalshop_read | Jas | Jas 牧场阅读角（朝北） | 17094.5 | 71 | 18274.5 | -99 | 34 | 9 | N | 待测 | `npc/events/npc_route_points.json` | Jas 牧场阅读角（朝北）；indoor=true |
| jas_animalshop_bedroom | Jas | Jas 卧室（朝西） | 17102.5 | 71 | 18244.5 | -100 | 34 | 12 | S | 待测 | `npc/events/npc_route_points.json` | Jas 卧室（朝西）；indoor=true |
| jas_animalshop_sleep | Jas | Jas 床铺（朝西） | 17105.5 | 71 | 18243.5 | -100 | 34 | 11 | S | 待测 | `npc/events/npc_route_points.json` | Jas 床铺（朝西）；indoor=true |
| jas_museum_read | Jas | Jas 博物馆阅读（朝东） | 13072.5 | 71 | 13075.5 | 126 | 38 | 41 | N | 待测 | `npc/events/npc_route_points.json` | Jas 博物馆阅读（朝东）；indoor=true |
| jas_town_playground | Jas | Jas 镇上游乐场（朝北） | -98 | -10 | 148 | -10 | 66 | -64 | N | 待测 | `npc/events/npc_route_points.json` | Jas 镇上游乐场（朝北） |
| jas_town_walk | Jas | Jas 镇上散步（朝南） | -193 | -10 | 128 | -2 | 66 | -41 | N | 待测 | `npc/events/npc_route_points.json` | Jas 镇上散步（朝南） |
| jas_town_jumprope | Jas | Jas 镇上跳绳（朝东） | -140 | -17 | 26 | -2 | 64 | 54 | S | 待测 | `npc/events/npc_route_points.json` | Jas 镇上跳绳（朝东） |
| jas_forest_jumprope | Jas | Jas 森林跳绳（朝北） | 263 | -13 | -2 | -148 | 64 | 19 | S | 待测 | `npc/events/npc_route_points.json` | Jas 森林跳绳（朝北） |
| jas_beach_play | Jas | Jas 沙滩玩耍（朝北） | -230 | -15 | -156 | 30 | 60 | 112 | S | 待测 | `npc/events/npc_route_points.json` | Jas 沙滩玩耍（朝北） |
| jodi_samhouse_kitchen | Jodi | Jodi SamHouse 厨房做饭（朝东） | 17106.5 | 71 | 17094.5 | -28 | 39 | 21 | N | 待测 | `npc/events/npc_route_points.json` | Jodi SamHouse 厨房做饭（朝东）；indoor=true |
| jodi_samhouse_living | Jodi | Jodi SamHouse 客厅休息（朝南） | 17098.5 | 71 | 17090.5 | -29 | 38 | 34 | S | 待测 | `npc/events/npc_route_points.json` | Jodi SamHouse 客厅休息（朝南）；indoor=true |
| jodi_samhouse_sleep | Jodi | Jodi SamHouse 睡觉位（朝西） | 17110.5 | 71 | 17114.5 | -13 | 39 | 18 | S | 待测 | `npc/events/npc_route_points.json` | Jodi SamHouse 睡觉位（朝西）；indoor=true |
| jodi_samhouse_bedroom | Jodi | Jodi SamHouse 卧室日常（朝西） | 17109.5 | 71 | 17111.5 | -14 | 39 | 21 | S | 待测 | `npc/events/npc_route_points.json` | Jodi SamHouse 卧室日常（朝西）；indoor=true |
| jodi_seedshop_browse | Jodi | Jodi SeedShop 采购（朝北） | 12035.5 | 71 | 12051.5 | 35 | 36 | -23 | W | 待测 | `npc/events/npc_route_points.json` | Jodi SeedShop 采购（朝北）；indoor=true |
| jodi_town_walk | Jodi | Jodi 镇上散步（朝东） | -117 | -17 | 48 | 67 | 64 | 45 | N | 待测 | `npc/events/npc_route_points.json` | Jodi 镇上散步（朝东） |
| leah_house_easel | Leah | Leah 画架前创作（朝东） | 17097.5 | 71 | 18821.5 | -85 | 38 | 47 | N | 待测 | `npc/events/npc_route_points.json` | Leah 画架前创作（朝东）；indoor=true |
| leah_house_kitchen | Leah | Leah 厨房（朝西） | 17093.5 | 71 | 18828.5 | -76 | 38 | 47 | E | 待测 | `npc/events/npc_route_points.json` | Leah 厨房（朝西）；indoor=true |
| leah_house_sleep | Leah | Leah 睡觉位（朝西） | 17099.5 | 71 | 18818.5 | -88 | 38 | 46 | S | 待测 | `npc/events/npc_route_points.json` | Leah 睡觉位（朝西）；indoor=true |
| leah_forest_forage | Leah | Leah 森林觅食（朝南） | 178 | -15 | -149 | -112 | 64 | 114 | 3 西/W | 待测 | `npc/events/npc_route_points.json` | Leah 森林觅食（朝南） |
| leah_beach_sketch | Leah | Leah 海滩写生（朝北） | -287 | -14 | -174 | 26 | 60 | 99 | S | 待测 | `npc/events/npc_route_points.json` | Leah 海滩写生（朝北） |
| leah_museum_browse | Leah | Leah 博物馆浏览（朝北） | 13072.5 | 71 | 13068.5 | 118 | 38 | 40 | W | 待测 | `npc/events/npc_route_points.json` | Leah 博物馆浏览（朝北）；indoor=true |
| leah_saloon_seat | Leah | Leah 酒吧座位（朝南） | 14211.5 | 71 | 14220.5 | 33 | 36 | 11 | W | 待测 | `npc/events/npc_route_points.json` | Leah 酒吧座位（朝南）；indoor=true |
| linus_tent_fire | Linus | Linus 帐篷旁篝火（朝西） | -217 | -2 | 266 | 54 | 85 | -146 | W | 待测 | `npc/events/npc_route_points.json` | Linus 帐篷旁篝火（朝西） |
| linus_tent_inside | Linus | Linus 帐篷内部（朝南） | -218 | -1 | 275 | 52 | 85 | -154 | S | 待测 | `npc/events/npc_route_points.json` | Linus 帐篷内部（朝南） |
| linus_tent_sleep | Linus | Linus 帐篷睡觉位（朝东） | -217 | -1 | 282 | -83 | 85 | -154 | S | 待测 | `npc/events/npc_route_points.json` | Linus 帐篷睡觉位（朝东） |
| linus_mountain_forage | Linus | Linus 山区觅食（朝东） | -277 | -12 | 205 | 71 | 81 | -121 | E | 待测 | `npc/events/npc_route_points.json` | Linus 山区觅食（朝东） |
| linus_beach_fish | Linus | Linus 海滩钓鱼（朝北） | -260 | -15 | -220 | 90 | 60 | 130 | S | 待测 | `npc/events/npc_route_points.json` | Linus 海滩钓鱼（朝北） |
| linus_town_dumpster | Linus | Linus 镇上翻垃圾桶（朝东） | -171 | -18 | 17 | 33 | 64 | 13 | N | 待测 | `npc/events/npc_route_points.json` | Linus 镇上翻垃圾桶（朝东） |
| marnie_animalshop_counter | Marnie | Marnie 牧场柜台/工作位（朝西） | 17095.5 | 71 | 18253.5 | -88 | 24 | 21 | S | 待测 | `npc/events/npc_route_points.json` | Marnie 牧场柜台/工作位（朝西）；indoor=true |
| marnie_animalshop_kitchen | Marnie | Marnie 牧场厨房（朝东） | 17096.5 | 71 | 18268.5 | -72 | 34 | 20 | N | 待测 | `npc/events/npc_route_points.json` | Marnie 牧场厨房（朝东）；indoor=true |
| marnie_animalshop_living | Marnie | Marnie 牧场客厅（朝西） | 17095.5 | 71 | 18247.5 | -97 | 34 | 21 | E | 待测 | `npc/events/npc_route_points.json` | Marnie 牧场客厅（朝西）；indoor=true |
| marnie_animalshop_sleep | Marnie | Marnie 牧场卧室/睡觉位（朝西） | 17107.5 | 71 | 18254.5 | -87 | 34 | 9 | 3 西/W | 待测 | `npc/events/npc_route_points.json` | Marnie 牧场卧室/睡觉位（朝西）；indoor=true |
| marnie_saloon_seat | Marnie | Marnie 酒吧座位（朝南） | 14211.5 | 71 | 14219.5 | 36 | 36 | 12 | N | 待测 | `npc/events/npc_route_points.json` | Marnie 酒吧座位（朝南）；indoor=true |
| marnie_town_walk | Marnie | Marnie 镇上散步（朝北） | -137 | -17 | 9 | -21 | 64 | 8 | S | 待测 | `npc/events/npc_route_points.json` | Marnie 镇上散步（朝北） |
| marnie_forest_walk | Marnie | Marnie 森林散步（朝南） | 229 | -13 | -70 | -128 | 64 | 57 | S | 待测 | `npc/events/npc_route_points.json` | Marnie 森林散步（朝南） |
| marnie_seedshop_visit | Marnie | Marnie 杂货店采购（朝东） | 12036.5 | 71 | 12055.5 | 33 | 36 | -25 | S | 待测 | `npc/events/npc_route_points.json` | Marnie 杂货店采购（朝东）；indoor=true |
| marnie_carpenter_visit | Marnie | Marnie 木匠店拜访（朝西） | 16525.5 | 75 | 16533.5 | 29 | 51 | -119 | N | 待测 | `npc/events/npc_route_points.json` | Marnie 木匠店拜访（朝西）；indoor=true |
| trailer_outdoor_door | 通用：Trailer | 已迁入固定建筑门点表（不用在 NPC 路线点重复填） | -231 | -18 | 20 |  |  |  | 未引用 | 跳过 | `npc/events/npc_route_points.json` | 拖车门点改走玩家 portal/landing；已录入坐标移到上方固定建筑门点表。 |
| maru_sciencehouse_tinker | Maru | Maru ScienceHouse 修理/发明工作台（朝西） | 16525.5 | 75 | 16533.5 | 42 | 51 | -121 | N | 待测 | `npc/events/npc_route_points.json` | Maru ScienceHouse 修理/发明工作台（朝西）；indoor=true |
| maru_sciencehouse_telescope | Maru | Maru ScienceHouse 望远镜（朝北） | 16542.5 | 77 | 16519.5 | 32 | 58 | -123 | S | 待测 | `npc/events/npc_route_points.json` | Maru ScienceHouse 望远镜（朝北）；indoor=true |
| maru_sciencehouse_sleep | Maru | Maru ScienceHouse 卧室/睡觉位（朝西） | 16546.5 | 77 | 16519.5 | 25 | 58 | -125 | S | 待测 | `npc/events/npc_route_points.json` | Maru ScienceHouse 卧室/睡觉位（朝西）；indoor=true |
| maru_clinic_work | Maru | Maru 诊所护士工作台（朝东） | 15375.5 | 71 | 15362.5 | 2 | 43 | -15 | S | 待测 | `npc/events/npc_route_points.json` | Maru 诊所护士工作台（朝东）；indoor=true |
| maru_saloon_visit | Maru | Maru 酒吧座位（朝南） | 14210.5 | 71 | 14154.5 | 40 | 36 | 17 | N | 待测 | `npc/events/npc_route_points.json` | Maru 酒吧座位（朝南）；indoor=true |
| maru_town_walk | Maru | Maru 镇上散步（朝东） | -118 | -17 | 17 | 81 | 64 | 48 | N | 待测 | `npc/events/npc_route_points.json` | Maru 镇上散步（朝东） |
| pam_trailer_couch | Pam | Pam 拖车客厅/沙发停留点 | -231 | -18 | 20 | 75 | 35 | 0 | S | 待测 | `npc/events/npc_route_points.json` | 拖车统一地图室内点；由 MD 录入，应用后写入 `indoor=true`。 |
| pam_trailer_kitchen | Pam | Pam 拖车厨房停留点 | -231 | -18 | 20 | 76 | 35 | 3 | E | 待测 | `npc/events/npc_route_points.json` | 拖车统一地图室内点；由 MD 录入，应用后写入 `indoor=true`。 |
| pam_trailer_sleep | Pam | Pam 拖车睡觉位 | -231 | -18 | 20 | 73 | 35 | 1 | N | 待测 | `npc/events/npc_route_points.json` | 拖车统一地图室内点；由 MD 录入，应用后写入 `indoor=true`。 |
| pam_saloon_seat | Pam | Pam 酒吧常驻座位 | 14216.5 | 71 | 14233.5 | 34 | 36 | 15 | W | 待测 | `npc/events/npc_route_points.json` | Pam 酒吧常驻座位；indoor=true |
| pam_busstop_bench | Pam | Pam 公交站等车点（朝北） | 69 | -12 | 207 | -62 | 64 | -61 | S | 待测 | `npc/events/npc_route_points.json` | Pam 公交站等车点（朝北） |
| pam_town_walk | Pam | Pam 镇上闲逛（朝南） | -37 | -16 | 59 | 52 | 64 | 62 | N | 待测 | `npc/events/npc_route_points.json` | Pam 镇上闲逛（朝南） |
| penny_trailer_sleep | Penny | Penny 拖车睡觉位 | -233 | -18 | 20 | 61 | 35 | 4 | N | 待测 | `npc/events/npc_route_points.json` | 拖车统一地图室内点；由 MD 录入，应用后写入 `indoor=true`。 |
| penny_trailer_kitchen | Penny | Penny 拖车厨房停留点 | -233 | -18 | 20 | 76 | 35 | 1 | S | 待测 | `npc/events/npc_route_points.json` | 拖车统一地图室内点；由 MD 录入，应用后写入 `indoor=true`。 |
| penny_trailer_reading | Penny | Penny 拖车阅读位 | -233 | -18 | 20 | 65 | 35 | -2 | N | 待测 | `npc/events/npc_route_points.json` | 拖车统一地图室内点；由 MD 录入，应用后写入 `indoor=true`。 |
| penny_museum_tutor | Penny | Penny 博物馆授课位（朝西） | 13075.5 | 71 | 13076.5 | 127 | 38 | 38 | S | 待测 | `npc/events/npc_route_points.json` | Penny 博物馆授课位（朝西）；indoor=true |
| penny_town_walk | Penny | Penny 镇上散步（朝北） | -272 | -18 | -28 | 70 | 64 | -21 | N | 待测 | `npc/events/npc_route_points.json` | Penny 镇上散步（朝北） |
| penny_town_bench | Penny | Penny 镇上长椅 | -163 | -18 | -3 | 28 | 64 | 22 | S | 待测 | `npc/events/npc_route_points.json` | Penny 镇上长椅 |
| pierre_seedshop_counter | Pierre | Pierre 种子店柜台 | 12051.5 | 71 | 12038.5 | 22 | 36 | -25 | S | 待测 | `npc/events/npc_route_points.json` | Pierre 种子店柜台；indoor=true |
| pierre_seedshop_sleep | Pierre | Pierre 种子店睡觉位（朝西） | 12067.5 | 71 | 12058.5 | 41 | 37 | -41 | S | 待测 | `npc/events/npc_route_points.json` | Pierre 种子店睡觉位（朝西）；indoor=true |
| pierre_seedshop_living | Pierre | Pierre 种子店起居区（朝东） | 12042.5 | 71 | 12060.5 | 55 | 37 | -38 | N | 待测 | `npc/events/npc_route_points.json` | Pierre 种子店起居区（朝东）；indoor=true |
| pierre_saloon_seat | Pierre | Pierre 酒吧座位（朝东） | 14217.5 | 71 | 14209.5 | 53 | 36 | 10 | S | 待测 | `npc/events/npc_route_points.json` | Pierre 酒吧座位（朝东）；indoor=true |
| pierre_town_walk | Pierre | Pierre 镇上散步（朝东） | -133 | -10 | 123 | -10 | 66 | -46 | E | 待测 | `npc/events/npc_route_points.json` | Pierre 镇上散步（朝东） |
| robin_sciencehouse_counter | Robin | Robin 木匠店柜台（朝西） | 16530.5 | 75 | 16520.5 | 32 | 51 | -122 | S | 待测 | `npc/events/npc_route_points.json` | Robin 木匠店柜台（朝西）；indoor=true |
| robin_sciencehouse_sleep | Robin | Robin 木匠店睡觉位（朝西） | 16545.5 | 77 | 16536.5 | 48 | 58 | -125 | S | 待测 | `npc/events/npc_route_points.json` | Robin 木匠店睡觉位（朝西）；indoor=true |
| robin_sciencehouse_living | Robin | Robin 木匠店起居区（朝东） | 16540.5 | 77 | 16544.5 | 55 | 58 | -121 | N | 待测 | `npc/events/npc_route_points.json` | Robin 木匠店起居区（朝东）；indoor=true |
| robin_saloon_seat | Robin | Robin 酒吧座位（朝西） | 14216.5 | 71 | 14253.5 | 40 | 36 | 17 | N | 待测 | `npc/events/npc_route_points.json` | Robin 酒吧座位（朝西）；indoor=true |
| robin_town_walk | Robin | Robin 镇上散步（朝北） | -210 | -9 | 136 | 76 | 66 | -49 | S | 待测 | `npc/events/npc_route_points.json` | Robin 镇上散步（朝北） |
| sam_samhouse_guitar | Sam | Sam 房间弹吉他（朝西） | 17100.5 | 71 | 17109.5 | -11 | 39 | 31 | N | 待测 | `npc/events/npc_route_points.json` | Sam 房间弹吉他（朝西）；indoor=true |
| sam_samhouse_living | Sam | Sam 家客厅（朝南） | 17098.5 | 71 | 17090.5 | -27 | 38 | 36 | E | 待测 | `npc/events/npc_route_points.json` | Sam 家客厅（朝南）；indoor=true |
| sam_samhouse_sleep | Sam | Sam 房间床（朝西） | 17101.5 | 71 | 17111.5 | -8 | 39 | 27 | S | 待测 | `npc/events/npc_route_points.json` | Sam 房间床（朝西）；indoor=true |
| sam_museum_work | Sam | Sam 博物馆工作台（朝东） | 13065.5 | 71 | 13089.5 | 123 | 38 | 38 | S | 待测 | `npc/events/npc_route_points.json` | Sam 博物馆工作台（朝东）；indoor=true |
| sam_town_skateboard | Sam | Sam 镇上玩滑板（朝北） | -95 | -16 | -27 | -15 | 64 | 42 | S | 待测 | `npc/events/npc_route_points.json` | Sam 镇上玩滑板（朝北） |
| sam_town_walk | Sam | Sam 镇上散步（朝南） | -145 | -17 | 20 | -4 | 64 | 52 | S | 待测 | `npc/events/npc_route_points.json` | Sam 镇上散步（朝南） |
| sam_saloon_pool | Sam | Sam 酒吧台球桌（朝南） | 14214.5 | 71 | 14251.5 | 58 | 36 | 13 | W | 待测 | `npc/events/npc_route_points.json` | Sam 酒吧台球桌（朝南）；indoor=true |
| sam_beach_hangout | Sam | Sam 沙滩闲逛（朝南） | -279 | -14 | -163 | -166 | 64 | 57 | E | 待测 | `npc/events/npc_route_points.json` | Sam 沙滩闲逛（朝南） |
| sam_forest_walk | Sam | Sam 森林散步（朝北） | 243 | -13 | -23 | -166 | 64 | 57 | 3 西/W | 待测 | `npc/events/npc_route_points.json` | Sam 森林散步（朝北） |
| sebastian_room_computer | Sebastian | Sebastian 房间电脑前（朝北） | 16519.5 | 71 | 16535.5 | 41 | 80 | -109 | N | 待测 | `npc/events/npc_route_points.json` | Sebastian 房间电脑前（朝北）；indoor=true |
| sebastian_room_desk | Sebastian | Sebastian 房间书桌（朝北） | 16515.5 | 71 | 16530.5 | 36 | 80 | -108 | S | 待测 | `npc/events/npc_route_points.json` | Sebastian 房间书桌（朝北）；indoor=true |
| sebastian_room_bed | Sebastian | Sebastian 房间床（朝北） | 16515.5 | 71 | 16535.5 | 43 | 80 | -105 | N | 待测 | `npc/events/npc_route_points.json` | Sebastian 房间床（朝北）；indoor=true |
| sebastian_saloon_pool | Sebastian | Sebastian 酒吧台球桌（朝北） | 14213.5 | 71 | 14255.5 | 55 | 36 | 15 | N | 待测 | `npc/events/npc_route_points.json` | Sebastian 酒吧台球桌（朝北）；indoor=true |
| sebastian_town_walk | Sebastian | Sebastian 镇上散步（朝北） | -209 | -10 | 97 | 75 | 66 | -47 | W | 待测 | `npc/events/npc_route_points.json` | Sebastian 镇上散步（朝北） |
| sebastian_town_smoke | Sebastian | Sebastian 镇上抽烟（朝北） | -209 | -10 | 97 | 88 | 81 | -101 | S | 待测 | `npc/events/npc_route_points.json` | Sebastian 镇上抽烟（朝北） |
| sebastian_samhouse_keyboard | Sebastian | Sebastian 在Sam家弹键盘（朝西） | 17098.5 | 71 | 17109.5 | -12 | 39 | 29 | S | 待测 | `npc/events/npc_route_points.json` | Sebastian 在Sam家弹键盘（朝西）；indoor=true |
| sebastian_samhouse_hangout | Sebastian | Sebastian 在Sam家闲逛（朝东） | 17096.5 | 71 | 17111.5 | -31 | 38 | 37 | S | 待测 | `npc/events/npc_route_points.json` | Sebastian 在Sam家闲逛（朝东）；indoor=true |
| sebastian_mountain_smoke | Sebastian | Sebastian 山上抽烟（朝南） | -252 | -5 | 176 | 51 | 85 | -109 | S | 待测 | `npc/events/npc_route_points.json` | Sebastian 山上抽烟（朝南） |
| sebastian_mountain_walk | Sebastian | Sebastian 山上散步（朝南） | -285 | -10 | 199 | 87 | 81 | -111 | E | 待测 | `npc/events/npc_route_points.json` | Sebastian 山上散步（朝南） |
| sebastian_mountain_cliff | Sebastian | Sebastian 山上悬崖边（朝北） | -312 | -15 | 261 | 90 | 81 | -101 | S | 待测 | `npc/events/npc_route_points.json` | Sebastian 山上悬崖边（朝北） |
| sebastian_sciencehouse_hall | Sebastian | Sebastian 科学屋走廊（朝南） | 16528.5 | 75 | 16514.5 | 40 | 46 | -110 | S | 待测 | `npc/events/npc_route_points.json` | Sebastian 科学屋走廊（朝南）；indoor=true |
| shane_animalshop_room | Shane | Shane 房间（Marnie家） | 17104.5 | 71 | 18265.5 | -76 | 34 | 11 | N | 待测 | `npc/events/npc_route_points.json` | Shane 房间（Marnie家）；indoor=true |
| shane_animalshop_sleep | Shane | Shane 睡觉位置 | 17108.5 | 71 | 18273.5 | -68 | 34 | 8 | S | 待测 | `npc/events/npc_route_points.json` | Shane 睡觉位置；indoor=true |
| shane_animalshop_outside | Shane | Shane Marnie家门外 | 156 | -13 | -18 | -81 | 64 | 32 | N | 待测 | `npc/events/npc_route_points.json` | Shane Marnie家门外 |
| shane_animalshop_chickens | Shane | Shane 看鸡（Marnie牧场旁） | 156 | -13 | -18 | -77 | 64 | 37 | S | 待测 | `npc/events/npc_route_points.json` | Shane 看鸡（Marnie牧场旁） |
| shane_saloon_drink | Shane | Shane 酒吧喝酒位 | 14216.5 | 71 | 14218.5 | 21 | 36 | 11 | E | 待测 | `npc/events/npc_route_points.json` | Shane 酒吧喝酒位；indoor=true |
| shane_saloon_seat | Shane | Shane 酒吧座位 | 14210.5 | 71 | 14252.5 | 21 | 36 | 11 | E | 待测 | `npc/events/npc_route_points.json` | Shane 酒吧座位；indoor=true |
| shane_seedshop_browse | Shane | Shane 杂货店逛逛 | 12051.5 | 71 | 12043.5 | 19 | 36 | -18 | N | 待测 | `npc/events/npc_route_points.json` | Shane 杂货店逛逛；indoor=true |
| vincent_samhouse_play | Vincent | Vincent Sam家玩耍 | 17091.5 | 71 | 17103.5 | -17 | 39 | 39 | N | 待测 | `npc/events/npc_route_points.json` | Vincent Sam家玩耍；indoor=true |
| vincent_samhouse_kitchen | Vincent | Vincent Sam家厨房 | 17106.5 | 71 | 17090.5 | -29 | 39 | 32 | S | 待测 | `npc/events/npc_route_points.json` | Vincent Sam家厨房；indoor=true |
| vincent_samhouse_sleep | Vincent | Vincent Sam家睡觉 | 17093.5 | 71 | 17099.5 | -21 | 39 | 38 | S | 待测 | `npc/events/npc_route_points.json` | Vincent Sam家睡觉；indoor=true |
| vincent_town_playground | Vincent | Vincent 镇上游乐场 | -119 | -10 | 118 | -18 | 66 | -66 | S | 待测 | `npc/events/npc_route_points.json` | Vincent 镇上游乐场 |
| vincent_town_walk | Vincent | Vincent 镇上散步 | -123 | -10 | 91 | -9 | 66 | -41 | S | 待测 | `npc/events/npc_route_points.json` | Vincent 镇上散步 |
| vincent_beach_play | Vincent | Vincent 海滩玩耍 | -230 | -15 | -188 | 39 | 60 | 97 | S | 待测 | `npc/events/npc_route_points.json` | Vincent 海滩玩耍 |
| vincent_museum_read | Vincent | Vincent 博物馆看书 | 13071.5 | 71 | 13075.5 | 128 | 38 | 41 | W | 待测 | `npc/events/npc_route_points.json` | Vincent 博物馆看书；indoor=true |
| fishshop_outdoor_door | 通用：FishShop | 鱼店室外入口门点 | -237 | -15 | -212 | 65 | 60 | 150 | S | 待测 | `npc/events/npc_route_points.json` | 鱼店室外入口门点 |
| fishshop_indoor_entry | 通用：FishShop | 鱼店室内传送落点 | 17666.5 | 71 | 17670.5 | 65 | 31 | 147 | N | 待测 | `npc/events/npc_route_points.json` | 鱼店室内传送落点；indoor=true |
| willy_fishshop_counter | Willy | Willy 鱼店柜台 | 17670.5 | 71 | 17670.5 | 65 | 31 | 143 | S | 待测 | `npc/events/npc_route_points.json` | Willy 鱼店柜台；indoor=true |
| willy_fishshop_room | Willy | Willy 鱼店卧室（暂同柜台） | 17670.5 | 71 | 17670.5 | 62 | 37 | 146 | S | 待测 | `npc/events/npc_route_points.json` | Willy 鱼店卧室（暂同柜台）；indoor=true |
| willy_fishshop_sleep | Willy | Willy 鱼店睡觉（暂同柜台） | 17670.5 | 71 | 17670.5 | 67 | 37 | 142 | S | 待测 | `npc/events/npc_route_points.json` | Willy 鱼店睡觉（暂同柜台）；indoor=true |
| willy_beach_fishing | Willy | Willy 海滩钓鱼位 | -229 | -15 | -220 | 71 | 60 | 155 | S | 待测 | `npc/events/npc_route_points.json` | Willy 海滩钓鱼位 |
| willy_beach_evening | Willy | Willy 海滩傍晚位 | -260 | -15 | -220 | 48 | 60 | 133 | S | 待测 | `npc/events/npc_route_points.json` | Willy 海滩傍晚位 |
| willy_saloon_seat | Willy | Willy 酒吧座位 | 14210.5 | 71 | 14233.5 | 35 | 36 | 17 | N | 待测 | `npc/events/npc_route_points.json` | Willy 酒吧座位；indoor=true |
| willy_forest_walk | Willy | Willy 森林散步 | 217 | -13 | -67 | -110 | 64 | 116 | S | 待测 | `npc/events/npc_route_points.json` | Willy 森林散步 |
| willy_town_bench | Willy | Willy 镇上长椅 | -167 | -18 | -5 | -2 | 66 | -53 | S | 待测 | `npc/events/npc_route_points.json` | Willy 镇上长椅 |
| gunther_counter | Gunther | Gunther 博物馆柜台 | 13074.5 | 71 | 13060.5 | 111 | 38 | 39 | S | 待测 | `npc/events/npc_route_points.json` | Gunther 博物馆柜台；indoor=true |
| marlon_counter | Marlon | Marlon 冒险家公会柜台 | 17672.5 | 71 | 17093.5 | 105 | 60 | -149 | S | 待测 | `npc/events/npc_route_points.json` | Marlon 冒险家公会柜台；indoor=true |
| wizard_home | Wizard | Wizard 法师塔内 | 18249.5 | 71 | 17095.5 | -179 | 34 | 55 | S | 待测 | `npc/events/npc_route_points.json` | Wizard 法师塔内；indoor=true |
| sandy_oasis_counter | Sandy | Sandy 绿洲柜台工作位（朝西） | 18245.5 | 71 | 17666.5 | -254 | 30 | -150 | S | 待测 | `npc/events/npc_route_points.json` | Sandy 绿洲柜台工作位（朝西）；indoor=true |
| morris_jojamart_manager | Morris | Morris Joja Mart 固定站位 | 18247.5 | 71 | 18259.5 | 114 | 45 | -22 | S | 待测 | `npc/events/default_spawns.json`, `JojaNpcEvents.java` | Morris 永远在 Joja 室内该点；indoor=true。 |
| joja_cashier_counter | Joja cashier | Joja 女收银员固定站位 | 18246.5 | 71 | 18249.5 | 104 | 45 | -21 | E | 待测 | `npc/events/default_spawns.json`, `JojaNpcEvents.java` | 女收银员永远在 Joja 室内该点；indoor=true。 |
| camel_merchant_desert | 骆驼商人 | 沙漠骆驼商人固定站位 | 见旧商人生成逻辑 |  |  | -193 | 64 | -185 | S | 待测 | `CamelMerchantEvents.java` | 骆驼商人永远在该点。 |
| traveling_cart_forest | 旅行商人 | 旅行商人周五/周日固定站位 | 见旧旅行商人生成逻辑 |  |  | -135 | 64 | 21 | S | 待测 | `TravelingCartEvents.java`, `TravelingCartManager.java` | 旅行商人周五和周日出现。 |

## 农场和主地图连接点

`FarmType.java` 里很多坐标是农场模板内部坐标，不一定跟主地图 pregen 一起改。但农场出口、回家、晕倒、睡醒这些连接必须验证。

| ID | 含义 | 旧值 | 新值 | 状态 | 要改的文件 | 备注 |
|---|---|---|---|---|---|---|
| farm_spawn_by_type | 不同农场类型的出生点 | 看 `FarmType.java` | 农场模板内部坐标保持不变，不随主地图 pregen 迁移 | 跳过 | `FarmType.java`, `FarmInstanceInitializer.java` | 农场内部区域、schem、出生点和内部布局不得修改。 |
| farm_to_valley_exit | 农场出口到主地图 |  | south: trigger min=(-63,64,-44), max=(-63,66,-42), exit landing=(-62,64,-43), yaw=EAST; east: trigger min=(-116,64,-3), max=(-112,66,-3), exit landing=(-114,64,-2), yaw=SOUTH; west: trigger min=(-116,64,-58), max=(-112,66,-58), exit landing=(-114,69,-64), yaw=NORTH | 待测 | `FarmEntryBarrierManager.java`, `InteriorPortalInteractionEvents.java` | 主地图侧三个去农场入口和从农场出来的主地图落点；农场实例内部 EntryData/出口触发区保持现状。 |
| pass_out_return | 晕倒回家 |  | 玩家所属农场实例内部返回逻辑保持不变，不随主地图 pregen 迁移 | 跳过 | `PassOutService.java`, `DimensionEventHandler.java` | 农场内部坐标和实例归属逻辑不得修改。 |
| bed_spawn_return | 睡觉/醒来返回 |  | 玩家所属农场实例内部睡醒/床点逻辑保持不变，不随主地图 pregen 迁移 | 跳过 | `SleepInteractionHandler.java`, `StardewTimeManager.java` | 农场内部坐标和 schem 不变。 |

## 区域型系统

区域用“备注”填 `min=(x,y,z), max=(x,y,z)`。这些通常不是简单坐标平移，而是要按新地图重新画区域。

| ID | 系统 | 旧坐标来源 | 新区域 / 新点位 | 状态 | 要改的文件 | 备注 |
|---|---|---|---|---|---|---|
| artifact_spots | 古物蚯蚓点 | service 里硬编码/数据 | town/forest/mountain 等主地图大区：bounds min=(-151,63,-237), max=(200,91,79), exposed `yellow_dirt`; beach: bounds min=(-4,57,77), max=(239,65,186), exposed `sand`, hoed/revert back to sand; desert: bounds min=(-310,53,-241), max=(-158,107,-113), exposed `sand`, hoed/revert back to sand | 待测 | `ArtifactSpotSpawnService.java`, `ArtifactDropService.java` | 沙滩和沙漠现在只认沙子，不再认砂岩；回退也回退成沙子。 |
| forage_spawns | 野外采集物 | service 里硬编码/数据 | beach: bounds min=(-4,57,77), max=(239,65,186), exposed `sand`; desert: bounds min=(-310,53,-241), max=(-158,107,-113), exposed `sand`; town: bounds min=(-40,62,-99), max=(91,84,17), exposed `grass_block`; mountain: bounds min=(-123,69,-104), max=(-43,79,-65) and min=(-39,69,-214), max=(101,95,-105), exposed `grass_block`; forest: bounds min=(-194,50,-12), max=(-47,74,138), exposed `grass_block`; secret_woods: bounds min=(-265,67,-1), max=(-183,86,42), exposed `grass_block`, use vanilla Woods forage entries | 待测 | `ForageSpawnService.java` | 神秘森林采集物按 SDV `Woods` 原版：Spring Morel/Common Mushroom/Wild Horseradish; Summer Fiddlehead Fern/Red Mushroom; Fall Chanterelle/Common Mushroom/Red Mushroom; Winter Holly。 |
| quarry_access_to_quarry | 采石场入口传送 | `QuarryAccessManager` 旧入口 trigger/ENTRY_DEST | trigger min=(137,81,-119), max=(137,81,-115); landing=(136,83,-117), yaw=WEST | 待测 | `QuarryAccessManager.java`, `InteriorPortalInteractionEvents.java` | target=`quarry_entrance`；传送触发方块放在 trigger 区域。 |
| quarry_access_to_town | 采石场出口传送 | `QuarryAccessManager` 旧出口 trigger/EXIT_DEST | trigger min=(139,81,-119), max=(139,83,-115); landing=(140,81,-117), yaw=EAST | 待测 | `QuarryAccessManager.java`, `InteriorPortalInteractionEvents.java` | target=`quarry_exit`；传送触发方块放在 trigger 区域。 |
| quarry_spawns | 采石场生成 | service 里硬编码/数据 | bounds min=(155,80,-140), max=(194,81,-101); spawn on top of every exposed coarse-dirt block in this range | 待测 | `QuarrySpawnService.java`, `QuarryAccessManager.java` | 采石场范围为该区域内所有露天黄土块正上方；要和采石场解锁/传送区域一致。 |
| coal_forest_clumps | 神秘森林大木桩 | service 里硬编码/数据 | fixed large stump main positions: (-235,68,11), (-237,68,6), (-232,68,7), (-223,68,36), (-212,68,35), (-204,68,5); no hollow logs | 待测 | `CoalForestClumpSpawnService.java` | 旧煤矿森林大块资源改为神秘森林逻辑；只刷大木桩，不刷空心原木。 |
| coal_forest_bounds | 神秘森林区域判定 | `x=129..207, y=-16..2, z=-246..-102` | bounds min=(-265,67,-1), max=(-183,86,42) | 待测 | `CoalForestArea.java`, `CoalForestClumpSpawnService.java` | 保留原保护/砍树豁免结构，只把区域语义迁到神秘森林。 |
| fishing_town | 城镇钓鱼 | fishing 数据 + 判定逻辑 | 用户后续自行刷水域/biome，不做本轮 runtime 迁移 | 跳过 | `fishing/locations/town.json`, fishing runtime | 新地图水域由用户后续处理。 |
| fishing_beach | 海滩钓鱼 | fishing 数据 + 判定逻辑 | 用户后续自行刷水域/biome，不做本轮 runtime 迁移 | 跳过 | `fishing/locations/beach.json`, fishing runtime | 新地图水域由用户后续处理。 |
| fishing_mountain | 山湖钓鱼 | fishing 数据 + 判定逻辑 | 用户后续自行刷水域/biome，不做本轮 runtime 迁移 | 跳过 | `fishing/locations/mountain.json`, fishing runtime | 新地图水域由用户后续处理。 |
| fishing_forest | 森林/秘密森林钓鱼 | fishing 数据 + 判定逻辑 | 用户后续自行刷水域/biome，不做本轮 runtime 迁移 | 跳过 | `fishing/locations/forest.json`, `woods.json` | 新地图水域由用户后续处理。 |
| fishing_desert | 沙漠钓鱼 | fishing 数据 + 沙漠 biome/区域 | 用户后续自行刷水域/biome，不做本轮 runtime 迁移 | 跳过 | `fishing/locations/desert.json`, `StardewBiomePatcher.java`, `DesertConstants.java` | 新地图水域由用户后续处理。 |
| biome_patch_desert | 沙漠 biome patch 区域 | `DesertConstants.DESERT_BBOX_*` | bounds min=(-310,53,-241), max=(-158,107,-113) | 待测 | `StardewBiomePatcher.java`, `DesertConstants.java` | 沙漠区域同时用于沙漠音乐、采集和古物；玩家在其他任何区域走主地图音乐。 |
| community_center_unlocks | 社区中心修复区域 | 过场/奖励放置逻辑 | 原来结构代码完全不变，不做本轮迁移 | 跳过 | `AreaCompletionService.java`, `CCAreaRegistry.java`, 社区中心过场 | 社区中心保持原结构/代码，不改。 |
| community_center_room_rects | 社区中心各房间修复矩形 | `CC_SCHEM_POS1=(-146,101,-1333)` 相关绝对坐标 | 原来结构代码完全不变，不做本轮迁移 | 跳过 | `CCAreaRegistry.java`, `InteriorSubspaceManager.java` | 社区中心保持原结构/代码，不改。 |
| music_regions | 主地图/室内/沙漠/神秘森林音乐区域 | 旧室内大坐标 + 沙漠 bounds | fixed interiors use embedded fixed region bounds; desert bounds min=(-310,53,-241), max=(-158,107,-113); secret_woods bounds min=(-265,67,-1), max=(-183,86,42); every other outdoor area uses normal main-map seasonal music | 待测 | `StardewMusicManager.java`, `ModSounds.java`, `sounds.json` | 神秘森林按 SDV `woodsTheme` / "In The Deep Woods"，源音频 `000000d8.wav` -> `woods_theme.ogg`。 |

## 事件和过场锚点

| ID | 含义 | 旧来源 | 新坐标 | 状态 | 要改的文件 | 备注 |
|---|---|---|---|---|---|---|
| lewis_cc_tour | Lewis 社区中心参观 | cutscene event JSON / commands |  | 待测 | `cutscene_events/lewis_cc_tour.json`, cutscene runtime | 摄像机、玩家、NPC 锚点都可能要改。 |
| marlon_mine_intro | 矿井介绍 | cutscene event JSON / commands |  | 待测 | `cutscene_events/marlon_mine_intro.json` | 依赖矿井入口。 |
| willy_fishing_rod | Willy 送鱼竿 | cutscene event JSON / commands |  | 待测 | `cutscene_events/willy_fishing_rod.json` | 海滩/鱼店锚点。 |
| wizard_intro | Wizard 初见 | cutscene event JSON / commands |  | 待测 | `cutscene_events/wizard_intro.json` | 法师塔多半是 overworld/interior，但来源/返回点要验证。 |
| wizard_e112 | Wizard 后续过场 | cutscene event JSON / commands |  | 待测 | `cutscene_events/wizard_e112.json` | 全部 camera/spawn/move 坐标都在旧法师塔室内。 |
| clint_furnace_wake_up | Clint 熔炉晨间事件 | cutscene event JSON / commands |  | 待测 | `cutscene_events/clint_furnace_wake_up.json` | 铁匠铺/农场锚点都要确认，农场相对点可跳过。 |
| gus_mini_jukebox_wake_up | Gus 点唱机晨间事件 | cutscene event JSON / commands |  | 待测 | `cutscene_events/gus_mini_jukebox_wake_up.json` | 酒吧/农场锚点都要确认，农场相对点可跳过。 |
| pierre_year_one_seed_notice_wake_up | Pierre 种子提醒晨间事件 | cutscene event JSON / commands |  | 待测 | `cutscene_events/pierre_year_one_seed_notice_wake_up.json` | 种子店/农场锚点都要确认，农场相对点可跳过。 |
| demetrius_cave_choice_wake_up | Demetrius 洞穴选择晨间事件 | farm anchor JSON / commands |  | 跳过 | `cutscene_events/demetrius_cave_choice_wake_up.json` | 主要是 farm_spawn 相对锚点；农场模板没变可跳过。 |
| leah_sculpture_wake_up | Leah 雕塑晨间事件 | farm anchor JSON / commands |  | 跳过 | `cutscene_events/leah_sculpture_wake_up.json` | 主要是 farm_spawn 相对锚点；农场模板没变可跳过。 |
| cutscene_events_full_file | 过场 JSON 全文件扫描 | 见文件 |  | 待测 | `src/main/resources/data/stardewcraft/cutscene_events/*.json` | 对 `relative:false` 的绝对坐标逐项重测；`anchor:farm_spawn` 通常跳过。 |
| morning_events | 晨间事件 | 玩家/农场本地 |  | 待测 | `WakeUpEventScheduler.java`, event JSON | 通常和主地图绝对坐标关系较弱。 |

## 迁移执行清单

1. 替换 `src/main/resources/pregen/stardew_valley/region/*.mca`。
2. 重新生成 `src/main/resources/pregen/stardew_valley/region_manifest.txt`。
3. 增加 `StardewValleyPrebuiltRegionInstaller.CURRENT_PREGEN_VERSION`。
4. 进游戏测点，把新坐标填进本账本。
5. 先改集中坐标表：
   - `WarpDestinations.java`
   - `SystemTotemManager.java`
   - `TotemPoleBlock.java`
   - `MinecartStationManager.java`
   - `npc_route_points.json`
   - `npc/location_mappings/base_locations.json`
   - `InteriorSubspaceManager.java` 里的室外 portal 点
   - Portal hint 已改为跟随实际 `PortalTriggerBlock` 区域，不再维护单独点位表
   - `DesertConstants.java`
   - `DesertGalaxyPillarBootstrap.java`
6. 再改客户端/表现坐标：传送提示、音乐区域、过场摄像机。
7. 再改区域系统：采集、古物、采石场、煤矿森林、钓鱼、生物群系、社区中心。
8. 再改过场锚点和事件触发条件。
9. 跑 `./gradlew classes --console=plain`。
10. 进游戏按下面清单验证。

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
| 沙漠公交往返可用 | 待测 |  |
| 沙漠边界、天气、音乐、biome 判定正常 | 待测 |  |
| 骷髅矿入口/出口可用 | 待测 |  |
| Oasis 入口/出口可用 | 待测 |  |
| 银河柱和仪式触发点正确 | 待测 |  |
| 系统图腾柱放在正确位置 | 待测 |  |
| 玩家图腾柱只能放在正确区域 | 待测 |  |
| 矿车站点生成并可正确旅行 | 待测 |  |
| Pierre/种子店双向传送正常 | 待测 |  |
| 酒吧双向传送正常 | 待测 |  |
| 诊所双向传送正常 | 待测 |  |
| 铁匠铺双向传送正常 | 待测 |  |
| 博物馆双向传送正常 | 待测 |  |
| 其他固定室内建筑双向传送正常 | 待测 | Marnie、Leah、Sam、Haley、Alex、Elliott、法师塔、Joja、Oasis 等。 |
| 传送提示粒子/文字出现在正确门区 | 待测 |  |
| 固定室内和沙漠音乐切换正确 | 待测 |  |
| 社区中心入口和奖励区域正常 | 待测 |  |
| NPC 能走到室外日程点 | 待测 |  |
| NPC 能进出室内 | 待测 |  |
| NPC base location / 地点 anchor 正确 | 待测 |  |
| 古物点只刷在合法表面 | 待测 |  |
| 野外采集物刷在合理区域 | 待测 |  |
| 钓鱼地点识别正常 | 待测 |  |
| 采石场生成/解锁正常 | 待测 |  |
| 晕倒后安全回家 | 待测 |  |
| 睡觉/醒来返回安全 | 待测 |  |
| 过场能在正确锚点触发 | 待测 |  |
| 过场摄像机、NPC、假玩家站位正确 | 待测 |  |

## 变更记录

| 日期 | 变更 | 作者 | 备注 |
|---|---|---|---|
| 2026-05-11 | 创建坐标迁移账本 | Copilot | 初版中文表格，用于新 pregen 地图坐标替换。 |
| 2026-05-11 | 农场改为玩家实例动态目标 | Copilot | 移除主地图固定农场传送点和农场系统图腾柱。 |
| 2026-05-15 | 补齐全量坐标录入清单 | Copilot | 增加沙漠、音乐、NPC base location、图腾放置区域、社区中心区域、过场全文件扫描等漏项；Portal hint 改为跟随实际传送方块区域。 |
