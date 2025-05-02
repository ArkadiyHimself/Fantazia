package net.arkadiyhimself.fantazia.api.attachment.basis_attachments;

import net.arkadiyhimself.fantazia.packets.attachment_modify.TickingIntegerUpdateS2C;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class TickingIntegerHolder implements INBTSerializable<IntTag> {

    private final IAttachmentHolder holder;
    private final ResourceLocation id;
    private final boolean alwaysSync;

    private int value = 0;

    public TickingIntegerHolder(IAttachmentHolder holder, ResourceLocation id, boolean alwaysSync) {
        this.holder = holder;
        this.alwaysSync = alwaysSync;
        this.id = id;
    }

    @Override
    public @UnknownNullability IntTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return IntTag.valueOf(value);
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull IntTag intTag) {
        this.value = intTag.getAsInt();
    }

    public void tick() {
        if (value > 0) value--;
        if ((alwaysSync || value == 0) && holder instanceof Entity entity && !entity.level().isClientSide()) PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new TickingIntegerUpdateS2C(id, value, entity.getId()));
    }

    public int value() {
        return value;
    }

    public void set(int value) {
        this.value = value;
        if (holder instanceof Entity entity && !entity.level().isClientSide()) PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new TickingIntegerUpdateS2C(id, value, entity.getId()));
    }
}
