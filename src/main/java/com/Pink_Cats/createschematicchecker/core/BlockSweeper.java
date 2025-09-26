package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.ban_block;
import static com.Pink_Cats.createschematicchecker.core.NbtInterFace.StrTag;
import static com.Pink_Cats.createschematicchecker.core.StrFunc.isInBanBlock;

public class BlockSweeper {

    public static String Clear = "minecraft:air";

    public static Map<String,Object> ClearBanBlock(CompoundTag Data,String type,int sequence) {
        Map<String, Object> result = new HashMap<>();
        boolean IsMatch = true;


        int totalCount = CountToClear(Data.toString(),ban_block);
        int total = totalCount;

        String Before = Data.toString();


        if (type.equals("block")){
            CompoundTag nbt = Data.getCompound("nbt");
            String id = StrTag(Objects.requireNonNull(nbt.get("id")));



            //Clear ID
            if (isInBanBlock(id)) {
                nbt.putString("id", Clear);
                totalCount -=1;
            }

            //
            if (id.equals("create:redstone_requester")){
                CompoundTag EncodedRequest = nbt.getCompound("EncodedRequest");
                Message.FM(EncodedRequest);
                //ListTag ordered_stacks = EncodedRequest.getList("ordered_stacks",10);
                CompoundTag ordered_stacks = EncodedRequest.getCompound("ordered_stacks");

                Message.FE(ordered_stacks);


            }



        }
        if (type.equals("palette")) {
            if (isInBanBlock(StrTag(Objects.requireNonNull(Data.get("Name"))))) {
                Data.putString("Name", Clear);
                totalCount -=1;
            }

        }



        if (totalCount !=0){
            IsMatch = false;
            Message.FM(type+"MisMatch: Block:["+sequence+"] All: "+total+" Left: "+totalCount);
            Message.FM("Before:");
            Message.FM(Before);
            Message.FM("After:");
            Message.FM(Data.toString());
        }
        result.put("IsMatch", IsMatch);
        result.put("Data", Data);
        return result;


    }





    // tool func

    public static CompoundTag BlockClearID(CompoundTag Data) {
        CompoundTag result = new CompoundTag();

        return Data;
    }


    public static int CountToClear(String data,String[] ban_block) {
        int totalCount = 0;
        for (String element : ban_block) {
            int count = countOccurrences(data, element);
            totalCount += count;
        }
        return totalCount;
    }


    public static int countOccurrences(String str, String subStr) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(subStr, idx)) != -1) {
            count++;
            idx += subStr.length();
        }
        return count;
    }




}
