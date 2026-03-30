package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.lang.mes;
import net.minecraft.core.BlockPos;

public class EngineTest {


    public static boolean IsBlockAttach(BlockPos pos,BlockPos pos2) {
        boolean Attach = false;
        if (    Math.abs(pos.getZ()-pos2.getZ()) == 1   )
            if  (pos.getX()==pos2.getX() && pos.getY() == pos2.getY())
                return true;

        if (    Math.abs(pos.getX()-pos2.getX()) == 1   )
            if  (pos.getZ()==pos2.getZ() && pos.getY() == pos2.getY())
                return true;

        if (    Math.abs(pos.getY()-pos2.getY()) == 1   )
            return pos.getX() == pos2.getX() && pos.getZ() == pos2.getZ();


        return false;
    }

    public static void main(String[] args) {

        BlockPos Pos = new BlockPos(0, 0, 0);
        BlockPos Pos2 = new BlockPos(0, 0, 1);


        mes.warn(IsBlockAttach(Pos,Pos2));
    }




}
