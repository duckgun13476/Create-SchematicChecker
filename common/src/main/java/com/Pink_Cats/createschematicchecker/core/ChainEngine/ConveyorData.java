package com.Pink_Cats.createschematicchecker.core.ChainEngine;

import java.util.List;

public class ConveyorData {

    private final int[] selfPos;
    private final List<int[]> connectionList;

    public ConveyorData(int[] selfPos, List<int[]> connectionList) {
        this.selfPos = selfPos;
        this.connectionList = connectionList;
    }

    public int[] getSelfPos() {
        return selfPos;
    }

    public List<int[]> getConnectionList() {
        return connectionList;
    }
}
