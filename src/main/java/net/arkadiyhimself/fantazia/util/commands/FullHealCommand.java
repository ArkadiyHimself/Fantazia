package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class FullHealCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fullheal").executes(context -> {
            ServerPlayer serverPlayer = context.getSource().getPlayer();
            if (serverPlayer != null) serverPlayer.setHealth(serverPlayer.getMaxHealth());
            return 1;
        }));
    }
}
