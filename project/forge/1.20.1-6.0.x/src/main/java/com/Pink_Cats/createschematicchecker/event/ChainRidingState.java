package com.Pink_Cats.createschematicchecker.event;

import net.minecraft.world.phys.Vec3;

/** Server-side chain rider heartbeat state kept outside the mixin package. */
public record ChainRidingState(Vec3 position, long tick) {
}
