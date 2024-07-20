package net.arkadiyhimself.fantazia.util.Interfaces;

import net.minecraft.world.entity.player.Player;

public interface IPlayerAbility {
    Player getOwner();
    void respawn();
}
