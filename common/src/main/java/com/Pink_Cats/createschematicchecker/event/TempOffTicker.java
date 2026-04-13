package com.Pink_Cats.createschematicchecker.event;

import com.Pink_Cats.createschematicchecker.lang.Message;

import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

public class TempOffTicker {

    public static boolean Temporary_stop = false;
    public static boolean CheckSchematic = true;
    public static int StopTick = 0;

    public static void tick() {
        if (Temporary_stop) {
            StopTick = 300 * 20;
            Temporary_stop = false;
            CheckSchematic = false;
        }
        if (StopTick > 0) {
            StopTick--;
        }
        if (StopTick == 1) {
            Message.FM(translateDirect("csc.off.temporary.restore"));
            CheckSchematic = true;
        }
    }

    public static int getStopTick() {
        return StopTick;
    }
}
