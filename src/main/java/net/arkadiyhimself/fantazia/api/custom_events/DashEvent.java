package net.arkadiyhimself.fantazia.api.custom_events;

import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Children of {@link DashEvent} are fired when an event involving dashing occurs <br>
 * All the events here are fired in {@link DashHolder} <br>
 * <br>
 * {@link DashEvent#dashHolder} contains the Dash capability involved in the dashing action
 * <br>
 * <br>
 * The events are fired on the {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS}.
 */
public abstract class DashEvent extends PlayerEvent {
    private final DashHolder dashHolder;
    public DashEvent(Player player, DashHolder dashHolder) {
        super(player);
        this.dashHolder = dashHolder;
    }
    public DashHolder getDash() { return this.dashHolder; }

    /**
     * {@link Start} is fired when a player attempts to start dashing. <br>
     * <br>
     * This event is {@link net.neoforged.bus.api.ICancellableEvent}.<br>
     * If it is canceled, the player does not start dashing.<br>
     * <br>
     * {@link Start#duration} contains the duration or the dash in ticks and can be changed
     */

    public static class Start extends DashEvent implements ICancellableEvent {
        private int duration;
        public Start(Player player, DashHolder dashHolder, int duration) {
            super(player, dashHolder);
            this.duration = duration;
        }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = Math.max(0, duration); }
    }

    /**
     * {@link Expired} is fired when player's {@link DashHolder#getDur()} reaches 0 after the player started dashing.<br>
     * <br>
     * This event is not {@link ICancellableEvent}
     */
    public static class Expired extends DashEvent {
        public Expired(Player player, DashHolder dashHolder) {
            super(player, dashHolder);
        }
    }

    /**
     * {@link Stopped} is fired when player's {@link DashHolder#stopDash()} is used.<br>
     * <br>
     * This event is {@link ICancellableEvent}
     */
    public static class Stopped extends DashEvent implements ICancellableEvent {
        public Stopped(Player player, DashHolder dashHolder) {
            super(player, dashHolder);
        }
    }
}
