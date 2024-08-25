package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class FTZDamageTypes {
    public static final ResourceKey<DamageType> REMOVAL = register("removal"); // implemented
    public static final ResourceKey<DamageType> BLEEDING = register("bleeding"); // implemented
    public static final ResourceKey<DamageType> FROZEN = register("frozen"); // implemented
    public static final ResourceKey<DamageType> ANCIENT_FLAME = register("ancient_flame"); // implemented
    public static final ResourceKey<DamageType> ANCIENT_BURNING = register("ancient_burning"); // implemented
    public static final ResourceKey<DamageType> PARRY = register("parry"); // implemented
    public static final ResourceKey<DamageType> HATCHET = register("hatchet"); // implemented
    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Fantazia.res(name));
    }
    public static final class DamageSources {
        private final Registry<DamageType> damageTypes;
        private final DamageSource removal;
        private final DamageSource bleeding;
        private final DamageSource frozen;
        private final DamageSource ancientFlame;
        private final DamageSource ancientBurning;
        public DamageSources(RegistryAccess access) {
            this.damageTypes = access.registryOrThrow(Registries.DAMAGE_TYPE);

            this.removal = source(REMOVAL);
            this.bleeding = source(BLEEDING);
            this.frozen = source(FROZEN);
            this.ancientFlame = source(ANCIENT_FLAME);
            this.ancientBurning = source(ANCIENT_BURNING);
        }
        private DamageSource source(ResourceKey<DamageType> pDamageTypeKey) {
            return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey));
        }
        private DamageSource source(ResourceKey<DamageType> pDamageTypeKey, @Nullable Entity pEntity) {
            return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey), pEntity);
        }
        private DamageSource source(ResourceKey<DamageType> pDamageTypeKey, @Nullable Entity pCausingEntity, @Nullable Entity pDirectEntity) {
            return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey), pCausingEntity, pDirectEntity);
        }

        public DamageSource removal() {
            return removal;
        }
        public DamageSource bleeding() {
            return bleeding;
        }
        public DamageSource frozen() {
            return frozen;
        }
        public DamageSource ancientFlame() {
            return ancientFlame;
        }
        public DamageSource ancientBurning() {
            return ancientBurning;
        }

        public DamageSource parry(Player player) {
            return this.source(PARRY, player);
        }
        public DamageSource hatchet(ThrownHatchet hatchet, @Nullable Entity owner) {
            return this.source(HATCHET, hatchet, owner);
        }
    }
}
