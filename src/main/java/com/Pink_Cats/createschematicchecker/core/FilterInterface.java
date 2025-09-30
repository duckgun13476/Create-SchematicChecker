package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.lang.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import static com.Pink_Cats.createschematicchecker.core.BlockSweeper.Clear;
import static com.Pink_Cats.createschematicchecker.core.StrFunc.TagMapId;
import static com.Pink_Cats.createschematicchecker.core.StrFunc.isInBanBlock;

public class FilterInterface {



    public static int FilterSweeper(CompoundTag data,int find_count,int turn){
        CompoundTag components = data.getCompound("components");
        String version;
        ListTag filter_items;

        //Message.FM("components    "+components);             //FilterDebug

        //for Create 0.5.1j down
        if (components.toString().equals("{}")){
            version = "0.5.1";
            components = data.getCompound("tag").getCompound("Items");
            filter_items = components.getList("Items", 10);
        }
        else {
            version = "6.0.+";
            filter_items = components.getList("create:filter_items", 10);
        }



        for (int index = 0; index < filter_items.size(); index++) {
            Message.FM("filter_item"+filter_items.getCompound(index));             //FilterDebug

            CompoundTag filter_item;
            if (version.equals( "6.0.+"))
            {filter_item = filter_items.getCompound(index).getCompound("Items");}
            else {
                filter_item = filter_items.getCompound(index);}

            Message.FM("filter_item "+filter_item);
            find_count = SweeperIfHasId(filter_item,find_count,turn);

        }
        return find_count;
    }


    public static int SweeperIfHasId(CompoundTag data, int count,int turn){
        turn -=1;
        if (turn == 0){
            return -100;
        }
        String id = TagMapId(data);
        if (id.equals("create:filter")){
            Message.FM("filter");
            count = FilterSweeper(data,count,turn);
        }
        if (isInBanBlock(id)) {
            data.putString("id", Clear);
            count -=1;
        }
        return count;
    }
}
