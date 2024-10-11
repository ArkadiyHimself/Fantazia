package net.arkadiyhimself.fantazia.events;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.AttachmentHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.CommonDataHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.StuckHatchetHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.StunEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityManager;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DoubleJumpHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.OwnedAurasHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.api.custom_events.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.api.data_component.HiddenPotentialHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.data.loot.LootInstancesManager;
import net.arkadiyhimself.fantazia.data.spawn.MobEffectsOnSpawnManager;
import net.arkadiyhimself.fantazia.data.talent.TalentHelper;
import net.arkadiyhimself.fantazia.data.talent.TalentTreeData;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentHierarchyManager;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentManager;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentTabManager;
import net.arkadiyhimself.fantazia.data.talent.reload.WisdomRewardManager;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.entities.goals.StandStillGoal;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.networking.packets.attachment_syncing.PlayerAbilityUpdateS2C;
import net.arkadiyhimself.fantazia.networking.packets.stuff.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.networking.packets.stuff.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.*;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.arkadiyhimself.fantazia.util.commands.*;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.GameEventTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.item.ItemEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.compress.utils.Lists;
import top.theillusivec4.curios.api.event.CurioCanUnequipEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.*;

@EventBusSubscriber(modid = Fantazia.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommonEvents {
    private CommonEvents() {}

    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        LivingEntity livingTarget = event.getEntity();
        DamageSource source = event.getSource();
        if (livingTarget.level().isClientSide()) return;

        if (source.is(FTZDamageTypeTags.NON_LETHAL)) {
            event.setCanceled(true);
            livingTarget.setHealth(1f);
        }

        CommonDataHolder commonDataHolder = LivingDataGetter.takeHolder(livingTarget, CommonDataHolder.class);
        Holder<AbstractSpell> entangle = FTZSpells.ENTANGLE;
        if (SpellHelper.hasSpell(livingTarget, entangle) && commonDataHolder != null && commonDataHolder.getPrevHP() > livingTarget.getMaxHealth() * 0.1f && FTZHooks.ForgeExtension.onDeathPrevention(event.getEntity(),entangle)) {
            EffectCleansing.tryCleanseAll(livingTarget, Cleanse.POWERFUL, MobEffectCategory.HARMFUL);
            event.setCanceled(true);
            livingTarget.setHealth(livingTarget.getMaxHealth() * 0.1f);
        }

        if (source.getEntity() instanceof LivingEntity attacker) {
            MobEffectInstance instance;
            if ((instance = attacker.getEffect(FTZMobEffects.FURY)) != null) {
                boolean amulet = SpellHelper.hasSpell(attacker, FTZSpells.DAMNED_WRATH);
                if (amulet) LivingEffectHelper.effectWithoutParticles(attacker, instance.getEffect(), instance.getDuration() + 100, instance.getAmplifier());
                else EffectCleansing.forceCleanse(attacker, FTZMobEffects.FURY);

                SoundEvent soundEvent = amulet ? FTZSoundEvents.FURY_PROLONG.get() : FTZSoundEvents.FURY_DISPEL.get();
                if (attacker instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(soundEvent));
            }
            if (attacker instanceof Player player) {
                ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(livingTarget.getType());
                TalentsHolder.ProgressHolder progressHolder = PlayerAbilityHelper.getProgressHolder(player);
                if (progressHolder != null) progressHolder.award("slayed", id);
            }
            if ((instance = livingTarget.getEffect(FTZMobEffects.CURSED_MARK)) != null) {
                int dur = 600 + instance.getAmplifier() * 600;
                LivingEffectHelper.makeDoomed(attacker, dur);
            }
        }
    }

    @SubscribeEvent
    public static void entityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Player player) PlayerAbilityGetter.acceptConsumer(player, OwnedAurasHolder.class, OwnedAurasHolder::clearAll);
        event.getEntity().getData(FTZAttachmentTypes.ARMOR_STAND_COMMAND_AURA).onDeath();
    }

    @SubscribeEvent
    public static void livingDrops(LivingDropsEvent event) {
        LivingEntity killed = event.getEntity();
        if (event.getSource().getDirectEntity() instanceof LivingEntity killer) {
            Registry<Enchantment> enchantmentRegistry = killed.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

            Optional<Holder.Reference<Enchantment>> disInt = enchantmentRegistry.getHolder(FTZEnchantments.DISINTEGRATION);
            int level = disInt.map(enchantmentReference -> killer.getMainHandItem().getEnchantmentLevel(enchantmentReference)).orElse(0);
            List<ItemEntity> toRemove = Lists.newArrayList();
            if (level > 0) for (ItemEntity entity : event.getDrops()) {
                float multiplier = switch (entity.getItem().getRarity()) {
                    case COMMON -> 1f;
                    case UNCOMMON -> 1.25f;
                    case RARE -> 1.75f;
                    case EPIC -> 2.5f;
                };
                Item item = entity.getItem().getItem();
                if (!FTZItemTags.hasTag(item, FTZItemTags.NO_DISINTEGRATION)) {
                    FantazicCombat.dropExperience(killed, level * 1.5f * multiplier, killer);
                    toRemove.add(entity);
                }
            }
            for (ItemEntity itemEntity : toRemove) event.getDrops().remove(itemEntity);
        }


        LivingDataGetter.acceptConsumer(killed, StuckHatchetHolder.class, StuckHatchetHolder::dropHatchet);
    }

    @SubscribeEvent
    public static void livingPickupItem(VanillaEventsExtension.LivingPickUpItemEvent event) {
        if (event.getEntity().hasEffect(FTZMobEffects.STUN)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void criticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        float modifier = event.getDamageMultiplier();
        ItemStack stack = event.getEntity().getMainHandItem();

        Registry<Enchantment> enchantmentRegistry = player.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Optional<Holder.Reference<Enchantment>> desStrike = enchantmentRegistry.getHolder(FTZEnchantments.DECISIVE_STRIKE);
        int i = desStrike.map(stack::getEnchantmentLevel).orElse(0);
        if (i > 0 && event.isVanillaCritical()) event.setDamageMultiplier(modifier + i * 0.25f + 0.25f);
    }

    @SubscribeEvent
    public static void livingHeal(LivingHealEvent event) {
        HealingSourcesHolder healingSources = LevelAttributesHelper.getHealingSources(event.getEntity().level());
        if (healingSources == null) return;
        boolean flag = AdvancedHealing.tryHeal(event.getEntity(), healingSources.generic(), event.getAmount());
        if (flag) event.setCanceled(true);
        if (SpellHelper.hasSpell(event.getEntity(), FTZSpells.ENTANGLE) || event.getEntity().hasEffect(FTZMobEffects.FROZEN)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void livingHurt(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        float amount = event.getNewDamage();
        LivingEntity target = event.getEntity();
        if (event.getEntity().level().isClientSide()) return;
        if (source.is(FTZDamageTypes.BLEEDING)) VisualHelper.randomParticleOnModel(target, FTZParticleTypes.BLOOD.random(), VisualHelper.ParticleMovement.FALL);

        FantazicCombat.meleeAttack(event);

        if (target instanceof Player player) player.getData(FTZAttachmentTypes.ABILITY_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.DATA_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.EFFECT_MANAGER).onHit(event);

        for (Map.Entry<ResourceKey<DamageType>, Float> entry : AuraHelper.damageMultipliers(target).entrySet()) if (source.is(entry.getKey())) event.setNewDamage(amount * entry.getValue());

        if (event.getEntity() instanceof Player player && !source.is(FTZDamageTypes.REMOVAL)) player.getCooldowns().addCooldown(FTZItems.TRANQUIL_HERB.get(), 100);
    }

    @SubscribeEvent
    public static void livingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        if (target instanceof Player player) {
            player.getData(FTZAttachmentTypes.ABILITY_MANAGER).onHit(event);
            for (ItemStack itemStack : InventoryHelper.fullInventory(player)) if (itemStack.has(FTZDataComponentTypes.HIDDEN_POTENTIAL)) itemStack.update(FTZDataComponentTypes.HIDDEN_POTENTIAL, HiddenPotentialHolder.DEFAULT, holder -> holder.onHit(event));
        }

        target.getData(FTZAttachmentTypes.DATA_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.EFFECT_MANAGER).onHit(event);

        float pre = target.getHealth();
        float post = target.getHealth() - event.getNewDamage();
        if (target.level().isClientSide()) return;
        Holder<AbstractSpell> damned = FTZSpells.DAMNED_WRATH;
        if (post < 0.3f * target.getMaxHealth() && !source.is(FTZDamageTypes.REMOVAL)) {
            if (SpellHelper.hasActiveSpell(target, damned)) {
                EffectCleansing.tryCleanseAll(target, Cleanse.MEDIUM, MobEffectCategory.HARMFUL);
                LivingEffectHelper.makeFurious(target, 200);
                LivingEffectHelper.giveBarrier(target, 20);
                if (target instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.DAMNED_WRATH.get()));
            }
        }
    }

    @SubscribeEvent
    public static void livingAttack(LivingIncomingDamageEvent event) {
        if (event.isCanceled()) return;
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();

        if (source.is(DamageTypes.FREEZE)) LivingEffectHelper.makeFrozen(target, 100);

        for (ResourceKey<DamageType> resourceKey : AuraHelper.damageImmunities(target)) if (source.is(resourceKey)) event.setCanceled(true);

        if (target instanceof Player player) player.getData(FTZAttachmentTypes.ABILITY_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.DATA_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.EFFECT_MANAGER).onHit(event);
        Entity attacker = event.getSource().getEntity();

        if (FantazicCombat.attemptEvasion(event) || SpellHelper.wardenSonicBoom(event)) return;
    }

    @SubscribeEvent
    public static void effectApplicable(MobEffectEvent.Applicable event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null) return;
        Holder<MobEffect> effect = instance.getEffect();
        LivingEntity entity = event.getEntity();
        if (effect == FTZMobEffects.STUN && event.getEntity() instanceof Player player && (player.isCreative() || player.isSpectator())) event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        if (!FTZMobEffects.Application.isApplicable(entity.getType(), effect)) event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
    }

    @SubscribeEvent
    public static void effectAdded(MobEffectEvent.Added event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null) return;
        Holder<MobEffect> effect = instance.getEffect();
        LivingEntity livingEntity = event.getEntity();
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).effectAdded(event.getEffectInstance());
        if (FTZMobEffectTags.hasTag(effect, FTZMobEffectTags.INTERRUPT)) ActionsHelper.interrupt(livingEntity);
    }

    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof PathfinderMob mob) mob.goalSelector.addGoal(5, new StandStillGoal(mob));
        if (entity instanceof LivingEntity livingEntity) {
            AttributeInstance lifesteal = livingEntity.getAttribute(FTZAttributes.LIFESTEAL);
            if (lifesteal != null && livingEntity.isInvertedHealAndHarm()) lifesteal.setBaseValue(0.2);
            if (livingEntity.getAttribute(FTZAttributes.EVASION) != null) livingEntity.getData(FTZAttachmentTypes.DATA_MANAGER).putHolder(EvasionHolder::new);

            AttributeInstance evasion = livingEntity.getAttribute(FTZAttributes.EVASION);
            if (evasion != null && livingEntity.getType() == EntityType.ENDERMAN) evasion.setBaseValue(30);

            AttributeInstance durability = livingEntity.getAttribute(FTZAttributes.MAX_STUN_POINTS);
            if (durability != null) {
                double base = FTZAttributes.MAX_STUN_POINTS.value().getDefaultValue();
                if (livingEntity.getType() == EntityType.WARDEN) base = 2500;
                if (livingEntity.getType() == EntityType.IRON_GOLEM) base = 1500;
                durability.setBaseValue(base);
            }

            if (livingEntity instanceof Mob mob && !event.loadedFromDisk()) FantazicCombat.grantEffectsOnSpawn(mob);
        }
    }

    @SubscribeEvent
    public static void levelTick(LevelTickEvent.Pre event) {
        event.getLevel().getData(FTZAttachmentTypes.LEVEL_ATTRIBUTES).tick();
    }

    @SubscribeEvent
    public static void effectRemoved(MobEffectEvent.Remove event) {
        Holder<MobEffect> effect = event.getEffect();
        LivingEntity livingEntity = event.getEntity();
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).effectEnded(event.getEffect());

        if (livingEntity instanceof ServerPlayer player) if (effect == FTZMobEffects.STUN) PacketDistributor.sendToPlayer(player, new PlayAnimationS2C(""));
    }

    @SubscribeEvent
    public static void effectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance == null) return;
        Holder<MobEffect> effect = effectInstance.getEffect();
        LivingEntity livingEntity = event.getEntity();
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).effectEnded(effect);
    }

    @SubscribeEvent
    public static void changeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getNewGameMode() == GameType.CREATIVE || event.getNewGameMode() == GameType.SPECTATOR) if (event.getEntity().hasEffect(FTZMobEffects.STUN)) EffectCleansing.forceCleanse(event.getEntity(), FTZMobEffects.STUN);
    }

    @SubscribeEvent
    public static void livingTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        AuraHelper.aurasTick(entity);

        if (!entity.level().isClientSide()) AttachmentHelper.tickAttachments(entity);

        if (!(entity instanceof LivingEntity livingEntity) || livingEntity.level().isClientSide() || livingEntity.isDeadOrDying()) return;


        if (livingEntity.getHealth() > livingEntity.getMaxHealth()) {
            float amo = livingEntity.getHealth() - livingEntity.getMaxHealth();
            DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(livingEntity.level());
            if (sources != null) livingEntity.hurt(sources.removal(), amo);
        }

        if (!livingEntity.getActiveEffects().isEmpty() && livingEntity.hasEffect(FTZMobEffects.FURY)) {
            Collection<MobEffectInstance> effects = new ArrayList<>(livingEntity.getActiveEffects());
            for (MobEffectInstance effect : effects) if (effect.getEffect() == FTZMobEffects.STUN) effect.tick(livingEntity, () -> {});
        }
    }

    @SubscribeEvent
    public static void shieldBlock(LivingShieldBlockEvent event) {
        Entity blocker = event.getEntity();
        Entity attacker = event.getDamageSource().getEntity();
        if (attacker instanceof ThrownHatchet thrownHatchet && thrownHatchet.phasingTicks() > 0) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void commandRegister(final RegisterCommandsEvent event) {
        AuraCarrierCommand.register(event.getDispatcher(), event.getBuildContext());
        SpellCastCommand.register(event.getDispatcher());
        CooldownCommand.register(event.getDispatcher(), event.getBuildContext());
        AdvancedHealCommand.register(event.getDispatcher(), event.getBuildContext());
        ResetCommand.register(event.getDispatcher());
        WisdomCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void curioChange(CurioChangeEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player player) {
            PlayerAbilityManager playerAbilityManager = player.getData(FTZAttachmentTypes.ABILITY_MANAGER);
            playerAbilityManager.onCurioEquip(event.getTo());
            playerAbilityManager.onCurioUnEquip(event.getFrom());
        }
    }

    @SubscribeEvent
    public static void curioUnEquip(CurioCanUnequipEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!player.hasInfiniteMaterials() && event.getStack().getItem() instanceof SpellCasterItem spellCasterItem && SpellHelper.onCooldown(player, spellCasterItem.getSpell())) event.setUnequipResult(TriState.FALSE);
            if (event.getUnequipResult() == TriState.FALSE) PacketDistributor.sendToPlayer(player, new PlaySoundForUIS2C(FTZSoundEvents.DENIED.get()));
        }
    }

    @SubscribeEvent
    public static void gameEvent(VanillaGameEvent event) {
        if (event.getVanillaEvent().is(GameEventTags.VIBRATIONS) && event.getContext().sourceEntity() != null && event.getContext().sourceEntity() instanceof LivingEntity entity && !entity.level().isClientSide()) {
            AABB aabb = entity.getBoundingBox().inflate(8);
            entity.level().getEntitiesOfClass(ServerPlayer.class, aabb).forEach(player -> PlayerAbilityHelper.tryListen((ServerLevel) entity.level(), event.getContext(), event.getEventPosition(), player));
        }
    }

    @SubscribeEvent
    public static void itemToss(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
        if (stack.has(FTZDataComponentTypes.HIDDEN_POTENTIAL)) stack.update(FTZDataComponentTypes.HIDDEN_POTENTIAL, HiddenPotentialHolder.DEFAULT, HiddenPotentialHolder::reset);
    }

    @SubscribeEvent
    public static void livingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) PlayerAbilityGetter.acceptConsumer(player, DoubleJumpHolder.class, DoubleJumpHolder::regularJump);
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!event.getEntity().hasEffect(FTZMobEffects.STUN)) LivingEffectGetter.acceptConsumer(player, StunEffect.class, StunEffect::ended);

        if (player instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlayerAbilityUpdateS2C(serverPlayer.getData(FTZAttachmentTypes.ABILITY_MANAGER).serializeNBT(serverPlayer.registryAccess()), serverPlayer.getId()));
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
            Registry<Enchantment> enchantmentRegistry = attacker.registryAccess().registryOrThrow(Registries.ENCHANTMENT);


            Optional<Holder.Reference<Enchantment>> iceAspect = enchantmentRegistry.getHolder(FTZEnchantments.ICE_ASPECT);
            int i = iceAspect.map(stack::getEnchantmentLevel).orElse(0);
            if (i > 0) LivingEffectHelper.effectWithoutParticles(target, FTZMobEffects.FROZEN, 40 + i * 20);

            if (attacker instanceof ServerPlayer serverPlayer) {
                if (attacker.hasEffect(FTZMobEffects.DISARM)) {
                    PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.DENIED.get()));
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        player.getData(FTZAttachmentTypes.ABILITY_MANAGER).respawn();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingChangeTarget(LivingChangeTargetEvent event) {
        if (event.getEntity().hasEffect(FTZMobEffects.STUN)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void livingUseTotem(LivingUseTotemEvent event) {
        if (event.getEntity().hasEffect(FTZMobEffects.DOOMED)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onDeathPrevention(VanillaEventsExtension.FantazicDeathPrevention event) {
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
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).onHeal(event);
    }

    @SubscribeEvent
    public static void reloadListener(AddReloadListenerEvent event) {
        event.addListener(new TalentManager());
        event.addListener(new TalentHierarchyManager());
        event.addListener(new WisdomRewardManager());
        event.addListener(new TalentTabManager());
        event.addListener(new LootInstancesManager());
        event.addListener(new MobEffectsOnSpawnManager());
    }

    @SubscribeEvent
    public static void levelLoad(LevelEvent.Load event) {
        TalentTreeData.reload();
    }

    @SubscribeEvent
    public static void advancementProgress(AdvancementEvent.AdvancementProgressEvent event) {
        if (event.getAdvancementProgress().isDone() && event.getProgressType() == AdvancementEvent.AdvancementProgressEvent.ProgressType.GRANT) TalentHelper.onAdvancementObtain(event.getAdvancement(), event.getEntity());
    }

    @SubscribeEvent
    public static void playerBrewedPotion(PlayerBrewedPotionEvent event) {
        Player player = event.getEntity();
        PotionContents potionContents = event.getStack().get(DataComponents.POTION_CONTENTS);
        if (potionContents == null) return;

        TalentsHolder.ProgressHolder progressHolder = PlayerAbilityHelper.getProgressHolder(player);
        if (progressHolder != null) potionContents.customEffects().forEach(effect -> progressHolder.award("brewed", BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value())));
    }

    @SubscribeEvent
    public static void animalTamed(AnimalTameEvent event) {
        Player player = event.getTamer();
        Animal animal = event.getAnimal();
        ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType());
        TalentsHolder.ProgressHolder progressHolder = PlayerAbilityHelper.getProgressHolder(player);
        if (progressHolder != null) progressHolder.award("tamed", location);
    }

    @SubscribeEvent
    public static void playerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Level level = event.getEntity().level();

        ResourceKey<Level> to = event.getTo();
        TalentsHolder.ProgressHolder progressHolder = PlayerAbilityHelper.getProgressHolder(event.getEntity());
        if (progressHolder != null && !to.equals(Level.OVERWORLD)) progressHolder.award("visited_" + to.location(), 50);
    }
}
