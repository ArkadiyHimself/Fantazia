package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities.SelfSpell;
import net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities.SpellHelper;
import net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities.TargetedSpell;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.List;

public class SpellCastCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_SELF_SPELL = (context, builder) -> {
        Collection<SelfSpell> spells = SpellHelper.SELF_SPELLS.values();
        return SharedSuggestionProvider.suggestResource(spells.stream().map(SelfSpell::getId), builder);
    };
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TARGETED_SPELL = (context, builder) -> {
        Collection<TargetedSpell<? extends LivingEntity>> spells = SpellHelper.TARGETED_SPELLS.values();
        return SharedSuggestionProvider.suggestResource(spells.stream().map(TargetedSpell::getId), builder);
    };
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spellcast").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("self").then(Commands.argument("spell", ResourceLocationArgument.id()).suggests(SUGGEST_SELF_SPELL)
                        .executes(commandContext -> {
                            castSelfSpell(commandContext);
                            return 1;
                        })))
                .then(Commands.literal("targeted").then(Commands.argument("spell", ResourceLocationArgument.id()).suggests(SUGGEST_TARGETED_SPELL)
                        .executes(context -> {
                            castTargetedSpell(context);
                            return 1;
                        }).then(Commands.argument("entities", EntityArgument.entities())
                                .executes(context -> {
                                    castTargetedSpells(context, EntityArgument.getEntities(context, "entities"));
                                    return 1;
                                })))));
    }
    private static void castSelfSpell(CommandContext<CommandSourceStack> commandContext) {
        ServerPlayer player = commandContext.getSource().getPlayer();
        if (player != null) {
            ResourceLocation id = commandContext.getArgument("spell", ResourceLocation.class);
            SelfSpell spell = SpellHelper.SELF_SPELLS.get(id);
            commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.self.success", spell.getName()), true);
            spell.onCast(player);
        }
    }
    private static void castTargetedSpell(CommandContext<CommandSourceStack> commandContext) {
        ServerPlayer player = commandContext.getSource().getPlayer();
        if (player == null) return;
        ResourceLocation id = commandContext.getArgument("spell", ResourceLocation.class);
        TargetedSpell<? extends LivingEntity> spell = SpellHelper.TARGETED_SPELLS.get(id);
        LivingEntity target = SpellHelper.commandTargetedSpell(player, spell);
        if (target == null) commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.failure"), true);
        else commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.success.single", spell.getName(), target.getDisplayName()), true);
    }
    private static void castTargetedSpells(CommandContext<CommandSourceStack> commandContext, Collection<? extends Entity> entities) {
        List<LivingEntity> livingEntities = Lists.newArrayList();
        ServerPlayer player = commandContext.getSource().getPlayer();
        if (player == null) return;
        entities.forEach(entity -> {
            if (entity instanceof LivingEntity livingEntity) livingEntities.add(livingEntity);
        });
        if (livingEntities.isEmpty()) {
            commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.failure"), true);
            return;
        }
        ResourceLocation id = commandContext.getArgument("spell", ResourceLocation.class);
        TargetedSpell<? extends LivingEntity> spell = SpellHelper.TARGETED_SPELLS.get(id);
        List<? extends LivingEntity> cast = SpellHelper.commandTargetedSpell(player, spell, livingEntities);
        if (cast.isEmpty()) commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.failure"), true);
        else commandContext.getSource().sendSuccess(() -> Component.translatable(  "commands.spellcast.targeted.success", spell.getName(), cast.size()),true);
    }
}
