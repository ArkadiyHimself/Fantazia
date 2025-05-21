package net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.api.attachment.IBasicHolder;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Optional;

public class AddedAurasHolder implements IBasicHolder {

    private final Entity owner;
    private final List<AuraInstance> addedAuras = Lists.newArrayList();

    public AddedAurasHolder(IAttachmentHolder holder) {
        this.owner = holder instanceof Entity entity ? entity : null;
    }

    @Override
    public ResourceLocation id() {
        return Fantazia.res("added_aura");
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag listTag = new ListTag();
        for (AuraInstance auraInstance : addedAuras) {
            ResourceLocation auraID = FantazicRegistries.AURAS.getKey(auraInstance.getAura().value());
            if (auraID == null) continue;
            listTag.add(StringTag.valueOf(auraID.toString()));
        }
        tag.put("added_auras", listTag);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        if (owner == null) return;
        addedAuras.clear();
        ListTag listTag = tag.getList("added_auras", Tag.TAG_STRING);
        for (int i = 0; i < listTag.size(); i++) {
            Optional<Holder.Reference<Aura>> basicAura = FantazicRegistries.AURAS.getHolder(ResourceLocation.parse(listTag.getString(i)));
            basicAura.ifPresent(basicAuraReference -> addedAuras.add(new AuraInstance(owner, basicAuraReference.getDelegate())));
        }
    }

    @Override
    public CompoundTag serializeInitial() {
        return new CompoundTag();
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
    }

    public void addAura(Holder<Aura> aura) {
        if (owner == null) return;
        for (AuraInstance auraInstance : addedAuras) if (auraInstance.getAura() == aura.value()) return;
        addedAuras.add(new AuraInstance(owner, aura));
    }
}
