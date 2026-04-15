# StardewCraft 专用服务器兼容性审查报告

**审查日期**: 2026-04-15  
**审查范围**: `src/main/java/com/stardew/craft/` 全部 Java 源文件  
**模组版本**: 0.1.3-alpha (NeoForge 1.21.1)

---

## 概要

| 严重性 | 问题数 | 说明 |
|--------|--------|------|
| 🔴 **P0 — 启动即崩溃** | 3 处 | 服务端类加载时直接触发 `ClassNotFoundException` |
| 🟠 **P1 — 网络包类加载崩溃** | **~76 个文件** | Payload record 类顶层 import 客户端类 |
| 🟡 **P2 — 运行时崩溃** | 5 处 | 服务端执行到特定分支时 `NoClassDefFoundError` |
| ⚪ **P3 — 应清理** | 2 处 | 测试/调试文件残留 |

> **结论**: 目前模组**无法在专用服务器 (dedicated server) 上启动**。服务端 JVM 加载类时会因找不到 `net.minecraft.client.*` 包而立刻崩溃。

---

## 核心原理

在专用服务器的 JAR 中，`net.minecraft.client.*` 包**完全不存在**。Java 类加载器在加载一个类时，会解析该类的**所有顶层 import**，无论这些 import 对应的代码是否会在运行时被执行。因此：

```java
// ❌ 服务端加载这个类时立即崩溃 — 哪怕 handleClient 永远不会被调用
import net.minecraft.client.Minecraft;  // ← 类加载阶段就失败了

public record SomePayload(...) implements CustomPacketPayload {
    @OnlyIn(Dist.CLIENT)
    private static void handleClient() {
        Minecraft mc = Minecraft.getInstance();
    }
}
```

`@OnlyIn(Dist.CLIENT)` 只能防止**方法体**被编入服务端 JAR，但**无法阻止 import 语句的类解析**。

---

## 🔴 P0 — 服务端启动即崩溃 (3 处)

### P0-1: `StardewCraft.java` (主 @Mod 入口)

| 位置 | 问题 |
|------|------|
| **第 18 行** | `import com.stardew.craft.client.weapon.WeaponShaderRegistry;` |
| **第 837 行** | `modEventBus.addListener(WeaponShaderRegistry::onRegisterShadersSafe);` |

**影响**: `@Mod` 类是模组入口，服务端启动时**第一个**被加载。import 客户端着色器注册类 → 触发 `WeaponShaderRegistry` 类加载 → 触发其内部的 `net.minecraft.client.*` 依赖解析 → **服务端立刻崩溃**。

**修复**: 将 shader 注册移入 `StardewCraftClient.java`（已有 `@Mod(dist = Dist.CLIENT)` 保护）。

---

### P0-2: `BulletinBoardBlock.java` (Block 类)

| 位置 | 问题 |
|------|------|
| **第 3 行** | `import com.stardew.craft.client.gui.quest.BillboardScreen;` |
| **第 16 行** | `import net.minecraft.client.Minecraft;` |

**影响**: Block 类在注册时被双端加载。两行客户端 import → 服务端类加载失败 → 方块注册崩溃。

**修复**: 将 `openBillboardScreen()` 中的客户端调用改为 fully-qualified inline reference 放入 `@OnlyIn` 内部类，或抽到 `/client/` 包中的 helper。

---

### P0-3: `JojaVendingMachineBlock.java` (Block 类) 

| 位置 | 问题 |
|------|------|
| **第 3 行** | `import com.stardew.craft.client.gui.common.StardewConfirmDialogScreen;` |
| **第 4 行** | `import com.stardew.craft.client.gui.common.StardewQuestionDialogSpec;` |

**影响**: 同上，Block 类双端加载时 import 触发客户端类链式解析。

**修复**: 移客户端逻辑到 `@OnlyIn(Dist.CLIENT)` 内部类中，使用 fully-qualified 引用。

---

## 🟠 P1 — 网络包类加载崩溃 (~76 个文件)

