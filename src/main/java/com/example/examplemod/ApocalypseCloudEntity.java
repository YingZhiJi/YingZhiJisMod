package com.example.examplemod;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.UUID;

public class ApocalypseCloudEntity extends Entity {

    private static final int LIFETIME = 200;
    private static final double RADIUS = 5.0;
    private static final double PULL_RADIUS = 8.0;
    private static final double PULL_FORCE = 0.25;

    private UUID ownerUUID;
    private int age;

    public ApocalypseCloudEntity(EntityType<? extends ApocalypseCloudEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static void spawn(Level level, double x, double y, double z, LivingEntity owner) {
        ApocalypseCloudEntity cloud = new ApocalypseCloudEntity(ExampleMod.APOCALYPSE_CLOUD_ENTITY.get(), level);
        cloud.setPos(x, y - 1.0, z);
        cloud.ownerUUID = owner != null ? owner.getUUID() : null;
        level.addFreshEntity(cloud);
    }

    @Override
    public void tick() {
        super.tick();
        this.age++;

        if (this.level().isClientSide()) {
            for (int i = 0; i < 6; i++) {
                double angle = this.random.nextDouble() * Math.PI * 2;
                double r = Math.sqrt(this.random.nextDouble()) * RADIUS;
                double dx = r * Math.cos(angle);
                double dz = r * Math.sin(angle);
                double dy = this.random.nextDouble() * 3.0;
                this.level().addParticle(
                        new DustParticleOptions(new Vector3f(1.0F, 0.05F, 0.05F), 1.0F),
                        this.getX() + dx, this.getY() + 0.2 + dy, this.getZ() + dz,
                        0.0, 0.01, 0.0);
            }
            return;
        }

        if (this.age % 10 == 0) {
            AABB area = new AABB(
                    this.getX() - RADIUS, this.getY() - RADIUS, this.getZ() - RADIUS,
                    this.getX() + RADIUS, this.getY() + RADIUS, this.getZ() + RADIUS
            );
            for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
                if (this.ownerUUID != null && living.getUUID().equals(this.ownerUUID)) continue;
                double dist = Math.sqrt(living.distanceToSqr(this.getX(), this.getY(), this.getZ()));
                if (dist > RADIUS) continue;

                living.getPersistentData().putInt("yzj_apocalypse_window", 10);

                if (living.getMobType() == MobType.UNDEAD) {
                    living.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));
                } else {
                    living.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
                }
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 1));
            }
        }

        AABB pullArea = new AABB(
                this.getX() - PULL_RADIUS, this.getY() - PULL_RADIUS, this.getZ() - PULL_RADIUS,
                this.getX() + PULL_RADIUS, this.getY() + PULL_RADIUS, this.getZ() + PULL_RADIUS
        );
        for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, pullArea)) {
            if (this.ownerUUID != null && living.getUUID().equals(this.ownerUUID)) continue;
            if (living instanceof net.minecraft.world.entity.player.Player) continue;

            double dx = this.getX() - living.getX();
            double dy = this.getY() - living.getY();
            double dz = this.getZ() - living.getZ();
            double len = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (len > 1.0 && len <= PULL_RADIUS) {
                living.push(dx / len * PULL_FORCE, dy / len * PULL_FORCE * 0.3, dz / len * PULL_FORCE);
                living.hurtMarked = true;
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