package com.Pink_Cats.createschematicchecker.core.BlueEngine;

import com.Pink_Cats.createschematicchecker.core.attach.ConveyorDegree;
import com.Pink_Cats.createschematicchecker.lang.Message;
import com.pinkcats.torque.layer.net.minecraft.nbt.CompoundTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.ListTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.Nbt;
import com.pinkcats.torque.layer.net.minecraft.nbt.Tag;

import java.util.*;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.BlockSweeper.ClearBanBlock;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.BlockSweeper.TagSweepHelper;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.*;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.TagFunc.BlockGetId;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.TagFunc.PaletteGetId;
import static com.Pink_Cats.createschematicchecker.core.ChainEngine.ConveyorInterface.StringPickHalfPos;
import static com.Pink_Cats.createschematicchecker.core.ChainEngine.ConveyorInterface.StringPickPos;
import static com.Pink_Cats.createschematicchecker.core.attach.Math.StringToInt;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

public class NbtFunc {
    boolean TEST_DEBUG_ID = false;

    public Map<String, Object> NBTCheck(CompoundTag nbt_data, List<String> CheatLog) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> paletteItemResult;
        Map<String, Object> blockResult;
        Map<String, Object> entityResult;

        boolean Cheat = false;
        boolean Problem = false;
        boolean IsNotMatch = false;
        boolean CannotCheck = false;
        try {
            ListTag palette = nbt_data.getList("palette", 10);
            for (int i = 0; i < palette.size(); i++) {
                CompoundTag paletteItem = palette.getCompound(i);
                paletteItemResult = BaseBlockHandle(paletteItem, "palette", palette, i, CheatLog);
                paletteItem = S_tag(paletteItemResult.get("Data"));
                Cheat = S_bool(paletteItemResult.get("Cheat")) || Cheat;

                IsNotMatch = S_bool(paletteItemResult.get("IsNotMatch")) || IsNotMatch;
                if (paletteItem != null) {
                    palette.set(i, paletteItem);
                }
            }

            ListTag blocks = nbt_data.getList("blocks", 10);

            List<Object[]> beltList = new ArrayList<>();

            List<int[]> Location = new ArrayList<>();
            List<List<int[]>> Destination = new ArrayList<>();

            List<int[]> CrafterPos = new ArrayList<>();
            List<int[]> CrafterPosFlow = new ArrayList<>();
            List<ListTag> CrafterBlocks = new ArrayList<>();
            List<Integer> IsCraftController = new ArrayList<>();

            int ControllerCount = 0;
            int FindTankCount = 0;

            for (int i = 0; i < blocks.size(); i++) {
                try {
                    CompoundTag block = blocks.getCompound(i);
                    String id = BlockGetId(block, palette);
                    blockCounts.put(id, blockCounts.getOrDefault(id, 0) + 1);
                    Message.diag("[Diag][NbtFunc][BLOCK] index=" + i
                            + ", id=" + id
                            + ", size=" + block.size()
                            + ", pos=" + block.get("pos"));

                    if (id.equals("minecraft:sign")) {
                        String nbt = Objects.requireNonNull(block.toString());
                        if (nbt.contains("run_command")) {
                            Cheat = true;
                            CheatLog.add(translateDirect("console.cheat.sign") + "[" + nbt + "]");
                        }
                    }

                    if (id.equals("createbigcannons:fuzed_block")) {
                        String InsideID = Objects.requireNonNull(block.getCompound("nbt").getCompound("Fuze").get("id")).toString();
                        if (!InsideID.contains("createbigcannons:")) {
                            Cheat = true;
                            CheatLog.add(translateDirect("console.cheat.createbigcannons") + "[" + InsideID + "]");
                        }
                    }

                    if (id.equals("create:clipboard")) {
                        String nbt = Objects.requireNonNull(block.toString());
                        boolean hasAttributeModifiers = nbt.contains("AttributeModifiers:");
                        boolean hasAttributeName = nbt.contains("AttributeName:");
                        boolean hasUsingConvertsTo = nbt.contains("using_converts_to");
                        boolean hasBundleContents = nbt.contains("bundle_contents");
                        boolean hasModifiers = nbt.contains("modifiers:");
                        boolean hasInfinityAmount = nbt.contains("amount:infinityd");
                        boolean hasContainer = nbt.contains("minecraft:container");
                        Message.diag("[Diag][NbtFunc][CLIPBOARD] index=" + i + ", nbt=" + nbt);
                        Message.diag("[Diag][NbtFunc][CLIPBOARD_FLAGS] index=" + i
                                + ", AttributeModifiers=" + hasAttributeModifiers
                                + ", AttributeName=" + hasAttributeName
                                + ", using_converts_to=" + hasUsingConvertsTo
                                + ", bundle_contents=" + hasBundleContents
                                + ", modifiers=" + hasModifiers
                                + ", amount:infinityd=" + hasInfinityAmount
                                + ", minecraft:container=" + hasContainer);

                        if (hasAttributeModifiers || hasAttributeName ||
                                hasUsingConvertsTo ||
                                hasBundleContents ||
                                hasModifiers ||
                                hasInfinityAmount ||
                                hasContainer) {
                            Cheat = true;
                            CheatLog.add(translateDirect("console.cheat.clipboard"));
                            CheatLog.add("-閵?" + nbt);
                        }
                    }

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

                    if (id.equals("create:chain_conveyor")) {
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
                            Destination.add(connectionList);
                            Location.add(SelfPos);
                        }
                    }

                    if (id.equals("create:mechanical_crafter")) {
                        int[] SelfPos = StringPickPos(String.valueOf(block.get("pos")));
                        String Input = String.valueOf(block.getCompound("nbt").getCompound("ConnectedInput").get("Controller")).replaceAll("[b]", "");
                        int IsController = StringToInt(Input);
                        CompoundTag connection = block.getCompound("nbt").getCompound("ConnectedInput");
                        ListTag Connections = (ListTag) connection.get("Data");

                        CrafterPos.add(SelfPos);
                        CrafterBlocks.add(Connections);
                        IsCraftController.add(IsController);
                        if (IsController == 1) {
                            for (int j = 1; j < Connections.size(); j++) {
                                CrafterPosFlow.add(SelfPos);
                            }
                        }
                    }

                    if (id.equals("create:fluid_tank")) {
                        FindTankCount += 1;
                        Tag Size = block.getCompound("nbt").get("Size");
                        Tag Height = block.getCompound("nbt").get("Height");
                        if (Size != null && Height != null) {
                            ControllerCount = ControllerCount + (StringToInt(Size.toString()) * StringToInt(Size.toString()) * StringToInt(Height.toString()));
                        }
                    }

                    if (id.contains("copycats:")) {
                        if (!block.getCompound("nbt").toString().equals("{}")) {
                            List<String> fake_id = new ArrayList<String>();
                            fake_id.add("minecraft:air");
                            List<String> consume_id = new ArrayList<String>();

                            CompoundTag material = block.getCompound("nbt").getCompound("material_data");
                            if (material.isEmpty()) {
                                material = block.getCompound("nbt").getCompound("Material");

                                String inside_material = NoQuotes(Objects.requireNonNull(material.get("Name")).toString());

                                CompoundTag Item = block.getCompound("nbt")
                                        .getCompound("Item");

                                if (!Item.toString().equals("{}")) {
                                    Tag count = Item.get("Count");
                                    if (count == null) {
                                        count = Item.get("count");
                                    }

                                    int consumedItem_count = StringToInt(
                                            count.toString().replaceAll("[b;]", ""));

                                    String consumedItem_id = NoQuotes(block.getCompound("nbt")
                                            .getCompound("Item").get("id").toString());

                                    if (!inside_material.equals(consumedItem_id) || consumedItem_count > 1 || consumedItem_count < 0) {
                                        if (!consumedItem_id.equals("minecraft:air")) {
                                            CompoundTag replace = block.getCompound("nbt")
                                                    .getCompound("Item");
                                            replace.put("id", TagString("minecraft:air"));

                                            CompoundTag replace2 = block.getCompound("nbt").getCompound("Material");
                                            replace2.put("Name", TagString("minecraft:air"));

                                            CheatLog.add(translateDirect("console.cheat.copycats") + "[" + consumedItem_count + "|1][" + consumedItem_id + "|" + inside_material + "]");
                                            Cheat = true;
                                        }
                                    }
                                }
                            } else {
                                for (String key : material.getAllKeys()) {
                                    CompoundTag plastic = material.getCompound(key);
                                    String inside_material = NoQuotes(Objects.requireNonNull(plastic.getCompound("material").get("Name")).toString());

                                    if (!fake_id.contains(inside_material)) {
                                        fake_id.add(inside_material);
                                    } else {
                                        continue;
                                    }
                                    CompoundTag consumedItem_inside = plastic.getCompound("consumedItem");
                                    if (!consumedItem_inside.isEmpty()) {
                                        String consumedItem_id = NoQuotes(consumedItem_inside.get("id").toString());
                                        Tag inside_count = consumedItem_inside.get("Count");
                                        if (inside_count == null) {
                                            inside_count = consumedItem_inside.get("count");
                                        }

                                        int consumedItem_count = StringToInt(
                                                inside_count
                                                        .toString()
                                                        .replaceAll("[b;]", ""));

                                        if (!fake_id.contains(consumedItem_id) || consumedItem_count > 1 || consumedItem_count < 0) {
                                            if (!consumedItem_id.equals("minecraft:air")) {
                                                CompoundTag replace = plastic.getCompound("consumedItem");
                                                replace.put("id", TagString("minecraft:air"));
                                                CompoundTag replace2 = plastic.getCompound("material");
                                                replace2.put("Name", TagString("minecraft:air"));

                                                CheatLog.add(translateDirect("console.cheat.copycats") + "[" + consumedItem_count + "|1][" + consumedItem_id + "|" + inside_material + "]");
                                                Cheat = true;
                                            }
                                        }
                                    }
                                }
                                fake_id.removeIf("minecraft:air"::equals);
                                if (hasDuplicate(fake_id)) {
                                    CheatLog.add(translateDirect("console.cheat.copycats.count"));
                                    Cheat = true;
                                }
                            }
                        }
                    }

                    if (block.size() > 2) {
                        String beforeBlock = block.toString();

                        blockResult = BaseBlockHandle(block, "block", palette, i, CheatLog);

                        block = S_tag(blockResult.get("Data"));
                        Cheat = S_bool(blockResult.get("Cheat")) || Cheat;
                        IsNotMatch = S_bool(blockResult.get("IsNotMatch")) || IsNotMatch;
                        Message.diag("[Diag][NbtFunc][BASE_BLOCK_RESULT] index=" + i
                                + ", id=" + id
                                + ", resultCheat=" + S_bool(blockResult.get("Cheat"))
                                + ", resultIsNotMatch=" + S_bool(blockResult.get("IsNotMatch"))
                                + ", changed=" + (block != null && !beforeBlock.equals(block.toString())));
                    }

                    if (block != null) {
                        blocks.set(i, block);
                    }
                } catch (Exception e) {
                    if (enable_debug) {
                        e.printStackTrace();
                    }

                    Message.FE("ErrorBlock: [" + i + "] Reason: " + e.getMessage());
                    CannotCheck = true;
                    break;
                }
            }

