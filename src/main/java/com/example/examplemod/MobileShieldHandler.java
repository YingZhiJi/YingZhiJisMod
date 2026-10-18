package com.example.examplemod;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobileShieldHandler {

    public static final String NBT_STRONG_GUARD = "yzj_mobile_shield_strong";

    private static final UUID SLOW_UUID =
            UUID.fromString("d4e5f6a7-2222-3333-4444-555566667777");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        int cd = player.getPersistentData().getInt(MobileShieldItem.NBT_DASH_CD);
        if (cd > 0) {
            player.getPersistentData().putInt(MobileShieldItem.NBT_DASH_CD, cd - 1);
        }

        // 强效举盾：潜行 + 主副手有盾牌
        boolean strongGuard = false;
        if (player.isShiftKeyDown()) {
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            if (main.is(ExampleMod.MOBILE_SHIELD.get())
                    || off.is(ExampleMod.MOBILE_SHIELD.get())) {
                strongGuard = true;
            }
        }

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SLOW_UUID);
            if (strongGuard) {
                speed.addTransientModifier(new AttributeModifier(
                        SLOW_UUID, "mobile_shield_strong_slow", -0.3,
                        AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }

        player.getPersistentData().putBoolean(NBT_STRONG_GUARD, strongGuard);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        ItemStack shield = null;
        EquipmentSlot slot = null;
        if (main.is(ExampleMod.MOBILE_SHIELD.get())) {
            shield = main;
            slot = EquipmentSlot.MAINHAND;
        } else if (off.is(ExampleMod.MOBILE_SHIELD.get())) {
            shield = off;
            slot = EquipmentSlot.OFFHAND;
        }
        if (shield == null) return;

        boolean strong = player.getPersistentData().getBoolean(NBT_STRONG_GUARD);
        // 普通举盾：正在使用该物品
        boolean normalBlocking = player.isUsingItem() && player.getUseItem() == shield;

        if (!strong && !normalBlocking) return;

        // 只挡正面 180°
        if (!isFromFront(player, event.getSource())) return;

        // 穿刺箭无视盾牌
        Entity directEntity = event.getSource().getDirectEntity();
        if (directEntity instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0) {
            return;
        }
        // 原版无视盾牌的伤害标签
        if (event.getSource().is(DamageTypeTags.BYPASSES_SHIELD)) {
            return;
        }

        final EquipmentSlot fslot = slot;

        if (strong) {
            // ===== 强效举盾：正面全免疫，不破盾 =====
            event.setCanceled(true);

            // 扣 2 耐久
            shield.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(fslot));

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS,
                    1.0F, 0.8F + player.getRandom().nextFloat() * 0.4F);

            if (player.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.CRIT,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        5, 0.3, 0.3, 0.3, 0.1);
            }
        } else {
            // ===== 普通举盾：模拟原版盾牌 =====
            event.setCanceled(true);

            // 扣耐久：原版逻辑 1 + floor(伤害 / 4)
            int durabilityCost = 1 + (int) Math.floor(event.getAmount() / 4.0F);
            shield.hurtAndBreak(durabilityCost, player, p -> p.broadcastBreakEvent(fslot));

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS,
                    1.0F, 0.8F + player.getRandom().nextFloat() * 0.4F);

            // 斧头破盾
            Entity attacker = event.getSource().getEntity();
            if (attacker instanceof LivingEntity livingAttacker) {
                ItemStack weapon = livingAttacker.getMainHandItem();
                if (weapon.getItem() instanceof AxeItem) {
                    // 禁用盾牌 5 秒（100 tick）
                    player.getCooldowns().addCooldown(shield.getItem(), 100);
                    player.stopUsingItem();
                    player.level().broadcastEntityEvent(player, (byte) 30); // 播放破盾音效
                }
            }
        }
    }

    private static boolean isFromFront(Player player, DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker == null) attacker = source.getDirectEntity();
        if (attacker == null) return true;

        double dx = attacker.getX() - player.getX();
        double dz = attacker.getZ() - player.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.001) return true;
        dx /= len;
        dz /= len;

        Vec3 look = player.getLookAngle();
        Vec3 lookH = new Vec3(look.x, 0, look.z).normalize(); // 只看水平朝向
        return lookH.x * dx + lookH.z * dz > 0.0;
    }
}