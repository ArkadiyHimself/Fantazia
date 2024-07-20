package net.arkadiyhimself.fantazia.api;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.MobEffects.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MobEffectRegistry {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Fantazia.MODID);

    // new mob effects
    public static final RegistryObject<MobEffect> HAEMORRHAGE; // finished
    public static final RegistryObject<MobEffect> FURY; // finished
    public static final RegistryObject<MobEffect> STUN; // finished
    public static final RegistryObject<MobEffect> BARRIER; // finished
    public static final RegistryObject<MobEffect> LAYERED_BARRIER; // finished
    public static final RegistryObject<MobEffect> ABSOLUTE_BARRIER; // finished
    public static final RegistryObject<MobEffect> DEAFENING; // finished
    public static final RegistryObject<MobEffect> FROZEN; // finished
    public static final RegistryObject<MobEffect> MIGHT; // finished
    public static final RegistryObject<MobEffect> DOOMED; // finished
    public static final RegistryObject<MobEffect> DISARM; // finished
    public static final RegistryObject<MobEffect> REFLECT; // finished
    public static final RegistryObject<MobEffect> DEFLECT; // finished
    public static final RegistryObject<MobEffect> MICROSTUN; // finished
    public static final RegistryObject<MobEffect> CORROSION; // finished

    static {
        FURY = EFFECTS.register("fury", () -> new Fury(MobEffectCategory.NEUTRAL, 16057348)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, "5b9aec64-4a33-11ee-be56-0242ac120002", 0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL));
        HAEMORRHAGE = EFFECTS.register("haemorrhage", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 6553857, true));
        STUN = EFFECTS.register("stun", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 10179691, true)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, "103503fe-4a33-11ee-be56-0242ac120002", -10, AttributeModifier.Operation.ADDITION));
        BARRIER = EFFECTS.register("barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true)
                .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "21fe121a-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION));
        LAYERED_BARRIER = EFFECTS.register("layered_barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 126,true)
                .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "2c3db8d4-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION));
        ABSOLUTE_BARRIER = EFFECTS.register("absolute_barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 7995643,true)
                .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "444492cc-4a33-11ee-be56-0242ac120002", 0.5, AttributeModifier.Operation.ADDITION));
        DEAFENING = EFFECTS.register("deafening", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 4693243,true));
        FROZEN = EFFECTS.register("frozen", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 8780799, true)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, "4b3d404c-4a33-11ee-be56-0242ac120002", -0.25f, AttributeModifier.Operation.MULTIPLY_TOTAL)
                .addAttributeModifier(Attributes.ATTACK_SPEED, "500fbb5e-4a33-11ee-be56-0242ac120002", -0.6f, AttributeModifier.Operation.ADDITION));
        MIGHT = EFFECTS.register("might", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 16767061,true)
                .addAttributeModifier(Attributes.ATTACK_DAMAGE, "5502680a-4a33-11ee-be56-0242ac120002", 1, AttributeModifier.Operation.ADDITION));
        DOOMED = EFFECTS.register("doomed", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 0, true));
        DISARM = EFFECTS.register("disarm", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 16447222,true));
        REFLECT = EFFECTS.register("reflect", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true));
        DEFLECT = EFFECTS.register("deflect", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true));
        MICROSTUN = EFFECTS.register("microstun", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 10179691,false));
        CORROSION = EFFECTS.register("corrosion", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 16057348,true)
                .addAttributeModifier(Attributes.ARMOR, "df08f537-e143-4f64-90e8-4a46c505de44", -1f, AttributeModifier.Operation.ADDITION));
    }
    public static void register(IEventBus eventBus) { EFFECTS.register(eventBus); }
}
