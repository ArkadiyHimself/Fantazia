package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

public class FTZCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Fantazia.MODID);
    public static final CreativeModeTab WEAPONS = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.FRAGILE_BLADE.get())).title(Component.translatable("fantazia.creativetab.weapons").withStyle(ChatFormatting.DARK_PURPLE)).build();
    public static final CreativeModeTab ARTIFACTS = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.ENTANGLER.get())).title(Component.translatable("fantazia.creativetab.artifacts").withStyle(ChatFormatting.DARK_PURPLE)).build();
    public static final CreativeModeTab EXPENDABLES = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.OBSCURE_ESSENCE.get())).title(Component.translatable("fantazia.creativetab.expendables").withStyle(ChatFormatting.DARK_PURPLE)).build();
    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    static {
        REGISTER.register("weapons", () -> WEAPONS);
        REGISTER.register("artifacts", () -> ARTIFACTS);
        REGISTER.register("expendables", () -> EXPENDABLES);
    }

}
