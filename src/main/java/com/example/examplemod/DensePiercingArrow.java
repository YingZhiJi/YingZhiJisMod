package com.example.examplemod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class DensePiercingArrow extends Arrow {

    public DensePiercingArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(2.5);
        this.setPierceLevel((byte) 8);
    }

    public DensePiercingArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.DENSE_PIERCING_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
        this.setBaseDamage(2.5);
        this.setPierceLevel((byte) 8);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY; // 落地即碎
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }
}