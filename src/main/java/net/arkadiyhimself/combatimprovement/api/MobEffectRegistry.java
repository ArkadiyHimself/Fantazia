package net.arkadiyhimself.combatimprovement.api;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.MobEffects.effectsdostuff.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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
    public static final RegistryObject<MobEffect> MIGHT;
    public static final RegistryObject<MobEffect> DOOMED;
    public static final RegistryObject<MobEffect> DISARM;
    public static final RegistryObject<MobEffect> REFLECT;
    public static final RegistryObject<MobEffect> DEFLECT;

    static {
        FURY = EFFECTS.register("fury", () -> new Fury(MobEffectCategory.NEUTRAL, 16057348)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, "5b9aec64-4a33-11ee-be56-0242ac120002", 0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL));
        HAEMORRHAGE = EFFECTS.register("haemorrhage", () -> new Haemorrhage(MobEffectCategory.HARMFUL, 6553857) {});
        STUN = EFFECTS.register("stun", () -> new Stun(MobEffectCategory.HARMFUL, 10179691)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, "103503fe-4a33-11ee-be56-0242ac120002", -10, AttributeModifier.Operation.ADDITION));
        BARRIER = EFFECTS.register("barrier", () -> new MobEffectRegistry(MobEffectCategory.BENEFICIAL, 8780799)
                .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "21fe121a-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION));
        LAYERED_BARRIER = EFFECTS.register("layered_barrier", () -> new MobEffectRegistry(MobEffectCategory.BENEFICIAL, 126)
                .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "2c3db8d4-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION));
        ABSOLUTE_BARRIER = EFFECTS.register("absolute_barrier", () -> new MobEffectRegistry(MobEffectCategory.BENEFICIAL, 7995643)
                .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "444492cc-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION));
        DEAFENING = EFFECTS.register("deafening", () -> new MobEffectRegistry(MobEffectCategory.HARMFUL, 4693243));
        FROZEN = EFFECTS.register("frozen", () -> new Frozen(MobEffectCategory.HARMFUL, 8780799)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, "4b3d404c-4a33-11ee-be56-0242ac120002", -0.25f, AttributeModifier.Operation.ADDITION)
                .addAttributeModifier(Attributes.ATTACK_SPEED, "500fbb5e-4a33-11ee-be56-0242ac120002", -0.6f, AttributeModifier.Operation.ADDITION));
        MIGHT = EFFECTS.register("might", () -> new MobEffectRegistry(MobEffectCategory.BENEFICIAL, 16767061)
                .addAttributeModifier(Attributes.ATTACK_DAMAGE, "5502680a-4a33-11ee-be56-0242ac120002", 1, AttributeModifier.Operation.ADDITION));
        DOOMED = EFFECTS.register("doomed", () -> new Doomed(MobEffectCategory.HARMFUL, 0));
        DISARM = EFFECTS.register("disarm", () -> new MobEffectRegistry(MobEffectCategory.HARMFUL, 16447222));
        REFLECT = EFFECTS.register("reflect", () -> new MobEffectRegistry(MobEffectCategory.BENEFICIAL, 8780799));
        DEFLECT = EFFECTS.register("deflect", () -> new MobEffectRegistry(MobEffectCategory.BENEFICIAL, 8780799));
    }

    protected MobEffectRegistry(MobEffectCategory pCategory, int pColor) { super(pCategory, pColor); }
    public static void register(IEventBus eventBus) { EFFECTS.register(eventBus); }
}
