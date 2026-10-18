package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class DenseArrowHandler {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        CompoundTag data = entity.getPersistentData();

        // 殁亡：无敌帧清零
        int decaying = data.getInt("yzj_decaying");
        if (decaying > 0) {
            entity.invulnerableTime = 0;
            data.putInt("yzj_decaying", decaying - 1);
        }

        // 天启：图腾破解窗口倒计时
        int window = data.getInt("yzj_apocalypse_window");
        if (window > 0) {
            data.putInt("yzj_apocalypse_window", window - 1);
        }
    }

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (event.getDamageSource().getDirectEntity() instanceof DenseDeepSculkArrow
                || event.getDamageSource().getDirectEntity() instanceof DenseApocalypseArrow
                || event.getDamageSource().getDirectEntity() instanceof ApocalypseCloudEntity
                || event.getDamageSource().getDirectEntity() instanceof DecayingCloudEntity) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onUseTotem(LivingUseTotemEvent event) {
        LivingEntity entity = event.getEntity();

        // 被天启伤害过 → 破解图腾
        if (entity.getPersistentData().getInt("yzj_apocalypse_window") > 0) {
            event.setCanceled(true);
            entity.kill();
        }
    }
}