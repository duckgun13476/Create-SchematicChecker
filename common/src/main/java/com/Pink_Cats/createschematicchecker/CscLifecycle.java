package com.Pink_Cats.createschematicchecker;

import com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister;
import com.Pink_Cats.createschematicchecker.FancyConfig.ConfigArchiveNotice;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.pinkcats.torque.layer.TorqueLayer;
import com.pinkcats.torque.layer.platform.Platform;

import java.util.List;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.attach.Math.StringToInt;
import static com.Pink_Cats.createschematicchecker.database.SingleLog.CSC_MES;
import static com.Pink_Cats.createschematicchecker.database.SingleLog.CSC_WARN;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

public class CscLifecycle {
    public interface CreateConfigSource {
        String cannonDelay();

        String maxBeltLength();

        String maxEjectorDistance();

        String maxChassisRange();

        boolean hasChainConveyorLength();
    }

    private final CreateConfigSource createConfig;

    public CscLifecycle(CreateConfigSource createConfig) {
        this.createConfig = createConfig;
    }

    public void commonSetup() {
        Message.FM(translateDirect("console.LoadingConfig"));
        List<String> log = List.of();
        CSC_Variables_Load();
        CSC_INIT(log);
    }

    public void serverStarting() {
        injectCreateConfig();
        CSC_MES.reopen();
        CSC_WARN.reopen();
        Message.FM("   _____  _____  _____ ");
        Message.FM("  / ____|/ ____|/ ____|");
        Message.FM(" | |    | (___ | |     " + "   " + translateDirect("console.CscVersion"));
        Message.FM(" | |     \\___ \\| |     " + "   " + platformVersionLine());
        Message.FM(" | |____ ____) | |____ " + "   " + translateDirect("console.feedback"));
        Message.FM("  \\_____|_____/ \\_____|");
        Message.FM("                       ");
    }

    public void serverStopping() {
        CSC_Variables_Save();
        ConfigArchiveNotice.clear();
        Message.FM(translateDirect("console.csc.StopServer"));
        CSC_MES.close();
        CSC_WARN.close();
    }

    public void configReloading() {
        try {
            CSC_RELOAD();
        } catch (Exception ex) {
            Message.FE(translateDirect("console.ConfigReloadError"));
        }
    }

    private String platformVersionLine() {
        try {
            Platform platform = TorqueLayer.platform();
            return translateDirect("console.McVersion") + " "
                    + platform.minecraftVersion() + " "
                    + platform.loaderName();
        } catch (IllegalStateException ex) {
            return translateDirect("console.McVersion");
        }
    }

    private void injectCreateConfig() {
        try {
            CannonDelay = createConfig.cannonDelay();
            MaxBelt = createConfig.maxBeltLength();
            MaxIndex = String.valueOf(StringToInt(ConfigRegister.MaxBelt) - 1);
            MaxEject = createConfig.maxEjectorDistance();
            MaxChassisRange = createConfig.maxChassisRange();
            CreateVersion = createConfig.hasChainConveyorLength() ? "6.0" : "0.5";
        } catch (Exception ex) {
            Message.FE(translateDirect("console.ConfigInjectCreateError"));
        }
    }
}
