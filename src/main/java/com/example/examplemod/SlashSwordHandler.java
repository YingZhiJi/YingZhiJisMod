package com.example.examplemod;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.stats.Stats;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class SlashSwordHandler {

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SlashSwordItem)) return;

        // 取消原版攻击，完全自定义
        event.setCanceled(true);

        Entity target = event.getTarget();
        float charge = player.getAttackStrengthScale(0.5F);

        // 基础伤害（含充能）
        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        baseDamage *= 0.2F + charge * charge * 0.8F;

        // 附魔加成（锋利等）
        float enchantedBonus = 0.0F;
        if (target instanceof LivingEntity living) {
            enchantedBonus = EnchantmentHelper.getDamageBonus(weapon, living.getMobType());
        }

        // 总伤害 = 基础 + 附魔
        float totalDamage = baseDamage + enchantedBonus;

        // 暴击判定（原版规则）
        boolean canCrit = charge > 0.9F
                && player.fallDistance > 0.0F
                && !player.onGround()
                && !player.onClimbable()
                && !player.isInWater()
                && !player.hasEffect(MobEffects.BLINDNESS)
                && !player.isPassenger()
                && !player.isSprinting()
                && target instanceof LivingEntity;

        // 横扫判定（原版规则）
        boolean canSweep = !canCrit
                && !player.isSprinting()
                && player.onGround()
                && charge > 0.9F;

        // ===== 主目标伤害 =====
        float mainDamage = totalDamage;
        if (canCrit) {
            mainDamage *= 2.0F;  // 斩剑暴击 = 总伤 × 2（原版是 × 1.5）
        }

        target.hurt(player.damageSources().playerAttack(player), mainDamage);

        // ===== 横扫 =====
        if (canSweep) {
            // 基础横扫 = 总伤的一半（锋利只算一半）
            float sweepDamage = totalDamage * 0.5F;

            // 横扫之刃正常加伤
            int sweepingLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    Enchantments.SWEEPING_EDGE, weapon);
            if (sweepingLevel > 0) {
                float sweepingRatio = 1.0F - 1.0F / (sweepingLevel + 1.0F);
                sweepDamage += sweepingRatio * totalDamage;
            }

            // 范围 1×0.25×1 格（和原版一致）
            List<LivingEntity> nearby = player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    target.getBoundingBox().inflate(1.0, 0.25, 1.0));

            for (LivingEntity entity : nearby) {
                if (entity == player || entity == target) continue;
                if (entity.isAlliedTo(player)) continue;

                entity.hurt(player.damageSources().playerAttack(player), sweepDamage);

                // 横扫击退
                double dx = entity.getX() - player.getX();
                double dz = entity.getZ() - player.getZ();
                double len = Math.sqrt(dx * dx + dz * dz);
                if (len > 0.0) {
                    entity.push(dx / len * 0.4, 0.1, dz / len * 0.4);
                }
            }
        }

        // ===== 附魔后处理（火焰附加等） =====
        if (target instanceof LivingEntity living) {
            EnchantmentHelper.doPostHurtEffects(living, player);
        }

        // ===== 耐久 =====
        if (!player.getAbilities().instabuild) {
            weapon.hurtAndBreak(1, player,
                    p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }

        // ===== 重置冷却 & 统计 =====
        player.resetAttackStrengthTicker();
        player.awardStat(Stats.ITEM_USED.get(weapon.getItem()));
    }
}