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

public class IronSpectralArrow extends Arrow {

    // 拉弓力度，1.0 = 满拉，由 IronBowItem 发射时赋值
    public float power = 1.0F;

    public IronSpectralArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(1.0); // 降低基础伤害，原版是 2.0
    }

    public IronSpectralArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.IRON_SPECTRAL_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
        this.setBaseDamage(1.0); // 降低基础伤害，原版是 2.0
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ExampleMod.IRON_SPECTRAL_ARROW.get());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200), this.getOwner());
        }
    }
}