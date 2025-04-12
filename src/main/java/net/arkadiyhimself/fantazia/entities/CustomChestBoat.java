package net.arkadiyhimself.fantazia.entities;

import net.arkadiyhimself.fantazia.registries.FTZEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CustomChestBoat extends ChestBoat {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(CustomChestBoat.class, EntityDataSerializers.INT);

    public CustomChestBoat(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    public CustomChestBoat(Level level, double pX, double pY, double pZ) {
        this(FTZEntityTypes.CUSTOM_CHEST_BOAT.get(), level);
        this.setPos(pX, pY, pZ);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_TYPE, CustomBoat.Type.OBSCURE.ordinal());
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Type", this.getCustomVariant().getSerializedName());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Type", 8)) this.setCustomVariant(CustomBoat.Type.byName(compound.getString("Type")));
    }

    @Override
    public @NotNull Item getDropItem() {
        return getCustomVariant().getChestBoatItem();
    }

    public @NotNull CustomBoat.Type getCustomVariant() {
        return CustomBoat.Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    public void setCustomVariant(CustomBoat.Type type) {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());
    }
}
