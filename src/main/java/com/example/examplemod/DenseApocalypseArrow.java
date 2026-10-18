package com.example.examplemod;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;
import java.util.UUID;

public class DenseApocalypseArrow extends Arrow {

    public float power = 1.0F;

    private static final float DIRECT_TRUE_DAMAGE = 50.0F;
    private static final float EXPLOSION_MAX_DAMAGE = 80.0F;
    private static final double EXPLOSION_RADIUS = 8.0;

    public DenseApocalypseArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(6.0);
    }

    public DenseApocalypseArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.DENSE_APOCALYPSE_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
        this.setBaseDamage(6.0);
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide()) return;

        Entity hit = result.getEntity();

        // 直击真伤
        if (hit instanceof LivingEntity living && living.isAlive()) {
            living.hurt(this.damageSources().fellOutOfWorld(), DIRECT_TRUE_DAMAGE * this.power);
        }

        // 爆炸 + 粒子云
        triggerApocalypse(hit.getX(), hit.getY(), hit.getZ());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide()) {
            triggerApocalypse(this.getX(), this.getY(), this.getZ());
        }
    }

    private void triggerApocalypse(double x, double y, double z) {
        Level level = this.level();
        UUID ownerUUID = this.getOwner() != null ? this.getOwner().getUUID() : null;

        // 1. 粒子云
        ApocalypseCloudEntity.spawn(level, x, y, z, this.getOwner() instanceof LivingEntity le ? le : null);

        // 2. 平躺动画
        ApocalypseAnimationEntity.spawn(level, x, y, z);

        // 3. 爆炸粒子 + 音效
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,
                    x, y, z, 1, 0, 0, 0, 0);
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                    x, y, z, 12, 3.0, 3.0, 3.0, 0);
            serverLevel.playSound(null, x, y, z,
                    ModSounds.APOCALYPSE_HIT.get(),
                    net.minecraft.sounds.SoundSource.PLAYERS, 4.0F, 1.0F);
        }

        // 4. 爆炸伤害 + 图腾破解窗口
        AABB area = new AABB(
                x - EXPLOSION_RADIUS, y - EXPLOSION_RADIUS, z - EXPLOSION_RADIUS,
                x + EXPLOSION_RADIUS, y + EXPLOSION_RADIUS, z + EXPLOSION_RADIUS
        );
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity target : targets) {
            if (ownerUUID != null && target.getUUID().equals(ownerUUID)) continue;
            double dist = Math.sqrt(target.distanceToSqr(x, y, z));
            if (dist > EXPLOSION_RADIUS) continue;

            float falloff = (float) (1.0 - dist / EXPLOSION_RADIUS);
            float damage = EXPLOSION_MAX_DAMAGE * this.power * falloff;

            // 图腾破解窗口
            target.getPersistentData().putInt("yzj_apocalypse_window", 10);

            target.invulnerableTime = 0;
            target.hurt(this.damageSources().generic(), damage);
        }

        // 5. 消失
        this.discard();
    }
}