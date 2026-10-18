package com.potato.potatocharm;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "yingzhiji_s_mod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PotatoCharmImmunityHandler {

    /**
     * 伤害事件：取消所有不绕过无敌的伤害
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!PotatoCharmHelper.hasPotatoCharm(player)) return;

        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

        event.setCanceled(true);
    }

    /**
     * 攻击事件：比 LivingHurtEvent 更早，拦下攻击本身
     */
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!PotatoCharmHelper.hasPotatoCharm(player)) return;

        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

        event.setCanceled(true);
    }

    /**
     * 击退事件：免疫一切强制位移（击退、爆炸推力、攻击退后）
     */
    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!PotatoCharmHelper.hasPotatoCharm(player)) return;

        event.setCanceled(true);
    }

    /**
     * 状态效果事件：拒绝一切药水效果（含正面和负面）
     */
    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!PotatoCharmHelper.hasPotatoCharm(player)) return;

        event.setResult(Event.Result.DENY);
    }
}