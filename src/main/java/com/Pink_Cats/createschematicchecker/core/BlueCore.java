package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.core.NbtInterFace.S_tag;
import static com.Pink_Cats.createschematicchecker.core.StrFunc.SchematicOutput;

public class BlueCore {

    NbtFunc MainCheck = new NbtFunc();
    public Map<String,Object> SchematicBlueCore(String blueprintId)
    {
        //蓝图路径
        //String DefaultPath = "./schematics/uploaded/";
        String DefaultPath = System.getProperty("user.dir");
        String path = DefaultPath + blueprintId;
        Message.FD("Path:"+path);
        try {
            CompoundTag nbt_data = Path_to_CompoundTag(path);

            Message.FD("Start:\n");
            //SchematicOutput(nbt_data);

            Map<String,Object> result = MainCheck.NBTCheck(nbt_data);

            Message.FD("\n");
            Message.FD("Result:\n");
            //SchematicOutput(S_tag(result.get("nbt_data")));
            CompoundTag_to_Path(S_tag(result.get("nbt_data")), DefaultPath+"/uploaded/result.nbt");
            return result;

        } catch (IOException e) {

            Message.FW("file can't read" + path);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Map.of();
    }




    //tool func
    private CompoundTag Path_to_CompoundTag(String SchematicPath) throws IOException {
        File BluePrint = new File(SchematicPath);
        FileInputStream file_stream = new FileInputStream(BluePrint);
        return NbtIo.readCompressed(file_stream);
    }


    public void CompoundTag_to_Path(CompoundTag compoundTag, String outputPath) throws IOException {
        File outputFile = new File(outputPath);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        NbtIo.writeCompressed(compoundTag, fileOutputStream);
        fileOutputStream.close();
    }




}
