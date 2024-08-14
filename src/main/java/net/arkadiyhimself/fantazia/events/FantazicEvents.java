package net.arkadiyhimself.fantazia.events;

import dev._100media.capabilitysyncer.network.SimpleLevelCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.ClientValues;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.DoubleJump;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataManager;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.AuraOwning;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.CommonData;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.EvasionData;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.HatchetStuck;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectManager;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.StunEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureManager;
import net.arkadiyhimself.fantazia.api.capability.itemstack.StackDataGetter;
import net.arkadiyhimself.fantazia.api.capability.itemstack.StackDataManager;
import net.arkadiyhimself.fantazia.api.capability.itemstack.stackdata.CommonStackData;
import net.arkadiyhimself.fantazia.api.capability.itemstack.stackdata.HiddenPotential;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCap;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapGetter;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.arkadiyhimself.fantazia.api.fantazicevents.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.data.talents.*;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.entities.goals.StandStillGoal;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.*;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.arkadiyhimself.fantazia.util.commands.*;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.compress.utils.Lists;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Fantazia.MODID)
public class FantazicEvents {
    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        LivingEntity livingTarget = event.getEntity();
        if (livingTarget.level().isClientSide()) return;
        DamageSource source = event.getSource();
        DataManager dataManager = DataGetter.getUnwrap(livingTarget);
        if (source.is(FTZDamageTypeTags.NON_LETHAL)) {
            event.setCanceled(true);
            livingTarget.setHealth(1f);
        }
        if (dataManager != null) {
            CommonData commonData = dataManager.takeData(CommonData.class);
            if (SpellHelper.hasSpell(livingTarget, FTZSpells.ENTANGLE) && commonData != null && commonData.getPrevHP() > livingTarget.getMaxHealth() * 0.1f) {
                boolean work = FTZEvents.ForgeExtension.onDeathPrevention(event.getEntity(), FTZSpells.ENTANGLE);
                if (work) {
                    EffectCleansing.tryCleanseAll(livingTarget, FTZSpells.ENTANGLE.hasCleanse() ? FTZSpells.ENTANGLE.getStrength() : Cleanse.POWERFUL, MobEffectCategory.HARMFUL);
                    event.setCanceled(true);
                    livingTarget.setHealth(livingTarget.getMaxHealth() * 0.1f);
                }
            }
        }


