package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ResetCommand {
    private ResetCommand() {}
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("reset")
                .then(Commands.literal("talents").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    PlayerAbilityGetter.acceptConsumer(serverPlayer, TalentsHolder.class, TalentsHolder::revokeAll);
                    return 1;
                }))
                .then(Commands.literal("health").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    serverPlayer.setHealth(serverPlayer.getMaxHealth());
                    serverPlayer.getFoodData().eat(20,20);
                    PlayerAbilityGetter.acceptConsumer(serverPlayer, ManaHolder.class, ManaHolder::restore);
                    PlayerAbilityGetter.acceptConsumer(serverPlayer, StaminaHolder.class, StaminaHolder::restore);
                    return 1;
                }))
                .then(Commands.literal("loot_modifiers").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    PlayerAbilityGetter.acceptConsumer(serverPlayer, LootTableModifiersHolder.class, LootTableModifiersHolder::reset);
                    return 1;
                }))
                .then(Commands.literal("custom_criteria").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    PlayerAbilityGetter.acceptConsumer(serverPlayer, CustomCriteriaHolder.class, CustomCriteriaHolder::reset);
                    return 1;
                })));
    }
}
