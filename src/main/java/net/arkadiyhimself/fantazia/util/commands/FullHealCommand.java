package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.ManaData;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.StaminaData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class FullHealCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fullheal").executes(context -> {
            ServerPlayer serverPlayer = context.getSource().getPlayer();
            if (serverPlayer == null) return 0;
            serverPlayer.setHealth(serverPlayer.getMaxHealth());
            serverPlayer.getFoodData().eat(20,20);
            AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
            if (abilityManager != null) {
                abilityManager.getAbility(ManaData.class).ifPresent(ManaData::restore);
                abilityManager.getAbility(StaminaData.class).ifPresent(StaminaData::restore);
            }
            return 1;
        }));
    }
}
