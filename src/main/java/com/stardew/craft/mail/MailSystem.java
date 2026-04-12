package com.stardew.craft.mail;

import com.stardew.craft.StardewCraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

/**
 * 邮件系统事件钩子：注册数据包重载监听器。
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
public final class MailSystem {
    private MailSystem() {}

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new MailReloadListener());
    }

    private static final class MailReloadListener extends SimplePreparableReloadListener<Void> {
        @Override
        protected Void prepare(@SuppressWarnings("null") ResourceManager resourceManager,
                               @SuppressWarnings("null") ProfilerFiller profiler) {
            return null;
        }

        @Override
        protected void apply(@SuppressWarnings("null") Void nothing,
                             @SuppressWarnings("null") ResourceManager resourceManager,
                             @SuppressWarnings("null") ProfilerFiller profiler) {
            MailRegistry.reload(resourceManager);
        }
    }
}
