# Changelog

## 0.3.8-fix4 - 2026-05-14

### Update Log (English)

#### Fixes

- Fixed Stardew-style UI texture sampling and UV drift across non-4x Minecraft GUI scales by moving many atlas-dependent widgets to standalone PNG slices.
- Fixed V-menu layout regressions from the UI scaling pass, preserving the 4x9 Minecraft inventory grid and the original top tab placement.
- Fixed the V-menu top-left frame artifact by correcting the sliced menu tile resource instead of hiding it with layout offsets.
- Fixed the leaderboard page header and row styling, removing the metric icon from the title and making top-three rows visibly distinct.
- Fixed the leaderboard side tabs to follow the workbench-style icon tab behavior, hover tooltips, and SHWIP click sound.
- Fixed shop UI panel shadow layering so the upper shop panel no longer casts a dark overlay across the inventory area.
- Fixed shop money box and money digit alignment under non-4x GUI scale.

#### Changes

- Added the V-menu leaderboard system with server-authored snapshots, pagination, client cache, request/sync payloads, and money, mining, fishing, shipping, combat, and life metrics.
- Added reusable Stardew UI texture helpers for standalone PNG rendering, scaled item drawing, common buttons, arrows, boxes, dialogue parts, social icons, skill icons, and game menu widgets.
- Migrated many Stardew-style screens and HUD elements away from direct large-atlas UV rendering, including shop, quest log, billboard, overnight, geode, elevator, catalogue, workbench, TV, and common dialogue UI pieces.
- Added the UI atlas slicing manifest/tooling and a written UI scaling normalization standard for future UI work.
- Added leaderboard persistence hooks for player names, mine block statistics, bombed mine blocks, shipped item totals, and total shipping value.

#### Localization

- Added English and Chinese leaderboard text, metric descriptions, value formats, V-menu tab label updates, and related configuration labels.

### 更新日志（中文）

#### 修复

- 修复大量星露谷风格 UI 在 Minecraft 非 4x GUI scale 下的贴图采样、UV 漂移和缩放异常，逐步改为独立 PNG 切片绘制。
- 修复 UI 缩放迁移过程中 V 键菜单布局被误改的问题，保留 Minecraft 4x9 背包网格和原本的顶部 tab 位置。
- 修复 V 键菜单左上角脏块，改为修正切片资源本身，而不是用界面偏移遮挡。
- 修复排行榜页标题和榜单行样式，移除标题里的指标图标，并让前三名高亮更清晰。
- 修复排行榜侧边 tab，使其按工作台图标 tab 的交互、悬浮提示和 SHWIP 点击音效表现。
- 修复商店上半部分面板阴影层级错误，避免黑色阴影盖到下方背包区域。
- 修复商店金币框和金币数字在非 4x GUI scale 下的位置失调。

#### 改动

- 新增 V 键菜单排行榜系统，包含服务端排行榜快照、分页、客户端缓存、请求/同步网络包，以及财富、采矿、钓鱼、出货、战斗和生活类榜单。
- 新增可复用的星露谷 UI 独立贴图 helper，覆盖缩放物品绘制、通用按钮、箭头、面板、对话框部件、社交图标、技能图标和游戏菜单控件。
- 将大量星露谷风格界面和 HUD 从直接采样大合图迁移到独立 PNG 绘制，包括商店、任务日志、公告板、过夜结算、晶球、电梯、家具目录、工作台、电视和通用对话 UI 部件。
- 新增 UI atlas 切片清单/脚本，以及后续 UI 缩放规范化的书面标准。
- 新增排行榜所需的玩家名称、矿井方块、爆破方块、出货数量和出货总价值统计接入。

#### 本地化

- 补充英文和中文排行榜文本、榜单说明、数值格式、V 键菜单 tab 名称和相关配置文本。

## 0.3.8-fix3 - 2026-05-13

### Update Log (English)

#### Fixes

