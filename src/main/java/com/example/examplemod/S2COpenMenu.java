package com.example.examplemod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2COpenMenu {

    public final int entityId;

    public S2COpenMenu(int entityId) {
        this.entityId = entityId;
    }

    public S2COpenMenu(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ClientScreenOpener.openMenu(this.entityId);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}