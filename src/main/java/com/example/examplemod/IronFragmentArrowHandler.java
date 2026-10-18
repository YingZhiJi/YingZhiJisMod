package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class IronFragmentArrowHandler {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        CompoundTag data = entity.getPersistentData();
        int remaining = data.getInt("yzj_fragmenting");
        if (remaining <= 0) return;

        float power = data.getFloat("yzj_fragment_power");

        // 原始伤害
        float rawDamage = 0.5F * power;

        // 手动计算护甲减免（护甲值 + 韧性 + 保护附魔）
        float armor = entity.getArmorValue();
        float toughness = (float) entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        float reducedDamage = CombatRules.getDamageAfterAbsorb(rawDamage, armor, toughness);

        entity.invulnerableTime = 0;
        entity.hurt(entity.damageSources().generic(), reducedDamage);

        data.putInt("yzj_fragmenting", remaining - 1);
    }
}