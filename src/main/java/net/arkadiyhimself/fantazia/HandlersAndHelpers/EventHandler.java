package net.arkadiyhimself.fantazia.HandlersAndHelpers;

import dev._100media.capabilitysyncer.network.SimpleLevelCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities.Spells;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.AdvancedHealing;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.HealingSource;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.HealingTypes;
import net.arkadiyhimself.fantazia.AdvancedMechanics.CleanseManager.EffectCleansing;
import net.arkadiyhimself.fantazia.Entities.Goals.StandStillGoal;
import net.arkadiyhimself.fantazia.Entities.HatchetEntity;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.CustomEvents.NewEvents;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.CustomEvents.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.Items.MagicCasters.DashStone;
import net.arkadiyhimself.fantazia.Items.Weapons.Melee.FragileBlade;
import net.arkadiyhimself.fantazia.MobEffects.SimpleMobEffect;
import net.arkadiyhimself.fantazia.Networking.NetworkHandler;
import net.arkadiyhimself.fantazia.Networking.packets.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.Networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.api.*;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.AttackBlock;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.Dash;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.DoubleJump;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.RenderingValues;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AuraCarrier.AuraCarrier;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AuraCarrier.GetAuraCarrier;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.AttachCommonData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.CommonData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.EffectGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.EffectManager;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects.StunEffect;
import net.arkadiyhimself.fantazia.util.Capability.ItemStack.Common.AttachCommonItem;
import net.arkadiyhimself.fantazia.util.Capability.ItemStack.Common.CommonItem;
import net.arkadiyhimself.fantazia.util.Capability.ItemStack.FragileSword.AttachFragileBlade;
import net.arkadiyhimself.fantazia.util.Capability.ItemStack.FragileSword.FragileBladeCap;
import net.arkadiyhimself.fantazia.util.Capability.Level.LevelCap;
import net.arkadiyhimself.fantazia.util.Capability.Level.LevelCapGetter;
import net.arkadiyhimself.fantazia.util.commands.AuraCarrierCommand;
import net.arkadiyhimself.fantazia.util.commands.CooldownCommand;
import net.arkadiyhimself.fantazia.util.commands.FullHealCommand;
import net.arkadiyhimself.fantazia.util.commands.SpellCastCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = Fantazia.MODID)
public class EventHandler {
    public static final Map<Player, Float> previousHealth = new WeakHashMap<>();
    public static final List<Item> noDisintegration = new ArrayList<>(){{
        add(Items.NETHER_STAR);
    }};
    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        DamageSource source = event.getSource();
        if (event.getEntity() instanceof Player player) {
            if (WhereMagicHappens.Abilities.hasSpell(player, Spells.ENTANGLE) && previousHealth.containsKey(player) && previousHealth.get(player) > player.getMaxHealth() * 0.1f) {
                boolean work = NewEvents.ForgeExtenstion.onDeathPreventation(event.getEntity(), Spells.ENTANGLE);
                if (work) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth() * 0.1f);
                }
            }
        }
        if (source.getEntity() instanceof LivingEntity livingEntity) {
            MobEffectInstance instance;
            if ((instance = livingEntity.getEffect(MobEffectRegistry.FURY.get())) != null) {
                boolean amulet = WhereMagicHappens.Abilities.hasSpell(livingEntity, Spells.DAMNED_WRATH);
                if (amulet) WhereMagicHappens.Abilities.addEffectWithoutParticles(livingEntity, instance.getEffect(), instance.getDuration() + 100, instance.getAmplifier());
                else EffectCleansing.forceCleanse(livingEntity, MobEffectRegistry.FURY.get());

                SoundEvent soundEvent = amulet ? SoundRegistry.FURY_DISPEL.get() : SoundRegistry.FURY_PROLONG.get();
                if (livingEntity instanceof ServerPlayer serverPlayer) {
                    NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(soundEvent), serverPlayer);
                }
            }
        }
    }
    @SubscribeEvent
    public static void remove(EntityLeaveLevelEvent event) {
        AttachCommonData.get(event.getEntity()).ifPresent(commonData -> {
            commonData.getAurasFromItems().forEach((passiveCaster, auraInstance) -> auraInstance.discard());
            commonData.getAurasFromItems().clear();
        });
        if (event.getEntity() instanceof ArmorStand armorStand) GetAuraCarrier.get(armorStand).ifPresent(AuraCarrier::onDeath);
    }
    @SubscribeEvent
    public static void livingDrops(LivingDropsEvent event) {
        if (event.getSource() == null) return;
        LivingEntity killed = event.getEntity();
        if (event.getSource().getDirectEntity() instanceof LivingEntity killer) {
            int level = killer.getMainHandItem().getEnchantmentLevel(EnchantmentRegistry.DISINTEGRATION.get());
            if (level > 0) {
                for (ItemEntity entity : event.getDrops()) {
                    float multiplier = switch (entity.getItem().getItem().getRarity(entity.getItem())) {
                        case COMMON -> 1f;
                        case UNCOMMON -> 1.25f;
                        case RARE -> 1.75f;
                        case EPIC -> 2.5f;
                    };
                    boolean flag1 = (noDisintegration.contains(entity.getItem().getItem()));
                    CommonItem cap = AttachCommonItem.getUnwrap(entity.getItem());
                    boolean flag2 = (cap != null && cap.wasPickedUp);
                    if (!flag1 && !flag2) {
                        WhereMagicHappens.Abilities.dropExperience(killed, level * 5 * multiplier);
                        event.getDrops().remove(entity);
                    }
                }
            }
        }
        if (WhereMagicHappens.Abilities.hatchetStuck.containsKey(killed) && !event.isCanceled()) {
            HatchetEntity hatchetEntity = new HatchetEntity(killed.level(), killed.getPosition(0f), WhereMagicHappens.Abilities.hatchetStuck.get(killed));
            AttachCommonItem.get(hatchetEntity.getPickupItem()).ifPresent(commonItem -> commonItem.wasPickedUp = false);
            killed.level().addFreshEntity(hatchetEntity);
            WhereMagicHappens.Abilities.hatchetStuck.remove(killed);
        }
    }
    @SubscribeEvent
    public static void pickupItem(VanillaEventsExtension.LivingPickUpItemEvent event) {
        if (!event.isCanceled()) {
            AttachCommonItem.get(event.getItemEntity().getItem()).ifPresent(commonItem -> commonItem.wasPickedUp = true);
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
    public static void criticalHit(CriticalHitEvent event) {
        float modifier = event.getDamageModifier();
        int i = event.getEntity().getMainHandItem().getEnchantmentLevel(EnchantmentRegistry.DECISIVE_STRIKE.get());
        if (i > 0 && event.isVanillaCritical()) {
            modifier += i * 0.25;
            event.setDamageModifier(modifier);
        }
    }
    @SubscribeEvent
    public static void livingHeal(LivingHealEvent event) {
        boolean flag = AdvancedHealing.heal(event.getEntity(), new HealingSource(HealingTypes.VANILLA), event.getAmount());
        if (flag) event.setCanceled(true);
        if (WhereMagicHappens.Abilities.hasSpell(event.getEntity(), Spells.ENTANGLE) || event.getEntity().hasEffect(MobEffectRegistry.FROZEN.get())) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void livingDamage(LivingDamageEvent event) {
        LivingEntity living = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        EffectGetter.get(living).ifPresent(effectManager -> effectManager.onHit(event));
        float pre = living.getHealth();
        float post = living.getHealth() - event.getAmount();
        if (event.getSource() == null) return;
        if (living.level().isClientSide()) { return; }
        if (living.hasEffect(MobEffectRegistry.FURY.get())) {
            float multiplier = WhereMagicHappens.Abilities.hasSpell(living, Spells.DAMNED_WRATH) ? 1.5f : 2f;
            event.setAmount(event.getAmount() * multiplier);
        }
        if (attacker instanceof LivingEntity livAtt && livAtt.hasEffect(MobEffectRegistry.FURY.get())) {
            event.setAmount(event.getAmount() * 2);
            if (WhereMagicHappens.Abilities.hasSpell(living, Spells.DAMNED_WRATH)) {
                float heal = 0.15f * event.getAmount();
                AdvancedHealing.heal(livAtt, new HealingSource(HealingTypes.LIFESTEAL), heal);
            }
        }
        if (post < 0.3f * living.getMaxHealth()) {
            if (WhereMagicHappens.Abilities.hasActiveSpell(living, Spells.DAMNED_WRATH)) {
                WhereMagicHappens.Abilities.addEffectWithoutParticles(living, MobEffectRegistry.FURY.get(), 200);
                WhereMagicHappens.Abilities.addEffectWithoutParticles(living, MobEffectRegistry.ABSOLUTE_BARRIER.get(), 20);
                if (living instanceof ServerPlayer serverPlayer)
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.BLOODLUST_AMULET.get()), serverPlayer);
            }
        }
        if (event.getSource().is(DamageTypes.SONIC_BOOM) || event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            living.addEffect(new MobEffectInstance(MobEffectRegistry.DEAFENING.get(), 200, 0, true, false, true));
            if (living instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.RINGING.get()), serverPlayer);
            }
        }
        if (living instanceof ServerPlayer player) {
            previousHealth.put(player, player.getHealth());
        }
        if (living.hasEffect(MobEffectRegistry.DOOMED.get()) && event.getAmount() > 0 && !event.isCanceled()) {
            event.setAmount(Float.MAX_VALUE);
            living.playSound(SoundRegistry.FALLEN_BREATH.get());
            double x = living.getX();
            double y = living.getY();
            double z = living.getZ();
            double height = living.getBbHeight();
            Minecraft.getInstance().level.addParticle(ParticleRegistry.FALLEN_SOUL.get(), x, y + height * 2 / 3, z, 0.0D, -0.135D, 0.0D);

            BlockPos blockPos = living.getOnPos();
            Block block = living.level().getBlockState(blockPos).getBlock();
            if (block == Blocks.DIRT || block == Blocks.SAND || block == Blocks.NETHERRACK || block == Blocks.GRASS_BLOCK) {
                living.level().setBlockAndUpdate(blockPos, Blocks.SOUL_SAND.defaultBlockState());
            }
        }
    }
    @SubscribeEvent
    public static void livingAttack(LivingAttackEvent event) {
        if (event.getEntity().hasEffect(MobEffectRegistry.ABSOLUTE_BARRIER.get())) {
            event.setCanceled(true);
            return;
        }
        if (event.getSource() == null) return;
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity target = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        EffectGetter.get(target).ifPresent(effectManager -> effectManager.onHit(event));
        AttachCommonData.get(target).ifPresent(commonData -> commonData.onHit(event));
        if (attacker instanceof Warden warden && event.getSource().is(DamageTypes.SONIC_BOOM)) {
            if (target instanceof ServerPlayer player) {
                if (WhereMagicHappens.Abilities.hasActiveSpell(player, Spells.REFLECT)) {
                    AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
                    if (abilityManager != null) abilityManager.getAbility(RenderingValues.class).ifPresent(RenderingValues::onMirrorActivation);
                    event.setCanceled(true);
                    WhereMagicHappens.Abilities.rayOfParticles(player, warden, ParticleTypes.SONIC_BOOM);
                    player.level().playSound(null, player.blockPosition(), SoundRegistry.MYSTIC_MIRROR.get(), SoundSource.NEUTRAL);
                    warden.hurt(event.getEntity().level().damageSources().sonicBoom(player), 15f);
                }
            }
            if (target.hasEffect(MobEffectRegistry.REFLECT.get())) {
                event.setCanceled(true);
                target.removeEffect(MobEffectRegistry.REFLECT.get());
                WhereMagicHappens.Abilities.rayOfParticles(target, warden, ParticleTypes.SONIC_BOOM);
                warden.hurt(event.getEntity().level().damageSources().sonicBoom(target), 15f);
            }
            if (target.hasEffect(MobEffectRegistry.DEFLECT.get())) {
                event.setCanceled(true);
                target.removeEffect(MobEffectRegistry.DEFLECT.get());
            }
        }
        if (target instanceof Player player) {
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager != null) {
                abilityManager.getAbility(Dash.class).ifPresent(dash -> dash.onHit(event));
                abilityManager.getAbility(AttackBlock.class).ifPresent(attackBlock -> attackBlock.onHit(event));
            }
        }
    }
    @SubscribeEvent
    public static void livingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        LivingEntity target = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        AttachCommonData.get(target).ifPresent(commonData -> commonData.onHit(event));
        EffectGetter.get(target).ifPresent(effectManager -> effectManager.onHit(event));


        boolean meleeAttack = event.getSource().is(DamageTypes.PLAYER_ATTACK) || event.getSource().is(DamageTypes.MOB_ATTACK) || event.getSource().is(DamageTypeRegistry.PARRY);
        if (meleeAttack && attacker instanceof LivingEntity livingAtt) {
            AttributeInstance lifeSteal = livingAtt.getAttribute(AttributeRegistry.LIFESTEAL.get());
            double heal = lifeSteal == null ? 0 : lifeSteal.getValue() * event.getAmount();
            if (heal > 0) {
                AdvancedHealing.heal(livingAtt, new HealingSource(HealingTypes.LIFESTEAL, target), (float) heal);
            }
        }

        if (attacker instanceof ServerPlayer player) {
            if (meleeAttack) {
                ItemStack itemStack = player.getMainHandItem();
                FragileBladeCap bladeCap = AttachFragileBlade.getUnwrap(itemStack);
                if (bladeCap != null) {
                    FragileBladeCap.DAMAGE_LEVEL old = bladeCap.getDamageLevel();
                    bladeCap.onAttack(event.getSource().is(DamageTypeRegistry.PARRY));
                    FragileBladeCap.DAMAGE_LEVEL current = bladeCap.getDamageLevel();
                    float dmg = event.getAmount() + bladeCap.damage;
                    event.setAmount(dmg);
                    event.getEntity().playSound(bladeCap.getHitSound());
                    if (old != FragileBladeCap.DAMAGE_LEVEL.MAXIMUM && current == FragileBladeCap.DAMAGE_LEVEL.MAXIMUM) {
                        event.getEntity().playSound(SoundRegistry.FRAG_SWORD_UNLEASH.get());
                    }
                }
            }
        }
        if (event.getEntity().getMainHandItem().getItem() instanceof FragileBlade) {
            AttachFragileBlade.get(event.getEntity().getMainHandItem()).ifPresent(FragileBladeCap::reset);
        }
        if (event.getEntity() instanceof Player player) {
            for (ItemStack stack : WhereMagicHappens.Gui.searchForItems(player, FragileBlade.class)) {
                AttachFragileBlade.get(stack).ifPresent(FragileBladeCap::reset);
            }
            player.getCooldowns().addCooldown(ItemRegistry.TRANQUIL_HERB.get(), 100);
        }
    }
    @SubscribeEvent
    public static void effectApplicable(MobEffectEvent.Applicable event) {
        MobEffect effect = event.getEffectInstance().getEffect();
        LivingEntity entity = event.getEntity();
        if (effect == MobEffectRegistry.STUN.get() && event.getEntity() instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) event.setResult(Event.Result.DENY);
        }
        if (SimpleMobEffect.immuneToBleeding.contains(entity.getType()) && effect == MobEffectRegistry.HAEMORRHAGE.get()) event.setResult(Event.Result.DENY);
        if (!SimpleMobEffect.affectedByDeafening.contains(entity.getType()) && effect == MobEffectRegistry.DEAFENING.get()) event.setResult(Event.Result.DENY);
    }
    @SubscribeEvent
    public static void effectAdded(MobEffectEvent.Added event) {
        MobEffect effect = event.getEffectInstance().getEffect();
        LivingEntity livingEntity = event.getEntity();
        EffectGetter.get(livingEntity).ifPresent(effectManager -> effectManager.effectAdded(event.getEffectInstance()));
        AttachCommonData.get(livingEntity).ifPresent(commonData -> commonData.onEffectRecieve(event.getEffectInstance()));


        if (effect == MobEffectRegistry.HAEMORRHAGE.get()) {
            livingEntity.level().playSound(null, livingEntity.blockPosition(), SoundRegistry.FLESH_RIPPING.get(), SoundSource.NEUTRAL,0.35f,1f);
            livingEntity.hurt(new DamageSource(livingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.BLEEDING)), livingEntity.getHealth() * 0.1f);
            int ampl = event.getEffectInstance().getAmplifier();
            AttachCommonData.get(livingEntity).ifPresent(commonData -> commonData.setBleedingHeal(4 + ampl * 2));
        }
        if (livingEntity instanceof ServerPlayer player) {
            if (effect == MobEffectRegistry.DOOMED.get()) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DOOMED.get()), player);
            }

        }
        if (effect == MobEffectRegistry.MICROSTUN.get()) {
            WhereMagicHappens.Abilities.interrupt(livingEntity);
        }
        if (effect == MobEffectRegistry.FURY.get()) {
            AttachCommonData.get(livingEntity).ifPresent(commonData -> commonData.setFurious(true));
        }
    }
    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof PathfinderMob mob) {
            mob.goalSelector.addGoal(1, new StandStillGoal(mob));
        }
        if (entity instanceof LivingEntity livingEntity) {
            AttributeInstance lifesteal = livingEntity.getAttribute(AttributeRegistry.LIFESTEAL.get());
            if (lifesteal != null && livingEntity.isInvertedHealAndHarm()) {
                lifesteal.addPermanentModifier(new AttributeModifier("built_in_lifesteal", 0.2f, AttributeModifier.Operation.ADDITION));
            }
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

        AttachCommonData.get(livingEntity).ifPresent(commonData -> commonData.onEffectEnd(event.getEffectInstance()));

        if (livingEntity instanceof ServerPlayer player) {
            if (effect == MobEffectRegistry.DOOMED.get()) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.UNDOOMED.get()), player);
            }
            if (effect == MobEffectRegistry.STUN.get()) {
                NetworkHandler.sendToPlayer(new PlayAnimationS2C(""), player);
            }
        }
        if (effect == MobEffectRegistry.FURY.get()) {
            AttachCommonData.get(livingEntity).ifPresent(commonData -> commonData.setFurious(false));
        }
    }
    @SubscribeEvent
    public static void effectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance == null) return;
        MobEffect effect = effectInstance.getEffect();
        LivingEntity livingEntity = event.getEntity();
        EffectGetter.get(livingEntity).ifPresent(effectManager -> effectManager.effectEnded(effectInstance));

        AttachCommonData.get(livingEntity).ifPresent(commonData -> commonData.onEffectEnd(event.getEffectInstance()));
        if (livingEntity instanceof ServerPlayer player) {
            if (effect == MobEffectRegistry.DOOMED.get()) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.UNDOOMED.get()), player);
            }
        }
        if (effect == MobEffectRegistry.FURY.get()) {
            AttachCommonData.get(livingEntity).ifPresent(commonData -> commonData.setFurious(false));
        }
    }
    @SubscribeEvent
    public static void changeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if(event.getNewGameMode() == GameType.CREATIVE || event.getNewGameMode() == GameType.SPECTATOR) {
            if (event.getEntity().hasEffect(MobEffectRegistry.STUN.get())) event.getEntity().removeEffect(MobEffectRegistry.STUN.get());
            EffectManager effectManager = EffectGetter.getUnwrap(event.getEntity());
            if (effectManager == null) return;
            effectManager.getEffect(StunEffect.class).ifPresent(StunEffect::ended);
        }
    }
    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.isDeadOrDying() && !livingEntity.level().isClientSide()) {
            AttachCommonData.get(livingEntity).ifPresent(CommonData::tick);
            EffectGetter.get(livingEntity).ifPresent(EffectManager::tick);
        }
        event.getEntity().getActiveEffects().forEach(mobEffect -> AttachCommonData.get(livingEntity).ifPresent(commonData -> commonData.onEffectTick(mobEffect)));
    }
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        // abilities
        if (event.player instanceof ServerPlayer serverPlayer) {
            if (event.phase == TickEvent.Phase.START) {
                AbilityGetter.get(serverPlayer).ifPresent(AbilityManager::tick);




                List<ItemStack> fragSwords = WhereMagicHappens.Gui.searchForItems(serverPlayer, FragileBlade.class);
                for (ItemStack stack : fragSwords) {
                    AttachFragileBlade.get(stack).ifPresent(FragileBladeCap::tick);
                }
            }
            if (WhereMagicHappens.Abilities.hasSpell(serverPlayer, Spells.DEVOUR)) {
                WhereMagicHappens.Abilities.addEffectWithoutParticles(serverPlayer, MobEffects.HUNGER, 2);
                event.player.causeFoodExhaustion(serverPlayer.getFoodData().getSaturationLevel() > 0 ? 0.1f : 0.01f);
            }

        }
    }
    @SubscribeEvent
    public static void shieldBlock(ShieldBlockEvent event) {
        Entity blocker = event.getEntity();
        Entity attacker = event.getDamageSource().getEntity();
        if (attacker instanceof HatchetEntity hatchetEntity && hatchetEntity.phasingTicks > 0) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void curioEquip(CurioEquipEvent event) {
        if ("dashstone".equals(event.getSlotContext().identifier()) && event.getEntity() instanceof Player player) {
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (event.getStack().getItem() instanceof DashStone dashStone) {
                abilityManager.getAbility(Dash.class).ifPresent(dash -> dash.setLevel(dashStone.level));
                abilityManager.updateTracking();
            }
        }
    }
    @SubscribeEvent
    public static void commandRegister(final RegisterCommandsEvent event) {
        AuraCarrierCommand.register(event.getDispatcher());
        SpellCastCommand.register(event.getDispatcher());
        CooldownCommand.register(event.getDispatcher(), event.getBuildContext());
        FullHealCommand.register(event.getDispatcher());
    }
    @SubscribeEvent
    public static void curioChange(CurioChangeEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!event.isCanceled()) {
            AttachCommonData.get(livingEntity).ifPresent(commonData -> commonData.onCurioUnequip(event.getFrom()));
            AttachCommonData.get(livingEntity).ifPresent(commonData -> commonData.onCurioEquip(event.getTo()));
        }
    }
    @SubscribeEvent
    public static void curioUnEquip(CurioUnequipEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (Objects.equals(event.getSlotContext().identifier(), "dashstone")) {
                if (player.isSpectator() || player.isCreative()) {
                    abilityManager.getAbility(Dash.class).ifPresent(dash -> dash.setLevel(0));
                    abilityManager.updateTracking();
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
        if (event.getVanillaEvent().is(GameEventTags.VIBRATIONS) && event.getContext().sourceEntity() != null && event.getContext().sourceEntity() instanceof LivingEntity entity && !entity.level().isClientSide()) {
            AABB aabb = entity.getBoundingBox().inflate(8);
            entity.level().getEntitiesOfClass(ServerPlayer.class, aabb).forEach(player ->
                    WhereMagicHappens.Abilities.listenVibration((ServerLevel) entity.level(), event.getContext(), event.getEventPosition(), player));
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
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            abilityManager.getAbility(DoubleJump.class).ifPresent(DoubleJump::regularJump);
        }
    }
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!event.getEntity().hasEffect(MobEffectRegistry.STUN.get())) {
            EffectManager effectManager = EffectGetter.getUnwrap(player);
            if (effectManager != null) effectManager.getEffect(StunEffect.class).ifPresent(StunEffect::ended);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            LevelCap levelCap = LevelCapGetter.getLevelCap(event.getEntity().level());
            if (levelCap != null) {
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SimpleLevelCapabilityStatusPacket(LevelCapGetter.LEVEL_CAP_RL, levelCap));
            }
        }
    }
    @SubscribeEvent
    public static void mobAttack(VanillaEventsExtension.MobAttackEvent event) {
        if (event.getEntity().hasEffect(MobEffectRegistry.DISARM.get()) || event.getEntity().hasEffect(MobEffectRegistry.STUN.get())) {
            event.setCanceled(true);
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DENIED.get()), serverPlayer);
            }
        }
    }
    @SubscribeEvent
    public static void attackEntity(AttackEntityEvent event) {
        Player attacker = event.getEntity();
        ItemStack stack = attacker.getMainHandItem();
        if (event.getTarget() instanceof LivingEntity target) {
            int i = event.getEntity().getMainHandItem().getEnchantmentLevel(EnchantmentRegistry.ICE_ASPECT.get());
            if (i > 0) {
                WhereMagicHappens.Abilities.addEffectWithoutParticles(target, MobEffectRegistry.FROZEN.get(), 40 + i * 20);
            }
            if (attacker instanceof ServerPlayer serverPlayer) {
                if (attacker.hasEffect(MobEffectRegistry.DISARM.get())) {
                    NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DENIED.get()), serverPlayer);
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

        AttachCommonData.get(player).ifPresent(CommonData::onRespawn);
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
        if (!work) event.setCanceled(true);
    }
    @SubscribeEvent
    public static void onDeathPreventation(VanillaEventsExtension.DeathPreventationEvent event) {
        if (event.getEntity().hasEffect(MobEffectRegistry.DOOMED.get())) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onProjectile(ProjectileImpactEvent event) {
        HitResult result = event.getRayTraceResult();
        Projectile projectile = event.getProjectile();
        if (result instanceof EntityHitResult entityHitResult) {
            if (projectile instanceof Snowball) {
                Entity entity = entityHitResult.getEntity();
                int i = entity.getTicksFrozen();
                entity.setTicksFrozen(Math.min(entity.getTicksRequiredToFreeze(), i + 50));
            }
        }
    }
}
