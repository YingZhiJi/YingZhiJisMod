package com.example.examplemod;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;

public class ModTiers {

    /**
     * 燃灰材质：
     * - 耐久 = 铁 × 2 = 500
     * - 挖掘等级/速度/攻击加成/附魔金 = 下界合金
     * - 修复材料 = 燃灰
     */
    public static final Tier ASHBLAZITE = new Tier() {
        @Override public int getUses() { return 500; }
        @Override public float getSpeed() { return Tiers.NETHERITE.getSpeed(); }
        @Override public float getAttackDamageBonus() { return Tiers.NETHERITE.getAttackDamageBonus(); }
        @Override public int getLevel() { return Tiers.NETHERITE.getLevel(); }
        @Override public int getEnchantmentValue() { return Tiers.NETHERITE.getEnchantmentValue(); }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(ModMaterials.ASHBLAZITE.get());
        }
    };
}