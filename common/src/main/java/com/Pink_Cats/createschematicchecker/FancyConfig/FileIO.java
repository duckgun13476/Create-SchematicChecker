package com.Pink_Cats.createschematicchecker.FancyConfig;

import com.Pink_Cats.createschematicchecker.lang.Message;

import java.io.File;

public class FileIO {

    public static void createIfNotExists(String path) {
        File file = new File(path);

        if (!file.exists()) {
            boolean created = file.mkdirs();
            if (!created) {
                Message.FE("Path Create Failed");
            }
        }
    }


}
