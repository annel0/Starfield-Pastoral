# Changelog

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

#### Changes

- Auto-feed troughs now actively refill connected trough networks from silo hay while the chunk is loaded.
- Shared-farm hay storage now aggregates legacy member-owned hay while using the farm owner as the canonical storage key for new hay.
- Stardew Valley weather sync now uses the custom Stardew weather state instead of mutating vanilla level weather.

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

#### 改动

- 自动喂食槽现在会在区块加载时主动从筒仓补充连接的喂食槽网络。
- 共享农场干草存储现在以农场主人作为新干草的统一归属，同时兼容读取和扣除旧版本成员名下的干草。
- 星露谷天气同步现在只使用自定义星露谷天气状态，不再改写原版维度天气。

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