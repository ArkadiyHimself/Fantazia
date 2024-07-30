package net.arkadiyhimself.fantazia.api.capability.entity.data;

import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.AuraOwning;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.CommonData;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.DarkFlameTicks;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.HatchetStuck;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class DataManager extends LivingEntityCapability {
    private final List<DataHolder> DATA = Lists.newArrayList();
    public DataManager(LivingEntity entity) {
        super(entity);
        DataProvider.provide(this);
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.livingEntity.getId(), DataGetter.DATA_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        DATA.forEach(dataHolder -> tag.merge(dataHolder.serialize()));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        DATA.forEach(dataHolder -> dataHolder.deserialize(nbt));
    }
    public void tick() {
        DATA.forEach(dataHolder -> {
            if (dataHolder instanceof ITicking iTicking) iTicking.tick();
        });
        updateTracking();
    }
    public void respawn() {
        DATA.forEach(DataHolder::respawn);
    }
    public void grantData(Function<LivingEntity, DataHolder> ability) {
        DataHolder dataHolder = ability.apply(livingEntity);
        if (hasData(dataHolder.getClass())) return;
        DATA.add(dataHolder);
    }
    public <T extends DataHolder> LazyOptional<T> getData(Class<T> tClass) {
        T ability = takeData(tClass);
        return ability == null ? LazyOptional.empty() : LazyOptional.of(() -> ability);
    }
    @Nullable
    public <T extends DataHolder> T takeData(Class<T> tClass) {
        for (DataHolder dataHolder : DATA) if (tClass == dataHolder.getClass()) return tClass.cast(dataHolder);
        return null;
    }
    public <T extends DataHolder> boolean hasData(Class<T> tClass) {
        for (DataHolder dataHolder : DATA) if (tClass.isInstance(dataHolder)) return true;
        return false;
    }
    public void onHit(LivingAttackEvent event) {
        DATA.forEach(dataHolder -> {
            if (dataHolder instanceof IDamageReacting damageReacting) damageReacting.onHit(event);
        });
    }
    public void onHit(LivingHurtEvent event) {
        DATA.forEach(dataHolder -> {
            if (dataHolder instanceof IDamageReacting damageReacting) damageReacting.onHit(event);
        });
    }
    public void onHit(LivingDamageEvent event) {
        DATA.forEach(dataHolder -> {
            if (dataHolder instanceof IDamageReacting damageReacting) damageReacting.onHit(event);
        });
    }
    private static class DataProvider {
        private static void provide(DataManager dataManager) {
            dataManager.grantData(DarkFlameTicks::new);
            dataManager.grantData(CommonData::new);
            dataManager.grantData(AuraOwning::new);
            dataManager.grantData(HatchetStuck::new);
        }
    }
}
