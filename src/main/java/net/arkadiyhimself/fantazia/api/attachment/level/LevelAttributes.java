package net.arkadiyhimself.fantazia.api.attachment.level;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.*;
import net.arkadiyhimself.fantazia.api.type.entity.IHolderManager;
import net.arkadiyhimself.fantazia.api.type.level.ILevelAttributeHolder;
import net.arkadiyhimself.fantazia.networking.packets.attachment_syncing.LevelAttributesUpdateS2C;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class LevelAttributes implements IHolderManager<ILevelAttributeHolder, Level> {

    public final Map<Class<? extends ILevelAttributeHolder>, ILevelAttributeHolder> holders = Maps.newHashMap();
    private final Level level;

    public LevelAttributes(IAttachmentHolder holder) {
        this.level = holder instanceof Level lvl ? lvl : null;
        provide();
    }

    @Override
    public Level getOwner() {
        return this.level;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        for (ILevelAttributeHolder holder : holders.values()) tag.put(holder.id().toString(), holder.serializeNBT(provider));

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        for (ILevelAttributeHolder holder : holders.values()) if (compoundTag.contains(holder.id().toString())) holder.deserializeNBT(provider, compoundTag.getCompound(holder.id().toString()));

    }

    @Override
    public <I extends ILevelAttributeHolder> void putHolder(Function<Level, I> holder) {
        if (this.level == null) return;
        ILevelAttributeHolder levelAttributeHolder = holder.apply(level);
        if (holders.containsKey(levelAttributeHolder.getClass())) return;
        holders.put(levelAttributeHolder.getClass(), levelAttributeHolder);
    }

    @Override
    public <I extends ILevelAttributeHolder> @Nullable I actualHolder(Class<I> iClass) {
        for (ILevelAttributeHolder iLevelAttributeHolder : holders.values()) if (iClass == iLevelAttributeHolder.getClass()) return iClass.cast(iLevelAttributeHolder);
        return null;
    }

    @Override
    public <I extends ILevelAttributeHolder> Optional<I> optionalHolder(Class<I> iClass) {
        I holder = actualHolder(iClass);
        return holder == null ? Optional.empty() : Optional.of(holder);
    }

    @Override
    public <I extends ILevelAttributeHolder> boolean hasHolder(Class<I> iClass) {
        return holders.containsKey(iClass);
    }

    @Override
    public CompoundTag syncSerialize() {
        CompoundTag tag = new CompoundTag();

        for (ILevelAttributeHolder holder : holders.values()) tag.put(holder.id().toString(), holder.syncSerialize());

        return tag;
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        for (ILevelAttributeHolder holder : holders.values()) if (tag.contains(holder.id().toString())) holder.syncDeserialize(tag.getCompound(holder.id().toString()));
    }

    public void tick() {
        holders.values().forEach(ILevelAttributeHolder::tick);
    }

    public void provide() {
        putHolder(AurasInstancesHolder::new);
        putHolder(DamageSourcesHolder::new);
        putHolder(HealingSourcesHolder::new);
        putHolder(TalentAttributeModifiersHolder::new);
        putHolder(EffectsOnSpawnHolder::new);
    }

    public static void updateTracking(Level level) {
        PacketDistributor.sendToAllPlayers(new LevelAttributesUpdateS2C(LevelAttributesGetter.getUnwrap(level).syncSerialize()));
    }
}
