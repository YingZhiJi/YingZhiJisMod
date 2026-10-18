package com.potato.potatocharm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2STeleport {

    public static final int MODE_CURRENT = 0;
    public static final int MODE_ORIGIN  = 1;
    public static final int MODE_CUSTOM  = 2;

    public final int mode;
    public final String dimId;
    public final double x, y, z;

    public C2STeleport(int mode, String dimId, double x, double y, double z) {
        this.mode = mode;
        this.dimId = dimId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public C2STeleport(FriendlyByteBuf buf) {
        this.mode = buf.readVarInt();
        this.dimId = buf.readUtf();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(mode);
        buf.writeUtf(dimId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!PotatoCharmHelper.hasPotatoCharm(player)) return;

            ResourceLocation loc = new ResourceLocation(dimId);
            ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, loc);
            ServerLevel target = player.server.getLevel(key);
            if (target == null) return;

            switch (mode) {
                case MODE_CURRENT -> doTeleport(player, target,
                        player.getX(), player.getY(), player.getZ(),
                        player.getYRot(), player.getXRot());
                case MODE_ORIGIN -> {
                    BlockPos spawn = target.getSharedSpawnPos();
                    doTeleport(player, target,
                            spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5,
                            player.getYRot(), player.getXRot());
                }
                case MODE_CUSTOM -> {
                    double finalY = Math.max(y, target.getMinBuildHeight() + 1);
                    doTeleport(player, target, x, finalY, z,
                            player.getYRot(), player.getXRot());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static void doTeleport(ServerPlayer player, ServerLevel target,
                                   double x, double y, double z,
                                   float yRot, float xRot) {
        player.getPersistentData().putBoolean("potato_allowed_tp", true);
        player.teleportTo(target, x, y, z, yRot, xRot);
        player.setDeltaMovement(0, 0, 0);
        player.fallDistance = 0;
        player.invulnerableTime = 60;
    }
}