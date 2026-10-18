package com.example.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("yingzhiji_s_mod", "main"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    public static void register() {
        int id = 0;

        CHANNEL.messageBuilder(S2COpenMenu.class, id++)
                .encoder(S2COpenMenu::encode)
                .decoder(S2COpenMenu::new)
                .consumerMainThread(S2COpenMenu::handle)
                .add();

        CHANNEL.messageBuilder(S2COpenDialogue.class, id++)
                .encoder(S2COpenDialogue::encode)
                .decoder(S2COpenDialogue::new)
                .consumerMainThread(S2COpenDialogue::handle)
                .add();

        CHANNEL.messageBuilder(S2COpenQuests.class, id++)
                .encoder(S2COpenQuests::encode)
                .decoder(S2COpenQuests::new)
                .consumerMainThread(S2COpenQuests::handle)
                .add();

        CHANNEL.messageBuilder(C2SOpenDialogNode.class, id++)
                .encoder(C2SOpenDialogNode::encode)
                .decoder(C2SOpenDialogNode::new)
                .consumerMainThread(C2SOpenDialogNode::handle)
                .add();

        CHANNEL.messageBuilder(C2SRequestTrade.class, id++)
                .encoder(C2SRequestTrade::encode)
                .decoder(C2SRequestTrade::new)
                .consumerMainThread(C2SRequestTrade::handle)
                .add();

        CHANNEL.messageBuilder(C2SRequestQuests.class, id++)
                .encoder(C2SRequestQuests::encode)
                .decoder(C2SRequestQuests::new)
                .consumerMainThread(C2SRequestQuests::handle)
                .add();

        CHANNEL.messageBuilder(C2SQuestAction.class, id++)
                .encoder(C2SQuestAction::encode)
                .decoder(C2SQuestAction::new)
                .consumerMainThread(C2SQuestAction::handle)
                .add();

        System.out.println("[Q] ModNetwork 注册完成, 共 " + id + " 个包");
    }
}