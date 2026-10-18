package com.potato.potatocharm;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PotatoCharmClientHandler {

    public static void openScreen(List<String> dims, String currentDim, boolean timeLocked) {
        Minecraft.getInstance().setScreen(new PotatoCharmScreen(dims, currentDim, timeLocked));
    }
}