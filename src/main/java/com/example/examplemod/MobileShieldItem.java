package com.example.examplemod;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class MobileShieldItem extends Item {

    public static final String NBT_DASH_CD = "yzj_mobile_shield_dash_cd";

    private static final UUID ARMOR_UUID =
            UUID.fromString("c3d4e5f6-1111-2222-3333-444455556666");

    private static final int DASH_COOLDOWN = 30;
    private static final double DASH_SPEED = 1.5;
    private static final float DASH_DAMAGE = 12.0F;
    private static final double DASH_RANGE = 3.0;
    private static final int DASH_INVULN_TICKS = 30;

    public MobileShieldItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            return stack.getHoverName();
        }
        return Component.translatable(this.getDescriptionId(stack))
                .withStyle(ChatFormatting.RED);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.mobile_shield.desc.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.mobile_shield.desc.2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.mobile_shield.desc.3").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.mobile_shield.desc.4").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.mobile_shield.desc.5").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> base = super.getDefaultAttributeModifiers(slot);
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> b = ImmutableMultimap.builder();
            b.putAll(base);
            b.put(Attributes.ARMOR, new AttributeModifier(
                    ARMOR_UUID, "mobile_shield_armor", 4.0,
                    AttributeModifier.Operation.ADDITION));
            return b.build();
        }
        return base;
    }

    @Override public boolean isEnchantable(ItemStack stack) { return false; }
    @Override public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment e) { return false; }
    @Override public boolean isBookEnchantable(ItemStack stack, ItemStack book) { return false; }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BLOCK; }

    @Override
    public int getUseDuration(ItemStack stack) { return 72000; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            return InteractionResultHolder.success(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;
        if (player.getMainHandItem() != stack) return false;

        if (!player.isShiftKeyDown()) {
            return false;
        }

        if (entity.level().isClientSide()) {
            return true;
        }

        if (player.getPersistentData().getInt(NBT_DASH_CD) > 0) return true;
        player.getPersistentData().putInt(NBT_DASH_CD, DASH_COOLDOWN);

        Vec3 look = player.getLookAngle();
        player.setDeltaMovement(look.x * DASH_SPEED, 0.25, look.z * DASH_SPEED);
        player.hurtMarked = true;
        player.invulnerableTime = DASH_INVULN_TICKS;

        stack.hurtAndBreak(10, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        double frontX = player.getX() + look.x * DASH_RANGE;
        double frontZ = player.getZ() + look.z * DASH_RANGE;
        AABB area = new AABB(
                frontX - DASH_RANGE, player.getY() - 1.5, frontZ - DASH_RANGE,
                frontX + DASH_RANGE, player.getY() + 2.5, frontZ + DASH_RANGE);

        for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (target == player) continue;
            if (target.isAlliedTo(player)) continue;

            target.hurt(player.damageSources().playerAttack(player), DASH_DAMAGE);

            Vec3 dir = new Vec3(target.getX() - player.getX(), 0, target.getZ() - player.getZ());
            if (dir.lengthSqr() > 0.001) {
                dir = dir.normalize();
                target.push(dir.x * 1.2, 0.5, dir.z * 1.2);
                target.hurtMarked = true;
            }
        }

        Level level = player.level();
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 0.7F);
        if (level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.CLOUD,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    15, 0.5, 0.3, 0.5, 0.08);
        }

        player.getCooldowns().addCooldown(this, DASH_COOLDOWN);
        return true;
    }
}