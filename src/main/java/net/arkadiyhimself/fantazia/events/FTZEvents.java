package net.arkadiyhimself.fantazia.events;

import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSource;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.fantazicevents.*;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

public class FTZEvents {
    public static class ForgeExtension {
        public static VanillaEventsExtension.ParticleTickEvent onParticleTick(Particle particle, Vec3 position, Vec3 deltaMovement, float red, float green, float blue, int age, boolean hasPhysics, boolean onGround) {
            VanillaEventsExtension.ParticleTickEvent event = new VanillaEventsExtension.ParticleTickEvent(particle, position, deltaMovement, red, green, blue, age, hasPhysics, onGround);
            MinecraftForge.EVENT_BUS.post(event);
            return event;
        }
        public static boolean onMobAttack(Mob mob, Entity target) {
            VanillaEventsExtension.MobAttackEvent event = new VanillaEventsExtension.MobAttackEvent(mob, target);
            return !MinecraftForge.EVENT_BUS.post(event);
        }
        public static boolean onDeathPrevention(LivingEntity entity, Object cause) {
            VanillaEventsExtension.DeathPreventionEvent event = new VanillaEventsExtension.DeathPreventionEvent(entity, cause);
            return !MinecraftForge.EVENT_BUS.post(event);
        }
        public static boolean onLivingPickUpItem(LivingEntity entity, ItemEntity item) {
            VanillaEventsExtension.LivingPickUpItemEvent event = new VanillaEventsExtension.LivingPickUpItemEvent(entity, item);
            return !MinecraftForge.EVENT_BUS.post(event);
        }
        public static float onAdvancedHealing(LivingEntity entity, HealingSource source, float amount) {
            VanillaEventsExtension.AdvancedHealEvent event = new VanillaEventsExtension.AdvancedHealEvent(entity, source, amount);
            return (MinecraftForge.EVENT_BUS.post(event) ? 0 : event.getAmount());
        }
        public static boolean onEffectCleanse(LivingEntity living, MobEffectInstance effectInstance, Cleanse cleanse) {
            VanillaEventsExtension.CleanseEffectEvent event = new VanillaEventsExtension.CleanseEffectEvent(living, effectInstance, cleanse);
            return !MinecraftForge.EVENT_BUS.post(event);
        }
        public static boolean onEntityTick(Entity entity) {
            return MinecraftForge.EVENT_BUS.post(new VanillaEventsExtension.EntityTickEvent(entity));
        }
    }
    public static boolean onBlockingStart(Player player, ItemStack itemStack) {
        BlockingEvent.Start event = new BlockingEvent.Start(player, itemStack);
        return !MinecraftForge.EVENT_BUS.post(event);
    }
    public static void onBlockingExpired(Player player, ItemStack itemStack) {
        BlockingEvent.Expired event = new BlockingEvent.Expired(player, itemStack);
        MinecraftForge.EVENT_BUS.post(event);
    }
    public static BlockingEvent.ParryDecision onParryDecision(Player player, ItemStack itemStack, float amount, LivingEntity attacker) {
        BlockingEvent.ParryDecision event = new BlockingEvent.ParryDecision(player, itemStack, amount, attacker);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }
    public static boolean onBlock(Player player, ItemStack itemStack, float amount, LivingEntity attacker) {
        BlockingEvent.Block event = new BlockingEvent.Block(player, itemStack, amount, attacker);
        return !MinecraftForge.EVENT_BUS.post(event);
    }
    public static BlockingEvent.Parry onParry(Player player, ItemStack itemStack, float amountTake, LivingEntity attacker, float amountDeal) {
        BlockingEvent.Parry event = new BlockingEvent.Parry(player, itemStack, amountTake, attacker, amountDeal);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }
    public static int onDashStart(Player player, Dash dash, int duration) {
        DashEvent.Start event = new DashEvent.Start(player, dash, duration);
        return (MinecraftForge.EVENT_BUS.post(event)) ? 0 : event.getDuration();
    }
    public static void onDashExpired(Player player, Dash dash) {
        DashEvent.Expired event = new DashEvent.Expired(player, dash);
        MinecraftForge.EVENT_BUS.post(event);
    }
    public static boolean onDashEnd(Player player, Dash dash) {
        DashEvent.Stopped event = new DashEvent.Stopped(player, dash);
        return !MinecraftForge.EVENT_BUS.post(event);
    }
    public static boolean onDoubleJump(Player player) {
        DoubleJumpEvent event = new DoubleJumpEvent(player);
        return !MinecraftForge.EVENT_BUS.post(event);
    }
    public static <T extends Entity> void onAuraTick(AuraInstance<T> aura) {
        AuraEvent.Tick<T> event = new AuraEvent.Tick<>(aura);
        MinecraftForge.EVENT_BUS.post(event);
    }
    public static <T extends Entity> void onAuraEnter(AuraInstance<T> aura, T entity) {
        MinecraftForge.EVENT_BUS.post(new AuraEvent.Enter<>(aura, entity));
    }
    public static <T extends Entity> void onAuraExit(AuraInstance<T> aura, T entity) {
        MinecraftForge.EVENT_BUS.post(new AuraEvent.Exit<>(aura, entity));
    }
}
