package com.example.examplemod;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ExampleMod.MODID);

    public static final RegistryObject<BlockEntityType<CrystallizerBlockEntity>> CRYSTALLIZER =
            BLOCK_ENTITIES.register("crystallizer",
                    () -> BlockEntityType.Builder.of(CrystallizerBlockEntity::new,
                            ModBlocks.CRYSTALLIZER.get()).build(null));
}