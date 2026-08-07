package com.Pink_Cats.createschematicchecker.event;

import net.minecraft.world.phys.Vec3;

/**
 * Server-side chain rider heartbeat state.  This must stay outside the mixin
 * package: the field is merged into Create's packet class at runtime.
 */
public record ChainRidingState(Vec3 position, long tick) {
}
