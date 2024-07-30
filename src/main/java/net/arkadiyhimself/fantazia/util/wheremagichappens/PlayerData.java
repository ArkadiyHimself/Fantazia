package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

/**
 * This code was politely borrowed from Aizistral
 */
public class PlayerData {
    /**
     * Retrieves the given Tag type from the player's persistent NBT.
     */

    public static Tag getPersistentTag(Player player, String tag, Tag expectedValue) {
        CompoundTag data = player.getPersistentData();
        CompoundTag persistent;

        if (!data.contains(Player.PERSISTED_NBT_TAG)) {
            data.put(Player.PERSISTED_NBT_TAG, (persistent = new CompoundTag()));
        } else {
            persistent = data.getCompound(Player.PERSISTED_NBT_TAG);
        }

        if (persistent.contains(tag))
            return persistent.get(tag);
        else
            //persistent.put(tag, expectedValue);
            return expectedValue;
    }

    /**
     * Remove tag from the player's persistent NBT.
     */

    public static void removePersistentTag(Player player, String tag) {
        CompoundTag data = player.getPersistentData();
        CompoundTag persistent;

        if (!data.contains(Player.PERSISTED_NBT_TAG)) {
            data.put(Player.PERSISTED_NBT_TAG, (persistent = new CompoundTag()));
        } else {
            persistent = data.getCompound(Player.PERSISTED_NBT_TAG);
        }

        if (persistent.contains(tag)) {
            persistent.remove(tag);
        }
    }

    /**
     * Sets the given Tag type to the player's persistent NBT.
     */

    public static void setPersistentTag(Player player, String tag, Tag value) {
        CompoundTag data = player.getPersistentData();
        CompoundTag persistent;

        if (!data.contains(Player.PERSISTED_NBT_TAG)) {
            data.put(Player.PERSISTED_NBT_TAG, (persistent = new CompoundTag()));
        } else {
            persistent = data.getCompound(Player.PERSISTED_NBT_TAG);
        }

        persistent.put(tag, value);
    }

    /**
     * Sets the given boolean tag to the player's persistent NBT.
     */

    public static void setPersistentBoolean(Player player, String tag, boolean value) {
        setPersistentTag(player, tag, ByteTag.valueOf(value));
    }

    /**
     * Retrieves the given boolean tag from the player's persistent NBT.
     */

    public static boolean getPersistentBoolean(Player player, String tag, boolean expectedValue) {
        Tag theTag = getPersistentTag(player, tag, ByteTag.valueOf(expectedValue));
        return theTag instanceof ByteTag ? ((ByteTag) theTag).getAsByte() != 0 : expectedValue;
    }

    public static void setPersistentInteger(Player player, String tag, int value) {
        setPersistentTag(player, tag, IntTag.valueOf(value));
    }

    public static int getPersistentInteger(Player player, String tag, int expectedValue) {
        Tag theTag = getPersistentTag(player, tag, IntTag.valueOf(expectedValue));
        return theTag instanceof IntTag ? ((IntTag) theTag).getAsInt() : expectedValue;
    }

    /**
     * Checks whether player has specified tag in their persistent NBT, whatever
     * it's type or value is.
     */

    public static boolean hasPersistentTag(Player player, String tag) {
        CompoundTag data = player.getPersistentData();
        CompoundTag persistent;

        if (!data.contains(Player.PERSISTED_NBT_TAG)) {
            data.put(Player.PERSISTED_NBT_TAG, (persistent = new CompoundTag()));
        } else {
            persistent = data.getCompound(Player.PERSISTED_NBT_TAG);
        }

        if (persistent.contains(tag))
            return true;
        else
            return false;

    }
}
