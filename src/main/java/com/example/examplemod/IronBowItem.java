package com.example.examplemod;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class IronBowItem extends BowItem {

    public IronBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(Level level, Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (findIronArrow(player).isEmpty()) {
            return net.minecraft.world.InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return net.minecraft.world.InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        int useTime = this.getUseDuration(stack) - timeLeft;
        float power = BowItem.getPowerForTime(useTime);

        if (power < 0.1F) return;

        ItemStack ammo = findIronArrow(player);
        if (ammo.isEmpty()) return;

        // 根据箭种创建对应实体
        AbstractArrow arrow = createArrow(level, player, ammo, power);
        arrow.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 6.0F, 1.0F);
        level.addFreshEntity(arrow);

        if (!player.getAbilities().instabuild) {
            ammo.shrink(1);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));

        player.awardStat(Stats.ITEM_USED.get(this));
    }

    /**
     * 根据箭种生成对应的箭实体
     */
    private AbstractArrow createArrow(Level level, Player player, ItemStack ammo, float power) {
        if (ammo.is(ExampleMod.IRON_SPECTRAL_ARROW.get())) {
            IronSpectralArrow arrow = new IronSpectralArrow(level, player);
            arrow.power = power;
            return arrow;
        }
        if (ammo.is(ExampleMod.IRON_BURNING_ARROW.get())) {
            return new IronBurningArrow(level, player);
        }
        if (ammo.is(ExampleMod.IRON_POISON_ARROW.get())) {
            return new IronPoisonArrow(level, player);
        }
        if (ammo.is(ExampleMod.IRON_COOLING_ARROW.get())) {
            IronCoolingArrow arrow = new IronCoolingArrow(level, player);
            arrow.power = power;
            return arrow;
        }
        if (ammo.is(ExampleMod.IRON_FRAGMENT_ARROW.get())) {
            IronFragmentArrow arrow = new IronFragmentArrow(level, player);
            arrow.power = power;
            return arrow;
        }
        if (ammo.is(ExampleMod.DENSE_ARROW.get())) {
            return new DenseArrow(level, player);
        }
        if (ammo.is(ExampleMod.DENSE_PIERCING_ARROW.get())) {
            return new DensePiercingArrow(level, player);
        }
        if (ammo.is(ExampleMod.DENSE_OVERPRESSURE_ARROW.get())) {
            DenseOverpressureArrow arrow = new DenseOverpressureArrow(level, player);
            arrow.power = power;
            arrow.setBaseDamage(4.0F * power);
            return arrow;
        }
        if (ammo.is(ExampleMod.DENSE_DECAYING_ARROW.get())) {
            return new DenseDecayingArrow(level, player);
        }
        if (ammo.is(ExampleMod.DENSE_DEEPSCULK_ARROW.get())) {
            DenseDeepSculkArrow arrow = new DenseDeepSculkArrow(level, player);
            arrow.power = power;
            return arrow;
        }
        if (ammo.is(ExampleMod.DENSE_APOCALYPSE_ARROW.get())) {
            DenseApocalypseArrow apocalypseArrow = new DenseApocalypseArrow(level, player);
            apocalypseArrow.power = power;
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.APOCALYPSE_LAUNCH.get(),
                    net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 1.0F);
            return apocalypseArrow;
        }
        return new IronArrow(level, player);
    }

    /**
     * 在玩家背包里找可用的箭，找不到返回空
     */
    private ItemStack findIronArrow(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isValidAmmo(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * 判断这个物品能不能被这把弓当箭用
     */
    private boolean isValidAmmo(ItemStack stack) {
        return stack.is(ExampleMod.IRON_ARROW.get())
                || stack.is(ExampleMod.IRON_SPECTRAL_ARROW.get())
                || stack.is(ExampleMod.IRON_BURNING_ARROW.get())
                || stack.is(ExampleMod.IRON_POISON_ARROW.get())
                || stack.is(ExampleMod.IRON_COOLING_ARROW.get())
                || stack.is(ExampleMod.IRON_FRAGMENT_ARROW.get())
                || stack.is(ExampleMod.DENSE_ARROW.get())
                || stack.is(ExampleMod.DENSE_PIERCING_ARROW.get())
                || stack.is(ExampleMod.DENSE_OVERPRESSURE_ARROW.get())
                || stack.is(ExampleMod.DENSE_DECAYING_ARROW.get())
                || stack.is(ExampleMod.DENSE_DEEPSCULK_ARROW.get())
                || stack.is(ExampleMod.DENSE_APOCALYPSE_ARROW.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.iron_bow.desc.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.iron_bow.desc.2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.yingzhiji_s_mod.iron_bow.desc.3").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.world.item.enchantment.Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }
}