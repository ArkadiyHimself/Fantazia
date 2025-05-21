package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

public class FantazicNBTUtil {

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

    public static boolean hasPersistentTag(Player player, String tag) {
        CompoundTag data = player.getPersistentData();
        CompoundTag persistent;

        if (!data.contains(Player.PERSISTED_NBT_TAG)) data.put(Player.PERSISTED_NBT_TAG, (persistent = new CompoundTag()));
        else persistent = data.getCompound(Player.PERSISTED_NBT_TAG);
        return persistent.contains(tag);
    }

}
