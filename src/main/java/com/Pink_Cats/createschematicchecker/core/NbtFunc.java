package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.Message;
import net.minecraft.nbt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlockSweeper.ClearBanBlock;
import static com.Pink_Cats.createschematicchecker.core.NbtInterFace.*;
import static com.Pink_Cats.createschematicchecker.core.StrFunc.HasBanBlock;
import static com.Pink_Cats.createschematicchecker.core.StrFunc.HasBanTag;
import static com.Pink_Cats.createschematicchecker.core.TagFunc.BlockGetId;
import static com.mojang.text2speech.Narrator.LOGGER;

public class NbtFunc {
    //test para
    boolean DEBUG_ID = false;



    public Map<String, Object> NBTCheck(CompoundTag nbt_data)//校验 NBT
    {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> paletteItemResult;
        Map<String, Object> blockResult;
        Map<String, Object> entityResult;

        boolean Cheat = false;
        //PinkCats Inject
        try {

            // 提取 palette 信息
            ListTag palette = nbt_data.getList("palette", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < palette.size(); i++) {
                CompoundTag paletteItem = palette.getCompound(i);
                paletteItemResult = BaseBlockHandle(paletteItem,"palette",palette,i);
                paletteItem = S_tag(paletteItemResult.get("Data"));
                Cheat = S_bool(paletteItemResult.get("Cheat")) ;
                if (paletteItem != null) {
                    palette.set(i, paletteItem);
                }
            }

            // 提取 blocks 信息
            ListTag blocks = nbt_data.getList("blocks", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < blocks.size(); i++) {
                CompoundTag block = blocks.getCompound(i);
                blockResult = BaseBlockHandle(block,"block",palette,i);
                block = S_tag(blockResult.get("Data"));
                Cheat = S_bool(blockResult.get("Cheat"));
                if (block != null) {
                    blocks.set(i, block);
                }
            }

            // 提取 entity 信息
            ListTag entities = nbt_data.getList("entities", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entity = entities.getCompound(i);
                entityResult = BaseBlockHandle(entity,"entity",palette,i);
                entity = S_tag(entityResult.get("Data"));
                Cheat = S_bool(entityResult.get("Cheat")) ;
                if (entity != null) {
                    entities.set(i, entity);
                }
            }

            nbt_data.put("blocks", blocks);
            nbt_data.put("palette", palette);
            nbt_data.put("entities", entities);
            // Output Result
            if (debug_total_block){
                for (Map.Entry<String, Integer> entry : blockCounts.entrySet()) {
                    Message.FP("ID: " + entry.getKey() + ", Count: " + entry.getValue());
                }
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        //Inject limit.
        //Add special matcher here.↑
        result.put("nbt_data", nbt_data);
        result.put("Cheat",Cheat);
        return result;
    }

    Map<String, Integer> blockCounts = new HashMap<>();

    public Map<String,Object> BaseBlockHandle(CompoundTag data,String type,ListTag PaletteBlockData,int sequence) {
        boolean Cheat = false;
        boolean HasBanBlock = false;
        Map<String, Object> result = new HashMap<>();

        if (HasBanTag(data.toString())) {
            Message.FM("Find Tag in " + data);
            Cheat = true;
        }

        if (HasBanBlock(data.toString())) {
            //Message.FM("Find Blacklist Block in " + data);
            HasBanBlock = true;
        }


        if (type.equals("block")) {
            String id = BlockGetId(data,PaletteBlockData);
            if (DEBUG_ID) {Message.FE("ID: " + id);}
            blockCounts.put(id, blockCounts.getOrDefault(id, 0) + 1);


            if (HasBanBlock) {
                Map<String,Object> MapData = ClearBanBlock(data,"block",sequence);
                data = S_tag(MapData.get("Data"));
                boolean IsMatch = S_bool(MapData.get("IsMatch"));
                //Message.FE(data);
            }




            //Message.FP("create:clipboard".equals(BlockGetId(data)));
            Cheat = false;


        }

        if (type.equals("palette")) {
            if (HasBanBlock) {
                Map<String,Object> MapData = ClearBanBlock(data,"palette",sequence);
                data = S_tag(MapData.get("Data"));
                boolean IsMatch = S_bool(MapData.get("IsMatch"));
                Message.FE(data);
            }
            Cheat = false;

        }

        if (type.equals("entity")) {
            if (kill_entity){
                data = new CompoundTag();
            }
        }


        if (Cheat) {
            data = new CompoundTag();
        }

        result.put("Cheat", Cheat);
        result.put("Data", data);
        return result;

    }




}

//Message.FM("Name"+ New_enty);
//CompoundTag New_test = new CompoundTag();
//New_enty.put("Name","create:brass_casing" );
//Message.FM("exist"+ entry);
//Message.FM("new"+ New_enty);
//Message.FE(NbtInterFace.AreTagEquals(entry, New_enty));
//Message.FM("Name"+ New_enty);
//New_enty.remove("Name");
//Message.FM("Name"+ New_enty);
//Message.FM("Palette"+ entry);
//CompoundTag exp = new CompoundTag();
//exp.put("Name",TagString("create:brass_casing"));
//Message.FM("Exist exp "+ exp);
//Message.FE(NbtInterFace.AreTagEquals(entry, exp));


