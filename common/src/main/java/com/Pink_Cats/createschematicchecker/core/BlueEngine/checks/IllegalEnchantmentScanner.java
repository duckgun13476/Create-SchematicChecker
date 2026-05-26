package com.Pink_Cats.createschematicchecker.core.BlueEngine.checks;

import com.pinkcats.torque.layer.net.minecraft.nbt.CompoundTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.ListTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.NoQuotes;

public final class IllegalEnchantmentScanner {
    private static final int MAX_ENCHANTMENT_LEVEL = 40;

    private IllegalEnchantmentScanner() {
    }

    public static int collect(Tag tag, List<String> cheatLog) {
        List<String> illegalEnchantments = new ArrayList<>();
        collect(tag, "", false, illegalEnchantments);
        if (!illegalEnchantments.isEmpty()) {
            cheatLog.add("Illegal enchantment level detected. Max allowed level is "
                    + MAX_ENCHANTMENT_LEVEL + ".");
            cheatLog.addAll(illegalEnchantments);
        }
        return illegalEnchantments.size();
    }

    private static void collect(
            Tag tag,
            String path,
            boolean insideEnchantmentTag,
            List<String> illegalEnchantments
    ) {
        if (tag == null) {
            return;
        }

        if (tag instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag) tag;
            for (String key : compoundTag.getAllKeys()) {
                Tag child = compoundTag.get(key);
                String childPath = path.isEmpty() ? key : path + "." + key;
                boolean childInsideEnchantmentTag = insideEnchantmentTag || isEnchantmentTagKey(key);
                collect(child, childPath, childInsideEnchantmentTag, illegalEnchantments);
            }
            return;
        }

        if (tag instanceof ListTag) {
            ListTag listTag = (ListTag) tag;
            int index = 0;
            for (Tag child : listTag) {
                collect(child, path + "[" + index + "]", insideEnchantmentTag, illegalEnchantments);
                index++;
            }
            return;
        }

        if (insideEnchantmentTag) {
            Integer level = tagAsInteger(tag);
            if (level != null && level > MAX_ENCHANTMENT_LEVEL) {
                illegalEnchantments.add("- " + path + "=" + level);
            }
        }
    }

    private static boolean isEnchantmentTagKey(String key) {
        String lowerKey = key.toLowerCase(Locale.ROOT);
        return lowerKey.equals("enchantments")
                || lowerKey.equals("storedenchantments")
                || lowerKey.equals("minecraft:enchantments")
                || lowerKey.equals("minecraft:stored_enchantments");
    }

    private static Integer tagAsInteger(Tag tag) {
        String rawValue = NoQuotes(tag.toString()).trim();
        if (!rawValue.matches("-?\\d+[bBsSlL]?")) {
            return null;
        }

        String normalizedValue = rawValue.replaceAll("[bBsSlL]$", "");
        try {
            return Integer.parseInt(normalizedValue);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
