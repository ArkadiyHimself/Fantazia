package net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHolder;
import net.arkadiyhimself.fantazia.api.type.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.util.library.SizedList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class CommonDataHolder extends LivingDataHolder implements IDamageEventListener {
    private final SizedList<ParameterHolder> parameterHolders = new SizedList<>(101);
    private Vec3 rewindPosition = Vec3.ZERO;
    private int rewindTicks = 0;
    private int damageTicks = 0;
    private float prevHP;
    public CommonDataHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("common_data"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("damageTicks", damageTicks);
        if (rewindTicks > 0) {
            tag.putInt("rewindTicks", rewindTicks);
            CompoundTag rewindPos = new CompoundTag();
            rewindPos.putDouble("rewindX", rewindPosition.x());
            rewindPos.putDouble("rewindY", rewindPosition.y());
            rewindPos.putDouble("rewindZ", rewindPosition.z());
            tag.put("rewindPos", rewindPos);
        }

        ListTag parameters = new ListTag();
        for (ParameterHolder holder : parameterHolders) parameters.add(holder.serialize());
        tag.put("parameters", parameters);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        damageTicks = compoundTag.getInt("damageTicks");
        rewindTicks = compoundTag.getInt("rewindTicks");
        if (compoundTag.contains("rewindPos")) {
            CompoundTag rewindPos = compoundTag.getCompound("rewindPos");
            double x = rewindPos.getDouble("rewindX");
            double y = rewindPos.getDouble("rewindY");
            double z = rewindPos.getDouble("rewindZ");
            this.rewindPosition = new Vec3(x, y, z);
        }

        ListTag parameters = compoundTag.getList("parameters", Tag.TAG_COMPOUND);
        if (parameters.isEmpty()) return;
        for (int i = 0; i < parameters.size(); i++) parameterHolders.add(ParameterHolder.deserialize(parameters.getCompound(i)));
    }

    @Override
    public void tick() {
        parameterHolders.add(ParameterHolder.write(getEntity()));
        if (damageTicks > 0) damageTicks--;
        if (rewindTicks > 0) rewindTicks--;
    }
    @Override
    public void onHit(LivingDamageEvent.Post event) {
        prevHP = event.getEntity().getHealth();
        if (!event.getSource().is(FTZDamageTypes.REMOVAL)) damageTicks = 100;
    }
    public int getDamageTicks() {
        return damageTicks;
    }
    public float getPrevHP() {
        return prevHP;
    }
    public boolean writtenParameters() {
        return parameterHolders.size() == parameterHolders.getMaxSize();
    }
    public boolean tryReadParameters(int index, LivingEntity livingEntity) {
        if (parameterHolders.size() <= index) return false;
        rewindPosition = livingEntity.position();
        rewindTicks = 6;
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
