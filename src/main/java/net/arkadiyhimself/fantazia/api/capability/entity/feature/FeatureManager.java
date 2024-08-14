package net.arkadiyhimself.fantazia.api.capability.entity.feature;

import dev._100media.capabilitysyncer.core.EntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.features.ArrowEnchant;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.features.AuraCarry;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class FeatureManager extends EntityCapability {
    private final List<FeatureHolder> FEATURES = Lists.newArrayList();
    public FeatureManager(Entity entity) {
        super(entity);
        FeatureProvider.provide(this);
    }
    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), FeatureGetter.FEATURE_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        FEATURES.forEach(featureHolder -> tag.merge(featureHolder.serialize()));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        FEATURES.forEach(featureHolder -> featureHolder.deserialize(nbt));
    }

    public void tick() {
        FEATURES.forEach(featureHolder -> {
            if (featureHolder instanceof ITicking iTicking) iTicking.tick();
        });
        updateTracking();
    }
    public void onDeath() {
        FEATURES.forEach(FeatureHolder::onDeath);
    }
    public void grantFeature(Function<Entity, FeatureHolder> feature) {
        FeatureHolder featureHolder = feature.apply(entity);
        if (hasFeature(featureHolder.getClass())) return;
        FEATURES.add(featureHolder);
    }
    public <T extends FeatureHolder> LazyOptional<T> getFeature(Class<T> tClass) {
        T ability = takeFeature(tClass);
        return ability == null ? LazyOptional.empty() : LazyOptional.of(() -> ability);
    }
    @Nullable
    public <T extends FeatureHolder> T takeFeature(Class<T> tClass) {
        for (FeatureHolder featureHolder : FEATURES) if (tClass == featureHolder.getClass()) return tClass.cast(featureHolder);
        return null;
    }
    public  <T extends FeatureHolder> boolean hasFeature(Class<T> tClass) {
        for (FeatureHolder featureHolder : FEATURES) if (tClass.isInstance(featureHolder)) return true;
        return false;
    }
    private static class FeatureProvider {
        private static void provide(FeatureManager featureManager) {
            Entity owner = featureManager.entity;
            if (owner instanceof ArmorStand armorStand) featureManager.grantFeature((t -> new AuraCarry(armorStand)));
            if (owner instanceof AbstractArrow abstractArrow) featureManager.grantFeature(t -> new ArrowEnchant(abstractArrow));
        }

    }
}
