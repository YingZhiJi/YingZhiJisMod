package com.example.examplemod;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class EndFlashItem extends BreachBladeItem {

    public static final Tier END_FLASH_TIER = new Tier() {
        @Override public int getUses() { return 3600; }
        @Override public float getSpeed() { return 9.0F; }
        @Override public float getAttackDamageBonus() { return 0.0F; }
        @Override public int getLevel() { return 4; }
        @Override public int getEnchantmentValue() { return 15; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
    };

    private static final UUID REACH_UUID =
            UUID.fromString("a1b2c3d4-2222-3333-4444-555566667777");
    private static final String REACH_NAME = "end_flash_reach";

    public EndFlashItem(Properties properties) {
        super(END_FLASH_TIER, 17, -2.7F, properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> base = super.getDefaultAttributeModifiers(slot);
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(base);
            builder.put(ForgeMod.ENTITY_REACH.get(),
                    new AttributeModifier(REACH_UUID, REACH_NAME, 1.0D,
                            AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return base;
    }

    /**
     * 让物品自发光，跳过光照影响。
     * 使用 emissive 版本的 RenderType，配合 BLOCK_ATLAS（图集）。
     */
    public @NotNull Collection<RenderType> getRenderTypes(@NotNull ItemStack stack, boolean fabulous) {
        return List.of(RenderType.entityTranslucentEmissive(InventoryMenu.BLOCK_ATLAS));
    }
}