package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ApocalypseAnimationEntity extends Entity {

    private static final int LIFETIME = 200; // 10 秒

    private int age;

    public ApocalypseAnimationEntity(EntityType<? extends ApocalypseAnimationEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static void spawn(Level level, double x, double y, double z) {
        ApocalypseAnimationEntity anim = new ApocalypseAnimationEntity(ExampleMod.APOCALYPSE_ANIMATION_ENTITY.get(), level);
        anim.setPos(x, y + 0.15, z);
        level.addFreshEntity(anim);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.age++;
            if (this.age >= LIFETIME) {
                this.discard();
            }
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.age = tag.getInt("Age");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", this.age);
    }
}