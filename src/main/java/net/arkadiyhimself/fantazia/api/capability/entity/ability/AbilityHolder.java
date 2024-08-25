package net.arkadiyhimself.fantazia.api.capability.entity.ability;

import net.arkadiyhimself.fantazia.api.capability.IPlayerAbility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public abstract class AbilityHolder implements IPlayerAbility {
    private final Player player;
    public AbilityHolder(Player player) {
        this.player = player;
    }
    public abstract String ID();
    @Override
    public abstract CompoundTag serialize(boolean toDisk);

    @Override
    public abstract void deserialize(CompoundTag tag, boolean fromDisk);

    @Override
    public final Player getPlayer() {
        return this.player;
    }

    @Override
    public void respawn() {

    }
}
