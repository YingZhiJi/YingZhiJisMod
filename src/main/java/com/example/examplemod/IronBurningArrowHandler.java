package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class IronBurningArrowHandler {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        CompoundTag data = entity.getPersistentData();
        if (!data.getBoolean("yzj_burning")) return;

        // 火灭了，清标记，停止伤害
        if (!entity.isOnFire()) {
            data.remove("yzj_burning");
            return;
        }

        // 每 10 tick 造成 0.1 真实伤害
        if (entity.tickCount % 10 == 0) {
            entity.invulnerableTime = 0;
            entity.hurt(entity.damageSources().onFire(), 0.1F);
        }
    }
}