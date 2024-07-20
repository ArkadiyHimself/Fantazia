package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public class CooldownCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("cooldown").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                // add
                .then(Commands.literal("add").then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument("item", ItemArgument.item(buildContext)).then(Commands.argument("seconds", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                .executes(context -> addCooldown(context.getSource(), ItemArgument.getItem(context, "item"), EntityArgument.getPlayers(context, "players"), IntegerArgumentType.getInteger(context, "seconds")))))))
                // remove
                .then(Commands.literal("remove").then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument("item", ItemArgument.item(buildContext))
                                .executes(context -> removeCooldown(context.getSource(), ItemArgument.getItem(context, "item"), EntityArgument.getPlayers(context, "players")))))));
    }
    private static int addCooldown(CommandSourceStack pSource, ItemInput pItem, Collection<ServerPlayer> pTargets, int seconds) throws CommandSyntaxException {
        for (ServerPlayer serverPlayer : pTargets) {
            serverPlayer.getCooldowns().addCooldown(pItem.getItem(), seconds * 20);
        }
        ItemStack itemstack = pItem.createItemStack(1, false);
        if (pTargets.size() == 1) {
            pSource.sendSuccess(() -> Component.translatable("commands.cooldown.add.success", seconds, itemstack.getDisplayName(), pTargets.iterator().next().getDisplayName()), true);
        } else {
            pSource.sendSuccess(() -> Component.translatable("commands.cooldown.add.success", seconds, itemstack.getDisplayName(), pTargets.size()), true);
        }

        return pTargets.size();
    }
    private static int removeCooldown(CommandSourceStack pSource, ItemInput pItem, Collection<ServerPlayer> pTargets) throws CommandSyntaxException {
        for (ServerPlayer serverPlayer : pTargets) {
            if (serverPlayer.getCooldowns().isOnCooldown(pItem.getItem())) serverPlayer.getCooldowns().removeCooldown(pItem.getItem());
        }
        ItemStack itemstack = pItem.createItemStack(1, false);
        if (pTargets.size() == 1) {
            pSource.sendSuccess(() -> Component.translatable("commands.cooldown.remove.success", itemstack.getDisplayName(), pTargets.iterator().next().getDisplayName()), true);
        } else {
            pSource.sendSuccess(() -> Component.translatable("commands.cooldown.remove.success", itemstack.getDisplayName(), pTargets.size()), true);
        }
        return pTargets.size();
    }
}
