package com.example.examplemod;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;

public class CommanderTreasureBagItem extends Item {

    private static final Random RANDOM = new Random();

    public CommanderTreasureBagItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            // 随机一张唱片
            ItemStack disc = rollDisc();
            if (!player.getInventory().add(disc)) {
                player.drop(disc, false);
            }

            // 必定一把旧军刀
            ItemStack saber = new ItemStack(ExampleMod.OLD_SABER.get());
            if (!player.getInventory().add(saber)) {
                player.drop(saber, false);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private ItemStack rollDisc() {
        List<Item> discs = List.of(
                ExampleMod.MUSIC_DISC_UNIVERSAL_COLLAPSE.get(),
                ExampleMod.MUSIC_DISC_THE_FINALS.get(),
                ExampleMod.MUSIC_DISC_ARKNIGHTS_OST3.get(),
                ExampleMod.MUSIC_DISC_X_COOL.get(),
                ExampleMod.MUSIC_DISC_SOMNIOMANCER.get(),
                ExampleMod.MUSIC_DISC_BE_MY_STAR.get(),
                ExampleMod.MUSIC_DISC_BATTLEPLAN_ARCLIGHT.get()
        );
        return new ItemStack(discs.get(RANDOM.nextInt(discs.size())));
    }
}