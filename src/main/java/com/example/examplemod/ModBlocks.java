package com.example.examplemod;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.level.block.LiquidBlock;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MODID);

    // 燃灰矿石（Y > 0）：硬度/抗性参照青金石矿，掉落经验 2-5
    public static final RegistryObject<Block> ASHBLAZITE_ORE =
            BLOCKS.register("ashblazite_ore",
                    () -> new DropExperienceBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.STONE)
                                    .instrument(NoteBlockInstrument.BASEDRUM)
                                    .requiresCorrectToolForDrops()
                                    .strength(3.0F, 3.0F),
                            UniformInt.of(2, 5)));

    // 深层燃灰矿石（Y < 0）
    public static final RegistryObject<Block> ASHBLAZITE_DEEPSLATE_ORE =
            BLOCKS.register("ashblazite_deepslate_ore",
                    () -> new DropExperienceBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.DEEPSLATE)
                                    .instrument(NoteBlockInstrument.BASEDRUM)
                                    .requiresCorrectToolForDrops()
                                    .strength(4.5F, 3.0F)
                                    .sound(SoundType.DEEPSLATE),
                            UniformInt.of(2, 5)));

    // 燃灰块：纯建筑方块
    public static final RegistryObject<Block> ASHBLAZITE_BLOCK =
            BLOCKS.register("ashblazite_block",
                    () -> new Block(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.COLOR_GRAY)
                                    .instrument(NoteBlockInstrument.BASEDRUM)
                                    .requiresCorrectToolForDrops()
                                    .strength(5.0F, 6.0F)));

    // 结晶器
    public static final RegistryObject<Block> CRYSTALLIZER =
            BLOCKS.register("crystallizer", CrystallizerBlock::new);

    // 熔融燃灰流体方块
    public static final RegistryObject<LiquidBlock> MOLTEN_ASHBLAZITE_BLOCK =
            BLOCKS.register("molten_ashblazite_block",
                    () -> new MoltenAshblaziteBlock(
                            ModFluids.MOLTEN_ASHBLAZITE,
                            BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.LAVA)
                                    .lightLevel(s -> 10)
                                    .noLootTable()));

    // 熔融龙息流体方块
    public static final RegistryObject<LiquidBlock> MOLTEN_DRAGON_BREATH_BLOCK =
            BLOCKS.register("molten_dragon_breath_block",
                    () -> new MoltenAshblaziteBlock(
                            ModFluids.MOLTEN_DRAGON_BREATH,
                            BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.LAVA)
                                    .lightLevel(s -> 12)
                                    .noLootTable()));

}