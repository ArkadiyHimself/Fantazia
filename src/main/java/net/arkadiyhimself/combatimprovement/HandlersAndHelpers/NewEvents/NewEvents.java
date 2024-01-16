package net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents;

import net.minecraft.client.particle.Particle;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

public class NewEvents {
    public static class ForgeExtenstion {
        public static VanillaEventsExtension.ParticleTickEvent onParticleTick(Particle particle, Vec3 positon, Vec3 deltaMovement, float red, float green, float blue, int age, boolean hasPhysics, boolean onGround) {
            VanillaEventsExtension.ParticleTickEvent event = new VanillaEventsExtension.ParticleTickEvent(particle, positon, deltaMovement, red, green, blue, age, hasPhysics, onGround);
            MinecraftForge.EVENT_BUS.post(event);
            return event;
        }
        public static boolean onMobAttack(Mob mob, Entity target) {
            VanillaEventsExtension.MobAttackEvent event = new VanillaEventsExtension.MobAttackEvent(mob, target);
            return !MinecraftForge.EVENT_BUS.post(event);
        }
        public static boolean onDeathPreventation(LivingEntity entity, Object cause) {
            VanillaEventsExtension.DeathPreventationEvent event = new VanillaEventsExtension.DeathPreventationEvent(entity, cause);
            return !MinecraftForge.EVENT_BUS.post(event);
        }
    }
    public static boolean onBlockingStart(ServerPlayer player, ItemStack itemStack) {
        BlockingEvent.Start event = new BlockingEvent.Start(player, itemStack);
        return !MinecraftForge.EVENT_BUS.post(event);
    }
    public static void onBlockingEnd(ServerPlayer player, ItemStack itemStack) {
        BlockingEvent.End event = new BlockingEvent.End(player, itemStack);
        MinecraftForge.EVENT_BUS.post(event);
    }
    public static boolean onBlock(ServerPlayer player, ItemStack itemStack, float amount, LivingEntity attacker) {
        BlockingEvent.Block event = new BlockingEvent.Block(player, itemStack, amount, attacker);
        return !MinecraftForge.EVENT_BUS.post(event);
    }
    public static BlockingEvent.Parry onParry(ServerPlayer player, ItemStack itemStack, float amountTake, LivingEntity attacker, float amountDeal) {
        BlockingEvent.Parry event = new BlockingEvent.Parry(player, itemStack, amountTake, attacker, amountDeal);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }
    public static int onDashStart(ServerPlayer player, int duration) {
        DashEvent.Start event = new DashEvent.Start(player, duration);
        return (MinecraftForge.EVENT_BUS.post(event)) ? 0 : event.getDuration();
    }
    public static void onDashExpired(ServerPlayer player) {
        DashEvent.Expired event = new DashEvent.Expired(player);
        MinecraftForge.EVENT_BUS.post(event);
    }
    public static boolean onDashEnd(ServerPlayer player) {
        DashEvent.Ended event = new DashEvent.Ended(player);
        return !MinecraftForge.EVENT_BUS.post(event);
    }
    public static boolean onDoubleJump(ServerPlayer player) {
        DoubleJumpEvent event = new DoubleJumpEvent(player);
        return !MinecraftForge.EVENT_BUS.post(event);
    }
}
