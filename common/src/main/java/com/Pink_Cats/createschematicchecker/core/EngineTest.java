package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.lang.mes;

public class EngineTest {

    public static boolean IsBlockAttach(int x, int y, int z, int x2, int y2, int z2) {
        if (Math.abs(z - z2) == 1) {
            if (x == x2 && y == y2) {
                return true;
            }
        }

        if (Math.abs(x - x2) == 1) {
            if (z == z2 && y == y2) {
                return true;
            }
        }

        if (Math.abs(y - y2) == 1) {
            return x == x2 && z == z2;
        }

        return false;
    }

    public static void main(String[] args) {
        mes.warn(IsBlockAttach(0, 0, 0, 0, 0, 1));
    }
}
