package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.api.attachment.IBasicHolder;
import net.arkadiyhimself.fantazia.api.attachment.IHolderManager;
import net.arkadiyhimself.fantazia.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.IHealEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.*;
import net.arkadiyhimself.fantazia.api.custom_events.VanillaEventsExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class LivingEffectManager implements IHolderManager<ILivingEffectHolder, LivingEntity> {

    private final HashMap<Class<? extends ILivingEffectHolder>, ILivingEffectHolder> holders = Maps.newHashMap();
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
        for (ILivingEffectHolder iLivingEffectHolder : holders.values()) tag.put(iLivingEffectHolder.id().toString(), iLivingEffectHolder.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        for (ILivingEffectHolder iLivingEffectHolder : holders.values()) if (compoundTag.contains(iLivingEffectHolder.id().toString())) iLivingEffectHolder.deserializeNBT(provider, compoundTag.getCompound(iLivingEffectHolder.id().toString()));
    }

    @Override
    public <I extends ILivingEffectHolder> void putHolder(Function<LivingEntity, I> holder) {
        if (livingEntity == null) return;
        ILivingEffectHolder iLivingEffectHolder = holder.apply(livingEntity);
        if (hasHolder(iLivingEffectHolder.getClass())) return;
        holders.put(iLivingEffectHolder.getClass(), iLivingEffectHolder);
    }

    @Override
    public <I extends ILivingEffectHolder> @Nullable I actualHolder(Class<I> iClass) {
        for (ILivingEffectHolder iLivingEffectHolder : holders.values()) if (iClass == iLivingEffectHolder.getClass()) return iClass.cast(iLivingEffectHolder);
        return null;
    }

    @Override
    public <I extends ILivingEffectHolder> Optional<I> optionalHolder(Class<I> iClass) {
        I holder = actualHolder(iClass);
        return holder == null ? Optional.empty() : Optional.of(holder);
    }

    @Override
    public <I extends ILivingEffectHolder> boolean hasHolder(Class<I> iClass) {
        return holders.containsKey(iClass);
    }

    public CompoundTag serializeTick() {
        CompoundTag tag = new CompoundTag();

        for (ILivingEffectHolder holder : holders.values()) if (holder instanceof ISyncEveryTick syncEveryTick) tag.put(holder.id().toString(), syncEveryTick.serializeTick());

        return tag;
    }

    public void deserializeTick(CompoundTag tag) {
        for (ILivingEffectHolder holder : holders.values()) if (holder instanceof ISyncEveryTick syncEveryTick) syncEveryTick.deserializeTick(tag.getCompound(holder.id().toString()));
    }

    public CompoundTag syncSerialize() {
        CompoundTag tag = new CompoundTag();

        for (ILivingEffectHolder holder : holders.values()) tag.put(holder.id().toString(), holder.syncSerialize());

        return tag;
    }

    public void syncDeserialize(CompoundTag tag) {
        for (ILivingEffectHolder holder : holders.values()) if (tag.contains(holder.id().toString())) holder.syncDeserialize(tag.getCompound(holder.id().toString()));
    }


    public void tick() {
        if (getOwner().level().isClientSide()) holders.values().forEach(IBasicHolder::clientTick);
        else holders.values().forEach(IBasicHolder::serverTick);
    }

    public void effectAdded(MobEffectInstance instance) {
        for (ILivingEffectHolder iLivingEffectHolder : holders.values()) iLivingEffectHolder.added(instance);
    }
    public void effectEnded(MobEffect effect) {
        for (ILivingEffectHolder iLivingEffectHolder : holders.values()) iLivingEffectHolder.ended(effect);
    }

    public void onHit(LivingIncomingDamageEvent event) {
        for (ILivingEffectHolder iLivingEffectHolder : holders.values()) if (iLivingEffectHolder instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Pre event) {
        for (ILivingEffectHolder iLivingEffectHolder : holders.values()) if (iLivingEffectHolder instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Post event) {
        for (ILivingEffectHolder iLivingEffectHolder : holders.values()) if (iLivingEffectHolder instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHeal(VanillaEventsExtension.AdvancedHealEvent event) {
        for (ILivingEffectHolder iLivingEffectHolder : holders.values()) if (iLivingEffectHolder instanceof IHealEventListener listener) listener.onHeal(event);
    }

    private void provide() {
        putHolder(BarrierEffectHolder::new);
        putHolder(LayeredBarrierEffectHolder::new);
        putHolder(MobEffectDurationSyncHolder::new);
        putHolder(PuppeteeredEffectHolder::new);
        putHolder(SimpleMobEffectHolderSyncHolder::new);
        putHolder(StunEffectHolder::new);
    }
}