- Fixed a Lewis cutscene crash that could happen when the client disconnected while an event was ending.
- Fixed cutscene movement freezing so vertical motion is preserved and players are less likely to trigger flight checks.
- Fixed targeted bait losing its target fish data when inserted into and removed from fishing rods.
- Fixed Stardew Valley weather forcing vanilla overworld rain, so vanilla weather commands work normally again outside the Stardew Valley dimension.
- Fixed auto-grabbers not collecting held animal products such as cow milk, goat milk, sheep milk, and wool.
- Fixed auto-feed troughs failing to detect their barn or coop when placed as valid adjacent interior fixtures.
- Fixed auto-feed trough hay consumption so it now pulls from the owning farm's shared silo storage instead of the interacting player's personal key.
- Fixed silo, pasture grass, and wheat hay storage ownership so hay is credited to the farm where the action happens.
- Fixed Stardew bed interactions so players enter the sleeping pose before confirming sleep, can cancel back out of bed, and no longer get placed at incorrect offsets on custom bed models.
- Fixed Stardew multiplayer sleep voting so only players who remain in bed count toward the vote, while waiting sleepers continue recovering stamina each second.
- Fixed charged hoe range previews disappearing when aiming at protected Stardew yellow dirt.
- Fixed multiplayer silo managers staying visually unbuilt after construction when the silo belonged to a shared farm owner instead of the interacting member.
- Fixed hay hoppers and silo readouts resolving hay storage through the wrong player in shared farms, which could show 0/0 despite an existing silo.
- Fixed crab pots being blocked by public-area protection in Stardew Valley waterways.
- Fixed crab pot catches using one combined pool instead of separating ocean and freshwater catch pools.
- Fixed crab pot ownership so only the player who placed a crab pot can bait, collect, or remove it.
- Disabled external item automation for crab pots so pipes cannot bypass crab pot ownership.
- Fixed farm join accept/reject commands being hidden behind operator-only command registration in multiplayer.
- Increased glow radius for Small Glow Ring, Glow Ring, Iridium Band, and Glowstone Ring by about 50%.
- Fixed NPC-bound friendship doors rendering opaque instead of using the oak door cutout layer.
- Fixed seasonal leaf tinting registration for vanilla and Stardew leaves without applying the effect to cherry leaves.
- Fixed beverage items using the eating animation instead of Minecraft's drinking animation, including artisan drinks, milk, Joja Cola, clinic tonics, Ginger Ale, and Triple Shot Espresso.

#### Changes

- Auto-feed troughs now continuously perform low-frequency refill checks while the chunk is loaded, pulling silo hay into empty connected trough networks as needed.
- Shared-farm hay storage now aggregates legacy member-owned hay while using the farm owner as the canonical storage key for new hay.
- Stardew Valley weather sync now uses the custom Stardew weather state instead of mutating vanilla level weather.
- Custom Stardew beds now resolve their sleep anchor to the correct head block and use vanilla sleeping orientation/rendering behavior instead of applying custom entity position offsets.
- Added built-in Xaero's Minimap icon resources for StardewCraft NPCs, animals, Junimos, crows, and traveling merchants.
- Added NPC-bound friendship doors that use oak door visuals, let Stardew NPCs pass through, and block players until they meet the configured friendship requirement.

#### Localization

- Added or corrected small English and Chinese localization entries touched by the fix pass.

### 更新日志（中文）

#### 修复

- 修复刘易斯剧情在客户端断开连接、剧情结束回包时可能崩溃的问题。
- 修复剧情冻结玩家移动时清空竖直速度导致更容易触发飞行检测的问题。
- 修复针对性鱼饵装入钓竿再取出后丢失目标鱼数据、变回普通鱼饵的问题。
- 修复星露谷天气强行锁定主世界原版下雨，导致 `/weather` 指令无法正常关雨的问题。
- 修复自动采集器无法采集牛奶、羊奶、绵羊奶和羊毛等动物持有产物的问题。
- 修复自动喂食槽在合法贴着室内空气格摆放时识别不到所属鸡舍或畜棚，导致完全不会自动补草的问题。
- 修复自动喂食槽扣草时没有从所在农场的共享筒仓干草池扣除的问题。
- 修复筒仓、牧草和小麦产出干草时归属不稳定的问题，现在会优先按所在农场记入干草。
- 修复星露谷床交互流程，现在玩家会先进入躺床状态再确认是否睡觉，取消时会正常起床，并修复自定义床模型上的错误躺床偏移。
- 修复多人睡觉投票流程，现在只有仍然躺在床上的玩家会计入投票，等待投票期间仍会每秒恢复体力。
- 修复锄头蓄力范围预览在对准受保护的星露谷黄土时不显示的问题。
- 修复多人服务器中共享农场成员建造筒仓后，服务端提示已建成但管理界面仍显示未成型的问题。
- 修复共享农场里筒仓界面和喂料斗按错误玩家读取干草存储，导致已有筒仓仍显示 0/0 的问题。
- 修复星露谷公共水域因公共区域保护而无法放置蟹笼的问题。
- 修复蟹笼捕获物没有区分海水/淡水池子、所有产物混在一起随机的问题。
- 修复蟹笼所有权，现在只有放置者可以塞鱼饵、收取产物或拆除蟹笼。
- 禁用蟹笼的外部物品自动化接口，避免管道绕过蟹笼主人限制。
- 修复多人模式中 `/stardew farm accept` 和 `/stardew farm reject` 被管理员权限命令树误拦截的问题。
- 将小型光辉戒指、光辉戒指、铱环和光辉石戒指的发光半径提高约 50%。
- 修复绑定 NPC 的好感门没有使用橡木门 cutout 渲染层，导致透明区域显示为不透明的问题。
- 修正原版树叶和星露谷树叶的季节染色注册，并避免樱花树叶被季节染色影响。
- 修复饮品物品使用时播放吃东西动画的问题，现在酒、果汁、咖啡、牛奶、Joja 可乐、诊所药水、姜汁汽水和三倍浓缩咖啡会使用 Minecraft 的饮用动画。

