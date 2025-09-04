package net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.LivingDataHolder;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.library.concept_of_consistency.ConCosInstance;
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
    private ConCosInstance instance;
    private int iFrames = 0;
    private int cooldown = 0;

    public EvasionHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.location("evasion"));
        this.evasion = livingEntity.getAttribute(FTZAttributes.EVASION);
        assert evasion != null;
        this.instance = new ConCosInstance(evasion.getValue() / 100);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("evasionTicks", iFrames);
        tag.putInt("cooldown", cooldown);
        tag.put("random", instance.serialize());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        this.iFrames = compoundTag.getInt("evasionTicks");
        this.cooldown = compoundTag.getInt("cooldown");
        this.instance = ConCosInstance.deserialize(compoundTag.getCompound("random"));
    }

    @Override
    public void serverTick() {
        if (cooldown > 0) cooldown--;
        if (iFrames > 0) iFrames--;
    }

    @Override
    public void clientTick() {
        if (iFrames > 0) iFrames--;
    }

    public boolean tryEvade() {
        updateEvasion();
        if (getEntity().hasEffect(FTZMobEffects.CHAINED) || cooldown > 0 || !instance.performAttempt()) return false;

        success();
        getEntity().level().playSound(null, getEntity().blockPosition(), FTZSoundEvents.ENTITY_EVADE.get(), SoundSource.NEUTRAL);

        return true;
    }

    public void updateEvasion() {
        if (Double.compare(evasion.getValue() / 100f, instance.getSupposedChance()) == 0) return;
        instance = instance.transform(evasion.getValue() / 100);
    }

    public int getIFrames() {
        return iFrames;
    }

    public void success() {
        this.iFrames = 5;
        this.cooldown = COOLDOWN;
        if (!getEntity().level().isClientSide()) IPacket.successfulEvasion(getEntity());
    }
}
