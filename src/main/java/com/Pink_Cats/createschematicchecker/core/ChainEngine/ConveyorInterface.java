package com.Pink_Cats.createschematicchecker.core.ChainEngine;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigValue.processArrayString;
import static com.Pink_Cats.createschematicchecker.core.attach.Math.StringToFloat;
import static com.Pink_Cats.createschematicchecker.core.attach.Math.StringToInt;

public class ConveyorInterface {


    public static int[] StringPickPos (String Pos){

        String[] apart = processArrayString(Pos);
        int [] PosRes = new int [3];
        PosRes[0] = StringToInt(apart[0]);
        PosRes[1] = StringToInt(apart[1]);
        PosRes[2] = StringToInt(apart[2]);
        return PosRes;
    }

    public static float[] StringPickHalfPos (String Pos){

        String[] apart = processArrayString(Pos);
        float [] PosRes = new float [3];
        PosRes[0] = StringToFloat(apart[0]);
        PosRes[1] = StringToFloat(apart[1]);
        PosRes[2] = StringToFloat(apart[2]);
        return PosRes;
    }

}
