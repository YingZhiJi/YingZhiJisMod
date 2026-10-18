package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class FlyingZombieHeadEntity extends Entity {

    private static final int MAX_LIFETIME = 240;

    private int lifetime = 0;

    public FlyingZombieHeadEntity(EntityType<? extends FlyingZombieHeadEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(false);
        this.setGlowingTag(true);
    }

    public static void launch(Level level, double x, double y, double z) {
        FlyingZombieHeadEntity e = new FlyingZombieHeadEntity(
                ExampleMod.FLYING_ZOMBIE_HEAD_ENTITY.get(), level);
        e.setPos(x, y, z);

        double angle = Math.random() * Math.PI * 2;
        double horizontal = 0.15 + Math.random() * 0.2;
        e.setDeltaMovement(
                Math.cos(angle) * horizontal,
                1.4 + Math.random() * 0.4,
                Math.sin(angle) * horizontal
        );
        level.addFreshEntity(e);
    }

    @Override
    public void tick() {
        super.tick();
        this.lifetime++;

        if (this.level().isClientSide()) {
            // 粒子拖尾
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    this.getX(), this.getY(), this.getZ(),
                    0, 0, 0);
            if (this.tickCount % 2 == 0) {
                this.level().addParticle(ParticleTypes.SMOKE,
                        this.getX(), this.getY(), this.getZ(),
                        0, 0.01, 0);
            }
            return;
        }

        // 重力
        Vec3 vel = this.getDeltaMovement();
        this.setDeltaMovement(vel.x, vel.y - 0.06, vel.z);

        // 应用移动
        this.move(MoverType.SELF, this.getDeltaMovement());

        // 空气阻力
        Vec3 cur = this.getDeltaMovement();
        this.setDeltaMovement(cur.x * 0.99, cur.y, cur.z * 0.99);

        // 落地/碰撞检测
        boolean landed = this.onGround()
                || (this.verticalCollision && this.getDeltaMovement().y < 0)
                || this.horizontalCollision;

        if (landed || this.lifetime > MAX_LIFETIME) {
            placeHead();
        }
    }

    private void placeHead() {
        if (!(this.level() instanceof ServerLevel sl)) {
            this.discard();
            return;
        }

        BlockPos pos = this.blockPosition();
        // 落到方块上方
        while (pos.getY() > this.level().getMinBuildHeight()
                && this.level().getBlockState(pos).isAir()) {
            pos = pos.below();
        }
        pos = pos.above();

        if (this.level().getBlockState(pos).isAir()) {
            this.level().setBlockAndUpdate(pos, Blocks.ZOMBIE_HEAD.defaultBlockState());
        }

        sl.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ZOMBIE_AMBIENT, SoundSource.HOSTILE, 0.8F, 1.3F);

        this.discard();
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.lifetime = tag.getInt("Lifetime");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Lifetime", this.lifetime);
    }
}