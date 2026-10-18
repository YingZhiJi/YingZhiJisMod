package com.example.examplemod;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class IronPoisonArrow extends Arrow {

    public IronPoisonArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(1.0);
    }

    public IronPoisonArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.IRON_POISON_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
        this.setBaseDamage(1.0);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ExampleMod.IRON_POISON_ARROW.get());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide()) return;
        if (result.getEntity() instanceof LivingEntity living) {
            // 18 秒 = 360 tick，中毒 I（等级 0）
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 360, 0), this.getOwner());
        }
    }
}