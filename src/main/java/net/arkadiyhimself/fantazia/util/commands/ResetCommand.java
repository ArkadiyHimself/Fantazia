package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.*;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.EffectsSpawnAppliersHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ResetCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("reset")
                .then(Commands.literal("talents").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    PlayerAbilityHelper.acceptConsumer(serverPlayer, TalentsHolder.class, TalentsHolder::revokeAll);
                    return 1;
                }))
                .then(Commands.literal("health").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    serverPlayer.setHealth(serverPlayer.getMaxHealth());
                    serverPlayer.getFoodData().eat(20,20);
                    PlayerAbilityHelper.acceptConsumer(serverPlayer, ManaHolder.class, ManaHolder::restore);
                    PlayerAbilityHelper.acceptConsumer(serverPlayer, StaminaHolder.class, StaminaHolder::restore);
                    return 1;
                }))
                .then(Commands.literal("wisdom_rewards").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    PlayerAbilityHelper.acceptConsumer(serverPlayer, TalentsHolder.class, TalentsHolder::resetWisdomRewards);
                    return 1;
                }))
                .then(Commands.literal("loot_modifiers").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    serverPlayer.setData(FTZAttachmentTypes.OBTAINED_DASHSTONE, false);
                    PlayerAbilityHelper.acceptConsumer(serverPlayer, LootTableModifiersHolder.class, LootTableModifiersHolder::reset);
                    return 1;
                }))
                .then(Commands.literal("custom_criteria").executes(context -> {
                    ServerPlayer serverPlayer = context.getSource().getPlayer();
                    if (serverPlayer == null) return 0;
                    PlayerAbilityHelper.acceptConsumer(serverPlayer, CustomCriteriaHolder.class, CustomCriteriaHolder::reset);
                    return 1;
                }))
                .then(Commands.literal("effects_on_spawn").executes(context -> {
                    ServerLevel serverLevel = context.getSource().getLevel();
                    LevelAttributesHelper.acceptConsumer(serverLevel, EffectsSpawnAppliersHolder.class, EffectsSpawnAppliersHolder::reset);
                    return 1;
                }))
                .then(Commands.literal("dashstone_entity").executes(context -> {
                    PlayerAbilityHelper.acceptConsumer(context.getSource().getPlayer(), DashHolder.class, DashHolder::resetDashstoneEntity);
                    return 1;
                }))
                .then(Commands.literal("prompts").executes(context -> {
                    Player player = context.getSource().getPlayer();
                    if (player != null) player.getData(FTZAttachmentTypes.USED_PROMPTS).clear();
                    return 1;
                }))
                .then(Commands.literal("experience").executes(context -> {
                    if (context.getSource().getPlayer() instanceof ServerPlayer player) {
                        player.setExperienceLevels(0);
                        player.setExperiencePoints(0);
                    }
                    return 1;
                })));
    }
}
