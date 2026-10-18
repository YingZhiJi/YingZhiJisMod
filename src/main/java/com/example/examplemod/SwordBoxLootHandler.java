package com.example.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class SwordBoxLootHandler {

    private static final ResourceLocation BONUS_CHEST =
            new ResourceLocation("minecraft", "chests/spawn_bonus_chest");

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (!event.getName().equals(BONUS_CHEST)) return;

        LootPool pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(ExampleMod.SWORD_BOX.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                .name("yingzhiji_s_mod:sword_box")
                .build();

        event.getTable().addPool(pool);
    }
}