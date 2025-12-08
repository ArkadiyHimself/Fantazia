package net.arkadiyhimself.fantazia;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class FantazicConfig {

    private static final ModConfigSpec CLIENT;

    public static final ModConfigSpec.ConfigValue<Integer> staminaBarXoff;
    public static final ModConfigSpec.ConfigValue<Integer> staminaBarYoff;

    public static final ModConfigSpec.ConfigValue<Integer> manaBarXoff;
    public static final ModConfigSpec.ConfigValue<Integer> manaBarYoff;

    public static final ModConfigSpec.ConfigValue<Integer> curioSlotsXoff;
    public static final ModConfigSpec.ConfigValue<Integer> curioSlotsYoff;

    private static final ModConfigSpec COMMON;

    public static final ModConfigSpec.ConfigValue<Integer> euphoriaCleanse;
    public static final ModConfigSpec.ConfigValue<Integer> passiveBarrierCleanse;
    public static final ModConfigSpec.ConfigValue<Integer> combHealthCleanse;

    static {
        ModConfigSpec.Builder client = new ModConfigSpec.Builder();

        client.comment("The offsets of custom GUIs from Fantazia").push("offsets");
        staminaBarXoff = client.
                comment("The X offset of stamina bar")
                .define("staminaBarXoff", 0);
        staminaBarYoff = client.
                comment("The Y offset of stamina bar")
                .define("staminaBarYoff", 0);

        manaBarXoff = client.
                comment("The X offset of mana bar")
                .define("manaBarXoff", 0);

        manaBarYoff = client.
                comment("The Y offset of mana bar")
                .define("manaBarYoff", 0);
        curioSlotsXoff = client
                .comment("The X offset of curio slots for Spell Casters and Aura Casters")
                .define("curioSlotsXoff", 0);
        curioSlotsYoff = client
                .comment("The Y offset of curio slots for Spell Casters and Aura Casters")
                .define("curioSlotsYoff", 0);
        client.pop();

        CLIENT = client.build();

        ModConfigSpec.Builder common = new ModConfigSpec.Builder();
        common.comment("Various values which affect the gameplay");
        common.push("cleanse").comment("Minimal cleanse strength required to dispel various effects that are not mob effects. If set to 3 or more, the effect is uncleanseable");
        euphoriaCleanse = common.comment("Euphoria effect:").define("euphoriaCleanse", 1);
        passiveBarrierCleanse = common.comment("Passive barrier effect (by default obtained by a talent given for getting netherite chestplate):").define("passiveBarrierCleanse",0);
        combHealthCleanse = common.comment("The passive heal effect from the Restore spell granted by Abnormal Beehive item; 'cleansing' it means completely canceling its incoming heal").define("combHealthCleanse", 0);
        common.pop();
        COMMON = common.build();
    }

    public static void setup(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, CLIENT);
        container.registerConfig(ModConfig.Type.COMMON, COMMON);
    }
}
