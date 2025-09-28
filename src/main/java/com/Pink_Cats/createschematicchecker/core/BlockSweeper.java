package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.ban_block;
import static com.Pink_Cats.createschematicchecker.core.MagicChain.MagicChainClear;
import static com.Pink_Cats.createschematicchecker.core.NbtInterFace.*;
import static com.Pink_Cats.createschematicchecker.core.StrFunc.isInBanBlock;

public class BlockSweeper {

    public static String Clear = "minecraft:air";

    public static Map<String,Object> ClearBanBlock(CompoundTag Data,String type,int sequence) {
        Map<String, Object> result = new HashMap<>();
        boolean IsMatch = true;


        int totalCount = CountToClear(Data.toString(),ban_block);
        int total = totalCount;

        String Before = Data.toString();
        String block_information = "";
        Map<String,Object> ChainResult;


        if (type.equals("block")){
            CompoundTag nbt = Data.getCompound("nbt");
            String id = StrTag(Objects.requireNonNull(nbt.get("id")));
            block_information = id;


            //Clear ID
            if (isInBanBlock(id)) {
                nbt.putString("id", Clear);
                totalCount -=1;
            }



            //ID InterFace
            String[][] idLogicArray = {

                    //Create 6.0.*
                    {"create:stock_ticker"       , "nbt.$Categories.id"},
                    {"create:redstone_requester" , "nbt.EncodedRequest.ordered_stacks.$entries.item_stack.id"},

                    //Create 0.5.1
                    {"create:redstone_link"      , "nbt.FrequencyFirst.id"},
                    {"create:redstone_link"      , "nbt.FrequencyLast.id"},
                    {"create:depot"              , "nbt.HeldItem.Item.id"},
                    {"create:weighted_ejector"   , "nbt.HeldItem.Item.id"},
                    {"create:chute"              , "nbt.Item.id"},
                    {"create:smart_chute"        , "nbt.Item.id"},
                    {"create:smart_chute"        , "nbt.Filter.id"},
                    {"create:saw"                , "nbt.Filter.id"},
                    {"create:deployer"           , "nbt.Filter.id"},
                    {"create:deployer"           , "nbt.$Inventory.id"},
                    {"create:funnel"             , "nbt.Filter.id"},
                    {"create:placard"            , "nbt.Item.id"},
                    {"create:content_observer"   , "nbt.Filter.id"},
                    {"create:belt"               , "nbt.Inventory.$Items.Item.id"},
                    {"create:basin"              , "nbt.Filter.id"},
                    {"create:basin"              , "nbt.InputItems.$Items.id"},
                    {"create:smart_fluid_pipe"   , "nbt.Filter.id"},
                    {"create:mechanical_crafter" , "nbt.Inventory.$Items.id"},
                    {"create:toolbox"            , "nbt.Inventory.$Compartments.id"},
                    {"create:toolbox"            , "nbt.Inventory.$Items.id"},
                    {"create:stockpile_switch"   , "nbt.Filter.id"},
                    {"create:brass_tunnel"       , "nbt.Filter.id"},
                    {"create:brass_tunnel"       , "nbt.$Filters.Filter.id"},
                    {"create:brass_tunnel"       , "nbt.StackToDistribute.id"},
                    {"create:mechanical_roller"  , "nbt.Filter.id"},



            };

            // 遍历字符串数组，检查是否等于字符串 A，如果等于就执行相应的逻辑 B
            for (String[] idLogic : idLogicArray) {
                String idKey = idLogic[0];
                if (id.equals(idKey)) {
                    for (int i = 1; i < idLogic.length; i++) {
                        String logicValue = idLogic[1];
                        ChainResult = MagicChainClear(Data, logicValue, totalCount,"id");
                        Data = S_tag(ChainResult.get("data"));
                        totalCount = S_int(ChainResult.get("find_count"));
                    }
                }
            }
        }

        if (type.contains("rule")){
            if (type.contains("block")){
                CompoundTag nbt = Data.getCompound("nbt");
                String id = StrTag(Objects.requireNonNull(nbt.get("id")));


                //Cheat InterFace

                if (id.equals("create:lectern_controller")){
                    ChainResult = MagicChainClear(Data,
                            "nbt.Controller.id",totalCount ,
                            "operate" +
                                    ".clear$tag" +
                                    ".replace$id$create:linked_controller");
                    Data = S_tag(ChainResult.get("data"));
                    totalCount = S_int(ChainResult.get("find_count"));
                }

                if (id.equals("create:clipboard")){
                    ChainResult = MagicChainClear(Data,
                            "nbt.Item.id",totalCount ,
                            "operate" +
                                    ".clear$tag" +
                                    ".replace$id$create:clipboard");
                    Data = S_tag(ChainResult.get("data"));
                    totalCount = S_int(ChainResult.get("find_count"));
                }


                if (id.equals("create:valve_handle")){
                    ChainResult = MagicChainClear(Data,
                            "nbt.ScrollValue",totalCount ,
                            "operate" +
                                    ".limit$ScrollValue$-180$180");
                    Data = S_tag(ChainResult.get("data"));
                    totalCount = S_int(ChainResult.get("find_count"));
                }


                String[][] SurgeryLogicArray = {
                        {"create:belt", "nbt.Length", "operate.limit$Length$0$30"},
                        {"create:belt", "nbt.Index", "operate.limit$Index$0$29"},
                        // Add more entries as needed
                };

                for (String[] idLogic : SurgeryLogicArray) {
                    String idKey = idLogic[0];
                    String chain = idLogic[1];
                    String surgery = idLogic[2];
                    if (id.equals(idKey)) {
                        for (int i = 1; i < idLogic.length; i++) {
                            ChainResult = MagicChainClear(Data, chain, totalCount,surgery);
                            Data = S_tag(ChainResult.get("data"));
                            totalCount = S_int(ChainResult.get("find_count"));
                        }
                    }
                }





            }
            else {
                String id = StrTag(Objects.requireNonNull(Data.get("Name")));






            }




        }



        if (type.equals("palette")) {
            String id = StrTag(Objects.requireNonNull(Data.get("Name")));
            block_information = id;
            if (isInBanBlock(id)) {
                Data.putString("Name", Clear);
                totalCount -=1;
            }

        }



        if (totalCount !=0){
            IsMatch = false;
            Message.FM("["+type+"]["+block_information+"]MisMatch: Block:["+(sequence+1)+"] All: "+total+" Left: "+totalCount);
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


    public static String Clear$(String input) {
        return input.replace("$", "");
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
