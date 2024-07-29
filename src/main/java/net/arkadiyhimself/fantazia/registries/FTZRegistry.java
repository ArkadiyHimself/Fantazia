package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public abstract class FTZRegistry<T> {
    private final DeferredRegister<T> REGISTER;
    protected FTZRegistry(ResourceKey<Registry<T>> registry) {
        this.REGISTER = DeferredRegister.create(registry, Fantazia.MODID);
        this.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterEvent);
    }
    protected FTZRegistry(IForgeRegistry<T> registry) {
        this(registry.getRegistryKey());
    }
    protected RegistryObject<T> register(String name, Supplier<T> supplier) {
        return this.REGISTER.register(name, supplier);
    }
    private void onRegisterEvent(RegisterEvent event) {
        if (event.getRegistryKey() == this.REGISTER.getRegistryKey()) this.onRegistry(event);
    }
    protected void onRegistry(RegisterEvent event) {
    }

}
