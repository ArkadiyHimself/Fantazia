package net.arkadiyhimself.combatimprovement.Registries.MobEffects;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.effectsdostuff.Frozen;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.effectsdostuff.Fury;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.effectsdostuff.Haemorrhage;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.effectsdostuff.Stun;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

public class MobEffectRegistry extends MobEffect {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CombatImprovement.MODID);

    // new mob effects
    public static final RegistryObject<MobEffect> HAEMORRHAGE;
    public static final RegistryObject<MobEffect> FURY;
    public static final RegistryObject<MobEffect> STUN;
    public static final RegistryObject<MobEffect> BARRIER;
    public static final RegistryObject<MobEffect> LAYERED_BARRIER;
    public static final RegistryObject<MobEffect> ABSOLUTE_BARRIER;
    public static final RegistryObject<MobEffect> DEAFENING;
    public static final RegistryObject<MobEffect> FROZEN;
    public static final RegistryObject<MobEffect> MIGTH;

    static {
        FURY = EFFECTS.register("fury", () -> new Fury(MobEffectCategory.NEUTRAL, 16057348)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, UUID.randomUUID().toString(), 0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL));
        HAEMORRHAGE = EFFECTS.register("haemorrhage", () -> new Haemorrhage(MobEffectCategory.HARMFUL, 6553857) {});
        STUN = EFFECTS.register("stun", () -> new Stun(MobEffectCategory.HARMFUL, 10179691)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, UUID.randomUUID().toString(), -10, AttributeModifier.Operation.ADDITION));
        BARRIER = EFFECTS.register("barrier", () -> new MobEffectRegistry(MobEffectCategory.BENEFICIAL, 8780799)
                .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, UUID.randomUUID().toString(), 0.5, AttributeModifier.Operation.ADDITION));
        LAYERED_BARRIER = EFFECTS.register("layered_barrier", () -> new MobEffectRegistry(MobEffectCategory.BENEFICIAL, 126)
                .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, UUID.randomUUID().toString(), 0.5, AttributeModifier.Operation.ADDITION));
        ABSOLUTE_BARRIER = EFFECTS.register("absolute_barrier", () -> new MobEffectRegistry(MobEffectCategory.BENEFICIAL, 7995643)
                .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, UUID.randomUUID().toString(), 0.5, AttributeModifier.Operation.ADDITION));
        DEAFENING = EFFECTS.register("deafening", () -> new MobEffectRegistry(MobEffectCategory.HARMFUL, 4693243));
        FROZEN = EFFECTS.register("frozen", () -> new Frozen(MobEffectCategory.HARMFUL, 8780799)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, UUID.randomUUID().toString(), -0.25f, AttributeModifier.Operation.ADDITION)
                .addAttributeModifier(Attributes.ATTACK_SPEED, UUID.randomUUID().toString(), -0.6f, AttributeModifier.Operation.ADDITION));
        MIGTH = EFFECTS.register("might", () -> new MobEffectRegistry(MobEffectCategory.BENEFICIAL, 16767061)
                .addAttributeModifier(Attributes.ATTACK_DAMAGE, UUID.randomUUID().toString(), 1, AttributeModifier.Operation.ADDITION));
    }

    protected MobEffectRegistry(MobEffectCategory pCategory, int pColor) { super(pCategory, pColor); }
    public static void register(IEventBus eventBus) { EFFECTS.register(eventBus); }
}
