package com.example.examplemod;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, ExampleMod.MODID);

    // ===== 熔融燃灰 =====
    public static final RegistryObject<FlowingFluid> MOLTEN_ASHBLAZITE =
            FLUIDS.register("molten_ashblazite",
                    () -> new ForgeFlowingFluid.Source(moltenAshblaziteProperties()));

    public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_ASHBLAZITE =
            FLUIDS.register("flowing_molten_ashblazite",
                    () -> new ForgeFlowingFluid.Flowing(moltenAshblaziteProperties()));

    // ===== 熔融龙息 =====
    public static final RegistryObject<FlowingFluid> MOLTEN_DRAGON_BREATH =
            FLUIDS.register("molten_dragon_breath",
                    () -> new ForgeFlowingFluid.Source(moltenDragonBreathProperties()));

    public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_DRAGON_BREATH =
            FLUIDS.register("flowing_molten_dragon_breath",
                    () -> new ForgeFlowingFluid.Flowing(moltenDragonBreathProperties()));

    // ===== Properties 工厂方法（延迟构造，避免前向引用） =====

    private static ForgeFlowingFluid.Properties moltenAshblaziteProperties() {
        return new ForgeFlowingFluid.Properties(
                ModFluidTypes.MOLTEN_ASHBLAZITE_TYPE,
                MOLTEN_ASHBLAZITE,
                FLOWING_MOLTEN_ASHBLAZITE)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2)
                .tickRate(20)
                .block(() -> ModBlocks.MOLTEN_ASHBLAZITE_BLOCK.get());
    }

    private static ForgeFlowingFluid.Properties moltenDragonBreathProperties() {
        return new ForgeFlowingFluid.Properties(
                ModFluidTypes.MOLTEN_DRAGON_BREATH_TYPE,
                MOLTEN_DRAGON_BREATH,
                FLOWING_MOLTEN_DRAGON_BREATH)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2)
                .tickRate(20)
                .block(() -> ModBlocks.MOLTEN_DRAGON_BREATH_BLOCK.get());
    }
}