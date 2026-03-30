package com.Pink_Cats.createschematicchecker.core.ChainEngine;

import com.Pink_Cats.createschematicchecker.lang.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.antlr.runtime.misc.IntArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.ban_block;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigValue.ChainSplit;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigValue.KnifeSplit;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.BlockSweeper.Clear$;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.BlockSweeper.CountToClear;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.FilterInterface.SweeperIfHasId;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.*;
import static com.Pink_Cats.createschematicchecker.core.attach.Math.StringToInt;
import static com.Pink_Cats.createschematicchecker.lang.CSCLanguage.translateDirect;

public class MagicChain {


    private static final Logger log = LoggerFactory.getLogger(MagicChain.class);

    public static Map<String,Object> MagicChainClear(CompoundTag data, String chain, int find_count, String type, String idKeyIn){
        HashMap<String,Object> magicChain = new HashMap<>();
        String[] BaseChain = ChainSplit(chain);
        boolean Cheat = false;
        ArrayList<Object> ResultTagList = new ArrayList<>();
        ResultTagList.add(data);

        //Is a magic
        ArrayList<Object> ResultCompoundTag = MagicEngine(ResultTagList,BaseChain,idKeyIn);

        //ArrayList<CompoundTag> ResultCompoundTag = S_TagList(ChainRes);

        for (Object tag : ResultCompoundTag) {
            if (type.equals("id"))
                {
                    if (tag instanceof CompoundTag tag1) {
                        find_count = SweeperIfHasId(tag1, find_count, 2 + 3);  //3 allow deep count
                        if (find_count == -100) {
                            Message.FE(translateDirect("check.csc.cheat.filter"));
                        }
                    }
                }




            if (type.contains("operate"))
                {
                    String[] operate_chain = ChainSplit(type);
                    for (int index = 1; index < operate_chain.length; index++) {
                        String[] knife = KnifeSplit(operate_chain[index]);
                        if (operate_chain[index].contains("clear")) {
                            if (tag instanceof CompoundTag tag1) {
                                String S_result = tag1.getCompound(knife[1]).toString();
                                int totalCount = CountToClear(S_result,ban_block);
                                find_count = find_count - totalCount;
                                tag1.remove(knife[1]);
                            }
                        }

                        if (operate_chain[index].contains("replace")) {
                            if (tag instanceof CompoundTag tag1) {
                                tag1.put(knife[1], TagString(knife[2]));
                            }
                        }


                        if (operate_chain[index].contains("limit")) {
                            int DownLimit = StringToInt(knife[2]);
                            int UpLimit = StringToInt(knife[3]);


                            int ActualCount = 0;
                            if (tag instanceof CompoundTag tag1) {
                                Tag res = tag1.get(knife[1]);
                                ActualCount = StringToInt(Objects.requireNonNull(res).toString());
                            } else if (tag instanceof int[] tag1) {
                                switch (knife[1]) {
                                    case "X" -> ActualCount = tag1[0];
                                    case "Y" -> ActualCount = tag1[1];
                                    case "Z" -> ActualCount = tag1[2];
                                }
                            } else {
                                //Message.debug(Arrays.toString(tag));
                                throw new IllegalArgumentException("Wrong in Char Match: " + tag.getClass().getName() + " " + String.valueOf(tag));
                            }

                            try {

                                if (ActualCount > UpLimit) {
                                    if (tag instanceof CompoundTag tag1)
                                        tag1.put(knife[1], TagInt(UpLimit));

                                    if (tag instanceof int[] tag1){
                                        switch (knife[1]) {
                                            case "X" ->  tag1[0] = UpLimit;
                                            case "Y" ->  tag1[1] = UpLimit;
                                            case "Z" ->  tag1[2] = UpLimit;
                                        }
                                    }

                                } else if (ActualCount < DownLimit) {
                                    if (tag instanceof CompoundTag tag1)
                                        tag1.put(knife[1], TagInt(DownLimit));

                                    if (tag instanceof int[] tag1){
                                        switch (knife[1]) {
                                            case "X" ->  tag1[0] = DownLimit;
                                            case "Y" ->  tag1[1] = DownLimit;
                                            case "Z" ->  tag1[2] = DownLimit;
                                        }
                                    }

                                }
                            } catch (Exception e) {
                                Message.debug(ResultTagList);
                                Message.debug(Arrays.toString(BaseChain));
                                Message.debug(knife[0] + knife[1] + knife[2]);
                                throw e;
                            }

                        }

                        //Message.FM("operate result"+tag);



                    }
                }

        }

        magicChain.put("data",data);
        magicChain.put("find_count",find_count);
        return magicChain;
    }




