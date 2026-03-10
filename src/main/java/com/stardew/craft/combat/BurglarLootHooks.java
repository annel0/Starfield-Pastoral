package com.stardew.craft.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface BurglarLootHooks {

    List<BurglarLootHooks> HOOKS = new CopyOnWriteArrayList<>();

    void onBurglarKill(LivingEntity entity, ServerPlayer player);

    static void register(BurglarLootHooks hook) {
        if (hook != null) {
            HOOKS.add(hook);
        }
    }

    static void unregister(BurglarLootHooks hook) {
        HOOKS.remove(hook);
    }

    static void fireBurglarKill(LivingEntity entity, ServerPlayer player) {
        if (entity == null || player == null) {
            return;
        }
        for (BurglarLootHooks hook : HOOKS) {
            hook.onBurglarKill(entity, player);
        }
    }
}
