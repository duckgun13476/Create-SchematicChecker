package com.Pink_Cats.createschematicchecker.core.BlueEngine;

import com.Pink_Cats.createschematicchecker.lang.Message;
import com.pinkcats.torque.layer.net.minecraft.nbt.CompoundTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.ListTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.Nbt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.isInBanBlock;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.TagFunc.BlockGetId;
import static com.Pink_Cats.createschematicchecker.core.ChainEngine.MagicChain.MagicChainClear;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

public class BlockSweeper {

    public static String Clear = "minecraft:air";

    public static Map<String, Object> ClearBanBlock(CompoundTag Data, String type, int sequence, ListTag PaletteData) {
        try {
            Map<String, Object> result = new HashMap<>();
            boolean IsNotMatch = false;

            int totalCount = CountToClear(Data.toString(), ban_block);
            int total = totalCount;

            String Before = Data.toString();
            String block_information = "";
            Map<String, Object> ChainResult;

            if (type.equals("block")) {
                CompoundTag nbt = Data.getCompound("nbt");
                String id = StrTag(Objects.requireNonNull(nbt.get("id")));
                block_information = id;

                if (isInBanBlock(id)) {
                    nbt.putString("id", Clear);
                    totalCount -= 1;
                }

                for (String[] idLogic : ID_match_rule) {
                    try {
                        String idKey = idLogic[0];
                        if (id.equals(idKey)) {
                            for (int i = 1; i < idLogic.length; i++) {
                                String logicValue = idLogic[1];
                                ChainResult = MagicChainClear(Data, logicValue, totalCount, "id", id);
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

                    if (id.equals("create:lectern_controller")) {
                        ChainResult = MagicChainClear(Data,
                                "nbt.Controller.id", totalCount,
                                "operate" +
                                        ".clear$tag" +
                                        ".replace$id$create:linked_controller", id);
                        Data = S_tag(ChainResult.get("data"));
                        totalCount = S_int(ChainResult.get("find_count"));
                    }

                    if (id.equals("create:clipboard")) {
                        ChainResult = MagicChainClear(Data,
                                "nbt.Item.id", totalCount,
                                "operate" +
                                        ".clear$tag" +
                                        ".replace$id$create:clipboard", id);
                        Data = S_tag(ChainResult.get("data"));
                        totalCount = S_int(ChainResult.get("find_count"));
                    }

                    if (id.equals("create:valve_handle")) {
                        ChainResult = MagicChainClear(Data,
                                "nbt.ScrollValue", totalCount,
                                "operate" +
                                        ".limit$ScrollValue$-180$180", id);
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
                                    ChainResult = MagicChainClear(Data, chain, totalCount, surgery, idKey);
                                    Data = S_tag(ChainResult.get("data"));
                                    totalCount = S_int(ChainResult.get("find_count"));
                                }
                            }
                        } catch (Exception e) {
                            Message.FE(translateDirect("check.chain.rule.error"));
                            Message.FE(translateDirect("check.chain.rule.block") +
                                    (sequence) + 1 + translateDirect("check.chain.rule.rule") +
                                    idKey + "|" + chain + "|" + surgery + "]");
                            if (enable_debug) {
                                Message.debug(idLogic[0] + "|" + idLogic[1] + "|" + idLogic[2]);
                                Message.debug(Data);
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (type.contains("palette")) {
                    String id = StrTag(Objects.requireNonNull(Data.get("Name")));

                    if (id.equals("supplementaries:urn")) {
                        ChainResult = MagicChainClear(Data,
                                "Properties.treasure", totalCount,
                                "operate" +
                                        ".replace$treasure$false", id);
                        Data = S_tag(ChainResult.get("data"));
                        totalCount = S_int(ChainResult.get("find_count"));
                    }

                    if (id.equals("create_connected:kinetic_battery")) {
                        ChainResult = MagicChainClear(Data,
                                "Properties.level", totalCount,
                                "operate" +
                                        ".replace$level$0", id);
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

            if (!type.contains("rule")) {
                if (totalCount != 0) {
                    IsNotMatch = true;
                    Message.FM(
                            translateDirect("csc.mismatch.output1") + type +
                                    translateDirect("csc.mismatch.output2") + block_information +
                                    translateDirect("csc.mismatch.output3") + (sequence + 1) +
                                    translateDirect("csc.mismatch.output4") + total +
                                    translateDirect("csc.mismatch.output5") + totalCount);
                    Message.FM(translateDirect("csc.mismatch.before"));
                    Message.FM(Before);
                    Message.FM(translateDirect("csc.mismatch.after"));
                    Message.FM(Data.toString());
                }
            }

            result.put("IsNotMatch", IsNotMatch);
            result.put("Data", Data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<String, Object>();
    }

    public static boolean TagSweepHelper(CompoundTag Data, String HasTag) {
        int TagCount = countBanTag(Data.toString());
        int SafeCount = 0;
        CompoundTag a = Data.getCompound("nbt");
        CompoundTag FilterSlot = a.getCompound("Filter");
        if (!FilterSlot.toString().equals("{}")) {
            CompoundTag components = FilterSlot.getCompound("components");
            if (!components.toString().equals("{}")) {
                if (components.contains("!minecraft:attribute_modifiers")) {
                    SafeCount++;
                }
            }
        }

        if (SafeCount == TagCount) {
            return false;
        } else {
            Message.FW(translateDirect("config.tag.mismatch.output") + "[" + HasTag + "]" + translateDirect("config.tag.mismatch.output2") + Data);
            return true;
        }
    }

    public static int countBanTag(String data) {
        String lowData = data.toLowerCase();
        int count = 0;

        for (String item : ban_tag) {
            String lowItem = item.toLowerCase();
            int index = 0;

            while ((index = lowData.indexOf(lowItem, index)) != -1) {
                count++;
                index += lowItem.length();
            }
        }

        return count;
    }

    public static CompoundTag BlockClearID(CompoundTag Data) {
        CompoundTag result = Nbt.newCompoundTag();
        return Data;
    }

    public static String Clear$(String input) {
        return input.replace("$", "");
    }

    public static int CountToClear(String data, String[] ban_block) {
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
