package com.Pink_Cats.createschematicchecker.core.BlueEngine;

import com.Pink_Cats.createschematicchecker.lang.Message;
import net.minecraft.nbt.*;

import java.util.ArrayList;

import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.NoQuotes;

public class NbtInterFace {



    public static boolean AreTagEquals(CompoundTag tag1, CompoundTag tag2) {
        if (tag1.size() != tag2.size()) {
            return false;
        }
        else  {
            return tag1.equals(tag2);
        }
    }
    //compoundTag.put("byteTag", ByteTag.valueOf((byte) 10));
    //compoundTag.put("shortTag", ShortTag.valueOf((short) 20));
    //compoundTag.put("intTag", IntTag.valueOf(30));
    //compoundTag.put("longTag", LongTag.valueOf(40L));
    //compoundTag.put("floatTag", FloatTag.valueOf(50.0f));
    //compoundTag.put("doubleTag", DoubleTag.valueOf(60.0));
    //    compoundTag.put("stringTag", StringTag.valueOf(nbtString));

    public static Tag TagString(String data){
        return StringTag.valueOf(data);
    }

    public static Tag TagInt(int data){
        return IntTag.valueOf(data);
    }

    public static Tag TagLong(long data){
        return LongTag.valueOf(data);
    }
    public static Tag TagFloat(float data){
        return FloatTag.valueOf(data);
    }
    public static Tag TagDouble(double data){
        return DoubleTag.valueOf(data);
    }

    public static Tag TagByte(byte data){
        return ByteTag.valueOf(data);
    }

    public static Tag TagShort(short data){
        return ShortTag.valueOf(data);
    }


    //-----------------------------------------------------------------------------------------------------------

    public static boolean S_bool(Object data){
        try {
            if (data instanceof Boolean) {
                return (Boolean) data;
            }
        }
        catch (ClassCastException e) {
            Message.FE("error in O_bool"+ e.toString());
        }
        return false;
    }

    public static String S_str(Object data){
        try {
            if (data instanceof String) {
                return (String) data;
            }
        }
        catch (ClassCastException e) {
            Message.FE("error in O_str"+ e.toString());
        }
        return "";
    }

    public static CompoundTag S_tag(Object data){
        try {
            if (data instanceof CompoundTag) {
                return (CompoundTag) data;
            }
        }
        catch (ClassCastException e) {
            Message.FE("error in O_tag"+ e.toString());
        }
        return null;
    }

    public static int S_int(Object data){
        try {
            if (data instanceof Integer) {
                return Integer.parseInt(String.valueOf(data));
            }

        } catch (NumberFormatException e) {
            System.out.println("error in O_int"+ e);

        }
        return 0;
    }

    public static ArrayList<CompoundTag> S_TagList(ArrayList<Object> objectList) {
        ArrayList<CompoundTag> compoundTagList = new ArrayList<>();
        try {
            for (Object obj : objectList) {
                if (obj instanceof CompoundTag) {
                    compoundTagList.add((CompoundTag) obj);
                }
            }

            return compoundTagList;
        } catch (ClassCastException e) {
            Message.FE("error in O_TagList"+ e);
            return compoundTagList;
        }

    }
    //-----------------------------------------------------------------------------------------------------------

    public static String StrTag(Tag data){
        return NoQuotes(data.toString());
    }


    public static int StringToInt(String str) {
        return Integer.parseInt(str);
    }
    public static float StringToFloat(String str) {
        return Float.parseFloat(str);
    }


}