#### 改动

- 自动喂食槽现在会在区块加载期间持续进行低频补草检查，按需从筒仓向空的连接喂食槽网络补充干草。
- 共享农场干草存储现在以农场主人作为新干草的统一归属，同时兼容读取和扣除旧版本成员名下的干草。
- 星露谷天气同步现在只使用自定义星露谷天气状态，不再改写原版维度天气。
- 自定义星露谷床现在会把睡眠锚点解析到正确的床头格，并使用原版睡眠朝向与渲染逻辑，不再手动给实体叠加位置偏移。
- 内置 Xaero 小地图图标资源，覆盖星野牧歌 NPC、动物、祝尼魔、乌鸦和旅行商人等实体。
- 新增可绑定 NPC 的好感门，外观复用橡木门，星露谷 NPC 可直接穿过，玩家需要达到配置的好感度后才能开门通行。

#### 本地化

- 补充或修正了本轮修复涉及的少量英文与中文文本。

## 0.3.8fix - 2026-05-08

基线版本：release: 0.3.8-alpha

### 本次我们做了什么

- 修复 NPC 对话期间会提前恢复行走的问题；对话现在会完整锁定 NPC 移动，关闭对话或玩家下线后会正确解锁。
- 优化 NPC 长距离移动的中间寻路点计算，减少室内外切换、高低差路径里的异常卡住与误判瞬移。
- 扩展星露谷物品到原版物品的一次性转换配方，补齐苹果、骨片、钻石、鸡蛋、绿宝石、羽毛、蜂蜜、墨汁、牛奶、鹦鹉螺壳、兔子脚、史莱姆球、羊毛等兼容入口。
- 新增基于 c 标签和本地兼容标签的原版配方适配，让更多原版合成与部分跨模组配方直接接受星露谷等价物品。
- 补上兔子脚相关酿造兼容，使星露谷兔子脚也能进入原版酿造链。
- 改进共享农场联机加入流程，客户端会同步“待审批加入”状态；已有加入申请时再创建农场会先确认，加入成功后的落地与起步资源发放也更顺。
- 放宽共享农场成员的日常系统权限，让作物、牧草、乌鸦、农场洞穴和每日结算更按成员所属农场生效，而不是只认房主。
- 增强离线玩家跨天补偿与晨间事件调度，避免多人环境里邮件、早晨事件和社区中心过场被静默跳过。
- 调整矿井与骷髅矿生态，重生成时会清理残留怪物，并重新收紧高层矿石与特殊房间节奏，减少一层就出现过量高价值矿的情况。
- 加入怪物召唤调试命令，并补充木乃伊坍塌的服务端同步、客户端渲染与爆炸处决链路，让木乃伊表现更接近原版星露谷。
- 修正鱼塘水体标签与鱼塘拉鱼交互，优化商店连续购买停止条件、电视烹饪频道解锁校验、Joja 路线细节、跨维度进出和时间对齐等边角问题。
- 将炸弹范围进一步削弱一档，避免当前版本的爆炸覆盖面偏大。

### 主要改动分类

#### NPC 与剧情

- 对话锁移动、关闭回包与登出清理补齐。
- NPC 路径中间点高度解析修正。
- 多人环境下的晨间事件、邮件与社区中心过场调度更稳定。

#### 物品兼容与配方

- 新增 Stardew 物品到原版物品的单向转换配方。
- 新增 c 标签桥接与 vanilla_compat 标签层。
- 覆写大量原版配方以接受钻石、蛋、奶、蜂蜜、羽毛、兔子脚、史莱姆球等星露谷等价物。
- 新增兔子脚酿造兼容。

#### 联机与共享农场

- 农场加入申请待处理状态同步到客户端。
- 加入申请与创建农场的冲突流程增加确认保护。
- 共享农场成员权限、传送、每日结算与农场洞穴逻辑继续向“真正共享”收敛。

#### 矿井、怪物与战斗

- 矿层重生成会清理残留怪物。
- 骷髅矿高层矿石与房间分布再平衡。
- 木乃伊倒地渲染、同步和处决逻辑补齐。
- 新增怪物召唤调试命令。

#### 其他修复

- 鱼塘可作为水体使用，并支持等待上钩时直接从鱼塘拉鱼。
- 商店长按购买会在背包鼠标堆叠装不下或余额不足时自动停止。
- 电视菜谱解锁改为校验当天实际播出的配方。
- 星露谷维度时间对齐、跨维度落点、传送效果与若干本地化文本继续修正。