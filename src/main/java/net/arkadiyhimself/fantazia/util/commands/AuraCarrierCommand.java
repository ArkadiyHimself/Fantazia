package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.advanced.capability.entity.AuraCarrier.GetAuraCarrier;
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
        Collection<BasicAura<Entity, Entity>> auras = BasicAura.AURAS.values();
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
    public static void createCarrier(CommandContext<CommandSourceStack> context, Vec3 pPos, BasicAura<Entity, Entity> aura) {
        BlockPos blockpos = BlockPos.containing(pPos);
        ServerLevel serverlevel = context.getSource().getLevel();
        ArmorStand armorStand = new ArmorStand(serverlevel, blockpos.getX(), blockpos.getY(), blockpos.getZ());
        context.getSource().getLevel().addFreshEntity(armorStand);
        context.getSource().sendSuccess(() -> Component.translatable("commands.summon.success", armorStand.getDisplayName()), true);

        GetAuraCarrier.get(armorStand).ifPresent(auraCarrier -> auraCarrier.setAuraInstance(aura));
    }
}
