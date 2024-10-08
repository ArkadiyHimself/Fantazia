package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.entities.DashStoneEntity;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZEntityTypes {
    private FTZEntityTypes() {}
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, Fantazia.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<? extends ThrownHatchet>> HATCHET = REGISTER.register("hatchet", () -> EntityType.Builder.<ThrownHatchet>of(ThrownHatchet::new, MobCategory.AMBIENT).sized(0.5f,0.5f).build(Fantazia.res("hatchet").toString())); // finished and implemented
    public static final DeferredHolder<EntityType<?>, EntityType<? extends DashStoneEntity>> DASHSTONE = REGISTER.register("dashstone", () -> EntityType.Builder.of(DashStoneEntity::new, MobCategory.AMBIENT).sized(0.25f, 0.25f).build(Fantazia.res("dashstone").toString()));
    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
