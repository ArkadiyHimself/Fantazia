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
    public final DeferredRegister<T> REGISTER;
    protected FTZRegistry(DeferredRegister<T> register) {
        this.REGISTER = register;
        this.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterEvent);
        String reg = this.REGISTER.getRegistryName().getPath();
        Fantazia.LOGGER.info("Fantazia: " + reg.substring(0,1).toUpperCase() + reg.substring(1) + " registry!");
    }
    protected FTZRegistry(ResourceKey<Registry<T>> registry) {
        this.REGISTER = DeferredRegister.create(registry, Fantazia.MODID);
        this.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterEvent);
        String reg = this.REGISTER.getRegistryName().getPath();
        Fantazia.LOGGER.info("Fantazia: " + reg.substring(0,1).toUpperCase() + reg.substring(1) + " registry!");
    }
    protected FTZRegistry(IForgeRegistry<T> registry) {
        this(registry.getRegistryKey());
    }
    protected RegistryObject<T> register(String name, Supplier<? extends T> supplier) {
        return this.REGISTER.register(name, supplier);
    }
    private void onRegisterEvent(RegisterEvent event) {
        if (event.getRegistryKey() == this.REGISTER.getRegistryKey()) this.onRegistry(event);
    }
    protected void onRegistry(RegisterEvent event) {
    }

}