            boolean BeltMismatch = false;
            int BeltCountMismatch = 0;
            List<String> MismatchController = new ArrayList<String>();
            if (check_belt) {
                for (Object[] item : beltList) {
                    if (item[1] != item[3]) {
                        BeltMismatch = true;
                        BeltCountMismatch++;
                        MismatchController.add(item[0].toString());
                    }
                }
            }
            if (BeltCountMismatch > maxBeltCheatLimit) {
                CheatLog.add(translateDirect("console.cheat.belt.count") + "[" + maxBeltCheatLimit + "|" + BeltCountMismatch + "]");
                Cheat = true;
            }

            if (remove_belt_instead_kill) {
                if (BeltMismatch) {
                    for (int i = 0; i < blocks.size(); i++) {
                        CompoundTag block = blocks.getCompound(i);
                        String id = BlockGetId(block, palette);
                        if (id.equals("create:belt")) {
                            String Controller = String.valueOf(block.getCompound("nbt").getCompound("Controller"));
                            if (MismatchController.contains(Controller)) {
                                blocks.remove(i);
                                i -= 1;
                            }
                        }
                    }
                }
            } else {
                if (BeltMismatch) {
                    CheatLog.add(translateDirect("console.cheat.belt.mismatch"));
                    Cheat = true;
                }
            }

