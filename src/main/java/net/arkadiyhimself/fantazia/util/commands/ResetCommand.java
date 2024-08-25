package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.LootTablePSERAN;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.ManaData;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.StaminaData;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ResetCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("reset")
                .then(Commands.literal("talents").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    AbilityGetter.abilityConsumer(serverPlayer, TalentsHolder.class, TalentsHolder::revokeAll);
                    return 1;
                }))
                .then(Commands.literal("health").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    serverPlayer.setHealth(serverPlayer.getMaxHealth());
                    serverPlayer.getFoodData().eat(20,20);
                    AbilityGetter.abilityConsumer(serverPlayer, ManaData.class, ManaData::restore);
                    AbilityGetter.abilityConsumer(serverPlayer, StaminaData.class, StaminaData::restore);
                    return 1;
                }))
                .then(Commands.literal("loot_modifiers").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    AbilityGetter.abilityConsumer(serverPlayer, LootTablePSERAN.class, LootTablePSERAN::reset);
                    return 1;
                })));
    }
}
