package com.Pink_Cats.createschematicchecker.core;

import com.Pink_Cats.createschematicchecker.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.*;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigValue.ChainSplit;
import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigValue.KnifeSplit;
import static com.Pink_Cats.createschematicchecker.core.BlockSweeper.Clear;
import static com.Pink_Cats.createschematicchecker.core.BlockSweeper.Clear$;
import static com.Pink_Cats.createschematicchecker.core.FilterInterface.FilterSweeper;
import static com.Pink_Cats.createschematicchecker.core.FilterInterface.SweeperIfHasId;
import static com.Pink_Cats.createschematicchecker.core.NbtInterFace.*;
import static com.Pink_Cats.createschematicchecker.core.StrFunc.isInBanBlock;

public class MagicChain {


    public static Map<String,Object> MagicChainClear(CompoundTag data,String chain,int find_count,String type){
        HashMap<String,Object> magicChain = new HashMap<>();
        String[] BaseChain = ChainSplit(chain);
        Message.FM("BaseChain"+ Arrays.toString(BaseChain));
        ArrayList<Object> ResultTagList = new ArrayList<>();
        ResultTagList.add(data);

        //Is a magic
        Message.FM("EngineStart");
        Message.FM(data);
        ArrayList<CompoundTag> ResultCompoundTag = S_TagList(MagicEngine(ResultTagList,BaseChain));
        for (CompoundTag tag : ResultCompoundTag) {
            if (type.equals("id"))
                {
                    find_count = SweeperIfHasId(tag,find_count);
                }


            if (type.contains("operate"))
                {
                    Message.FM("operate");
                    String[] operate_chain = ChainSplit(type);
                    for (int index = 1; index < operate_chain.length; index++) {

                        String[] knife = KnifeSplit(operate_chain[index]);
                        if (operate_chain[index].contains("clear")) {
                            tag.remove(knife[1]);
                        }

                        if (operate_chain[index].contains("replace")) {
                            tag.put(knife[1],TagString(knife[2])) ;
                        }


                        if (operate_chain[index].contains("limit")) {
                            Message.FM(tag);
                            Message.FM(Arrays.toString(knife));
                            int DownLimit = StringToInt(knife[2]);
                            int UpLimit = StringToInt(knife[3]);
                            Message.FM(DownLimit+" "+UpLimit);
                            int ActualCount = StringToInt(Objects.requireNonNull(tag.get(knife[1])).toString());
                            if (ActualCount>UpLimit){
                                tag.put(knife[1],TagInt(UpLimit) );
                            } else if (ActualCount < DownLimit) {
                                tag.put(knife[1],TagInt(DownLimit) );
                            }
                        }

                        Message.FM("operate result"+tag);



                    }
                }

        }

        magicChain.put("data",data);
        magicChain.put("find_count",find_count);
        return magicChain;
    }




    public static ArrayList<Object> MagicEngine(ArrayList<Object> DataArray,String[] Chain){
        ArrayList<Object> ResultList = new ArrayList<>();

        for (Object TagItem : DataArray) {
            for(int i=0;i<Chain.length;i++){
                boolean IsArrayList = false;
                if (Chain[i].contains("$")){
                    IsArrayList = true;
                    Chain[i] = Clear$(Chain[i]);
                }


                Message.FM(Chain[i] + IsArrayList);
                if (Chain.length==i+1)
                {
                    Message.FM("Is the last chain");
                    if (TagItem instanceof CompoundTag CompoundTagItem){
                        ResultList.add(CompoundTagItem);
                        //Message.FM(ResultList);

                    }
                }
                else {
                    if (TagItem instanceof CompoundTag CompoundTagItem){
                        if ( IsArrayList ) {


                            //inside Engine

                            String[] ListChain = new String[Chain.length-i-1];;
                            // 将元素添加到新的字符串列表中
                            System.arraycopy(Chain, i+1, ListChain, 0, Chain.length - i-1);
                            //Message.FM("ListChain"+ Arrays.toString(ListChain));

                            Message.FM("before_list"+CompoundTagItem);

                            ListTag ordered_stack_list = CompoundTagItem.getList(Chain[i], 10);




                            Message.FM("ordered_stack_list"+ordered_stack_list);

                            ArrayList<Object> EntryList = new ArrayList<>();
                            for (int j = 0; j < ordered_stack_list.size(); j++) {

                                CompoundTag entity = ordered_stack_list.getCompound(j);

                                EntryList.add(entity);
                                Message.FM("entity"+entity);
                            }
                            //Message.FM("EntryList"+EntryList);
                            ResultList = MagicEngine(EntryList,ListChain);
                            //Message.FM("End from inside Engine");
                            break;

                        }

                        else  {
                            //Message.FM("pick start"+CompoundTagItem);
                            //Message.FM(Chain[i]);
                            TagItem = CompoundTagItem.getCompound(Chain[i]);
                            Message.FM("pick result"+ TagItem);                             //Debug is here
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