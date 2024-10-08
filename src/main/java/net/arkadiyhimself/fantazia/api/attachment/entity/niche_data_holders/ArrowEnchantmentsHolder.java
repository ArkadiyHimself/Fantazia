package net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.type.entity.IBasicHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class ArrowEnchantmentsHolder implements IBasicHolder {
    private final AbstractArrow arrow;
    private boolean frozen = false;
    private int duelist = 0;
    private int ballista = 0;

    public ArrowEnchantmentsHolder(IAttachmentHolder iAttachmentHolder) {
        this.arrow = iAttachmentHolder instanceof AbstractArrow entity ? entity : null;
    }

    @Override
    public ResourceLocation id() {
        return Fantazia.res("arrow_enchantments");
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("frozen", frozen);
        tag.putInt("duelist", duelist);
        tag.putInt("ballista", ballista);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        this.frozen = compoundTag.getBoolean("frozen");
        this.duelist = compoundTag.getInt("duelist");
        this.ballista = compoundTag.getInt("ballista");
    }

    @Override
    public void tick() {
        if (arrow == null) return;
        if (arrow.isInPowderSnow) frozen = true;
        double X = arrow.position().x();
        double Y = arrow.position().y();
        double Z = arrow.position().z();
        if (frozen && Minecraft.getInstance().level != null) Minecraft.getInstance().level.addParticle(ParticleTypes.SNOWFLAKE, X, Y, Z,0,0,0);
    }

    @Override
    public CompoundTag syncSerialize() {
        return new CompoundTag();
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {

    }

    public void freeze() {
        this.frozen = true;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setDuelist(int value) {
        this.duelist = value;
    }

    public int getDuelist() {
        return duelist;
    }

    public void setBallista(int value) {
        this.ballista = value;
    }

    public int getBallista() {
        return ballista;
    }
    public static class Serializer implements IAttachmentSerializer<CompoundTag, ArrowEnchantmentsHolder> {

        @Override
        public @NotNull ArrowEnchantmentsHolder read(@NotNull IAttachmentHolder iAttachmentHolder, @NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
            ArrowEnchantmentsHolder holder = new ArrowEnchantmentsHolder(iAttachmentHolder);
            holder.deserializeNBT(provider, compoundTag);
            return holder;
        }

        @Override
        public @Nullable CompoundTag write(@NotNull ArrowEnchantmentsHolder arrowEnchantmentsHolder, HolderLookup.@NotNull Provider provider) {
            return arrowEnchantmentsHolder.serializeNBT(provider);
        }
    }
}
