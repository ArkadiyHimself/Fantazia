package net.arkadiyhimself.fantazia;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class FantazicConfig {
    private static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> staminaBarXoff;
    public static final ModConfigSpec.ConfigValue<Integer> staminaBarYoff;

    public static final ModConfigSpec.ConfigValue<Integer> manaBarXoff;
    public static final ModConfigSpec.ConfigValue<Integer> manaBarYoff;

    public static final ModConfigSpec.ConfigValue<Integer> curioSlotsXoff;
    public static final ModConfigSpec.ConfigValue<Integer> curioSlotsYoff;

    public static final ModConfigSpec.ConfigValue<Integer> euphoriaIconXoff;
    public static final ModConfigSpec.ConfigValue<Integer> euphoriaIconYoff;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("The offsets of custom GUIs from Fantazia").push("offsets");
        staminaBarXoff = builder.
                comment("The X offset of stamina bar")
                .define("staminaBarXoff", 0);
        staminaBarYoff = builder.
                comment("The Y offset of stamina bar")
                .define("staminaBarYoff", 0);

        manaBarXoff = builder.
                comment("The X offset of mana bar")
                .define("manaBarXoff", 0);

        manaBarYoff = builder.
                comment("The Y offset of mana bar")
                .define("manaBarYoff", 0);
        curioSlotsXoff = builder
                .comment("The X offset of curio slots for Spell Casters and Aura Casters")
                .define("curioSlotsXoff", 0);
        curioSlotsYoff = builder
                .comment("The Y offset of curio slots for Spell Casters and Aura Casters")
                .define("curioSlotsYoff", 0);
        euphoriaIconXoff = builder
                .comment("The X offset of euphoria icon")
                .define("euphoriaIconXoff", 0);
        euphoriaIconYoff = builder
                .comment("The Y offset of euphoria icon")
                .define("euphoriaIconYoff", 0);
        builder.pop();



        SPEC = builder.build();
    }

    public static void setup(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC);
    }
}
