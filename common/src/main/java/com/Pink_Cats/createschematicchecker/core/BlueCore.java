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
import static com.Pink_Cats.createschematicchecker.network.OnlineTasks.reportProblem;
import static com.Pink_Cats.createschematicchecker.event.BlueprintPaths.parseUploadedBlueprint;

public class BlueCore {

    NbtFunc MainCheck = new NbtFunc();
    public Map<String,Object> SchematicBlueCore(String blueprintId, List<String> CheatLog)
    {
        //蓝图路径
        String DefaultPath = "./schematics/uploaded/";
        String path = DefaultPath + blueprintId;
        Message.FD(translateDirect("console.thread.new")+path);
        Message.diag("[Diag][BlueCore][START] blueprintId=" + blueprintId + ", fullPath=" + path);
        try {
            com.Pink_Cats.createschematicchecker.event.BlueprintPaths.UploadedBlueprint uploadedBlueprint = parseUploadedBlueprint(blueprintId);
            path = uploadedBlueprint.path().toString();
            CompoundTag nbt_data = Path_to_CompoundTag(path);
            String User = uploadedBlueprint.user();
            String Blueprint = uploadedBlueprint.fileName();
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

            if (CannotCheck && report_schematic) {
                reportSample(nbt_data, User, Blueprint, "CheckFail");
            } else if (IsProblem && report_schematic) {
                reportSample(nbt_data, User, Blueprint, IsCheat ? "Cheat" : "Problem");
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

    private void reportSample(CompoundTag nbtData, String user, String blueprint, String classification) throws IOException {
        String outputDirectory = "config/CSC/problem/" + user + "/";
        String outputFile = classification + "_" + getCurrentDateTime() + blueprint;
        Message.diag("[Diag][BlueCore][REPORT] outputDir=" + outputDirectory + ", outputFile=" + outputFile);
        CompoundTag_to_Path(nbtData, outputDirectory, outputFile);
        reportProblem(outputDirectory + outputFile);
    }




    //tool func
    public CompoundTag Path_to_CompoundTag(String SchematicPath) throws IOException {
        File BluePrint = new File(SchematicPath);
        try (FileInputStream fileStream = new FileInputStream(BluePrint)) {
            return Nbt.readCompressed(fileStream);
        }
    }


    public void CompoundTag_to_Path(CompoundTag compoundTag, String outputPath,String file) throws IOException {
        createIfNotExists(outputPath);
        File outputFile = new File(outputPath,file);
        Message.diag("[Diag][BlueCore][FILE_WRITE] outputPath=" + outputPath
                + ", file=" + file
                + ", absoluteTarget=" + outputFile.getAbsolutePath()
                + ", parentExists=" + (outputFile.getParentFile() != null && outputFile.getParentFile().exists()));
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            Nbt.writeCompressed(compoundTag, fileOutputStream);
        }
    }




}
