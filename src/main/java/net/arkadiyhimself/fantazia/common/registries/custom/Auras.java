package net.arkadiyhimself.fantazia.common.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.common.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments.TickingIntegerHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.common.api.custom_registry.DeferredAura;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.common.registries.*;
import net.arkadiyhimself.fantazia.data.predicate.DamageTypePredicate;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ApplyEffect;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;

public class Auras {

    private static final FantazicRegistries.Auras REGISTER = FantazicRegistries.createAuras(Fantazia.MODID);

    public static final DeferredAura<Aura> DEBUG;
    public static final DeferredAura<Aura> LEADERSHIP;
    public static final DeferredAura<Aura> TRANQUIL;
    public static final DeferredAura<Aura> DESPAIR;
    public static final DeferredAura<Aura> CORROSIVE;
    public static final DeferredAura<Aura> HELLFIRE;
    public static final DeferredAura<Aura> FROSTBITE;
    public static final DeferredAura<Aura> DIFFRACTION;
    public static final DeferredAura<Aura> UNCOVER;

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    private static DeferredAura<Aura> register(String id, Aura.Builder builder) {
        return REGISTER.register(id, builder);
    }

    static {
        DEBUG = register("debug", Aura.builder(Aura.TYPE.NEGATIVE, 10f)
                .addProximityAttributeModifier(Attributes.MAX_HEALTH, Fantazia.location("aura.debug"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, ampl -> -0.9)
        );

        LEADERSHIP = register("leadership", Aura.builder(Aura.TYPE.POSITIVE, 12f)
                .primaryFilter((entity, owner) -> {
                    boolean pet = entity instanceof TamableAnimal animal && owner instanceof LivingEntity livingOwner && animal.isOwnedBy(livingOwner);
                    boolean ally = entity instanceof Player && owner instanceof Player;
                    return pet || ally;
                })
                .addAttributeModifier(Attributes.ATTACK_DAMAGE, Fantazia.location("aura.leadership"),AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, ampl -> 0.4 + 0.15 * ampl)
                .addAttributeModifier(FTZAttributes.LIFESTEAL, Fantazia.location("aura.leadership"), AttributeModifier.Operation.ADD_VALUE, ampl -> 0.25 + 0.1 * ampl)
                .onTickAffected((entity, auraInstance) -> {
                    Entity owner = auraInstance.getOwner();
                    if (owner instanceof LivingEntity livingOwner && livingOwner.hasEffect(FTZMobEffects.FURY) && entity instanceof LivingEntity livingEntity) ApplyEffect.makeFurious(livingEntity,2);
                }));

        TRANQUIL = register("tranquil", Aura.builder( Aura.TYPE.POSITIVE, 6f)
                .primaryFilter((entity, owner) -> {
                    boolean flag = entity instanceof AgeableMob || entity instanceof Player;
                    boolean flag1 = entity instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() == owner;
                    return flag1 || flag;
                })
                .secondaryFilter((entity, owner) -> {
                    if (owner instanceof LivingEntity livingOwner && livingOwner.getData(FTZAttachmentTypes.TRANQUILIZE_DAMAGE_TICKS).value() > 0) return false;
                    if (entity.getData(FTZAttachmentTypes.TRANQUILIZE_DAMAGE_TICKS).value() > 0) return false;

                    return entity instanceof LivingEntity livingEntity && !AuraHelper.ownsAura(livingEntity, Auras.TRANQUIL) && livingEntity.getMaxHealth() > livingEntity.getHealth();
                })
                .ownerConditions(owner -> {
                    if (!(owner instanceof LivingEntity livingOwner)) return false;
                    if (owner instanceof Player player && player.getCooldowns().isOnCooldown(FTZItems.TRANQUIL_HERB.get())) return false;
                    return livingOwner.getData(FTZAttachmentTypes.TRANQUILIZE_DAMAGE_TICKS).value() <= 0;
                })
                .onTickAffected((entity, auraInstance) -> {
                    float ampl = auraInstance.getAmplifier();
                    float heal = 0.25f + ampl / 8;
                    if (entity instanceof LivingEntity livingEntity) LevelAttributesHelper.healEntityByOther(livingEntity, auraInstance.getOwner(), heal / 20, HealingSourcesHolder::regenAura);
                })
                .onTickOwner(auraInstance -> {
                    float ampl = auraInstance.getAmplifier();
                    float heal = (0.25f + ampl / 8) * 1.25f;
                    if (auraInstance.getOwner() instanceof LivingEntity livingOwner) LevelAttributesHelper.healEntityByItself(livingOwner, heal / 20, HealingSourcesHolder::regenAura);
                })
                .onTickBlock((blockPos, auraInstance) -> {
                    if (RandomUtil.nextFloat() >= 0.0085f) return;
                    Level level = auraInstance.getLevel();
                    BlockState state = level.getBlockState(blockPos);
                    if (state.getBlock() instanceof BonemealableBlock bonemealableBlock && !(state.getBlock() instanceof GrassBlock) && bonemealableBlock.isValidBonemealTarget(level, blockPos, state) && level instanceof ServerLevel serverLevel && bonemealableBlock.isBonemealSuccess(level, level.random, blockPos, state)) bonemealableBlock.performBonemeal(serverLevel, level.random, blockPos, state);
                }));

        DESPAIR = register("despair", Aura.builder(Aura.TYPE.NEGATIVE, 8f)
                .secondaryFilter((entity, owner) -> {
                    if (!(entity instanceof LivingEntity livingEntity)) return false;
                    if (owner instanceof LivingEntity livingOwner && livingOwner.getHealth() > livingEntity.getHealth()) return true;
                    return livingEntity.hasEffect(FTZMobEffects.DOOMED) && !livingEntity.hasEffect(FTZMobEffects.FURY);
                })
                .addAttributeModifier(Attributes.ATTACK_DAMAGE, Fantazia.location("aura.despair"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, ampl -> -(0.3 + 0.1 * ampl))
                .addProximityAttributeModifier(Attributes.MOVEMENT_SPEED, Fantazia.location("aura.despair"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, ampl -> -(0.4 + 0.2 * ampl))
        );

        CORROSIVE = register("corrosive", Aura.builder(Aura.TYPE.NEGATIVE, 7.5f)
                .addMobEffect(FTZMobEffects.CORROSION, 2)
                .addProximityAttributeModifier(Attributes.ARMOR, Fantazia.location("aura.corrosive"), -3, AttributeModifier.Operation.ADD_VALUE)
                .addAttributeModifier(FTZAttributes.MAX_STUN_POINTS, Fantazia.location("aura.corrosive"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, ampl -> (-(0.2 + ampl * 0.2)))
        );

        HELLFIRE = register("hellfire", Aura.builder(Aura.TYPE.NEGATIVE, 6f)
                .onTickAffected(livingEntity -> {
                    if (livingEntity.getRemainingFireTicks() == 1) livingEntity.setRemainingFireTicks(21);

                    TickingIntegerHolder ancientFlame = livingEntity.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS);
                    if (ancientFlame.value() == 1) ancientFlame.set(21);
                })
                .putDamageMultiplier(DamageTypePredicate.builder().addTagPredicates(FTZDamageTypeTags.ANCIENT_FLAME).build(), 1.5f)
                .putDamageMultiplier(DamageTypePredicate.builder().addTagPredicates(DamageTypeTags.IS_FIRE).build(), 1.75f));

        FROSTBITE = register("frostbite", Aura.builder(Aura.TYPE.NEGATIVE, 8f)
                .primaryFilter(entity -> !(entity instanceof AgeableMob) && entity.canFreeze() && !(entity instanceof ArmorStand))
                .onTickAffected((mob, auraInstance) -> {
                    int ampl = auraInstance.getAmplifier();
                    mob.setTicksFrozen(Math.min(mob.getTicksRequiredToFreeze() + 2, mob.getTicksFrozen() + 3 + ampl));
                })
                .putDamageMultiplier(DamageTypePredicate.builder().addTagPredicates(DamageTypeTags.IS_FREEZING).build(), 3f)
                .addDynamicAttributeModifiersStatic(Attributes.ARMOR_TOUGHNESS, Fantazia.location("aura.frostbite"), -0.8, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, LivingEntity::getPercentFrozen)
                .putTooltipFormating(ChatFormatting.BLUE));

        DIFFRACTION = register("diffraction", Aura.builder(Aura.TYPE.NEGATIVE, 6f)
                .primaryFilter(entity -> entity instanceof Monster));

        UNCOVER = register("uncover", Aura.builder(Aura.TYPE.NEGATIVE, 24f)
                .primaryFilter(entity -> entity instanceof LivingEntity));
    }
}
