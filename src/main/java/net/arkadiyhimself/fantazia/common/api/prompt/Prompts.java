package net.arkadiyhimself.fantazia.common.api.prompt;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.FTZKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Prompts {

    private static final Map<ResourceLocation, Prompt> PROMPTS = Maps.newHashMap();
    public static final Codec<Prompt> CODEC = ResourceLocation.CODEC.xmap(PROMPTS::get, Prompt::id);

    public static final Prompt USE_MELEE_BLOCK = pressButton("use_melee_block", () -> FTZKeyMappings.BLOCK.getKey().getDisplayName());
    public static final Prompt OPEN_TALENT_SCREEN = pressButton("open_talent_screen", () -> FTZKeyMappings.TALENTS.getKey().getDisplayName());
    public static final Prompt USE_DOUBLE_JUMP = pressButton("use_double_jump", () -> Minecraft.getInstance().options.keyJump.getKey().getDisplayName());
    public static final Prompt USE_SPELLCAST1 = spellCastButton(1, () -> FTZKeyMappings.SPELLCAST1.getKey().getDisplayName());
    public static final Prompt USE_SPELLCAST2 = spellCastButton(2, () -> FTZKeyMappings.SPELLCAST2.getKey().getDisplayName());
    public static final Prompt USE_SPELLCAST3 = spellCastButton(3, () -> FTZKeyMappings.SPELLCAST3.getKey().getDisplayName());


    public static Prompt getPrompt(ResourceLocation id) {
        return PROMPTS.get(id);
    }

    public static Map<ResourceLocation, Prompt> getPrompts() {
        return PROMPTS.isEmpty() ? ImmutableMap.of() : ImmutableMap.copyOf(PROMPTS);
    }

    private static Prompt pressButton(String name, Supplier<Object> button) {
        ResourceLocation id = Fantazia.location(name);
        String title = "fantazia.gui.prompt.press_button_title";
        String text = "fantazia.gui.prompt." + name;
        Prompt prompt = new Prompt(id, title, text, id.withPrefix("prompt_toast/"), List.of(button), List.of());
        PROMPTS.put(id, prompt);
        return prompt;
    }

    private static Prompt spellCastButton(int index, Supplier<Object> button) {
        ResourceLocation id = Fantazia.location("use_spellcast" + index);
        String title = "fantazia.gui.prompt.press_button_title";
        String text = "fantazia.gui.prompt." + "use_spellcast";
        Prompt prompt = new Prompt(id, title, text, Fantazia.location("prompt_toast/use_spellcast"), List.of(button), List.of());
        PROMPTS.put(id, prompt);
        return prompt;
    }

    private static Prompt create(String name, List<Supplier<Object>> forTitle, List<Supplier<Object>> forText) {
        ResourceLocation id = Fantazia.location(name);
        String title = "fantazia.gui.prompt." + name + ".title";
        String text = "fantazia.gui.prompt." + name;
        Prompt prompt = new Prompt(id, title, text, id.withPrefix("prompt_toast/"), forTitle, forText);
        PROMPTS.put(id, prompt);
        return prompt;
    }
}
