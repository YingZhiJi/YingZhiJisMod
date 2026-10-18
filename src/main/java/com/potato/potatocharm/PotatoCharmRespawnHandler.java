package com.potato.potatocharm;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "yingzhiji_s_mod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PotatoCharmRespawnHandler {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!PotatoCharmHelper.hasPotatoCharm(player)) return;

        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();
        BlockPos respawnPos = pos.getY() < -64
                ? new BlockPos(pos.getX(), 312, pos.getZ())
                : pos;

        player.setRespawnPosition(level.dimension(), respawnPos, player.getYRot(), true, false);
        player.invulnerableTime = 60;
    }
}