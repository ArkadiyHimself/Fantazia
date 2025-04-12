package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class LayeredBarrierEffect extends LivingEffectHolder implements IDamageEventListener {

    private int layers;
    private float color;

    public LayeredBarrierEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("layered_barrier_effect"), FTZMobEffects.LAYERED_BARRIER);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putInt("layers", this.layers);
        tag.putFloat("color", this.color);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        super.deserializeNBT(provider, compoundTag);
        layers = compoundTag.getInt("layers");
        color = compoundTag.getFloat("color");
    }

    @Override
    public CompoundTag syncSerialize() {
        return serializeNBT(getEntity().registryAccess());
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        deserializeNBT(getEntity().registryAccess(), tag);
    }

    @Override
    public void tick() {
        super.tick();
        color = Math.max(0, color - 0.2f);
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        layers = instance.getAmplifier() + 1;
    }

    @Override
    public void ended() {
        super.ended();
        layers = 0;
    }

    @Override
    public void onHit(LivingIncomingDamageEvent event) {
        if (!hasBarrier() || event.getSource().is(FTZDamageTypeTags.PIERCES_BARRIER) || getEntity().hasEffect(FTZMobEffects.ABSOLUTE_BARRIER)) return;

        color = 1f;
        event.setCanceled(true);
        layers--;
        if (layers <= 0 && getEntity().hasEffect(FTZMobEffects.LAYERED_BARRIER)) EffectCleansing.forceCleanse(getEntity(), FTZMobEffects.LAYERED_BARRIER);
    }

    public boolean hasBarrier() {
        return layers > 0 && duration() > 0;
    }

    public void remove() {
        layers = 0;
    }

    public int getLayers() {
        return layers;
    }

    public float getColor() {
        return color;
    }
}
