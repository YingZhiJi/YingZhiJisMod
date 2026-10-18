package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ZomperiumScout extends Monster {

    private static final Random RNG = new Random();

    private int potionHoldTimer = 0;
    private int vehicleCooldown = 0;
    private int fireExtinguishCooldown = 0;
    private int healCooldown = 0;

    public ZomperiumScout(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 5;
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 35.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    // ===== 自然生成：露天、无视昼夜 =====
    public static boolean checkSpawnRules(EntityType<? extends ZomperiumScout> type,
                                          ServerLevelAccessor level,
                                          MobSpawnType spawnType,
                                          BlockPos pos,
                                          RandomSource random) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return false;
        if (!level.canSeeSky(pos)) return false;
        if (!level.isEmptyBlock(pos) && !level.getBlockState(pos).is(Blocks.COBWEB)) return false;
        if (!level.isEmptyBlock(pos.above())) return false;
        return true;
    }

    // ===== 自然生成时带 5 个列兵 =====
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag dataTag) {

        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);

        // 只有自然生成才组队
        if (reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION) {
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 5; i++) {
                    ZomperiumPrivate soldier = ExampleMod.ZOMPERIUM_PRIVATE_ENTITY.get().create(serverLevel);
                    if (soldier == null) continue;

                    double dx = (this.random.nextDouble() - 0.5) * 6.0;
                    double dz = (this.random.nextDouble() - 0.5) * 6.0;
                    soldier.moveTo(this.getX() + dx, this.getY(), this.getZ() + dz,
                            this.random.nextFloat() * 360.0F, 0.0F);

                    soldier.finalizeSpawn(level, difficulty, MobSpawnType.MOB_SUMMONED, null, null);
                    serverLevel.addFreshEntity(soldier);
                }
            }
        }

        return result;
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
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
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
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) return;

        if (this.potionHoldTimer > 0) {
            this.potionHoldTimer--;
            if (this.potionHoldTimer <= 0) {
                this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
        }
        if (this.vehicleCooldown > 0) this.vehicleCooldown--;
        if (this.fireExtinguishCooldown > 0) this.fireExtinguishCooldown--;
        if (this.healCooldown > 0) this.healCooldown--;

        if (this.tickCount % 5 == 0) {
            breakCobwebs();
        }

        if (this.vehicleCooldown <= 0) {
            destroyVehicles();
            this.vehicleCooldown = 10;
        }

        if (this.isOnFire()
                && !this.isInLava()
                && this.fireExtinguishCooldown <= 0
                && !this.isUsingItem()) {
            throwWaterPotionAtSelf();
            this.fireExtinguishCooldown = 100;
        }

        if (this.healCooldown <= 0 && !this.isUsingItem() && this.potionHoldTimer <= 0) {
            if (this.getHealth() < this.getMaxHealth()) {
                drinkHarmingPotionSelf();
                this.healCooldown = 80;
                return;
            }

            LivingEntity woundedAlly = findWoundedAlly();
            if (woundedAlly != null) {
                throwSplashHarmingAtAlly(woundedAlly);
                this.healCooldown = 60;
            }
        }
    }

    private void drinkHarmingPotionSelf() {
        ItemStack potion = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HARMING);
        this.setItemSlot(EquipmentSlot.OFFHAND, potion);
        this.potionHoldTimer = 30;
        this.startUsingItem(InteractionHand.OFF_HAND);
    }

    private LivingEntity findWoundedAlly() {
        AABB area = this.getBoundingBox().inflate(12.0);
        List<LivingEntity> allies = this.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != this
                        && e.isAlive()
                        && e.getType().is(ModTags.Entities.ZOMPERIUM)
                        && e.getHealth() < e.getMaxHealth());
        if (allies.isEmpty()) return null;

        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (LivingEntity e : allies) {
            double d = this.distanceToSqr(e);
            if (d < closestDist) {
                closestDist = d;
                closest = e;
            }
        }
        return closest;
    }

    private void throwSplashHarmingAtAlly(LivingEntity ally) {
        ItemStack potion = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.STRONG_HARMING);

        this.setItemSlot(EquipmentSlot.OFFHAND, potion);
        this.potionHoldTimer = 20;
        this.swing(InteractionHand.OFF_HAND, true);

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.SPLASH_POTION_THROW, SoundSource.HOSTILE, 1.0F, 1.0F);

        ThrownPotion thrownPotion = new ThrownPotion(this.level(), this);
        thrownPotion.setItem(potion);
        thrownPotion.setPos(this.getX(), this.getY() + 1.2, this.getZ());

        double dx = ally.getX() - this.getX();
        double dy = ally.getY() + ally.getBbHeight() / 2.0 - (this.getY() + 1.2);
        double dz = ally.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        thrownPotion.shoot(dx, dy + dist * 0.1, dz, 1.2F, 0.5F);
        this.level().addFreshEntity(thrownPotion);
    }

    private void throwWaterPotionAtSelf() {
        ItemStack potion = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.WATER);

        this.setItemSlot(EquipmentSlot.OFFHAND, potion);
        this.potionHoldTimer = 20;
        this.swing(InteractionHand.OFF_HAND, true);

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.SPLASH_POTION_THROW, SoundSource.HOSTILE, 1.0F, 1.0F);

        ThrownPotion thrownPotion = new ThrownPotion(this.level(), this);
        thrownPotion.setItem(potion);
        thrownPotion.setPos(this.getX(), this.getY() + 1.2, this.getZ());
        thrownPotion.shoot(0, -0.3, 0, 0.5F, 0.5F);
        this.level().addFreshEntity(thrownPotion);
    }

    @Override
    public void completeUsingItem() {
        super.completeUsingItem();
        if (!this.level().isClientSide()) {
            this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            this.potionHoldTimer = 0;
        }
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
                center.offset(-2, -1, -2),
                center.offset(2, 2, 2))) {
            if (this.level().getBlockState(pos).is(Blocks.COBWEB)) {
                this.level().destroyBlock(pos, true, this);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    // ===== 掉落：手台 + 腐肉 + 铁锭 + 僵尸头 =====
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);

        RandomSource random = this.getRandom();

        // 专属掉落：Z帝国手台
        this.spawnAtLocation(new ItemStack(ExampleMod.ZOMPERIUM_HANDSET_RADIO.get()));

        // 腐肉：0-2，每级抢夺 +0-1
        int rotten = random.nextInt(3);
        if (looting > 0) {
            rotten += random.nextInt(looting + 1);
        }
        if (rotten > 0) {
            this.spawnAtLocation(new ItemStack(Items.ROTTEN_FLESH, rotten));
        }

        // 铁锭：10% 基础，每级抢夺 +2%，1-5 个
        float ironChance = 0.10F + looting * 0.02F;
        if (random.nextFloat() < ironChance) {
            int count = 1 + random.nextInt(5);
            this.spawnAtLocation(new ItemStack(Items.IRON_INGOT, count));
        }

        // 僵尸头：1% 基础，每级抢夺 +1%，只有玩家击杀才掉落
        if (recentlyHit && source.getEntity() instanceof Player) {
            if (random.nextFloat() < 0.01F + looting * 0.01F) {
                this.spawnAtLocation(new ItemStack(Items.ZOMBIE_HEAD));
            }
        }
    }
}