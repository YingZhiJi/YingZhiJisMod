package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class DecayingSoulEntity extends Entity {

    private static final int LIFETIME = 20; // 1 秒
    private int age;

    public DecayingSoulEntity(EntityType<? extends DecayingSoulEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static void spawn(Level level, double x, double y, double z) {
        DecayingSoulEntity soul = new DecayingSoulEntity(ExampleMod.DECAYING_SOUL_ENTITY.get(), level);
        soul.setPos(x, y, z);
        level.addFreshEntity(soul);
    }

    @Override
    public void tick() {
        super.tick();
        this.age++;

        if (this.level().isClientSide()) {
            // 每 tick 生成一圈灵魂粒子，从外向内汇聚
            for (int i = 0; i < 5; i++) {
                double angle = this.random.nextDouble() * Math.PI * 2;
                double radius = 2.0 + this.random.nextDouble() * 2.0;
                double dx = Math.cos(angle) * radius;
                double dz = Math.sin(angle) * radius;
                double dy = (this.random.nextDouble() - 0.5) * 1.5;

                // 速度向量指向中心
                double speedFactor = 0.12;
                this.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.SOUL,
                        this.getX() + dx,
                        this.getY() + dy,
                        this.getZ() + dz,
                        -dx * speedFactor,
                        -dy * speedFactor,
                        -dz * speedFactor);
            }
        }

        if (this.age >= LIFETIME) {
            this.discard();
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