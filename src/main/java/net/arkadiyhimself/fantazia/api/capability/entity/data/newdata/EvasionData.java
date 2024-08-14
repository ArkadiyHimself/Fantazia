package net.arkadiyhimself.fantazia.api.capability.entity.data.newdata;

import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataHolder;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.library.pseudorandom.PSERANInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class EvasionData extends DataHolder implements ITicking {
    private static final String ID = "evasion:";
    private static final int COOLDOWN = 20;
    private final AttributeInstance evasion;
    private PSERANInstance instance;
    private int iFrames = 0;
    private int cooldown = 0;
    @SuppressWarnings("ConstantConditions")
    public EvasionData(LivingEntity livingEntity) {
        super(livingEntity);
        evasion = livingEntity.getAttribute(FTZAttributes.EVASION);
        assert evasion != null;
        instance = new PSERANInstance(evasion.getValue());
    }
    @Override
    public void tick() {
        updateEvasion();
        if (cooldown > 0) cooldown--;
        if (iFrames > 0) iFrames--;
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        if (iFrames != 0) tag.putInt(ID + "evasionTicks", iFrames);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        this.iFrames = tag.contains(ID + "evasionTicks") ? tag.getInt(ID + "evasionTicks") : 0;
    }

    public boolean tryEvade() {
        if (cooldown > 0) return false;
        boolean flag = instance.performAttempt();
        if (flag) {
            iFrames = 5;
            cooldown = COOLDOWN;
            getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.EVASION, SoundSource.NEUTRAL);
        }
        return flag;
    }
    public void updateEvasion() {
        instance = instance.transform(evasion.getValue());
    }
    public int getIFrames() {
        return iFrames;
    }
}
