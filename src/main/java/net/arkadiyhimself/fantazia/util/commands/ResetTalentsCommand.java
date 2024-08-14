package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ResetTalentsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("talentreset").executes(context -> {
            ServerPlayer serverPlayer = context.getSource().getPlayer();
            if (serverPlayer == null) return 0;
            AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
            if (abilityManager != null) abilityManager.getAbility(TalentsHolder.class).ifPresent(TalentsHolder::revokeAll);
            return 1;
        }));
    }
}
