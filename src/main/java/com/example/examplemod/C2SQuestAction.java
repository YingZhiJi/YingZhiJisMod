package com.example.examplemod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SQuestAction {

    public final int entityId;
    public final String questId;
    public final String action;   // "accept" / "submit"

    public C2SQuestAction(int entityId, String questId, String action) {
        this.entityId = entityId;
        this.questId = questId;
        this.action = action;
    }

    public C2SQuestAction(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.questId = buf.readUtf();
        this.action = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeUtf(this.questId);
        buf.writeUtf(this.action);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Entity entity = player.level().getEntity(this.entityId);
            if (entity instanceof LibrarianEntity librarian) {
                librarian.handleQuestAction(player, this.questId, this.action);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}