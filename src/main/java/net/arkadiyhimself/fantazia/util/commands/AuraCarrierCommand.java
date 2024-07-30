package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public class AuraCarrierCommand {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_AURA = (context, builder) -> {
        Collection<BasicAura<? extends Entity, ? extends Entity>> auras = BasicAura.AURAS.values();
        return SharedSuggestionProvider.suggestResource(auras.stream().map(BasicAura::getMapKey), builder);
    };
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("auracarrier").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.argument("aura", ResourceLocationArgument.id()).suggests(SUGGEST_AURA).executes((context -> {
                    ResourceLocation resourceLocation = context.getArgument("aura", ResourceLocation.class);
                    createCarrier(context, context.getSource().getPosition(), BasicAura.AURAS.get(resourceLocation));
                    return 1;
                }))));
    }
    public static void createCarrier(CommandContext<CommandSourceStack> context, Vec3 pPos, BasicAura<? extends Entity, ? extends Entity> aura) {
        BlockPos blockpos = BlockPos.containing(pPos);
        ServerLevel serverlevel = context.getSource().getLevel();
        ArmorStand armorStand = new ArmorStand(serverlevel, blockpos.getX(), blockpos.getY(), blockpos.getZ());
        context.getSource().getLevel().addFreshEntity(armorStand);
        context.getSource().sendSuccess(() -> Component.translatable("commands.summon.success", armorStand.getDisplayName()), true);

        FeatureManager featureManager = FeatureGetter.getUnwrap(armorStand);
        if (featureManager == null) return;
        featureManager.getFeature(AuraCarry.class).ifPresent(auraCarry -> auraCarry.setAura(aura));
    }
}
