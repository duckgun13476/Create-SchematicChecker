package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BlueCore {

    NbtFunc MainCheck = new NbtFunc();
    public Boolean SchematicBlueCore(String blueprintId)
    {
        //蓝图路径
        //String DefaultPath = "./schematics/uploaded/";
        String DefaultPath = System.getProperty("user.dir");
        String path = DefaultPath + blueprintId;
        Message.FD("Path:"+path);
        try {
            CompoundTag nbt_data = Path_to_CompoundTag(path);
            return MainCheck.NBTCheck(nbt_data);

        } catch (IOException e) {

            Message.FW("file can't read" + path);
            //e.printStackTrace();
            return false;
        }
    }




    //tool func
    private CompoundTag Path_to_CompoundTag(String SchematicPath) throws IOException {
        File BluePrint = new File(SchematicPath);
        FileInputStream file_stream = new FileInputStream(BluePrint);
        return NbtIo.readCompressed(file_stream);
    }


}
