package com.potato.potatocharm;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "yingzhiji_s_mod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PotatoCharmTravelGuard {

    @SubscribeEvent
    public static void onTravel(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!PotatoCharmHelper.hasPotatoCharm(player)) return;

        // 我们自己的菜单传送带有标记，放行
        if (player.getPersistentData().getBoolean("potato_allowed_tp")) {
            player.getPersistentData().remove("potato_allowed_tp");
            return;
        }

        // 其余一切维度切换全部阻止（末地门、传送门、模组传送、指令传送）
        event.setCanceled(true);
    }
}