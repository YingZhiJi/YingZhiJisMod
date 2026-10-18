package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HealthBonusHandler {

    /** 死亡重生：把旧玩家的加成 NBT 搬到新玩家 */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
        if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;

        CompoundTag oldData = oldPlayer.getPersistentData();
        CompoundTag newData = newPlayer.getPersistentData();

        int abyssal = oldData.getInt(HealthBonusHelper.NBT_ABYSSAL);
        if (abyssal > 0) newData.putInt(HealthBonusHelper.NBT_ABYSSAL, abyssal);

        if (oldData.getBoolean(HealthBonusHelper.NBT_DESTINY)) {
            newData.putBoolean(HealthBonusHelper.NBT_DESTINY, true);
        }
        if (oldData.getBoolean(HealthBonusHelper.NBT_MAIDEN)) {
            newData.putBoolean(HealthBonusHelper.NBT_MAIDEN, true);
        }

        HealthBonusHelper.refresh(newPlayer);
    }

    /** 重生后立即再刷一次（保险） */
    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            HealthBonusHelper.refresh(player);
        }
    }

    /** 每 5 秒兜底刷新一次 */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.tickCount % 100 != 0) return;
        HealthBonusHelper.refresh(player);
    }
}