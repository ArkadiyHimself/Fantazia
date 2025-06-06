package net.arkadiyhimself.fantazia.api.attachment.level.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributeHolder;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.packets.attachment_syncing.IAttachmentSync;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

public class AurasInstancesHolder extends LevelAttributeHolder {

    private final List<AuraInstance> auraInstances = Lists.newArrayList();

    private CompoundTag preserved = new CompoundTag();

    public AurasInstancesHolder(Level level) {
        super(level, Fantazia.res("aura_instances"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        /*
        CompoundTag tag = new CompoundTag();
        if (auraInstances.isEmpty()) return tag;

        ListTag instances = new ListTag();
        for (AuraInstance auraInstance : auraInstances) instances.add(auraInstance.serializeSave());

        tag.put("auras", instances);
        return tag;
         */
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        /*
        auraInstances.clear();

        if (!(getLevel() instanceof ServerLevel serverLevel)) return;
        if (!compoundTag.contains("auras")) return;
        ListTag tags = compoundTag.getList("auras", Tag.TAG_COMPOUND);

        for (int i = 0; i < tags.size(); i++) {
            AuraInstance auraInstance = AuraInstance.deserializeSave(tags.getCompound(i), serverLevel);
            if (auraInstance != null) auraInstances.add(auraInstance);
        }

         */
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        if (auraInstances.isEmpty()) return tag;

        ListTag instances = new ListTag();
        for (AuraInstance auraInstance : auraInstances) instances.add(auraInstance.serializeSync());
        tag.put("auras", instances);

        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        preserved = tag;
    }

    @Override
    public void serverTick() {
        auraInstances.forEach(AuraInstance::tick);
        auraInstances.removeIf(AuraInstance::removed);
    }

    @Override
    public void clientTick() {
        auraInstances.forEach(AuraInstance::tick);
        auraInstances.removeIf(AuraInstance::removed);
        deserialize();
    }

    public List<AuraInstance> getAuraInstances() {
        return new ArrayList<>(auraInstances);
    }

    public void addAuraInstance(AuraInstance instance) {
        if (!(getLevel() instanceof ServerLevel serverLevel) || auraInstances.contains(instance)) return;
        auraInstances.add(instance);
        IAttachmentSync.updateAuraInstances(serverLevel);
    }

    private void deserialize() {
        if (!(getLevel() instanceof ClientLevel clientLevel)) return;
        auraInstances.clear();
        if (!preserved.contains("auras")) return;
        ListTag tags = preserved.getList("auras", Tag.TAG_COMPOUND);

        for (int i = 0; i < tags.size(); i++) {
            AuraInstance auraInstance = AuraInstance.deserializeSync(tags.getCompound(i), clientLevel);
            if (auraInstance != null && !auraInstance.removed()) auraInstances.add(auraInstance);
        }
    }
}
