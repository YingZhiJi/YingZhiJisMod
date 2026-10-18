package com.example.examplemod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SRequestQuests {

    public final int entityId;

    public C2SRequestQuests(int entityId) {
        this.entityId = entityId;
    }

    public C2SRequestQuests(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            System.out.println("[Q] 服务端收到 C2SRequestQuests, entityId=" + entityId
                    + ", player=" + (player != null));
            if (player == null) return;
            Entity entity = player.level().getEntity(this.entityId);
            System.out.println("[Q] 查找实体: " + (entity == null ? "null" : entity.getClass().getSimpleName()));
            if (entity instanceof LibrarianEntity librarian) {
                System.out.println("[Q] 找到图书管理员，调用 sendQuestList");
                librarian.sendQuestList(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}