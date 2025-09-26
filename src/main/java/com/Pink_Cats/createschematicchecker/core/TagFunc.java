package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Objects;

import static com.Pink_Cats.createschematicchecker.core.NbtInterFace.S_bool;
import static com.Pink_Cats.createschematicchecker.core.NbtInterFace.S_tag;

public class TagFunc {



    public static String BlockGetId (CompoundTag tag, ListTag PaletteBlockData) {
        try{
            return Objects.requireNonNull(
                    StrFunc.NoQuotes(
                            String.valueOf(
                                    Objects.requireNonNull(
                                            tag.getCompound("nbt").get("id")
                                    )
                            )
                    )

            );
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
