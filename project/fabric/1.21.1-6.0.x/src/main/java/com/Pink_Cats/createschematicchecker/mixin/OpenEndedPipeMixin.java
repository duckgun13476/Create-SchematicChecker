package com.Pink_Cats.createschematicchecker.mixin;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import com.simibubi.create.content.fluids.pipes.VanillaFluidTargets;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.mixin.accessor.FlowingFluidAccessor;
import com.simibubi.create.infrastructure.fabric.transfer.TransactionSuccessCallback;
import com.simibubi.create.infrastructure.fabric.transfer.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.fix_quark_lava_fluidlogged_duplication;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

@Mixin(value = OpenEndedPipe.class, remap = false)
public class OpenEndedPipeMixin {

    @Unique
    private static final BooleanProperty createSchematicChecker$LAVALOGGED = BooleanProperty.create("lavalogged");

    @Shadow
    private Level world;

    @Shadow
    private BlockPos outputPos;

    @Shadow
    private BlockPos pos;

    @Unique
    private static final String createSchematicChecker$UPDATE_SNAPSHOTS = "updateSnapshots";

    @Inject(method = "removeFluidFromSpace", at = @At("HEAD"), cancellable = true, remap = false)
    private void removeFluidFromSpace(TransactionContext ctx, CallbackInfoReturnable<FluidStack> cir) {
        FluidStack empty = FluidStack.EMPTY;
        if (world == null) {
            cir.setReturnValue(empty);
            cir.cancel();
            return;
        }
        if (!world.isLoaded(outputPos)) {
            cir.setReturnValue(empty);
            cir.cancel();
            return;
        }

        BlockState state = world.getBlockState(outputPos);
        FluidState fluidState = state.getFluidState();
        boolean waterlog = state.hasProperty(WATERLOGGED);
        boolean lavalog = fix_quark_lava_fluidlogged_duplication && state.hasProperty(createSchematicChecker$LAVALOGGED);

        FluidStack drainBlock = VanillaFluidTargets.drainBlock(world, outputPos, state, ctx);
        if (!drainBlock.isEmpty()) {
            if (state.hasProperty(BlockStateProperties.LEVEL_HONEY)
                    && AllFluids.HONEY.is(drainBlock.getFluid())) {
                TransactionSuccessCallback.register(ctx, () -> AdvancementBehaviour.tryAward(world, pos, AllAdvancements.HONEY_DRAIN));
            }
            cir.setReturnValue(drainBlock);
            cir.cancel();
            return;
        }

        if (!waterlog && !lavalog && !state.canBeReplaced()) {
            cir.setReturnValue(empty);
            cir.cancel();
            return;
        }
        if (fluidState.isEmpty() || !fluidState.isSource()) {
            cir.setReturnValue(empty);
            cir.cancel();
            return;
        }

        FluidStack stack = new FluidStack(fluidState.getType(), FluidConstants.BUCKET);

        if (FluidHelper.isWater(stack.getFluid())) {
            AdvancementBehaviour.tryAward(world, pos, AllAdvancements.WATER_SUPPLY);
        }

        createSchematicChecker$invokeUpdateSnapshots(world, ctx);

        if (waterlog || lavalog) {
            if (waterlog) {
                world.setBlock(outputPos, state.setValue(WATERLOGGED, false), 3);
                TransactionSuccessCallback.register(ctx, () -> world.scheduleTick(outputPos, Fluids.WATER, 1));
                state = world.getBlockState(outputPos);
            }
            if (lavalog) {
                world.setBlock(outputPos, state.setValue(createSchematicChecker$LAVALOGGED, false), 3);
                TransactionSuccessCallback.register(ctx, () -> world.scheduleTick(outputPos, Fluids.LAVA, 1));
            }
            cir.setReturnValue(stack);
            cir.cancel();
            return;
        }

        var newState = fluidState.createLegacyBlock()
                .setValue(LiquidBlock.LEVEL, 14);

        var newFluidState = newState.getFluidState();

        if (newFluidState.getType() instanceof FlowingFluidAccessor flowing) {
            var potentiallyFilled = flowing.create$getNewLiquid(world, outputPos, newState);

            if (potentiallyFilled.equals(fluidState)) {
                cir.setReturnValue(stack);
                cir.cancel();
                return;
            }
        }

        world.setBlock(outputPos, newState, 3);

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

    @Unique
    private static void createSchematicChecker$invokeUpdateSnapshots(Level world, TransactionContext ctx) {
        if (world == null || ctx == null) {
            return;
        }

        try {
            Method method = world.getClass().getMethod(createSchematicChecker$UPDATE_SNAPSHOTS, TransactionContext.class);
            method.invoke(world, ctx);
        } catch (ReflectiveOperationException ignored) {
        }
    }
}
