package net.arkadiyhimself.fantazia.api.capability.entity.ability;

import net.arkadiyhimself.fantazia.api.capability.IPlayerAbility;
import net.minecraft.world.entity.player.Player;

public abstract class AbilityHolder implements IPlayerAbility {
    private final Player player;
    protected AbilityHolder(Player player) {
        this.player = player;
    }
    public abstract String id();

    @Override
    public final Player getPlayer() {
        return this.player;
    }

    @Override
    public void respawn() {

    }
}
