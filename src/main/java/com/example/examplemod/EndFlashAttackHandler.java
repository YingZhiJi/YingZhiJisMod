package com.example.examplemod;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class EndFlashAttackHandler {

    public static final String MARK_TAG = "yzj_end_flash_marked";

    private static final float TRUE_DAMAGE = 5.0F;

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof EndFlashItem)) return;

        event.setCanceled(true);

        Entity target = event.getTarget();

        // ★ 末影龙 Part → 末影龙本体
        if (target instanceof EnderDragonPart part) {
            target = part.parentMob;
        }
        if (!(target instanceof LivingEntity living)) return;

        float charge = player.getAttackStrengthScale(0.5F);

        // ===== 主伤害 =====
        float attributeDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float enchantedBonus = EnchantmentHelper.getDamageBonus(weapon, living.getMobType());
        float chargeScale = 0.2F + charge * charge * 0.8F;
        float weaponDamage = (attributeDamage + enchantedBonus) * chargeScale;

        // ===== 暴击判定 =====
        boolean crit = charge > 0.9F
                && player.fallDistance > 0.0F
                && !player.onGround()
                && !player.onClimbable()
                && !player.isInWater()
                && !player.hasEffect(MobEffects.BLINDNESS)
                && !player.isPassenger()
                && !player.isSprinting();

        // ===== 标记 / 暴击倍率 / 粒子 =====
        CompoundTag data = living.getPersistentData();
        float critMultiplier = 1.0F;

        if (crit) {
            critMultiplier = 1.5F;

            boolean superCrit = data.getBoolean(MARK_TAG);
            if (superCrit) {
                critMultiplier = 3.0F;
                data.remove(MARK_TAG);
            } else {
                data.putBoolean(MARK_TAG, true);
            }

            spawnCritParticles(player, living, superCrit);
        }

        // ===== 第一次 hurt：主伤害 =====
        living.invulnerableTime = 0;
        boolean hit = living.hurt(
                player.damageSources().playerAttack(player),
                weaponDamage * critMultiplier);

        if (hit) {
            EnchantmentHelper.doPostHurtEffects(living, player);

            int fireAspect = EnchantmentHelper.getFireAspect(player);
            if (fireAspect > 0 && !living.isOnFire()) {
                living.setSecondsOnFire(fireAspect * 4);
            }

            // ===== 第二次 hurt：真伤 =====
            living.invulnerableTime = 0;
            living.hurt(
                    player.damageSources().playerAttack(player),
                    TRUE_DAMAGE * critMultiplier);
        }

        // ===== 横扫 =====
        boolean canSweep = !crit
                && !player.isSprinting()
                && player.onGround()
                && charge > 0.9F;

        if (canSweep) {
            float sweepDamage = weaponDamage / 3.0F;

            int sweepingLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    Enchantments.SWEEPING_EDGE, weapon);
            if (sweepingLevel > 0) {
                float sweepingRatio = 1.0F - 1.0F / (sweepingLevel + 1.0F);
                sweepDamage += sweepingRatio * weaponDamage;
            }

            List<LivingEntity> nearby = player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    target.getBoundingBox().inflate(1.0, 0.25, 1.0));

            for (LivingEntity e : nearby) {
                if (e == player || e == target) continue;
                if (e.isAlliedTo(player)) continue;

                e.invulnerableTime = 0;
                e.hurt(player.damageSources().playerAttack(player), sweepDamage);

                double dx = e.getX() - player.getX();
                double dz = e.getZ() - player.getZ();
                double len = Math.sqrt(dx * dx + dz * dz);
                if (len > 0.0) {
                    e.push(dx / len * 0.4, 0.1, dz / len * 0.4);
                }
            }
        }

        // ===== 耐久 =====
        weapon.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        // ===== 冷却 / 统计 / 饥饿 =====
        player.resetAttackStrengthTicker();
        player.causeFoodExhaustion(0.1F);
        player.awardStat(Stats.ITEM_USED.get(weapon.getItem()));
    }

    /**
     * 暴击粒子：龙息 + 末影 + 暴击 + 白烟
     * 速度较高 → 快速散开消失
     */
    private static void spawnCritParticles(Player player, LivingEntity target, boolean superCrit) {
        if (!(player.level() instanceof ServerLevel sl)) return;

        double px = target.getX();
        double py = target.getY() + target.getBbHeight() * 0.5;
        double pz = target.getZ();

        int dragonBreath = superCrit ? 25 : 12;
        int portal       = superCrit ? 30 : 15;
        int crit         = superCrit ? 25 : 12;
        int cloud        = superCrit ? 20 : 10;

        // 龙息：速度快，散得快
        sl.sendParticles(ParticleTypes.DRAGON_BREATH,
                px, py, pz, dragonBreath, 0.5, 0.5, 0.5, 0.5);

        // 末影
        sl.sendParticles(ParticleTypes.PORTAL,
                px, py, pz, portal, 0.6, 0.6, 0.6, 0.7);

        // 暴击
        sl.sendParticles(ParticleTypes.CRIT,
                px, py, pz, crit, 0.6, 0.6, 0.6, 0.8);

        // 白烟
        sl.sendParticles(ParticleTypes.CLOUD,
                px, py, pz, cloud, 0.5, 0.5, 0.5, 0.5);
    }
}