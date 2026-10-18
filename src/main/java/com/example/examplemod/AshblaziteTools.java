package com.example.examplemod;

import net.minecraft.world.item.ItemStack;

public class AshblaziteTools {

    public static boolean isAshblaziteTool(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(ExampleMod.ASHBLAZITE_SWORD.get())
                || stack.is(ExampleMod.ASHBLAZITE_PICKAXE.get())
                || stack.is(ExampleMod.ASHBLAZITE_AXE.get())
                || stack.is(ExampleMod.ASHBLAZITE_SHOVEL.get())
                || stack.is(ExampleMod.ASHBLAZITE_HOE.get())
                || stack.is(ExampleMod.ASHBLAZITE_SLASH_SWORD.get())
                || stack.is(ExampleMod.ASHBLAZITE_HEAVY_PICKAXE.get());
    }
}