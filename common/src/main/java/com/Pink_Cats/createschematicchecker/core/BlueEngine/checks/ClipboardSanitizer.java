package com.Pink_Cats.createschematicchecker.core.BlueEngine.checks;

import com.pinkcats.torque.layer.net.minecraft.nbt.CompoundTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.ListTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ClipboardSanitizer {
    private static final Set<String> CLIPBOARD_NBT_KEYS = new HashSet<>(Arrays.asList("id", "components"));
    private static final Set<String> CLIPBOARD_COMPONENT_KEYS = new HashSet<>(Arrays.asList(
            "minecraft:custom_name",
            "create:clipboard_content"
    ));
    private static final Set<String> CLIPBOARD_CONTENT_KEYS = new HashSet<>(Arrays.asList(
            "previously_opened_page",
            "pages",
            "type",
            "read_only"
    ));
    private static final Set<String> CLIPBOARD_ENTRY_KEYS = new HashSet<>(Arrays.asList(
            "icon",
            "checked",
            "item_amount",
            "text"
    ));
    private static final Set<String> CLIPBOARD_ITEM_KEYS = new HashSet<>(Arrays.asList("id", "count", "Count"));
    private static final Set<String> CLIPBOARD_TEXT_KEYS = new HashSet<>(Arrays.asList(
            "text",
            "translate",
            "color",
            "extra",
            "hoverEvent",
            "italic",
            "bold",
            "underlined",
            "strikethrough",
            "obfuscated",
            "insertion",
            "fallback",
            "with"
    ));
    private static final Set<String> CLIPBOARD_HOVER_KEYS = new HashSet<>(Arrays.asList("action", "contents"));

    private ClipboardSanitizer() {
    }

    public static boolean sanitize(CompoundTag block) {
        boolean changed = false;
        CompoundTag nbt = block.getCompound("nbt");
        if (nbt == null || nbt.isEmpty()) {
            return false;
        }

        changed = removeKeysExcept(nbt, CLIPBOARD_NBT_KEYS) || changed;

        Tag componentsTag = nbt.get("components");
        if (componentsTag == null) {
            return changed;
        }

        if (!(componentsTag instanceof CompoundTag)) {
            nbt.remove("components");
            return true;
        }

        CompoundTag components = (CompoundTag) componentsTag;
        changed = removeKeysExcept(components, CLIPBOARD_COMPONENT_KEYS) || changed;
        changed = sanitizeContent(components) || changed;
        if (components.isEmpty()) {
            nbt.remove("components");
            changed = true;
        }

        return changed;
    }

    private static boolean sanitizeContent(CompoundTag components) {
        boolean changed = false;
        Tag contentTag = components.get("create:clipboard_content");
        if (contentTag == null) {
            return false;
        }

        if (!(contentTag instanceof CompoundTag)) {
            components.remove("create:clipboard_content");
            return true;
        }

        CompoundTag content = (CompoundTag) contentTag;
        changed = removeKeysExcept(content, CLIPBOARD_CONTENT_KEYS) || changed;

        Tag pagesTag = content.get("pages");
        if (pagesTag instanceof ListTag) {
            for (Tag pageTag : (ListTag) pagesTag) {
                if (pageTag instanceof ListTag) {
                    for (Tag entryTag : (ListTag) pageTag) {
                        if (entryTag instanceof CompoundTag) {
                            changed = sanitizeEntry((CompoundTag) entryTag) || changed;
                        }
                    }
                }
            }
        } else if (pagesTag != null) {
            content.remove("pages");
            changed = true;
        }

        return changed;
    }

    private static boolean sanitizeEntry(CompoundTag entry) {
        boolean changed = removeKeysExcept(entry, CLIPBOARD_ENTRY_KEYS);

        Tag iconTag = entry.get("icon");
        if (iconTag instanceof CompoundTag) {
            changed = sanitizeItem((CompoundTag) iconTag) || changed;
        } else if (iconTag != null) {
            entry.remove("icon");
            changed = true;
        }

        Tag textTag = entry.get("text");
        if (textTag instanceof CompoundTag) {
            changed = sanitizeText((CompoundTag) textTag) || changed;
        }

        return changed;
    }

    private static boolean sanitizeItem(CompoundTag item) {
        return removeKeysExcept(item, CLIPBOARD_ITEM_KEYS);
    }

    private static boolean sanitizeText(CompoundTag text) {
        boolean changed = removeKeysExcept(text, CLIPBOARD_TEXT_KEYS);

        Tag extraTag = text.get("extra");
        if (extraTag instanceof ListTag) {
            for (Tag child : (ListTag) extraTag) {
                if (child instanceof CompoundTag) {
                    changed = sanitizeText((CompoundTag) child) || changed;
                }
            }
        } else if (extraTag != null) {
            text.remove("extra");
            changed = true;
        }

        Tag hoverTag = text.get("hoverEvent");
        if (hoverTag instanceof CompoundTag) {
            changed = sanitizeHover((CompoundTag) hoverTag) || changed;
        } else if (hoverTag != null) {
            text.remove("hoverEvent");
            changed = true;
        }

        return changed;
    }

    private static boolean sanitizeHover(CompoundTag hoverEvent) {
        boolean changed = removeKeysExcept(hoverEvent, CLIPBOARD_HOVER_KEYS);

        Tag contentsTag = hoverEvent.get("contents");
        if (contentsTag instanceof CompoundTag) {
            changed = sanitizeItem((CompoundTag) contentsTag) || changed;
        } else if (contentsTag != null && !(contentsTag instanceof ListTag)) {
            hoverEvent.remove("contents");
            changed = true;
        }

        return changed;
    }

    private static boolean removeKeysExcept(CompoundTag tag, Set<String> allowedKeys) {
        boolean changed = false;
        for (String key : new ArrayList<>(tag.getAllKeys())) {
            if (!allowedKeys.contains(key)) {
                tag.remove(key);
                changed = true;
            }
        }
        return changed;
    }
}
