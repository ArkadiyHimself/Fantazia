package net.arkadiyhimself.fantazia.advanced.aura;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSource;
import net.arkadiyhimself.fantazia.advanced.healing.HealingTypes;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataManager;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.CommonData;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public class BasicAuras {
    public static final BasicAura<Entity, Entity> DEBUG = new BasicAura<>(10f, Fantazia.res("debug"), BasicAura.TYPE.MIXED, Entity.class);
    public static final BasicAura<LivingEntity, LivingEntity> LEADERSHIP = new BasicAura<LivingEntity, LivingEntity>(12f, Fantazia.res("leadership"), BasicAura.TYPE.POSITIVE, LivingEntity.class)
            .addPrimaryFilter((entity, owner) -> {
                boolean pet = entity instanceof TamableAnimal animal && animal.isOwnedBy(owner);
                boolean ally = entity instanceof Player && owner instanceof Player;
                return pet || ally;
            })
            .addModifiers(new HashMap<>(){{
                put(Attributes.ATTACK_DAMAGE, new AttributeModifier("leadership_damage", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL));
                put(FTZAttributes.LIFESTEAL, new AttributeModifier("leadership_lifesteal", 0.25f, AttributeModifier.Operation.ADDITION));
            }})
            .tickingOnEntities((entity, owner) -> {
                if (owner.hasEffect(FTZMobEffects.FURY)) entity.addEffect(new MobEffectInstance(FTZMobEffects.FURY, 2, 0));
            });
    public static final BasicAura<LivingEntity, LivingEntity> TRANQUIL = new BasicAura<LivingEntity, LivingEntity>(6f, Fantazia.res("tranquil"), BasicAura.TYPE.POSITIVE, LivingEntity.class)
            .addPrimaryFilter((entity, owner) -> {
                boolean flag = entity instanceof AgeableMob || entity instanceof Player;
                boolean flag1 = entity instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() == owner;
                return flag1 || flag;
            })
            .addSecondaryFilter((entity, owner) -> {
                if (owner instanceof Player player && player.getCooldowns().isOnCooldown(FTZItems.TRANQUIL_HERB)) return false;
                DataManager dataOwner = DataGetter.getUnwrap(owner);
                if (dataOwner != null) {
                    CommonData commonData = dataOwner.takeData(CommonData.class);
                    if (commonData != null && commonData.getDamageTicks() > 0) return false;
                }
                DataManager dataEntity = DataGetter.getUnwrap(entity);
                if (dataEntity != null) {
                    CommonData commonData = dataEntity.takeData(CommonData.class);
                    if (commonData != null && commonData.getDamageTicks() > 0) return false;
                }
                return !InventoryHelper.hasCurio(entity, FTZItems.TRANQUIL_HERB) && entity.getMaxHealth() > entity.getHealth();
            })
            .addOwnerConditions((owner) -> {
                if (owner instanceof Player player && player.getCooldowns().isOnCooldown(FTZItems.TRANQUIL_HERB)) return false;
                DataManager dataManager = DataGetter.getUnwrap(owner);
                if (dataManager != null) {
                    CommonData commonData = dataManager.takeData(CommonData.class);
                    return commonData == null || commonData.getDamageTicks() <= 0;
                }
                return true;
            })
            .tickingOnEntities((entity, owner) -> {
                HealingSource source = new HealingSource(HealingTypes.REGEN_EFFECT, owner);
                AdvancedHealing.heal(entity, source, 0.25f / 20);
            })
            .tickingOnOwner(owner -> {
                HealingSource source = new HealingSource(HealingTypes.REGEN_EFFECT, owner);
                AdvancedHealing.heal(owner, source, 0.3125f / 20);
            })
            .tickingOnBlocks((blockPos, auraInstance) -> {
                if (Fantazia.RANDOM.nextFloat() >= 0.00085f) return;
                Level level = auraInstance.getLevel();
                BlockState state = level.getBlockState(blockPos);
                if (state.getBlock() instanceof BonemealableBlock bonemealableBlock && !(state.getBlock() instanceof GrassBlock)) {
                    if (bonemealableBlock.isValidBonemealTarget(level, blockPos, state, level.isClientSide)) {
                        if (level instanceof ServerLevel serverLevel) {
                            if (bonemealableBlock.isBonemealSuccess(level, level.random, blockPos, state)) {
                                bonemealableBlock.performBonemeal(serverLevel, level.random, blockPos, state);
                            }
                        }
                    }
                }
            });
    public static final BasicAura<LivingEntity, LivingEntity> DESPAIR = new BasicAura<LivingEntity, LivingEntity>(8f, Fantazia.res("despair"), BasicAura.TYPE.NEGATIVE, LivingEntity.class)
            .addPrimaryFilter((entity, owner) ->!(entity instanceof Mob mob) || mob.getTarget() == owner)
            .addSecondaryFilter((entity, owner) -> (owner.getHealth() > entity.getHealth() || entity.hasEffect(FTZMobEffects.DOOMED)) && !entity.hasEffect(FTZMobEffects.FURY))
            .addModifiers(new HashMap<>(){{
                put(Attributes.ATTACK_DAMAGE, new AttributeModifier("despair_damage", -0.35, AttributeModifier.Operation.MULTIPLY_TOTAL));
                put(Attributes.ARMOR, new AttributeModifier("despair_armor", -5, AttributeModifier.Operation.ADDITION));
            }});
}
