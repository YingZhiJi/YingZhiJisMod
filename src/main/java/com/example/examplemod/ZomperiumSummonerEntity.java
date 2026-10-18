package com.example.examplemod;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class ZomperiumSummonerEntity extends Entity {

    private static final int DELAY = 200; // 10 秒
    private int age;
    private boolean burstDone = false;

    public ZomperiumSummonerEntity(EntityType<? extends ZomperiumSummonerEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static void spawn(Level level, double x, double y, double z) {
        ZomperiumSummonerEntity e = new ZomperiumSummonerEntity(ExampleMod.ZOMPERIUM_SUMMONER_ENTITY.get(), level);
        e.setPos(x, y, z);
        e.setYRot(0);
        level.addFreshEntity(e);
    }

    @Override
    public void tick() {
        super.tick();
        this.age++;

        // ===== 客户端：地面烟雾，红→白→黑渐变，向上飘散 =====
        if (this.level().isClientSide()) {
            clientParticles();
            return;
        }

        // ===== 服务端：倒计时结束 → 爆炸 + 冲击波 + 指挥官 =====
        if (!this.burstDone && this.age >= DELAY) {
            this.burstDone = true;
            doFinalBurst();

            ZomperiumCommander commander =
                    ExampleMod.ZOMPERIUM_COMMANDER_ENTITY.get().create(this.level());
            if (commander != null) {
                commander.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0);
                commander.setPersistenceRequired();
                this.level().addFreshEntity(commander);
            }
            this.discard();
        }
    }

    private void clientParticles() {
        float t = (float) Math.min(this.age, DELAY) / DELAY;

        // 红(0) → 白(0.5) → 黑(1)
        float r, g, b;
        if (t < 0.5f) {
            float u = t * 2f;
            r = 1f;
            g = u;
            b = u;
        } else {
            float u = (t - 0.5f) * 2f;
            r = 1f - u;
            g = 1f - u;
            b = 1f - u;
        }

        // 随时间递增浓度与半径
        int count = 4 + (int) (t * 8);
        double radius = 1.2 + t * 2.0;

        for (int i = 0; i < count; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2;
            double rr = Math.sqrt(this.random.nextDouble()) * radius;
            double dx = Math.cos(angle) * rr;
            double dz = Math.sin(angle) * rr;

            // 轻微颜色抖动
            float jr = clamp(r + (this.random.nextFloat() - 0.5f) * 0.15f);
            float jg = clamp(g + (this.random.nextFloat() - 0.5f) * 0.15f);
            float jb = clamp(b + (this.random.nextFloat() - 0.5f) * 0.15f);

            DustParticleOptions dust = new DustParticleOptions(new Vector3f(jr, jg, jb), 2.0f);

            double vx = (this.random.nextDouble() - 0.5) * 0.02;
            double vy = 0.05 + this.random.nextDouble() * 0.06;
            double vz = (this.random.nextDouble() - 0.5) * 0.02;

            this.level().addParticle(dust,
                    this.getX() + dx, this.getY() + 0.1, this.getZ() + dz,
                    vx, vy, vz);
        }

        // 少量浓烟增加体积感
        if (this.random.nextFloat() < 0.6f) {
            double dx = (this.random.nextDouble() - 0.5) * 2.0;
            double dz = (this.random.nextDouble() - 0.5) * 2.0;
            this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    this.getX() + dx, this.getY() + 0.2, this.getZ() + dz,
                    0, 0.05, 0);
        }
    }

    private static float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private void doFinalBurst() {
        if (!(this.level() instanceof ServerLevel sl)) return;

        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        // 1. 爆炸音效
        sl.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE,
                SoundSource.HOSTILE, 4.0f, 0.7f);
        sl.playSound(null, x, y, z, SoundEvents.DRAGON_FIREBALL_EXPLODE,
                SoundSource.HOSTILE, 3.0f, 1.0f);
        sl.playSound(null, x, y, z, SoundEvents.ENDER_DRAGON_GROWL,
                SoundSource.HOSTILE, 2.0f, 1.5f);

        // 2. 核心爆炸
        sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                x, y + 1.0, z, 1, 0, 0, 0, 0);
        sl.sendParticles(ParticleTypes.EXPLOSION,
                x, y + 1.0, z, 30, 3.0, 0.5, 3.0, 0.15);
        sl.sendParticles(ParticleTypes.FLASH,
                x, y + 1.0, z, 4, 0.5, 0.5, 0.5, 0);

        // 3. 白色扩散冲击波环
        for (int i = 0; i < 72; i++) {
            double angle = i * Math.PI * 2 / 72;
            for (int rr = 1; rr <= 6; rr++) {
                double dx = Math.cos(angle) * rr;
                double dz = Math.sin(angle) * rr;
                float fade = 1.0f - (rr / 6.0f) * 0.7f;
                DustParticleOptions dust = new DustParticleOptions(
                        new Vector3f(fade, fade, fade), 1.5f);
                sl.sendParticles(dust,
                        x + dx, y + 0.4, z + dz,
                        1, 0, 0.2, 0, 0.2);
            }
        }

        // 4. 灵魂火焰 + 末地烛上升
        sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                x, y + 0.3, z, 40, 2.0, 0.2, 2.0, 0.15);
        sl.sendParticles(ParticleTypes.END_ROD,
                x, y + 0.5, z, 25, 2.5, 0.5, 2.5, 0.2);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.age = tag.getInt("Age");
        this.burstDone = tag.getBoolean("BurstDone");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", this.age);
        tag.putBoolean("BurstDone", this.burstDone);
    }
}