        if (source.getEntity() instanceof LivingEntity livingEntity) {
            MobEffectInstance instance;
            if ((instance = livingEntity.getEffect(FTZMobEffects.FURY)) != null) {
                boolean amulet = SpellHelper.hasSpell(livingEntity, FTZSpells.DAMNED_WRATH);
                if (amulet) EffectHelper.effectWithoutParticles(livingEntity, instance.getEffect(), instance.getDuration() + 100, instance.getAmplifier());
                else EffectCleansing.forceCleanse(livingEntity, FTZMobEffects.FURY);

                SoundEvent soundEvent = amulet ? FTZSoundEvents.FURY_DISPEL : FTZSoundEvents.FURY_PROLONG;
                if (livingEntity instanceof ServerPlayer serverPlayer) {
                    NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(soundEvent), serverPlayer);
                }
            }
        }
    }
    @SubscribeEvent
    public static void entityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            DataManager dataManager = DataGetter.getUnwrap(livingEntity);
            if (dataManager != null) dataManager.getData(AuraOwning.class).ifPresent(AuraOwning::clearAll);
        }
        FeatureGetter.get(event.getEntity()).ifPresent(FeatureManager::onDeath);
    }
    @SubscribeEvent
    public static void livingDrops(LivingDropsEvent event) {
        if (event.getSource() == null) return;
        LivingEntity killed = event.getEntity();
        if (event.getSource().getDirectEntity() instanceof LivingEntity killer) {
            int level = killer.getMainHandItem().getEnchantmentLevel(FTZEnchantments.DISINTEGRATION);
            List<ItemEntity> toRemove = Lists.newArrayList();
            if (level > 0) for (ItemEntity entity : event.getDrops()) {
                float multiplier = switch (entity.getItem().getItem().getRarity(entity.getItem())) {
                    case COMMON -> 1f;
                    case UNCOMMON -> 1.25f;
                    case RARE -> 1.75f;
                    case EPIC -> 2.5f;
                };
                Item item = entity.getItem().getItem();
                boolean flag1 = (FTZItemTags.hasTag(item, FTZItemTags.NO_DISINTEGRATION));
                StackDataManager stackDataManager = StackDataGetter.getUnwrap(entity.getItem());
                boolean flag2 = false;
                if (stackDataManager != null) {
                    CommonStackData commonStackData = stackDataManager.takeData(CommonStackData.class);
                    if (commonStackData != null && commonStackData.pickedUp()) flag2 = true;
                }
                if (!flag1 && !flag2) {
                    FantazicCombat.dropExperience(killed, level * 1.5f * multiplier);
                    toRemove.add(entity);
                }
            }
            for (ItemEntity itemEntity : toRemove) event.getDrops().remove(itemEntity);
        }

        DataManager dataManager = DataGetter.getUnwrap(killed);
        if (dataManager == null) return;
        HatchetStuck hatchetStuck = dataManager.takeData(HatchetStuck.class);
        if (hatchetStuck != null) hatchetStuck.dropHatchet();
    }
    @SubscribeEvent
    public static void livingPickupItem(VanillaEventsExtension.LivingPickUpItemEvent event) {
        if (event.getEntity().hasEffect(FTZMobEffects.STUN)) event.setCanceled(true);
        if (!event.isCanceled()) {
            StackDataManager stackDataManager = StackDataGetter.getUnwrap(event.getItemEntity().getItem());
            if (stackDataManager == null) return;
            stackDataManager.getData(CommonStackData.class).ifPresent(CommonStackData::picked);
        }
    }
    @SubscribeEvent
    public static void criticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        float modifier = event.getDamageModifier();
        ItemStack stack = event.getEntity().getMainHandItem();
        int i = stack.getEnchantmentLevel(FTZEnchantments.DECISIVE_STRIKE);
        if (i > 0 && event.isVanillaCritical()) event.setDamageModifier(modifier + i * 0.25f + 0.25f);
    }
    @SubscribeEvent
    public static void livingHeal(LivingHealEvent event) {
        HealingSources healingSources = LevelCapHelper.healingSources(event.getEntity().level());
        if (healingSources == null) return;
        boolean flag = AdvancedHealing.heal(event.getEntity(), healingSources.generic(), event.getAmount());
        if (flag) event.setCanceled(true);
        if (SpellHelper.hasSpell(event.getEntity(), FTZSpells.ENTANGLE) || event.getEntity().hasEffect(FTZMobEffects.FROZEN)) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void livingDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        AbilityGetter.get(target).ifPresent(abilityManager -> abilityManager.onHit(event));
        EffectGetter.get(target).ifPresent(effectManager -> effectManager.onHit(event));
        DataGetter.get(target).ifPresent(dataManager -> dataManager.onHit(event));

        float pre = target.getHealth();
        float post = target.getHealth() - event.getAmount();
        if (target.level().isClientSide()) return;
        if (post < 0.3f * target.getMaxHealth() && !source.is(FTZDamageTypes.REMOVAL)) {
            if (SpellHelper.hasActiveSpell(target, FTZSpells.DAMNED_WRATH)) {
                EffectCleansing.tryCleanseAll(target, FTZSpells.DAMNED_WRATH.hasCleanse() ? FTZSpells.DAMNED_WRATH.getStrength() : Cleanse.MEDIUM, MobEffectCategory.HARMFUL);
                EffectHelper.makeFurious(target, 200);
                EffectHelper.giveBarrier(target, 20);
                if (target instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.BLOODLUST_AMULET), serverPlayer);
            }
        }
    }
    @SubscribeEvent
    public static void livingAttack(LivingAttackEvent event) {
        if (event.isCanceled()) return;
        if (event.getSource() == null) return;
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();

        if (source.is(DamageTypes.FREEZE)) EffectHelper.makeFrozen(target, 100);

        for (ResourceKey<DamageType> resourceKey : AuraHelper.damageImmunities(target)) if (source.is(resourceKey)) event.setCanceled(true);

        AbilityGetter.get(target).ifPresent(abilityManager -> abilityManager.onHit(event));
        EffectGetter.get(target).ifPresent(effectManager -> effectManager.onHit(event));
        DataGetter.get(target).ifPresent(dataManager -> dataManager.onHit(event));
        Entity attacker = event.getSource().getEntity();

        if (FantazicCombat.attemptEvasion(event)) return;

        if (attacker instanceof Warden warden && event.getSource().is(DamageTypes.SONIC_BOOM)) {
            if (target instanceof ServerPlayer player) {
                if (SpellHelper.hasActiveSpell(player, FTZSpells.REFLECT)) {
                    AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
                    if (abilityManager != null) abilityManager.getAbility(ClientValues.class).ifPresent(ClientValues::onMirrorActivation);
                    event.setCanceled(true);
                    VisualHelper.rayOfParticles(player, warden, ParticleTypes.SONIC_BOOM);
                    player.level().playSound(null, player.blockPosition(), FTZSoundEvents.MYSTIC_MIRROR, SoundSource.NEUTRAL);
                    warden.hurt(event.getEntity().level().damageSources().sonicBoom(player), 15f);
                }
            }
            int num = switch (Minecraft.getInstance().options.particles().get()) {
                case MINIMAL -> 20;
                case DECREASED -> 30;
                case ALL -> 40;
            };
            if (target.hasEffect(FTZMobEffects.REFLECT)) {
                event.setCanceled(true);
                EffectCleansing.forceCleanse(target, FTZMobEffects.REFLECT);
                VisualHelper.rayOfParticles(target, warden, ParticleTypes.SONIC_BOOM);
                for (int i = 0; i < num; i++) VisualHelper.randomParticleOnModel(target, ParticleTypes.ENCHANT, VisualHelper.ParticleMovement.FROM_CENTER);
                target.level().playSound(null, target.blockPosition(), FTZSoundEvents.REFLECT, SoundSource.PLAYERS);
                warden.hurt(event.getEntity().level().damageSources().sonicBoom(target), 15f);
            } else if (target.hasEffect(FTZMobEffects.DEFLECT)) {
                event.setCanceled(true);
                EffectCleansing.forceCleanse(target, FTZMobEffects.DEFLECT);
                for (int i = 0; i < num; i++) VisualHelper.randomParticleOnModel(target, ParticleTypes.ENCHANT, VisualHelper.ParticleMovement.FROM_CENTER);
                target.level().playSound(null, target.blockPosition(), FTZSoundEvents.REFLECT, SoundSource.PLAYERS);
            }
        }
    }
    @SubscribeEvent
    public static void livingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        FantazicCombat.meleeAttack(event);
        DamageSource source = event.getSource();
        float amount = event.getAmount();
        LivingEntity target = event.getEntity();

        AbilityGetter.get(target).ifPresent(abilityManager -> abilityManager.onHit(event));
        EffectGetter.get(target).ifPresent(effectManager -> effectManager.onHit(event));
        DataGetter.get(target).ifPresent(dataManager -> dataManager.onHit(event));

        for (Map.Entry<ResourceKey<DamageType>, Float> entry : AuraHelper.damageMultipliers(target).entrySet()) if (source.is(entry.getKey())) event.setAmount(amount * entry.getValue());

        StackDataGetter.get(event.getEntity().getMainHandItem()).ifPresent(stackDataManager -> stackDataManager.onHit(event));
        if (event.getEntity() instanceof Player player && !source.is(FTZDamageTypes.REMOVAL)) player.getCooldowns().addCooldown(FTZItems.TRANQUIL_HERB, 100);
    }
    @SubscribeEvent
    public static void effectApplicable(MobEffectEvent.Applicable event) {
        MobEffect effect = event.getEffectInstance().getEffect();
        LivingEntity entity = event.getEntity();
        if (effect == FTZMobEffects.STUN && event.getEntity() instanceof Player player && (player.isCreative() || player.isSpectator())) event.setResult(Event.Result.DENY);
        if (!FTZMobEffects.Application.isAffected(entity.getType(), effect)) event.setResult(Event.Result.DENY);
        if (FTZMobEffects.Application.isImmune(entity.getType(), effect)) event.setResult(Event.Result.DENY);
    }
    @SubscribeEvent
    public static void effectAdded(MobEffectEvent.Added event) {
        MobEffect effect = event.getEffectInstance().getEffect();
        LivingEntity livingEntity = event.getEntity();
        EffectGetter.get(livingEntity).ifPresent(effectManager -> effectManager.effectAdded(event.getEffectInstance()));
        if (FTZMobEffectTags.hasTag(effect, FTZMobEffectTags.INTERRUPT)) ActionsHelper.interrupt(livingEntity);
    }
    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof PathfinderMob mob) mob.goalSelector.addGoal(1, new StandStillGoal(mob));
        if (entity instanceof LivingEntity livingEntity) {
            AttributeInstance lifesteal = livingEntity.getAttribute(FTZAttributes.LIFESTEAL);
            if (lifesteal != null && livingEntity.isInvertedHealAndHarm()) lifesteal.setBaseValue(0.2f);
            DataManager dataManager = DataGetter.getUnwrap(livingEntity);
            if (dataManager == null) return;
            if (livingEntity.getAttribute(FTZAttributes.EVASION) != null) dataManager.grantData(EvasionData::new);
        }
    }
    @SubscribeEvent
    public static void levelTick(TickEvent.LevelTickEvent event) {
        LevelCapGetter.get(event.level).ifPresent(LevelCap::tick);
    }

    @SubscribeEvent
    public static void effectRemoved(MobEffectEvent.Remove event) {
        MobEffect effect = event.getEffect();
        LivingEntity livingEntity = event.getEntity();
        EffectGetter.get(livingEntity).ifPresent(effectManager -> effectManager.effectEnded(event.getEffectInstance()));

        if (livingEntity instanceof ServerPlayer player) {
            if (effect == FTZMobEffects.STUN) NetworkHandler.sendToPlayer(new PlayAnimationS2C(""), player);
        }
    }
    @SubscribeEvent
    public static void effectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance == null) return;
        MobEffect effect = effectInstance.getEffect();
        LivingEntity livingEntity = event.getEntity();
        EffectGetter.get(livingEntity).ifPresent(effectManager -> effectManager.effectEnded(effectInstance));

    }
    @SubscribeEvent
    public static void changeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if(event.getNewGameMode() == GameType.CREATIVE || event.getNewGameMode() == GameType.SPECTATOR) {
            if (event.getEntity().hasEffect(FTZMobEffects.STUN)) EffectCleansing.forceCleanse(event.getEntity(), FTZMobEffects.STUN);
            EffectManager effectManager = EffectGetter.getUnwrap(event.getEntity());
            if (effectManager == null) return;
            effectManager.getEffect(StunEffect.class).ifPresent(StunEffect::ended);
        }
    }
    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        Player player = event.getEntity();
        if (event.isWasDeath()) {
            List<BasicTalent> talents = TalentHelper.getTalents(player);
            for (BasicTalent talent : talents) if (talent instanceof AttributeTalent attributeTalent) attributeTalent.applyModifier(player);
        }
    }
    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();

        if (livingEntity.isDeadOrDying() || livingEntity.level().isClientSide()) return;
        if (livingEntity.getHealth() > livingEntity.getMaxHealth()) {
            float amo = livingEntity.getHealth() - livingEntity.getMaxHealth();
            livingEntity.hurt(new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(FTZDamageTypes.REMOVAL)), amo);
        }
        EffectGetter.get(livingEntity).ifPresent(EffectManager::tick);
        DataGetter.get(livingEntity).ifPresent(DataManager::tick);
        AuraHelper.aurasTick(livingEntity);
        if (!livingEntity.getActiveEffects().isEmpty() && livingEntity.hasEffect(FTZMobEffects.FURY)) {
            Collection<MobEffectInstance> effects = new ArrayList<>(livingEntity.getActiveEffects());
            for (MobEffectInstance effect : effects) {
                MobEffect mobEffect = effect.getEffect();
                if (mobEffect == FTZMobEffects.STUN) effect.tick(livingEntity, () -> {});
            }
        }
    }
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        // abilities
        if (event.player instanceof ServerPlayer serverPlayer) {
            if (event.phase == TickEvent.Phase.END) {
                AbilityGetter.get(serverPlayer).ifPresent(AbilityManager::tick);

                for (ItemStack stack : InventoryHelper.fullInventory(serverPlayer)) {
                    StackDataManager stackDataManager = StackDataGetter.getUnwrap(stack);
                    if (stackDataManager == null) continue;
                    stackDataManager.getData(HiddenPotential.class).ifPresent(HiddenPotential::tick);
                }
            }
            if (SpellHelper.hasSpell(serverPlayer, FTZSpells.DEVOUR)) {
                EffectHelper.effectWithoutParticles(serverPlayer, MobEffects.HUNGER, 2);
                event.player.causeFoodExhaustion(serverPlayer.getFoodData().getSaturationLevel() > 0 ? 0.1f : 0.01f);
            }
        }
    }
    @SubscribeEvent
    public static void shieldBlock(ShieldBlockEvent event) {
        Entity blocker = event.getEntity();
        Entity attacker = event.getDamageSource().getEntity();
        if (attacker instanceof ThrownHatchet thrownHatchet && thrownHatchet.phasingTicks() > 0) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void commandRegister(final RegisterCommandsEvent event) {
        AuraCarrierCommand.register(event.getDispatcher());
        SpellCastCommand.register(event.getDispatcher());
        CooldownCommand.register(event.getDispatcher(), event.getBuildContext());
        FullHealCommand.register(event.getDispatcher());
        AdvancedHealCommand.register(event.getDispatcher(), event.getBuildContext());
        ResetTalentsCommand.register(event.getDispatcher());
        WisdomCommand.register(event.getDispatcher());
    }
    @SubscribeEvent
    public static void curioChange(CurioChangeEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (event.isCanceled()) return;
        DataManager dataManager = DataGetter.getUnwrap(livingEntity);
        if (dataManager == null) return;
        dataManager.getData(AuraOwning.class).ifPresent(auraOwning -> auraOwning.onCurioEquip(event.getTo()));
        dataManager.getData(AuraOwning.class).ifPresent(auraOwning -> auraOwning.onCurioUnEquip(event.getFrom()));
    }
    @SubscribeEvent
    public static void curioUnEquip(CurioUnequipEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (player.getCooldowns().isOnCooldown(event.getStack().getItem()) && !player.getAbilities().instabuild) event.setResult(Event.Result.DENY);
            if (event.getResult() == Event.Result.DENY) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.DENIED), player);
        }
    }
    @SubscribeEvent
    public static void gameEvent(VanillaGameEvent event) {
        if (event.getVanillaEvent().is(GameEventTags.VIBRATIONS) && event.getContext().sourceEntity() != null && event.getContext().sourceEntity() instanceof LivingEntity entity && !entity.level().isClientSide()) {
            AABB aabb = entity.getBoundingBox().inflate(8);
            entity.level().getEntitiesOfClass(ServerPlayer.class, aabb).forEach(player -> AbilityHelper.tryListen((ServerLevel) entity.level(), event.getContext(), event.getEventPosition(), player));
        }
    }
    @SubscribeEvent
    public static void itemToss(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
        StackDataManager stackDataManager = StackDataGetter.getUnwrap(stack);
        if (stackDataManager == null) return;
        stackDataManager.getData(HiddenPotential.class).ifPresent(HiddenPotential::reset);
    }
    @SubscribeEvent
    public static void livingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager == null) return;
            abilityManager.getAbility(DoubleJump.class).ifPresent(DoubleJump::regularJump);
        }
    }
    @SubscribeEvent
    public static void livingKnockBack(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource lastSource = entity.getLastDamageSource();
        if (lastSource != null && lastSource.is(FTZDamageTypeTags.NO_KNOCKBACK)) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!event.getEntity().hasEffect(FTZMobEffects.STUN)) {
            EffectManager effectManager = EffectGetter.getUnwrap(player);
            if (effectManager != null) effectManager.getEffect(StunEffect.class).ifPresent(StunEffect::ended);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            LevelCap levelCap = LevelCapGetter.getLevelCap(event.getEntity().level());
            if (levelCap != null) NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SimpleLevelCapabilityStatusPacket(LevelCapGetter.LEVEL_CAP_RL, levelCap));
        }
    }
    @SubscribeEvent
    public static void mobAttack(VanillaEventsExtension.MobAttackEvent event) {
        if (event.getEntity().hasEffect(FTZMobEffects.DISARM) || event.getEntity().hasEffect(FTZMobEffects.STUN)) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void attackEntity(AttackEntityEvent event) {
        Player attacker = event.getEntity();
        ItemStack stack = attacker.getMainHandItem();
        if (event.getTarget() instanceof LivingEntity target) {
            int i = event.getEntity().getMainHandItem().getEnchantmentLevel(FTZEnchantments.ICE_ASPECT);
            if (i > 0) {
                EffectHelper.effectWithoutParticles(target, FTZMobEffects.FROZEN, 40 + i * 20);
            }
            if (attacker instanceof ServerPlayer serverPlayer) {
                if (attacker.hasEffect(FTZMobEffects.DISARM)) {
                    NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.DENIED), serverPlayer);
                    event.setCanceled(true);
                }
            }
        }
    }
    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player == null) return;

        AbilityGetter.get(player).ifPresent(AbilityManager::respawn);
        EffectGetter.get(player).ifPresent(EffectManager::respawm);
        DataGetter.get(player).ifPresent(DataManager::respawn);
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingChangeTarget(LivingChangeTargetEvent event) {
        if (event.getEntity().hasEffect(FTZMobEffects.STUN)) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void livingUseTotem(LivingUseTotemEvent event) {
        // yes.
        boolean work = FTZEvents.ForgeExtension.onDeathPrevention(event.getEntity(), Items.TOTEM_OF_UNDYING);
        if (!work) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void onDeathPrevention(VanillaEventsExtension.DeathPreventionEvent event) {
        if (event.getEntity().hasEffect(FTZMobEffects.DOOMED)) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (FantazicCombat.attemptEvasion(event)) return;

        HitResult result = event.getRayTraceResult();
        Projectile projectile = event.getProjectile();
        if (result instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            if (projectile instanceof Snowball) entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze(), entity.getTicksFrozen() + 50));
            if (projectile instanceof AbstractArrow arrow && entity instanceof LivingEntity livingEntity) FantazicCombat.arrowImpact(arrow, livingEntity);
        }
    }
    @SubscribeEvent
    public static void onAdvancedHeal(VanillaEventsExtension.AdvancedHealEvent event) {
        LivingEntity livingEntity = event.getEntity();
        EffectGetter.get(livingEntity).ifPresent(effectManager -> effectManager.onHeal(event));
    }
    @SubscribeEvent
    public static void reloadListener(AddReloadListenerEvent event) {
        event.addListener(new TalentLoad());
        event.addListener(new TalentHierarchiesLoad());
    }
    @SubscribeEvent
    public static void levelLoad(LevelEvent.Load event) {
        TalentTreeData.reload();
    }
    @SubscribeEvent
    public static void advancementProgress(AdvancementEvent.AdvancementProgressEvent event) {
        if (event.getAdvancementProgress().isDone()) TalentHelper.onAdvancementObtain(event.getAdvancement(), event.getEntity());
    }
}
