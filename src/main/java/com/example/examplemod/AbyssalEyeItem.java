package com.example.examplemod;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AbyssalEyeItem extends Item {

    public AbyssalEyeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (HealthBonusHelper.getAbyssalUsed(player) >= HealthBonusHelper.ABYSSAL_MAX) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("这枚眼睛的力量已经对你无效了……"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide()) {
            HealthBonusHelper.useAbyssalEye(player);
            if (HealthBonusHelper.getAbyssalUsed(player) >= HealthBonusHelper.ABYSSAL_MAX) {
                HealthBonusHelper.grantAdvancement(player, "knowledgeable");
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.2F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}