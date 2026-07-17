package com.Pink_Cats.createschematicchecker.mixin;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.contraptions.fluids.FlowSource;
import com.simibubi.create.content.contraptions.fluids.OpenEndedPipe;
import com.simibubi.create.foundation.utility.BlockFace;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.fix_quark_lava_fluidlogged_duplication;
import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;

@Mixin(value = OpenEndedPipe.class, remap = false)
public class OpenEndedPipeMixin extends FlowSource {

    @Unique
    private static final BooleanProperty createSchematicChecker$LAVALOGGED = BooleanProperty.create("lavalogged");

    @Shadow
    private World world;

    @Shadow
    private BlockPos outputPos;

    @Shadow
    private BlockPos pos;

    public OpenEndedPipeMixin(BlockFace location) {
        super(location);
    }

    @Inject(method = "removeFluidFromSpace", at = @At("HEAD"), cancellable = true, remap = false)
    private void removeFluidFromSpace(boolean simulate, CallbackInfoReturnable<FluidStack> cir) {
        FluidStack empty = FluidStack.EMPTY;
        if (world == null) {
            cir.setReturnValue(empty);
            cir.cancel();
            return;
        }
        if (!world.isAreaLoaded(outputPos, 0)) {
            cir.setReturnValue(empty);
            cir.cancel();
            return;
        }

        BlockState state = world.getBlockState(outputPos);
        FluidState fluidState = state.getFluidState();
        boolean waterlog = state.hasProperty(WATERLOGGED);
        boolean lavalog = fix_quark_lava_fluidlogged_duplication && state.hasProperty(createSchematicChecker$LAVALOGGED);

        if (state.hasProperty(BlockStateProperties.HONEY_LEVEL)
                && state.get(BlockStateProperties.HONEY_LEVEL) >= 5) {
            if (!simulate) {
                world.setBlockState(outputPos, state.with(BlockStateProperties.HONEY_LEVEL, 0), 3);
            }
            cir.setReturnValue(new FluidStack(AllFluids.HONEY.get().getStillFluid(), 250));
            cir.cancel();
            return;
        }

        if (!waterlog && !state.getMaterial().isReplaceable()) {
            cir.setReturnValue(empty);
            cir.cancel();
            return;
        }

        if (fluidState.isEmpty() || !fluidState.isSource()) {
            cir.setReturnValue(empty);
            cir.cancel();
            return;
        }

        FluidStack stack = new FluidStack(fluidState.getFluid(), 1000);

        if (simulate) {
            cir.setReturnValue(stack);
            cir.cancel();
            return;
        }

        if (waterlog || lavalog) {
            if (waterlog) {
                world.setBlockState(outputPos, state.with(WATERLOGGED, false), 3);
                world.getPendingFluidTicks().scheduleTick(outputPos, Fluids.WATER, 1);
                state = world.getBlockState(outputPos);
            }
            if (lavalog) {
                world.setBlockState(outputPos, state.with(createSchematicChecker$LAVALOGGED, false), 3);
                world.getPendingFluidTicks().scheduleTick(outputPos, Fluids.LAVA, 1);
            }
            cir.setReturnValue(stack);
            cir.cancel();
            return;
        }

        world.setBlockState(outputPos, fluidState.getBlockState().with(FlowingFluidBlock.LEVEL, 14), 3);

        cir.setReturnValue(stack);
        cir.cancel();
    }

    /**
     * @author Pink_Cats
     * @reason Fix Pipe
     */
    @Overwrite
    public boolean isEndpoint() {
        return true;
    }
}
