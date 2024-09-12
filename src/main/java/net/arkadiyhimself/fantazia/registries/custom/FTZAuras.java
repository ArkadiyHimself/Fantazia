package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.advanced.aura.Auras;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.minecraftforge.registries.RegistryObject;

public class FTZAuras {
    private FTZAuras() {}
    public static final RegistryObject<BasicAura<?>> DEBUG = FantazicRegistry.AURAS.register("debug", () -> Auras.DEBUG);
    public static final RegistryObject<BasicAura<?>> LEADERSHIP = FantazicRegistry.AURAS.register("leadership", () -> Auras.LEADERSHIP);
    public static final RegistryObject<BasicAura<?>> TRANQUIL = FantazicRegistry.AURAS.register("tranquil", () -> Auras.TRANQUIL);
    public static final RegistryObject<BasicAura<?>> DESPAIR = FantazicRegistry.AURAS.register("despair", () -> Auras.DESPAIR);
    public static final RegistryObject<BasicAura<?>> CORROSIVE = FantazicRegistry.AURAS.register("corrosive", () -> Auras.CORROSIVE);
}
