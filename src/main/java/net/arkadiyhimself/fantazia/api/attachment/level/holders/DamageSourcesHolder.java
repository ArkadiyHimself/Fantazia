package net.arkadiyhimself.fantazia.api.attachment.level.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributeHolder;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;

public class DamageSourcesHolder extends LevelAttributeHolder {
    private final Registry<DamageType> damageTypes;
    private final DamageSource removal;
    private final DamageSource bleeding;
    private final DamageSource frozen;
    private final DamageSource ancientFlame;
    private final DamageSource ancientBurning;
    public DamageSourcesHolder(Level level) {
        super(level, Fantazia.res("damage_sources"));
        this.damageTypes = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);

        this.removal = source(FTZDamageTypes.REMOVAL);
        this.bleeding = source(FTZDamageTypes.BLEEDING);
        this.frozen = source(FTZDamageTypes.FROZEN);
        this.ancientFlame = source(FTZDamageTypes.ANCIENT_FLAME);
        this.ancientBurning = source(FTZDamageTypes.ANCIENT_BURNING);
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
        return this.source(FTZDamageTypes.PARRY, player);
    }
    public DamageSource hatchet(ThrownHatchet hatchet, @Nullable Entity owner) {
        return this.source(FTZDamageTypes.HATCHET, hatchet, owner);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {}


}
