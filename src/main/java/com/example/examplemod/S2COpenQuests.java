package com.example.examplemod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2COpenQuests {

    public final int entityId;
    public final List<QuestView> quests;

    public S2COpenQuests(int entityId, List<QuestView> quests) {
        this.entityId = entityId;
        this.quests = quests;
    }

    public S2COpenQuests(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        int n = buf.readVarInt();
        this.quests = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            this.quests.add(new QuestView(buf));
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.quests.size());
        for (QuestView q : this.quests) {
            q.encode(buf);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        System.out.println("[Q] S2COpenQuests.handle 被调用, entityId=" + entityId
                + ", quests.size=" + quests.size()
                + ", side=" + FMLEnvironment.dist);

        ctx.get().enqueueWork(() -> {
            System.out.println("[Q] enqueueWork 内部的 lambda 执行了");
            if (FMLEnvironment.dist == Dist.CLIENT) {
                System.out.println("[Q] 打开 QuestScreen");
                ClientScreenOpener.openQuests(this);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}