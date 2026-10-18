package com.example.examplemod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SOpenDialogNode {

    public final int entityId;
    public final String nodeId;

    public C2SOpenDialogNode(int entityId, String nodeId) {
        this.entityId = entityId;
        this.nodeId = nodeId;
    }

    public C2SOpenDialogNode(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.nodeId = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeUtf(this.nodeId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Entity entity = player.level().getEntity(this.entityId);
            if (entity instanceof LibrarianEntity librarian) {
                librarian.sendDialogueNode(player, this.nodeId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}