            List<String> mismatch_conveyor_controller = new ArrayList<String>();
            List<String> mismatch_conveyor_destination = new ArrayList<String>();
            for (int i = 0; i < Location.size(); i++) {
                for (int[] item : Destination.get(i)) {
                    double AB = new ConveyorDegree(item[0], item[2]).calculateHypotenuse() - 2;
                    ConveyorDegree triangle = new ConveyorDegree(AB, item[1]);
                    double angleA = triangle.calculateAngleA();

                    if (angleA > max_conveyor_degree + 1) {
                        Cheat = true;
                        CheatLog.add(translateDirect("console.cheat.conveyor.angle") + " " + String.format("%.2f", Math.abs(angleA)) + "鎺?");
                    }
                    int Length = Math.max(
                            Math.max(
                                    Math.abs(item[0]),
                                    Math.abs(item[2])
                            ), Math.abs(item[1])
                    );

                    if (Length > maxConveyorCheatDistanceLimit) {
                        Cheat = true;
                        CheatLog.add(translateDirect("console.cheat.conveyor.distance") + "[" + Length + "|" + maxConveyorCheatDistanceLimit + "]");
                    }

                    item[0] = item[0] + Location.get(i)[0];
                    item[1] = item[1] + Location.get(i)[1];
                    item[2] = item[2] + Location.get(i)[2];

                    boolean pos_match = false;
                    for (int[] Pos : Location) {
                        if ((Arrays.equals(item, Pos))) {
                            pos_match = true;
                            break;
                        }
                    }
                    if (!pos_match) {
                        mismatch_conveyor_controller.add(Arrays.toString(Location.get(i)));
                        item[0] = item[0] - Location.get(i)[0];
                        item[1] = item[1] - Location.get(i)[1];
                        item[2] = item[2] - Location.get(i)[2];
                        mismatch_conveyor_destination.add(Arrays.toString(item));
                    }
                }
            }

