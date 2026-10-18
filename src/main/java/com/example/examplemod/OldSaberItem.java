package com.example.examplemod;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;

public class OldSaberItem extends SwordItem {

    public static final Tier OLD_SABER_TIER = new Tier() {
        @Override public int getUses() { return 1024; }
        @Override public float getSpeed() { return Tiers.IRON.getSpeed(); }
        @Override public float getAttackDamageBonus() { return Tiers.IRON.getAttackDamageBonus(); }
        @Override public int getLevel() { return Tiers.IRON.getLevel(); }
        @Override public int getEnchantmentValue() { return Tiers.IRON.getEnchantmentValue(); }
        @Override public Ingredient getRepairIngredient() { return Tiers.IRON.getRepairIngredient(); }
    };

    public OldSaberItem(Properties properties) {
        super(OLD_SABER_TIER, 4, 96.0F, properties);
    }

    // ===== 右键格挡 =====
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.world.item.enchantment.Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.old_saber.desc.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.old_saber.desc.2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.old_saber.desc.3").withStyle(ChatFormatting.DARK_GRAY));
    }
}