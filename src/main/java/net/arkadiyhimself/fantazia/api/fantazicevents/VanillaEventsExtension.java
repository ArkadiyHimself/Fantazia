package net.arkadiyhimself.fantazia.api.fantazicevents;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSource;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class VanillaEventsExtension {
    @Cancelable
    public static class ParticleTickEvent extends Event {
        public final Vec3 position;
        public final Vec3 deltaMovement;
        public final Particle particleType;
        public final float red;
        public final float green;
        public final float blue;
        public int age;
        public boolean hasPhysics;
        public boolean onGround;
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
    @Cancelable
    public static class MobAttackEvent extends LivingEvent {
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
    @Cancelable
    public static class DeathPreventionEvent extends LivingEvent {
        private final Object cause;
        public DeathPreventionEvent(LivingEntity entity, Object cause) {
            super(entity);
            this.cause = cause;
        }
        // a mob effect or an item which causes the death presentation; just use «getCause() instance of MobEffect» or something like that
        public Object getCause() {
            return cause;
        }
    }
    @Cancelable
    public static class LivingPickUpItemEvent extends LivingEvent {
        private final ItemEntity itemEntity;
        public LivingPickUpItemEvent(LivingEntity entity, ItemEntity itemEntity) {
            super(entity);
            this.itemEntity = itemEntity;
        }
        public ItemEntity getItemEntity() {
            return itemEntity;
        }
    }
    @Cancelable
    public static class AdvancedHealEvent extends LivingEvent {
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
    @Cancelable
    public static class CleanseEffectEvent extends MobEffectEvent {
        private final Cleanse cleanse;
        public CleanseEffectEvent(LivingEntity living, MobEffectInstance effectInstance, Cleanse cleanse) {
            super(living, effectInstance);
            this.cleanse = cleanse;
        }
        public Cleanse getStrength() {
            return cleanse;
        }
    }
    @Cancelable
    public static class EntityTickEvent extends EntityEvent {
        public EntityTickEvent(Entity entity) {
            super(entity);
        }
    }
}
