package com.example.examplemod;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class DestinyCrystalItem extends Item {

    private static final int CHARGE_TICKS = 20; // 1 秒

    public DestinyCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return CHARGE_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (HealthBonusHelper.getAbyssalUsed(player) < HealthBonusHelper.ABYSSAL_MAX) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("你还需要更多渊识之眼的洗礼……"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (HealthBonusHelper.hasDestiny(player)) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("你的命运已经注定过了。"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && !level.isClientSide()) {
            HealthBonusHelper.useDestinyCrystal(player);
            HealthBonusHelper.grantAdvancement(player, "sovereign");
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return stack;
    }
}