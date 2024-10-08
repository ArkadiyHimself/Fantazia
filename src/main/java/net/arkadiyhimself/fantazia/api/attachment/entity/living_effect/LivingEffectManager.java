package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.*;
import net.arkadiyhimself.fantazia.api.fantazicevents.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.api.type.entity.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class LivingEffectManager implements IHolderManager<ILivingEffect, LivingEntity> {
    private final HashMap<Class<? extends ILivingEffect>, ILivingEffect> holders = Maps.newHashMap();
    private final LivingEntity livingEntity;
    public LivingEffectManager(IAttachmentHolder holder) {
        this.livingEntity = holder instanceof LivingEntity entity ? entity : null;
        provide();
    }

    @Override
    public LivingEntity getOwner() {
        return this.livingEntity;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (ILivingEffect iLivingEffect : holders.values()) tag.put(iLivingEffect.id().toString(), iLivingEffect.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        for (ILivingEffect iLivingEffect : holders.values()) if (compoundTag.contains(iLivingEffect.id().toString())) iLivingEffect.deserializeNBT(provider, compoundTag.getCompound(iLivingEffect.id().toString()));
    }

    @Override
    public <I extends ILivingEffect> void putHolder(Function<LivingEntity, I> holder) {
        if (livingEntity == null) return;
        ILivingEffect iLivingEffect = holder.apply(livingEntity);
        if (hasHolder(iLivingEffect.getClass())) return;
        holders.put(iLivingEffect.getClass(), iLivingEffect);
    }

    @Override
    public <I extends ILivingEffect> @Nullable I actualHolder(Class<I> iClass) {
        for (ILivingEffect iLivingEffect : holders.values()) if (iClass == iLivingEffect.getClass()) return iClass.cast(iLivingEffect);
        return null;
    }

    @Override
    public <I extends ILivingEffect> Optional<I> optionalHolder(Class<I> iClass) {
        I holder = actualHolder(iClass);
        return holder == null ? Optional.empty() : Optional.of(holder);
    }

    @Override
    public <I extends ILivingEffect> boolean hasHolder(Class<I> iClass) {
        return holders.containsKey(iClass);
    }

    @Override
    public CompoundTag syncSerialize() {
        CompoundTag tag = new CompoundTag();

        for (ILivingEffect holder : holders.values()) tag.put(holder.id().toString(), holder.syncSerialize());

        return tag;
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        for (ILivingEffect holder : holders.values()) if (tag.contains(holder.id().toString())) holder.syncDeserialize(tag.getCompound(holder.id().toString()));
    }

    public void tick() {
        holders.values().forEach(IBasicHolder::tick);
    }

    public void effectAdded(MobEffectInstance instance) {
        for (ILivingEffect iLivingEffect : holders.values()) if (iLivingEffect.getEffect().value() == instance.getEffect().value()) iLivingEffect.added(instance);
    }
    public void effectEnded(Holder<MobEffect> effect) {
        for (ILivingEffect iLivingEffect : holders.values()) if (iLivingEffect.getEffect() == effect) iLivingEffect.ended();
    }

    public void onHit(LivingIncomingDamageEvent event) {
        for (ILivingEffect iLivingEffect : holders.values()) if (iLivingEffect instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Pre event) {
        for (ILivingEffect iLivingEffect : holders.values()) if (iLivingEffect instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Post event) {
        for (ILivingEffect iLivingEffect : holders.values()) if (iLivingEffect instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHeal(VanillaEventsExtension.AdvancedHealEvent event) {
        for (ILivingEffect iLivingEffect : holders.values()) if (iLivingEffect instanceof IHealListener listener) listener.onHeal(event);
    }

    public void provide() {
        putHolder(StunEffect::new);
        putHolder(BarrierEffect::new);
        putHolder(LayeredBarrierEffect::new);
        putHolder(AbsoluteBarrierEffect::new);
        putHolder(FuryEffect::new);
        putHolder(FrozenEffect::new);
        putHolder(DisarmEffect::new);
        putHolder(DoomedEffect::new);
        putHolder(DeafenedEffect::new);
        putHolder(HaemorrhageEffect::new);
        putHolder(CursedMarkEffect::new);
    }
}
