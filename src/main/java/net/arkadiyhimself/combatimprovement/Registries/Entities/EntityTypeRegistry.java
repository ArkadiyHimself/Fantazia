package net.arkadiyhimself.combatimprovement.Registries.Entities;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityTypeRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =  DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CombatImprovement.MODID);
    public static final RegistryObject<EntityType<HatchetEntity>> HATCHET = ENTITY_TYPES.register("hatchet",
            () -> EntityType.Builder.<HatchetEntity>of(HatchetEntity::new, MobCategory.AMBIENT)
                    .sized(0.3f,0.3f).build(new ResourceLocation(CombatImprovement.MODID, "hatchet").toString()));
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
