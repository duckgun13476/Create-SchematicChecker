package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.NbtInterFace.StrTag;

public class StrFunc {

    public static boolean HasBanTag(String data){
        for (String item : ban_tag){
            if (data.contains(item)){
                return true;
            }
        }
        return false;
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
            Message.FM("block");
            ListTag blocks = nbt_data.getList("blocks", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < blocks.size(); i++) {
                CompoundTag block = blocks.getCompound(i);
                Message.FM(block );
            }
            // 提取 palette 信息
            Message.FM("palette");
            ListTag palette = nbt_data.getList("palette", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < palette.size(); i++) {
                CompoundTag paletteItem = palette.getCompound(i);
                Message.FM(paletteItem );

            }
            // 提取 entity 信息
            Message.FM("entities");
            ListTag entities = nbt_data.getList("entities", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entity = entities.getCompound(i);
                Message.FM(entity.toString());
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
        return  StrTag(Objects.requireNonNull(nbt_data.get("id")));
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
