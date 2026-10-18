package com.example.examplemod;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class DenseDecayingArrow extends Arrow {

    // 落地后继续产生粒子的时长（tick）
    private static final int LANDED_PARTICLE_DURATION = 100; // 5 秒

    // 落地时刻，-1 表示还没落地
    private int landedTick = -1;

    public DenseDecayingArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(1.0);
    }

    public DenseDecayingArrow(Level level, LivingEntity shooter) {
        super(ExampleMod.DENSE_DECAYING_ARROW_ENTITY.get(), level);
        this.setOwner(shooter);
        this.setBaseDamage(1.0);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ExampleMod.DENSE_DECAYING_ARROW.get());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide() && !this.isRemoved()) {
            // 记录落地时刻（inGround 由 AbstractArrow 维护）
            if (this.inGround && this.landedTick < 0) {
                this.landedTick = this.tickCount;
            }

            // 落地超过 5 秒 → 不再产生粒子
            if (this.landedTick >= 0
                    && this.tickCount - this.landedTick >= LANDED_PARTICLE_DURATION) {
                return;
            }

            // 每 tick 一小堆灵魂粒子
            for (int i = 0; i < 3; i++) {
                double dx = (this.random.nextDouble() - 0.5) * 0.6;
                double dy = (this.random.nextDouble() - 0.5) * 0.6;
                double dz = (this.random.nextDouble() - 0.5) * 0.6;
                this.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.SOUL,
                        this.getX() + dx, this.getY() + dy, this.getZ() + dz,
                        0, 0.01, 0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide()) return;
        if (result.getEntity() instanceof LivingEntity living) {
            // 凋零 IV（放大 3），20 秒 = 400 tick
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 3), this.getOwner());

            // 10 秒内移除无敌帧
            living.getPersistentData().putInt("yzj_decaying", 200);

            // 10 秒传播窗口
            living.getPersistentData().putInt("yzj_decaying_spread", 200);

            // 记录发射者 UUID，用于敌我识别
            if (this.getOwner() != null) {
                living.getPersistentData().putUUID("yzj_decaying_owner", this.getOwner().getUUID());
            }

            // 生成灵魂汇聚特效
            DecayingSoulEntity.spawn(this.level(),
                    living.getX(),
                    living.getY() + living.getBbHeight() / 2.0,
                    living.getZ());
        }
    }
}