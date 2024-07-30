package net.arkadiyhimself.fantazia.api.capability;

import net.minecraft.world.entity.player.Player;

public interface IPlayerAbility extends INBTwrite {
    Player getPlayer();
    void respawn();
}
