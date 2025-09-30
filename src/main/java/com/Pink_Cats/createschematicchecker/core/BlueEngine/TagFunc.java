package com.Pink_Cats.createschematicchecker.core.BlueEngine;

import com.Pink_Cats.createschematicchecker.lang.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Objects;

public class TagFunc {



    public static String BlockGetId (CompoundTag tag, ListTag PaletteBlockData) {
        try{
            String result = Objects.requireNonNull(
                    StrFunc.NoQuotes(
                            String.valueOf(
                                    Objects.requireNonNull(
                                            tag.getCompound("nbt").get("id")
                                    )
                            )
                    )

            );
            if (result.equals("create:funnel")) {
                try {
                    int state = tag.getInt("state");
                    CompoundTag PaletteData = PaletteBlockData.getCompound(state);
                    return PaletteData.getString("Name");
                }
                catch(Exception ex) {
                    Message.FE("FunnelGetIdError:" + ex.getMessage());
                }
            }

            return result;
        }
        catch(Exception e){
            try {
                int state = tag.getInt("state");
                CompoundTag PaletteData = PaletteBlockData.getCompound(state);
                return PaletteData.getString("Name");
            }
            catch(Exception ex) {
                Message.FE("BlockGetIdError:" + e.getMessage());
            }

        }
        return "null:null";
    }

}
