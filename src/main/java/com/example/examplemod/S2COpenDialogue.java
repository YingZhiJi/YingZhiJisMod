package com.example.examplemod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2COpenDialogue {

    public final int entityId;
    public final String speaker;
    public final String text;
    public final String action;
    public final List<String> optionTexts;
    public final List<String> optionNexts;

    public S2COpenDialogue(int entityId, String speaker, String text, String action,
                           List<String> optionTexts, List<String> optionNexts) {
        this.entityId = entityId;
        this.speaker = speaker;
        this.text = text;
        this.action = action;
        this.optionTexts = optionTexts;
        this.optionNexts = optionNexts;
    }

    public S2COpenDialogue(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.speaker = buf.readUtf();
        this.text = buf.readUtf();
        this.action = buf.readBoolean() ? buf.readUtf() : null;
        int n = buf.readVarInt();
        this.optionTexts = new ArrayList<>(n);
        this.optionNexts = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            this.optionTexts.add(buf.readUtf());
            this.optionNexts.add(buf.readUtf());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeUtf(this.speaker);
        buf.writeUtf(this.text);
        if (this.action != null) {
            buf.writeBoolean(true);
            buf.writeUtf(this.action);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeVarInt(this.optionTexts.size());
        for (int i = 0; i < this.optionTexts.size(); i++) {
            buf.writeUtf(this.optionTexts.get(i));
            buf.writeUtf(this.optionNexts.get(i));
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ClientScreenOpener.openDialogue(this);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}