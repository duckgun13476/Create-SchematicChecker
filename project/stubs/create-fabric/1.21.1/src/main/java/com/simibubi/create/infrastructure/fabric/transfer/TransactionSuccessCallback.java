package com.simibubi.create.infrastructure.fabric.transfer;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public final class TransactionSuccessCallback {
    private TransactionSuccessCallback() {
    }

    public static void register(TransactionContext ctx, Runnable callback) {
    }
}
