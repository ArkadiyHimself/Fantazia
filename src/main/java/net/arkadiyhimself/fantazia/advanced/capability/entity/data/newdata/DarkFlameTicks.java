package net.arkadiyhimself.fantazia.advanced.capability.entity.data.newdata;

import net.arkadiyhimself.fantazia.advanced.capability.entity.data.DataHolder;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.util.interfaces.ITicking;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class DarkFlameTicks extends DataHolder implements ITicking {
    private static final String ID = "dark_flame:";
    private int flameTicks = 0;
    public DarkFlameTicks(LivingEntity livingEntity) {
        super(livingEntity);
    }
    @Override
    public void respawn() {
        flameTicks = 0;
    }

    @Override
    public void tick() {
        flameTicks--;
        if (isBurning()) getEntity().hurt(new DamageSource(getEntity().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(FTZDamageTypes.ANCIENT_BURNING)), 1.5f);
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt(ID + "flameTicks", flameTicks);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        flameTicks = tag.contains(ID + "flameTicks") ? tag.getInt(ID + "flameTicks") : 0;
    }
    public void setFlameTicks(int value) {
        if (flameTicks < value) flameTicks = value;
    }
    public boolean isBurning() {
        return flameTicks > 0;
    }
}
