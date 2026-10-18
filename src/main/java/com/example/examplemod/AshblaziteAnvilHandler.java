package com.example.examplemod;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class AshblaziteAnvilHandler {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        // 只拦燃灰工具
        if (!AshblaziteTools.isAshblaziteTool(left)) return;

        // 右侧是燃灰材料 → 禁止用铁砧砸材料修复
        if (right.is(ModMaterials.ASHBLAZITE.get())) {
            event.setCanceled(true);
        }
    }
}