package net.arkadiyhimself.fantazia.api.capability.entity.feature;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class FeatureGetter extends CapabilityAttacher {
    private static final Class<FeatureManager> FEATURE_CLASS = FeatureManager.class;
    public static final Capability<FeatureManager> FEATURE = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation FEATURE_RL = Fantazia.res("feature");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static FeatureManager getUnwrap(Entity entity) {
        return get(entity).orElse(null);
    }
    public static @NotNull LazyOptional<FeatureManager> get(Entity entity) {
        return entity.getCapability(FEATURE);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, Entity entity) {
        genericAttachCapability(event, new FeatureManager(entity), FEATURE, FEATURE_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(FEATURE_CLASS);
        CapabilityAttacher.registerEntityAttacher(Entity.class, FeatureGetter::attacher, FeatureGetter::get, true);
    }
}
