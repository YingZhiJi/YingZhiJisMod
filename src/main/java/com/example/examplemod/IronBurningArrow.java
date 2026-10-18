package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class IronBurningArrow extends Arrow {

    public IronBurningArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(1.0);
    }

    public IronBurningArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.IRON_BURNING_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
        this.setBaseDamage(1.0);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ExampleMod.IRON_BURNING_ARROW.get());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide()) return;
        if (result.getEntity() instanceof LivingEntity living) {
            if (!living.fireImmune()) {
                living.setSecondsOnFire(18);
                living.getPersistentData().putBoolean("yzj_burning", true);
            }
        }
    }
}