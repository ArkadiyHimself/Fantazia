package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.Auras;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.custom_registry.DeferredAura;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.neoforged.bus.api.IEventBus;

public class FTZAuras {
    private FTZAuras() {}

    public static final FantazicRegistries.Auras REGISTER = FantazicRegistries.createAuras(Fantazia.MODID);

    public static final DeferredAura<BasicAura> DEBUG = REGISTER.register("debug", () -> Auras.DEBUG);
    public static final DeferredAura<BasicAura> LEADERSHIP = REGISTER.register("leadership", () -> Auras.LEADERSHIP);
    public static final DeferredAura<BasicAura> TRANQUIL = REGISTER.register("tranquil", () -> Auras.TRANQUIL);
    public static final DeferredAura<BasicAura> DESPAIR = REGISTER.register("despair", () -> Auras.DESPAIR);
    public static final DeferredAura<BasicAura> CORROSIVE = REGISTER.register("corrosive", () -> Auras.CORROSIVE);
    public static final DeferredAura<BasicAura> HELLFIRE = REGISTER.register("hellfire", () -> Auras.HELLFIRE);
    public static final DeferredAura<BasicAura> FROSTBITE = REGISTER.register("frostbite", () -> Auras.FROSTBITE);
    public static final DeferredAura<BasicAura> DIFFRACTION = REGISTER.register("diffraction", () -> Auras.DIFFRACTION);
    public static final DeferredAura<BasicAura> UNCOVER = REGISTER.register("uncover", () -> Auras.UNCOVER);

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
