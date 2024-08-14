package net.arkadiyhimself.fantazia.registries;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class FTZCreativeModeTabs extends FTZRegistry<CreativeModeTab> {
    @SuppressWarnings("unused")
    private static final FTZCreativeModeTabs INSTANCE = new FTZCreativeModeTabs();
    @SuppressWarnings("ConstantConditions")
    public static final CreativeModeTab FTZ_WEAPONS = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.FRAGILE_BLADE)).title(Component.translatable("fantazia.creativetab.weapon").withStyle(ChatFormatting.DARK_PURPLE)).build();
    @SuppressWarnings("ConstantConditions")
    public static final CreativeModeTab FTZ_MAGIC = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.ENTANGLER)).title(Component.translatable("fantazia.creativetab.magic").withStyle(ChatFormatting.DARK_PURPLE)).build();
    @SuppressWarnings("ConstantConditions")
    public static final CreativeModeTab FTZ_EXPENDABLE = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.OBSCURE_ESSENCE)).title(Component.translatable("fantazia.creativetab.expendable").withStyle(ChatFormatting.DARK_PURPLE)).build();

    private FTZCreativeModeTabs() {
        super(Registries.CREATIVE_MODE_TAB);
        this.register("magic_tab", () -> FTZ_WEAPONS);
        this.register("weapon_tab", () -> FTZ_MAGIC);
        this.register("expendable_tab", () -> FTZ_EXPENDABLE);
    }
}
