package net.arkadiyhimself.fantazia.api.type.entity;

import net.minecraft.world.entity.player.Player;

public interface IPlayerAbility extends IBasicHolder {
    void respawn();
    Player getPlayer();
}
