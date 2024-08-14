package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.Auras;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.registries.FTZRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ObjectHolder;

public class FTZAuras extends FTZRegistry<BasicAura<?,?>> {
    @SuppressWarnings("unused")
    private static final FTZAuras INSTANCE = new FTZAuras();
    @ObjectHolder(value = Fantazia.MODID + ":debug", registryName = Fantazia.MODID + ":aura")
    public static final BasicAura<Entity, Entity> DEBUG = null;
    @ObjectHolder(value = Fantazia.MODID + ":leadership", registryName = Fantazia.MODID + ":aura")
    public static final BasicAura<LivingEntity, LivingEntity> LEADERSHIP = null;
    @ObjectHolder(value = Fantazia.MODID + ":tranquil", registryName = Fantazia.MODID + ":aura")
    public static final BasicAura<LivingEntity, LivingEntity> TRANQUIL = null;
    @ObjectHolder(value = Fantazia.MODID + ":despair", registryName = Fantazia.MODID + ":aura")
    public static final BasicAura<LivingEntity, LivingEntity> DESPAIR = null;
    public FTZAuras() {
        super(FantazicRegistry.AURAS);
        register("debug", () -> Auras.DEBUG);
        register("leadership", () -> Auras.LEADERSHIP);
        register("tranquil", () -> Auras.TRANQUIL);
        register("despair", () -> Auras.DESPAIR);
    }
}
