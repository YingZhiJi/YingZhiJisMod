package com.potato.potatocharm;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = "yingzhiji_s_mod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class PotatoCharmRegistration {

    public static final ResourceLocation ID =
            new ResourceLocation("yingzhiji_s_mod", "potato_charm");

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, helper ->
                helper.register(ID, new PotatoCharmItem(
                        new Item.Properties().stacksTo(1)
                ))
        );
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PotatoCharmNetwork::register);
    }

    @SubscribeEvent
    public static void onBuildTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
            Item item = ForgeRegistries.ITEMS.getValue(ID);
            if (item != null) {
                event.accept(new ItemStack(item));
            }
        }
    }
}