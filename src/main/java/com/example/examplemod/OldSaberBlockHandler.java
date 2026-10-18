package com.example.examplemod;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class OldSaberBlockHandler {

    // ===== 攻击：取消原版流程（无横扫），手动施加主目标伤害 =====
    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack weapon = player.getMainHandItem();

        if (!weapon.is(ExampleMod.OLD_SABER.get())) return;

        // 取消原版攻击 → 没有横扫、没有击退、没有暴击
        event.setCanceled(true);

        // 手动对主目标造成伤害
        Entity target = event.getTarget();
        float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        target.hurt(player.damageSources().playerAttack(player), damage);

        // 手动扣耐久
        if (!player.getAbilities().instabuild) {
            weapon.hurtAndBreak(1, player,
                    (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
    }

    // ===== 格挡减伤 =====
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (event.getSource().is(DamageTypeTags.BYPASSES_SHIELD)
                || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)
                || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        if (isBlockingWithSaber(player)) {
            event.setAmount(event.getAmount() * 0.5F);
        }
    }

    private static boolean isBlockingWithSaber(Player player) {
        ItemStack useItem = player.getUseItem();
        if (useItem.is(ExampleMod.OLD_SABER.get())) return true;

        if (!player.isUsingItem()) return false;
        return player.getItemInHand(InteractionHand.MAIN_HAND).is(ExampleMod.OLD_SABER.get())
                || player.getItemInHand(InteractionHand.OFF_HAND).is(ExampleMod.OLD_SABER.get());
    }
}