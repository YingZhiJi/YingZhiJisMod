package com.example.examplemod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SRequestTrade {

    public final int entityId;

    public C2SRequestTrade(int entityId) {
        this.entityId = entityId;
    }

    public C2SRequestTrade(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Entity entity = player.level().getEntity(this.entityId);
            if (entity instanceof LibrarianEntity librarian) {
                librarian.openTradeFor(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}