所有 `CustomPacketPayload` record 类在双端都会被类加载器处理（服务端需要序列化/反序列化）。以下文件在类顶层 import 了 `net.minecraft.client.Minecraft` 和/或 `com.stardew.craft.client.*`：

### 通用网络包 (`network/payload/`, `network/`)

| 文件 | 客户端 import 行数 |
|------|-------------------|
| `OpenGeodeMenuPayload.java` | 2 行 (Minecraft + GeodeMenuScreen) |
| `OpenGuntherMenuPayload.java` | 3 行 |
| `OpenMarnieMenuPayload.java` | 3 行 |
| `OpenMarlonMenuPayload.java` | 3 行 |
| `OpenBlacksmithMenuPayload.java` | 3 行 |
| `OpenRobinMenuPayload.java` | 3 行 |
| `OpenAnimalPurchaseScreenPayload.java` | 2 行 |
| `OpenGiftConfirmPayload.java` | 3 行 |
| `OpenMineExitDialogPayload.java` | 3 行 |
| `OpenNpcDialogueScreenPayload.java` | 4 行 |
| `OpenShopScreenPayload.java` | 1 行 |
| `OpenSleepConfirmScreenPayload.java` | 2 行 |
| `OpenTVScreenPayload.java` | 2 行 |
| `OpenMailPayload.java` | 2 行 |
| `OpenDecorationScreenPayload.java` | 2 行 |
| `OpenSofaColorScreenPayload.java` | 2 行 |
| `OpenTotemNamingScreenPayload.java` | 2 行 |
| `OpenGilGoalsPayload.java` | 2 行 |
| `OpenTreasureChestPayload.java` | 1 行 |
| `OpenAnimalMoveHomeScreenPayload.java` | 1 行 |
| `GeodeCrackResultPayload.java` | 2 行 |
| `ItemPickupHudPacket.java` | 1 行 (StardewHudMessageManager) |
| `MiningFloorSyncPacket.java` | 1 行 (MiningFloorHud) |
| `PlayerDataSyncPacket.java` | 1 行 (ClientPlayerDataCache) |
| `TimeSyncPacket.java` | 1 行 (StardewTimeHud) |
| `TimeWarningPayload.java` | 1 行 (StardewTimeHud) |
| `HayHarvestHudMessagePacket.java` | 1 行 |
| `MissingItemHudMessagePacket.java` | 1 行 |
| `MuseumDonationSyncPacket.java` | 1 行 |
| `EmoteBroadcastPayload.java` | 1 行 |
| `EquipmentSyncPayload.java` | 1 行 |
| `HoldUpItemPayload.java` | 1 行 |
| `SkillExperienceGainPayload.java` | 1 行 |
| `SyncNpcFriendshipOverviewPayload.java` | 1 行 |
| `SyncNpcFriendshipStatusPayload.java` | 1 行 |
| `CookingPotIngredientAvailabilityPayload.java` | 1 行 |
| `QuestCompletePayload.java` | 1 行 |

### 钓鱼系统网络包 (`fishing/network/`)

| 文件 | 客户端 import |
|------|-------------|
| `FishingHookedAnimPayload.java` | Minecraft + FishingBiteVisuals |
| `FishingBitePromptPayload.java` | Minecraft + FishingBiteVisuals |
| `FishingCatchVisualPayload.java` | Minecraft + FishingCatchVisuals |
| `FishingFailVisualPayload.java` | Minecraft + FishingCatchVisuals |
| `FishingRodCastStatePayload.java` | Minecraft |
| `FishingStartPayload.java` | Minecraft + FishingMinigameScreen |
| `StartMinigamePayload.java` | Minecraft + FishingMinigameScreen |

### 战斗系统网络包 (`combat/network/`) — 29 个文件

