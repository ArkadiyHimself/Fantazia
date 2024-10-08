package net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.type.entity.IBasicHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public class ArmorStandCommandAuraHolder implements IBasicHolder {
    private final ArmorStand armorStand;
    private @Nullable AuraInstance<? extends Entity> auraInstance = null;

    public ArmorStandCommandAuraHolder(IAttachmentHolder iAttachmentHolder) {
        this.armorStand = iAttachmentHolder instanceof ArmorStand entity ? entity : null;
    }

    @Override
    public ResourceLocation id() {
        return Fantazia.res("armor_stand_command_aura");
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (auraInstance == null || auraInstance.getAura().getID() == null) return tag;
        tag.putString("aura", auraInstance.getAura().getID().toString());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        if (!compoundTag.contains("aura") || armorStand == null) return;
        ResourceLocation resourceLocation = ResourceLocation.parse(compoundTag.getString("aura"));
        auraInstance = new AuraInstance<>(armorStand, FantazicRegistry.AURAS.get(resourceLocation));
    }

    @Override
    public CompoundTag syncSerialize() {
        return new CompoundTag();
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
    }

    public void onDeath() {
        if (auraInstance != null) auraInstance.discard();
    }
    public void setAura(BasicAura<? extends Entity> basicAura) {
        if (armorStand != null) this.auraInstance = new AuraInstance<>(armorStand, basicAura);
    }
    public static class Serializer implements IAttachmentSerializer<CompoundTag, ArmorStandCommandAuraHolder> {

        @Override
        public @NotNull ArmorStandCommandAuraHolder read(@NotNull IAttachmentHolder iAttachmentHolder, @NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
            ArmorStandCommandAuraHolder armorStandCommandAuraHolder = new ArmorStandCommandAuraHolder(iAttachmentHolder);
            armorStandCommandAuraHolder.deserializeNBT(provider, compoundTag);
            return armorStandCommandAuraHolder;
        }

        @Override
        public @Nullable CompoundTag write(@NotNull ArmorStandCommandAuraHolder armorStandCommandAuraHolder, HolderLookup.@NotNull Provider provider) {
            return armorStandCommandAuraHolder.serializeNBT(provider);
        }
    }
}
