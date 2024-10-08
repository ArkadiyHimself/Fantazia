package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.Auras;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZAuras {
    private FTZAuras() {}
    public static final DeferredRegister<BasicAura<? extends Entity>> REGISTER = DeferredRegister.create(FantazicRegistry.Keys.AURA, Fantazia.MODID);
    public static final DeferredHolder<BasicAura<? extends Entity>, BasicAura<Entity>> DEBUG = REGISTER.register("debug", () -> Auras.DEBUG);
    public static final DeferredHolder<BasicAura<? extends Entity>, BasicAura<LivingEntity>> LEADERSHIP = REGISTER.register("leadership", () -> Auras.LEADERSHIP);
    public static final DeferredHolder<BasicAura<? extends Entity>, BasicAura<LivingEntity>> TRANQUIL = REGISTER.register("tranquil", () -> Auras.TRANQUIL);
    public static final DeferredHolder<BasicAura<? extends Entity>, BasicAura<LivingEntity>> DESPAIR = REGISTER.register("despair", () -> Auras.DESPAIR);
    public static final DeferredHolder<BasicAura<? extends Entity>, BasicAura<Monster>> CORROSIVE = REGISTER.register("corrosive", () -> Auras.CORROSIVE);
    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