            if (mismatch_conveyor_controller.size() > maxConveyorCheatLimit) {
                CheatLog.add(translateDirect("console.cheat.conveyor.limit") + "[" + mismatch_conveyor_controller.size() + "|" + maxConveyorCheatLimit + "]");
                Cheat = true;
            }
            if (!mismatch_conveyor_controller.isEmpty()) {
                for (int i = 0; i < blocks.size(); i++) {
                    CompoundTag block = blocks.getCompound(i);
                    String id = BlockGetId(block, palette);
                    if (id.equals("create:chain_conveyor")) {
                        String Pos = Arrays.toString(StringPickPos(String.valueOf(block.get("pos"))));
                        for (String s : mismatch_conveyor_controller) {
                            if (s.equals(Pos)) {
                                ListTag Connections = (ListTag) block.getCompound("nbt").get("Connections");
                                for (int index2 = 0; index2 < mismatch_conveyor_destination.size(); index2++) {
                                    Connections.remove(index2);
                                    index2 -= 1;
                                }
                            }
                        }
                    }
                }
            }

            CrafterPos.addAll(CrafterPosFlow);

            List<int[]> CrafterPosBack = new ArrayList<>();
            for (int[] arr : CrafterPos) {
                int[] newArr = Arrays.copyOf(arr, arr.length);
                CrafterPosBack.add(newArr);
            }

