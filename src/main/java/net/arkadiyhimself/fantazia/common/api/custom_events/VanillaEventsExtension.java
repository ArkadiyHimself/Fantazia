package net.arkadiyhimself.fantazia.common.api.custom_events;

import net.arkadiyhimself.fantazia.common.advanced.cleanse.Cleanse;
import net.arkadiyhimself.fantazia.common.advanced.healing.HealingSource;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public class VanillaEventsExtension {

    public static class ParticleTickEvent extends Event implements ICancellableEvent {

        public final Vec3 position;
        public final Vec3 deltaMovement;
        public final Particle particleType;
        public final float red;
        public final float green;
        public final float blue;
        public final int age;
        public final boolean hasPhysics;
        public final boolean onGround;

        public ParticleTickEvent(Particle particle, Vec3 position, Vec3 deltaMovement, float red, float green, float blue, int age, boolean hasPhysics, boolean onGround) {
            this.particleType = particle;
            this.position = position;
            this.deltaMovement = deltaMovement;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.age = age;
            this.hasPhysics = hasPhysics;
            this.onGround = onGround;
        }
    }

    public static class MobAttackEvent extends LivingEvent implements ICancellableEvent {
        private final Entity target;

        public MobAttackEvent(Mob entity, Entity target) {
            super(entity);
            this.target = target;
        }

        public Entity getTarget()
        {
            return target;
        }
    }

    // honestly, I am not sure if I should even keep this one
    public static class FantazicDeathPrevention extends LivingEvent implements ICancellableEvent {

        private final Object cause;

        public FantazicDeathPrevention(LivingEntity entity, Object cause) {
            super(entity);
            this.cause = cause;
        }

        // a mob effect or an item which causes the death presentation; just use «getCause() instance of MobEffect» or something like that
        public Object getCause() {
            return cause;
        }
    }

    public static class LivingPickUpItemEvent extends LivingEvent implements ICancellableEvent {

        private final ItemEntity itemEntity;

        public LivingPickUpItemEvent(LivingEntity entity, ItemEntity itemEntity) {
            super(entity);
            this.itemEntity = itemEntity;
        }

        public ItemEntity getItemEntity() {
            return itemEntity;
        }
    }

    public static class AdvancedHealEvent extends LivingEvent implements ICancellableEvent {

        private final HealingSource source;
        private float amount;

        public AdvancedHealEvent(LivingEntity entity, HealingSource source, float amount) {
            super(entity);
            this.source = source;
            this.amount = amount;
        }

        public HealingSource getSource() {
            return source;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }
    }

    public static class CleanseEffectEvent extends MobEffectEvent implements ICancellableEvent {

        private Cleanse cleanse;

        public CleanseEffectEvent(LivingEntity living, MobEffectInstance effectInstance, Cleanse cleanse) {
            super(living, effectInstance);
            this.cleanse = cleanse;
        }

        public Cleanse getStrength() {
            return cleanse;
        }

        public void setStrength(Cleanse cleanse) {
            this.cleanse = cleanse;
        }
    }
}
