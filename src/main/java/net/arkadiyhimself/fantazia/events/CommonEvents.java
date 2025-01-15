package net.arkadiyhimself.fantazia.events;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
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
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.*;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributes;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.api.custom_events.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.api.data_component.HiddenPotentialHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.data.loot.LootInstancesManager;
import net.arkadiyhimself.fantazia.data.spawn.EffectsOnSpawnManager;
import net.arkadiyhimself.fantazia.data.talent.TalentHelper;
import net.arkadiyhimself.fantazia.data.talent.TalentTreeData;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentHierarchyManager;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentManager;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentTabManager;
import net.arkadiyhimself.fantazia.data.talent.reload.WisdomRewardManager;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.entities.goals.PuppeteeredAttackableTargets;
import net.arkadiyhimself.fantazia.entities.goals.StandStillGoal;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.packets.attachment_syncing.PlayerAbilityUpdateS2C;
import net.arkadiyhimself.fantazia.packets.stuff.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.packets.stuff.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.*;
import net.arkadiyhimself.fantazia.registries.custom.FTZAuras;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.arkadiyhimself.fantazia.util.commands.*;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.GameEventTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
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
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.compress.utils.Lists;
import top.theillusivec4.curios.api.event.CurioCanUnequipEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
            livingTarget.setHealth(0.1f);
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
                else EffectCleansing.reduceDuration(attacker, FTZMobEffects.FURY, 100);

                SoundEvent soundEvent = amulet ? FTZSoundEvents.FURY_PROLONG.get() : FTZSoundEvents.FURY_DISPEL.get();
                if (attacker instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(soundEvent));
            }

            if (attacker instanceof Player player) {
                PlayerAbilityHelper.awardWisdom(player, "slayed", BuiltInRegistries.ENTITY_TYPE.getKey(livingTarget.getType()));

                int manaRecycle = TalentHelper.getUnlockLevel(player, Fantazia.res("mana_recycle"));
                if (manaRecycle > 0) player.addEffect(new MobEffectInstance(FTZMobEffects.SURGE, 40, manaRecycle - 1));
                PlayerAbilityGetter.acceptConsumer(player, EuphoriaHolder.class, EuphoriaHolder::increase);
            }

            if ((instance = livingTarget.getEffect(FTZMobEffects.CURSED_MARK)) != null) {
                int dur = 600 + instance.getAmplifier() * 600;
                LivingEffectHelper.makeDoomed(attacker, dur);
            }

            if (SpellHelper.hasSpell(attacker, FTZSpells.SUSTAIN)) {
                EffectCleansing.tryCleanseAll(attacker, Cleanse.BASIC, MobEffectCategory.HARMFUL);
                LivingEffectHelper.effectWithoutParticles(attacker, MobEffects.REGENERATION,50,2);
                LivingEffectHelper.effectWithoutParticles(attacker, FTZMobEffects.LAYERED_BARRIER,200,2);
                if (attacker instanceof Player player) player.getFoodData().eat(1,1);
            }
        }
    }

    @SubscribeEvent
    public static void entityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Player player) PlayerAbilityGetter.acceptConsumer(player, OwnedAurasHolder.class, OwnedAurasHolder::clearAll);
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
    public static void sleep(PlayerWakeUpEvent event) {
        if (event.wakeImmediately()) return;
        Player player = event.getEntity();
        PlayerAbilityGetter.acceptConsumer(player, StaminaHolder.class, StaminaHolder::restore);
        PlayerAbilityGetter.acceptConsumer(player, ManaHolder.class, ManaHolder::restore);
    }

    @SubscribeEvent
    public static void livingHurt(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();

        LivingEntity target = event.getEntity();
        if (event.getEntity().level().isClientSide()) return;
        if (source.is(FTZDamageTypes.BLEEDING)) VisualHelper.randomParticleOnModel(target, FTZParticleTypes.BLOOD.random(), VisualHelper.ParticleMovement.FALL);

        FantazicCombat.meleeAttack(event);

        if (target instanceof Player player) player.getData(FTZAttachmentTypes.ABILITY_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.DATA_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.EFFECT_MANAGER).onHit(event);

        float amount = event.getNewDamage();
        float auraMultiplier = AuraHelper.getDamageMultiplier(target, source.typeHolder());
        event.setNewDamage(amount * auraMultiplier);

        if (event.getEntity() instanceof Player player && !source.is(FTZDamageTypes.REMOVAL)) player.getCooldowns().addCooldown(FTZItems.TRANQUIL_HERB.get(), 100);
    }

    @SubscribeEvent
    public static void livingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        boolean removal = source.is(FTZDamageTypes.REMOVAL);

        if (removal) {
            target.hurtTime = 0;
            target.invulnerableTime = 0;
        }

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
        if (post < 0.3f * target.getMaxHealth() && !removal) {
            if (SpellHelper.hasActiveSpell(target, damned)) {
                EffectCleansing.tryCleanseAll(target, Cleanse.MEDIUM, MobEffectCategory.HARMFUL);
                LivingEffectHelper.makeFurious(target, 200);
                LivingEffectHelper.giveBarrier(target, 20);
                if (target instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.DAMNED_WRATH.get()));
            }
        }

        DamageSourcesHolder damageSourcesHolder = LevelAttributesHelper.getDamageSources(target.level());
        AuraInstance<? extends Entity> diffraction = AuraHelper.takeAuraInstance(target, FTZAuras.DIFFRACTION.value());
        if (diffraction != null && damageSourcesHolder != null) {
            if (!removal && attacker instanceof Monster monster) {
                List<? extends Entity> affected = diffraction.entitiesInside();
                affected.removeIf(entity -> entity == monster);
                for (Entity entity : affected) {
                    entity.hurt(damageSourcesHolder.removal(), event.getNewDamage() * 0.35f);
                    for (int i = 0; i < 4; i++) VisualHelper.randomParticleOnModel(entity, ParticleTypes.DAMAGE_INDICATOR, VisualHelper.ParticleMovement.ASCEND);
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingAttack(LivingIncomingDamageEvent event) {
        if (event.isCanceled()) return;
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        float damage = event.getAmount();

        if (source.is(DamageTypes.FREEZE) && !LivingEffectHelper.hasBarrier(target)) LivingEffectHelper.makeFrozen(target, 100);
        if (source.is(DamageTypes.LIGHTNING_BOLT) && !LivingEffectHelper.hasBarrier(target)) LivingEffectHelper.effectWithoutParticles(target, FTZMobEffects.ELECTROCUTED, 100);
        if (source.is(FTZDamageTypeTags.ELECTRIC)) {
            if (!LivingEffectHelper.hasBarrier(target)) ActionsHelper.interrupt(target);
            if (SpellHelper.hasSpell(target, FTZSpells.LIGHTNING_STRIKE)) event.setAmount(damage * 0.6f);
        }

        if (AuraHelper.hasImmunityTo(target, source.typeHolder())) event.setCanceled(true);

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

            if (livingEntity instanceof ServerPlayer serverPlayer) LevelAttributes.updateTracking(serverPlayer);

            if (entity instanceof Mob mob) {
                mob.targetSelector.addGoal(2, new PuppeteeredAttackableTargets<>(mob, Monster.class, true));
            }
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
    public static void entityTickPre(EntityTickEvent.Pre event) {
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
    public static void entityTickPost(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.hasEffect(FTZMobEffects.DISGUISED)) livingEntity.setInvisible(true);
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
    public static void livingEntityUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (!event.isCanceled()) LivingEffectHelper.unDisguise(event.getEntity());
    }

    @SubscribeEvent
    public static void livingEntityUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        ItemStack itemStack = event.getResultStack();
        Item item = itemStack.getItem();

        if (event.getEntity() instanceof Player player) {
            FoodProperties foodProperties = itemStack.getFoodProperties(player);
            if (foodProperties != null) PlayerAbilityHelper.awardWisdom(player,"consumed", BuiltInRegistries.ITEM.getKey(item));
        }
    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.RightClickItem event) {
        if (event.getCancellationResult() != InteractionResult.FAIL) LivingEffectHelper.unDisguise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getCancellationResult() != InteractionResult.FAIL) LivingEffectHelper.unDisguise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getCancellationResult() != InteractionResult.FAIL) LivingEffectHelper.unDisguise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.LeftClickBlock event) {
        if (!event.isCanceled()) LivingEffectHelper.unDisguise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        player.getData(FTZAttachmentTypes.ABILITY_MANAGER).respawn();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingChangeTarget(LivingChangeTargetEvent event) {
        if (event.getEntity().hasEffect(FTZMobEffects.STUN) && event.getNewAboutToBeSetTarget() != null) event.setCanceled(true);
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
        event.addListener(new EffectsOnSpawnManager());
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

        potionContents.customEffects().forEach(effect -> PlayerAbilityHelper.awardWisdom(player,"brewed", BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value())));
    }

    @SubscribeEvent
    public static void animalTamed(AnimalTameEvent event) {
        Player player = event.getTamer();
        Animal animal = event.getAnimal();
        PlayerAbilityHelper.awardWisdom(player,"tamed", BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType()));

    }

    @SubscribeEvent
    public static void playerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Level level = event.getEntity().level();

        ResourceKey<Level> to = event.getTo();
        if (!to.equals(Level.OVERWORLD)) PlayerAbilityHelper.awardWisdom(event.getEntity(), "visited_" + to.location(), 50);
    }

    @SubscribeEvent
    public static void itemCraftedEvent(PlayerEvent.ItemCraftedEvent event) {
        ItemStack itemStack = event.getCrafting();
        Item item = itemStack.getItem();

        if (event.getInventory() instanceof TransientCraftingContainer) PlayerAbilityHelper.awardWisdom(event.getEntity(),"crafted", BuiltInRegistries.ITEM.getKey(item));
    }

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addStartMix(FTZItems.OBSCURE_SUBSTANCE.value(), FTZPotions.SURGE);
        builder.addMix(FTZPotions.SURGE, Items.REDSTONE, FTZPotions.LONG_SURGE);
        builder.addMix(FTZPotions.SURGE, Items.GLOWSTONE_DUST, FTZPotions.STRONG_SURGE);

        builder.addStartMix(Items.COCOA_BEANS, FTZPotions.RECOVERY);
        builder.addMix(FTZPotions.RECOVERY, Items.REDSTONE, FTZPotions.LONG_RECOVERY);
        builder.addMix(FTZPotions.RECOVERY, Items.GLOWSTONE_DUST, FTZPotions.STRONG_RECOVERY);

        builder.addStartMix(Items.GOLD_INGOT, FTZPotions.FURY);
        builder.addMix(FTZPotions.FURY, Items.REDSTONE, FTZPotions.LONG_FURY);

        builder.addStartMix(Items.RAW_COPPER, FTZPotions.CORROSION);
        builder.addStartMix(Items.COPPER_INGOT, FTZPotions.CORROSION);
        builder.addMix(FTZPotions.CORROSION, Items.REDSTONE, FTZPotions.LONG_CORROSION);
        builder.addMix(FTZPotions.CORROSION, Items.GLOWSTONE_DUST, FTZPotions.STRONG_CORROSION);
    }
}