            try {
                boolean CraftMisMatch = false;
                if (!CrafterPos.isEmpty()) {
                    for (int index = 0; index < IsCraftController.size(); index++) {
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

                                boolean PosMisMatch = false;
                                for (int index5 = 0; index5 < CrafterPosBack.size(); index5++) {
                                    if (Arrays.equals(CrafterPosBack.get(index5), MatchBlockPos)) {
                                        PosMisMatch = false;
                                        CrafterPosBack.remove(index5);
                                        break;
                                    }
                                    PosMisMatch = true;
                                }
                                if (PosMisMatch) {
                                    CraftMisMatch = true;
                                }
                            }
                        }

                        if (IsCraftController.get(index) == 1) {
                            boolean Attach;
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
                                    CheatLog.add(translateDirect("console.cheat.crafter.attach"));
                                }
                            }
                        }
                    }
                    if (!CrafterPosBack.isEmpty()) {
                        CraftMisMatch = true;
                        CheatLog.add(translateDirect("console.cheat.crafter.mismatch"));
                    }

                    if (CraftMisMatch) {
                        Cheat = true;
                    }
                }
            } catch (Exception ex) {
                if (enable_debug) {
                    ex.printStackTrace();
                }
            }

            if (ControllerCount != FindTankCount) {
                if (remove_fluid_tank_instead_kill) {
                    int removedTankCount = 0;
                    for (int i = 0; i < blocks.size(); i++) {
                        CompoundTag block = blocks.getCompound(i);
                        String id = BlockGetId(block, palette);
                        if (id.equals("create:fluid_tank")) {
                            blocks.remove(i);
                            i -= 1;
                            removedTankCount++;
                        }
                    }
                    CheatLog.add(translateDirect("console.cheat.FluidTank.mismatch") + "[" + ControllerCount + "|" + FindTankCount + "]");
                    CheatLog.add("[FluidTank] cleared mismatched tanks: " + removedTankCount);
                } else {
                    CheatLog.add(translateDirect("console.cheat.FluidTank.mismatch") + "[" + ControllerCount + "|" + FindTankCount + "]");
                    Cheat = true;
                }
            }

            ListTag entities = nbt_data.getList("entities", 10);
            boolean IsEntityKilled = false;
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entity = entities.getCompound(i);
                entityResult = BaseBlockHandle(entity, "entity", palette, i, CheatLog);
                Object entityRes = entityResult.get("Data");
                if (entityRes.toString().equals("{}")) {
                    entity = Nbt.newCompoundTag();
                    IsEntityKilled = true;
                    entities.remove(i);
                    i -= 1;
                } else {
                    entity = S_tag(entityRes);
                }
                Cheat = S_bool(entityResult.get("Cheat")) || Cheat;
                if (!IsEntityKilled) {
                    if (kill_entity) {
                        String entityId = NoQuotes(entity.getCompound("nbt").get("id").toString());
                        if (IsWhitelistEntity(entityId)) {
                            if (!entityId.equals("create:super_glue")) {
                                Message.FE(translateDirect("csc.kill.whitelist.ignore") + entityId);
                            }
                        } else {
                            entities.remove(entity);
                        }
                    }
                }
            }

            nbt_data.put("blocks", blocks);
            nbt_data.put("palette", palette);
            nbt_data.put("entities", entities);
            if (debug_total_block) {
                Message.FP(translateDirect("console.debug.itemDetail"));
                for (Map.Entry<String, Integer> entry : blockCounts.entrySet()) {
                    Message.FP("ID: " + entry.getKey() + ", Count: " + entry.getValue());
                }
                blockCounts.clear();
            }
        } catch (Exception e) {
            if (enable_debug) {
                e.printStackTrace();
            }
            Message.FE(e.getMessage());
            CannotCheck = true;
        }
        if (Cheat) {
            Problem = true;
        }

        if (IsNotMatch) {
            Problem = true;
        }

        result.put("CannotCheck", CannotCheck);
        result.put("Problem", Problem);
        result.put("nbt_data", nbt_data);
        result.put("Cheat", Cheat);
        Message.diag("[Diag][NbtFunc][FINAL] cheat=" + Cheat
                + ", problem=" + Problem
                + ", cannotCheck=" + CannotCheck
                + ", isNotMatch=" + IsNotMatch);
        return result;
    }

    Map<String, Integer> blockCounts = new HashMap<>();

    public Map<String, Object> BaseBlockHandle(CompoundTag data, String type, ListTag PaletteBlockData, int sequence, List<String> CheatLog) {
        boolean Cheat = false;
        boolean HasBanBlock = false;
        boolean IsNotMatch = false;
        Map<String, Object> result = new HashMap<>();
        String beforeData = data == null ? "null" : data.toString();

        if (type.equals("entity")) {
            if (!data.isEmpty()) {
                String EntityId = BlockGetId(data, PaletteBlockData);
                if (IsBanEntity(EntityId)) {
                    data = Nbt.newCompoundTag();
                }

                if (EntityId.equals("create:super_glue")) {
                    float[] From = StringPickHalfPos(data
                            .getCompound("nbt")
                            .get("From")
                            .toString()
                            .replaceAll("d", "")
                    );

                    float[] To = StringPickHalfPos(data
                            .getCompound("nbt")
                            .get("To")
                            .toString()
                            .replaceAll("d", "")
                    );
                    float X = Math.abs(From[0] - To[0]);
                    float Y = Math.abs(From[1] - To[1]);
                    float Z = Math.abs(From[2] - To[2]);
                    if (X > 24 || Y > 24 || Z > 24) {
                        data = Nbt.newCompoundTag();
                        Message.FE("Super Glue To big!");
                        Message.FE(data);
                    }
                }
            }
        }

        String HasTag = HasBanTag(data.toString());
        if (!HasTag.isEmpty()) {
            IsNotMatch = TagSweepHelper(data, HasTag);
        }

        if (HasBanBlock(data.toString())) {
            HasBanBlock = true;
        }

        Map<String, Object> MapData;

        if (!type.equals("entity")) {
            MapData = ClearBanBlock(data, "rule." + type, sequence, PaletteBlockData);
            data = S_tag(MapData.get("Data"));
            IsNotMatch = S_bool(MapData.get("IsNotMatch")) || IsNotMatch;
        }

        if (type.equals("block")) {
            String id = BlockGetId(data, PaletteBlockData);
            if (white_list_mod_enable) {
                if (!isIsWhiteListMod(id)) {
                    data = Nbt.newCompoundTag();
                }
            }

            if (TEST_DEBUG_ID) {
                Message.FE("ID: " + id);
            }

            if (HasBanBlock) {
                MapData = ClearBanBlock(data, "block", sequence, PaletteBlockData);
                data = S_tag(MapData.get("Data"));
                IsNotMatch = S_bool(MapData.get("IsNotMatch")) || IsNotMatch;
            }
        }
        if (type.equals("palette")) {
            String id = PaletteGetId(data);
            if (white_list_mod_enable) {
                if (!isIsWhiteListMod(id)) {
                    CompoundTag Properties = null;
                    if (data != null) {
                        Properties = data.getCompound("Properties");
                        for (int para = 0; para < 50; para++) {
                            boolean next = false;
                            for (String key : Properties.getAllKeys()) {
                                String TagValue = Properties.get(key).toString();
                                if (TagValue.contains("true") || TagValue.contains("false")) {
                                    Properties.remove(key);
                                    next = true;
                                    break;
                                }
                                if (isValidTagValue(TagValue)) {
                                    Properties.remove(key);
                                    next = true;
                                    break;
                                }
                            }
                            if (!next) {
                                break;
                            }
                        }
                    }
                }
            }

            if (id.equals("create:track")) {
                CompoundTag Properties = data.getCompound("Properties");
                String track_shape = NoQuotes(data.getCompound("Properties")
                        .get("shape").toString());

                if (track_shape.equals("none") || track_shape.equals("NONE")) {
                    Properties.put("shape", TagString("xo"));
                    Message.FE(translateDirect("csc.trams.track_problem"));
                }
            }

            if (HasBanBlock) {
                MapData = ClearBanBlock(data, "palette", sequence, PaletteBlockData);
                data = S_tag(MapData.get("Data"));
                IsNotMatch = S_bool(MapData.get("IsNotMatch")) || IsNotMatch;
            }
        }

        if (IsNotMatch) {
            data = Nbt.newCompoundTag();
        }

        result.put("Cheat", Cheat);
        result.put("Data", data);
        result.put("IsNotMatch", IsNotMatch);
        Message.diag("[Diag][NbtFunc][BASE_HANDLE] type=" + type
                + ", sequence=" + sequence
                + ", cheat=" + Cheat
                + ", isNotMatch=" + IsNotMatch
                + ", changed=" + !Objects.equals(beforeData, data == null ? "null" : data.toString()));
        return result;
    }

    private static boolean isIsWhiteListMod(String id) {
        boolean Is_WhiteListMod = false;
        for (String modID : SortModId(white_list_mod)) {
            if (id.contains(modID)) {
                Is_WhiteListMod = true;
                break;
            }
        }
        return Is_WhiteListMod;
    }

    public static boolean isValidTagValue(String tagValue) {
        return tagValue.matches("[0-9\"']*");
    }

    public static String[] SortModId(String[] whiteList) {
        return Arrays.stream(whiteList)
                .map(modID -> modID + ":")
                .toArray(String[]::new);
    }
}
