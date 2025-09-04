package net.arkadiyhimself.fantazia.common.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.api.custom_registry.DeferredRune;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForgeMod;

public class Runes {

    private static final FantazicRegistries.Runes REGISTER = FantazicRegistries.createRunes(Fantazia.MODID);

    public static final DeferredRune<Rune> EMPTY;
    public static final DeferredRune<Rune> OMNIDIRECTIONAL;
    public static final DeferredRune<Rune> AEROBAT;
    public static final DeferredRune<Rune> PIERCER;
    public static final DeferredRune<Rune> PURE_VESSEL;
    public static final DeferredRune<Rune> PROSPERITY;
    public static final DeferredRune<Rune> NOISELESS;
    public static final DeferredRune<Rune> METICULOUS;
    public static final DeferredRune<Rune> EXTENSION;
    public static final DeferredRune<Rune> AQUATIC;

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    private static DeferredRune<Rune> register(String id, Rune.Builder builder) {
        return REGISTER.register(id, builder);
    }

    static {
        EMPTY = register("empty", Rune.builder()
                .nameFormatting(ChatFormatting.GRAY)
                .descFormatting(ChatFormatting.GRAY));

        OMNIDIRECTIONAL = register("omnidirectional", Rune.builder()
                .nameFormatting(ChatFormatting.DARK_BLUE)
                .descFormatting(ChatFormatting.BLUE));

        AEROBAT = register("aerobat", Rune.builder()
                .nameFormatting(ChatFormatting.DARK_BLUE)
                .descFormatting(ChatFormatting.BLUE));

        PIERCER = register("piercer", Rune.builder()
                .nameFormatting(ChatFormatting.DARK_RED)
                .descFormatting(ChatFormatting.RED));

        PURE_VESSEL = register("pure_vessel", Rune.builder()
                .nameFormatting(ChatFormatting.DARK_GRAY)
                .descFormatting(ChatFormatting.GRAY)
                .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, 1.5, AttributeModifier.Operation.ADD_VALUE));

        PROSPERITY = register("prosperity", Rune.builder()
                .nameFormatting(ChatFormatting.GREEN)
                .descFormatting(ChatFormatting.BLUE)
                .addAttributeModifier(Attributes.LUCK, 2, AttributeModifier.Operation.ADD_VALUE)
                .fortune(2).looting(2));

        NOISELESS = register("noiseless", Rune.builder()
                .nameFormatting(ChatFormatting.DARK_BLUE)
                .descFormatting(ChatFormatting.BLUE));

        METICULOUS = register("meticulous", Rune.builder()
                .nameFormatting(ChatFormatting.DARK_PURPLE)
                .descFormatting(ChatFormatting.LIGHT_PURPLE));

        EXTENSION = register("extension", Rune.builder()
                .nameFormatting(ChatFormatting.AQUA)
                .descFormatting(ChatFormatting.BLUE));

        AQUATIC = register("aquatic", Rune.builder()
                .onTick(livingEntity -> {
                    if (livingEntity.isUnderWater())
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 2));
                })
                .addAttributeModifier(Attributes.OXYGEN_BONUS, 1.5, AttributeModifier.Operation.ADD_VALUE)
                .addAttributeModifier(NeoForgeMod.SWIM_SPEED, 0.7, AttributeModifier.Operation.ADD_VALUE)
                .nameFormatting(ChatFormatting.AQUA)
                .descFormatting(ChatFormatting.BLUE));
    }
}
