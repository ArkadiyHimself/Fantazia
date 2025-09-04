package net.arkadiyhimself.fantazia.common.entity;

import net.arkadiyhimself.fantazia.common.registries.FTZEntityTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import net.neoforged.fml.common.asm.enumextension.NamedEnum;
import net.neoforged.fml.common.asm.enumextension.NetworkedEnum;
import net.neoforged.fml.common.asm.enumextension.ReservedConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public class CustomBoat extends Boat {

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(CustomBoat.class, EntityDataSerializers.INT);

    public CustomBoat(EntityType<? extends CustomBoat> entityType, Level level) {
        super(entityType, level);
    }

    public CustomBoat(Level level, double pX, double pY, double pZ) {
        this(FTZEntityTypes.CUSTOM_BOAT.get(), level);
        this.setPos(pX, pY, pZ);
        this.xo = pX;
        this.yo = pY;
        this.zo = pZ;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_TYPE, Type.OBSCURE.ordinal());
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Type", this.getCustomVariant().getSerializedName());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Type", 8)) this.setCustomVariant(Type.byName(compound.getString("Type")));
    }

    @Override
    public @NotNull Item getDropItem() {
        return getCustomVariant().getBoatItem();
    }

    public @NotNull Type getCustomVariant() {
        return Type.byId(this.entityData.get(DATA_ID_TYPE));
    }

    public void setCustomVariant(Type type) {
        this.entityData.set(DATA_ID_TYPE, type.ordinal());
    }

    @NetworkedEnum(NetworkedEnum.NetworkCheck.CLIENTBOUND)
    @NamedEnum(1)
    public enum Type implements StringRepresentable, IExtensibleEnum {
        OBSCURE("obscure", FTZItems.OBSCURE_BOAT, FTZItems.OBSCURE_BOAT);

        private final String name;
        private final Holder<Item> boatItem;
        private final Holder<Item> chestBoatItem;

        public static final StringRepresentable.EnumCodec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private static final IntFunction<Type> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        @ReservedConstructor
        Type(String name, Holder<Item> boatItem, Holder<Item> chestBoatItem) {
            this.name = name;
            this.boatItem = boatItem;
            this.chestBoatItem = chestBoatItem;
        }

        public Item getBoatItem() {
            return this.boatItem.value();
        }

        public Item getChestBoatItem() {
            return this.chestBoatItem.value();
        }

        public @NotNull String getSerializedName() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public static Type byId(int id) {
            return BY_ID.apply(id);
        }

        public static Type byName(String name) {
            return CODEC.byName(name, OBSCURE);
        }
    }
}
