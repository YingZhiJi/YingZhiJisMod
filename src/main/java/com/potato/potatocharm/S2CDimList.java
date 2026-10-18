package com.potato.potatocharm;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2CDimList {

    public final List<String> dims;
    public final String currentDim;
    public final boolean timeLocked;

    public S2CDimList(List<String> dims, String currentDim, boolean timeLocked) {
        this.dims = dims;
        this.currentDim = currentDim;
        this.timeLocked = timeLocked;
    }

    public S2CDimList(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        this.dims = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.dims.add(buf.readUtf());
        }
        this.currentDim = buf.readUtf();
        this.timeLocked = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(dims.size());
        for (String s : dims) {
            buf.writeUtf(s);
        }
        buf.writeUtf(currentDim);
        buf.writeBoolean(timeLocked);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                PotatoCharmClientHandler.openScreen(dims, currentDim, timeLocked);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}