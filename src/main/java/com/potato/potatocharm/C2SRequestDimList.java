package com.potato.potatocharm;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class C2SRequestDimList {

    public C2SRequestDimList() {}

    public C2SRequestDimList(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!PotatoCharmHelper.hasPotatoCharm(player)) return;

            List<String> dims = new ArrayList<>();
            for (ServerLevel level : player.server.getAllLevels()) {
                dims.add(level.dimension().location().toString());
            }
            dims.sort(String::compareTo);

            String current = player.level().dimension().location().toString();
            boolean locked = player.getPersistentData().getInt("potato_time_lock") >= 0;

            PotatoCharmNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new S2CDimList(dims, current, locked)
            );
        });
        ctx.get().setPacketHandled(true);
    }
}