| 文件 | 客户端 import |
|------|-------------|
| `WickedKrisPoisonStatusPayload.java` | Minecraft + WickedKrisPoisonClientState |
| `BrokenTridentThrustStrikePayload.java` | Minecraft + SkillEffectsClient |
| `BrokenTridentCatchPayload.java` | Minecraft + BrokenTridentCatchClientState + SkillEffectsClient |
| `ForestBlessingPayload.java` | Minecraft + ForestBlessingClientState |
| `SteelSpineFuryPayload.java` | Minecraft + SteelSpineFuryClientState |
| `SteelSpineFuryEnterPayload.java` | Minecraft + SkillEffectsClient |
| `SteelSpineFuryHitPayload.java` | Minecraft + SkillEffectsClient |
| `SteelSpineFuryStrikePayload.java` | Minecraft + SkillEffectsClient |
| `TremorBlockPayload.java` | Minecraft |
| `BurglarShankLootPayload.java` | Minecraft + StardewTimeHud + SkillEffectsClient |
| `IridiumNeedleCritPayload.java` | Minecraft + IridiumNeedleCritClientState |
| `IridiumNeedleFrenzyPayload.java` | Minecraft + IridiumNeedleFrenzyClientState + SkillEffectsClient |
| `IridiumNeedleThrustStrikePayload.java` | Minecraft + SkillEffectsClient |
| `LavaKatanaReverbPayload.java` | Minecraft + LavaKatanaReverbClientState |
| `DwarfDaggerThrustPayload.java` | Minecraft + DwarfDaggerThrustClientState |
| `DwarfDaggerRushPayload.java` | Minecraft + DwarfDaggerRushClientState |
| `InsectEyeStancePayload.java` | Minecraft + InsectEyeStanceClientState |
| `DragontoothShivBreathPayload.java` | Minecraft + DragontoothShivBreathClientState |
| `DarkSwordBloodMoonPayload.java` | Minecraft + DarkSwordBloodMoonClientState |
| `DarkSwordBloodDebtPayload.java` | Minecraft + DarkSwordBloodDebtClientState |
| `DashMovementPayload.java` | Minecraft + DashMovementClientState |
| `DwarfFortressPayload.java` | Minecraft + DwarfFortressClientState |
| `CarvingKnifeThrustStrikePayload.java` | Minecraft + SkillEffectsClient |
| `SilverSaberFoldbackPayload.java` | Minecraft + SilverSaberFoldbackClientState |
| `CrystalDaggerLayerPayload.java` | Minecraft + CrystalDaggerLayerClientState |
| `CrystalDaggerBurstPayload.java` | Minecraft + SkillEffectsClient |
| `ElfBladePayload.java` | Minecraft + ElfBladeClientState |
| `WindSpirePayload.java` | Minecraft + WindSpireClientState |
| `SteelFalchionTracePayload.java` | Minecraft + SteelFalchionTraceClientState |
| _(以及约 20 个只 import `com.stardew.craft.client.*` 不含 Minecraft 的战斗 Payload)_ | 各种客户端状态类 |

### 社区中心 (`communitycenter/cutscene/`)

| 文件 | 问题 |
|------|------|
| `CutscenePayload.java` | 第 69 行直接引用 `ScreenFade.onCutscenePacket()` |
| `ScreenFade.java` | import Minecraft + GuiGraphics，且不在 `/client/` 包、无 @OnlyIn 注解 |

**影响**: CutscenePayload 类加载 → 链式触发 ScreenFade → 触发 client 类解析 → 崩溃。

### `ClientOvernightHandler.java`

| 位置 | 问题 |
|------|------|
| 不在 `/client/` 包 | import Minecraft, Screen, PassOutOverlayScreen, ShippingMenuScreen |

**注意**: 此文件有 `@OnlyIn(Dist.CLIENT)` 注解在类级别，NeoForge dist-cleaner 会在服务端 JAR 中移除整个类。但如果其他非客户端代码**以 import 方式引用**这个类，仍可能触发问题。需确认引用方式都是在 `@OnlyIn` 保护的代码路径内。

---

## 🟡 P2 — 运行时特定路径崩溃 (5 处)

这些文件没有顶层 `import net.minecraft.client.*`，但在方法体中使用了 fully-qualified 客户端引用，当服务端执行到该分支时会 `NoClassDefFoundError`。

