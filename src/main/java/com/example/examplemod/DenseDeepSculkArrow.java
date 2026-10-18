package com.example.examplemod;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;

public class DenseDeepSculkArrow extends Arrow {

    public float power = 1.0F;

    private static final float DIRECT_DAMAGE = 25.0F;
    private static final float SPLASH_DAMAGE = 10.0F;
    private static final double AREA_RADIUS = 5.0;

    public DenseDeepSculkArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(0.0);
    }

    public DenseDeepSculkArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.DENSE_DEEPSCULK_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
        this.setBaseDamage(0.0);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide() && !this.isRemoved()) {
            if (this.tickCount % 2 == 0) {
                this.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.SONIC_BOOM,
                        this.getX(), this.getY() + 0.2, this.getZ(),
                        0, 0, 0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide()) {
            net.minecraft.world.entity.Entity target = result.getEntity();
            triggerSonicBoom(
                    target.getX(),
                    target.getY() + target.getBbHeight() / 2.0,
                    target.getZ(),
                    target
            );
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide()) {
            triggerSonicBoom(this.getX(), this.getY(), this.getZ(), null);
        }
    }

    private void triggerSonicBoom(double x, double y, double z,
                                  net.minecraft.world.entity.Entity directHit) {
        Level level = this.level();

        // 1. 音效
        level.playSound(null, x, y, z, SoundEvents.WARDEN_SONIC_BOOM,
                SoundSource.PLAYERS, 1.0F, 1.0F);

        // 2. 巨大的音波粒子爆发
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SONIC_BOOM,
                    x, y, z, 3, 0, 0, 0, 0);
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI * 2 / 8;
                double dx = Math.cos(angle) * 3.0;
                double dz = Math.sin(angle) * 3.0;
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.SONIC_BOOM,
                        x + dx, y, z + dz, 1, 0, 0, 0, 0);
            }
        }

        // 3. 范围伤害 + 沉默
        AABB area = new AABB(
                x - AREA_RADIUS, y - AREA_RADIUS, z - AREA_RADIUS,
                x + AREA_RADIUS, y + AREA_RADIUS, z + AREA_RADIUS
        );

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity target : targets) {
            if (target == this.getOwner()) continue;

            float damage = (target == directHit)
                    ? DIRECT_DAMAGE * this.power
                    : SPLASH_DAMAGE * this.power;

            target.invulnerableTime = 0;
            target.hurt(this.damageSources().generic(), damage);

            // 施加沉默
            DenseDeepSculkArrowHandler.applySilence(target);
        }

        // 4. 消失
        this.discard();
    }
}