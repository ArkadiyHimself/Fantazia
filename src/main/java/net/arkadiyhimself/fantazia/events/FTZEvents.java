package net.arkadiyhimself.fantazia.events;

import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSource;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
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
import net.neoforged.neoforge.common.NeoForge;

public class FTZEvents {
    public static class ForgeExtension {
        public static VanillaEventsExtension.ParticleTickEvent onParticleTick(Particle particle, Vec3 position, Vec3 deltaMovement, float red, float green, float blue, int age, boolean hasPhysics, boolean onGround) {
            VanillaEventsExtension.ParticleTickEvent event = new VanillaEventsExtension.ParticleTickEvent(particle, position, deltaMovement, red, green, blue, age, hasPhysics, onGround);
            NeoForge.EVENT_BUS.post(event);
            return event;
        }
        public static boolean onMobAttack(Mob mob, Entity target) {
            VanillaEventsExtension.MobAttackEvent event = new VanillaEventsExtension.MobAttackEvent(mob, target);
            return !NeoForge.EVENT_BUS.post(event).isCanceled();
        }
        public static boolean onDeathPrevention(LivingEntity entity, Object cause) {
            VanillaEventsExtension.FantazicDeathPrevention event = new VanillaEventsExtension.FantazicDeathPrevention(entity, cause);
            return !NeoForge.EVENT_BUS.post(event).isCanceled();
        }
        public static boolean onLivingPickUpItem(LivingEntity entity, ItemEntity item) {
            VanillaEventsExtension.LivingPickUpItemEvent event = new VanillaEventsExtension.LivingPickUpItemEvent(entity, item);
            return !NeoForge.EVENT_BUS.post(event).isCanceled();
        }
        public static float onAdvancedHealing(LivingEntity entity, HealingSource source, float amount) {
            VanillaEventsExtension.AdvancedHealEvent event = new VanillaEventsExtension.AdvancedHealEvent(entity, source, amount);
            return (NeoForge.EVENT_BUS.post(event).isCanceled() ? 0 : event.getAmount());
        }
        public static boolean onEffectCleanse(LivingEntity living, MobEffectInstance effectInstance, Cleanse cleanse) {
            VanillaEventsExtension.CleanseEffectEvent event = new VanillaEventsExtension.CleanseEffectEvent(living, effectInstance, cleanse);
            return !NeoForge.EVENT_BUS.post(event).isCanceled();
        }
    }
    public static boolean onBlockingStart(Player player, ItemStack itemStack) {
        BlockingEvent.Start event = new BlockingEvent.Start(player, itemStack);
        return !NeoForge.EVENT_BUS.post(event).isCanceled();
    }
    public static void onBlockingExpired(Player player, ItemStack itemStack) {
        BlockingEvent.Expired event = new BlockingEvent.Expired(player, itemStack);
        NeoForge.EVENT_BUS.post(event);
    }
    public static BlockingEvent.ParryDecision onParryDecision(Player player, ItemStack itemStack, float amount, LivingEntity attacker) {
        BlockingEvent.ParryDecision event = new BlockingEvent.ParryDecision(player, itemStack, amount, attacker);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }
    public static boolean onBlock(Player player, ItemStack itemStack, float amount, LivingEntity attacker) {
        BlockingEvent.Block event = new BlockingEvent.Block(player, itemStack, amount, attacker);
        return !NeoForge.EVENT_BUS.post(event).isCanceled();
    }
    public static BlockingEvent.Parry onParry(Player player, ItemStack itemStack, float amountTake, LivingEntity attacker, float amountDeal) {
        BlockingEvent.Parry event = new BlockingEvent.Parry(player, itemStack, amountTake, attacker, amountDeal);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }
    public static int onDashStart(Player player, DashHolder dashHolder, int duration) {
        DashEvent.Start event = new DashEvent.Start(player, dashHolder, duration);
        return NeoForge.EVENT_BUS.post(event).isCanceled() ? 0 : event.getDuration();
    }
    public static void onDashExpired(Player player, DashHolder dashHolder) {
        DashEvent.Expired event = new DashEvent.Expired(player, dashHolder);
        NeoForge.EVENT_BUS.post(event);
    }
    public static boolean onDashEnd(Player player, DashHolder dashHolder) {
        DashEvent.Stopped event = new DashEvent.Stopped(player, dashHolder);
        return !NeoForge.EVENT_BUS.post(event).isCanceled();
    }
    public static boolean onDoubleJump(Player player) {
        DoubleJumpEvent event = new DoubleJumpEvent(player);
        return NeoForge.EVENT_BUS.post(event).isCanceled();
    }
    public static <T extends Entity> void onAuraTick(AuraInstance<T> aura) {
        AuraEvent.Tick<T> event = new AuraEvent.Tick<>(aura);
        NeoForge.EVENT_BUS.post(event);
    }
    public static <T extends Entity> void onAuraEnter(AuraInstance<T> aura, T entity) {
        NeoForge.EVENT_BUS.post(new AuraEvent.Enter<>(aura, entity));
    }
    public static <T extends Entity> void onAuraExit(AuraInstance<T> aura, T entity) {
        NeoForge.EVENT_BUS.post(new AuraEvent.Exit<>(aura, entity));
    }
}
