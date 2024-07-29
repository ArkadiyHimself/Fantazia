package net.arkadiyhimself.fantazia.registries;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class FTZCreativeModeTabs extends FTZRegistry<CreativeModeTab> {
    private static final FTZCreativeModeTabs INSTANCE = new FTZCreativeModeTabs();
    public static final CreativeModeTab FTZ_WEAPONS = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.FRAGILE_BLADE)).title(Component.translatable("fantazia.creativetab.weapon").withStyle(ChatFormatting.DARK_PURPLE)).build();
    public static final CreativeModeTab FTZ_MAGIC = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.ENTANGLER)).title(Component.translatable("fantazia.creativetab.magic").withStyle(ChatFormatting.DARK_PURPLE)).build();
    private FTZCreativeModeTabs() {
        super(Registries.CREATIVE_MODE_TAB);
        this.register("magic_tab", () -> FTZ_WEAPONS);
        this.register("weapon_tab", () -> FTZ_MAGIC);
    }
}
