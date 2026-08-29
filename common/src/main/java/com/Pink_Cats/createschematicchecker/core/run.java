package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.lang.Message;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.S_bool;


public class run {

    public  static void main(String[] args) {
        
        initTorqueLayerForStandalone();

        List<String> log = new ArrayList<>();
        CSC_INIT(log);

        // 杈撳嚭閰嶇疆椤圭殑鍊硷紝浣跨敤 toJavaString 鏂规硶杞崲涓?Java String
        //Message.FM("Language: " + language);
        //Message.FM("Enable: " + enable);
        //Message.FM("Random: " + RANDOM.getDefaultValue());

        List<String> CheatLog = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        Message.FP("**********************************************");
        BlueCore checker = new BlueCore();
        String id = args.length > 0 ? args[0] : "Dev/111.nbt";
        Map<String, Object> checkResult = checker.SchematicBlueCore(id,CheatLog);
        boolean result = S_bool(checkResult.get("Cheat")) ;
        boolean cannotCheck = S_bool(checkResult.get("CannotCheck")) ;
        boolean problem = S_bool(checkResult.get("Problem")) ;
        Message.FM("Blueprint: " + id);
        Message.FM("Cheat=" + result + ", Problem=" + problem + ", CannotCheck=" + cannotCheck);
        Message.FM("CheatLog=" + CheatLog);
        Message.FP("**********************************************");


        // 鑾峰彇缁撴潫鏃堕棿
        long endTime = System.currentTimeMillis();

        // 璁＄畻杩愯鏃堕棿
        long executionTime = endTime - startTime;
        Message.FP("Total use time: " + executionTime + " ms");

        System.exit(0);

    }

    private static void initTorqueLayerForStandalone() {
        try {
            Class<?> torqueLayer = Class.forName("com.pinkcats.torque.layer.TorqueLayer");
            torqueLayer.getMethod("platform").invoke(null);
        } catch (Throwable ignored) {
            try {
                Class<?> loggerFactoryClass = Class.forName("org.slf4j.LoggerFactory");
                Class<?> loggerClass = Class.forName("org.slf4j.Logger");
                Object logger = loggerFactoryClass
                        .getMethod("getLogger", String.class)
                        .invoke(null, "CreateSchematicCheckerStandalone");
                Class<?> torqueLayer = Class.forName("com.pinkcats.torque.layer.TorqueLayer");
                Class<?> platformInterface = Class.forName("com.pinkcats.torque.layer.platform.Platform");
                String[] platformClassNames = {
                        "com.pinkcats.torque.layer.forge.ForgePlatform",
                        "com.pinkcats.torque.layer.neoforge.NeoForgePlatform",
                        "com.pinkcats.torque.layer.fabric.FabricPlatform"
                };
                Throwable platformFailures = null;
                for (String platformClassName : platformClassNames) {
                    try {
                        Class<?> platformClass = Class.forName(platformClassName);
                        Object platform = null;
                        // Loader platforms with an event bus must be constructed through their
                        // four-argument form for standalone scans. The three-argument Forge
                        // constructor fetches FMLJavaModLoadingContext and cannot run here.
                        for (java.lang.reflect.Constructor<?> constructor : platformClass.getDeclaredConstructors()) {
                            if (constructor.getParameterCount() == 4) {
                                platform = constructor.newInstance(logger, "standalone", "standalone", null);
                                break;
                            }
                        }
                        if (platform == null) {
                            platform = platformClass
                                    .getDeclaredConstructor(loggerClass, String.class, String.class)
                                    .newInstance(logger, "standalone", "standalone");
                        }
                        torqueLayer.getMethod("init", platformInterface).invoke(null, platform);
                        return;
                    } catch (Throwable candidateFailure) {
                        if (platformFailures == null) {
                            platformFailures = candidateFailure;
                        } else {
                            platformFailures.addSuppressed(candidateFailure);
                        }
                    }
                }
                throw new IllegalStateException("No compatible TorqueLayer platform was available", platformFailures);
            } catch (Throwable ignoredAgain) {
                System.err.println("Standalone TorqueLayer init failed: " + ignoredAgain);
                // In a normal mod loader run, TorqueAPI initializes its layer before CSC uses it.
            }
        }
    }


}
