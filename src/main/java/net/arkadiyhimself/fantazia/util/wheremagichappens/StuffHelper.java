package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

/**
 * This code was politely borrowed from Aizistral
 */
public class StuffHelper {

    /**
     * Sets the given Tag type to the player's persistent NBT.
     */

    public static void setPersistentTag(Player player, String tag, Tag value) {
        CompoundTag data = player.getPersistentData();
        CompoundTag persistent;
        if (!data.contains(Player.PERSISTED_NBT_TAG)) data.put(Player.PERSISTED_NBT_TAG, (persistent = new CompoundTag()));
        else persistent = data.getCompound(Player.PERSISTED_NBT_TAG);
        persistent.put(tag, value);
    }

    public static void setPersistentBoolean(Player player, String tag) {
        setPersistentTag(player, tag, ByteTag.valueOf(true));
    }

    /**
     * Checks whether player has specified tag in their persistent NBT, whatever
     * it's type or value is.
     */

    public static boolean hasPersistentTag(Player player, String tag) {
        CompoundTag data = player.getPersistentData();
        CompoundTag persistent;

        if (!data.contains(Player.PERSISTED_NBT_TAG)) data.put(Player.PERSISTED_NBT_TAG, (persistent = new CompoundTag()));
        else persistent = data.getCompound(Player.PERSISTED_NBT_TAG);
        return persistent.contains(tag);
    }
}
