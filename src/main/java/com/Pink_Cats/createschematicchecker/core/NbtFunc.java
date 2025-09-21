package com.Pink_Cats.createschematicchecker.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import static com.mojang.text2speech.Narrator.LOGGER;

public class NbtFunc {

    public Boolean NBTCheck(CompoundTag nbt_data)//校验 NBT
    {
        //PinkCats Inject
        try {
            // 提取 blocks 信息
            System.out.print("block\n");
            ListTag blocks = nbt_data.getList("blocks", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < blocks.size(); i++) {
                CompoundTag block = blocks.getCompound(i);
                System.out.printf(block.toString());
            }
            // 提取 palette 信息
            System.out.print("\npalette\n");
            ListTag palette = nbt_data.getList("palette", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < palette.size(); i++) {
                CompoundTag entry = palette.getCompound(i);
                System.out.printf(entry.toString());
            }
            // 提取 entity 信息
            System.out.print("\nentities\n");
            ListTag entities = nbt_data.getList("entities", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entity = entities.getCompound(i);
                System.out.printf(entity.toString());
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        //Inject limit.
        //Add special matcher here.↑





        return true;
    }







}
