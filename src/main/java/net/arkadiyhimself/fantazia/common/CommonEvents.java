package net.arkadiyhimself.fantazia.common;

import com.mojang.brigadier.CommandDispatcher;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.common.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.common.advanced.cleanse.Cleanse;
import net.arkadiyhimself.fantazia.common.advanced.cleanse.EffectCleansing;
import net.arkadiyhimself.fantazia.common.advanced.rune.RuneHelper;
import net.arkadiyhimself.fantazia.common.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.AttachmentsSyncing;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.holders.StuckHatchetHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityManager;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.*;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.common.api.curio.FTZSlots;
import net.arkadiyhimself.fantazia.common.api.custom_events.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.common.api.data_component.HiddenPotentialComponent;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompts;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.common.enchantment.effects.ConvertLootToExp;
import net.arkadiyhimself.fantazia.common.enchantment.effects.CriticalStrikeModify;
import net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component.FTZEnchantmentEffectComponentTypes;
import net.arkadiyhimself.fantazia.data.effect_from_damage.ServerEffectFromDamageManager;
import net.arkadiyhimself.fantazia.data.loot.ServerLootModifierManager;
import net.arkadiyhimself.fantazia.data.spawn_effect.ServerSpawnEffectManager;
import net.arkadiyhimself.fantazia.data.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.data.talent.TalentHelper;
import net.arkadiyhimself.fantazia.data.talent.TalentTreeData;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentHierarchyManager;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentManager;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentTabManager;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerWisdomRewardManager;
import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardCategories;
import net.arkadiyhimself.fantazia.common.entity.ThrownHatchet;
import net.arkadiyhimself.fantazia.common.entity.goals.PuppeteeredAttackableTargets;
import net.arkadiyhimself.fantazia.common.entity.goals.StandStillGoal;
import net.arkadiyhimself.fantazia.common.entity.magic_projectile.AbstractMagicProjectile;
import net.arkadiyhimself.fantazia.common.item.RuneWielderItem;
import net.arkadiyhimself.fantazia.common.item.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.common.item.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.networking.attachment_syncing.IAttachmentSync;
import net.arkadiyhimself.fantazia.common.registries.*;
import net.arkadiyhimself.fantazia.common.registries.custom.Auras;
import net.arkadiyhimself.fantazia.common.registries.custom.Runes;
import net.arkadiyhimself.fantazia.common.registries.custom.Spells;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.data.tags.FTZMobEffectTags;
import net.arkadiyhimself.fantazia.util.commands.*;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ApplyEffect;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import org.apache.commons.compress.utils.Lists;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;
import top.theillusivec4.curios.api.event.CurioCanUnequipEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.*;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Fantazia.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommonEvents {

    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        LivingEntity livingTarget = event.getEntity();
        DamageSource source = event.getSource();

        if (source.is(FTZDamageTypeTags.NON_LETHAL)) {
            event.setCanceled(true);
            livingTarget.setHealth(0.1f);
        }

        Holder<AbstractSpell> entangle = Spells.ENTANGLE;
        if (SpellHelper.spellAvailable(livingTarget, entangle) && livingTarget.getData(FTZAttachmentTypes.ENTANGLE_PREVIOUS_HEALTH) > livingTarget.getMaxHealth() * 0.1f && FantazicHooks.ForgeExtension.onDeathPrevention(event.getEntity(),entangle)) {
            EffectCleansing.tryCleanseAll(livingTarget, Cleanse.POWERFUL, MobEffectCategory.HARMFUL);
            event.setCanceled(true);
            livingTarget.setHealth(livingTarget.getMaxHealth() * 0.1f);
        }

        if (source.getEntity() instanceof LivingEntity attacker) {
            MobEffectInstance instance;

            if (attacker.hasEffect(FTZMobEffects.FURY)) {
                boolean damnedWrath = SpellHelper.spellAvailable(attacker, Spells.DAMNED_WRATH);
                EffectCleansing.changeDuration(attacker, FTZMobEffects.FURY, damnedWrath ? 100 : -100);
                EffectCleansing.changeDuration(attacker, FTZMobEffects.FURY, damnedWrath ? 100 : -100);

                SoundEvent soundEvent = damnedWrath ? FTZSoundEvents.FURY_PROLONG.get() : FTZSoundEvents.FURY_DISPEL.get();
                if (attacker instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, soundEvent);
            }

            if (attacker instanceof ServerPlayer player) {
                PlayerAbilityHelper.awardWisdom(player, WisdomRewardCategories.SLAYED, BuiltInRegistries.ENTITY_TYPE.getKey(livingTarget.getType()));

                int manaRecycle = player.getData(FTZAttachmentTypes.MANA_RECYCLE_LEVEL);
                if (manaRecycle > 0) player.addEffect(new MobEffectInstance(FTZMobEffects.SURGE, 40, manaRecycle - 1));
                PlayerAbilityHelper.acceptConsumer(player, EuphoriaHolder.class, EuphoriaHolder::increase);

                if (SpellHelper.spellAvailable(player, Spells.SUSTAIN))
                    PlayerAbilityHelper.reduceRecharge(player, Spells.SUSTAIN, 40);
            }

            if ((instance = livingTarget.getEffect(FTZMobEffects.CURSED_MARK)) != null) {
                int dur = 600 + instance.getAmplifier() * 600;
                ApplyEffect.makeDoomed(attacker, dur);
            }

            if (SpellHelper.spellAvailable(attacker, Spells.SUSTAIN) && attacker != livingTarget) {
                int ampl = SpellHelper.getSpellAmplifier(attacker, Spells.SUSTAIN);
                EffectCleansing.tryCleanseAll(attacker, Cleanse.BASIC, MobEffectCategory.HARMFUL);
                attacker.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, 1 + ampl));
                attacker.addEffect(new MobEffectInstance(FTZMobEffects.LAYERED_BARRIER, 200, 1 + ampl));
                if (attacker instanceof Player player) player.getFoodData().eat(1 + ampl,1 + ampl);
            }
        }
    }

    @SubscribeEvent
    public static void livingDrops(LivingDropsEvent event) {
        LivingEntity killed = event.getEntity();
        DamageSource damageSource = event.getSource();
        Vec3 pos = killed.position();
        if (event.getSource().getDirectEntity() instanceof LivingEntity killer && killed.level() instanceof ServerLevel serverLevel) {
            List<ItemEntity> toRemove = Lists.newArrayList();
            for (ItemEntity itemEntity : event.getDrops()) {
                if (ConvertLootToExp.convertLootToExp(serverLevel, killer.getMainHandItem(), itemEntity.getItem(), killed, damageSource))
                    toRemove.add(itemEntity);
            }
            event.getDrops().removeAll(toRemove);

            if (FantazicUtil.dropSkull(serverLevel, killer.getMainHandItem(), killed, damageSource)) {
                Item item = FantazicUtil.getSkull(killed);
                if (item != null) event.getDrops().add(new ItemEntity(killed.level(), pos.x, pos.y, pos.z, item.getDefaultInstance()));
            }
        }


        LivingDataHelper.acceptConsumer(killed, StuckHatchetHolder.class, StuckHatchetHolder::dropHatchet);
    }

    @SubscribeEvent
    public static void sweepAttack(SweepAttackEvent event) {
        Player player = event.getEntity();

        if (PlayerAbilityHelper.isDashing(player)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void livingPickupItem(VanillaEventsExtension.LivingPickUpItemEvent event) {
        if (event.getEntity().hasEffect(FTZMobEffects.STUN)) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void criticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        float multiplier = event.getDamageMultiplier();
        ItemStack stack = event.getEntity().getMainHandItem();
        Entity target = event.getTarget();

        if (PlayerAbilityHelper.isDashing(player)) {
            event.setCriticalHit(false);
            return;
        }

        if (player.level() instanceof ServerLevel serverLevel)
            multiplier = CriticalStrikeModify.modifyCriticalStrike(serverLevel, stack, target, player.damageSources().playerAttack(player), multiplier, event.isVanillaCritical());

        event.setDamageMultiplier(multiplier);
    }

    @SubscribeEvent
    public static void livingHeal(LivingHealEvent event) {
        boolean flag = LevelAttributesHelper.healEntity(event.getEntity(), event.getAmount(), HealingSourcesHolder::generic);
        if (flag) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void sleep(PlayerWakeUpEvent event) {
        if (event.wakeImmediately()) return;
        Player player = event.getEntity();
        PlayerAbilityHelper.acceptConsumer(player, StaminaHolder.class, StaminaHolder::restore);
        PlayerAbilityHelper.acceptConsumer(player, ManaHolder.class, ManaHolder::restore);
    }

    @SubscribeEvent
    public static void livingDamagePre(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();

        LivingEntity target = event.getEntity();
        target.setData(FTZAttachmentTypes.ENTANGLE_PREVIOUS_HEALTH, target.getHealth());
        if (event.getEntity().level().isClientSide()) return;
        if (source.is(FTZDamageTypes.BLEEDING)) VisualHelper.particleOnEntityServer(target, FTZParticleTypes.BLOOD.random(), ParticleMovement.FALL);

        FantazicCombat.meleeAttack(event);

        if (target instanceof Player player) player.getData(FTZAttachmentTypes.ABILITY_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.DATA_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.EFFECT_MANAGER).onHit(event);

        float amount = event.getNewDamage();
        if (!source.is(FTZDamageTypes.REMOVAL) && !source.is(FTZDamageTypes.BLEEDING) && SpellHelper.castPassiveSpell(target, Spells.REINFORCE).success()) {
            amount -= 0.75f + 0.1f * SpellHelper.getSpellAmplifier(target, Spells.REINFORCE);
        }
        float auraMultiplier = AuraHelper.getDamageMultiplier(target, source.typeHolder());
        amount *= auraMultiplier;
        event.setNewDamage(amount);

        if (event.getEntity() instanceof Player player && !source.is(FTZDamageTypes.REMOVAL)) player.getCooldowns().addCooldown(FTZItems.TRANQUIL_HERB.get(), 100);
        LivingEffectHelper.simpleEffectOnHit(event);
    }

    @SubscribeEvent
    public static void livingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        boolean blockable = source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK);
        boolean removal = source.is(FTZDamageTypes.REMOVAL);

        if (target instanceof ServerPlayer player) {
            player.getData(FTZAttachmentTypes.ABILITY_MANAGER).onHit(event);
            for (ItemStack itemStack : FantazicUtil.fullInventory(player)) if (itemStack.has(FTZDataComponentTypes.HIDDEN_POTENTIAL)) itemStack.update(FTZDataComponentTypes.HIDDEN_POTENTIAL, HiddenPotentialComponent.DEFAULT, holder -> holder.onHit(event));
            if (blockable && PlayerAbilityHelper.canDoMeleeBlock(player)) Prompts.USE_MELEE_BLOCK.maybePromptPlayer(player);
        }

        target.getData(FTZAttachmentTypes.DATA_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.EFFECT_MANAGER).onHit(event);

        float health = target.getHealth();
        if (target.level().isClientSide()) return;
        if (health < 0.3f * target.getMaxHealth() && !removal) SpellHelper.castPassiveSpell(target, Spells.DAMNED_WRATH);
        if (!source.is(FTZDamageTypes.REMOVAL)) target.getData(FTZAttachmentTypes.TRANQUILIZE_DAMAGE_TICKS).set(100);

        DamageSourcesHolder damageSourcesHolder = LevelAttributesHelper.getDamageSources(target.level());
        AuraInstance diffraction = AuraHelper.ownedAuraInstance(target, Auras.DIFFRACTION);
        if (diffraction != null && damageSourcesHolder != null) {
            if (!removal && attacker instanceof Monster monster) {
                List<? extends Entity> affected = diffraction.entitiesInside();
                affected.removeIf(entity -> entity != monster);
                for (Entity entity : affected) {
                    entity.hurt(damageSourcesHolder.removal(), event.getNewDamage() * (0.8f + 0.2f * diffraction.getAmplifier()));
                    VisualHelper.particleOnEntityServer(entity, ParticleTypes.DAMAGE_INDICATOR, ParticleMovement.ASCEND, 4);
                }
            }
        }

        LivingEffectHelper.simpleEffectOnHit(event);
        ServerEffectFromDamageManager.tryApplyEffects(target, source.typeHolder());
    }

    @SubscribeEvent
    public static void livingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.isCanceled()) return;
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        Supplier<Float> damage = event::getAmount;

        Entity attacker = source.getEntity();
        if (attacker != null && attacker.getData(FTZAttachmentTypes.DASHSTONE_MINION) && target.getData(FTZAttachmentTypes.DASHSTONE_MINION)) {
            event.setCanceled(true);
            return;
        }

        if (source.is(FTZDamageTypeTags.ELECTRIC)) {
            if (!LivingEffectHelper.hasBarrier(target)) ActionsHelper.interrupt(target);
            if (SpellHelper.spellAvailable(target, Spells.LIGHTNING_STRIKE)) event.setAmount(damage.get() * 0.6f);
        }

        if (source.is(DamageTypeTags.IS_FALL) && RuneHelper.hasRune(target, Runes.PURE_VESSEL)) event.setAmount(damage.get() - 1.5f);
        if (source.is(DamageTypes.WITHER) && SpellHelper.spellAvailable(target, Spells.SUSTAIN)) event.setCanceled(true);
        if (source.is(FTZDamageTypes.REMOVAL)) event.getContainer().setPostAttackInvulnerabilityTicks(target.invulnerableTime);

        if (AuraHelper.hasImmunityTo(target, source.typeHolder())) event.setCanceled(true);

        if (target instanceof Player player) player.getData(FTZAttachmentTypes.ABILITY_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.DATA_MANAGER).onHit(event);
        target.getData(FTZAttachmentTypes.EFFECT_MANAGER).onHit(event);

        if (FantazicCombat.attemptEvasion(event)) return;
        if (SpellHelper.wardenSonicBoom(event)) return;
        // TO-DO
        // figure out how to deal with guardian's double attack
        //if (SpellHelper.guardianAttack(event)) return;
        LivingEffectHelper.simpleEffectOnHit(event);
    }

    @SubscribeEvent
    public static void effectApplicable(MobEffectEvent.Applicable event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null) return;
        Holder<MobEffect> effect = instance.getEffect();
        LivingEntity entity = event.getEntity();
        if (effect == FTZMobEffects.STUN && event.getEntity() instanceof Player player && (player.isCreative() || player.isSpectator())) event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        if (!FTZMobEffects.Application.isApplicable(entity.getType(), effect)) event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        if (instance.getEffect() == MobEffects.WITHER && SpellHelper.spellAvailable(entity, Spells.SUSTAIN)) event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void entityJoinLevel(EntityJoinLevelEvent event) {
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
                if (livingEntity.getType() == EntityType.WARDEN) base = 2000;
                if (livingEntity.getType() == EntityType.IRON_GOLEM) base = 850;
                durability.setBaseValue(base);
            }
            if (entity instanceof Mob mob) {
                mob.targetSelector.addGoal(2, new PuppeteeredAttackableTargets<>(mob, Monster.class, true));
            }
        } else if (entity instanceof Arrow arrow) {
            if (FantazicUtil.hasEffect(arrow, FTZMobEffects.FURY)) arrow.setData(FTZAttachmentTypes.HAS_FURY, true);
        }

        if (!entity.level().isClientSide()) IAttachmentSync.onEntityJoinLevel(entity);

    }

    @SubscribeEvent
    public static void levelTick(LevelTickEvent.Pre event) {
        event.getLevel().getData(FTZAttachmentTypes.LEVEL_ATTRIBUTES).tick();
    }

    @SubscribeEvent
    public static void effectAdded(MobEffectEvent.Added event) {
        MobEffectInstance instance = event.getEffectInstance();
        Holder<MobEffect> effect = instance.getEffect();
        int dur = instance.getDuration();
        int ampl = instance.getDuration();
        LivingEntity livingEntity = event.getEntity();
        LivingEffectHelper.simpleEffectAdded(livingEntity, instance, event.getEffectSource());

        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).effectAdded(event.getEffectInstance());
        if (effect.is(FTZMobEffectTags.INTERRUPT)) ActionsHelper.interrupt(livingEntity);

        AuraInstance diffraction = AuraHelper.ownedAuraInstance(livingEntity, Auras.DIFFRACTION);
        if (diffraction != null && effect.value() != FTZMobEffects.STUN.get() && !effect.value().isBeneficial()) {
            int newDur = instance.isInfiniteDuration() ? 400 : Mth.clamp(dur, 50, 400);
            for (Entity entity : diffraction.entitiesInside()) if (entity instanceof Monster monster) monster.addEffect(new MobEffectInstance(effect, newDur, ampl));
        }
    }

    @SubscribeEvent
    public static void effectRemoved(MobEffectEvent.Remove event) {
        if (event.isCanceled()) return;
        LivingEntity livingEntity = event.getEntity();
        MobEffect mobEffect = event.getEffect().value();
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).effectEnded(mobEffect);
        LivingEffectHelper.simpleEffectRemoved(livingEntity, mobEffect);
    }

    @SubscribeEvent
    public static void effectExpired(MobEffectEvent.Expired event) {
        if (event.isCanceled()) return;
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance == null) return;
        LivingEntity livingEntity = event.getEntity();
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).effectEnded(effectInstance.getEffect().value());
        LivingEffectHelper.simpleEffectRemoved(livingEntity, effectInstance.getEffect().value());
    }

    @SubscribeEvent
    public static void changeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getNewGameMode() == GameType.CREATIVE || event.getNewGameMode() == GameType.SPECTATOR) if (event.getEntity().hasEffect(FTZMobEffects.STUN)) EffectCleansing.forceCleanse(event.getEntity(), FTZMobEffects.STUN);
    }

    @SubscribeEvent
    public static void entityTickPre(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();

        AuraHelper.handleAuraAffectingOnTick(entity);
        AttachmentsSyncing.tickAttachments(entity);

        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        if (entity instanceof Arrow arrow && FantazicUtil.hasEffect(arrow, FTZMobEffects.FROZEN)) {
            Vec3 pos = arrow.position();
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, 2, 0,0,0, 0);
        }

        if (!(entity instanceof LivingEntity livingEntity) || livingEntity.level().isClientSide() || livingEntity.isDeadOrDying()) return;
        RuneHelper.tickRunes(livingEntity);
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
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext context = event.getBuildContext();
        AuraCarrierCommand.register(dispatcher, context);
        SpellCastCommand.register(dispatcher);
        CooldownCommand.register(dispatcher, context);
        AdvancedHealCommand.register(dispatcher, context);
        ResetCommand.register(dispatcher);
        WisdomCommand.register(dispatcher);
        BuildTooltipCommand.register(dispatcher, context);
        CheckCommand.register(dispatcher);
        InterruptCommand.register(dispatcher);
    }

    @SubscribeEvent
    public static void curioChange(CurioChangeEvent event) {
        LivingEntity livingEntity = event.getEntity();
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        String ident = event.getIdentifier();
        int index = event.getSlotIndex();
        if (livingEntity instanceof ServerPlayer player) {
            PlayerAbilityManager playerAbilityManager = player.getData(FTZAttachmentTypes.ABILITY_MANAGER);
            playerAbilityManager.onCurioEquip(to);
            playerAbilityManager.onCurioUnEquip(from);

            CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), to);
            if (ident.equals(FTZSlots.ACTIVECASTER) && FantazicUtil.isActiveCaster(to.getItem())) {
                if (index == 0) Prompts.USE_SPELLCAST1.maybePromptPlayer(player);
                else if (index == 1) Prompts.USE_SPELLCAST2.maybePromptPlayer(player);
                else if (index == 2) Prompts.USE_SPELLCAST3.maybePromptPlayer(player);
            }
        }
    }

    @SubscribeEvent
    public static void curioUnEquip(CurioCanUnequipEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!player.isCreative() && event.getSlotContext().identifier().equals("dashstone")) event.setUnequipResult(TriState.FALSE);
            if (!player.hasInfiniteMaterials() && event.getStack().getItem() instanceof SpellCasterItem spellCasterItem && SpellHelper.onCooldown(player, spellCasterItem.getSpell())) event.setUnequipResult(TriState.FALSE);
            if (event.getUnequipResult() != null && event.getUnequipResult().isFalse() && player instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.DENIED.get());

        }
    }

    @SubscribeEvent
    public static void curioEquip(CurioCanEquipEvent event) {
        ItemStack stack = event.getStack();
        if (event.getEntity() instanceof Player player) {
            if (stack.getItem() instanceof AuraCasterItem item) {
               // if (FantazicUtil.duplicatingCurio(player, item) >= 1) event.setEquipResult(TriState.FALSE);
            }
        }
    }

    @SubscribeEvent
    public static void gameEvent(VanillaGameEvent event) {
        if (event.getVanillaEvent().is(GameEventTags.VIBRATIONS) && event.getContext().sourceEntity() != null && event.getContext().sourceEntity() instanceof LivingEntity entity && !entity.level().isClientSide() && !RuneHelper.hasRune(entity, Runes.NOISELESS)) {
            AABB aabb = entity.getBoundingBox().inflate(8);
            entity.level().getEntitiesOfClass(ServerPlayer.class, aabb).forEach(player -> PlayerAbilityHelper.tryListen((ServerLevel) entity.level(), event.getContext(), event.getEventPosition(), player));
        }
    }

    @SubscribeEvent
    public static void itemToss(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
        if (stack.has(FTZDataComponentTypes.HIDDEN_POTENTIAL)) stack.update(FTZDataComponentTypes.HIDDEN_POTENTIAL, HiddenPotentialComponent.DEFAULT, HiddenPotentialComponent::reset);
    }

    @SubscribeEvent
    public static void livingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player player) PlayerAbilityHelper.acceptConsumer(player, DoubleJumpHolder.class, DoubleJumpHolder::regularJump);
        if (livingEntity.hasEffect(FTZMobEffects.HAEMORRHAGE)) LevelAttributesHelper.hurtEntity(livingEntity, 1.25f, DamageSourcesHolder::bleeding);
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) IAttachmentSync.onEntityJoinLevel(serverPlayer);
    }

    @SubscribeEvent
    public static void mobAttack(VanillaEventsExtension.MobAttackEvent event) {
        if (event.getEntity().hasEffect(FTZMobEffects.DISARM) || event.getEntity().hasEffect(FTZMobEffects.STUN)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void attackEntity(AttackEntityEvent event) {
        Player attacker = event.getEntity();
        ItemStack stack = attacker.getMainHandItem();
        Entity target = event.getTarget();

        if (attacker instanceof ServerPlayer serverPlayer) {
            if (attacker.hasEffect(FTZMobEffects.DISARM)) {
                IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.DENIED.get());
                event.setCanceled(true);
                return;
            }
        }

        if (target instanceof AbstractMagicProjectile projectile && projectile.getOwner() == attacker) projectile.deflect(attacker);
    }

    @SubscribeEvent
    public static void livingEntityUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (!event.isCanceled()) ApplyEffect.unDisguise(event.getEntity());
    }

    @SubscribeEvent
    public static void livingEntityUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        ItemStack itemStack = event.getItem();
        Item item = itemStack.getItem();

        if (event.getEntity() instanceof ServerPlayer player) {
            FoodProperties foodProperties = itemStack.getFoodProperties(player);
            if (foodProperties != null) PlayerAbilityHelper.awardWisdom(player, WisdomRewardCategories.CONSUMED, BuiltInRegistries.ITEM.getKey(item));
        }
    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.RightClickItem event) {
        if (event.getCancellationResult() != InteractionResult.FAIL) ApplyEffect.unDisguise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getCancellationResult() != InteractionResult.FAIL) ApplyEffect.unDisguise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getCancellationResult() != InteractionResult.FAIL) ApplyEffect.unDisguise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.LeftClickBlock event) {
        if (!event.isCanceled()) ApplyEffect.unDisguise(event.getEntity());
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
        else if (SpellHelper.ghastFireBall(event)) return;
        else if (SpellHelper.shulkerBullet(event)) return;

        HitResult result = event.getRayTraceResult();
        Projectile projectile = event.getProjectile();
        if (result instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            if (projectile instanceof Snowball) entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze(), entity.getTicksFrozen() + 50));
            if (projectile instanceof AbstractArrow arrow) {
                if (entity instanceof LivingEntity livingEntity) FantazicCombat.arrowImpact(arrow, livingEntity);
                if (entity instanceof AbstractMagicProjectile magicProjectile) magicProjectile.deflect(entity);
            }
        }
    }

    @SubscribeEvent
    public static void onAdvancedHeal(VanillaEventsExtension.AdvancedHealEvent event) {
        LivingEntity livingEntity = event.getEntity();
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).onHeal(event);
    }

    @SubscribeEvent
    public static void reloadListener(AddReloadListenerEvent event) {
        event.addListener(new ServerTalentManager());
        event.addListener(new ServerTalentHierarchyManager());
        event.addListener(new ServerWisdomRewardManager());
        event.addListener(new ServerTalentTabManager());
        event.addListener(new ServerLootModifierManager());
        event.addListener(new ServerSpawnEffectManager());
        event.addListener(new ServerEffectFromDamageManager());
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

        if (player instanceof ServerPlayer serverPlayer) potionContents.customEffects().forEach(effect -> PlayerAbilityHelper.awardWisdom(serverPlayer, WisdomRewardCategories.BREWED, BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value())));
    }

    @SubscribeEvent
    public static void animalTamed(AnimalTameEvent event) {
        Player player = event.getTamer();
        Animal animal = event.getAnimal();
        if (player instanceof ServerPlayer serverPlayer) PlayerAbilityHelper.awardWisdom(serverPlayer, WisdomRewardCategories.TAMED, BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType()));
    }

    @SubscribeEvent
    public static void playerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        Level level = event.getEntity().level();

        ResourceKey<Level> from = event.getFrom();
        ResourceKey<Level> to = event.getTo();
        if (!to.equals(Level.OVERWORLD) && player instanceof ServerPlayer serverPlayer) PlayerAbilityHelper.awardWisdom(serverPlayer, WisdomRewardCategories.VISITED, to.location());
        player.getData(FTZAttachmentTypes.ABILITY_MANAGER).onChangeDimension(from, to);

        if (player instanceof ServerPlayer serverPlayer) {
            IAttachmentSync.onEntityJoinLevel(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void itemCraftedEvent(PlayerEvent.ItemCraftedEvent event) {
        ItemStack itemStack = event.getCrafting();
        Item item = itemStack.getItem();
        Player player = event.getEntity();

        if (event.getInventory() instanceof TransientCraftingContainer && player instanceof ServerPlayer serverPlayer) PlayerAbilityHelper.awardWisdom(serverPlayer, WisdomRewardCategories.CRAFTED, BuiltInRegistries.ITEM.getKey(item));
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

        builder.addStartMix(Items.ENDER_PEARL, FTZPotions.CHAINED);
        builder.addMix(FTZPotions.CHAINED, Items.REDSTONE, FTZPotions.LONG_CHAINED);
    }

    @SubscribeEvent
    public static void wandererTrades(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();

        genericTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemCost(Items.EMERALD,4 + pRandom.nextInt(0, 3)),
                new ItemStack(FTZBlocks.OBSCURE_SAPLING.asItem()),
                4,5,0.2f
        ));

        rareTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 16),
                new ItemStack(FTZItems.LEADERS_HORN.asItem()),
                1,15,1f
        ));

        rareTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 8 + pRandom.nextInt(0, 6)),
                RuneWielderItem.rune(Runes.AEROBAT),
                1, 12, 1f
        ));
    }

    @SubscribeEvent
    public static void villagerTrades(VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        VillagerProfession profession = event.getType();
        if (profession == VillagerProfession.CLERIC) {
            trades.get(5).add((trader, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 20 + randomSource.nextInt(0,5)),
                    new ItemStack(FTZItems.BROKEN_STAFF.asItem()),
                    1, 10, 0.2f
            ));
        } else if (profession == VillagerProfession.WEAPONSMITH) {
            trades.get(3).add((trader, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 16 + randomSource.nextInt(0, 8)),
                    RuneWielderItem.rune(Runes.METICULOUS),
                    4, 20, 0.2f
            ));
        }
    }

    @SubscribeEvent
    public static void finalizeSpawn(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();
        FantazicCombat.grantEffectsOnSpawn(mob);
    }

    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) IAttachmentSync.onEntityTracked(event.getTarget(), serverPlayer);
    }

    @SubscribeEvent
    public static void effectParticleModification(EffectParticleModificationEvent event) {
        if (event.getEffect().getEffect().is(FTZMobEffectTags.NO_PARTICLES)) event.setVisible(false);
    }

    @SubscribeEvent
    public static void rightClickItems(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (player.hasEffect(FTZMobEffects.DISARM) && stack.getItem() instanceof ShieldItem) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    @SubscribeEvent
    public static void enderEntityTeleport(EntityTeleportEvent.EnderEntity event) {
        LivingEntity entity = event.getEntityLiving();
        if (LivingEffectHelper.isStunned(entity) || entity.hasEffect(FTZMobEffects.CHAINED)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void chorusFruitTeleport(EntityTeleportEvent.ChorusFruit event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.hasEffect(FTZMobEffects.CHAINED)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void enderPearlTeleport(EntityTeleportEvent.EnderPearl event) {
        ServerPlayer player = event.getPlayer();
        if (player.hasEffect(FTZMobEffects.CHAINED)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void updateItemTick(LivingEntityUseItemEvent.Tick event) {
        ItemStack itemStack = event.getItem();
        LivingEntity entity = event.getEntity();
        int duration = event.getDuration();
        int tickRate = entity.tickCount;

        if (itemStack.is(FTZItemTags.SLOWED_BY_FROZEN)) {
            MobEffectInstance frozen = entity.getEffect(FTZMobEffects.FROZEN);
            if (frozen != null) {
                int ampl = frozen.getAmplifier();
                int i = Math.max(1, 5 - ampl);
                if ((tickRate % i) == 0) event.setDuration(duration + 1);
            }
        }
    }

    @SubscribeEvent
    public static void breakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        float speed = event.getNewSpeed();
        float multiplier = 1f;

        MobEffectInstance frozen = player.getEffect(FTZMobEffects.FROZEN);
        if (frozen != null) {
            multiplier = (4f - frozen.getAmplifier()) / 5f;
            multiplier = Math.max(0.1f, multiplier);
        }

        event.setNewSpeed(speed * multiplier);
    }
}
