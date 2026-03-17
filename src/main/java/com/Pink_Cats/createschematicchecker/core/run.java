package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.lang.Message;


import java.util.List;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.S_bool;


public class run {

    public  static void main(String[] args) {
        
        List<String> log = List.of();
        CSC_INIT(log);

        // 输出配置项的值，使用 toJavaString 方法转换为 Java String
        //Message.FM("Language: " + language);
        //Message.FM("Enable: " + enable);
        //Message.FM("Random: " + RANDOM.getDefaultValue());

        List<String> CheatLog = List.of();

        long startTime = System.currentTimeMillis();
        Message.FP("**********************************************");
        BlueCore checker = new BlueCore();
        String id = "/uploaded/track_1.nbt";
        boolean result = S_bool(checker.SchematicBlueCore(id,CheatLog).get("Cheat")) ;
        if (result) {Message.FE("clear!");}else {Message.FM("Success");}
        Message.FP("**********************************************");


        // 获取结束时间
        long endTime = System.currentTimeMillis();

        // 计算运行时间
        long executionTime = endTime - startTime;
        Message.FP("Total use time: " + executionTime + " ms");

        String id2 = "/uploaded/result.nbt";
        boolean result2 = S_bool(checker.SchematicBlueCore(id2,CheatLog).get("Cheat")) ;


    }


}
