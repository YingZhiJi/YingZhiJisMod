package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientScreenOpener {

    public static void openMenu(int entityId) {
        Minecraft.getInstance().setScreen(new LibrarianMenuScreen(entityId));
    }

    public static void openDialogue(S2COpenDialogue packet) {
        Minecraft.getInstance().setScreen(new DialogueScreen(packet));
    }

    public static void openQuests(S2COpenQuests packet) {
        QuestScreen screen = new QuestScreen(packet);
        // 保留上一个 QuestScreen 的选中任务
        if (Minecraft.getInstance().screen instanceof QuestScreen old) {
            String prev = old.getSelectedId();
            if (prev != null) screen.setSelectedId(prev);
        }
        Minecraft.getInstance().setScreen(screen);
    }
}