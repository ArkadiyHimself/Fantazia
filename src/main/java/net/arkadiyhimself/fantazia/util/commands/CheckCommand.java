package net.arkadiyhimself.fantazia.util.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.arkadiyhimself.fantazia.util.library.concept_of_consistency.ConCosInstance;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CheckCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("check")
                .then(Commands.literal("concept_of_consistency")
                        .then(Commands.argument("chance", DoubleArgumentType.doubleArg(0, 1)).executes(CheckCommand::testConCos)
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 4096)).executes(CheckCommand::testConCosWithAmount)))));
    }

    private static int testConCos(CommandContext<CommandSourceStack> context) {
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

    private static int testConCosWithAmount(CommandContext<CommandSourceStack> context) {
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
}
