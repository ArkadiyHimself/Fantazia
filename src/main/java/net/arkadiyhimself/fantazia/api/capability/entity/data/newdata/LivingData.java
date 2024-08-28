package net.arkadiyhimself.fantazia.api.capability.entity.data.newdata;

import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataHolder;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.util.library.SizedList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class LivingData extends DataHolder implements ITicking, IDamageReacting {
    private final SizedList<ParameterHolder> parameterHolders = new SizedList<>(101);
    private Vec3 rewindPosition = Vec3.ZERO;
    private int rewindTicks = 0;
    private int damageTicks = 0;
    private float prevHP;
    public LivingData(LivingEntity livingEntity) {
        super(livingEntity);
        prevHP = livingEntity.getHealth();
    }
    @Override
    public String ID() {
        return "living_data";
    }
    @Override
    public void tick() {
        parameterHolders.add(ParameterHolder.write(getEntity()));
        if (damageTicks > 0) damageTicks--;
        if (rewindTicks > 0) rewindTicks--;
    }
    @Override
    public void onHit(LivingHurtEvent event) {
        prevHP = getEntity().getHealth();
        if (!event.getSource().is(FTZDamageTypes.REMOVAL)) damageTicks = 100;
    }
    @Override
    public CompoundTag serialize(boolean toDisk) {
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

        if (!toDisk) return tag;
        ListTag parameters = new ListTag();
        for (ParameterHolder holder : parameterHolders) parameters.add(holder.serialize());
        tag.put("parameters", parameters);

        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        damageTicks = tag.getInt("damageTicks");
        rewindTicks = tag.getInt("rewindTicks");
        if (tag.contains("rewindPos")) {
            CompoundTag rewindPos = tag.getCompound("rewindPos");
            double x = rewindPos.getDouble("rewindX");
            double y = rewindPos.getDouble("rewindY");
            double z = rewindPos.getDouble("rewindZ");
            this.rewindPosition = new Vec3(x, y, z);
        }
        if (!fromDisk) return;

        ListTag parameters = tag.getList("parameters", Tag.TAG_COMPOUND);
        if (parameters.isEmpty()) return;
        for (int i = 0; i < parameters.size(); i++) parameterHolders.add(ParameterHolder.deserialize(parameters.getCompound(i)));
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
    public boolean tryReadParameters(int index) {
        if (parameterHolders.size() <= index) return false;
        rewindPosition = getEntity().position();
        rewindTicks = 6;
        parameterHolders.get(index).read(getEntity());
        return true;
    }
    public boolean rewind() {
        return rewindTicks > 0;
    }
    public Vec3 rewindPos() {
        return rewindPosition;
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
