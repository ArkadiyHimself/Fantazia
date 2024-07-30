package net.arkadiyhimself.fantazia.api.fantazicevents;


import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Children of {@link AuraEvent} are fired when an event involving an aura occurs
 * <br>
 * All events here are called inside {@link AuraInstance}
 * <br>
 * <br>
 * {@link AuraEvent#aura} contains the AuraInstance involved in the event
 * <br>
 * <br>
 * The events are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class AuraEvent extends Event {
    private final AuraInstance aura;
    public AuraEvent(AuraInstance aura) {
        this.aura = aura;
    }
    public AuraInstance getAura() {
        return aura;
    }

    /**
     * {@link Tick} is fired anytime an instance of aura is ticked if {@link AuraInstance#tick()}
     * <br>
     * <br>
     *  This event is not {@link Cancelable}.
     * <br>
     * <br>
     * This event does not have a {@link HasResult result}
     */
    public static class Tick extends AuraEvent {
        public Tick(AuraInstance aura) {
            super(aura);
        }
    }

    /**
     * {@link Enter} is fired anytime an entity enters the range of an aura
     * <br>
     * <br>
     * {@link Enter#entity} contains the entity that entered the aura
     * <br>
     * <br>
     *  This event is not {@link Cancelable}.
     * <br>
     * <br>
     * This event does not have a {@link HasResult result}
     */
    public static class Enter extends AuraEvent {
        private final Entity entity;
        public Enter(AuraInstance aura, Entity entity) {
            super(aura);
            this.entity = entity;
        }
        public Entity getEntity() {
            return entity;
        }
    }

    /**
     * {@link Exit} is fired anytime an entity exits the range of an aura
     * <br>
     * <br>
     * {@link Exit#entity} contains the entity that exited the aura
     * <br>
     * <br>
     *  This event is not {@link Cancelable}.
     * <br>
     * <br>
     * This event does not have a {@link HasResult result}
     */
    public static class Exit extends AuraEvent {
        private final Entity entity;
        public Exit(AuraInstance aura, Entity entity) {
            super(aura);
            this.entity = entity;
        }
        public Entity getEntity() {
            return entity;
        }
    }
}
