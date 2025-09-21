package com.Pink_Cats.createschematicchecker.FancyConfig;


import com.electronwill.nightconfig.toml.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigRegister {

    private String name;
    private String version;
    private String description;
    private List<String> banBlock;

    // Getter和Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getBanBlock() { return banBlock; }
    public void setBanBlock(List<String> banBlock) { this.banBlock = banBlock; }
}