package net.arkadiyhimself.fantazia.util.interfaces;

import net.minecraft.world.entity.player.Player;

public interface IPlayerAbility extends INBTsaver {
    Player getPlayer();
    void respawn();
}
