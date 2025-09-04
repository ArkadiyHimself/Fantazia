package net.arkadiyhimself.fantazia.common.api.attachment.level;

import net.arkadiyhimself.fantazia.common.api.attachment.IHolderManager;
import net.arkadiyhimself.fantazia.common.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.AurasInstancesHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.EffectsSpawnAppliersHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.HealingSourcesHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class LevelAttributesManager implements IHolderManager<ILevelAttributeHolder, Level> {

    public final List<ILevelAttributeHolder> holders = Lists.newArrayList();
    private final Level level;

    public LevelAttributesManager(IAttachmentHolder holder) {
        this.level = holder instanceof Level lvl ? lvl : null;
        provide();
    }

    @Override
    public Level getEntity() {
        return this.level;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (ILevelAttributeHolder holder : holders) tag.put(holder.id().toString(), holder.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        for (ILevelAttributeHolder holder : holders) if (compoundTag.contains(holder.id().toString())) holder.deserializeNBT(provider, compoundTag.getCompound(holder.id().toString()));

    }

    @Override
    public <I extends ILevelAttributeHolder> void putHolder(Function<Level, I> holder) {
        if (this.level == null) return;
        ILevelAttributeHolder levelAttributeHolder = holder.apply(level);
        if (hasHolder(levelAttributeHolder.getClass())) return;
        holders.add(levelAttributeHolder);
    }

    @Override
    public <I extends ILevelAttributeHolder> @Nullable I actualHolder(Class<I> iClass) {
        for (ILevelAttributeHolder iLevelAttributeHolder : holders) if (iClass == iLevelAttributeHolder.getClass()) return iClass.cast(iLevelAttributeHolder);
        return null;
    }

    @Override
    public <I extends ILevelAttributeHolder> Optional<I> optionalHolder(Class<I> iClass) {
        I holder = actualHolder(iClass);
        return holder == null ? Optional.empty() : Optional.of(holder);
    }

    @Override
    public <I extends ILevelAttributeHolder> boolean hasHolder(Class<I> iClass) {
        return actualHolder(iClass) != null;
    }

    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        for (ILevelAttributeHolder holder : holders) tag.put(holder.id().toString(), holder.serializeInitial());
        return tag;
    }

    public void deserializeInitial(CompoundTag tag) {
        for (ILevelAttributeHolder holder : holders) if (tag.contains(holder.id().toString())) holder.deserializeInitial(tag.getCompound(holder.id().toString()));
    }

    public CompoundTag serializeTick() {
        CompoundTag tag = new CompoundTag();
        for (ILevelAttributeHolder holder : holders) if (holder instanceof ISyncEveryTick syncEveryTick) tag.put(holder.id().toString(), syncEveryTick.serializeTick());
        return tag;
    }

    public void deserializeTick(CompoundTag tag) {
        for (ILevelAttributeHolder holder : holders) if (holder instanceof ISyncEveryTick syncEveryTick) syncEveryTick.deserializeTick(tag.getCompound(holder.id().toString()));
    }

    public void tick() {
        if (level.isClientSide()) holders.forEach(ILevelAttributeHolder::clientTick);
        else holders.forEach(ILevelAttributeHolder::serverTick);
    }

    public void provide() {
        putHolder(AurasInstancesHolder::new);
        putHolder(DamageSourcesHolder::new);
        putHolder(HealingSourcesHolder::new);
        putHolder(EffectsSpawnAppliersHolder::new);
    }
}
