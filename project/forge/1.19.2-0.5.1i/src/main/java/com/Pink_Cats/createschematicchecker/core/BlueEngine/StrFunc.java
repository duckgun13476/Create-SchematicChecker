package com.Pink_Cats.createschematicchecker.core.BlueEngine;

import com.Pink_Cats.createschematicchecker.lang.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

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
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 定义格式：yyyy_MMdd_HH_mm
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm");

        // 格式化并返回
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
            // 提取 blocks 信息
            Message.FP("block");
            ListTag blocks = nbt_data.getList("blocks", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < blocks.size(); i++) {
                CompoundTag block = blocks.getCompound(i);
                Message.FP(block );
            }
            // 提取 palette 信息
            Message.FM("palette");
            ListTag palette = nbt_data.getList("palette", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < palette.size(); i++) {
                CompoundTag paletteItem = palette.getCompound(i);
                Message.FP(paletteItem );

            }
            // 提取 entity 信息
            Message.FP("entities");
            ListTag entities = nbt_data.getList("entities", 10); // 10 表示 CompoundTag 类型
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
        // 使用 replace() 方法将双引号替换为空字符串
        return input.replace("\"", "");
    }

    public static String NoAir(String input) {
        // 使用 replaceAll() 方法将空格和回车替换为空字符串
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
                return true; // 如果无法将元素添加到集合中（即已经存在相同的元素），返回 true
            }
        }

        return false; // 如果遍历完整个数组都没有发现重复元素，返回 false
    }

}
