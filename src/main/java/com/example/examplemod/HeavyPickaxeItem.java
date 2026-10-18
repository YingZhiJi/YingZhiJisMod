package com.example.examplemod;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class HeavyPickaxeItem extends DiggerItem {

    private static final float ATTACK_DAMAGE_MOD = 1.0F + 2.5F;
    private static final float ATTACK_SPEED_MOD = -2.8F;

    public HeavyPickaxeItem(Tier baseTier, Properties properties) {
        super(ATTACK_DAMAGE_MOD, ATTACK_SPEED_MOD,
                new DoubleDurabilityTier(baseTier),
                BlockTags.MINEABLE_WITH_PICKAXE,
                properties);
    }

    private static class DoubleDurabilityTier implements Tier {
        private final Tier base;

        DoubleDurabilityTier(Tier base) {
            this.base = base;
        }

        @Override public int getUses() { return base.getUses() * 2; }
        @Override public float getSpeed() { return base.getSpeed(); }
        @Override public float getAttackDamageBonus() { return base.getAttackDamageBonus(); }
        @Override public int getLevel() { return base.getLevel(); }
        @Override public int getEnchantmentValue() { return base.getEnchantmentValue(); }
        @Override public Ingredient getRepairIngredient() { return base.getRepairIngredient(); }
    }
}