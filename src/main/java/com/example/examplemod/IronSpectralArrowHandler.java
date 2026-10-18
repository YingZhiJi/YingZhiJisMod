package com.example.examplemod;

import net.minecraft.world.entity.MobType;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class IronSpectralArrowHandler {

    // 第一阶段：护甲计算前，10.5 点物理伤害 × 拉弓力度
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof IronSpectralArrow arrow) {
            if (event.getEntity().getMobType() == MobType.UNDEAD) {
                event.setAmount(10.5F * arrow.power);
            }
        }
    }

    // 第二阶段：护甲计算后，8 点真实伤害 + 生命上限 5% × 拉弓力度
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getSource().getDirectEntity() instanceof IronSpectralArrow arrow) {
            if (event.getEntity().getMobType() == MobType.UNDEAD) {
                // 9 点真实伤害
                float trueDamage = 9.0F * arrow.power;

                // 生命上限 5% 的额外伤害，对所有亡灵一视同仁
                float percentDamage = event.getEntity().getMaxHealth() * 0.05F * arrow.power;

                event.setAmount(event.getAmount() + trueDamage + percentDamage);
            }
        }
    }
}