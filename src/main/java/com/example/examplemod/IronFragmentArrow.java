package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class IronFragmentArrow extends Arrow {

    public float power = 1.0F;

    public IronFragmentArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(0.5);
    }

    public IronFragmentArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.IRON_FRAGMENT_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
        this.setBaseDamage(0.5);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY; // 落地即碎，无法回收
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide()) return;
        if (result.getEntity() instanceof LivingEntity living) {
            CompoundTag data = living.getPersistentData();
            // 40 tick 内 40 次，每次 0.5 × 拉弓力度
            data.putInt("yzj_fragmenting", 40);
            data.putFloat("yzj_fragment_power", this.power);
        }
        this.discard(); // 命中实体后直接消失
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide()) {
            this.discard(); // 落地即碎
        }
    }
}