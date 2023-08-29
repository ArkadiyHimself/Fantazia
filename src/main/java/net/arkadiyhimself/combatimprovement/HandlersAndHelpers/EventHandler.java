package net.arkadiyhimself.combatimprovement.HandlersAndHelpers;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents.DashEvent;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Networking.packets.KickOutOfGuiS2CPacket;
import net.arkadiyhimself.combatimprovement.Networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.combatimprovement.Registries.Items.ItemRegistry;
import net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.DashStone;
import net.arkadiyhimself.combatimprovement.Registries.Items.Weapons.FragileBlade;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.effectsdostuff.Haemorrhage;
import net.arkadiyhimself.combatimprovement.Registries.Sounds.SoundRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Blocking.AttachBlocking;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.AttachDJump;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.DJump;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.Dash;
import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.AttachFragileBlade;
import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.FragileBladeCap;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.AbsoluteBarrier.AbsoluteBarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect.Barrier;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect.BarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrier;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.StunEffect.Stun;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.StunEffect.StunEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = CombatImprovement.MODID)
public class EventHandler {
    public static final Map<Player, Float> previousHealth = new WeakHashMap<>();
    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (UsefulMethods.Abilities.hasCurio(player, ItemRegistry.ENTANGLER.get()) && previousHealth.containsKey(player) && previousHealth.get(player) >= 2) {
                event.setCanceled(true);
                player.setHealth(1.5f);
            }
        }
    }
    @SubscribeEvent
    public static void livingHeal(LivingHealEvent event) {
        if (UsefulMethods.Abilities.hasCurio(event.getEntity(), ItemRegistry.ENTANGLER.get())) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void livingDamage(LivingDamageEvent event) {
        if (event.getEntity().level.isClientSide()) { return; }
        if (event.getEntity().hasEffect(MobEffectRegistry.FURY.get())) {
            event.setAmount(event.getAmount() * 2);
        }
        if (event.getSource().getEntity() instanceof LivingEntity entity && entity.hasEffect(MobEffectRegistry.FURY.get())) {
            event.setAmount(event.getAmount() * 2);
        }
        if (event.getSource().isExplosion() || "sonic_boom".equals(event.getSource().getMsgId())) {
            event.getEntity().addEffect(new MobEffectInstance(MobEffectRegistry.DEAFENING.get(), 200, 0, true, false, true));
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.RINGING.get()), serverPlayer);
            }
        }
        if (event.getEntity() instanceof ServerPlayer player) {
            previousHealth.put(player, player.getHealth());
        }
    }
    @SubscribeEvent
    public static void livingAttack(LivingAttackEvent event) {
        if (event.getEntity().level.isClientSide()) { return; }
        LivingEntity entity = event.getEntity();
        if (event.getEntity().hasEffect(MobEffectRegistry.ABSOLUTE_BARRIER.get())) {
            event.setCanceled(true);
            return;
        }
        if (entity instanceof Player player) {
            AttachDash.get(player).ifPresent(dash -> dash.onHit(event));
            AttachBlocking.get(player).ifPresent(blocking -> blocking.onHit(event));
        }
        if (!UsefulMethods.Abilities.blocksDamage(entity) && !event.isCanceled()) {
            StunEffect.get(entity).ifPresent((stun) -> stun.onHit(event));
        }
        if (event.getSource().getEntity() instanceof Player player) {
            player.sendSystemMessage(Component.translatable(String.valueOf(player.getAttributeValue(Attributes.ATTACK_DAMAGE))));
        }
    }
    @SubscribeEvent
    public static void livingHurt(LivingHurtEvent event) {
        if (event.getEntity().level.isClientSide()) { return; }
        LivingEntity entity = event.getEntity();
        BarrierEffect.get(entity).ifPresent(barrier -> barrier.onHit(event));
        LayeredBarrierEffect.get(entity).ifPresent(layeredBarrier -> layeredBarrier.onHit(event));

        boolean melee = "player".equals(event.getSource().getMsgId()) || (!(event.getSource() instanceof IndirectEntityDamageSource) && "mob".equals(event.getSource().getMsgId()));

        if (melee && event.getSource().getEntity() instanceof LivingEntity attacker) {
            ItemStack stack = attacker.getMainHandItem();
            if (stack.getItem() instanceof FragileBlade) {
                FragileBladeCap cap = AttachFragileBlade.getUnwrap(stack);
                float dmg = cap.damage;
                event.setAmount(event.getAmount() + dmg);
                event.getEntity().playSound(AttachFragileBlade.getUnwrap(stack).getHitSound());
                AttachFragileBlade.get(stack).ifPresent(FragileBladeCap::onAttack);
            }
        }
        if (event.getEntity().getMainHandItem().getItem() instanceof FragileBlade) {
            AttachFragileBlade.get(event.getEntity().getMainHandItem()).ifPresent(FragileBladeCap::reset);
        }
        if (event.getEntity() instanceof Player player) {
            for (ItemStack stack : UsefulMethods.Gui.searchForItems(player, FragileBlade.class)) {
                AttachFragileBlade.get(stack).ifPresent(FragileBladeCap::reset);
            }
        }
    }
    @SubscribeEvent
    public static void effectAdded(MobEffectEvent.Added event) {
        MobEffect effect = event.getEffectInstance().getEffect();
        LivingEntity entity = event.getEntity();
        if (effect == MobEffectRegistry.STUN.get()) {
            if (entity instanceof Mob mob) {
                for (Goal.Flag flag : Goal.Flag.values()) {
                    mob.goalSelector.disableControlFlag(flag);
                    mob.targetSelector.disableControlFlag(flag);
                }
            }
            if (entity instanceof ServerPlayer player) {
                NetworkHandler.sendToPlayer(new KickOutOfGuiS2CPacket(), player);
            }
            StunEffect.get(entity).ifPresent(stun -> stun.setMaxDur(event.getEffectInstance().getDuration()));
        }
        if (effect == MobEffectRegistry.BARRIER.get()) {
            BarrierEffect.get(entity).ifPresent(barrier -> barrier.addBarrier(Math.max(1, event.getEffectInstance().getAmplifier())));
        }
        if (effect == MobEffectRegistry.LAYERED_BARRIER.get()) {
            LayeredBarrierEffect.get(entity).ifPresent(layeredBarrier -> layeredBarrier.addLayeredBarrier(event.getEffectInstance().getAmplifier()));
        }
        if (effect == MobEffectRegistry.ABSOLUTE_BARRIER.get()) {
            AbsoluteBarrierEffect.get(entity).ifPresent(absoluteBarrier -> absoluteBarrier.setBarrier(true));
        }
    }
    @SubscribeEvent
    public static void effectRemoved(MobEffectEvent.Remove event) {
        MobEffect effect = event.getEffect();
        LivingEntity entity = event.getEntity();
        if (effect == MobEffectRegistry.STUN.get()) {
            if (entity instanceof Mob mob) {
                for (Goal.Flag flag : Goal.Flag.values()) {
                    mob.goalSelector.enableControlFlag(flag);
                    mob.targetSelector.enableControlFlag(flag);
                }
            }
            StunEffect.get(event.getEntity()).ifPresent(Stun::endStun);
        }
        if (effect == MobEffectRegistry.BARRIER.get()) {
            BarrierEffect.get(entity).ifPresent(Barrier::removeBarrier);
        }
        if (effect == MobEffectRegistry.LAYERED_BARRIER.get()) {
            LayeredBarrierEffect.get(entity).ifPresent(LayeredBarrier::removeLayeredBarrier);
        }
        if (effect == MobEffectRegistry.ABSOLUTE_BARRIER.get()) {
            AbsoluteBarrierEffect.get(entity).ifPresent(absoluteBarrier -> absoluteBarrier.setBarrier(false));
        }
    }
    @SubscribeEvent
    public static void effectExpired(MobEffectEvent.Expired event) {
        MobEffect effect = event.getEffectInstance().getEffect();
        LivingEntity entity = event.getEntity();
        if (effect == MobEffectRegistry.STUN.get()) {
            if (entity instanceof Mob mob) {
                for (Goal.Flag flag : Goal.Flag.values()) {
                    mob.goalSelector.enableControlFlag(flag);
                    mob.targetSelector.enableControlFlag(flag);
                }
            }
            StunEffect.get(entity).ifPresent(Stun::endStun);
        }
        if (effect == MobEffectRegistry.BARRIER.get()) {
            BarrierEffect.get(entity).ifPresent(Barrier::removeBarrier);
        }
        if (effect == MobEffectRegistry.LAYERED_BARRIER.get()) {
            LayeredBarrierEffect.get(entity).ifPresent(LayeredBarrier::removeLayeredBarrier);
        }
        if (effect == MobEffectRegistry.ABSOLUTE_BARRIER.get()) {
            AbsoluteBarrierEffect.get(entity).ifPresent(absoluteBarrier -> absoluteBarrier.setBarrier(false));
        }
    }
    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        StunEffect.get(player).ifPresent(Stun::endStun);
        BarrierEffect.get(player).ifPresent(Barrier::removeBarrier);
        LayeredBarrierEffect.get(player).ifPresent(LayeredBarrier::removeLayeredBarrier);
    }
    @SubscribeEvent
    public static void mouseScrolling(InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().level != null) {
            if (StunEffect.getUnwrap(Minecraft.getInstance().player).isStunned()) { event.setCanceled(true); }
        }
    }
    @SubscribeEvent
    public static void changeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if(event.getNewGameMode() == GameType.CREATIVE || event.getNewGameMode() == GameType.SPECTATOR) {
            StunEffect.get(event.getEntity()).ifPresent(Stun::endStun);
            if (event.getEntity().hasEffect(MobEffectRegistry.STUN.get())) {
                event.getEntity().removeEffect(MobEffectRegistry.STUN.get());
            }
        }
    }
    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.isDeadOrDying() && !entity.level.isClientSide()) {
            StunEffect.get(entity).ifPresent(Stun::tick);
            BarrierEffect.get(entity).ifPresent(Barrier::tick);
            LayeredBarrierEffect.get(entity).ifPresent(LayeredBarrier::tick);
        }
        if (event.getEntity() instanceof Player player) {
            Dash dash = AttachDash.getUnwrap(player);
            if (dash != null && dash.isDashing() && dash.dashLevel >= 3) {
                player.noPhysics = true;
                player.clearFire();
            }
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        // abilities
        if (event.player instanceof ServerPlayer serverPlayer) {
            if (event.phase == TickEvent.Phase.START) {
                AttachDash.get(serverPlayer).ifPresent(dash -> dash.ticking(serverPlayer));
                AttachBlocking.get(serverPlayer).ifPresent(blocking -> blocking.ticking(serverPlayer));
                AttachDJump.get(serverPlayer).ifPresent(DJump::ticking);

                List<ItemStack> fragSwords = UsefulMethods.Gui.searchForItems(serverPlayer, FragileBlade.class);
                for (ItemStack stack : fragSwords) {
                    AttachFragileBlade.get(stack).ifPresent(FragileBladeCap::tick);
                }
            }
            if (!CuriosApi.getCuriosHelper().findCurios(serverPlayer, ItemRegistry.SOUL_EATER.get()).isEmpty()) {
                UsefulMethods.Abilities.addEffectWithoutParticles(serverPlayer, MobEffects.HUNGER, 2);
                event.player.causeFoodExhaustion(serverPlayer.getFoodData().getSaturationLevel() > 0 ? 0.1f : 0.01f);
            }
        }
    }
    @SubscribeEvent
    public static void curioEquip(CurioEquipEvent event) {
        if (Objects.equals(event.getSlotContext().identifier(), "dashstone") && event.getEntity() instanceof Player player) {
            if (event.getStack().getItem() instanceof DashStone dashStone && CuriosApi.getCuriosHelper().findCurios(player, "dashstone").isEmpty()) {
                AttachDash.get(player).ifPresent(dash -> dash.setDashLevel(dashStone.level));
            }
        }
    }
    @SubscribeEvent
    public static void curioUnEquip(CurioUnequipEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (Objects.equals(event.getSlotContext().identifier(), "dashstone")) {
                if (player.isSpectator() || player.isCreative()) {
                    AttachDash.get(player).ifPresent(dash -> dash.setDashLevel(0));
                } else {
                    event.setResult(Event.Result.DENY);
                }
            }
            if (player.getCooldowns().isOnCooldown(event.getStack().getItem()) && !(player.isCreative() || player.isSpectator())) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
    @SubscribeEvent
    public static void itemToss(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
        if (event.getEntity().getItem().getItem() instanceof FragileBlade) {
            AttachFragileBlade.get(stack).ifPresent(FragileBladeCap::reset);
        }
    }
    @SubscribeEvent
    public static void livingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            AttachDJump.get(player).ifPresent(dJump -> {
                dJump.startTick = false;
                dJump.jumpDelay = 1;
                dJump.justJumped = true;
                dJump.updateTracking();
            });
        }
        if (event.getEntity().hasEffect(MobEffectRegistry.HAEMORRHAGE.get())) {
            event.getEntity().hurt(Haemorrhage.BLEEDING, 1F + event.getEntity().getEffect(MobEffectRegistry.HAEMORRHAGE.get()).getAmplifier() * 0.5f);
            ((Haemorrhage) event.getEntity().getEffect(MobEffectRegistry.HAEMORRHAGE.get()).getEffect()).activeDMGdelay = 10;
            ((Haemorrhage) event.getEntity().getEffect(MobEffectRegistry.HAEMORRHAGE.get()).getEffect()).passiveDMGdelay = 10;
        }
    }
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().hasEffect(MobEffectRegistry.STUN.get())) {
            StunEffect.get(event.getEntity()).ifPresent(Stun::endStun);
        }
    }
}
