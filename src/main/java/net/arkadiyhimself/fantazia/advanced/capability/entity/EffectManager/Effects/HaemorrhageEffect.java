package net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects;

import net.arkadiyhimself.fantazia.advanced.capability.entity.CommonData.AttachCommonData;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectHolder;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.events.custom.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.registry.DamageTypeRegistry;
import net.arkadiyhimself.fantazia.registry.MobEffectRegistry;
import net.arkadiyhimself.fantazia.registry.SoundRegistry;
import net.arkadiyhimself.fantazia.util.interfaces.IDamageReacting;
import net.arkadiyhimself.fantazia.util.interfaces.IHealReacting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class HaemorrhageEffect extends EffectHolder implements IDamageReacting, IHealReacting {
    public static final List<EntityType<? extends LivingEntity>> IMMUNE = new ArrayList<>(){{
        add(EntityType.SKELETON);
        add(EntityType.SKELETON_HORSE);
        add(EntityType.WARDEN);
        add(EntityType.WITHER_SKELETON);
        add(EntityType.SLIME);
        add(EntityType.MAGMA_CUBE);
    }};
    private float toHeal = 0;
    private int soundCD = 0;
    public HaemorrhageEffect(LivingEntity owner) {
        super(owner, MobEffectRegistry.HAEMORRHAGE.get());
    }

    public boolean makeSound() {
        return soundCD < 0;
    }
    public void madeSound() {
        soundCD = 10;
    }
    @Override
    public void tick() {
        super.tick();
        if (soundCD > 0) soundCD--;
    }

    @Override
    public void onHeal(VanillaEventsExtension.AdvancedHealEvent event) {
        toHeal -= event.getAmount();
        if (toHeal <= 0) EffectCleansing.forceCleanse(getOwner(), MobEffectRegistry.HAEMORRHAGE.get());
    }
    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        getOwner().level().playSound(null, getOwner().blockPosition(), SoundRegistry.FLESH_RIPPING.get(), SoundSource.NEUTRAL,0.35f,1f);
        getOwner().hurt(new DamageSource(getOwner().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypeRegistry.BLEEDING)), getOwner().getHealth() * 0.1f);
        toHeal = 4 + 2 * instance.getAmplifier();
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt(ID + "soundCD", soundCD);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        soundCD = tag.contains(ID + "soundCD") ? tag.getInt(ID + "soundCD") : 0;
    }
}
