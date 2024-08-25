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
    @SuppressWarnings("ConstantConditions")
    public static final CreativeModeTab FTZ_WEAPONS = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.FRAGILE_BLADE.get())).title(Component.translatable("fantazia.creativetab.weapon").withStyle(ChatFormatting.DARK_PURPLE)).build();
    @SuppressWarnings("ConstantConditions")
    public static final CreativeModeTab FTZ_MAGIC = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.ENTANGLER.get())).title(Component.translatable("fantazia.creativetab.magic").withStyle(ChatFormatting.DARK_PURPLE)).build();
    @SuppressWarnings("ConstantConditions")
    public static final CreativeModeTab FTZ_EXPENDABLE = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.OBSCURE_ESSENCE.get())).title(Component.translatable("fantazia.creativetab.expendable").withStyle(ChatFormatting.DARK_PURPLE)).build();
    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    static  {
        REGISTER.register("magic_tab", () -> FTZ_WEAPONS);
        REGISTER.register("weapon_tab", () -> FTZ_MAGIC);
        REGISTER.register("expendable_tab", () -> FTZ_EXPENDABLE);
    }

}
