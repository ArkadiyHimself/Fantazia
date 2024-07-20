package net.arkadiyhimself.fantazia.api;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.Entities.HatchetEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityTypeRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =  DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Fantazia.MODID);
    public static final RegistryObject<EntityType<HatchetEntity>> HATCHET = ENTITY_TYPES.register("hatchet",
            () -> EntityType.Builder.<HatchetEntity>of(HatchetEntity::new, MobCategory.AMBIENT)
                    .sized(0.5f,0.5f).build(Fantazia.res("hatchet").toString()));
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
