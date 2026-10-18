package com.potato.potatocharm;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SPotatoAction {

    public static final int ACTION_SET_SURVIVAL     = 0;
    public static final int ACTION_SET_CREATIVE     = 1;
    public static final int ACTION_SET_TIME         = 2;
    public static final int ACTION_TOGGLE_TIME_LOCK = 3;
    public static final int ACTION_POTATO_BURST     = 4;

    public final int action;
    public final int value;

    public C2SPotatoAction(int action) {
        this(action, 0);
    }

    public C2SPotatoAction(int action, int value) {
        this.action = action;
        this.value = value;
    }

    public C2SPotatoAction(FriendlyByteBuf buf) {
        this.action = buf.readVarInt();
        this.value = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(action);
        buf.writeVarInt(value);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!PotatoCharmHelper.hasPotatoCharm(player)) return;

            CompoundTag data = player.getPersistentData();

            switch (action) {
                case ACTION_SET_SURVIVAL -> player.setGameMode(GameType.SURVIVAL);
                case ACTION_SET_CREATIVE -> player.setGameMode(GameType.CREATIVE);
                case ACTION_SET_TIME -> {
                    ServerLevel level = player.serverLevel();
                    level.setDayTime(value);
                    if (data.getInt("potato_time_lock") >= 0) {
                        data.putInt("potato_time_lock", value);
                    }
                }
                case ACTION_TOGGLE_TIME_LOCK -> {
                    int cur = data.getInt("potato_time_lock");
                    if (cur >= 0) {
                        data.putInt("potato_time_lock", -1);
                    } else {
                        long dayTime = player.serverLevel().getDayTime();
                        int tod = (int)(((dayTime % 24000L) + 24000L) % 24000L);
                        data.putInt("potato_time_lock", tod);
                    }
                }
                case ACTION_POTATO_BURST -> doPotatoBurst(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static void doPotatoBurst(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        double px = player.getX();
        double py = player.getY() + 1.2;
        double pz = player.getZ();

        ItemStack proto = new ItemStack(Items.POTATO);

        int rings = 3;
        int perRing = 12;

        for (int ring = 0; ring < rings; ring++) {
            double radius = 0.8 + ring * 0.5;
            double upward = 0.35 + ring * 0.12;
            double outward = 0.8 - ring * 0.15;

            for (int i = 0; i < perRing; i++) {
                double angle = (Math.PI * 2 * i / perRing) + ring * 0.25;
                double dx = Math.cos(angle);
                double dz = Math.sin(angle);

                ItemEntity item = new ItemEntity(level,
                        px + dx * radius, py, pz + dz * radius,
                        proto.copy());
                item.setDeltaMovement(dx * outward, upward, dz * outward);
                try {
                    java.lang.reflect.Field f = ItemEntity.class.getDeclaredField("pickupDelay");
                    f.setAccessible(true);
                    f.setInt(item, 40);
                } catch (ReflectiveOperationException ignored) {}
                level.addFreshEntity(item);
            }
        }
    }
}