### P2-1: `DwarfService.java`

| 位置 | 问题 |
|------|------|
| **第 81 行** | `net.minecraft.client.Minecraft.getInstance().getLanguageManager().getSelected()` |

**上下文**: `convertToDwarvish()` 方法中检测语言是否为中文。虽然有 try-catch 包裹，但 JIT 可能在编译方法时就尝试解析类引用。在不同 JVM 行为下不一定安全。

**修复**: 通过 `ServerPlayer.getLanguage()` (1.21+) 或网络包传递语言偏好。

---

### P2-2: `WeaponTooltipBuilder.java`

| 位置 | 问题 |
|------|------|
| **第 3 行** | `import com.stardew.craft.client.ModKeyMappings;` |
| **第 5 行** | `import net.minecraft.client.Minecraft;` |

**上下文**: `appendTooltip()` 在物品堆叠上调用，客户端和服务端都可能调用。import 客户端键位映射类 → 服务端类加载崩溃。

**修复**: tooltip 只在客户端调用时需要按键提示。用 `level.isClientSide` 检查 + 将客户端代码移入 `@OnlyIn` 内部类。

---

### P2-3: `SmokedFishItem.java`

| 位置 | 问题 |
|------|------|
| **第 3 行** | `import com.stardew.craft.client.render.SmokedFishItemRenderer;` |

**影响**: Item 类双端加载。如果 import 的 renderer 类链式依赖 client API → 崩溃。

---

### P2-4: `CookingDishItem.java`

| 位置 | 问题 |
|------|------|
| **第 3 行** | `import com.stardew.craft.client.TooltipConstants;` |

**影响**: 需检查 `TooltipConstants` 是否依赖客户端类。如果只是常量定义则安全，如果引用了 `Font`/`GuiGraphics` 等则会崩溃。

---

### P2-5: Jade 集成 (`integration/jade/`)

| 文件 | 问题 |
|------|------|
| `CropFertilizerJadeProvider.java` | import `ClientFertilizerCache` |
| `FarmlandFertilizerJadeProvider.java` | import `ClientFertilizerCache` |

**影响**: Jade 是仅客户端 mod，但如果这些 Provider 在服务端被类加载器扫描到（例如通过反射注册），可能崩溃。需确认 Jade 集成是否有 `@OnlyIn` 或条件加载保护。

---

### P2-6: JEI 集成 (`integration/jei/`)

| 文件 | 客户端 import |
|------|-------------|
| `ArtisanRecipeCategory.java` | Minecraft, Font, GuiGraphics |
| `FishingInfoCategory.java` | Minecraft, Font, GuiGraphics |
| `GeodeProcessingCategory.java` | Minecraft, Font, GuiGraphics |
| `ShopInfoCategory.java` | Minecraft, Font, GuiGraphics |
| `StardewCraftingCategory.java` | Minecraft, Font, GuiGraphics |
| `JeiDrawHelper.java` | Font, GuiGraphics |

**影响**: JEI 是仅客户端 mod。如果服务端没有 JEI，这些类可能不会被加载（取决于注册方式）。但如果项目使用了 `@JeiPlugin` 注解或类路径扫描，可能在无 JEI 的服务端触发加载。需确认 JEI 集成入口是否有条件保护。

---

### P2-7: FertilizerSyncEvents / MuseumDonationSyncEvents

| 文件 | 问题 |
|------|------|
| `FertilizerSyncEvents.java` 第 4 行 | `import com.stardew.craft.client.ClientFertilizerCache;` |
| `MuseumDonationSyncEvents.java` 第 4 行 | `import com.stardew.craft.client.ClientMuseumDonationCache;` |

**影响**: 这两个文件的主类有 `@EventBusSubscriber` 无 Dist 限制（处理服务端事件），但 import 了客户端缓存类。如果客户端缓存类本身引用了 `net.minecraft.client.*`，则服务端类加载链会崩溃。需确认 `ClientFertilizerCache` 和 `ClientMuseumDonationCache` 是否有 client-only 依赖。

