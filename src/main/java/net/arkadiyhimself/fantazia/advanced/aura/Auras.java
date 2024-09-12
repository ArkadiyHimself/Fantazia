package net.arkadiyhimself.fantazia.advanced.aura;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.LivingData;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.StunEffect;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.checkerframework.checker.units.qual.A;

public class Auras {
    private Auras() {
    }
    public static final BasicAura<Entity> DEBUG = new BasicAura<>(10f, BasicAura.TYPE.MIXED, Entity.class)
            .addDynamicAttributeModifier(Attributes.MAX_HEALTH, new AttributeModifier("debug_aura_health", -18, AttributeModifier.Operation.ADDITION));
    public static final BasicAura<LivingEntity> LEADERSHIP = new BasicAura<>(12f, BasicAura.TYPE.POSITIVE, LivingEntity.class)
            .addPrimaryFilter((entity, owner) -> {
                boolean pet = entity instanceof TamableAnimal animal && owner instanceof LivingEntity livingOwner && animal.isOwnedBy(livingOwner);
                boolean ally = entity instanceof Player && owner instanceof Player;
                return pet || ally;
            })
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier("leadership_damage", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL))
            .addAttributeModifier(FTZAttributes.LIFESTEAL.get(), new AttributeModifier("leadership_lifesteal", 0.25f, AttributeModifier.Operation.ADDITION))
            .tickingOnEntities((entity, owner) -> {
                if (owner instanceof LivingEntity livingOwner && livingOwner.hasEffect(FTZMobEffects.FURY.get())) EffectHelper.makeFurious(entity,2);
            });
    public static final BasicAura<LivingEntity> TRANQUIL = new BasicAura<>(6f, BasicAura.TYPE.POSITIVE, LivingEntity.class)
            .addPrimaryFilter((entity, owner) -> {
                boolean flag = entity instanceof AgeableMob || entity instanceof Player;
                boolean flag1 = entity instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() == owner;
                return flag1 || flag;
            })
            .addSecondaryFilter((entity, owner) -> {
                if (owner instanceof Player player && player.getCooldowns().isOnCooldown(FTZItems.TRANQUIL_HERB.get())) return false;
                if (owner instanceof LivingEntity livingOwner) {
                    LivingData livingData = DataGetter.takeDataHolder(livingOwner, LivingData.class);
                    if (livingData != null && livingData.getDamageTicks() > 0) return false;
                }
                LivingData livingData = DataGetter.takeDataHolder(entity, LivingData.class);
                if (livingData != null && livingData.getDamageTicks() > 0) return false;

                return !InventoryHelper.hasCurio(entity, FTZItems.TRANQUIL_HERB.get()) && entity.getMaxHealth() > entity.getHealth();
            })
            .addOwnerConditions(owner -> {
                if (!(owner instanceof LivingEntity livingOwner)) return false;
                if (owner instanceof Player player && player.getCooldowns().isOnCooldown(FTZItems.TRANQUIL_HERB.get())) return false;
                LivingData livingData = DataGetter.takeDataHolder(livingOwner, LivingData.class);
                return livingData == null || livingData.getDamageTicks() <= 0;
            })
            .tickingOnEntities((entity, owner) -> {
                HealingSources healingSources = LevelCapHelper.getHealingSources(entity.level());
                if (healingSources != null) AdvancedHealing.tryHeal(entity, healingSources.regenAura(owner), 0.25f / 20);
            })
            .tickingOnOwner(owner -> {
                if (!(owner instanceof LivingEntity livingOwner)) return;
                HealingSources healingSources = LevelCapHelper.getHealingSources(livingOwner.level());
                if (healingSources != null) AdvancedHealing.tryHeal(livingOwner, healingSources.regenAura(owner), 0.3125f / 20);
            })
            .tickingOnBlocks((blockPos, auraInstance) -> {
                if (Fantazia.RANDOM.nextFloat() >= 0.00085f) return;
                Level level = auraInstance.getLevel();
                BlockState state = level.getBlockState(blockPos);
                if (state.getBlock() instanceof BonemealableBlock bonemealableBlock && !(state.getBlock() instanceof GrassBlock) && bonemealableBlock.isValidBonemealTarget(level, blockPos, state, level.isClientSide) && level instanceof ServerLevel serverLevel && bonemealableBlock.isBonemealSuccess(level, level.random, blockPos, state)) bonemealableBlock.performBonemeal(serverLevel, level.random, blockPos, state);
            });
    public static final BasicAura<LivingEntity> DESPAIR = new BasicAura<>(8f, BasicAura.TYPE.NEGATIVE, LivingEntity.class)
            .addPrimaryFilter((entity, owner) ->!(entity instanceof Mob mob) || mob.getTarget() == owner)
            .addSecondaryFilter((entity, owner) -> (owner instanceof LivingEntity livingOwner && livingOwner.getHealth() > entity.getHealth() || entity.hasEffect(FTZMobEffects.DOOMED.get())) && !entity.hasEffect(FTZMobEffects.FURY.get()))
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier("despair_damage", -0.35, AttributeModifier.Operation.MULTIPLY_TOTAL))
            .addDynamicAttributeModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier("despair_slow", -0.8, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final BasicAura<Monster> CORROSIVE = new BasicAura<>(7.5f, BasicAura.TYPE.NEGATIVE, Monster.class)
            .addMobEffect(FTZMobEffects.CORROSION.get(), 2)
            .addAttributeModifier(FTZAttributes.MAX_STUN_POINTS.get(), new AttributeModifier("corrosive_durability", -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
}
