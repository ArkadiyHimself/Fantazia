package net.arkadiyhimself.fantazia.api.capability.entity.data.newdata;

import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataHolder;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class LivingData extends DataHolder implements ITicking, IDamageReacting {
    private int damageTicks = 0;
    private float prevHP;
    public LivingData(LivingEntity livingEntity) {
        super(livingEntity);
        prevHP = livingEntity.getHealth();
    }

    @Override
    public String ID() {
        return "living_data";
    }

    @Override
    public void tick() {
        if (damageTicks > 0) damageTicks--;
    }
    @Override
    public void onHit(LivingHurtEvent event) {
        prevHP = getEntity().getHealth();
        if (!event.getSource().is(FTZDamageTypes.REMOVAL)) damageTicks = 100;
    }

    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("damageTicks", damageTicks);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        damageTicks = tag.getInt("damageTicks");
    }

    public int getDamageTicks() {
        return damageTicks;
    }

    public float getPrevHP() {
        return prevHP;
    }
}
