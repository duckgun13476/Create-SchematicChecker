package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtFunc;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.pinkcats.torque.layer.net.minecraft.nbt.CompoundTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.Nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.enable_backup;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.report_schematic;
import static com.Pink_Cats.createschematicchecker.FancyConfig.FileIO.createIfNotExists;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.S_bool;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.S_tag;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.getCurrentDateTime;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;
import static com.Pink_Cats.createschematicchecker.network.OnlineTasks.postDataAsync;
import static com.Pink_Cats.createschematicchecker.network.OnlineTasks.reportProblem;

public class BlueCore {

    NbtFunc MainCheck = new NbtFunc();
    public Map<String,Object> SchematicBlueCore(String blueprintId, List<String> CheatLog)
    {
        //蓝图路径
        String DefaultPath = "./schematics/uploaded/";
        //String DefaultPath = System.getProperty("user.dir");
        String path = DefaultPath + blueprintId;
        Message.FD(translateDirect("console.thread.new")+path);
        Message.diag("[Diag][BlueCore][START] blueprintId=" + blueprintId + ", fullPath=" + path);
        try {
            CompoundTag nbt_data = Path_to_CompoundTag(path);
            String User = blueprintId.split("/")[0];
            String Blueprint = blueprintId.split("/")[1];
            Message.diag("[Diag][BlueCore][SPLIT] user=" + User
                    + ", blueprintSegment=" + Blueprint
                    + ", slashCount=" + blueprintId.chars().filter(ch -> ch == '/').count());
            //SchematicOutput(nbt_data);
            if (enable_backup)
            {
                Message.diag("[Diag][BlueCore][BACKUP] outputDir=config/CSC/backup/" + User + "/"
                        + ", outputFile=" + getCurrentDateTime() + Blueprint);
                CompoundTag_to_Path(nbt_data, "config/CSC/backup/"+User+"/",getCurrentDateTime()+Blueprint);
            }

            Map<String,Object> result = MainCheck.NBTCheck(nbt_data,CheatLog);
            boolean CannotCheck = S_bool(result.get("CannotCheck"));
            boolean IsCheat = S_bool(result.get("Cheat"));
            boolean IsProblem = S_bool(result.get("Problem"));
            Message.diag("[Diag][BlueCore][NBTCHECK] cannotCheck=" + CannotCheck
                    + ", isCheat=" + IsCheat
                    + ", isProblem=" + IsProblem);

            if (CannotCheck && report_schematic){
                Message.diag("[Diag][BlueCore][REPORT] outputDir=config/CSC/problem/" + User + "/"
                        + ", outputFile=CheckFail_" + getCurrentDateTime() + Blueprint);
                CompoundTag_to_Path(nbt_data, "config/CSC/problem/"+User+"/","CheckFail_"+getCurrentDateTime()+Blueprint);
                reportProblem("config/CSC/problem/"+User+"/"+"CheckFail_"+getCurrentDateTime()+Blueprint);
            }

            if (enable_backup){
                if (IsProblem) {
                    if(IsCheat){
                        Message.diag("[Diag][BlueCore][REPORT] outputDir=config/CSC/problem/" + User + "/"
                                + ", outputFile=Cheat_" + getCurrentDateTime() + Blueprint);
                        CompoundTag_to_Path(nbt_data, "config/CSC/problem/"+User+"/","Cheat_"+getCurrentDateTime()+Blueprint);
                        postDataAsync("config/CSC/problem/"+User+"/"+"Cheat_"+getCurrentDateTime()+Blueprint);
                    }else {
                        Message.diag("[Diag][BlueCore][REPORT] outputDir=config/CSC/problem/" + User + "/"
                                + ", outputFile=Problem_" + getCurrentDateTime() + Blueprint);
                        CompoundTag_to_Path(nbt_data, "config/CSC/problem/"+User+"/","Problem_"+getCurrentDateTime()+Blueprint);
                        postDataAsync("config/CSC/problem/"+User+"/"+"Problem_"+getCurrentDateTime()+Blueprint);
                    }


                }

            }

            //SchematicOutput(S_tag(result.get("nbt_data")));

            Message.diag("[Diag][BlueCore][WRITEBACK] outputDir=" + DefaultPath+User+"/"
                    + ", outputFile=" + Blueprint);
            CompoundTag_to_Path(S_tag(result.get("nbt_data")), DefaultPath+User+"/",Blueprint);


            return result;

        } catch (java.util.zip.ZipException e) {

            Message.FW(translateDirect("core.decode.ZipError")+"[" + e.getMessage() + "]");
            Message.diag("[Diag][BlueCore][ZIP_EXCEPTION] blueprintId=" + blueprintId + ", fullPath=" + path + ", error=" + e);
            return cannotCheckResult();
        } catch (Exception e) {
            e.printStackTrace();
            Message.diag("[Diag][BlueCore][EXCEPTION] blueprintId=" + blueprintId + ", fullPath=" + path + ", error=" + e);
            Message.FW("Wrong in BlueCore" + DefaultPath);
            return cannotCheckResult();
        }
    }

    private Map<String, Object> cannotCheckResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("CannotCheck", true);
        result.put("Problem", true);
        result.put("Cheat", false);
        return result;
    }




    //tool func
    public CompoundTag Path_to_CompoundTag(String SchematicPath) throws IOException {
        File BluePrint = new File(SchematicPath);
        FileInputStream file_stream = new FileInputStream(BluePrint);
        return Nbt.readCompressed(file_stream);
    }


    public void CompoundTag_to_Path(CompoundTag compoundTag, String outputPath,String file) throws IOException {
        createIfNotExists(outputPath);
        File outputFile = new File(outputPath,file);
        Message.diag("[Diag][BlueCore][FILE_WRITE] outputPath=" + outputPath
                + ", file=" + file
                + ", absoluteTarget=" + outputFile.getAbsolutePath()
                + ", parentExists=" + (outputFile.getParentFile() != null && outputFile.getParentFile().exists()));
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        Nbt.writeCompressed(compoundTag, fileOutputStream);
        fileOutputStream.close();
    }




}
