package com.stardew.craft.menu;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 菜单类型注册
 */
public class ModMenuTypes {
    
    @SuppressWarnings("null")
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = 
        DeferredRegister.create(Registries.MENU, StardewCraft.MODID);
    
    // 矿井出口菜单（简单的无额外数据Menu）
    @SuppressWarnings("null")
    public static final DeferredHolder<MenuType<?>, MenuType<MineExitMenu>> MINE_EXIT = 
        MENU_TYPES.register("mine_exit", 
            () -> new MenuType<>(MineExitMenu::new, FeatureFlags.DEFAULT_FLAGS));

    // 矿井电梯菜单
    @SuppressWarnings("null")
    public static final DeferredHolder<MenuType<?>, MenuType<ElevatorMenu>> ELEVATOR =
        MENU_TYPES.register("elevator", 
            () -> new MenuType<>(ElevatorMenu::new, FeatureFlags.DEFAULT_FLAGS));

    // 鸡舍管理器菜单
    @SuppressWarnings("null")
    public static final DeferredHolder<MenuType<?>, MenuType<CoopManagerMenu>> COOP_MANAGER =
        MENU_TYPES.register("coop_manager",
            () -> new MenuType<>(CoopManagerMenu::new, FeatureFlags.DEFAULT_FLAGS));

    // 畜棚管理器菜单
    @SuppressWarnings("null")
    public static final DeferredHolder<MenuType<?>, MenuType<BarnManagerMenu>> BARN_MANAGER =
        MENU_TYPES.register("barn_manager",
            () -> new MenuType<>(BarnManagerMenu::new, FeatureFlags.DEFAULT_FLAGS));

    // 动物信息面板菜单
    @SuppressWarnings("null")
    public static final DeferredHolder<MenuType<?>, MenuType<AnimalQueryMenu>> ANIMAL_QUERY =
        MENU_TYPES.register("animal_query",
            () -> new MenuType<>(AnimalQueryMenu::new, FeatureFlags.DEFAULT_FLAGS));

    @SuppressWarnings("null")
    public static final DeferredHolder<MenuType<?>, MenuType<com.stardew.craft.fishing.TreasureChestMenu>> TREASURE_CHEST =
        MENU_TYPES.register("treasure_chest",
            () -> new MenuType<>(com.stardew.craft.fishing.TreasureChestMenu::new, FeatureFlags.DEFAULT_FLAGS));

    @SuppressWarnings("null")
    public static final DeferredHolder<MenuType<?>, MenuType<CookingPotMenu>> COOKING_POT =
        MENU_TYPES.register("cooking_pot",
            () -> new MenuType<>(CookingPotMenu::new, FeatureFlags.DEFAULT_FLAGS));

    @SuppressWarnings("null")
    public static final DeferredHolder<MenuType<?>, MenuType<WoodenChestMenu>> WOODEN_CHEST =
        MENU_TYPES.register("wooden_chest",
            () -> new MenuType<>(WoodenChestMenu::new, FeatureFlags.DEFAULT_FLAGS));

    @SuppressWarnings("null")
    public static final DeferredHolder<MenuType<?>, MenuType<StoneChestMenu>> STONE_CHEST =
        MENU_TYPES.register("stone_chest",
            () -> new MenuType<>(StoneChestMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
