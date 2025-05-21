package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.runes.Rune;
import net.arkadiyhimself.fantazia.api.custom_registry.DeferredRune;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;

public class Runes {

    private static final FantazicRegistries.Runes REGISTER = FantazicRegistries.createRunes(Fantazia.MODID);

    public static final DeferredRune<Rune> EMPTY;
    public static final DeferredRune<Rune> OMNIDIRECTIONAL;
    public static final DeferredRune<Rune> AEROBAT;
    public static final DeferredRune<Rune> PIERCER;
    public static final DeferredRune<Rune> PURE_VESSEL;
    public static final DeferredRune<Rune> PROSPERITY;
    public static final DeferredRune<Rune> NOISELESS;

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    private static DeferredRune<Rune> register(String id, Rune.Builder builder) {
        return REGISTER.register(id, builder::build);
    }

    static {
        EMPTY = register("empty", Rune.builder(Fantazia.res("empty"))
                .nameFormatting(ChatFormatting.GRAY, ChatFormatting.BOLD)
                .descFormatting(ChatFormatting.GRAY));

        OMNIDIRECTIONAL = register("omnidirectional", Rune.builder(Fantazia.res("omnidirectional"))
                .nameFormatting(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD)
                .descFormatting(ChatFormatting.BLUE));

        AEROBAT = register("aerobat", Rune.builder(Fantazia.res("aerobat"))
                .nameFormatting(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD)
                .descFormatting(ChatFormatting.BLUE));

        PIERCER = register("piercer", Rune.builder(Fantazia.res("piercer"))
                .nameFormatting(ChatFormatting.DARK_RED, ChatFormatting.BOLD)
                .descFormatting(ChatFormatting.RED));

        PURE_VESSEL = register("pure_vessel", Rune.builder(Fantazia.res("pure_vessel"))
                .nameFormatting(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD)
                .descFormatting(ChatFormatting.GRAY)
                .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, Fantazia.res("rune.pure_vessel"), 1.5, AttributeModifier.Operation.ADD_VALUE));

        PROSPERITY = register("prosperity", Rune.builder(Fantazia.res("prosperity"))
                .nameFormatting(ChatFormatting.GREEN, ChatFormatting.BOLD)
                .descFormatting(ChatFormatting.BLUE)
                .addAttributeModifier(Attributes.LUCK, Fantazia.res("rune.prosperity"), 2, AttributeModifier.Operation.ADD_VALUE)
                .fortune(2).looting(2));

        NOISELESS = register("noiseless", Rune.builder(Fantazia.res("noiseless"))
                .nameFormatting(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD)
                .descFormatting(ChatFormatting.BLUE));
    }
}
