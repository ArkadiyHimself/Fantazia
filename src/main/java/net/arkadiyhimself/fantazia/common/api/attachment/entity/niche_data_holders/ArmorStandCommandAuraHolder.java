package net.arkadiyhimself.fantazia.common.api.attachment.entity.niche_data_holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.common.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.common.api.attachment.IBasicHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class ArmorStandCommandAuraHolder implements IBasicHolder {

    private final ArmorStand armorStand;
    private @Nullable AuraInstance auraInstance = null;

    public ArmorStandCommandAuraHolder(IAttachmentHolder iAttachmentHolder) {
        this.armorStand = iAttachmentHolder instanceof ArmorStand entity ? entity : null;
    }

    @Override
    public ResourceLocation id() {
        return Fantazia.location("armor_stand_command_aura");
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (auraInstance != null) tag.put("instance", auraInstance.serializeSave());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        if (armorStand == null || !(armorStand.level() instanceof ServerLevel serverLevel)) return;

        AuraInstance instance = AuraInstance.deserializeSave(tag.getCompound("instance")).apply(armorStand);
        if (instance == null) return;
        this.auraInstance = instance;

        LevelAttributesHelper.addAuraInstance(serverLevel, auraInstance);
    }

    @Override
    public CompoundTag serializeInitial() {
        return new CompoundTag();
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
    }

    public void setAura(Holder<Aura> basicAura, int ampl) {
        if (armorStand == null || !(armorStand.level() instanceof ServerLevel serverLevel)) return;
        if (auraInstance != null) auraInstance.discard();
        auraInstance = new AuraInstance(armorStand, basicAura, ampl);
        LevelAttributesHelper.addAuraInstance(serverLevel, auraInstance);
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
