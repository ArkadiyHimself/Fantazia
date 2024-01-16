package net.arkadiyhimself.combatimprovement.HandlersAndHelpers;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents.NewEvents;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.NewEvents.VanillaEventsExtension;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Networking.packets.KickOutOfGuiS2CPacket;
import net.arkadiyhimself.combatimprovement.Networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.combatimprovement.Registries.Items.ItemRegistry;
import net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.DashStone;
import net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.Passive.PassiveCasters;
import net.arkadiyhimself.combatimprovement.Registries.Items.Weapons.Melee.FragileBlade;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.effectsdostuff.Haemorrhage;
import net.arkadiyhimself.combatimprovement.Registries.Particless.ParticleRegistry;
import net.arkadiyhimself.combatimprovement.Registries.SoundRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Blocking.AttachBlocking;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.AttachDJump;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.DJump;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.Dash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.DataSync;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.GameEventTags;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
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
            if (WhereMagicHappens.Abilities.hasCurio(player, ItemRegistry.ENTANGLER.get()) && previousHealth.containsKey(player) && previousHealth.get(player) >= 2) {
                boolean work = NewEvents.ForgeExtenstion.onDeathPreventation(event.getEntity(), ItemRegistry.ENTANGLER.get());
                if (work) {
                    event.setCanceled(true);
                    player.setHealth(1.5f);
                }
            }
        }
    }
    @SubscribeEvent
    public static void onLootTableEvent(LootTableLoadEvent event) {
        LootTable lootTable = event.getTable();
        if (WhereMagicHappens.LootTables.getAncientCityLootTable().contains(event.getName())) {
            LootPool ancientCity = WhereMagicHappens.LootTables.constructLootPool("ancient_city_plus", -10f, 2f,
                    WhereMagicHappens.LootTables.createOptionalLoot(ItemRegistry.SCULK_HEART.get(), 25));
            lootTable.addPool(ancientCity);
        }
        event.setTable(lootTable);
    }
    @SubscribeEvent
    public static void livingHeal(LivingHealEvent event) {
        if (WhereMagicHappens.Abilities.hasCurio(event.getEntity(), ItemRegistry.ENTANGLER.get()) || event.getEntity().hasEffect(MobEffectRegistry.FROZEN.get())) {
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
        if (event.getEntity().hasEffect(MobEffectRegistry.DOOMED.get()) && event.getAmount() > 0 && !event.isCanceled()) {
            event.setAmount(Float.MAX_VALUE);
            event.getEntity().playSound(SoundRegistry.FALLEN_BREATH.get());
            double x = event.getEntity().getX();
            double y = event.getEntity().getY();
            double z = event.getEntity().getZ();
            double height = event.getEntity().getBbHeight();
            Minecraft.getInstance().level.addParticle(ParticleRegistry.FALLEN_SOUL.get(), x, y + height * 2 / 3, z, 0.0D, -0.135D, 0.0D);

            BlockPos blockPos = event.getEntity().getOnPos();
            Block block = event.getEntity().level.getBlockState(blockPos).getBlock();
            if (block == Blocks.DIRT || block == Blocks.SAND || block == Blocks.NETHERRACK || block == Blocks.GRASS_BLOCK) {
                event.getEntity().level.setBlockAndUpdate(blockPos, Blocks.SOUL_SAND.defaultBlockState());
            }
        }
    }
    @SubscribeEvent
    public static void livingAttack(LivingAttackEvent event) {
        if (event.getEntity().level.isClientSide()) { return; }
        LivingEntity target = event.getEntity();
        if (event.getSource().getEntity() instanceof Warden warden && "sonic_boom".equals(event.getSource().getMsgId())) {
            if (target instanceof ServerPlayer player) {
                if (WhereMagicHappens.Abilities.hasCurio(player, ItemRegistry.MYSTIC_MIRROR.get()) && !player.getCooldowns().isOnCooldown(ItemRegistry.MYSTIC_MIRROR.get())) {
                    ((PassiveCasters) (ItemRegistry.MYSTIC_MIRROR.get())).passiveAbility(player);
                    AttachDataSync.get(player).ifPresent(DataSync::onMirrorActivation);
                    event.setCanceled(true);
                    WhereMagicHappens.Abilities.rayOfParticles(player, warden, ParticleTypes.SONIC_BOOM);
                    warden.hurt(new EntityDamageSource("sonic_boom", player).bypassArmor().bypassEnchantments().setMagic(), 15f);
                }
            }
            if (target.hasEffect(MobEffectRegistry.REFLECT.get())) {
                event.setCanceled(true);
                target.removeEffect(MobEffectRegistry.REFLECT.get());
                WhereMagicHappens.Abilities.rayOfParticles(target, warden, ParticleTypes.SONIC_BOOM);
                warden.hurt(new EntityDamageSource("sonic_boom", target).bypassArmor().bypassEnchantments().setMagic(), 15f);
            }
            if (target.hasEffect(MobEffectRegistry.DEFLECT.get())) {
                event.setCanceled(true);
                target.removeEffect(MobEffectRegistry.DEFLECT.get());
            }
        }
        if (event.getEntity().hasEffect(MobEffectRegistry.ABSOLUTE_BARRIER.get())) {
            event.setCanceled(true);
            return;
        }
        if (target instanceof Player player) {
            AttachDash.get(player).ifPresent(dash -> dash.onHit(event));
            AttachBlocking.get(player).ifPresent(blocking -> blocking.onHit(event));
        }
        if (!WhereMagicHappens.Abilities.blocksDamage(target) && !event.isCanceled()) {
            StunEffect.get(target).ifPresent((stun) -> stun.onHit(event));
        }
    }
    @SubscribeEvent
    public static void livingHurt(LivingHurtEvent event) {
        if (event.getEntity().level.isClientSide()) { return; }
        LivingEntity target = event.getEntity();
        BarrierEffect.get(target).ifPresent(barrier -> barrier.onHit(event));
        LayeredBarrierEffect.get(target).ifPresent(layeredBarrier -> layeredBarrier.onHit(event));

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
            for (ItemStack stack : WhereMagicHappens.Gui.searchForItems(player, FragileBlade.class)) {
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
                if (mob instanceof Warden warden) {
                    warden.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                    warden.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
                }
            }
            if (entity instanceof ServerPlayer player) {
                NetworkHandler.sendToPlayer(new KickOutOfGuiS2CPacket(), player);
            }
            StunEffect.get(entity).ifPresent(stun -> stun.setMaxDur(event.getEffectInstance().getDuration()));
        }
        if (entity instanceof ServerPlayer player) {
            if (effect == MobEffectRegistry.DOOMED.get()) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DOOMED.get()), player);
            }

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
        if (entity instanceof ServerPlayer player) {
            if (effect == MobEffectRegistry.DOOMED.get()) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.UNDOOMED.get()), player);
            }
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
        if (entity instanceof ServerPlayer player) {
            if (effect == MobEffectRegistry.DOOMED.get()) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.UNDOOMED.get()), player);
            }
        }
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
            AttachDataSync.get(player).ifPresent(DataSync::tick);
        }
    }
    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        if (event.player != null) {
            AABB aabb = event.player.getBoundingBox().inflate(10f);
            List<ThrownTrident> tridents = event.player.level.getEntitiesOfClass(ThrownTrident.class, aabb);
            if (!tridents.isEmpty()) {
                ThrownTrident trident = tridents.get(0);
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

                List<ItemStack> fragSwords = WhereMagicHappens.Gui.searchForItems(serverPlayer, FragileBlade.class);
                for (ItemStack stack : fragSwords) {
                    AttachFragileBlade.get(stack).ifPresent(FragileBladeCap::tick);
                }
            }
            if (!CuriosApi.getCuriosHelper().findCurios(serverPlayer, ItemRegistry.SOUL_EATER.get()).isEmpty()) {
                WhereMagicHappens.Abilities.addEffectWithoutParticles(serverPlayer, MobEffects.HUNGER, 2);
                event.player.causeFoodExhaustion(serverPlayer.getFoodData().getSaturationLevel() > 0 ? 0.1f : 0.01f);
            }
            AttachDataSync.get(serverPlayer).ifPresent(dataSync -> {
                if (serverPlayer.isSprinting()) {
                    dataSync.wasteStamina(0.025f, true, 10);
                }
            });

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
        if (event.getEntity() instanceof ServerPlayer player) {
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
            if (event.getResult() == Event.Result.DENY) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DENIED.get()), player);
            }
        }
    }
    @SubscribeEvent
    public static void gameEvent(VanillaGameEvent event) {
        if (event.getVanillaEvent().is(GameEventTags.VIBRATIONS) && event.getContext().sourceEntity() != null && event.getContext().sourceEntity() instanceof LivingEntity entity && !entity.level.isClientSide()) {
            AABB aabb = entity.getBoundingBox().inflate(8);
            entity.getLevel().getEntitiesOfClass(ServerPlayer.class, aabb).forEach(player -> {
                WhereMagicHappens.Abilities.listenVibration((ServerLevel) entity.level, event.getContext(), event.getEventPosition(), player);
            });
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
    @SubscribeEvent
    public static void mobAttack(VanillaEventsExtension.MobAttackEvent event) {
        if (event.getEntity().hasEffect(MobEffectRegistry.DISARM.get())) {
            event.setCanceled(true);
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DENIED.get()), serverPlayer);
            }
        }
    }
    @SubscribeEvent
    public static void attackEntity(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (event.getEntity().hasEffect(MobEffectRegistry.DISARM.get())) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DENIED.get()), serverPlayer);
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        AttachDataSync.get(player).ifPresent(DataSync::onRespawn);
        StunEffect.get(player).ifPresent(Stun::onRespawn);
        StunEffect.get(player).ifPresent(Stun::endStun);
        BarrierEffect.get(player).ifPresent(Barrier::removeBarrier);
        LayeredBarrierEffect.get(player).ifPresent(LayeredBarrier::removeLayeredBarrier);
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingChangeTarget(LivingChangeTargetEvent event) {
        if (event.getEntity().hasEffect(MobEffectRegistry.STUN.get())) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void livingUseTotem(LivingUseTotemEvent event) {
        // yes.
        boolean work = NewEvents.ForgeExtenstion.onDeathPreventation(event.getEntity(), Items.TOTEM_OF_UNDYING);
        if (!work) { event.setCanceled(true); }
    }
    @SubscribeEvent
    public static void onDeathPreventation(VanillaEventsExtension.DeathPreventationEvent event) {
        if (event.getEntity().hasEffect(MobEffectRegistry.DOOMED.get())) {
            event.setCanceled(true);
        }
    }
}
