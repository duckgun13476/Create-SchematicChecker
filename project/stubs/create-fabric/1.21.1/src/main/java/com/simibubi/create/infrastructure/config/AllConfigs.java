package com.simibubi.create.infrastructure.config;

public final class AllConfigs {
    private static final Server SERVER = new Server();

    private AllConfigs() {
    }

    public static Server server() {
        return SERVER;
    }

    public static final class Server {
        public final Schematics schematics = new Schematics();
        public final Kinetics kinetics = new Kinetics();
    }

    public static final class Schematics {
        public final ConfigValue<Integer> schematicannonDelay = new ConfigValue<>(10);
    }

    public static final class Kinetics {
        public final ConfigValue<Integer> maxBeltLength = new ConfigValue<>(20);
        public final ConfigValue<Integer> maxEjectorDistance = new ConfigValue<>(16);
        public final ConfigValue<Integer> maxChassisRange = new ConfigValue<>(16);
        public final ConfigValue<Integer> maxChainConveyorLength = new ConfigValue<>(32);
    }

    public static final class ConfigValue<T> {
        private final T value;

        public ConfigValue(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }
    }
}
