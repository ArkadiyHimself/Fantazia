package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.world.inventory.AmplificationMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZMenuTypes {

    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, Fantazia.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AmplificationMenu>> AMPLIFICATION = register("amplification", AmplificationMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String key, MenuType.MenuSupplier<T> factory) {
        return REGISTER.register(key, () -> new MenuType<>(factory, FeatureFlags.VANILLA_SET));
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
