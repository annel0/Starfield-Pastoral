# 钓鱼原版地点到群系 ID 映射

## 核心原则
所有映射决策必须优先遵循《星露谷物语》原版钓鱼逻辑，再落到代码实现，禁止臆造行为。

## 阅读说明
- 本文件表示“原版钓鱼地点 -> 本项目使用的具体群系 ID”。
- 映射行中不使用 `#tag` 写法。
- `状态 = 已映射` 表示 `FishingDataManager.resolveVanillaAlignedLocationKeys(...)` 当前已经会路由到该地点。
- `状态 = 未映射` 表示该原版地点存在，但当前代码还没有路由进去。

## 原版地点完整列表（无缺漏）

| 原版地点 | 具体群系 ID（项目侧） | 状态 |
|---|---|---|
| `Backwoods` | 无（尚未路由） | 未映射 |
| `Beach` | `stardewcraft:beach`, `stardewcraft:beach_pier`, `stardewcraft:beach_ocean`, `minecraft:beach`, `minecraft:snowy_beach`, `minecraft:stony_shore` | 已映射 |
| `BeachNightMarket` | `stardewcraft:night_market_submarine` | 已映射 |
| `BoatTunnel` | 无（尚未路由） | 未映射 |
| `BugLand` | `stardewcraft:mutant_bug_lair` | 已映射 |
| `Caldera` | `stardewcraft:volcano_caldera` | 已映射 |
| `Default` | 当无地点路由命中时的回退池 | 已映射 |
| `Desert` | `stardewcraft:calico_desert`, `minecraft:desert` | 已映射 |
| `DesertFestival` | 无（尚未路由） | 未映射 |
| `Farm_Beach` | 无（尚未路由） | 未映射 |
| `Farm_Forest` | 无（尚未路由） | 未映射 |
| `Farm_FourCorners` | 无（尚未路由） | 未映射 |
| `Farm_Hilltop` | 无（尚未路由） | 未映射 |
| `Farm_MeadowlandsFarm` | 无（尚未路由） | 未映射 |
| `Farm_Riverland` | 无（尚未路由） | 未映射 |
| `Farm_Wilderness` | 无（尚未路由） | 未映射 |
| `fishingGame` | 无（尚未路由） | 未映射 |
| `Forest` | `stardewcraft:cindersap_forest_river`, `stardewcraft:forest_waterfall`, `stardewcraft:secret_woods_pond` | 已映射 |
| `IslandFarmCave` | 无（尚未路由） | 未映射 |
| `IslandNorth` | `stardewcraft:ginger_island_river` | 已映射 |
| `IslandSouth` | `stardewcraft:ginger_island_ocean`, `stardewcraft:ginger_island_south` | 已映射 |
| `IslandSouthEast` | `stardewcraft:ginger_island_ocean`, `stardewcraft:ginger_island_south` | 已映射 |
| `IslandSouthEastCave` | `stardewcraft:pirate_cove` | 已映射 |
| `IslandWest` | `stardewcraft:ginger_island_ocean`, `stardewcraft:ginger_island_south`, `stardewcraft:ginger_island_river`, `stardewcraft:ginger_island_pond` | 已映射 |
| `Mountain` | `stardewcraft:mountain_lake` | 已映射 |
| `Railroad` | 无（尚未路由） | 未映射 |
| `Sewer` | `stardewcraft:sewers` | 已映射 |
| `Submarine` | `stardewcraft:night_market_submarine` | 已映射 |
| `Temp` | 无（尚未路由） | 未映射 |
| `Town` | `stardewcraft:pelican_town_river`, `stardewcraft:jojamart_bridge` | 已映射 |
| `UndergroundMine` | `stardewcraft:mines_level_20`, `stardewcraft:mines_level_60`, `stardewcraft:mines_level_100` | 已映射 |
| `WitchSwamp` | `stardewcraft:witch_swamp` | 已映射 |
| `Woods` | `stardewcraft:secret_woods`, `stardewcraft:secret_woods_pond` | 已映射 |

## 地图制作说明
- 如果你的地图是 StardewCraft 自定义地图，优先直接使用上面的 `stardewcraft:*` 群系 ID。
- 表内出现的 `minecraft:*` 群系，仅表示当前项目 tag 定义里允许这些原版群系参与该路由。
- 仍为 `未映射` 的原版地点，需要先补对应群系 ID，再接入 `resolveVanillaAlignedLocationKeys(...)`。

## 依据来源
- 地点路由代码：`src/main/java/com/stardew/craft/fishing/data/FishingDataManager.java`
- 路由所用群系 ID 定义：`src/main/resources/data/stardewcraft/tags/worldgen/biome/*.json`
- 原版地点基线：`源文件/Content/Data/Locations.json`
