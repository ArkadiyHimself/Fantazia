package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.ArrayList;
import java.util.List;

public class LayeredBarrierEffect extends EffectHolder implements IDamageReacting {
    public static List<ResourceKey<DamageType>> IGNORED = new ArrayList<>() {{
        add(DamageTypes.CRAMMING);
        add(DamageTypes.DROWN);
        add(DamageTypes.STARVE);
        add(DamageTypes.GENERIC_KILL);
        add(DamageTypes.IN_WALL);
    }};
    private int layers;
    private float color;
    public LayeredBarrierEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.LAYERED_BARRIER);
    }
    public boolean hasBarrier() {
        return layers > 0;
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
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt(ID + "layers", this.layers);
        tag.putFloat(ID + "color", this.color);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        layers = tag.contains(ID + "layers") ? tag.getInt(ID + "layers") : 0;
        color = tag.contains(ID + "color") ? tag.getFloat(ID + "color") : 0;
    }

    @Override
    public void respawn() {
        super.respawn();
        remove();
    }

    @Override
    public void onHit(LivingHurtEvent event) {
        if (!hasBarrier()) return;
        for (ResourceKey<DamageType> resourceKey : IGNORED) if (event.getSource().is(resourceKey)) return;

        color = 1f;
        event.setCanceled(true);
        layers--;
        if (layers <= 0 && getOwner().hasEffect(FTZMobEffects.LAYERED_BARRIER)) EffectCleansing.forceCleanse(getOwner(), FTZMobEffects.LAYERED_BARRIER);
    }
}
