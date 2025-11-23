package net.arkadiyhimself.fantazia.common.registries;

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

    public static final CreativeModeTab ARTIFACTS = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.ENTANGLER.asItem())).title(Component.translatable("creativetab.fantazia.artifacts").withStyle(ChatFormatting.DARK_PURPLE)).build();
    public static final CreativeModeTab EXPENDABLES = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.OBSCURE_SUBSTANCE.asItem())).title(Component.translatable("creativetab.fantazia.expendables").withStyle(ChatFormatting.DARK_PURPLE)).build();
    public static final CreativeModeTab BLOCKS = CreativeModeTab.builder().icon(() -> new ItemStack(FTZItems.OBSCURE_SIGN.asItem())).title(Component.translatable("creativetab.fantazia.blocks").withStyle(ChatFormatting.DARK_PURPLE)).build();

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
    static {
        REGISTER.register("artifacts", () -> ARTIFACTS);
        REGISTER.register("expendables", () -> EXPENDABLES);
        REGISTER.register("blocks", () -> BLOCKS);
    }

}
