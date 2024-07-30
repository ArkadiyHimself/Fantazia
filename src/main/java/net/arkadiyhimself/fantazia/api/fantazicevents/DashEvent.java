package net.arkadiyhimself.fantazia.api.fantazicevents;

import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Children of {@link DashEvent} are fired when an event involving dashing occurs <br>
 * All the events here are fired in {@link Dash} <br>
 * <br>
 * {@link DashEvent#dash} contains the Dash capability involved in the dashing action
 * <br>
 * <br>
 * The events are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class DashEvent extends PlayerEvent {
    private final Dash dash;
    public DashEvent(Player player, Dash dash) {
        super(player);
        this.dash = dash;
    }
    public Dash getDash() { return this.dash; }

    /**
     * {@link Start} is fired when a player attempts to start dashing. <br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If it is canceled, the player does not start dashing.<br>
     * <br>
     * {@link Start#duration} contains the duration or the dash in ticks and can be changed
     * <br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    @Cancelable
    public static class Start extends DashEvent {
        private int duration;
        public Start(Player player, Dash dash, int duration) {
            super(player, dash);
            this.duration = duration;
        }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = Math.max(0, duration); }
    }

    /**
     * {@link Expired} is fired when player's {@link Dash#getDur()} reaches 0 after the player started dashing.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    public static class Expired extends DashEvent {
        public Expired(Player player, Dash dash) {
            super(player, dash);
        }
    }

    /**
     * {@link Stopped} is fired when player's {@link Dash#stopDash()} is used.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    @Cancelable
    public static class Stopped extends DashEvent {
        public Stopped(Player player, Dash dash) {
            super(player, dash);
        }
    }
}
