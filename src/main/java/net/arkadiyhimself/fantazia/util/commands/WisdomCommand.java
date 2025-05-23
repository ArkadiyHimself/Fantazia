package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class WisdomCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wisdom").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.argument("amount", IntegerArgumentType.integer(0, Integer.MAX_VALUE)).executes(context -> {
            ServerPlayer serverPlayer = context.getSource().getPlayer();
            if (serverPlayer == null) return 0;
            TalentsHolder talentsHolder = PlayerAbilityHelper.takeHolder(serverPlayer, TalentsHolder.class);
            if (talentsHolder == null) return 0;
            talentsHolder.setWisdom(IntegerArgumentType.getInteger(context, "amount"));
            return 1;
        })));
    }

}
