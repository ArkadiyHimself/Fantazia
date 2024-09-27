package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.entities.DashStoneEntity;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FTZEntityTypes {
    private FTZEntityTypes() {}
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Fantazia.MODID);
    public static final RegistryObject<EntityType<? extends ThrownHatchet>> HATCHET = REGISTER.register("hatchet", () -> EntityType.Builder.<ThrownHatchet>of(ThrownHatchet::new, MobCategory.AMBIENT).sized(0.5f,0.5f).build(Fantazia.res("hatchet").toString())); // finished and implemented
    public static final RegistryObject<EntityType<? extends DashStoneEntity>> DASHSTONE = REGISTER.register("dashstone", () -> EntityType.Builder.<DashStoneEntity>of(DashStoneEntity::new, MobCategory.AMBIENT).sized(0.25f, 0.25f).build(Fantazia.res("dashstone").toString()));
    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
