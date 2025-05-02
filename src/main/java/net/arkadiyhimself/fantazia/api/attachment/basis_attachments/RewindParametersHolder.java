package net.arkadiyhimself.fantazia.api.attachment.basis_attachments;

import net.arkadiyhimself.fantazia.util.library.SizedList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class RewindParametersHolder implements INBTSerializable<CompoundTag> {

    private final SizedList<ParameterHolder> parameterHolders = new SizedList<>(101);
    private final IAttachmentHolder iAttachmentHolder;

    public RewindParametersHolder(IAttachmentHolder iAttachmentHolder) {
        this.iAttachmentHolder = iAttachmentHolder;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag parameters = new ListTag();
        for (ParameterHolder holder : parameterHolders) parameters.add(holder.serialize());
        tag.put("parameters", parameters);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        ListTag parameters = compoundTag.getList("parameters", Tag.TAG_COMPOUND);
        if (parameters.isEmpty()) return;
        for (int i = 0; i < parameters.size(); i++) parameterHolders.add(ParameterHolder.deserialize(parameters.getCompound(i)));
    }

    public void tick() {
        if (iAttachmentHolder instanceof LivingEntity entity) parameterHolders.add(ParameterHolder.write(entity));
    }

    public boolean writtenParameters() {
        return parameterHolders.size() >= parameterHolders.getMaxSize();
    }

    public boolean tryReadParameters(int index, LivingEntity livingEntity) {
        if (parameterHolders.size() <= index) return false;
        parameterHolders.get(index).read(livingEntity);
        return true;
    }

    public static class ParameterHolder {

        private final float health;
        private final Vec3 position;

        private ParameterHolder(float health, Vec3 position) {
            this.health = health;
            this.position = position;
        }

        public void read(LivingEntity livingEntity) {
            livingEntity.setHealth(health);
            livingEntity.teleportTo(position.x(), position.y(), position.z());
        }

        public static ParameterHolder write(LivingEntity entity) {
            return new ParameterHolder(entity.getHealth(), entity.position());
        }

        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("health", health);
            tag.putDouble("x", position.x());
            tag.putDouble("y", position.y());
            tag.putDouble("z", position.z());
            return tag;
        }

        public static ParameterHolder deserialize(CompoundTag tag) {
            float health = tag.getFloat("health");
            double x = tag.getDouble("x");
            double y = tag.getDouble("y");
            double z = tag.getDouble("z");
            return new ParameterHolder(health, new Vec3(x,y,z));
        }
    }
}
