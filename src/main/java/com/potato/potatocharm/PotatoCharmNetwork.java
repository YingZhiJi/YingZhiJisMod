package com.potato.potatocharm;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PotatoCharmNetwork {

    private static final String VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("yingzhiji_s_mod", "potato_charm"),
            () -> VERSION,
            VERSION::equals,
            VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(C2SRequestDimList.class, id++)
                .encoder(C2SRequestDimList::encode)
                .decoder(C2SRequestDimList::new)
                .consumerMainThread(C2SRequestDimList::handle)
                .add();

        CHANNEL.messageBuilder(S2CDimList.class, id++)
                .encoder(S2CDimList::encode)
                .decoder(S2CDimList::new)
                .consumerMainThread(S2CDimList::handle)
                .add();

        CHANNEL.messageBuilder(C2STeleport.class, id++)
                .encoder(C2STeleport::encode)
                .decoder(C2STeleport::new)
                .consumerMainThread(C2STeleport::handle)
                .add();

        CHANNEL.messageBuilder(C2SPotatoAction.class, id++)
                .encoder(C2SPotatoAction::encode)
                .decoder(C2SPotatoAction::new)
                .consumerMainThread(C2SPotatoAction::handle)
                .add();
    }
}