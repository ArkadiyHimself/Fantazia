package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.Auras;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.custom_registry.DeferredAura;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.bus.api.IEventBus;

public class FTZAuras {
    private FTZAuras() {}

    public static final FantazicRegistries.Auras REGISTER = FantazicRegistries.createAuras(Fantazia.MODID);

    public static final DeferredAura<BasicAura<LivingEntity>> DEBUG = REGISTER.register("debug", () -> Auras.DEBUG);
    public static final DeferredAura<BasicAura<LivingEntity>> LEADERSHIP = REGISTER.register("leadership", () -> Auras.LEADERSHIP);
    public static final DeferredAura<BasicAura<LivingEntity>> TRANQUIL = REGISTER.register("tranquil", () -> Auras.TRANQUIL);
    public static final DeferredAura<BasicAura<Monster>> DESPAIR = REGISTER.register("despair", () -> Auras.DESPAIR);
    public static final DeferredAura<BasicAura<Monster>> CORROSIVE = REGISTER.register("corrosive", () -> Auras.CORROSIVE);
    public static final DeferredAura<BasicAura<LivingEntity>> HELLFIRE = REGISTER.register("hellfire", () -> Auras.HELLFIRE);
    public static final DeferredAura<BasicAura<Mob>> FROSTBITE = REGISTER.register("frostbite", () -> Auras.FROSTBITE);
    public static final DeferredAura<BasicAura<Monster>> DIFFRACTION = REGISTER.register("diffraction", () -> Auras.DIFFRACTION);
    public static final DeferredAura<BasicAura<LivingEntity>> UNCOVER = REGISTER.register("uncover", () -> Auras.UNCOVER);

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
