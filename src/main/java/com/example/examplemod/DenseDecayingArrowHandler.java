package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class DenseDecayingArrowHandler {

    private static final double SPREAD_RADIUS = 5.0;
    private static final int SPREAD_WINDOW = 200;

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        CompoundTag data = entity.getPersistentData();
        int timer = data.getInt("yzj_decaying_spread");
        if (timer <= 0) return;

        data.putInt("yzj_decaying_spread", timer - 1);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();
        Level level = dead.level();
        if (level.isClientSide()) return;

        // 死因必须是凋零效果
        if (!event.getSource().is(net.minecraft.world.damagesource.DamageTypes.WITHER)) return;

        CompoundTag deadData = dead.getPersistentData();
        if (deadData.getInt("yzj_decaying_spread") <= 0) return;

        // 读取这条"殁亡链"的发射者 UUID
        UUID ownerUUID = deadData.hasUUID("yzj_decaying_owner")
                ? deadData.getUUID("yzj_decaying_owner")
                : null;

        double x = dead.getX();
        double y = dead.getY();
        double z = dead.getZ();

        // 向周围传播
        AABB area = new AABB(
                x - SPREAD_RADIUS, y - SPREAD_RADIUS, z - SPREAD_RADIUS,
                x + SPREAD_RADIUS, y + SPREAD_RADIUS, z + SPREAD_RADIUS
        );
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area);

        boolean spreadSuccess = false;

        for (LivingEntity target : targets) {
            if (target == dead) continue;
            if (!target.isAlive()) continue;
            // 敌我识别：跳过发射者
            if (ownerUUID != null && target.getUUID().equals(ownerUUID)) continue;

            // 施加完整殁亡效果
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 3));

            CompoundTag data = target.getPersistentData();
            data.putInt("yzj_decaying", 200);
            data.putInt("yzj_decaying_spread", SPREAD_WINDOW);
            if (ownerUUID != null) {
                data.putUUID("yzj_decaying_owner", ownerUUID);
            }

            spreadSuccess = true;
        }

        // 传播失败：原地留下凋零云
        if (!spreadSuccess) {
            DecayingCloudEntity.spawn(level, x, y, z, ownerUUID);
        }
    }
}