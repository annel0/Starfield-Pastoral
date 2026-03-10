package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.weather.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * 处理下雨时耕地自动湿润
 * 性能优化：只处理玩家附近32格范围内的耕地
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
public class FarmlandMoistureHandler {
    
    private static final int RADIUS = 32; // 玩家周围32格
    private static int lastWetDay = -1;
    
    /**
     * 定期检查并湿润玩家附近的耕地
     */
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        
        // 只在星露谷维度处理
        if (!level.dimension().equals(ModDimensions.STARDEW_VALLEY)) {
            return;
        }
        
        int currentDay = StardewTimeManager.get().getCurrentDay();
        if (currentDay == lastWetDay) {
            return;
        }
        
        // 获取当前天气
        String weather = WeatherManager.getCurrentWeather(level);
        boolean isRaining = "Rain".equals(weather) || "Storm".equals(weather);
        
        // 如果不是雨天，不处理
        if (!isRaining) {
            return;
        }
        
        // 遍历所有玩家，湿润其周围的耕地
        for (ServerPlayer player : level.players()) {
            moistenFarmlandAroundPlayer(level, player);
        }

        lastWetDay = currentDay;
    }
    
    /**
     * 湿润玩家周围的耕地
     */
    @SuppressWarnings("null")
    private static void moistenFarmlandAroundPlayer(ServerLevel level, ServerPlayer player) {
        BlockPos playerPos = player.blockPosition();
        // 只检查玩家周围32x32范围，高度±4
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                for (int y = -4; y <= 4; y++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    
                    if (state.getBlock() instanceof FarmBlock && state.hasProperty(BlockStateProperties.MOISTURE)) {
                        int currentMoisture = state.getValue(BlockStateProperties.MOISTURE);
                        if (currentMoisture < 7) {
                            BlockState wetState = state.setValue(BlockStateProperties.MOISTURE, 7);
                            level.setBlock(checkPos, wetState, 2);
                        }
                    }
                }
            }
        }
    }
}
