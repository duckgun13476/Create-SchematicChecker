package com.Pink_Cats.createschematicchecker.core;

import net.minecraft.nbt.CompoundTag;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigValue.processArrayString;
import static com.Pink_Cats.createschematicchecker.core.NbtInterFace.StringToInt;

public class ConveyorInterface {


    public static int[] StringPickPos (String Pos){

        String[] apart = processArrayString(Pos);
        int [] PosRes = new int [3];
        PosRes[0] = StringToInt(apart[0]);
        PosRes[1] = StringToInt(apart[1]);
        PosRes[2] = StringToInt(apart[2]);
        return PosRes;
    }

}
