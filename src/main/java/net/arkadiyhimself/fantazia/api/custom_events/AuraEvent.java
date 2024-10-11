package net.arkadiyhimself.fantazia.api.custom_events;


import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

/**
 * Children of {@link AuraEvent} are fired when an event involving an aura occurs
 * <br>
 * All events here are called inside {@link AuraInstance}
 * <br>
 * <br>
 * {@link AuraEvent#aura} contains the AuraInstance involved in the event
 * <br>
 * <br>
 * The events are fired on the {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS}.
 */
public abstract class AuraEvent<T extends Entity> extends Event {
    private final AuraInstance<T> aura;
    public AuraEvent(AuraInstance<T> aura) {
        this.aura = aura;
    }
    public AuraInstance<T> getAura() {
        return aura;
    }

    /**
     * {@link Tick} is fired anytime an instance of aura is ticked if {@link AuraInstance#tick()}
     * <br>
     * <br>
     *  This event is not {@link net.neoforged.bus.api.ICancellableEvent}
     */
    public static class Tick<T extends Entity> extends AuraEvent<T> {
        public Tick(AuraInstance<T> aura) {
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
     *  This event is not {@link net.neoforged.bus.api.ICancellableEvent}
     */
    public static class Enter<T extends Entity> extends AuraEvent<T> {
        private final T entity;
        public Enter(AuraInstance<T> aura, T entity) {
            super(aura);
            this.entity = entity;
        }
        public T getEntity() {
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
     *  This event is not {@link net.neoforged.bus.api.ICancellableEvent}
     */
    public static class Exit<T extends Entity> extends AuraEvent<T> {
        private final T entity;
        public Exit(AuraInstance<T> aura, T entity) {
            super(aura);
            this.entity = entity;
        }
        public T getEntity() {
            return entity;
        }
    }
}
