package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class DenseDeepSculkArrowHandler {

    // 沉默时长：3 秒 = 60 tick
    public static final int SILENCE_DURATION = 60;

    /**
     * 判断实体是否可被沉默
     * Boss 免疫：凋灵、末影龙、监守者
     */
    public static boolean isSilenceable(LivingEntity entity) {
        return !(entity instanceof WitherBoss)
                && !(entity instanceof EnderDragon)
                && !(entity instanceof Warden);
    }

    /**
     * 施加沉默
     */
    public static void applySilence(LivingEntity entity) {
        if (!isSilenceable(entity)) return;

        CompoundTag data = entity.getPersistentData();

        // 第一次被沉默时，记录它原本的静音状态
        if (data.getInt("yzj_silenced") <= 0) {
            data.putBoolean("yzj_was_silent", entity.isSilent());
        }
        data.putInt("yzj_silenced", SILENCE_DURATION);
    }

    /**
     * 每 tick 更新沉默状态
     */
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        CompoundTag data = entity.getPersistentData();
        int timer = data.getInt("yzj_silenced");
        if (timer <= 0) return;

        // 1. 静音
        entity.setSilent(true);

        // 2. 清除攻击目标
        if (entity instanceof Mob mob) {
            mob.setTarget(null);
        }

        // 3. 计时器递减
        int next = timer - 1;
        data.putInt("yzj_silenced", next);

        // 4. 时间到了，恢复原静音状态
        if (next <= 0) {
            entity.setSilent(data.getBoolean("yzj_was_silent"));
        }
    }

    /**
     * 拦截被沉默实体的攻击
     */
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (attacker.getPersistentData().getInt("yzj_silenced") > 0) {
                event.setCanceled(true);
            }
        }
    }
}