package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.lang.Message;

import java.util.ArrayList;
import java.util.List;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.S_bool;

public class run {

    public static void main(String[] args) {
        List<String> log = new ArrayList<String>();
        CSC_INIT(log);

        List<String> CheatLog = new ArrayList<String>();

        long startTime = System.currentTimeMillis();
        Message.FP("**********************************************");
        BlueCore checker = new BlueCore();
        String id = "/uploaded/track_1.nbt";
        boolean result = S_bool(checker.SchematicBlueCore(id, CheatLog).get("Cheat"));
        if (result) {
            Message.FE("clear!");
        } else {
            Message.FM("Success");
        }
        Message.FP("**********************************************");

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        Message.FP("Total use time: " + executionTime + " ms");

        String id2 = "/uploaded/result.nbt";
        boolean result2 = S_bool(checker.SchematicBlueCore(id2, CheatLog).get("Cheat"));
    }
}
