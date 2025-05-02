package net.arkadiyhimself.fantazia.api.attachment.entity.living_data;


import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.api.attachment.IBasicHolder;
import net.arkadiyhimself.fantazia.api.attachment.IHolderManager;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.DAMHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.StuckHatchetHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class LivingDataManager implements IHolderManager<IBasicHolder, LivingEntity> {

    private final Map<Class<? extends IBasicHolder>, IBasicHolder> holders = Maps.newHashMap();
    private final LivingEntity livingEntity;

    public LivingDataManager(IAttachmentHolder holder) {
        this.livingEntity = holder instanceof LivingEntity entity ? entity : null;
        provide();
    }

    @Override
    public LivingEntity getOwner() {
        return livingEntity;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (IBasicHolder iBasicHolder : holders.values()) tag.put(iBasicHolder.id().toString(), iBasicHolder.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        for (IBasicHolder iBasicHolder : holders.values()) if (compoundTag.contains(iBasicHolder.id().toString())) iBasicHolder.deserializeNBT(provider, compoundTag.getCompound(iBasicHolder.id().toString()));
    }

    @Override
    public <I extends IBasicHolder> void putHolder(Function<LivingEntity, I> holder) {
        if (this.livingEntity == null) return;
        IBasicHolder iBasicHolder = holder.apply(livingEntity);
        if (hasHolder(iBasicHolder.getClass())) return;
        holders.put(iBasicHolder.getClass(), iBasicHolder);
    }

    @Override
    public <I extends IBasicHolder> @Nullable I actualHolder(Class<I> iClass) {
        for (IBasicHolder iBasicHolder : holders.values()) if (iClass == iBasicHolder.getClass()) return iClass.cast(iBasicHolder);
        return null;
    }

    @Override
    public <I extends IBasicHolder> Optional<I> optionalHolder(Class<I> iClass) {
        I holder = actualHolder(iClass);
        return holder == null ? Optional.empty() : Optional.of(holder);
    }

    @Override
    public <I extends IBasicHolder> boolean hasHolder(Class<I> iClass) {
        return holders.containsKey(iClass);
    }

    public CompoundTag syncSerialize() {
        CompoundTag tag = new CompoundTag();
        for (IBasicHolder holder : holders.values()) tag.put(holder.id().toString(), holder.syncSerialize());
        return tag;
    }

    public void syncDeserialize(CompoundTag tag) {
        for (IBasicHolder holder : holders.values()) if (tag.contains(holder.id().toString())) holder.syncDeserialize(tag.getCompound(holder.id().toString()));
    }

    public void tick() {
        if (getOwner().level().isClientSide()) holders.values().forEach(IBasicHolder::clientTick);
        else holders.values().forEach(IBasicHolder::serverTick);
    }

    public void onHit(LivingIncomingDamageEvent event) {
        for (IBasicHolder iBasicHolder : holders.values()) if (iBasicHolder instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Pre event) {
        for (IBasicHolder iBasicHolder : holders.values()) if (iBasicHolder instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Post event) {
        for (IBasicHolder iBasicHolder : holders.values()) if (iBasicHolder instanceof IDamageEventListener listener) listener.onHit(event);
    }

    private void provide() {
        putHolder(StuckHatchetHolder::new);
        putHolder(DAMHolder::new);
    }
}
