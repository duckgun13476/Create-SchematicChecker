package com.Pink_Cats.createschematicchecker.event;


import com.Pink_Cats.createschematicchecker.Createschematicchecker;
import com.Pink_Cats.createschematicchecker.lang.Message;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;


import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

@EventBusSubscriber
public class TempOffEvent {

    public static boolean Temporary_stop = false;
    public static boolean CheckSchematic = true;
    public static int StopTick = 0;


    @SubscribeEvent
    public static void serverStartTickEvent(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
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
