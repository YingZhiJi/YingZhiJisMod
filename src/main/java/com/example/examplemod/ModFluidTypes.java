package com.example.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class ModFluidTypes {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ExampleMod.MODID);

    // ===== 熔融燃灰 =====
    public static final RegistryObject<FluidType> MOLTEN_ASHBLAZITE_TYPE =
            FLUID_TYPES.register("molten_ashblazite",
                    () -> new FluidType(FluidType.Properties.create()
                            .descriptionId("fluid.yingzhiji_s_mod.molten_ashblazite")
                            .density(3000)
                            .viscosity(6000)
                            .temperature(1300)
                            .lightLevel(10)
                            .canSwim(false)
                            .canDrown(false)
                            .canPushEntity(false)
                            .canConvertToSource(false)
                            .motionScale(0.007)
                            .supportsBoating(false)) {
                        @Override
                        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                            consumer.accept(new IClientFluidTypeExtensions() {
                                @Override
                                public ResourceLocation getStillTexture() {
                                    return new ResourceLocation(ExampleMod.MODID, "block/molten_ashblazite_still");
                                }
                                @Override
                                public ResourceLocation getFlowingTexture() {
                                    return new ResourceLocation(ExampleMod.MODID, "block/molten_ashblazite_flow");
                                }
                                @Override
                                public int getTintColor() {
                                    return 0xFFFF8C00;
                                }
                            });
                        }
                    });

    // ===== 熔融龙息 =====
    public static final RegistryObject<FluidType> MOLTEN_DRAGON_BREATH_TYPE =
            FLUID_TYPES.register("molten_dragon_breath",
                    () -> new FluidType(FluidType.Properties.create()
                            .descriptionId("fluid.yingzhiji_s_mod.molten_dragon_breath")
                            .density(1500)
                            .viscosity(2000)
                            .temperature(1500)
                            .lightLevel(12)
                            .canSwim(false)
                            .canDrown(false)
                            .canPushEntity(false)
                            .canConvertToSource(false)
                            .motionScale(0.007)
                            .supportsBoating(false)) {
                        @Override
                        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                            consumer.accept(new IClientFluidTypeExtensions() {
                                @Override
                                public ResourceLocation getStillTexture() {
                                    return new ResourceLocation(ExampleMod.MODID, "block/molten_dragon_breath_still");
                                }
                                @Override
                                public ResourceLocation getFlowingTexture() {
                                    return new ResourceLocation(ExampleMod.MODID, "block/molten_dragon_breath_flow");
                                }
                                @Override
                                public int getTintColor() {
                                    return 0xFFAA00FF;
                                }
                            });
                        }
                    });
}