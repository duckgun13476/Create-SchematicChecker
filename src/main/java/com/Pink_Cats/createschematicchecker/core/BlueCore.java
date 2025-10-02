package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtFunc;
import com.Pink_Cats.createschematicchecker.lang.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.enable_backup;
import static com.Pink_Cats.createschematicchecker.FancyConfig.FileIO.createIfNotExists;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.S_bool;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.S_tag;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.getCurrentDateTime;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;
import static com.Pink_Cats.createschematicchecker.network.AutoUpdate.PostDataAsync;

public class BlueCore {

    NbtFunc MainCheck = new NbtFunc();
    public Map<String,Object> SchematicBlueCore(String blueprintId, List<String> CheatLog)
    {
        //蓝图路径
        String DefaultPath = "./schematics/uploaded/";
        //String DefaultPath = System.getProperty("user.dir");
        String path = DefaultPath + blueprintId;
        Message.FD(translateDirect("console.thread.new")+path);
        try {
            CompoundTag nbt_data = Path_to_CompoundTag(path);
            String User = blueprintId.split("/")[0];
            String Blueprint = blueprintId.split("/")[1];
            //SchematicOutput(nbt_data);
            if (enable_backup)
            {
                CompoundTag_to_Path(nbt_data, "config/CSC/backup/"+User+"/",getCurrentDateTime()+Blueprint);
            }

            Map<String,Object> result = MainCheck.NBTCheck(nbt_data,CheatLog);
            boolean IsCheat = S_bool(result.get("Cheat"));
            boolean IsProblem = S_bool(result.get("Problem"));
            if (enable_backup){
                if (IsProblem) {
                    if(IsCheat){
                        CompoundTag_to_Path(nbt_data, "config/CSC/problem/"+User+"/","Cheat_"+getCurrentDateTime()+Blueprint);
                        PostDataAsync("config/CSC/problem/"+User+"/"+"Cheat_"+getCurrentDateTime()+Blueprint);
                    }else {
                        CompoundTag_to_Path(nbt_data, "config/CSC/problem/"+User+"/","Problem_"+getCurrentDateTime()+Blueprint);
                        PostDataAsync("config/CSC/problem/"+User+"/"+"Problem_"+getCurrentDateTime()+Blueprint);
                    }


                }

            }

            //SchematicOutput(S_tag(result.get("nbt_data")));

            CompoundTag_to_Path(S_tag(result.get("nbt_data")), DefaultPath+User+"/",Blueprint);


            return result;

        } catch (Exception e) {
            Message.FW("Wrong in BlueCore" + DefaultPath);
        }
        return Map.of();
    }




    //tool func
    private CompoundTag Path_to_CompoundTag(String SchematicPath) throws IOException {
        File BluePrint = new File(SchematicPath);
        FileInputStream file_stream = new FileInputStream(BluePrint);
        return NbtIo.readCompressed(file_stream);
    }


    public void CompoundTag_to_Path(CompoundTag compoundTag, String outputPath,String file) throws IOException {
        createIfNotExists(outputPath);
        File outputFile = new File(outputPath,file);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        NbtIo.writeCompressed(compoundTag, fileOutputStream);
        fileOutputStream.close();
    }




}
