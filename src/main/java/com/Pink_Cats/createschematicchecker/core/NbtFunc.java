package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.Message;
import net.minecraft.nbt.*;

import java.util.*;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlockSweeper.ClearBanBlock;
import static com.Pink_Cats.createschematicchecker.core.ConveyorInterface.StringPickPos;
import static com.Pink_Cats.createschematicchecker.core.NbtInterFace.*;
import static com.Pink_Cats.createschematicchecker.core.StrFunc.HasBanBlock;
import static com.Pink_Cats.createschematicchecker.core.StrFunc.HasBanTag;
import static com.Pink_Cats.createschematicchecker.core.TagFunc.BlockGetId;
import static com.mojang.text2speech.Narrator.LOGGER;

public class NbtFunc {
    //test para
    boolean DEBUG_ID = false;



    public Map<String, Object> NBTCheck(CompoundTag nbt_data)//校验 NBT
    {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> paletteItemResult;
        Map<String, Object> blockResult;
        Map<String, Object> entityResult;

        boolean Cheat = false;
        //PinkCats Inject
        try {

            // 提取 palette 信息
            ListTag palette = nbt_data.getList("palette", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < palette.size(); i++) {
                CompoundTag paletteItem = palette.getCompound(i);
                paletteItemResult = BaseBlockHandle(paletteItem, "palette", palette, i);
                paletteItem = S_tag(paletteItemResult.get("Data"));
                Cheat = S_bool(paletteItemResult.get("Cheat")) || Cheat;
                if (paletteItem != null) {
                    palette.set(i, paletteItem);
                }
            }

            // 提取 blocks 信息
            ListTag blocks = nbt_data.getList("blocks", 10); // 10 表示 CompoundTag 类型

            List<Object[]> beltList = new ArrayList<>();

            List<int[]> Location = new ArrayList<>();
            List<List<int[]>> Destination = new ArrayList<>();


            for (int i = 0; i < blocks.size(); i++) {
                CompoundTag block = blocks.getCompound(i);
                String id = BlockGetId(block, palette);

                //belt matcher
                if (id.equals("create:belt")) {
                    String Controller = String.valueOf(block.getCompound("nbt").getCompound("Controller"));
                    int Length = StringToInt(String.valueOf(block.getCompound("nbt").get("Length")));
                    int Index = StringToInt(String.valueOf(block.getCompound("nbt").get("Index")));
                    boolean isSame = false;
                    for (Object[] item : beltList) {
                        if (Controller.equals(item[0])) {
                            isSame = true;
                            item[3] = (int) item[3] + 1;
                        }
                    }
                    if (!isSame) {
                        Object[] newItem = {Controller, Length, Index, 1};
                        beltList.add(newItem);
                    }
                }

                //create:chain_conveyor
                if (id.equals("create:chain_conveyor")) {
                    //Message.FE(block);
                    int[] SelfPos = StringPickPos(String.valueOf(block.get("pos")));
                    List<int[]> connectionList = new ArrayList<>();
                    ListTag Connections = (ListTag) block.getCompound("nbt").get("Connections");

                    if (Connections != null) {
                        for (Tag connection : Connections) {
                            String pick_pos = connection.toString();
                            pick_pos = pick_pos.replaceAll("[I;]", "");
                            int[] Connection = StringPickPos(pick_pos);
                            connectionList.add(Connection);

                        }

                    }


                    if (Connections != null) {
                        //Message.FE(Arrays.toString(SelfPos) + "|" + connectionList);
                        Destination.add(connectionList);
                        Location.add(SelfPos);

                    }

                }


                blockResult = BaseBlockHandle(block, "block", palette, i);
                block = S_tag(blockResult.get("Data"));
                Cheat = S_bool(blockResult.get("Cheat")) || Cheat;
                if (block != null) {
                    blocks.set(i, block);
                }
            }


            //belt mismatch check
            boolean BeltMismatch = false;
            int BeltCountMismatch = 0;
            List<String> MismatchController = new ArrayList<>(List.of());
            if (check_belt) {
                for (Object[] item : beltList) {
                    if (item[1] != item[3]) {
                        BeltMismatch = true;
                        BeltCountMismatch++;
                        MismatchController.add(item[0].toString());
                        Message.FE("BeltMismatch Controller: " + item[0] + " | Length: " + item[1] + " | Index: " + item[2] + " | Count: " + item[3]);
                    }
                }
            }
            if (BeltCountMismatch>5){
                Cheat = true;
            }

            //belt mismatch fix
            if (remove_belt_instead_kill) {
                if (BeltMismatch) {
                    Message.FE("BeltMismatch fix");
                    for (int i = 0; i < blocks.size(); i++) {
                        Message.FE("BeltMismatch Controller: " + blocks.get(i).toString());
                        CompoundTag block = blocks.getCompound(i);
                        String id = BlockGetId(block, palette);
                        //belt matcher
                        if (id.equals("create:belt")) {
                            String Controller = String.valueOf(block.getCompound("nbt").getCompound("Controller"));
                            Message.FE(Controller);
                            if (MismatchController.contains(Controller)) {
                                blocks.remove(i);
                                i -= 1;
                            }
                        }
                    }
                }
            }


            //conveyor mismatch
            List<String> mismatch_conveyor_controller = new ArrayList<>(List.of());
            List<String> mismatch_conveyor_destination = new ArrayList<>(List.of());
            Message.FE("conveyor mismatch check");
            for (int i = 0; i < Location.size(); i++) {
                Message.FE(Arrays.toString(Location.get(i)) +"|");
                for (int[] item : Destination.get(i)) {

                    //45 angle check
                    if (Math.max(Math.abs(item[0]),Math.abs(item[2])-2) < Math.abs(item[1]) ) {
                        Cheat = true;
                        Message.FE("Angle Cheat!");
                    }
                    if (
                            Math.max(
                                    Math.max(
                                            Math.abs(item[0]),
                                            Math.abs(item[2])
                                    ),Math.abs(item[1])
                            ) > 50 ) {

                        Cheat = true;
                        Message.FE("Distance Cheat!");
                    }


                    item[0] = item[0] + Location.get(i)[0];
                    item[1] = item[1] + Location.get(i)[1];
                    item[2] = item[2] + Location.get(i)[2];

                    Message.FE( "|+" +Arrays.toString(item) );
                    boolean pos_match = false;
                    for (int[]Pos : Location){
                        if ((Arrays.equals(item, Pos))) {
                            pos_match = true;
                            break;
                        }
                    }
                    if (!pos_match) {
                        Message.FE("Not Match!");
                        mismatch_conveyor_controller.add(Arrays.toString(Location.get(i)));
                        item[0] = item[0] - Location.get(i)[0];
                        item[1] = item[1] - Location.get(i)[1];
                        item[2] = item[2] - Location.get(i)[2];
                        mismatch_conveyor_destination.add(Arrays.toString(item));
                    }

                }

            }

            if (mismatch_conveyor_controller.size()>  10){
                Cheat  = true;
            }
            for (String roller:mismatch_conveyor_controller ){
                Message.FE("roller"+roller);
            }
            //conveyor fix
            if (!mismatch_conveyor_controller.isEmpty()){
                Message.FE("conveyor fix");
                for (int i = 0; i < blocks.size(); i++) {
                    Message.FE("conveyor Mismatch Controller: " + blocks.get(i).toString());
                    CompoundTag block = blocks.getCompound(i);
                    String id = BlockGetId(block, palette);
                    //belt matcher
                    if (id.equals("create:chain_conveyor")) {
                        String Pos = Arrays.toString(StringPickPos(String.valueOf(block.get("pos"))));

                        for (int index = 0; index < mismatch_conveyor_controller.size(); index++) {
                            if (mismatch_conveyor_controller.get(index).equals(Pos)){

                                Message.FE("problem: "+Pos);
                                ListTag Connections = (ListTag) block.getCompound("nbt").get("Connections");
                                Message.FE("Connections: "+Connections.toString());

                                Message.FE("Pick"+ mismatch_conveyor_destination.get(index));
                                for (int index2 = 0; index2 < mismatch_conveyor_destination.size(); index2++) {

                                    String Connection =
                                            Arrays.toString(
                                                    StringPickPos(
                                                            mismatch_conveyor_destination
                                                                    .get(index2)
                                                                    .toString()
                                                                    .replaceAll("[I;]", "")));

                                    Message.FE("inside Connections"+ Connection);
                                    Connections.remove(index2);
                                    index2 -= 1;

                                }




                            }






                        }
                    }
                }
            }






            // 提取 entity 信息
            ListTag entities = nbt_data.getList("entities", 10); // 10 表示 CompoundTag 类型
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entity = entities.getCompound(i);
                entityResult = BaseBlockHandle(entity, "entity", palette, i);
                entity = S_tag(entityResult.get("Data"));
                Cheat = S_bool(entityResult.get("Cheat")) || Cheat;
                if (entity != null) {
                    entities.set(i, entity);
                }
            }

            nbt_data.put("blocks", blocks);
            nbt_data.put("palette", palette);
            nbt_data.put("entities", entities);
            // Output Result
            if (debug_total_block) {
                for (Map.Entry<String, Integer> entry : blockCounts.entrySet()) {
                    Message.FP("ID: " + entry.getKey() + ", Count: " + entry.getValue());
                }
                blockCounts.clear();
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        //Inject limit.
        //Add special matcher here.↑
        result.put("nbt_data", nbt_data);
        result.put("Cheat",Cheat);
        return result;
    }

    Map<String, Integer> blockCounts = new HashMap<>();

    public Map<String,Object> BaseBlockHandle(CompoundTag data,String type,ListTag PaletteBlockData,int sequence) {
        boolean Cheat = false;
        boolean HasBanBlock = false;
        boolean IsMatch  = true;
        Map<String, Object> result = new HashMap<>();

        if (HasBanTag(data.toString())) {
            Message.FM("Find Tag in " + data);
            //Cheat = true;
        }

        if (HasBanBlock(data.toString())) {
            //Message.FM("Find Blacklist Block in " + data);
            HasBanBlock = true;
        }



        Map<String,Object> MapData;


        //clear with rule
        MapData = ClearBanBlock(data, "rule." + type, sequence);
        data = S_tag(MapData.get("Data"));
        IsMatch = S_bool(MapData.get("IsMatch"));


        if (type.equals("block")) {
            String id = BlockGetId(data,PaletteBlockData);
            if (DEBUG_ID) {Message.FE("ID: " + id);}
            blockCounts.put(id, blockCounts.getOrDefault(id, 0) + 1);


            if (HasBanBlock) {
                MapData = ClearBanBlock(data,"block",sequence);
                data = S_tag(MapData.get("Data"));
                IsMatch = S_bool(MapData.get("IsMatch")) && IsMatch;
                //Message.FE(data);
            }




            //Message.FP("create:clipboard".equals(BlockGetId(data)));
            Cheat = false;


        }

        if (type.equals("palette")) {
            if (HasBanBlock) {
                MapData = ClearBanBlock(data,"palette",sequence);
                data = S_tag(MapData.get("Data"));
                IsMatch = S_bool(MapData.get("IsMatch")) && IsMatch;
                Message.FE(data);
            }
            Cheat = false;

        }

        if (type.equals("entity")) {
            if (kill_entity){
                data = new CompoundTag();
            }
        }


        if (Cheat) {
            data = new CompoundTag();
        }

        result.put("Cheat", Cheat);
        result.put("Data", data);
        result.put("IsMatch", IsMatch);
        return result;

    }




}

//Message.FM("Name"+ New_enty);
//CompoundTag New_test = new CompoundTag();
//New_enty.put("Name","create:brass_casing" );
//Message.FM("exist"+ entry);
//Message.FM("new"+ New_enty);
//Message.FE(NbtInterFace.AreTagEquals(entry, New_enty));
//Message.FM("Name"+ New_enty);
//New_enty.remove("Name");
//Message.FM("Name"+ New_enty);
//Message.FM("Palette"+ entry);
//CompoundTag exp = new CompoundTag();
//exp.put("Name",TagString("create:brass_casing"));
//Message.FM("Exist exp "+ exp);
//Message.FE(NbtInterFace.AreTagEquals(entry, exp));


