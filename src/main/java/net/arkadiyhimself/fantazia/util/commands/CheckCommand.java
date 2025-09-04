package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.CustomCriteriaHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.SpellInstancesHolder;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompt;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompts;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.util.library.concept_of_consistency.ConCosInstance;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CheckCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_SPELL = (context, builder) -> {
        List<Holder.Reference<AbstractSpell>> spells = new java.util.ArrayList<>(List.copyOf(FantazicRegistries.SPELLS.holders().toList()));
        return SharedSuggestionProvider.suggestResource(spells.stream().map(abstractSpellReference -> abstractSpellReference.value().getID()), builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("check")
                .then(Commands.literal("concept_of_consistency")
                        .then(Commands.argument("chance", DoubleArgumentType.doubleArg(0, 1)).executes(CheckCommand::checkConCos)
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 4096)).executes(CheckCommand::checkConCosWithAmount))))
                .then(Commands.literal("amplifier")
                        .then(Commands.argument("spell", ResourceLocationArgument.id()).suggests(SUGGEST_SPELL)
                                .executes(CheckCommand::checkSpellAmplifier)))
                .then(Commands.literal("prompts")
                        .then(Commands.literal("all").executes(CheckCommand::checkAllPrompts))
                        .then(Commands.literal("used").executes(CheckCommand::checkUsedPrompts)))
                .then(Commands.literal("custom_criteria")
                        .then(Commands.literal("obtained_items").executes(CheckCommand::checkObtainedItems))
                        .then(Commands.literal("obtained_runes").executes(CheckCommand::checkObtainedRunes))));
    }

    private static int checkConCos(CommandContext<CommandSourceStack> context) {
        ServerPlayer serverPlayer = context.getSource().getPlayer();
        if (serverPlayer == null) return 0;
        double chance = DoubleArgumentType.getDouble(context, "chance");
        ConCosInstance instance = new ConCosInstance(chance);
        int fail = 0;
        int success = 0;
        for (int i = 0; i < 100; i++) {
            if (instance.performAttempt()) success++;
            else fail++;
        }

        float average = (float) success / 100f;
        String avr = String.format("%.3f", average);
        String chn = String.format("%.3f" , chance);

        serverPlayer.sendSystemMessage(Component.literal("Tested concept of consistency (chance: " + chn + ")"));
        serverPlayer.sendSystemMessage(Component.literal("Successes: " + success));
        serverPlayer.sendSystemMessage(Component.literal("Fails: " + fail));
        serverPlayer.sendSystemMessage(Component.literal("Average: " + avr));

        return 1;
    }

    private static int checkConCosWithAmount(CommandContext<CommandSourceStack> context) {
        ServerPlayer serverPlayer = context.getSource().getPlayer();
        if (serverPlayer == null) return 0;
        double chance = DoubleArgumentType.getDouble(context, "chance");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ConCosInstance instance = new ConCosInstance(chance);
        int fail = 0;
        int success = 0;
        for (int i = 0; i < amount; i++) {
            if (instance.performAttempt()) success++;
            else fail++;
        }

        float average = (float) success / amount;
        String avr = String.format("%.3f", average);
        String chn = String.format("%.3f" , chance);

        serverPlayer.sendSystemMessage(Component.literal("Tested concept of consistency (chance: " + chn + ")"));
        serverPlayer.sendSystemMessage(Component.literal("Successes: " + success));
        serverPlayer.sendSystemMessage(Component.literal("Fails: " + fail));
        serverPlayer.sendSystemMessage(Component.literal("Average: " + avr));

        return 1;
    }

    private static int checkSpellAmplifier(CommandContext<CommandSourceStack> context) {
        ServerPlayer serverPlayer = context.getSource().getPlayer();
        if (serverPlayer == null) return 0;
        ResourceLocation spellId = ResourceLocationArgument.getId(context, "spell");
        Holder<AbstractSpell> spellHolder = FantazicRegistries.SPELLS.getHolderOrThrow(ResourceKey.create(FantazicRegistries.Keys.SPELL, spellId));
        SpellInstancesHolder instancesHolder = PlayerAbilityHelper.takeHolder(serverPlayer, SpellInstancesHolder.class);
        if (instancesHolder == null) return 0;
        serverPlayer.sendSystemMessage(Component.literal("Spell's amplifier: " + instancesHolder.getOrCreate(spellHolder).getAmplifier()));
        return 1;
    }

    private static int checkAllPrompts(CommandContext<CommandSourceStack> context) {
        ServerPlayer serverPlayer = context.getSource().getPlayer();
        if (serverPlayer == null) return 0;
        Set<ResourceLocation> prompts = Prompts.getPrompts().keySet();

        if (prompts.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.literal("No existing prompts were found!"));
        } else {
            serverPlayer.sendSystemMessage(Component.literal("All existing prompts:"));
            for (ResourceLocation id : prompts) serverPlayer.sendSystemMessage(Component.literal(id.toString()));
        }
        return 1;
    }

    private static int checkUsedPrompts(CommandContext<CommandSourceStack> context) {
        ServerPlayer serverPlayer = context.getSource().getPlayer();
        if (serverPlayer == null) return 0;
        ArrayList<Prompt> prompts = serverPlayer.getData(FTZAttachmentTypes.USED_PROMPTS);

        if (prompts.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.literal("No used prompts were found!"));
        } else {
            serverPlayer.sendSystemMessage(Component.literal("All used prompts:"));
            for (Prompt prompt : prompts) serverPlayer.sendSystemMessage(Component.literal(prompt.id().toString()));
        }
        return 1;
    }

    private static int checkObtainedItems(CommandContext<CommandSourceStack> context) {
        ServerPlayer serverPlayer = context.getSource().getPlayer();
        if (serverPlayer == null) return 0;
        CustomCriteriaHolder holder = PlayerAbilityHelper.takeHolder(serverPlayer, CustomCriteriaHolder.class);
        if (holder == null) return 0;
        List<Item> obtained = holder.getObtainedItems();
        if (obtained.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.literal("No obtained items were found!"));
        } else {
            serverPlayer.sendSystemMessage(Component.literal("All obtained items:"));
            for (Item item : obtained) serverPlayer.sendSystemMessage(Component.literal(item.toString()));
        }

        return 1;
    }

    private static int checkObtainedRunes(CommandContext<CommandSourceStack> context) {
        ServerPlayer serverPlayer = context.getSource().getPlayer();
        if (serverPlayer == null) return 0;
        CustomCriteriaHolder holder = PlayerAbilityHelper.takeHolder(serverPlayer, CustomCriteriaHolder.class);
        if (holder == null) return 0;
        List<Holder<Rune>> obtained = holder.getObtainedRunes();
        if (obtained.isEmpty()) {
            serverPlayer.sendSystemMessage(Component.literal("No obtained runes were found!"));
        } else {
            serverPlayer.sendSystemMessage(Component.literal("All obtained runes:"));
            for (Holder<Rune> runeHolder : obtained) serverPlayer.sendSystemMessage(Component.literal(FantazicRegistries.RUNES.getKey(runeHolder.value()).toString()));
        }
        return 1;
    }

}
