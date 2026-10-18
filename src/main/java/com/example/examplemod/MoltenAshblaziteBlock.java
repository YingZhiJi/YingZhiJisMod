package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class MoltenAshblaziteBlock extends LiquidBlock {

    public MoltenAshblaziteBlock(Supplier<? extends FlowingFluid> fluid,
                                 Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide() && entity instanceof LivingEntity living) {
            if (!living.fireImmune()) {
                living.setSecondsOnFire(8);
                living.hurt(level.damageSources().lava(), 4.0F);
            }
        }
    }
}