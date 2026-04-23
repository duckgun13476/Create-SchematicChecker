package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public interface FlowingFluidAccessor {
    FluidState create$getNewLiquid(Level world, BlockPos pos, BlockState state);
}
