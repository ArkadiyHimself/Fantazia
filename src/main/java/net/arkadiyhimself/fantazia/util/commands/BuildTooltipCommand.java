package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class BuildTooltipCommand {

    private BuildTooltipCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("build_tooltip")
                .then(Commands.literal("spell").then(Commands.argument("id", ResourceArgument.resource(context, FantazicRegistries.Keys.SPELL))
                        .executes(BuildTooltipCommand::spellTooltip)))
                .then(Commands.literal("aura").then(Commands.argument("id", ResourceArgument.resource(context, FantazicRegistries.Keys.AURA))
                        .executes(BuildTooltipCommand::auraTooltip)))
                .then(Commands.literal("rune").then(Commands.argument("id", ResourceArgument.resource(context, FantazicRegistries.Keys.RUNE))
                        .executes(BuildTooltipCommand::runeTooltip))));
    }

    private static int spellTooltip(CommandContext<CommandSourceStack> commandContext) {
        ServerPlayer player = commandContext.getSource().getPlayer();
        if (player == null) return 0;
        ResourceLocation id = commandContext.getArgument("id", ResourceLocation.class);
        IPacket.buildSpellTooltip(player, id);
        return 1;
    }

    private static int auraTooltip(CommandContext<CommandSourceStack> commandContext) {
        ServerPlayer player = commandContext.getSource().getPlayer();
        if (player == null) return 0;
        ResourceLocation id = commandContext.getArgument("id", ResourceLocation.class);
        IPacket.buildAuraTooltip(player, id);
        return 1;
    }

    private static int runeTooltip(CommandContext<CommandSourceStack> commandContext) {
        ServerPlayer player = commandContext.getSource().getPlayer();
        if (player == null) return 0;
        ResourceLocation id = commandContext.getArgument("id", ResourceLocation.class);
        IPacket.buildRuneTooltip(player, id);
        return 1;
    }
}
