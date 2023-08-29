package net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents;

import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.Dash;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Children of DashEvent are fired when an event involving dashing occurs <br>
 * All the events here are fired in {@link net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.Dash} <br>
 * <br>
 * The events do not have a result. {@link HasResult}<br>
 * <br>
 * The events are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class DashEvent extends PlayerEvent {
    private final Dash dash;
    public DashEvent(Player player) {
        super(player);
        dash = AttachDash.getUnwrap(player);
    }
    public Dash getDash() { return this.dash; }

    /**
     * Start is fired when a player attempts to start dashing. <br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If it is canceled, the player does not start dashing.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    @Cancelable
    public static class Start extends DashEvent {
        private int duration;
        public Start(Player player, int duration) {
            super(player);
            this.duration = duration;
        }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = Math.max(0, duration); }
    }

    /**
     * End is fired when player's {@link net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.Dash#dashDuration} gets to 0 after the player started dashing.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    public static class Expired extends DashEvent {
        public Expired(Player player) {
            super(player);
        }
    }

    /**
     * End is fired when player's {@link Dash#stopDash()} is used.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     */
    @Cancelable
    public static class Ended extends DashEvent {
        public Ended(Player player) {
            super(player);
        }
    }
}
