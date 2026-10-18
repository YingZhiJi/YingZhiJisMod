package com.example.examplemod;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class OldSaberBeltRenderer {

    /**
     * 返回应该挂在腰上的物品
     * 手里拿着军刀 → 挂空鞘
     * 背包有但不手持 → 挂带鞘军刀
     * 都没有 → 空 ItemStack
     */
    public static ItemStack getBeltItem(Player player) {
        if (isHoldingSaber(player)) {
            return new ItemStack(ExampleMod.OLD_SABER_SHEATH.get());
        } else if (hasSaberInInventory(player)) {
            return new ItemStack(ExampleMod.OLD_SABER.get());
        }
        return ItemStack.EMPTY;
    }

    private static boolean isHoldingSaber(Player player) {
        return player.getMainHandItem().is(ExampleMod.OLD_SABER.get())
                || player.getOffhandItem().is(ExampleMod.OLD_SABER.get());
    }

    private static boolean hasSaberInInventory(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(ExampleMod.OLD_SABER.get())) return true;
        }
        return false;
    }
}