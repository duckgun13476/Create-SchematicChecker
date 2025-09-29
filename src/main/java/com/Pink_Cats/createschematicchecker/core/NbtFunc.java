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

            List<int[]> CrafterPos = new ArrayList<>();
            List<int[]> CrafterPosFlow = new ArrayList<>();
            List<ListTag> CrafterBlocks = new ArrayList<>();
            List<Integer> IsCraftController = new ArrayList<>();


            for (int i = 0; i < blocks.size(); i++) {
                Message.FM("size"+i);
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

                //create:crafter
                Message.FD("id" + id);
                if (id.equals("create:mechanical_crafter")) {
                    Message.FE("crafter");
                    int[] SelfPos = StringPickPos(String.valueOf(block.get("pos")));

                    String Input = String.valueOf(block.getCompound("nbt").getCompound("ConnectedInput").get("Controller")).replaceAll("[b]", "");
                    int IsController = StringToInt(Input);

                    CompoundTag connection = block.getCompound("nbt").getCompound("ConnectedInput");
                    ListTag Connections = (ListTag) connection.get("Data");


                    CrafterPos.add(SelfPos);
                    CrafterBlocks.add(Connections);
                    IsCraftController.add(IsController);
                    Message.FE(Arrays.toString(SelfPos));
                    if (IsController == 1) {
                        Message.FE("Success control" + Connections.size());
                        for (int j=1;j<Connections.size();j++) {
                            CrafterPosFlow.add(SelfPos);
                        }
                    }
                }


                if (block.size() >2){
                    blockResult = BaseBlockHandle(block, "block", palette, i);
                    block = S_tag(blockResult.get("Data"));
                    Cheat = S_bool(blockResult.get("Cheat")) || Cheat;
                }

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
                                if (Connections != null) {
                                    Message.FE("Connections: "+ Connections);
                                }

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

            //Crafter Mismatch Check
            Message.FE("Crafter PosBack");
            for (int[] crafterPo : CrafterPos) {
                Message.FE("Crafter Pos ADD: " + Arrays.toString(crafterPo));
            }

            CrafterPos.addAll(CrafterPosFlow); //add these to end

            List<int[]> CrafterPosBack = new ArrayList<>();
            for (int[] arr : CrafterPos) {
                int[] newArr = Arrays.copyOf(arr, arr.length);
                CrafterPosBack.add(newArr);
            }

            Message.FE("Crafter PosBack");
            for (int[] crafterPo : CrafterPosBack) {
                Message.FE("Crafter Pos Back: " + Arrays.toString(crafterPo));
            }
            try {
                boolean CraftMisMatch = false;
                if (!CrafterPos.isEmpty()) {
                    for (int index = 0; index < IsCraftController.size(); index++) {

                        Message.FE("Pos:" + Arrays.toString(CrafterPos.get(index)) + "|" + IsCraftController.get(index) + "|" + CrafterBlocks.get(index));
                        int[] CorePos = CrafterPos.get(index);


                        List<int[]> bird_finder = new ArrayList<>();


                        for (int index2 = 0; index2 < CrafterBlocks.size(); index2++) {
                            CompoundTag DesBlock = CrafterBlocks.get(index).getCompound(index2);
                            if (!DesBlock.isEmpty()) {

                                int[] DesBlockPos = new int[3];
                                DesBlockPos[0] = StringToInt(Objects.requireNonNull(DesBlock.get("X")).toString());
                                DesBlockPos[1] = StringToInt(Objects.requireNonNull(DesBlock.get("Y")).toString());
                                DesBlockPos[2] = StringToInt(Objects.requireNonNull(DesBlock.get("Z")).toString());

                                if (IsCraftController.get(index) == 1) {
                                    bird_finder.add(DesBlockPos);
                                }
                                int[] MatchBlockPos = new int[3];
                                MatchBlockPos[0] = CorePos[0] + DesBlockPos[0];
                                MatchBlockPos[1] = CorePos[1] + DesBlockPos[1];
                                MatchBlockPos[2] = CorePos[2] + DesBlockPos[2];

                                Message.FE("Destination:" + Arrays.toString(MatchBlockPos));
                                boolean PosMisMatch = false;
                                for (int index5 = 0; index5 < CrafterPosBack.size(); index5++) {
                                    Message.FE("matching:" + Arrays.toString(CrafterPosBack.get(index5)));
                                    Message.FD("current name" );
                                    for (int[] tag :CrafterPosBack){
                                        Message.FD("current: " + Arrays.toString(tag));
                                    }
                                    if (Arrays.equals(CrafterPosBack.get(index5), MatchBlockPos)) {

                                        Message.FE("found");
                                        PosMisMatch = false;
                                        CrafterPosBack.remove(index5);
                                        break;
                                    }
                                    PosMisMatch = true;
                                    Message.FE("not found" + Arrays.toString(CorePos));
                                }


                                Message.FE("CraftMisMatch:  " + CraftMisMatch + "PosMisMatch   " + PosMisMatch);

                                if (PosMisMatch) {
                                    CraftMisMatch = true;
                                }
                                Message.FE("CraftMisMatch:  " + CraftMisMatch + "PosMisMatch   " + PosMisMatch);
                            }
                        }


                        if (IsCraftController.get(index) == 1) {
                            boolean Attach;
                            for (int[] birdPos2 : bird_finder) {
                                Message.FE("Attaching:" + Arrays.toString(birdPos2));}
                            for (int[] birdPos : bird_finder) {
                                Attach = false;
                                for (int[] birdIndex : bird_finder) {
                                    int X = Math.abs(birdPos[0] - birdIndex[0]);
                                    int Y = Math.abs(birdPos[1] - birdIndex[1]);
                                    int Z = Math.abs(birdPos[2] - birdIndex[2]);
                                    if (X + Y + Z <= 1) {
                                        Attach = true;
                                        break;
                                    }
                                }
                                if (!Attach) {
                                    CraftMisMatch = true;
                                    Message.FE("NotAttach:  " + CraftMisMatch);
                                }
                            }
                        }

                    }
                    Message.FE("count  "+ CrafterPosBack.size());
                    if (!CrafterPosBack.isEmpty())
                        CraftMisMatch = true;

                    if (CraftMisMatch) {
                        Message.FE("CraftMisMatch");
                    }


                }
            } catch (Exception ex) {
                ex.printStackTrace();
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


