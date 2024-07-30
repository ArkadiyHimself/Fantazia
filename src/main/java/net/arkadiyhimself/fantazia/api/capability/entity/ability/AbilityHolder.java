package net.arkadiyhimself.fantazia.api.capability.entity.ability;

import net.arkadiyhimself.fantazia.api.capability.IPlayerAbility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public abstract class AbilityHolder implements IPlayerAbility {
    private final Player player;
    public AbilityHolder(Player player) {
        this.player = player;
    }

    @Override
    public CompoundTag serialize() {
        return new CompoundTag();
    }

    @Override
    public void deserialize(CompoundTag tag) {

    }

    @Override
    public final Player getPlayer() {
        return this.player;
    }

    @Override
    public void respawn() {

    }
}