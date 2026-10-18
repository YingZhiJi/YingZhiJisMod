package com.potato.potatocharm;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "yingzhiji_s_mod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PotatoCharmActionTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!PotatoCharmHelper.hasPotatoCharm(player)) return;

        CompoundTag data = player.getPersistentData();

        int lockTime = data.getInt("potato_time_lock");
        if (lockTime >= 0) {
            ServerLevel level = player.serverLevel();
            long current = level.getDayTime();
            long mod = ((current % 24000L) + 24000L) % 24000L;
            long delta = lockTime - mod;
            if (Math.abs(delta) > 1) {
                level.setDayTime(current + delta);
            }
        }
    }
}