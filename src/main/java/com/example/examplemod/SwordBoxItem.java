package com.example.examplemod;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;

public class SwordBoxItem extends Item {

    private static final Random RANDOM = new Random();

    public SwordBoxItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            ItemStack reward = rollReward();

            // 先尝试放进背包，放不下就丢在地上
            if (!player.getInventory().add(reward)) {
                player.drop(reward, false);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private ItemStack rollReward() {
        int roll = RANDOM.nextInt(7);
        return switch (roll) {
            case 0 -> createDamaged(ExampleMod.OLD_SABER.get(), 128);
            case 1 -> new ItemStack(Items.WOODEN_SWORD);
            case 2 -> new ItemStack(Items.STONE_SWORD);
            case 3 -> new ItemStack(Items.IRON_SWORD);
            case 4 -> createDamaged(Items.DIAMOND_SWORD, 128);
            case 5 -> new ItemStack(Items.GOLDEN_SWORD);
            default -> createNetheriteSword();
        };
    }

    /**
     * 创建一个"只剩 remaining 点耐久"的物品
     */
    private ItemStack createDamaged(Item item, int remaining) {
        ItemStack stack = new ItemStack(item);
        stack.setDamageValue(stack.getMaxDamage() - remaining);
        return stack;
    }

    /**
     * 锋利 10 的下界合金剑，1 点耐久，不可修复
     */
    private ItemStack createNetheriteSword() {
        ItemStack stack = new ItemStack(Items.NETHERITE_SWORD);
        stack.setDamageValue(stack.getMaxDamage() - 1);
        stack.enchant(Enchantments.SHARPNESS, 10);

        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("Unrepairable", true);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.sword_box.desc.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.sword_box.desc.2").withStyle(ChatFormatting.DARK_GRAY));
    }
}