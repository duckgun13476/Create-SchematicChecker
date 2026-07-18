package com.Pink_Cats.createschematicchecker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

/** Source-only Fabric target; retained as a safe placeholder until it is wired into Gradle. */
@Pseudo
@Mixin(targets = "com.simibubi.create.content.kinetics.chainConveyor.ServerboundChainConveyorRidingPacket", remap = false)
public abstract class ChainConveyorRidingPacketMixin {}
