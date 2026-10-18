package com.potato.potatocharm;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PotatoCharmHelper {

    public static boolean hasPotatoCharm(Player player) {
        if (player == null) return false;
        if (player.getInventory() == null) return false;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof PotatoCharmItem) {
                return true;
            }
        }
        return false;
    }

    /** hue 单位是度（0~360），返回 0xRRGGBB */
    public static int rainbowColor(int hue) {
        float h = ((hue % 360) + 360) % 360 / 60f;
        int i = (int)Math.floor(h);
        float f = h - i;
        int vi = 255;
        int qi = (int)((1 - f) * 255);
        int ti = (int)(f * 255);
        switch (i) {
            case 0: return (vi << 16) | (ti << 8);
            case 1: return (qi << 16) | (vi << 8);
            case 2: return (vi << 8) | ti;
            case 3: return (qi << 8) | vi;
            case 4: return (ti << 16) | vi;
            default: return (vi << 16) | qi;
        }
    }
}