package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZCreativeModeTabs {
    private FTZCreativeModeTabs() {}
    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Fantazia.MODID);
    public static final CreativeModeTab WEAPONS = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.FRAGILE_BLADE.get())).title(Component.translatable("fantazia.creativetab.weapons").withStyle(ChatFormatting.DARK_PURPLE)).build();
    public static final CreativeModeTab ARTIFACTS = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.ENTANGLER.get())).title(Component.translatable("fantazia.creativetab.artifacts").withStyle(ChatFormatting.DARK_PURPLE)).build();
    public static final CreativeModeTab EXPENDABLES = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.OBSCURE_SUBSTANCE.get())).title(Component.translatable("fantazia.creativetab.expendables").withStyle(ChatFormatting.DARK_PURPLE)).build();
    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
    static {
        REGISTER.register("weapons", () -> WEAPONS);
        REGISTER.register("artifacts", () -> ARTIFACTS);
        REGISTER.register("expendables", () -> EXPENDABLES);
    }

}
