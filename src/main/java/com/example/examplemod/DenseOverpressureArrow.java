package com.example.examplemod;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class DenseOverpressureArrow extends Arrow {

    public float power = 1.0F;

    public DenseOverpressureArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
    }

    public DenseOverpressureArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.DENSE_OVERPRESSURE_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ExampleMod.DENSE_OVERPRESSURE_ARROW.get());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide()) return;

        Entity target = result.getEntity();
        if (!(target instanceof LivingEntity living)) return;

        // 击退强度也随拉弓力度缩放
        if (this.getOwner() != null) {
            double dx = living.getX() - this.getOwner().getX();
            double dz = living.getZ() - this.getOwner().getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len > 0.01) {
                dx /= len;
                dz /= len;
            }
            living.push(dx * 2.0 * this.power, 0.3 * this.power, dz * 2.0 * this.power);
            living.hurtMarked = true;
        }

        // 效果持续时长也随拉弓力度缩放
        int duration = (int) (120 * this.power);
        living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 1), this.getOwner());
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 1), this.getOwner());
    }
}