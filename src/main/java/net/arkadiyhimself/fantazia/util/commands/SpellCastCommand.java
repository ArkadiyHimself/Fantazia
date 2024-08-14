package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.arkadiyhimself.fantazia.advanced.spell.SelfSpell;
import net.arkadiyhimself.fantazia.advanced.spell.Spell;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.advanced.spell.TargetedSpell;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
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
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.List;

public class SpellCastCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_SELF_SPELL = (context, builder) -> {
        List<RegistryObject<Spell>> spells = new java.util.ArrayList<>(List.copyOf(FantazicRegistry.SPELLS.getEntries()));
        spells.removeIf(spell -> !(spell.get() instanceof SelfSpell));
        return SharedSuggestionProvider.suggestResource(spells.stream().map(RegistryObject::getId), builder);
    };
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TARGETED_SPELL = (context, builder) -> {
        List<RegistryObject<Spell>> spells = new java.util.ArrayList<>(List.copyOf(FantazicRegistry.SPELLS.getEntries()));
        spells.removeIf(spell -> !(spell.get() instanceof TargetedSpell<?>));
        return SharedSuggestionProvider.suggestResource(spells.stream().map(RegistryObject::getId), builder);
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
        if (player == null) return;
        ResourceLocation id = commandContext.getArgument("spell", ResourceLocation.class);
        List<RegistryObject<Spell>> registryObjects = FantazicRegistry.SPELLS.getEntries().stream().toList();
        SelfSpell spell = null;
        for (RegistryObject<Spell> spellRegistryObject : registryObjects) if (spellRegistryObject.getId().equals(id) && spellRegistryObject.get() instanceof SelfSpell selfSpell) spell = selfSpell;
        if (spell == null) return;
        SelfSpell finalSpell = spell;
        commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.self.success", finalSpell.getName()), true);
        SpellHelper.trySelfSpell(player, finalSpell, true);
    }
    private static void castTargetedSpell(CommandContext<CommandSourceStack> commandContext) {
        ServerPlayer player = commandContext.getSource().getPlayer();
        if (player == null) return;
        ResourceLocation id = commandContext.getArgument("spell", ResourceLocation.class);
        List<RegistryObject<Spell>> registryObjects = FantazicRegistry.SPELLS.getEntries().stream().toList();
        TargetedSpell<?> spell = null;
        for (RegistryObject<Spell> spellRegistryObject : registryObjects) if (spellRegistryObject.getId().equals(id) && spellRegistryObject.get() instanceof TargetedSpell<?> selfSpell) spell = selfSpell;
        if (spell == null) return;
        TargetedSpell<?> finalSpell = spell;
        LivingEntity target = SpellHelper.commandTargetedSpell(player, spell);
        if (target == null) commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.failure"), true);
        else commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.success.single", finalSpell.getName(), target.getDisplayName()), true);
    }
    private static void castTargetedSpells(CommandContext<CommandSourceStack> commandContext, Collection<? extends Entity> entities) {
        List<LivingEntity> livingEntities = Lists.newArrayList();
        ServerPlayer player = commandContext.getSource().getPlayer();
        if (player == null) return;
        for (Entity entity : entities) if (entity instanceof LivingEntity livingEntity) livingEntities.add(livingEntity);
        if (livingEntities.isEmpty()) {
            commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.failure"), true);
            return;
        }
        ResourceLocation id = commandContext.getArgument("spell", ResourceLocation.class);
        List<RegistryObject<Spell>> registryObjects = FantazicRegistry.SPELLS.getEntries().stream().toList();
        TargetedSpell<?> spell = null;
        for (RegistryObject<Spell> spellRegistryObject : registryObjects) if (spellRegistryObject.getId().equals(id) && spellRegistryObject.get() instanceof TargetedSpell<?> selfSpell) spell = selfSpell;
        if (spell == null) return;
        TargetedSpell<?> finalSpell = spell;
        List<? extends LivingEntity> cast = SpellHelper.commandTargetedSpell(player, finalSpell, livingEntities);
        if (cast.isEmpty()) commandContext.getSource().sendSuccess(() -> Component.translatable("commands.spellcast.targeted.failure"), true);
        else commandContext.getSource().sendSuccess(() -> Component.translatable(  "commands.spellcast.targeted.success", finalSpell.getName(), cast.size()),true);
    }
}
