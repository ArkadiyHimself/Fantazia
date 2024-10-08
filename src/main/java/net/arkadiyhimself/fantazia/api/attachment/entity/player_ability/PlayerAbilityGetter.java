package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability;

import net.arkadiyhimself.fantazia.api.type.entity.IPlayerAbility;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PlayerAbilityGetter {
    public static <T extends IPlayerAbility> @Nullable T takeHolder(Player player, Class<T> tClass) {
        PlayerAbilityManager playerAbilityManager = getUnwrap(player);
        return playerAbilityManager.actualHolder(tClass);
    }
    public static <T extends IPlayerAbility> void acceptConsumer(Player player, Class<T> tClass, Consumer<T> consumer) {
        PlayerAbilityManager playerAbilityManager = getUnwrap(player);
        playerAbilityManager.optionalHolder(tClass).ifPresent(consumer);
    }

    public static PlayerAbilityManager getUnwrap(Player player) {
        return player.getData(FTZAttachmentTypes.ABILITY_MANAGER);
    }
}
