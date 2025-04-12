package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class AuraCarrierCommand {
    private AuraCarrierCommand() {}
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("auracarrier").requires(commandSourceStack -> commandSourceStack.hasPermission(2)).then(Commands.argument("aura", ResourceArgument.resource(context, FantazicRegistries.Keys.AURA))
                .executes((AuraCarrierCommand::createAura))));
    }
    private static int createAura(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Holder.Reference<BasicAura<? extends Entity>> basicAuraReference = ResourceArgument.getResource(commandContext, "aura", FantazicRegistries.Keys.AURA);

        BlockPos blockpos = BlockPos.containing(commandContext.getSource().getPosition());
        ServerLevel serverlevel = commandContext.getSource().getLevel();
        ArmorStand armorStand = new ArmorStand(serverlevel, blockpos.getX(), blockpos.getY(), blockpos.getZ());
        serverlevel.addFreshEntity(armorStand);
        commandContext.getSource().sendSuccess(() -> Component.translatable("commands.summon.success", armorStand.getDisplayName()), true);

        armorStand.getData(FTZAttachmentTypes.ARMOR_STAND_COMMAND_AURA).setAura(basicAuraReference.value());
        return 1;
    }
}
