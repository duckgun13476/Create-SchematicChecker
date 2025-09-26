package com.Pink_Cats.createschematicchecker.core;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigValue.processArrayString;

public class MagicChain {


    public static Map<String,Object> MagicChainClear(CompoundTag data,String chain,int find_count){
        HashMap<String,Object> magicChain = new HashMap<>();
        String[] BaseChain = processArrayString(chain);







        magicChain.put("data",data);
        magicChain.put("find_count",find_count);
        return magicChain;
    }


}
