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
    private static final int COOLDOWN = 20;
    private final AttributeInstance evasion;
    private PSERANInstance instance;
    private int iFrames = 0;
    private int cooldown = 0;
    public EvasionData(LivingEntity livingEntity) {
        super(livingEntity);
        evasion = livingEntity.getAttribute(FTZAttributes.EVASION.get());
        assert evasion != null;
        instance = new PSERANInstance(evasion.getValue() / 100);
    }
    @Override
    public String ID() {
        return "evasion_data";
    }
    @Override
    public void tick() {
        updateEvasion();
        if (cooldown > 0) cooldown--;
        if (iFrames > 0) iFrames--;
    }
    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        if (iFrames > 0) tag.putInt("evasionTicks", iFrames);
        if (cooldown > 0) tag.putInt("cooldown", cooldown);
        tag.put("random", instance.serialize());
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        this.iFrames = tag.getInt("evasionTicks");
        this.cooldown = tag.getInt("cooldown");
        this.instance = PSERANInstance.deserialize(tag.getCompound("random"));
    }

    public boolean tryEvade() {
        if (cooldown > 0 || !instance.performAttempt()) return false;

        iFrames = 5;
        cooldown = COOLDOWN;
        getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.EVASION.get(), SoundSource.NEUTRAL);

        return true;
    }
    public void updateEvasion() {
        if (Double.compare(evasion.getValue() / 100, instance.getSupposedChance()) == 0) return;
        instance = instance.transform(evasion.getValue() / 100);
    }
    public int getIFrames() {
        return iFrames;
    }
}