    public static ArrayList<Object> MagicEngine(ArrayList<Object> DataArray,String[] Chain,String id){
        ArrayList<Object> ResultList = new ArrayList<>();

        for (Object TagItem : DataArray) {
            for(int i=0;i<Chain.length;i++){

                //if (id.equals("create:mechanical_arm")){
                //    Message.debug("- 0 0" +Arrays.toString(Chain)+"- 0 0"+Chain[i]);
                   // Message.debug("- 0 0" + TagItem +"--"+(Chain.length==i+1));
                //}

                boolean IsArrayList = false;
                if (Chain[i].contains("$")){
                    IsArrayList = true;
                    Chain[i] = Clear$(Chain[i]);
                }


                //Message.FM(Chain[i] + IsArrayList);      // EngineDebug
                if (Chain.length==i+1)
                {
                    //Message.FM("Is the last chain");    //  EngineDebug
                    ResultList.add(TagItem);
                    //Message.FM(ResultList);

                }
                else {
                    //if (Chain[i].equals("X") || Chain[i].equals("Y") || Chain[i].equals("Z")) {
                    //    Message.debug("- 0 0" +Chain[i]);
                    //    Message.debug(TagItem);
                    //}

                    //if (id.equals("create:mechanical_arm")) {
                    //    Message.debug("TagItem 的类型是: " + TagItem.getClass().getName());
                    //    if (TagItem instanceof CompoundTag CompoundTagItem) {
                    //        Message.debug(Arrays.toString(CompoundTagItem.getIntArray("Pos")));
                    //    }
                    //}


                    if (TagItem instanceof CompoundTag CompoundTagItem){
                        if ( IsArrayList ) {


                            //inside Engine

                            String[] ListChain = new String[Chain.length-i-1];;
                            System.arraycopy(Chain, i+1, ListChain, 0, Chain.length - i-1);
                            //Message.FM("ListChain"+ Arrays.toString(ListChain));

                            //Message.FM("before_list"+CompoundTagItem);

                            ListTag ordered_stack_list = CompoundTagItem.getList(Chain[i], 10);




                            //Message.FM("ordered_stack_list"+ordered_stack_list);

                            ArrayList<Object> EntryList = new ArrayList<>();
                            for (int j = 0; j < ordered_stack_list.size(); j++) {

                                CompoundTag entity = ordered_stack_list.getCompound(j);

                                EntryList.add(entity);
                                //Message.FM("entity"+entity);
                            }
                            //Message.FM("EntryList"+EntryList);
                            ResultList = MagicEngine(EntryList,ListChain,id);
                            //Message.FM("End from inside Engine");
                            break;

                        }
                    else  {
                            //Message.FM("pick start"+CompoundTagItem);
                            //Message.FM(Chain[i]);
                            TagItem = CompoundTagItem.getCompound(Chain[i]);
                            if (TagItem.toString().equals("{}")){
                                TagItem = CompoundTagItem.getIntArray(Chain[i]);
                            }

                            //Message.FM("pick result"+ TagItem);                             //Debug is here
                        }
                    }
                }



            }
        }


        return  ResultList;
    }

}
/*
                CompoundTag EncodedRequest = nbt.getCompound("EncodedRequest");
                Message.FM(EncodedRequest);
                //ListTag ordered_stacks = EncodedRequest.getList("ordered_stacks",10);
                CompoundTag ordered_stacks = EncodedRequest.getCompound("ordered_stacks");
                ListTag ordered_stack_list = ordered_stacks.getList("entries", 10);

                for (int i = 0; i < ordered_stack_list.size(); i++) {
                    CompoundTag entity = ordered_stack_list.getCompound(i);
                    Message.FE(entity);
                    CompoundTag item_stack = entity.getCompound("item_stack");
                    Message.FE(item_stack);
                    Tag stack_id = item_stack.get("id");
                    Message.FE(stack_id);
                }
*/