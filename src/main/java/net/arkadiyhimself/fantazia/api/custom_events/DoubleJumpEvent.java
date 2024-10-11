package net.arkadiyhimself.fantazia.api.custom_events;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Start is fired when a player attempts to perform a double jump. <br>
 * <br>
 * This event is {@link net.neoforged.bus.api.ICancellableEvent}.<br>
 * If it is canceled, the player does not start dashing
 */
public class DoubleJumpEvent extends PlayerEvent implements ICancellableEvent {
    public DoubleJumpEvent(Player player) {
        super(player);
    }
}
