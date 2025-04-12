package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.entities.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZEntityTypes {
    private FTZEntityTypes() {}
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, Fantazia.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<? extends ThrownHatchet>> HATCHET = REGISTER.register("hatchet", () -> EntityType.Builder.<ThrownHatchet>of(ThrownHatchet::new,
            MobCategory.AMBIENT).sized(0.5f,0.5f).build(Fantazia.res("hatchet").toString())); // finished and implemented
    public static final DeferredHolder<EntityType<?>, EntityType<? extends DashStoneEntity>> DASHSTONE = REGISTER.register("dashstone", () -> EntityType.Builder.of(DashStoneEntity::new,
            MobCategory.AMBIENT).sized(0.25f, 0.25f).build(Fantazia.res("dashstone").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<? extends ShockwaveEntity>> SHOCKWAVE = REGISTER.register("shockwave", () -> EntityType.Builder.<ShockwaveEntity>of(ShockwaveEntity::new,
            MobCategory.AMBIENT).sized(1.25f, 0.25f).build(Fantazia.res("shockwave").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<? extends CustomBoat>> CUSTOM_BOAT = REGISTER.register("custom_boat", () -> EntityType.Builder.<CustomBoat>of(CustomBoat::new,
            MobCategory.MISC).sized(1.375f, 0.5625f).build(Fantazia.res("custom_boat").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<? extends CustomChestBoat>> CUSTOM_CHEST_BOAT = REGISTER.register("custom_chest_boat", () -> EntityType.Builder.<CustomChestBoat>of(CustomChestBoat::new,
            MobCategory.MISC).sized(1.375f, 0.5625f).build(Fantazia.res("custom_chest_boat").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<? extends FantazicPainting>> FANTAZIC_PAINTING = REGISTER.register("fantazic_painting", () -> EntityType.Builder.<FantazicPainting>of(FantazicPainting::new,
            MobCategory.MISC).sized(0.5f,0.5f).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE).build(Fantazia.res("fantazic_painting").toString()));

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
