package com.example.examplemod;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMaterials {

    public static final DeferredRegister<Item> MATERIALS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);

    public static final RegistryObject<Item> KINETIC_CORE = MATERIALS.register("kinetic_core",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_FRAME = MATERIALS.register("crystal_frame",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> HIGH_STRENGTH_WIRE = MATERIALS.register("high_strength_wire",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> DEEPSCULK_CRYSTAL = MATERIALS.register("deepsculk_crystal",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> DECAYING_SOUL = MATERIALS.register("decaying_soul",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> DRAGON_BREATH_EXTRACT = MATERIALS.register("dragon_breath_extract",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> HEAVY_ALLOY = MATERIALS.register("heavy_alloy",
            () -> new Item(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> APOCALYPSE_CHARGE = MATERIALS.register("apocalypse_charge",
            () -> new Item(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> ASHBLAZITE = MATERIALS.register("ashblazite",
            () -> new Item(new Item.Properties()));

    // ===== 末地闪光相关 =====
    public static final RegistryObject<Item> HEAVY_MOLTEN_BLANK = MATERIALS.register("heavy_molten_blank",
            () -> new Item(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> DRAGON_BREATH_SMITHING_TEMPLATE = MATERIALS.register("dragon_breath_smithing_template",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> END_FLASH_BLANK = MATERIALS.register("end_flash_blank",
            () -> new Item(new Item.Properties().fireResistant()));
}