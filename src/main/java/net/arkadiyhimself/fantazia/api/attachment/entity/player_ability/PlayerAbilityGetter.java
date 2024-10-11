package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability;

import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.ManaHolder;
import net.arkadiyhimself.fantazia.api.type.entity.IPlayerAbility;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PlayerAbilityGetter {

    public static <T extends IPlayerAbility> @Nullable T takeHolder(Player player, Class<T> tClass) {
        return player.getData(FTZAttachmentTypes.ABILITY_MANAGER).actualHolder(tClass);
    }

    public static <T extends IPlayerAbility> void acceptConsumer(Player player, Class<T> tClass, Consumer<T> consumer) {
        player.getData(FTZAttachmentTypes.ABILITY_MANAGER).optionalHolder(tClass).ifPresent(consumer);
    }

    public static boolean wasteMana(Player player, float amount) {
        ManaHolder manaHolder = takeHolder(player, ManaHolder.class);
        return manaHolder != null && manaHolder.wasteMana(amount);
    }
}
