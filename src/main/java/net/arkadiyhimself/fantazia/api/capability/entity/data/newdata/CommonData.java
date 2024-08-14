package net.arkadiyhimself.fantazia.api.capability.entity.data.newdata;

import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataHolder;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class CommonData extends DataHolder implements ITicking, IDamageReacting {
    private static final String ID = "common_data:";
    private int damageTicks = 0;
    private float prevHP;
    public CommonData(LivingEntity livingEntity) {
        super(livingEntity);
        prevHP = livingEntity.getHealth();
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
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt(ID + "damageTicks", damageTicks);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        damageTicks = tag.contains(ID + "damageTicks") ? tag.getInt(ID + "damageTicks") : 0;
    }

    public int getDamageTicks() {
        return damageTicks;
    }

    public float getPrevHP() {
        return prevHP;
    }
}
