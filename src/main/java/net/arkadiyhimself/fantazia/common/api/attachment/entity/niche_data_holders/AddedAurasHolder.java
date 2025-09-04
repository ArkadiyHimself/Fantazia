package net.arkadiyhimself.fantazia.common.api.attachment.entity.niche_data_holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.common.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.common.api.attachment.IBasicHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public class AddedAurasHolder implements IBasicHolder {

    private final Entity owner;
    private final List<AuraInstance> addedAuras = Lists.newArrayList();

    public AddedAurasHolder(IAttachmentHolder holder) {
        this.owner = holder instanceof Entity entity ? entity : null;
    }

    @Override
    public ResourceLocation id() {
        return Fantazia.location("added_aura");
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag listTag = new ListTag();
        for (AuraInstance auraInstance : addedAuras) listTag.add(auraInstance.serializeSave());
        tag.put("added_auras", listTag);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        if (owner == null) return;
        ListTag listTag = tag.getList("added_auras", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            AuraInstance auraInstance = AuraInstance.deserializeSave(listTag.getCompound(i)).apply(owner);
            if (auraInstance == null) continue;
            if (owner.level() instanceof ServerLevel serverLevel) LevelAttributesHelper.addAuraInstance(serverLevel, auraInstance);
            addedAuras.add(auraInstance);
        }
    }

    @Override
    public CompoundTag serializeInitial() {
        return new CompoundTag();
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
    }

    public void addAura(Holder<Aura> aura, int amplifier) {
        if (owner == null || !(owner.level() instanceof ServerLevel serverLevel)) return;
        for (AuraInstance auraInstance : addedAuras) if (auraInstance.getAura() == aura.value()) return;
        AuraInstance auraInstance = new AuraInstance(owner, aura, amplifier);
        LevelAttributesHelper.addAuraInstance(serverLevel, auraInstance);
        addedAuras.add(auraInstance);
    }
}
