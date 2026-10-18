package com.example.examplemod;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class SlashSwordItem extends SwordItem {

    // 攻击伤害修正：剑是 3，斩剑是 2（总伤比同材质剑低 1）
    private static final int ATTACK_DAMAGE_MOD = 2;

    // 攻击速度修正：剑是 -2.4（1.6 攻速），斩剑是 -2.6（1.4 攻速）
    private static final float ATTACK_SPEED_MOD = -2.6F;

    public SlashSwordItem(Tier tier, Properties properties) {
        super(tier, ATTACK_DAMAGE_MOD, ATTACK_SPEED_MOD, properties);
    }
}