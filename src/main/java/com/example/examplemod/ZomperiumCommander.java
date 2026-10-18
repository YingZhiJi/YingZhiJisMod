package com.example.examplemod;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ZomperiumCommander extends Monster {

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.yingzhiji_s_mod.zomperium_commander"),
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private static final Random RNG = new Random();
    private static final int MUSIC_INTERVAL = 4500;

    // ===== 召唤参数 =====
    private static final int SUMMON_INTERVAL = 1200;
    private static final int SUMMON_COUNT = 4;
    private static final float PHASE_100_THRESHOLD = 100F;

    // ===== 游走/冲刺参数 =====
    private static final double ORBIT_RADIUS_BASE  = 5.0;
    private static final double ORBIT_RADIUS_ELITE = 12.0;
    private static final double TOO_CLOSE          = 3.0;
    private static final int    WINDUP_TICKS       = 8;
    private static final int    DASH_TICKS         = 10;
    private static final int    BACKSTEP_TICKS     = 8;
    private static final double DASH_SPEED         = 1.2;
    private static final double ORBIT_ANGULAR_SPEED = 0.035;
    private static final int    ORBIT_UPDATE_INTERVAL = 5;
    private static final int    ELITE_CHECK_INTERVAL = 20;
    private static final int    DASH_COOLDOWN_MIN  = 80;
    private static final int    DASH_COOLDOWN_VARIANCE = 40;
    private static final double DASH_MAX_RANGE     = 20.0;

    // ===== 面朝参数 =====
    private static final double FACE_RANGE = 10.0;

    // ===== 限伤 =====
    private static final float PHYSICAL_DAMAGE_CAP = 20.0F;
    private static final float NON_PLAYER_DAMAGE_CAP = 15.0F;

    // ===== 撤离参数 =====
    private static final int ESCAPE_COOLDOWN = 200;
    private static final double ESCAPE_BURST_SPEED = 3.5;

    // ===== 死亡动画参数 =====
    private static final int DEATH_SMOKE_DURATION = 40;
    private static final int DEATH_FUSE_DURATION = 30;
    private static final int DEATH_EXPLOSION_INTERVAL = 4;
    private static final int DEATH_EXPLOSION_COUNT = 5;
    private static final float DEATH_EXPLOSION_DAMAGE = 20.0F;
    private static final double DEATH_EXPLOSION_RADIUS = 6.0;

    // ===== 拖尾参数 =====
    private static final int    TRAIL_INTERVAL = 1;
    private static final double MOVE_THRESHOLD = 0.01;
    private static final double DASH_DETECT    = 0.9;
    private static final double BACK_OFFSET = 0.5;
    private static final float  SHOULDER_RATIO = 0.85F;

    private static final Vector3f TRAIL_DUST_COLOR = new Vector3f(0.55F, 0.05F, 0.05F);
    private static final Vector3f DASH_TRAIL_COLOR = new Vector3f(1.0F, 0.0F, 0.0F);

    private enum AIState { ORBIT, WINDUP, DASH, BACKSTEP }
    private AIState aiState = AIState.ORBIT;
    private int aiStateTimer = 0;
    private int dashCooldown = 60;
    private int orbitUpdateTimer = 0;
    private double orbitAngle = 0.0;
    private Vec3 dashVector = Vec3.ZERO;
    private boolean dashHit = false;
    private boolean eliteMinionAlive = false;
    private int eliteCheckTimer = 0;

    private int pullCooldown = 0;
    private int escapeCooldown = 0;
    private int healCooldown = 0;
    private int harmBurstRemaining = 0;
    private int harmBurstCooldown = 0;

    private FishingHook activeHook = null;
    private LivingEntity hookTarget = null;
    private int hookPullTimer = 0;

    private float recentDamage = 0.0F;
    private int recentDamageTimer = 0;

    private boolean hasUsedTotem = false;
    private boolean isDesperate = false;
    private int actionCooldown = 0;

    private int musicTick = 0;

    private int summonCooldown = SUMMON_INTERVAL;
    private boolean phase100Triggered = false;
    private boolean totemTriggered = false;

    private boolean deathAnimating = false;
    private int deathAnimTick = 0;
    private int deathExplosionTick = 0;
    private int deathExplosionsDone = 0;
    private boolean deathLootDropped = false;
    private ServerPlayer deathKiller = null;

    public ZomperiumCommander(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 200;
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ExampleMod.OLD_SABER.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);

        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_OTHER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, 0.0F);

        this.setPersistenceRequired();
    }

    @Override
    public void checkDespawn() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.ATTACK_DAMAGE, 18.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target.getType().is(ModTags.Entities.ZOMPERIUM)) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    public void setLastHurtByMob(LivingEntity entity) {
        if (entity != null && entity.getType().is(ModTags.Entities.ZOMPERIUM)) {
            return;
        }
        super.setLastHurtByMob(entity);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Raider.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            clientTrail();
        }
    }

    private void clientTrail() {
        if (this.tickCount % TRAIL_INTERVAL != 0) return;

        Vec3 vel = this.getDeltaMovement();
        double hs = vel.x * vel.x + vel.z * vel.z;
        if (hs < MOVE_THRESHOLD) return;

        Vec3 back = getBackPoint();

        if (hs > DASH_DETECT) {
            DustParticleOptions dust = new DustParticleOptions(DASH_TRAIL_COLOR, 2.0F);
            for (int i = 0; i < 4; i++) {
                this.level().addParticle(dust,
                        back.x, back.y, back.z,
                        0, 0, 0);
            }
        } else {
            DustParticleOptions dust = new DustParticleOptions(TRAIL_DUST_COLOR, 1.6F);
            this.level().addParticle(dust,
                    back.x, back.y, back.z,
                    0, 0.01, 0);

            if (this.tickCount % 4 == 0) {
                this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        back.x, back.y, back.z,
                        0, 0.01, 0);
            }
        }
    }

    private Vec3 getBackPoint() {
        Vec3 vel = this.getDeltaMovement();
        double len = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        double lx, lz;
        if (len > 0.001) {
            lx = vel.x / len;
            lz = vel.z / len;
        } else {
            Vec3 look = this.getLookAngle();
            double l2 = Math.sqrt(look.x * look.x + look.z * look.z);
            lx = l2 > 0.001 ? look.x / l2 : 0;
            lz = l2 > 0.001 ? look.z / l2 : 0;
        }
        double dx = -lx * BACK_OFFSET;
        double dz = -lz * BACK_OFFSET;
        double y = this.getY() + this.getBbHeight() * SHOULDER_RATIO;
        return new Vec3(this.getX() + dx, y, this.getZ() + dz);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) return;

        if (this.deathAnimating) {
            tickDeathAnimation();
            return;
        }

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (this.actionCooldown > 0) this.actionCooldown--;
        if (this.pullCooldown > 0) this.pullCooldown--;
        if (this.escapeCooldown > 0) this.escapeCooldown--;
        if (this.healCooldown > 0) this.healCooldown--;
        if (this.harmBurstCooldown > 0) this.harmBurstCooldown--;
        if (this.summonCooldown > 0) this.summonCooldown--;
        if (this.dashCooldown > 0) this.dashCooldown--;

        if (this.recentDamageTimer > 0) {
            this.recentDamageTimer--;
            if (this.recentDamageTimer <= 0) this.recentDamage = 0;
        }

        breakCobwebs();

        handleSummon();

        if (this.tickCount % 10 == 0) {
            scanForTargets64();
        }

        if (this.activeHook != null) {
            if (this.hookPullTimer > 0) {
                this.hookPullTimer--;
            } else {
                if (this.hookTarget != null && this.hookTarget.isAlive()) {
                    double pullDx = this.getX() - this.hookTarget.getX();
                    double pullDz = this.getZ() - this.hookTarget.getZ();
                    double len = Math.sqrt(pullDx * pullDx + pullDz * pullDz);
                    if (len > 0.1) {
                        this.hookTarget.push(pullDx / len * 2.0, 0.3, pullDz / len * 2.0);
                        this.hookTarget.hurtMarked = true;
                    }
                    this.level().playSound(null, this.hookTarget.getX(), this.hookTarget.getY(), this.hookTarget.getZ(),
                            SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.HOSTILE, 1.0F, 1.0F);
                }
                if (this.activeHook.isAlive()) {
                    this.activeHook.discard();
                }
                this.activeHook = null;
                this.hookTarget = null;
                this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
        }

        checkDesperatePhase();

        if (this.isDesperate && !this.hasUsedTotem) {
            if (this.isUsingItem()) this.stopUsingItem();
            if (!this.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) {
                this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING));
            }
            return;
        }

        if (this.isUsingItem()) {
            if (this.aiState == AIState.DASH || this.aiState == AIState.WINDUP) {
                this.aiState = AIState.ORBIT;
            }
            return;
        }

        if (this.getTarget() != null) {
            updateCombatAI();
        } else {
            if (this.aiState != AIState.ORBIT) {
                this.aiState = AIState.ORBIT;
            }
        }

        if (this.aiState == AIState.WINDUP || this.aiState == AIState.DASH) {
            return;
        }

        if (this.pullCooldown <= 0 && this.tickCount % 10 == 0) {
            handlePullApproach();
        }

        handleHarmBurst();

        if (this.recentDamage >= 60.0F && this.escapeCooldown <= 0) {
            burstEscape();
            this.escapeCooldown = ESCAPE_COOLDOWN;
            this.harmBurstRemaining = 3;
            this.harmBurstCooldown = 0;
            this.recentDamage = 0;
            this.recentDamageTimer = 0;
            return;
        }

        if (this.tickCount % 10 == 0 && this.actionCooldown <= 0) {
            handleOffhandLogic();
        }

        destroyVehicles();
    }

    // ================== 死亡动画 ==================

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide() && !this.deathAnimating) {
            this.deathAnimating = true;
            if (source.getEntity() instanceof ServerPlayer sp) {
                this.deathKiller = sp;
            } else if (source.getDirectEntity() instanceof ServerPlayer sp2) {
                this.deathKiller = sp2;
            }
            this.deathAnimTick = 0;
            this.deathExplosionTick = 0;
            this.deathExplosionsDone = 0;
            this.deathLootDropped = false;

            this.setHealth(0.01F);
            this.setNoGravity(true);
            this.setDeltaMovement(0, 0, 0);
            this.getNavigation().stop();
            this.setTarget(null);
            this.bossEvent.setProgress(0.0F);

            if (this.isUsingItem()) this.stopUsingItem();
            this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            return;
        }
        super.die(source);
    }

    @Override
    public boolean isDeadOrDying() {
        if (this.deathAnimating) return false;
        return super.isDeadOrDying();
    }

    @Override
    public boolean isAlive() {
        if (this.deathAnimating) return true;
        return super.isAlive();
    }

    private void tickDeathAnimation() {
        if (!(this.level() instanceof ServerLevel sl)) return;

        this.deathAnimTick++;
        this.setDeltaMovement(0, 0, 0);
        this.getNavigation().stop();
        this.setNoGravity(true);

        double x = this.getX();
        double y = this.getY() + this.getBbHeight() * 0.6;
        double z = this.getZ();

        if (this.deathAnimTick <= DEATH_SMOKE_DURATION) {
            sl.sendParticles(ParticleTypes.LARGE_SMOKE,
                    x, y, z, 6,
                    0.5, 0.6, 0.5, 0.02);
            sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    x, y + 0.3, z, 3,
                    0.4, 0.4, 0.4, 0.01);
            if (this.deathAnimTick == 1) {
                sl.playSound(null, x, y, z, SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.HOSTILE, 1.0F, 0.8F);
            }
            return;
        }

        int fuseStart = DEATH_SMOKE_DURATION + 1;
        int fuseEnd = DEATH_SMOKE_DURATION + DEATH_FUSE_DURATION;

        if (this.deathAnimTick == fuseStart) {
            sl.playSound(null, x, y, z, SoundEvents.TNT_PRIMED,
                    SoundSource.HOSTILE, 2.0F, 1.0F);
        }

        if (this.deathAnimTick <= fuseEnd) {
            sl.sendParticles(ParticleTypes.LARGE_SMOKE,
                    x, y, z, 8,
                    0.6, 0.8, 0.6, 0.03);
            sl.sendParticles(ParticleTypes.FLAME,
                    x, y, z, 3,
                    0.4, 0.6, 0.4, 0.02);
            return;
        }

        if (this.deathExplosionsDone < DEATH_EXPLOSION_COUNT) {
            this.deathExplosionTick--;
            if (this.deathExplosionTick <= 0) {
                this.deathExplosionTick = DEATH_EXPLOSION_INTERVAL;
                doDeathExplosion(sl);
                this.deathExplosionsDone++;
            }
            return;
        }

        if (!this.deathLootDropped) {
            this.deathLootDropped = true;

            if (this.deathKiller != null && this.deathKiller.isAlive()) {
                LibrarianEventHandler.processKill(this.deathKiller, this);
            }

            if (this.level() instanceof ServerLevel sl2) {
                AABB area = this.getBoundingBox().inflate(64.0);
                for (ServerPlayer sp : sl2.getEntitiesOfClass(ServerPlayer.class, area)) {
                    HealthBonusHelper.grantAdvancement(sp, "self_destruction");
                }
            }

            dropCustomDeathLoot(
                    this.damageSources().generic(),
                    0, true);
            this.bossEvent.removeAllPlayers();
            this.discard();
        }
    }

    private void doDeathExplosion(ServerLevel sl) {
        double x = this.getX();
        double y = this.getY() + this.getBbHeight() * 0.5;
        double z = this.getZ();

        sl.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE,
                SoundSource.HOSTILE, 4.0F, 0.7F + RNG.nextFloat() * 0.3F);

        sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                x, y, z, 1, 0, 0, 0, 0);

        int smokeCount = 60;
        for (int i = 0; i < smokeCount; i++) {
            double angle = RNG.nextDouble() * Math.PI * 2;
            double pitch = (RNG.nextDouble() - 0.5) * Math.PI;
            double speed = 0.3 + RNG.nextDouble() * 0.5;
            double vx = Math.cos(angle) * Math.cos(pitch) * speed;
            double vy = Math.sin(pitch) * speed * 0.7 + 0.15;
            double vz = Math.sin(angle) * Math.cos(pitch) * speed;

            sl.sendParticles(ParticleTypes.LARGE_SMOKE,
                    x, y, z, 1,
                    vx, vy, vz,
                    1.0);
        }

        DustParticleOptions redDust = new DustParticleOptions(
                new Vector3f(1.0F, 0.1F, 0.1F), 2.0F);
        for (int i = 0; i < 25; i++) {
            double angle = RNG.nextDouble() * Math.PI * 2;
            double pitch = (RNG.nextDouble() - 0.5) * Math.PI;
            double speed = 0.4 + RNG.nextDouble() * 0.5;
            double vx = Math.cos(angle) * Math.cos(pitch) * speed;
            double vy = Math.sin(pitch) * speed * 0.7 + 0.2;
            double vz = Math.sin(angle) * Math.cos(pitch) * speed;

            sl.sendParticles(redDust,
                    x, y, z, 1,
                    vx, vy, vz,
                    1.0);
        }

        sl.sendParticles(ParticleTypes.FLAME,
                x, y, z, 15,
                1.0, 1.0, 1.0, 0.3);

        AABB area = new AABB(
                x - DEATH_EXPLOSION_RADIUS, y - DEATH_EXPLOSION_RADIUS, z - DEATH_EXPLOSION_RADIUS,
                x + DEATH_EXPLOSION_RADIUS, y + DEATH_EXPLOSION_RADIUS, z + DEATH_EXPLOSION_RADIUS
        );
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity target : targets) {
            if (target == this) continue;
            if (target.getType().is(ModTags.Entities.ZOMPERIUM)) continue;
            double dist = Math.sqrt(target.distanceToSqr(x, y, z));
            if (dist > DEATH_EXPLOSION_RADIUS) continue;

            float falloff = (float) (1.0 - dist / DEATH_EXPLOSION_RADIUS);
            float damage = DEATH_EXPLOSION_DAMAGE * falloff;

            target.invulnerableTime = 0;
            target.hurt(this.damageSources().generic(), damage);
        }

        if (this.deathExplosionsDone == 1) {
            FlyingZombieHeadEntity.launch(this.level(), x, y, z);
        }
    }

    // ================== 战斗 AI ==================

    private void updateCombatAI() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            if (this.aiState != AIState.ORBIT) {
                this.aiState = AIState.ORBIT;
            }
            return;
        }

        if (this.eliteCheckTimer-- <= 0) {
            this.eliteCheckTimer = ELITE_CHECK_INTERVAL;
            this.eliteMinionAlive = checkEliteAlive();
        }

        double dist = this.distanceTo(target);

        if (dist <= FACE_RANGE) {
            faceTowards(target);
        }

        switch (this.aiState) {
            case ORBIT -> tickOrbit(target, dist);
            case WINDUP -> tickWindup(target);
            case DASH -> tickDash(target);
            case BACKSTEP -> tickBackstep(target);
        }
    }

    private void burstEscape() {
        LivingEntity target = this.getTarget();

        double dirX, dirZ;
        if (target != null) {
            double dx = this.getX() - target.getX();
            double dz = this.getZ() - target.getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len > 0.1) {
                dirX = dx / len;
                dirZ = dz / len;
            } else {
                double a = RNG.nextDouble() * Math.PI * 2;
                dirX = Math.cos(a);
                dirZ = Math.sin(a);
            }
        } else {
            double a = RNG.nextDouble() * Math.PI * 2;
            dirX = Math.cos(a);
            dirZ = Math.sin(a);
        }

        this.setDeltaMovement(dirX * ESCAPE_BURST_SPEED, 0.4, dirZ * ESCAPE_BURST_SPEED);
        this.hasImpulse = true;
        this.hurtMarked = true;

        this.setTarget(null);
        this.aiState = AIState.ORBIT;

        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.CLOUD,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    15, 0.4, 0.4, 0.4, 0.2);
            sl.sendParticles(ParticleTypes.END_ROD,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    10, 0.5, 0.5, 0.5, 0.3);
            sl.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENDER_DRAGON_FLAP,
                    SoundSource.HOSTILE, 1.0F, 1.2F);
        }
    }

    private void faceTowards(LivingEntity target) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double dy = target.getEyeY() - this.getEyeY();
        double horiz = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
        float targetPitch = (float) -(Math.atan2(dy, horiz) * 180.0 / Math.PI);

        this.setYHeadRot(targetYaw);
        this.yHeadRotO = targetYaw;
        this.setYBodyRot(targetYaw);
        this.yBodyRotO = targetYaw;
        this.setXRot(targetPitch);
        this.xRotO = targetPitch;
    }

    private void tickOrbit(LivingEntity target, double dist) {
        if (dist < TOO_CLOSE) {
            this.aiState = AIState.BACKSTEP;
            this.aiStateTimer = BACKSTEP_TICKS;
            return;
        }

        if (this.dashCooldown <= 0 && dist < DASH_MAX_RANGE && dist > 2.0) {
            this.aiState = AIState.WINDUP;
            this.aiStateTimer = WINDUP_TICKS;
            this.getNavigation().stop();
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 0.8F, 1.5F);
            return;
        }

        this.orbitAngle += ORBIT_ANGULAR_SPEED;
        if (this.orbitAngle > Math.PI * 2) this.orbitAngle -= Math.PI * 2;

        double desiredRadius = this.eliteMinionAlive ? ORBIT_RADIUS_ELITE : ORBIT_RADIUS_BASE;

        if (this.orbitUpdateTimer-- <= 0) {
            this.orbitUpdateTimer = ORBIT_UPDATE_INTERVAL;

            double tx = target.getX() + Math.cos(this.orbitAngle) * desiredRadius;
            double tz = target.getZ() + Math.sin(this.orbitAngle) * desiredRadius;
            this.getNavigation().moveTo(tx, target.getY(), tz, 1.0);
        }
    }

    private void tickWindup(LivingEntity target) {
        this.getNavigation().stop();
        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
        this.getLookControl().setLookAt(target, 60.0F, 60.0F);

        if (this.level() instanceof ServerLevel sl && this.aiStateTimer % 2 == 0) {
            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    4, 0.5, 0.7, 0.5, 0.02);
        }

        this.aiStateTimer--;
        if (this.aiStateTimer <= 0) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len < 0.05) {
                this.aiState = AIState.ORBIT;
                return;
            }
            this.dashVector = new Vec3(dx / len, 0, dz / len);
            this.dashHit = false;
            this.aiState = AIState.DASH;
            this.aiStateTimer = DASH_TICKS;
            this.setDeltaMovement(this.dashVector.x * DASH_SPEED, this.getDeltaMovement().y, this.dashVector.z * DASH_SPEED);
            this.hasImpulse = true;
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.RAVAGER_ATTACK, SoundSource.HOSTILE, 1.0F, 1.2F);

            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.CRIT,
                        this.getX(), this.getY() + 1.0, this.getZ(),
                        20, 0.4, 0.4, 0.4, 0.3);
            }
        }
    }

    private void tickDash(LivingEntity target) {
        this.setDeltaMovement(this.dashVector.x * DASH_SPEED, this.getDeltaMovement().y, this.dashVector.z * DASH_SPEED);
        this.hasImpulse = true;

        if (!this.dashHit) {
            AABB hitBox = this.getBoundingBox().inflate(0.5);
            if (hitBox.intersects(target.getBoundingBox()) && target.isAlive()) {
                float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                target.hurt(this.damageSources().mobAttack(this), damage);
                this.dashHit = true;
                this.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.HOSTILE, 1.0F, 1.0F);
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.CRIT,
                            target.getX(), target.getY() + 1.0, target.getZ(),
                            15, 0.3, 0.3, 0.3, 0.2);
                }
            }
        }

        this.aiStateTimer--;

        boolean stuck = this.horizontalCollision && !this.dashHit;

        if (this.aiStateTimer <= 0 || this.dashHit || stuck) {
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            this.aiState = AIState.BACKSTEP;
            this.aiStateTimer = BACKSTEP_TICKS;
            this.dashCooldown = DASH_COOLDOWN_MIN + RNG.nextInt(DASH_COOLDOWN_VARIANCE);
        }
    }

    private void tickBackstep(LivingEntity target) {
        double dx = this.getX() - target.getX();
        double dz = this.getZ() - target.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.05) {
            this.aiState = AIState.ORBIT;
            return;
        }
        this.setDeltaMovement(dx / len * 0.6, this.getDeltaMovement().y, dz / len * 0.6);
        this.hasImpulse = true;

        this.aiStateTimer--;
        if (this.aiStateTimer <= 0) {
            this.aiState = AIState.ORBIT;
            this.orbitUpdateTimer = 0;
        }
    }

    private boolean checkEliteAlive() {
        List<ZomperiumPrivate> minions = this.level().getEntitiesOfClass(
                ZomperiumPrivate.class, this.getBoundingBox().inflate(48.0));
        for (ZomperiumPrivate m : minions) {
            if (m.isAlive() && m.getPersistentData().getBoolean("yzj_commander_elite")) {
                return true;
            }
        }
        return false;
    }

    // ================== 召唤 ==================

    private void handleSummon() {
        if (this.summonCooldown <= 0) {
            ZomperiumPrivate.SummonVariant variant = this.hasUsedTotem
                    ? ZomperiumPrivate.SummonVariant.TIMER_AFTER_TOTEM
                    : ZomperiumPrivate.SummonVariant.TIMER_BASE;
            summonMinions(SUMMON_COUNT, variant);
            this.summonCooldown = SUMMON_INTERVAL;
        }

        if (!this.phase100Triggered && this.getHealth() < PHASE_100_THRESHOLD) {
            this.phase100Triggered = true;
            summonMinions(SUMMON_COUNT, ZomperiumPrivate.SummonVariant.PHASE_100);
        }
    }

    private void summonMinions(int count, ZomperiumPrivate.SummonVariant variant) {
        if (!(this.level() instanceof ServerLevel sl)) return;

        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2 * i / count + RNG.nextDouble() * 0.6;
            double radius = 3.0 + RNG.nextDouble() * 1.5;
            double sx = this.getX() + Math.cos(angle) * radius;
            double sz = this.getZ() + Math.sin(angle) * radius;

            BlockPos spawn = findSummonPos(sl, sx, this.getY(), sz);
            if (spawn == null) continue;

            ZomperiumPrivate minion = ExampleMod.ZOMPERIUM_PRIVATE_ENTITY.get().create(sl);
            if (minion == null) continue;

            minion.moveTo(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5,
                    this.getYRot() + 180.0F, 0.0F);
            minion.applySummonVariant(variant);
            minion.setPersistenceRequired();

            if (variant == ZomperiumPrivate.SummonVariant.TOTEM_ELITE) {
                minion.getPersistentData().putBoolean("yzj_commander_elite", true);
            }

            if (this.getTarget() != null) {
                minion.setTarget(this.getTarget());
            }

            sl.addFreshEntity(minion);
            playSummonEffect(sl, minion, variant.tier);
        }
    }

    private BlockPos findSummonPos(ServerLevel level, double x, double y, double z) {
        BlockPos base = BlockPos.containing(x, y, z);
        for (int dy = 0; dy <= 3; dy++) {
            BlockPos check = base.above(dy);
            if (level.getBlockState(check).isAir()
                    && level.getBlockState(check.above()).isAir()
                    && !level.getBlockState(check.below()).isAir()) {
                return check;
            }
        }
        return null;
    }

    private void playSummonEffect(ServerLevel level, ZomperiumPrivate minion, int tier) {
        double x = minion.getX();
        double y = minion.getY() + 1.0;
        double z = minion.getZ();

        if (tier <= 0) {
            level.sendParticles(ParticleTypes.CLOUD, x, y, z, 4, 0.2, 0.2, 0.2, 0.03);
            minion.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20, 0, false, false));
            level.playSound(null, x, y, z, SoundEvents.ZOMBIE_INFECT,
                    SoundSource.HOSTILE, 1.0F, 1.0F);

        } else if (tier == 1) {
            level.sendParticles(ParticleTypes.CLOUD, x, y, z, 6, 0.3, 0.3, 0.3, 0.03);
            level.sendParticles(ParticleTypes.CRIT, x, y, z, 4, 0.3, 0.3, 0.3, 0.08);
            minion.addEffect(new MobEffectInstance(MobEffects.GLOWING, 25, 0, false, false));
            level.playSound(null, x, y, z, SoundEvents.EVOKER_CAST_SPELL,
                    SoundSource.HOSTILE, 1.0F, 1.0F);

        } else if (tier == 2) {
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 8, 0.4, 0.4, 0.4, 0.05);
            level.sendParticles(ParticleTypes.CRIT, x, y, z, 6, 0.4, 0.4, 0.4, 0.08);
            level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 1, 0, 0, 0, 0);
            minion.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false));
            level.playSound(null, x, y, z, SoundEvents.RAVAGER_ROAR,
                    SoundSource.HOSTILE, 1.0F, 1.2F);

        } else {
            level.sendParticles(ParticleTypes.DRAGON_BREATH, x, y, z, 10, 0.5, 0.5, 0.5, 0.08);
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 10, 0.5, 0.5, 0.5, 0.08);
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 8, 0.4, 0.4, 0.4, 0.1);
            level.sendParticles(ParticleTypes.CRIT, x, y, z, 10, 0.5, 0.5, 0.5, 0.08);
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0, 0, 0, 0);
            minion.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
            level.playSound(null, x, y, z, SoundEvents.WITHER_SPAWN,
                    SoundSource.HOSTILE, 1.0F, 1.5F);
        }
    }

    // ================== 原有逻辑 ==================

    private void scanForTargets64() {
        AABB area = new AABB(
                this.getX() - 32.0, this.getY() - 32.0, this.getZ() - 32.0,
                this.getX() + 32.0, this.getY() + 32.0, this.getZ() + 32.0
        );

        List<LivingEntity> candidates = this.level().getEntitiesOfClass(LivingEntity.class, area);

        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (LivingEntity entity : candidates) {
            if (!isValidTarget(entity)) continue;

            double d = this.distanceToSqr(entity);
            if (d < closestDist) {
                closestDist = d;
                closest = entity;
            }
        }

        if (closest != null) {
            this.setTarget(closest);
        }
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (!entity.isAlive()) return false;

        if (entity.getType().is(ModTags.Entities.ZOMPERIUM)) {
            return false;
        }

        if (entity instanceof Player p) {
            return !p.isSpectator() && !p.isCreative();
        }

        if (entity instanceof AbstractVillager) {
            return true;
        }

        if (entity instanceof Raider) {
            return true;
        }

        if (entity instanceof IronGolem) {
            return true;
        }

        return false;
    }

    public void onTotemUsed() {
        this.hasUsedTotem = true;
        this.isDesperate = false;
        this.setHealth(150.0F);
        this.actionCooldown = 40;
        this.recentDamage = 0;

        if (!this.totemTriggered) {
            this.totemTriggered = true;
            summonMinions(SUMMON_COUNT, ZomperiumPrivate.SummonVariant.TOTEM_ELITE);
        }
    }

    /** 仅物理伤害限伤 20 */
    private boolean isPhysicalDamage(DamageSource source) {
        if (source.getDirectEntity() == null && source.getEntity() == null) return false;
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        if (source.is(DamageTypeTags.IS_FIRE)) return false;
        if (source.is(DamageTypeTags.IS_FALL)) return false;
        if (source.is(DamageTypeTags.IS_DROWNING)) return false;
        if (source.is(DamageTypeTags.IS_FREEZING)) return false;
        if (source.is(DamageTypeTags.IS_LIGHTNING)) return false;
        if (source.is(DamageTypes.MAGIC)) return false;
        if (source.is(DamageTypes.INDIRECT_MAGIC)) return false;
        if (source.is(DamageTypes.WITHER)) return false;
        if (source.is(DamageTypes.SONIC_BOOM)) return false;
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) return false;
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.deathAnimating) return false;

        boolean byPlayer =
                source.getEntity() instanceof Player
                        || source.getDirectEntity() instanceof Player;

        if (byPlayer) {
            if (isPhysicalDamage(source) && amount > PHYSICAL_DAMAGE_CAP) {
                amount = PHYSICAL_DAMAGE_CAP;
            }
        } else {
            if (amount > NON_PLAYER_DAMAGE_CAP) {
                amount = NON_PLAYER_DAMAGE_CAP;
            }
        }

        boolean result = super.hurt(source, amount);

        if (result && !this.level().isClientSide()) {
            this.recentDamage += amount;
            this.recentDamageTimer = 100;

            int reduce = (int) (amount * 10);
            if (reduce > 0) {
                this.summonCooldown = Math.max(0, this.summonCooldown - reduce);
            }
        }

        return result;
    }

    private void handleOffhandLogic() {
        if (hasNegativeEffect() && RNG.nextFloat() < 0.5F) {
            startUsing(new ItemStack(Items.MILK_BUCKET));
            this.actionCooldown = 60;
            return;
        }

        if (this.isInLava()) {
            ItemStack potion = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LONG_FIRE_RESISTANCE);
            startUsing(potion);
            this.actionCooldown = 100;
            return;
        }

        if (this.isOnFire() || isOnMagmaBlock()) {
            throwSplashPotionAtSelf(Potions.FIRE_RESISTANCE);
            this.clearFire();
            this.actionCooldown = 100;
            return;
        }

        if (this.getHealth() < this.getMaxHealth() && this.healCooldown <= 0) {
            ItemStack potion = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HARMING);
            startUsing(potion);
            this.healCooldown = 100;
            this.actionCooldown = 60;
            return;
        }

        if (RNG.nextFloat() < 0.15F) {
            throwSplashPotionAtZombie(Potions.STRONG_HARMING);
            this.actionCooldown = 80;
        }
    }

    private void handlePullApproach() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        double horizontalDist = Math.sqrt(
                Math.pow(this.getX() - target.getX(), 2)
                        + Math.pow(this.getZ() - target.getZ(), 2));
        double verticalDiff = target.getY() - this.getY();

        if (horizontalDist > 20.0 || verticalDiff > 10.0) {
            teleportToTarget(target);
            this.pullCooldown = 60;
            return;
        }

        if (horizontalDist > 10.0 || verticalDiff > 2.0) {
            useFishingRod(target);
            this.pullCooldown = 60;
        }
    }

    private void teleportToTarget(LivingEntity target) {
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.ENDER_PEARL));
        this.swing(InteractionHand.OFF_HAND, true);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENDER_PEARL_THROW, SoundSource.HOSTILE, 1.0F, 1.0F);

        double dx = target.getX() - this.getX();
        double dy = target.getY() + 1.0 - (this.getY() + 2.5);
        double dz = target.getZ() - this.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        ThrownEnderpearl pearl = new ThrownEnderpearl(this.level(), this);
        pearl.setItem(new ItemStack(Items.ENDER_PEARL));
        pearl.setPos(this.getX(), this.getY() + 2.5, this.getZ());

        pearl.shoot(dx, dy + horizontalDist * 0.3, dz, 1.8F, 0.3F);

        this.level().addFreshEntity(pearl);
        this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
    }

    private void handleHarmBurst() {
        if (this.harmBurstRemaining <= 0) return;
        if (this.harmBurstCooldown > 0) return;

        throwSplashPotionAtSelf(Potions.STRONG_HARMING);
        this.harmBurstRemaining--;
        this.harmBurstCooldown = 10;
    }

    private void checkDesperatePhase() {
        if (this.isDesperate) return;
        if (this.getHealth() > 50.0F) return;

        this.isDesperate = true;
    }

    private void startUsing(ItemStack stack) {
        this.setItemSlot(EquipmentSlot.OFFHAND, stack);
        this.startUsingItem(InteractionHand.OFF_HAND);
    }

    @Override
    public void completeUsingItem() {
        super.completeUsingItem();
        if (!this.level().isClientSide()) {
            this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        }
    }

    private void throwSplashPotionAtSelf(net.minecraft.world.item.alchemy.Potion potion) {
        ItemStack stack = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion);

        ThrownPotion thrownPotion = new ThrownPotion(this.level(), this);
        thrownPotion.setItem(stack);
        thrownPotion.setPos(this.getX(), this.getY() + 1.2, this.getZ());
        thrownPotion.shoot(0, -0.3, 0, 0.6F, 0.8F);

        this.level().addFreshEntity(thrownPotion);

        this.setItemSlot(EquipmentSlot.OFFHAND, stack);
        this.swing(InteractionHand.OFF_HAND, true);

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.SPLASH_POTION_THROW, SoundSource.HOSTILE, 1.0F, 1.0F);
    }

    private void throwSplashPotionAtZombie(net.minecraft.world.item.alchemy.Potion potion) {
        List<Zombie> zombies = this.level().getEntitiesOfClass(Zombie.class,
                this.getBoundingBox().inflate(8.0));
        if (zombies.isEmpty()) return;

        Zombie target = zombies.get(RNG.nextInt(zombies.size()));
        ItemStack stack = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion);

        ThrownPotion thrownPotion = new ThrownPotion(this.level(), this);
        thrownPotion.setItem(stack);
        thrownPotion.setPos(this.getX(), this.getY() + 1.2, this.getZ());

        double dx = target.getX() - this.getX();
        double dy = target.getY() + target.getBbHeight() / 2.0 - (this.getY() + 1.2);
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        thrownPotion.shoot(dx, dy + dist * 0.1, dz, 1.2F, 0.5F);

        this.level().addFreshEntity(thrownPotion);

        this.setItemSlot(EquipmentSlot.OFFHAND, stack);
        this.swing(InteractionHand.OFF_HAND, true);

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.SPLASH_POTION_THROW, SoundSource.HOSTILE, 1.0F, 1.0F);
    }

    private void useFishingRod(LivingEntity target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        if (this.activeHook != null) {
            if (this.activeHook.isAlive()) this.activeHook.discard();
            this.activeHook = null;
            this.hookTarget = null;
        }

        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.FISHING_ROD));
        this.swing(InteractionHand.OFF_HAND, true);

        net.minecraftforge.common.util.FakePlayer fakePlayer = FakePlayerFactory.get(
                serverLevel,
                new GameProfile(UUID.randomUUID(), "CommanderFishing")
        );
        fakePlayer.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());

        FishingHook hook = new FishingHook(fakePlayer, serverLevel, 0, 0);
        hook.setPos(this.getX(), this.getY() + 1.0, this.getZ());

        double dx = target.getX() - this.getX();
        double dy = target.getY() + target.getBbHeight() / 2.0 - (this.getY() + 1.0);
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        hook.shoot(dx, dy + dist * 0.15, dz, 1.5F, 0.5F);
        serverLevel.addFreshEntity(hook);

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.FISHING_BOBBER_THROW, SoundSource.HOSTILE, 1.0F, 1.0F);

        this.activeHook = hook;
        this.hookTarget = target;
        this.hookPullTimer = 5;
    }

    private boolean hasNegativeEffect() {
        return this.hasEffect(MobEffects.WITHER)
                || this.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)
                || this.hasEffect(MobEffects.WEAKNESS)
                || this.hasEffect(MobEffects.POISON)
                || this.hasEffect(MobEffects.BLINDNESS)
                || this.hasEffect(MobEffects.CONFUSION);
    }

    private boolean isOnMagmaBlock() {
        BlockPos below = this.blockPosition().below();
        BlockState state = this.level().getBlockState(below);
        return state.is(Blocks.MAGMA_BLOCK);
    }

    private void destroyVehicles() {
        AABB area = this.getBoundingBox().inflate(1.5);
        List<Entity> vehicles = this.level().getEntities(this, area, e ->
                e instanceof net.minecraft.world.entity.vehicle.Boat
                        || e instanceof net.minecraft.world.entity.vehicle.Minecart
                        || e instanceof net.minecraft.world.entity.vehicle.ChestBoat);

        for (Entity v : vehicles) {
            v.hurt(this.damageSources().generic(), Float.MAX_VALUE);
        }
    }

    private void breakCobwebs() {
        BlockPos center = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-3, -1, -3),
                center.offset(3, 3, 3))) {
            if (this.level().getBlockState(pos).is(Blocks.COBWEB)) {
                this.level().destroyBlock(pos, true, this);
            }
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        RandomSource random = this.getRandom();

        int rotten = random.nextInt(3);
        if (looting > 0) {
            rotten += random.nextInt(looting + 1);
        }
        if (rotten > 0) {
            this.spawnAtLocation(new ItemStack(Items.ROTTEN_FLESH, rotten));
        }

        this.spawnAtLocation(new ItemStack(ExampleMod.COMMANDER_TREASURE_BAG.get()));
        this.spawnAtLocation(new ItemStack(Items.IRON_BLOCK, 3 + random.nextInt(3)));
        this.spawnAtLocation(new ItemStack(Items.IRON_INGOT, 11 + random.nextInt(12)));

        if (random.nextFloat() < 0.5F) {
            this.spawnAtLocation(new ItemStack(Items.CAKE));
        }
        if (random.nextFloat() < 0.10F) {
            this.spawnAtLocation(new ItemStack(Items.FISHING_ROD));
        }
        if (random.nextFloat() < 0.60F) {
            this.spawnAtLocation(new ItemStack(Items.ENDER_PEARL, random.nextInt(4)));
        }
        if (random.nextFloat() < 0.30F) {
            this.spawnAtLocation(new ItemStack(Items.MILK_BUCKET));
        }
        if (random.nextFloat() < 0.50F) {
            int count = random.nextInt(3);
            for (int i = 0; i < count; i++) {
                ItemStack potion = PotionUtils.setPotion(
                        new ItemStack(Items.SPLASH_POTION), Potions.LONG_FIRE_RESISTANCE);
                this.spawnAtLocation(potion);
            }
        }
        if (random.nextFloat() < 0.50F) {
            int count = random.nextInt(3);
            for (int i = 0; i < count; i++) {
                ItemStack potion = PotionUtils.setPotion(
                        new ItemStack(Items.SPLASH_POTION), Potions.STRONG_HARMING);
                this.spawnAtLocation(potion);
            }
        }
    }
}