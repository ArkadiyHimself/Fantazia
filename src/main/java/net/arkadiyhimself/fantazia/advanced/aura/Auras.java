package net.arkadiyhimself.fantazia.advanced.aura;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.CommonDataHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;


public class Auras {
    private Auras() {}

    public static final BasicAura<LivingEntity> DEBUG = new BasicAura.Builder<>(LivingEntity.class, BasicAura.TYPE.NEGATIVE, 10f)
            .addDynamicAttributeModifier(Attributes.MAX_HEALTH, new AttributeModifier(Fantazia.res("aura.debug"), -18, AttributeModifier.Operation.ADD_VALUE))
            .build();

    public static final BasicAura<LivingEntity> LEADERSHIP = new BasicAura.Builder<>(LivingEntity.class, BasicAura.TYPE.POSITIVE, 12f)
            .primaryFilter((entity, owner) -> {
                boolean pet = entity instanceof TamableAnimal animal && owner instanceof LivingEntity livingOwner && animal.isOwnedBy(livingOwner);
                boolean ally = entity instanceof Player && owner instanceof Player;
                return pet || ally;
            })
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(Fantazia.res("aura.leadership"), 0.5f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
            .addAttributeModifier(FTZAttributes.LIFESTEAL, new AttributeModifier(Fantazia.res("aura.leadership"), 0.25f, AttributeModifier.Operation.ADD_VALUE))
            .onTickAffected((entity, owner) -> {
                if (owner instanceof LivingEntity livingOwner && livingOwner.hasEffect(FTZMobEffects.FURY)) LivingEffectHelper.makeFurious(entity,2);
            })
            .build();

    public static final BasicAura<LivingEntity> TRANQUIL = new BasicAura.Builder<>(LivingEntity.class, BasicAura.TYPE.POSITIVE, 6f)
            .primaryFilter((entity, owner) -> {
                boolean flag = entity instanceof AgeableMob || entity instanceof Player;
                boolean flag1 = entity instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() == owner;
                return flag1 || flag;
            })
            .secondaryFilter((entity, owner) -> {
                if (owner instanceof Player player && player.getCooldowns().isOnCooldown(FTZItems.TRANQUIL_HERB.get())) return false;
                if (owner instanceof LivingEntity livingOwner) {
                    CommonDataHolder commonDataHolder = LivingDataGetter.takeHolder(livingOwner, CommonDataHolder.class);
                    if (commonDataHolder != null && commonDataHolder.getDamageTicks() > 0) return false;
                }
                CommonDataHolder commonDataHolder = LivingDataGetter.takeHolder(entity, CommonDataHolder.class);
                if (commonDataHolder != null && commonDataHolder.getDamageTicks() > 0) return false;

                return !InventoryHelper.hasCurio(entity, FTZItems.TRANQUIL_HERB.get()) && entity.getMaxHealth() > entity.getHealth();
            })
            .ownerConditions(owner -> {
                if (!(owner instanceof LivingEntity livingOwner)) return false;
                if (owner instanceof Player player && player.getCooldowns().isOnCooldown(FTZItems.TRANQUIL_HERB.get())) return false;
                CommonDataHolder commonDataHolder = LivingDataGetter.takeHolder(livingOwner, CommonDataHolder.class);
                return commonDataHolder == null || commonDataHolder.getDamageTicks() <= 0;
            })
            .onTickAffected((entity, owner) -> {
                HealingSourcesHolder healingSources = LevelAttributesHelper.getHealingSources(entity.level());
                if (healingSources != null) AdvancedHealing.tryHeal(entity, healingSources.regenAura(owner), 0.25f / 20);
            })
            .onTickOwner(owner -> {
                if (!(owner instanceof LivingEntity livingOwner)) return;
                HealingSourcesHolder healingSources = LevelAttributesHelper.getHealingSources(livingOwner.level());
                if (healingSources != null) AdvancedHealing.tryHeal(livingOwner, healingSources.regenAura(owner), 0.3125f / 20);
            })
            .onTickBlock((blockPos, auraInstance) -> {
                if (Fantazia.RANDOM.nextFloat() >= 0.00085f) return;
                Level level = auraInstance.getLevel();
                BlockState state = level.getBlockState(blockPos);
                if (state.getBlock() instanceof BonemealableBlock bonemealableBlock && !(state.getBlock() instanceof GrassBlock) && bonemealableBlock.isValidBonemealTarget(level, blockPos, state) && level instanceof ServerLevel serverLevel && bonemealableBlock.isBonemealSuccess(level, level.random, blockPos, state)) bonemealableBlock.performBonemeal(serverLevel, level.random, blockPos, state);
            })
            .build();

    public static final BasicAura<Monster> DESPAIR = new BasicAura.Builder<>(Monster.class, BasicAura.TYPE.NEGATIVE, 8f)
            .secondaryFilter((entity, owner) -> (owner instanceof LivingEntity livingOwner && livingOwner.getHealth() > entity.getHealth() || entity.hasEffect(FTZMobEffects.DOOMED)) && !entity.hasEffect(FTZMobEffects.FURY))
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(Fantazia.res("aura.despair"), -0.35, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
            .addDynamicAttributeModifier(Attributes.MOVEMENT_SPEED, new AttributeModifier(Fantazia.res("aura.despair"), -0.8, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
            .build();

    public static final BasicAura<Monster> CORROSIVE = new BasicAura.Builder<>(Monster.class, BasicAura.TYPE.NEGATIVE, 7.5f)
            .addMobEffect(FTZMobEffects.CORROSION, 2)
            .addAttributeModifier(FTZAttributes.MAX_STUN_POINTS, new AttributeModifier(Fantazia.res("aura.corrosive"), -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
            .build();

}
