package com.stardew.craft.weather;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义粒子类型注册
 */
public class ModParticles {
    
    @SuppressWarnings("null")
    public static final DeferredRegister<ParticleType<?>> PARTICLES = 
        DeferredRegister.create(Registries.PARTICLE_TYPE, StardewCraft.MODID);

    // 橙色秋叶粒子
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> AUTUMN_LEAF_ORANGE = 
        PARTICLES.register("autumn_leaf_orange", () -> new SimpleParticleType(false));

    // 黄色秋叶粒子
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> AUTUMN_LEAF_YELLOW = 
        PARTICLES.register("autumn_leaf_yellow", () -> new SimpleParticleType(false));
    
    // 自定义雪花粒子
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CUSTOM_SNOWFLAKE = 
        PARTICLES.register("custom_snowflake", () -> new SimpleParticleType(false));

    // 暗黄色油泡粒子
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> OIL_BUBBLE = 
        PARTICLES.register("oil_bubble", () -> new SimpleParticleType(false));
}
