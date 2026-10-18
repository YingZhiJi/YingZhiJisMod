package com.example.examplemod;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public class DecayingCloudEntity extends Entity {

    private static final int LIFETIME = 100;      // 5 秒
    private static final double RADIUS = 3.0;     // 半径

    private UUID ownerUUID;
    private int age;

    public DecayingCloudEntity(EntityType<? extends DecayingCloudEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static void spawn(Level level, double x, double y, double z, UUID owner) {
        DecayingCloudEntity cloud = new DecayingCloudEntity(ExampleMod.DECAYING_CLOUD_ENTITY.get(), level);
        cloud.setPos(x, y, z);
        cloud.ownerUUID = owner;
        level.addFreshEntity(cloud);
    }

    @Override
    public void tick() {
        super.tick();
        this.age++;

        if (this.level().isClientSide()) {
            // 凋零粒子云
            for (int i = 0; i < 4; i++) {
                double angle = this.random.nextDouble() * Math.PI * 2;
                double r = Math.sqrt(this.random.nextDouble()) * RADIUS;
                double dx = r * Math.cos(angle);
                double dz = r * Math.sin(angle);
                double dy = this.random.nextDouble() * 1.5;
                this.level().addParticle(
                        ParticleTypes.SOUL,
                        this.getX() + dx, this.getY() + 0.2 + dy, this.getZ() + dz,
                        0, 0.02, 0);
            }
            return;
        }

        // 服务端：每 10 tick 施加凋零效果
        if (this.age % 10 == 0) {
            AABB area = new AABB(
                    this.getX() - RADIUS, this.getY() - RADIUS, this.getZ() - RADIUS,
                    this.getX() + RADIUS, this.getY() + RADIUS, this.getZ() + RADIUS
            );
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
            for (LivingEntity target : targets) {
                // 排除主人
                if (this.ownerUUID != null && target.getUUID().equals(this.ownerUUID)) continue;
                double dist = Math.sqrt(target.distanceToSqr(this.getX(), this.getY(), this.getZ()));
                if (dist > RADIUS) continue;

                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 1));
                // 标记这个目标也属于同一个"殁亡链"
                target.getPersistentData().putUUID("yzj_decaying_owner", this.ownerUUID);
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
        if (tag.hasUUID("Owner")) this.ownerUUID = tag.getUUID("Owner");
        this.age = tag.getInt("Age");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.ownerUUID != null) tag.putUUID("Owner", this.ownerUUID);
        tag.putInt("Age", this.age);
    }
}