package net.arkadiyhimself.fantazia.api.capability.entity.data.newdata;

import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataHolder;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class DarkFlameTicks extends DataHolder implements ITicking {
    private int flameTicks = 0;
    public DarkFlameTicks(LivingEntity livingEntity) {
        super(livingEntity);
    }
    @Override
    public String ID() {
        return "dark_flame";
    }
    @Override
    public void respawn() {
        flameTicks = 0;
    }
    @Override
    public void tick() {
        if (flameTicks > 0) flameTicks--;
        FTZDamageTypes.DamageSources sources = LevelCapHelper.getDamageSources(getEntity().level());
        if (sources != null && isBurning()) getEntity().hurt(sources.ancientBurning(), 1.5f);
    }

    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("flameTicks", flameTicks);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        flameTicks = tag.getInt("flameTicks");
    }
    public void setFlameTicks(int value) {
        if (flameTicks < value) flameTicks = value;
    }
    public boolean isBurning() {
        return flameTicks > 0;
    }
}
