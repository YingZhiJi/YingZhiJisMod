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

public class MaidenHoneyPotionItem extends Item {

    private static final int DRINK_TICKS = 32;

    public MaidenHoneyPotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return DRINK_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (HealthBonusHelper.getAbyssalUsed(player) < HealthBonusHelper.ABYSSAL_MAX) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("这瓶蜜药对你来说还太浓烈了……"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        if (!HealthBonusHelper.hasDestiny(player)) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("你的命运尚未注定，无法承受这份甜美。"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        if (HealthBonusHelper.hasMaiden(player)) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("你已经喝过了，少女的暗语不会再回应你。"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && !level.isClientSide()) {
            HealthBonusHelper.useMaidenHoney(player);
            HealthBonusHelper.grantAdvancement(player, "taste_good");
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return stack;
    }
}