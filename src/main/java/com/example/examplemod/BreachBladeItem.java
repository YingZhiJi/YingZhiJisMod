package com.example.examplemod;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.state.BlockState;

public class BreachBladeItem extends SwordItem {

    public BreachBladeItem(Tier tier, int attackDamageMod, float attackSpeedMod, Properties properties) {
        super(tier, attackDamageMod, attackSpeedMod, properties);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (isBreachBladeEfficient(state)) {
            return this.getTier().getSpeed();
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        if (isBreachBladeEfficient(state)) return true;
        return super.isCorrectToolForDrops(state);
    }

    protected boolean isBreachBladeEfficient(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_AXE)
                || state.is(BlockTags.MINEABLE_WITH_HOE)
                || state.is(BlockTags.SWORD_EFFICIENT)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.WOOL)
                || state.is(BlockTags.CAVE_VINES)
                || state.is(BlockTags.REPLACEABLE_BY_TREES);
    }

    /**
     * 让排障刀在附魔台上可以吃 WEAPON + DIGGER + BREAKABLE + VANISHABLE 类附魔。
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        EnchantmentCategory cat = enchantment.category;
        return cat == EnchantmentCategory.WEAPON
                || cat == EnchantmentCategory.DIGGER
                || cat == EnchantmentCategory.BREAKABLE
                || cat == EnchantmentCategory.VANISHABLE;
    }

    /**
     * 让附魔书在铁砧上也能给排障刀加 DIGGER 类附魔。
     */
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }
}