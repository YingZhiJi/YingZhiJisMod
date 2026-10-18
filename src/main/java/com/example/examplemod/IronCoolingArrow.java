package com.example.examplemod;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class IronCoolingArrow extends Arrow {

    public float power = 1.0F;

    public IronCoolingArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(1.0);
    }

    public IronCoolingArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.IRON_COOLING_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
        this.setBaseDamage(1.0);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ExampleMod.IRON_COOLING_ARROW.get());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide()) return;
        if (result.getEntity() instanceof LivingEntity living) {

            // 1. 灭火：直接扑灭目标身上的火
            living.clearFire();

            // 2. 3 秒抗火（60 tick）
            living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0), this.getOwner());

            // 3. 细雪冰冻效果：直接给 140 tick 冰冻值
            //    细雪里满冰冻是 140 tick，达到后开始掉冰冻伤害
            living.setTicksFrozen(140);

            // 4. 对所有生物附加 4 秒迟缓 III
            if (living != this.getOwner()) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2), this.getOwner());
            }

            // 5. 对弱水生物造成最高 20 额外伤害
            if (isWaterWeak(living)) {
                living.hurt(this.damageSources().arrow(this, this.getOwner()), 20.0F * this.power);
            }
        }
    }

    private boolean isWaterWeak(LivingEntity entity) {
        return entity.getType() == EntityType.BLAZE
                || entity.getType() == EntityType.MAGMA_CUBE
                || entity.getType() == EntityType.STRIDER;
    }
}