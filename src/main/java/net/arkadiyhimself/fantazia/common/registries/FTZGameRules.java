package net.arkadiyhimself.fantazia.common.registries;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.world.level.GameRules;

import java.util.Map;

public class FTZGameRules {
    private static final Map<GameRules.Key<?>, GameRules.Type<?>> GAMERULE_MAP = Maps.newHashMap();

    public static final GameRules.Key<GameRules.BooleanValue> EUPHORIA;
    public static final GameRules.Key<GameRules.BooleanValue> STUN_FROM_ATTACKS;
    public static final GameRules.Key<GameRules.BooleanValue> STUN_FROM_FALLING;
    public static final GameRules.Key<GameRules.BooleanValue> STUN_FROM_EXPLOSION;
    public static final GameRules.Key<GameRules.BooleanValue> PROMPTS;

    private static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        GameRules.Key<T> key = new GameRules.Key<>(Fantazia.location(name).toLanguageKey(), category);
        GameRules.Type<?> obj = GAMERULE_MAP.put(key, type);
        if (obj != null) throw new IllegalStateException("Duplicate game rule registration for " + name);
        else return key;
    }

    public static void onModSetup() {
        GAMERULE_MAP.forEach((key,type) -> GameRules.register(key.getId(), key.getCategory(), type));
    }

    static {
        EUPHORIA = register("euphoria", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
        STUN_FROM_ATTACKS = register("stun_from_attack", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
        STUN_FROM_FALLING = register("stun_from_falling", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
        STUN_FROM_EXPLOSION = register("stun_from_explosion", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
        PROMPTS = register("prompts", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    }
}
