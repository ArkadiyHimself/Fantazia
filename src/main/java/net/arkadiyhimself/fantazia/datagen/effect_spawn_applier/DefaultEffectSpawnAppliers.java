package net.arkadiyhimself.fantazia.datagen.effect_spawn_applier;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.spawn_effect.CombinedSpawnEffects;
import net.arkadiyhimself.fantazia.data.spawn_effect.EffectSpawnApplier;
import net.arkadiyhimself.fantazia.datagen.SubProvider;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.custom.Auras;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;

import java.util.function.Consumer;

public class DefaultEffectSpawnAppliers implements SubProvider<EffectSpawnApplierHolder> {

    public static DefaultEffectSpawnAppliers create() {
        return new DefaultEffectSpawnAppliers();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<EffectSpawnApplierHolder> consumer) {
        EffectSpawnApplier.builder(false)
                .addEntityTypes(EntityType.BLAZE)
                .addEffects(CombinedSpawnEffects.builder(0.3)
                        .addAuraInstance(Auras.HELLFIRE, 0)
                )
                .save(consumer, entityId(EntityType.BLAZE));

        EffectSpawnApplier.builder(false)
                .addEntityTypes(EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.SPIDER, EntityType.SKELETON, EntityType.STRAY)
                .addEffects(CombinedSpawnEffects.builder(0.2)
                        .addMobEffectInstance(FTZMobEffects.BARRIER,10)
                        .addMobEffectInstance(FTZMobEffects.MIGHT,4))
                .addEffects(CombinedSpawnEffects.builder(0.2)
                        .addMobEffectInstance(FTZMobEffects.DEFLECT, 0))
                .save(consumer, Fantazia.res("common_foes"));

        EffectSpawnApplier.builder(false)
                .addEntityTypes(EntityType.CREEPER)
                .addEffects(CombinedSpawnEffects.builder(0.15)
                        .addMobEffectInstance(FTZMobEffects.LAYERED_BARRIER, 4))
                .save(consumer, entityId(EntityType.CREEPER));

        EffectSpawnApplier.builder(false)
                .addEntityTypes(EntityType.WITCH)
                .addEffects(CombinedSpawnEffects.builder(0.15)
                        .addAuraInstance(Auras.CORROSIVE))
                .save(consumer, entityId(EntityType.WITCH));

        EffectSpawnApplier.builder(false)
                .addEntityTypes(EntityType.SNOW_GOLEM)
                .addEffects(CombinedSpawnEffects.builder(1)
                        .addAuraInstance(Auras.FROSTBITE, 2))
                .save(consumer, entityId(EntityType.SNOW_GOLEM));

        EffectSpawnApplier.builder(false)
                .addEntityTypes(EntityType.ENDERMAN)
                .addEffects(CombinedSpawnEffects.builder(0.13)
                        .addAuraInstance(Auras.DESPAIR)
                        .addMobEffectInstance(MobEffects.REGENERATION,2))
                .save(consumer, entityId(EntityType.ENDERMAN));
    }

    private ResourceLocation entityId(EntityType<?> entityType) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        return Fantazia.changeNamespace(id);
    }
}
