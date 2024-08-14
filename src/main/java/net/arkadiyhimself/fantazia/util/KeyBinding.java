package net.arkadiyhimself.fantazia.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
    public static final String KEY_MOD = "key.fantazia";

    public static final String KEY_DASH = "key.fantazia.dash";
    public static final String MOUSE_BLOCK = "mouse.fantazia.block";
    public static final String KEY_SWORD_ABILITY = "key.fantazia.sword_ability";
    public static final String KEY_SPELLCAST1 = "key.fantazia.spellcast1";
    public static final String KEY_SPELLCAST2 = "key.fantazia.spellcast2";
    public static final String KEY_TALENTS = "key.fantazia.talents";

    public static final KeyMapping DASH;
    public static final KeyMapping BLOCK;
    public static final KeyMapping SWORD_ABILITY;
    public static final KeyMapping SPELLCAST1;
    public static final KeyMapping SPELLCAST2;
    public static final KeyMapping TALENTS;
    static {
        DASH = new KeyMapping(KEY_DASH, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_MOD);
        BLOCK = new KeyMapping(MOUSE_BLOCK, KeyConflictContext.IN_GAME, InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_MOD);
        SWORD_ABILITY = new KeyMapping(KEY_SWORD_ABILITY, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, KEY_MOD);
        SPELLCAST1 = new KeyMapping(KEY_SPELLCAST1, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, KEY_MOD);
        SPELLCAST2 = new KeyMapping(KEY_SPELLCAST2, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_I, KEY_MOD);
        TALENTS = new KeyMapping(KEY_TALENTS, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, KEY_MOD);
    }
}
