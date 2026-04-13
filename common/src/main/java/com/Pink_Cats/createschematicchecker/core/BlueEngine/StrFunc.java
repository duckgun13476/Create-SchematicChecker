package com.Pink_Cats.createschematicchecker.core.BlueEngine;

import com.Pink_Cats.createschematicchecker.lang.Message;
import com.pinkcats.torque.layer.net.minecraft.nbt.CompoundTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.ListTag;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.StrTag;

public class StrFunc {


    public static String getCurrentDateTime() {
        // 鑾峰彇褰撳墠鏃堕棿
        LocalDateTime now = LocalDateTime.now();

        // 瀹氫箟鏍煎紡锛歽yyy_MMdd_HH_mm
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");

        // 鏍煎紡鍖栧苟杩斿洖
        return now.format(formatter);
    }


    public static String HasBanTag(String data){
        String LowData = data.toLowerCase();
        for (String item : ban_tag){
            String LowItem = item.toLowerCase();
            if (LowData.contains(LowItem)){
                return LowItem ;
            }
        }
        return "";
    }

    public static boolean isInBanTag( String target) {
        for (String item : ban_tag){
            if (target.equals(item)){
                return true;
            }
        }
        return false;
    }

    public static boolean IsBanEntity(String data){
        for (String item : ban_entity){
            if (data.equals(item)){
                return true;
            }
        }
        return false;
    }

    public static boolean IsWhitelistEntity(String data){
        for (String item : whitelist_entity){
            if (data.equals(item)){
                return true;
            }
        }
        return false;
    }

    public static boolean HasBanBlock(String data){

        for (String item : ban_block){
            if (data.contains(item)){
                return true;
            }
        }
        return false;
    }

    public static boolean isInBanBlock( String target) {
        for (String item : ban_block){
            if (target.equals(item)){
                return true;
            }
        }
        return false;
    }



    public static void SchematicOutput(CompoundTag nbt_data){
        try {
            // 鎻愬彇 blocks 淇℃伅
            Message.FP("block");
            ListTag blocks = nbt_data.getList("blocks", 10); // 10 琛ㄧず CompoundTag 绫诲瀷
            for (int i = 0; i < blocks.size(); i++) {
                CompoundTag block = blocks.getCompound(i);
                Message.FP(block );
            }
            // 鎻愬彇 palette 淇℃伅
            Message.FM("palette");
            ListTag palette = nbt_data.getList("palette", 10); // 10 琛ㄧず CompoundTag 绫诲瀷
            for (int i = 0; i < palette.size(); i++) {
                CompoundTag paletteItem = palette.getCompound(i);
                Message.FP(paletteItem );

            }
            // 鎻愬彇 entity 淇℃伅
            Message.FP("entities");
            ListTag entities = nbt_data.getList("entities", 10); // 10 琛ㄧず CompoundTag 绫诲瀷
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entity = entities.getCompound(i);
                Message.FP(entity.toString());
            }

        }
        catch (Exception e) {
            Message.FE(" Output Fail!" + e.getMessage());

        }
    }


    public static String NoQuotes(String input) {
        // 浣跨敤 replace() 鏂规硶灏嗗弻寮曞彿鏇挎崲涓虹┖瀛楃涓?
        return input.replace("\"", "");
    }

    public static String NoAir(String input) {
        // 浣跨敤 replaceAll() 鏂规硶灏嗙┖鏍煎拰鍥炶溅鏇挎崲涓虹┖瀛楃涓?
        return input.replaceAll("\\s", "");
    }


    public static String TagMapId(CompoundTag nbt_data){
        try {
            return  StrTag(Objects.requireNonNull(nbt_data.get("id")));
        } catch (Exception e) {
            //Message.FE(translateDirect("tag.no.id")+" "+nbt_data);
            return "null:null";
        }

    }

    public static boolean hasDuplicate(List<String> array) {
        Set<String> set = new HashSet<>();

        for (String element : array) {
            if (!set.add(element)) {
                return true; // 濡傛灉鏃犳硶灏嗗厓绱犳坊鍔犲埌闆嗗悎涓紙鍗冲凡缁忓瓨鍦ㄧ浉鍚岀殑鍏冪礌锛夛紝杩斿洖 true
            }
        }

        return false; // 濡傛灉閬嶅巻瀹屾暣涓暟缁勯兘娌℃湁鍙戠幇閲嶅鍏冪礌锛岃繑鍥?false
    }

}
