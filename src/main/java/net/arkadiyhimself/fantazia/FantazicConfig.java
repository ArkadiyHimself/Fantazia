package net.arkadiyhimself.fantazia;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.PatchouliConfigAccess;

public class FantazicConfig {
    private static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> staminaBarXoff;
    public static final ModConfigSpec.ConfigValue<Integer> staminaBarYoff;

    public static final ModConfigSpec.ConfigValue<Integer> manaBarXoff;
    public static final ModConfigSpec.ConfigValue<Integer> manaBarYoff;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        staminaBarXoff = builder.
                comment("The Y offset of stamina bar. Keep in mind that positive direction in X axis in gui is right")
                .define("staminaBarXoff", 0);
        staminaBarYoff = builder.
                comment("The Y offset of stamina bar. Keep in mind that positive direction in Y axis in gui is down")
                .define("staminaBarYoff", 0);

        manaBarXoff = builder.
                comment("The Y offset of mana bar. Keep in mind that positive direction in X axis in gui is right")
                .define("manaBarXoff", 0);

        manaBarYoff = builder.
                comment("The Y offset of mana bar. Keep in mind that positive direction in Y axis in gui is down")
                .define("manaBarYoff", 0);

        SPEC = builder.build();
    }

    public static void setup(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC);
    }
}
