package net.arkadiyhimself.fantazia.common.api.attachment.level.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributeHolder;
import net.arkadiyhimself.fantazia.common.entity.BlockFly;
import net.arkadiyhimself.fantazia.common.entity.Shockwave;
import net.arkadiyhimself.fantazia.common.entity.ThrownHatchet;
import net.arkadiyhimself.fantazia.common.entity.magic_projectile.SimpleChasingProjectile;
import net.arkadiyhimself.fantazia.common.entity.skong.Pimpillo;
import net.arkadiyhimself.fantazia.common.entity.skong.ThrownPin;
import net.arkadiyhimself.fantazia.common.registries.FTZDamageTypes;
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
import net.minecraft.world.phys.Vec3;
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
    private final DamageSource electric;
    private final DamageSource bifrost;

    public DamageSourcesHolder(Level level) {
        super(level, Fantazia.location("damage_sources"));
        this.damageTypes = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);

        this.removal = source(FTZDamageTypes.REMOVAL);
        this.bleeding = source(FTZDamageTypes.BLEEDING);
        this.frozen = source(FTZDamageTypes.FROZEN);
        this.ancientFlame = source(FTZDamageTypes.ANCIENT_FLAME);
        this.ancientBurning = source(FTZDamageTypes.ANCIENT_BURNING);
        this.electric = source(FTZDamageTypes.ELECTRIC);
        this.bifrost = source(FTZDamageTypes.BIFROST);
    }

    private DamageSource source(ResourceKey<DamageType> pDamageTypeKey, @Nullable Entity pCausingEntity, @Nullable Entity pDirectEntity, @Nullable Vec3 position) {
        return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey), pCausingEntity, pDirectEntity, position);
    }

    private DamageSource source(ResourceKey<DamageType> pDamageTypeKey, @Nullable Entity pCausingEntity, @Nullable Entity pDirectEntity) {
        return source(pDamageTypeKey, pCausingEntity, pDirectEntity, null);
    }

    private DamageSource source(ResourceKey<DamageType> pDamageTypeKey, @Nullable Entity pEntity) {
        return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey), pEntity);
    }

    private DamageSource source(ResourceKey<DamageType> pDamageTypeKey) {
        return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey));
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

    public DamageSource electric() {
        return electric;
    }

    public DamageSource bifrost() {
        return bifrost;
    }

    public DamageSource parry(Player player) {
        return this.source(FTZDamageTypes.PARRY, player);
    }

    public DamageSource ominousBell(Entity entity) {
        return this.source(FTZDamageTypes.OMINOUS_BELL, entity);
    }

    public DamageSource hatchet(ThrownHatchet hatchet) {
        return this.source(FTZDamageTypes.HATCHET, hatchet, hatchet.getOwner());
    }

    public DamageSource shockWave(Shockwave entity) {
        return this.source(FTZDamageTypes.SHOCKWAVE, entity, entity.getOwner());
    }

    public DamageSource simpleChasingProjectile(SimpleChasingProjectile entity) {
        return this.source(FTZDamageTypes.SIMPLE_CHASING_PROJECTILE, entity, entity.getOwner());
    }

    public DamageSource thrownPin(ThrownPin thrownPin) {
        return this.source(FTZDamageTypes.THROWN_PIN, thrownPin, thrownPin.getOwner());
    }

    public DamageSource pimpillo(Pimpillo pimpillo) {
        return this.source(FTZDamageTypes.PIMPILLO_EXPLOSION, pimpillo, pimpillo.getOwner());
    }

    public DamageSource blockFly(BlockFly blockFly) {
        return this.source(FTZDamageTypes.BLOCK_FLY, blockFly);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {}


}
