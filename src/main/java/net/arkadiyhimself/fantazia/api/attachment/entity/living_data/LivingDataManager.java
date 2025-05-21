package net.arkadiyhimself.fantazia.api.attachment.entity.living_data;


import net.arkadiyhimself.fantazia.api.attachment.IBasicHolder;
import net.arkadiyhimself.fantazia.api.attachment.IHolderManager;
import net.arkadiyhimself.fantazia.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.DAMHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.StuckHatchetHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class LivingDataManager implements IHolderManager<IBasicHolder, LivingEntity> {

    private final List<IBasicHolder> holders = Lists.newArrayList();
    private final LivingEntity livingEntity;

    public LivingDataManager(IAttachmentHolder holder) {
        this.livingEntity = holder instanceof LivingEntity entity ? entity : null;
        provide();
    }

    @Override
    public LivingEntity getEntity() {
        return livingEntity;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (IBasicHolder iBasicHolder : holders) tag.put(iBasicHolder.id().toString(), iBasicHolder.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        for (IBasicHolder iBasicHolder : holders) if (compoundTag.contains(iBasicHolder.id().toString())) iBasicHolder.deserializeNBT(provider, compoundTag.getCompound(iBasicHolder.id().toString()));
    }

    @Override
    public <I extends IBasicHolder> void putHolder(Function<LivingEntity, I> holder) {
        if (this.livingEntity == null) return;
        IBasicHolder iBasicHolder = holder.apply(livingEntity);
        if (hasHolder(iBasicHolder.getClass())) return;
        holders.add(iBasicHolder);
    }

    @Override
    public <I extends IBasicHolder> @Nullable I actualHolder(Class<I> iClass) {
        for (IBasicHolder iBasicHolder : holders) if (iClass == iBasicHolder.getClass()) return iClass.cast(iBasicHolder);
        return null;
    }

    @Override
    public <I extends IBasicHolder> Optional<I> optionalHolder(Class<I> iClass) {
        I holder = actualHolder(iClass);
        return holder == null ? Optional.empty() : Optional.of(holder);
    }

    @Override
    public <I extends IBasicHolder> boolean hasHolder(Class<I> iClass) {
        return actualHolder(iClass) != null;
    }

    public CompoundTag serializeTick() {
        CompoundTag tag = new CompoundTag();
        for (IBasicHolder holder : holders) if (holder instanceof ISyncEveryTick syncEveryTick) tag.put(holder.id().toString(), syncEveryTick.serializeTick());
        return tag;
    }

    public void deserializeTick(CompoundTag tag) {
        for (IBasicHolder holder : holders) if (holder instanceof ISyncEveryTick syncEveryTick) syncEveryTick.deserializeTick(tag.getCompound(holder.id().toString()));
    }

    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        for (IBasicHolder holder : holders) tag.put(holder.id().toString(), holder.serializeInitial());
        return tag;
    }

    public void deserializeInitial(CompoundTag tag) {
        for (IBasicHolder holder : holders) if (tag.contains(holder.id().toString())) holder.deserializeInitial(tag.getCompound(holder.id().toString()));
    }

    public void tick() {
        if (getEntity().level().isClientSide()) holders.forEach(IBasicHolder::clientTick);
        else holders.forEach(IBasicHolder::serverTick);
    }

    public void onHit(LivingIncomingDamageEvent event) {
        for (IBasicHolder iBasicHolder : holders) if (iBasicHolder instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Pre event) {
        for (IBasicHolder iBasicHolder : holders) if (iBasicHolder instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Post event) {
        for (IBasicHolder iBasicHolder : holders) if (iBasicHolder instanceof IDamageEventListener listener) listener.onHit(event);
    }

    private void provide() {
        putHolder(StuckHatchetHolder::new);
        putHolder(DAMHolder::new);
    }
}
