package com.Pink_Cats.createschematicchecker.event;


import com.Pink_Cats.createschematicchecker.lang.Message;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

@Mod.EventBusSubscriber
public class TempOffEvent {

    public static boolean Temporary_stop = false;
    public static boolean CheckSchematic = true;
    public static int StopTick = 0;


    @SubscribeEvent
    public static void serverStartTickEvent(TickEvent.ServerTickEvent evt) {
        if (evt.phase == TickEvent.Phase.START) {
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
    }

    public static int getStopTick() {
        return StopTick;
    }
}
