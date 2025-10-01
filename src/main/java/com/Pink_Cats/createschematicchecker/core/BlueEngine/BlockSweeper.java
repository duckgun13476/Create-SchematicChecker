package com.Pink_Cats.createschematicchecker.core.BlueEngine;

import com.Pink_Cats.createschematicchecker.lang.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.ChainEngine.MagicChain.MagicChainClear;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.isInBanBlock;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.TagFunc.BlockGetId;

public class BlockSweeper {

    public static String Clear = "minecraft:air";

    public static Map<String,Object> ClearBanBlock(CompoundTag Data,String type,int sequence,ListTag PaletteData) {
        try {
            Map<String, Object> result = new HashMap<>();
            boolean IsMatch = true;


            int totalCount = CountToClear(Data.toString(), ban_block);
            int total = totalCount;

            String Before = Data.toString();
            String block_information = "";
            Map<String, Object> ChainResult;


            if (type.equals("block")) {
                CompoundTag nbt = Data.getCompound("nbt");
                String id = StrTag(Objects.requireNonNull(nbt.get("id")));
                block_information = id;


                //Clear ID
                if (isInBanBlock(id)) {
                    nbt.putString("id", Clear);
                    totalCount -= 1;
                }


                //ID InterFace


                for (String[] idLogic : ID_match_rule) {
                    try {
                        String idKey = idLogic[0];
                        if (id.equals(idKey)) {
                            for (int i = 1; i < idLogic.length; i++) {
                                String logicValue = idLogic[1];
                                ChainResult = MagicChainClear(Data, logicValue, totalCount, "id");
                                Data = S_tag(ChainResult.get("data"));
                                totalCount = S_int(ChainResult.get("find_count"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }



            }

            if (type.contains("rule")) {
                if (type.contains("block")) {
                    String id = BlockGetId(Data, PaletteData);


                    //Cheat InterFace

                    if (id.equals("create:lectern_controller")) {
                        ChainResult = MagicChainClear(Data,
                                "nbt.Controller.id", totalCount,
                                "operate" +
                                        ".clear$tag" +
                                        ".replace$id$create:linked_controller");
                        Data = S_tag(ChainResult.get("data"));
                        totalCount = S_int(ChainResult.get("find_count"));
                    }

                    if (id.equals("create:clipboard")) {
                        ChainResult = MagicChainClear(Data,
                                "nbt.Item.id", totalCount,
                                "operate" +
                                        ".clear$tag" +
                                        ".replace$id$create:clipboard");
                        Data = S_tag(ChainResult.get("data"));
                        totalCount = S_int(ChainResult.get("find_count"));
                    }


                    if (id.equals("create:valve_handle")) {
                        ChainResult = MagicChainClear(Data,
                                "nbt.ScrollValue", totalCount,
                                "operate" +
                                        ".limit$ScrollValue$-180$180");
                        Data = S_tag(ChainResult.get("data"));
                        totalCount = S_int(ChainResult.get("find_count"));
                    }


                    for (String[] idLogic : Operate_match_rule) {
                        String idKey = idLogic[0];
                        String chain = idLogic[1];
                        String surgery = idLogic[2];
                        try {
                            if (id.equals(idKey)) {
                                for (int i = 1; i < idLogic.length; i++) {
                                    ChainResult = MagicChainClear(Data, chain, totalCount, surgery);
                                    Data = S_tag(ChainResult.get("data"));
                                    totalCount = S_int(ChainResult.get("find_count"));
                                }
                            }
                        } catch (Exception e) {
                            Message.FE("发现匹配规则的方块，但是没有在方块中找到处理的变量");
                            Message.FE("方块["+(sequence)+1+"]  规则 ["+idKey+"|"+chain+"|"+surgery+"]");
                            //e.printStackTrace();
                        }

                    }


                } else if (type.contains("palette")) {
                    String id = StrTag(Objects.requireNonNull(Data.get("Name")));

                    if (id.equals("supplementaries:urn")) {
                        ChainResult = MagicChainClear(Data,
                                "Properties.treasure", totalCount,
                                "operate" +
                                        ".replace$treasure$false");
                        Data = S_tag(ChainResult.get("data"));
                        totalCount = S_int(ChainResult.get("find_count"));
                    }

                    if (id.equals("create_connected:kinetic_battery")) {
                        ChainResult = MagicChainClear(Data,
                                "Properties.level", totalCount,
                                "operate" +
                                        ".replace$level$0");
                        Data = S_tag(ChainResult.get("data"));
                        totalCount = S_int(ChainResult.get("find_count"));
                    }


                } else {
                    String entityID = BlockGetId(Data, PaletteData);
                    Message.FE("entityID: " + entityID);
                    Message.FE(Data);
                }


            }


            if (type.equals("palette")) {
                String id = null;
                if (Data != null) {
                    id = NbtInterFace.StrTag(Objects.requireNonNull(Data.get("Name")));
                }
                block_information = id;
                if (isInBanBlock(id)) {
                    Data.putString("Name", Clear);
                    totalCount -= 1;
                }

            }

            if (!type.contains("rule")){
                if (totalCount != 0) {
                    IsMatch = false;
                    Message.FM("[" + type + "][" + block_information + "]MisMatch: Block:[" + (sequence + 1) + "] All: " + total + " Left: " + totalCount);
                    Message.FM("Before:");
                    Message.FM(Before);
                    Message.FM("After:");
                    Message.FM(Data.toString());
                }
            }


            result.put("IsMatch", IsMatch);
            result.put("Data", Data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Map.of();
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
