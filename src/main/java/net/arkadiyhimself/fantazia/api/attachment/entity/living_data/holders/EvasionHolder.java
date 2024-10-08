package net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHolder;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.library.pseudorandom.PSERANInstance;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class EvasionHolder extends LivingDataHolder {
    private static final int COOLDOWN = 20;
    private final AttributeInstance evasion;
    private PSERANInstance instance;
    private int iFrames = 0;
    private int cooldown = 0;
    public EvasionHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("evasion"));
        evasion = livingEntity.getAttribute(FTZAttributes.EVASION);
        assert evasion != null;
        instance = new PSERANInstance(evasion.getValue() / 100);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (iFrames > 0) tag.putInt("evasionTicks", iFrames);
        if (cooldown > 0) tag.putInt("cooldown", cooldown);
        tag.put("random", instance.serialize());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        this.iFrames = compoundTag.getInt("evasionTicks");
        this.cooldown = compoundTag.getInt("cooldown");
        this.instance = PSERANInstance.deserialize(compoundTag.getCompound("random"));
    }

    @Override
    public CompoundTag syncSerialize() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("evasionTicks", iFrames);
        return tag;
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        this.iFrames = tag.getInt("evasionTicks");
    }

    @Override
    public void tick() {
        if (cooldown > 0) cooldown--;
        if (iFrames > 0) iFrames--;
    }

    public boolean tryEvade() {
        updateEvasion();
        if (cooldown > 0 || !instance.performAttempt()) return false;

        iFrames = 5;
        cooldown = COOLDOWN;
        getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.EVASION.get(), SoundSource.NEUTRAL);

        return true;
    }
    public void updateEvasion() {
        if (Double.compare(evasion.getValue() / 100f, instance.getSupposedChance()) == 0) return;
        instance = instance.transform(evasion.getValue() / 100);
    }
    public int getIFrames() {
        return iFrames;
    }
}
