package com.potato.potatocharm;

import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "yingzhiji_s_mod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PotatoCharmCloneHandler {

    private static final String TAG_YROT = "potato_saved_yrot";
    private static final String TAG_XROT = "potato_saved_xrot";
    private static final String TAG_SELECTED = "potato_saved_selected";
    private static final String TAG_SYNC_PENDING = "potato_sync_pending";

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;
        if (!PotatoCharmHelper.hasPotatoCharm(player)) return;

        CompoundTag data = player.getPersistentData();
        data.putFloat(TAG_YROT, player.getYRot());
        data.putFloat(TAG_XROT, player.getXRot());
        data.putInt(TAG_SELECTED, player.getInventory().selected);
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
        if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;

        CompoundTag oldData = oldPlayer.getPersistentData();
        if (!oldData.contains(TAG_YROT)) return;

        float yRot = oldData.getFloat(TAG_YROT);
        float xRot = oldData.getFloat(TAG_XROT);
        int selected = oldData.getInt(TAG_SELECTED);

        newPlayer.setYRot(yRot);
        newPlayer.setXRot(xRot);
        newPlayer.yRotO = yRot;
        newPlayer.xRotO = xRot;
        newPlayer.setYHeadRot(yRot);
        newPlayer.setYBodyRot(yRot);
        newPlayer.getInventory().selected = selected;

        CompoundTag newData = newPlayer.getPersistentData();
        newData.putFloat(TAG_YROT, yRot);
        newData.putFloat(TAG_XROT, xRot);
        newData.putInt(TAG_SELECTED, selected);
        newData.putBoolean(TAG_SYNC_PENDING, true);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;

        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean(TAG_SYNC_PENDING)) return;

        // 只推一次 teleport 让客户端对齐初始视角，之后完全放手
        float yRot = data.getFloat(TAG_YROT);
        float xRot = data.getFloat(TAG_XROT);
        int selected = data.getInt(TAG_SELECTED);

        player.connection.teleport(
                player.getX(), player.getY(), player.getZ(),
                yRot, xRot
        );
        player.connection.send(new ClientboundSetCarriedItemPacket(selected));

        data.putBoolean(TAG_SYNC_PENDING, false);
    }
}