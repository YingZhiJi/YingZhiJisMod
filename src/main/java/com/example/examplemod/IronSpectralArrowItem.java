package com.example.examplemod;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class IronSpectralArrowItem extends ArrowItem {

    public IronSpectralArrowItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.iron_spectral_arrow.desc.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.iron_spectral_arrow.desc.2").withStyle(ChatFormatting.DARK_GRAY));
    }
}