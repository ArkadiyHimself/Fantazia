package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.arkadiyhimself.fantazia.common.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.SelfSpell;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.TargetedSpell;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.List;

public class SpellCastCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_SELF_SPELL = (context, builder) -> {
        List<Holder.Reference<AbstractSpell>> spells = new java.util.ArrayList<>(List.copyOf(FantazicRegistries.SPELLS.holders().toList()));
        spells.removeIf(spell -> !(spell.value() instanceof SelfSpell));
        return SharedSuggestionProvider.suggestResource(spells.stream().map(abstractSpellReference -> abstractSpellReference.value().getID()), builder);
    };
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TARGETED_SPELL = (context, builder) -> {
        List<Holder.Reference<AbstractSpell>> spells = new java.util.ArrayList<>(List.copyOf(FantazicRegistries.SPELLS.holders().toList()));
        spells.removeIf(spell -> !(spell.value() instanceof TargetedSpell<?>));
        return SharedSuggestionProvider.suggestResource(spells.stream().map(abstractSpellReference -> abstractSpellReference.value().getID()), builder);
    };
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spellcast").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("self").then(Commands.argument("spell", ResourceLocationArgument.id()).suggests(SUGGEST_SELF_SPELL)
                        .executes(SpellCastCommand::castSelfSpell)
                        .then(Commands.argument("amplifier", IntegerArgumentType.integer(0))
                                .executes(SpellCastCommand::castSelfSpellAmpl))))
                .then(Commands.literal("targeted").then(Commands.argument("spell", ResourceLocationArgument.id()).suggests(SUGGEST_TARGETED_SPELL)
                        .executes(SpellCastCommand::castTargetedSpell)
                        .then(Commands.argument("amplifier", IntegerArgumentType.integer(0)).executes(SpellCastCommand::castTargetedSpellAmpl)
                                .then(Commands.argument("entities", EntityArgument.entities()))
                                .executes(context -> castTargetedSpells(context, EntityArgument.getEntities(context, "entities")))))));
    }
    private static int castSelfSpell(CommandContext<CommandSourceStack> commandContext) {
        ServerPlayer player = commandContext.getSource().getPlayer();
        if (player == null) return 0;
        ResourceLocation id = commandContext.getArgument("spell", ResourceLocation.class);
        Holder<AbstractSpell> spell = FantazicRegistries.SPELLS.getHolderOrThrow(ResourceKey.create(FantazicRegistries.Keys.SPELL, id));
        if (!(spell.value() instanceof SelfSpell selfSpell)) return 0;
        commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.self.success", AbstractSpell.getName(id)), true);
        SpellHelper.trySelfSpell(player, selfSpell, 0,true);
        return 1;
    }

    private static int castSelfSpellAmpl(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        ResourceLocation id = context.getArgument("spell", ResourceLocation.class);
        Holder<AbstractSpell> spell = FantazicRegistries.SPELLS.getHolderOrThrow(ResourceKey.create(FantazicRegistries.Keys.SPELL, id));
        if (!(spell.value() instanceof SelfSpell selfSpell)) return 0;
        context.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.self.success", AbstractSpell.getName(id)), true);
        int ampl = IntegerArgumentType.getInteger(context, "amplifier");
        SpellHelper.trySelfSpell(player, selfSpell, ampl,true);
        return 1;
    }

    private static int castTargetedSpell(CommandContext<CommandSourceStack> commandContext) {
        ServerPlayer player = commandContext.getSource().getPlayer();
        if (player == null) return 0;
        ResourceLocation id = commandContext.getArgument("spell", ResourceLocation.class);
        Holder<AbstractSpell> spell = FantazicRegistries.SPELLS.getHolderOrThrow(ResourceKey.create(FantazicRegistries.Keys.SPELL, id));

        if (!(spell.value() instanceof TargetedSpell<?> targetedSpell)) return 0;
        LivingEntity target = SpellHelper.commandTargetedSpell(player, 0, targetedSpell);
        if (target == null) commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.failure"), true);
        else commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.success.single", AbstractSpell.getName(id), target.getDisplayName()), true);
        return 1;
    }

    private static int castTargetedSpellAmpl(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        ResourceLocation id = context.getArgument("spell", ResourceLocation.class);
        Holder<AbstractSpell> spell = FantazicRegistries.SPELLS.getHolderOrThrow(ResourceKey.create(FantazicRegistries.Keys.SPELL, id));

        if (!(spell.value() instanceof TargetedSpell<?> targetedSpell)) return 0;
        int ampl = IntegerArgumentType.getInteger(context, "amplifier");
        LivingEntity target = SpellHelper.commandTargetedSpell(player, ampl, targetedSpell);
        if (target == null) context.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.failure"), true);
        else context.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.success.single", AbstractSpell.getName(id), target.getDisplayName()), true);
        return 1;
    }

    private static int castTargetedSpells(CommandContext<CommandSourceStack> context, Collection<? extends Entity> entities) {
        List<LivingEntity> livingEntities = Lists.newArrayList();
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;
        for (Entity entity : entities) if (entity instanceof LivingEntity livingEntity) livingEntities.add(livingEntity);
        if (livingEntities.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.failure"), true);
            return 0;
        }
        ResourceLocation id = context.getArgument("spell", ResourceLocation.class);
        Holder<AbstractSpell> spell = FantazicRegistries.SPELLS.getHolderOrThrow(ResourceKey.create(FantazicRegistries.Keys.SPELL, id));
        if (!(spell.value() instanceof TargetedSpell<?> targetedSpell)) return 0;
        int ampl = IntegerArgumentType.getInteger(context, "amplifier");
        List<? extends LivingEntity> cast = SpellHelper.commandTargetedSpell(player, ampl, targetedSpell, livingEntities);
        if (cast.isEmpty()) context.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.failure"), true);
        else context.getSource().sendSuccess(() -> Component.translatable(  "commands.spellcast.targeted.success", AbstractSpell.getName(id), cast.size()),true);
        return 1;
    }
}