---

## ⚪ P3 — 应清理 (2 处)

| 文件 | 问题 |
|------|------|
| `TestRT.java` | import `net.minecraft.client.renderer.RenderType` — 测试残留文件 |
| `PrintMethods.java` | import `net.minecraft.client.gui.GuiGraphics` — 调试残留文件 |

**修复**: 删除这两个文件，或移入 `/client/` 包。

---

## ✅ 已正确实现的部分

| 部分 | 状态 |
|------|------|
| `StardewCraftClient.java` | `@Mod(dist = Dist.CLIENT)` ✅ |
| Mixin 配置 (`stardewcraft.mixins.json`) | `"client"` 数组正确分离 ✅ |
| Weather 渲染 (`WeatherDebrisRenderer`, `WeatherRainHider`, `ModParticleProviders`) | `@EventBusSubscriber(value = Dist.CLIENT)` ✅ |
| `/client/` 包下所有类 | 仅客户端加载 ✅ |
| `PaintbrushSelectionRenderer` | `value = Dist.CLIENT` ✅ |
| `DebugKeybinds` / `ModKeyMappings` | `value = Dist.CLIENT, bus = MOD` ✅ |

---

## 统一修复策略

### 方案 A: 移除客户端 import，使用 fully-qualified 内部类 (推荐)

适用于所有 Payload 类和 Block 类：

```java
// ✅ 修复后
public record OpenGeodeMenuPayload() implements CustomPacketPayload {
    public static void handle(OpenGeodeMenuPayload payload, IPayloadContext context) {
        context.enqueueWork(ClientHandler::open);
    }

    // 内部类被 @OnlyIn 保护，服务端永远不会尝试加载它
    @OnlyIn(Dist.CLIENT)
    private static final class ClientHandler {
        static void open() {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player == null) return;
            mc.setScreen(new com.stardew.craft.client.gui.GeodeMenuScreen());
        }
    }
}
```

### 方案 B: 对于主 Mod 类

将客户端专属的事件监听注册移入 `StardewCraftClient`：

```java
// StardewCraft.java — 删除 WeaponShaderRegistry import 和 addListener

// StardewCraftClient.java — 添加
modEventBus.addListener(WeaponShaderRegistry::onRegisterShadersSafe);
```

### 方案 C: 对于 DwarfService

使用服务端 API 获取语言：
```java
// ✅ 服务端安全
if (player instanceof ServerPlayer sp) {
    String lang = sp.clientInformation().language();
    isZh = lang != null && lang.startsWith("zh");
}
```

---

## 工作量估计

| 任务 | 文件数 | 优先级 |
|------|--------|--------|
| P0: StardewCraft.java shader 注册 | 1 | 必须 |
| P0: Block 类 (BulletinBoard + Joja) | 2 | 必须 |
| P1: 通用/钓鱼/战斗 Payload 类 | ~76 | 必须 |
| P2: DwarfService 语言检测 | 1 | 必须 |
| P2: WeaponTooltipBuilder + Item 类 | 3 | 必须 |
| P2: ScreenFade + CutscenePayload | 2 | 必须 |
| P2: Jade/JEI 集成条件加载验证 | 7 | 建议 |
| P2: Sync 事件类客户端缓存引用 | 2 | 建议 |
| P3: 删除测试残留 | 2 | 可选 |

---

## 验证清单

修复完成后，按以下步骤验证：

1. [ ] `./gradlew build` 编译无错
2. [ ] 使用 `./gradlew runServer` 或独立 server JAR 启动专用服务器
3. [ ] 检查服务端日志无 `ClassNotFoundException` / `NoClassDefFoundError`
4. [ ] 客户端连接服务端，验证所有 Payload 包双向通信正常
5. [ ] 测试所有 NPC 对话 / 商店 / 钓鱼 / 战斗技能效果在多人模式下正常
6. [ ] 测试矮人商店语言检测在服务端正确工作
