package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.List;

public class InterruptCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("interrupt").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.argument("entities", EntityArgument.entities())
                        .executes(context1 -> interruptEntities(context1, EntityArgument.getEntities(context1, "entities")))));
    }

    private static int interruptEntities(CommandContext<CommandSourceStack> context, Collection<? extends Entity> entities) {
        List<LivingEntity> livingEntities = Lists.newArrayList();
        for (Entity entity : entities)
            if (entity instanceof LivingEntity livingEntity)
                livingEntities.add(livingEntity);

        if (livingEntities.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.translatable("commands.interrupt.failure"), true);
            return 0;
        } else if (livingEntities.size() == 1) {
            LivingEntity livingEntity = livingEntities.getFirst();
            ActionsHelper.interrupt(livingEntity);
            context.getSource().sendSuccess(() -> Component.translatable("commands.interrupt.success.singe", livingEntity.getDisplayName()), true);
        } else {
            for (LivingEntity entity : livingEntities)
                ActionsHelper.interrupt(entity);
            context.getSource().sendSuccess(() -> Component.translatable("commands.interrupt.success", livingEntities.size()), true);
        }

        return 1;
    }
}
