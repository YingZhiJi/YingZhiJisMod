package com.example.examplemod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DenseArrow extends Arrow {

    public DenseArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(4.0);
    }

    public DenseArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.DENSE_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
        this.setBaseDamage(4.0);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ExampleMod.DENSE_ARROW.get());
    }
}