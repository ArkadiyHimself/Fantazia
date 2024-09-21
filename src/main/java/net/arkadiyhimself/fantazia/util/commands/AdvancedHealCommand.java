package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSource;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AdvancedHealCommand {
    private AdvancedHealCommand() {}
    private static final SimpleCommandExceptionType ERROR_INVULNERABLE = new SimpleCommandExceptionType(Component.translatable("commands.advancedheal.invulnerable"));
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher, CommandBuildContext context) {
        commandDispatcher.register(Commands.literal("advancedheal").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.entity()).then(Commands.argument("amount", FloatArgumentType.floatArg(0)).executes(ctx -> {
                    HealingSources sources = LevelCapHelper.getHealingSources(ctx.getSource().getLevel());
                    if (sources == null) return 0;
                    return heal(ctx.getSource(), EntityArgument.getEntity(ctx, "target"), FloatArgumentType.getFloat(ctx, "amount"), sources.generic());
                })
                        .then(Commands.argument("healingType", ResourceArgument.resource(context, FantazicRegistry.Keys.HEALING_TYPE)).executes(ctx -> {
                            HealingSources sources = LevelCapHelper.getHealingSources(ctx.getSource().getLevel());
                            if (sources == null) return 0;
                            return heal(ctx.getSource(), EntityArgument.getEntity(ctx, "target"), FloatArgumentType.getFloat(ctx, "amount"), new HealingSource(ResourceArgument.getResource(ctx, "healingType", FantazicRegistry.Keys.HEALING_TYPE)));
                        })))));
    }
    private static int heal(CommandSourceStack stack, Entity pTarget, float pAmount, HealingSource pSource) throws CommandSyntaxException {
        if (!(pTarget instanceof LivingEntity livingEntity)) return 0;
        if (AdvancedHealing.tryHeal(livingEntity, pSource, pAmount)) {
            stack.sendSuccess(() -> Component.translatable("commands.advancedheal.success", pAmount, pTarget.getDisplayName()), true);
            return 1;
        } else throw ERROR_INVULNERABLE.create();
    }
}
