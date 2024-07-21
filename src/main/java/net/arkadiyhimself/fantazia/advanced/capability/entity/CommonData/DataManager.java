package net.arkadiyhimself.fantazia.advanced.capability.entity.CommonData;

import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectGetter;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.util.interfaces.IPlayerAbility;
import net.arkadiyhimself.fantazia.util.interfaces.ITicking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class DataManager extends LivingEntityCapability {
    private final List<DataHolder> DATA = Lists.newArrayList();
    public DataManager(LivingEntity entity) {
        super(entity);
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
        DATA.forEach(inbTsaver -> {
            if (inbTsaver instanceof ITicking iTicking) iTicking.tick();
        });
        updateTracking();
    }
    public void respawn() {
        DATA.forEach(inbTsaver -> {
            if (inbTsaver instanceof IPlayerAbility iPlayerAbility) iPlayerAbility.respawn();
        });
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
        T object = null;
        for (DataHolder dataHolder : DATA) {
            if (tClass == dataHolder.getClass()) {
                object = tClass.cast(dataHolder);
                break;
            }
        }
        return object;
    }
    public <T extends DataHolder> boolean hasData(Class<T> tClass) {
        for (DataHolder dataHolder : DATA) {
            if (tClass.isInstance(dataHolder)) return true;
        }
        return false;
    }
    private class DataProvider {
        private static void provide(DataManager dataManager) {

        }
    }
}
