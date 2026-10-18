package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class HealthBonusHelper {

    private static final UUID HEALTH_UUID =
            UUID.fromString("7e8d3f2a-9b1c-4d5e-8f0a-123456789abc");

    public static final String NBT_ABYSSAL = "yzj_abyssal_eye_used";
    public static final String NBT_DESTINY = "yzj_destiny_crystal_used";
    public static final String NBT_MAIDEN  = "yzj_maiden_honey_used";

    public static final int ABYSSAL_MAX = 5;
    public static final int ABYSSAL_BONUS = 4;
    public static final int DESTINY_BONUS = 20;
    public static final int MAIDEN_BONUS  = 40;

    public static int getAbyssalUsed(Player p) {
        return p.getPersistentData().getInt(NBT_ABYSSAL);
    }

    public static boolean hasDestiny(Player p) {
        return p.getPersistentData().getBoolean(NBT_DESTINY);
    }

    public static boolean hasMaiden(Player p) {
        return p.getPersistentData().getBoolean(NBT_MAIDEN);
    }

    public static void useAbyssalEye(Player p) {
        CompoundTag data = p.getPersistentData();
        int cur = data.getInt(NBT_ABYSSAL);
        if (cur >= ABYSSAL_MAX) return;
        data.putInt(NBT_ABYSSAL, cur + 1);
        refresh(p);
    }

    public static void useDestinyCrystal(Player p) {
        p.getPersistentData().putBoolean(NBT_DESTINY, true);
        refresh(p);
    }

    public static void useMaidenHoney(Player p) {
        p.getPersistentData().putBoolean(NBT_MAIDEN, true);
        refresh(p);
    }

    public static void grantAdvancement(Player player, String advancementId) {
        if (!(player instanceof net.minecraft.server.level.ServerPlayer sp)) return;
        var adv = sp.server.getAdvancements()
                .getAdvancement(new net.minecraft.resources.ResourceLocation(
                        ExampleMod.MODID, advancementId));
        if (adv == null) return;
        for (String criterion : adv.getCriteria().keySet()) {
            sp.getAdvancements().award(adv, criterion);
        }
    }

    /** 重算生命上限 modifier */
    public static void refresh(Player p) {
        AttributeInstance inst = p.getAttribute(Attributes.MAX_HEALTH);
        if (inst == null) return;

        inst.removeModifier(HEALTH_UUID);

        int bonus = getAbyssalUsed(p) * ABYSSAL_BONUS;
        if (hasDestiny(p)) bonus += DESTINY_BONUS;
        if (hasMaiden(p)) bonus += MAIDEN_BONUS;

        if (bonus > 0) {
            inst.addPermanentModifier(new AttributeModifier(
                    HEALTH_UUID, "yzj_health_bonus", bonus,
                    AttributeModifier.Operation.ADDITION));
        }
    }
}