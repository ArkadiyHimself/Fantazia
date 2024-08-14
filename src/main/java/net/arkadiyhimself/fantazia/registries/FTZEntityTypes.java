package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class FTZEntityTypes extends FTZRegistry<EntityType<?>> {
    @SuppressWarnings("unused")
    private static final FTZEntityTypes INSTANCE = new FTZEntityTypes();
    @ObjectHolder(value = Fantazia.MODID + ":hatchet", registryName = "entity_type")
    public static final EntityType<ThrownHatchet> HATCHET = null; // finished and implemented
    public FTZEntityTypes() {
        super(ForgeRegistries.ENTITY_TYPES);

        this.register("hatchet", () -> EntityType.Builder.<ThrownHatchet>of(ThrownHatchet::new, MobCategory.AMBIENT).sized(0.5f,0.5f).build(Fantazia.res("hatchet").toString()));
    }
}
