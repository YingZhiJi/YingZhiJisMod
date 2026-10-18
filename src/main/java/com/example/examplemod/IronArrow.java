package com.example.examplemod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IronArrow extends Arrow {

    public IronArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
    }

    public IronArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.IRON_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ExampleMod.IRON_ARROW.get());
    }
}