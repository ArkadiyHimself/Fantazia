package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureManager;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.features.AuraCarry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class AuraCarrierCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_AURA = (context, builder) -> {
        List<RegistryObject<BasicAura<?,?>>> auras = List.copyOf(FantazicRegistry.AURAS.getEntries());
        return SharedSuggestionProvider.suggestResource(auras.stream().map(RegistryObject::getId), builder);
    };
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("auracarrier").requires(commandSourceStack -> commandSourceStack.hasPermission(2)).then(Commands.argument("aura", ResourceLocationArgument.id()).suggests(SUGGEST_AURA).executes((AuraCarrierCommand::createAura))));
    }
    private static int createAura(CommandContext<CommandSourceStack> commandContext) {
        ResourceLocation id = commandContext.getArgument("aura", ResourceLocation.class);
        List<RegistryObject<BasicAura<?,?>>> auras = List.copyOf(FantazicRegistry.AURAS.getEntries()).stream().toList();
        BasicAura<?,?> aura = null;
        for (RegistryObject<BasicAura<?,?>> basicAuraRegistryObject : auras) if (basicAuraRegistryObject.getId().equals(id)) aura = basicAuraRegistryObject.get();
        if (aura == null) return 0;
        BasicAura<?, ?> finalAura = aura;

        BlockPos blockpos = BlockPos.containing(commandContext.getSource().getPosition());
        ServerLevel serverlevel = commandContext.getSource().getLevel();
        ArmorStand armorStand = new ArmorStand(serverlevel, blockpos.getX(), blockpos.getY(), blockpos.getZ());
        serverlevel.addFreshEntity(armorStand);
        commandContext.getSource().sendSuccess(() -> Component.translatable("commands.summon.success", armorStand.getDisplayName()), true);

        FeatureManager featureManager = FeatureGetter.getUnwrap(armorStand);
        if (featureManager == null) return 0;
        featureManager.getFeature(AuraCarry.class).ifPresent(auraCarry -> auraCarry.setAura(finalAura));
        return 1;
    }
}
