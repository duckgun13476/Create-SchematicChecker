package com.Pink_Cats.createschematicchecker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "com.simibubi.create.content.kinetics.chainConveyor.ServerboundChainConveyorRidingPacket", remap = false)
public abstract class ChainConveyorRidingPacketMixin {}
