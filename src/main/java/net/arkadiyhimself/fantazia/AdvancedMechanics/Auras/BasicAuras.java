package net.arkadiyhimself.fantazia.AdvancedMechanics.Auras;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.AdvancedHealing;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.HealingSource;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.HealingTypes;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.api.AttributeRegistry;
import net.arkadiyhimself.fantazia.api.ItemRegistry;
import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.AttachCommonData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.CommonData;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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
import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;

public class BasicAuras {
    public static BasicAura<LivingEntity, LivingEntity> LEADERSHIP = new BasicAura<LivingEntity, LivingEntity>(12f, Fantazia.res("leadership"), BasicAura.TYPE.POSITIVE, LivingEntity.class)
            .addPrimaryFilter((entity, owner) -> {
                boolean pet = entity instanceof TamableAnimal animal && animal.isOwnedBy(owner);
                boolean ally = entity instanceof Player && owner instanceof Player;
                return pet || ally;
            })
            .addModifiers(new HashMap<>(){{
                put(Attributes.ATTACK_DAMAGE, new AttributeModifier("leadership_damage", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL));
                put(AttributeRegistry.LIFESTEAL.get(), new AttributeModifier("leadership_lifesteal", 0.25f, AttributeModifier.Operation.ADDITION));
            }})
            .tickingOnEntities((entity, owner) -> {
                if (owner.hasEffect(MobEffectRegistry.FURY.get())) {
                    entity.addEffect(new MobEffectInstance(MobEffectRegistry.FURY.get(), 2, 0));
                }
            })
            .addIconTooltip((entity, owner) -> {
                List<Component> components = Lists.newArrayList();
                WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.leadership.1", null, null);
                WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.leadership.2", null, null);
                WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.leadership.3", null, null);
                return components;
            });
    public static BasicAura<LivingEntity, LivingEntity> TRANQUIL = new BasicAura<LivingEntity, LivingEntity>(6f, Fantazia.res("tranquil"), BasicAura.TYPE.POSITIVE, LivingEntity.class)
            .addPrimaryFilter((entity, owner) -> {
                boolean flag = entity instanceof AgeableMob || entity instanceof Player;
                boolean flag1 = entity instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() == owner;
                return flag1 || flag;
            })
            .addSecondaryFilter((entity, owner) -> {
                if (owner instanceof Player player && player.getCooldowns().isOnCooldown(ItemRegistry.TRANQUIL_HERB.get())) return false;
                CommonData own = AttachCommonData.getUnwrap(owner);
                if (own != null && own.damageTicks > 0) return false;
                CommonData data = AttachCommonData.getUnwrap(entity);
                return (data == null || data.damageTicks <= 0) && !WhereMagicHappens.Abilities.hasCurio(entity, ItemRegistry.TRANQUIL_HERB.get()) && entity.getMaxHealth() > entity.getHealth();
            })
            .addOwnerConditions((owner) -> {
                if (owner instanceof Player player && player.getCooldowns().isOnCooldown(ItemRegistry.TRANQUIL_HERB.get())) return false;
                CommonData data = AttachCommonData.getUnwrap(owner);
                return data == null || data.damageTicks <= 0;
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
                if (WhereMagicHappens.random.nextFloat() >= 0.00085f) return;
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
            })
            .addIconTooltip((entity, aura) -> {
                List<Component> components = Lists.newArrayList();
                if (aura.getAura().secondaryFilter.test(entity, aura.getOwner()) || !Screen.hasShiftDown()) {
                    WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.tranquil.1", null, null);
                    WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.tranquil.2", null, null);
                    WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.tranquil.3", null, null);
                } else {
                    WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.tranquil.alt.1", null, null);
                    WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.tranquil.alt.2", null, null);
                }
                return components;
            });
    public static BasicAura<LivingEntity, LivingEntity> DESPAIR = new BasicAura<LivingEntity, LivingEntity>(8f, Fantazia.res("despair"), BasicAura.TYPE.NEGATIVE, LivingEntity.class)
            .addPrimaryFilter((entity, owner) ->!(entity instanceof Mob mob) || mob.getTarget() == owner)
            .addSecondaryFilter((entity, owner) -> (owner.getHealth() > entity.getHealth() || entity.hasEffect(MobEffectRegistry.DOOMED.get())) && !entity.hasEffect(MobEffectRegistry.FURY.get()))
            .addModifiers(new HashMap<>(){{
                put(Attributes.ATTACK_DAMAGE, new AttributeModifier("despair_damage", -0.35, AttributeModifier.Operation.MULTIPLY_TOTAL));
                put(Attributes.ARMOR, new AttributeModifier("despair_armor", -5, AttributeModifier.Operation.ADDITION));
            }})
            .addIconTooltip((entity, aura) -> {
                List<Component> components = Lists.newArrayList();
                if (aura.getAura().secondaryFilter.test(entity, aura.getOwner()) || !Screen.hasShiftDown()) {
                    WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.despair.1", null, null);
                    WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.despair.2", null, null);
                    WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.despair.3", null, null);
                } else {
                    WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.despair.alt.1", null, null);
                    WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.despair.alt.2", null, null);
                }
                return components;
            });
}
