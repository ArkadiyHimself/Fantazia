package net.arkadiyhimself.fantazia.api.type.entity;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IHolderManager<T extends INBTSerializable<CompoundTag>, M extends IAttachmentHolder> extends INBTSerializable<CompoundTag> {
    M getOwner();
    <I extends T> void putHolder(Function<M, I> holder);
    <I extends T> @Nullable I actualHolder(Class<I> iClass);
    <I extends T> Optional<I> optionalHolder(Class<I> iClass);
    <I extends T> boolean hasHolder(Class<I> iClass);
    CompoundTag syncSerialize();
    void syncDeserialize(CompoundTag tag);
}
