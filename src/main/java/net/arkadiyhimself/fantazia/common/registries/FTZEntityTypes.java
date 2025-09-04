package net.arkadiyhimself.fantazia.common.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.entity.*;
import net.arkadiyhimself.fantazia.common.entity.magic_projectile.SimpleChasingProjectile;
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
            MobCategory.AMBIENT).sized(0.5f,0.5f).build(Fantazia.location("hatchet").toString())); // finished and implemented
    public static final DeferredHolder<EntityType<?>, EntityType<? extends DashStone>> DASHSTONE = REGISTER.register("dashstone", () -> EntityType.Builder.of(DashStone::new,
            MobCategory.AMBIENT).sized(0.25f, 0.25f).build(Fantazia.location("dashstone").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<? extends Shockwave>> SHOCKWAVE = REGISTER.register("shockwave", () -> EntityType.Builder.<Shockwave>of(Shockwave::new,
            MobCategory.AMBIENT).sized(1.25f, 0.25f).build(Fantazia.location("shockwave").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<? extends CustomBoat>> CUSTOM_BOAT = REGISTER.register("custom_boat", () -> EntityType.Builder.<CustomBoat>of(CustomBoat::new,
            MobCategory.MISC).sized(1.375f, 0.5625f).build(Fantazia.location("custom_boat").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<? extends CustomChestBoat>> CUSTOM_CHEST_BOAT = REGISTER.register("custom_chest_boat", () -> EntityType.Builder.<CustomChestBoat>of(CustomChestBoat::new,
            MobCategory.MISC).sized(1.375f, 0.5625f).build(Fantazia.location("custom_chest_boat").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<? extends FantazicPainting>> FANTAZIC_PAINTING = REGISTER.register("fantazic_painting", () -> EntityType.Builder.<FantazicPainting>of(FantazicPainting::new,
            MobCategory.MISC).sized(0.5f,0.5f).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE).build(Fantazia.location("fantazic_painting").toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<? extends SimpleChasingProjectile>> SIMPLE_CHASING_PROJECTILE = REGISTER.register("simple_chasing_projectile", () -> EntityType.Builder.<SimpleChasingProjectile>of(SimpleChasingProjectile::new,
            MobCategory.MISC).sized(0.25f, 0.25f).build(Fantazia.location("simple_chasing_projectile").toString()));

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
