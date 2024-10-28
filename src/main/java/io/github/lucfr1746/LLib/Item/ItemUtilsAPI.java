package io.github.lucfr1746.LLib.Item;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class ItemUtilsAPI {

    private final @NotNull ItemStack itemStack;

    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    public ItemUtilsAPI(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemUtilsAPI setDungeonItem(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            nbtList.setBoolean("dungeon_item", value);
        });
        return this;
    }

    public boolean isDungeonItem() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (!nbtList.hasTag("dungeon_item")) {
                nbtList.setBoolean("dungeon_item", false);
            }
            return nbtList.getBoolean("dungeon_item");
        });
    }

    public ItemUtilsAPI setLoreNumbered(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            nbtList.setBoolean("lore_numbered", value);
        });
        return this;
    }

    public boolean isLoreNumbered() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (!nbtList.hasTag("lore_numbered")) {
                nbtList.setBoolean("lore_numbered", false);
            }
            return nbtList.getBoolean("lore_numbered");
        });
    }

    public ItemUtilsAPI setGlowing(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            nbtList.setBoolean("glowing", value);
        });
        return this;
    }

    public boolean isGlowing() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (!nbtList.hasTag("glowing")) {
                nbtList.setBoolean("glowing", false);
            }
            return nbtList.getBoolean("glowing");
        });
    }

    public ItemUtilsAPI setUnique(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (value) nbtList.setString("unique", UUID.randomUUID().toString());
            else nbtList.setString("unique", "none");
        });
        return this;
    }

    public boolean isUnique() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (!nbtList.hasTag("unique")) {
                nbtList.setString("unique", "none");
                return false;
            }
            return !Objects.equals(nbtList.getString("unique"), "none");
        });
    }

    public ItemUtilsAPI setRecombobulated(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            nbtList.setBoolean("is_Recombobulated", value);
        });
        return this;
    }

    public boolean isRecombobulated() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (!nbtList.hasTag("is_Recombobulated")) {
                nbtList.setBoolean("is_recombobulated", false);
            }
            return nbtList.getBoolean("is_recombobulated");
        });
    }

    public ItemUtilsAPI setTimestamp(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
            if (value) nbtList.setLong("timestamp", System.currentTimeMillis());
            else nbtList.setLong("timestamp", -1L);
        });
        return this;
    }

    public Long getTimestamp() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
            if (!nbtList.hasTag("timestamp")) {
                nbtList.setLong("timestamp", -1L);
            }
            return nbtList.getLong("timestamp");
        });
    }
}
