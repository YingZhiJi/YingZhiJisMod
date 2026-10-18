package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Random;

public class ZomperiumPrivate extends Monster {

    public static final String NBT_SUMMONED = "yzj_summoned";
    public static final String NBT_DROPS = "yzj_summoned_drops";

    private static final Random RNG = new Random();

    private int fireExtinguishCooldown = 0;
    private int potionHoldTimer = 0;
    private int vehicleCooldown = 0;

    /** 召唤变体：由指挥官在不同阶段使用 */
    public enum SummonVariant {
        TIMER_BASE(20.0F, 0),          // 定时基础：下界合金斩剑
        TIMER_AFTER_TOTEM(30.0F, 1),   // 图腾后定时：下界合金斩剑
        PHASE_100(40.0F, 2),           // 100 血触发：锋利3下界合金斧
        TOTEM_ELITE(80.0F, 3);         // 图腾触发：耐久100锋利10下界合金剑

        public final float health;
        public final int tier;

        SummonVariant(float health, int tier) {
            this.health = health;
            this.tier = tier;
        }

        public boolean drops() {
            return this == TOTEM_ELITE;
        }

        public ItemStack buildWeapon() {
            switch (this) {
                case TIMER_BASE:
                case TIMER_AFTER_TOTEM: {
                    return new ItemStack(ExampleMod.NETHERITE_SLASH_SWORD.get());
                }
                case PHASE_100: {
                    ItemStack s = new ItemStack(Items.NETHERITE_AXE);
                    s.enchant(Enchantments.SHARPNESS, 3);
                    return s;
                }
                case TOTEM_ELITE: {
                    ItemStack s = new ItemStack(Items.NETHERITE_SWORD);
                    s.enchant(Enchantments.SHARPNESS, 10);
                    s.setDamageValue(s.getMaxDamage() - 100);
                    return s;
                }
                default:
                    return new ItemStack(Items.IRON_SWORD);
            }
        }
    }

    public ZomperiumPrivate(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 5;

        this.setItemSlot(EquipmentSlot.MAINHAND, randomSword());
        this.setDropChance(EquipmentSlot.MAINHAND, 1.0F);
    }

    private static ItemStack randomSword() {
        int r = RNG.nextInt(4);
        Item sword = switch (r) {
            case 0 -> Items.IRON_SWORD;
            case 1 -> Items.GOLDEN_SWORD;
            case 2 -> Items.DIAMOND_SWORD;
            default -> Items.NETHERITE_SWORD;
        };
        return new ItemStack(sword);
    }

    /** 指挥官召唤时调用：覆盖血量/武器/掉落行为 */
    public void applySummonVariant(SummonVariant variant) {
        AttributeInstance hp = this.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) {
            hp.setBaseValue(variant.health);
            this.setHealth(variant.health);
        }

        ItemStack weapon = variant.buildWeapon();
        this.setItemSlot(EquipmentSlot.MAINHAND, weapon);
        this.setDropChance(EquipmentSlot.MAINHAND, variant.drops() ? 1.0F : 0.0F);

        CompoundTag data = this.getPersistentData();
        data.putBoolean(NBT_SUMMONED, true);
        data.putBoolean(NBT_DROPS, variant.drops());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 35.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    public static boolean checkSpawnRules(EntityType<? extends ZomperiumPrivate> type,
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

        if (this.fireExtinguishCooldown > 0) this.fireExtinguishCooldown--;
        if (this.vehicleCooldown > 0) this.vehicleCooldown--;

        if (this.potionHoldTimer > 0) {
            this.potionHoldTimer--;
            if (this.potionHoldTimer <= 0) {
                this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
        }

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

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        // 召唤物：不产生自定义掉落（腐肉/铁锭/僵尸头）
        // 精英变体的武器掉落由 setDropChance 控制
        if (this.getPersistentData().getBoolean(NBT_SUMMONED)) {
            return;
        }

        super.dropCustomDeathLoot(source, looting, recentlyHit);

        RandomSource random = this.getRandom();

        int rotten = random.nextInt(3);
        if (looting > 0) {
            rotten += random.nextInt(looting + 1);
        }
        if (rotten > 0) {
            this.spawnAtLocation(new ItemStack(Items.ROTTEN_FLESH, rotten));
        }

        float ironChance = 0.10F + looting * 0.02F;
        if (random.nextFloat() < ironChance) {
            int count = 1 + random.nextInt(5);
            this.spawnAtLocation(new ItemStack(Items.IRON_INGOT, count));
        }

        if (recentlyHit && source.getEntity() instanceof Player) {
            if (random.nextFloat() < 0.01F + looting * 0.01F) {
                this.spawnAtLocation(new ItemStack(Items.ZOMBIE_HEAD));
            }
        }
    }
}