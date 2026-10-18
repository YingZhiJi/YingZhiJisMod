package com.example.examplemod;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class OldSaberTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!event.getItemStack().is(ExampleMod.OLD_SABER.get())) return;

        // 拿到“攻击速度”这个属性的本地化名字（中文="攻击速度"，英文="Attack Speed"）
        String attackSpeedName = Component.translatable(Attributes.ATTACK_SPEED.getDescriptionId()).getString();

        // 从提示行里剔除含“攻击速度”的那一行
        event.getToolTip().removeIf(component ->
                component.getString().contains(attackSpeedName));
    }
}