package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability;

import net.arkadiyhimself.fantazia.api.attachment.IBasicHolder;
import net.minecraft.world.entity.player.Player;

public interface IPlayerAbility extends IBasicHolder {
    void respawn();
    Player getPlayer();
}
