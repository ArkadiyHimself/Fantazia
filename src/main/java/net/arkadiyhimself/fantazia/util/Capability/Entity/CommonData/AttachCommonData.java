package net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class AttachCommonData extends CapabilityAttacher {
    private static final Class<CommonData> COMMON_DATA_CLASS = CommonData.class;
    public static final Capability<CommonData> COMMON_DATA_SYNC = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation COMMON_DATA_SYNC_RL = Fantazia.res("commondata");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static CommonData getUnwrap(Entity entity) {
        return get(entity).orElse(null);
    }
    public static LazyOptional<CommonData> get(@Nullable Entity entity) {
        if (entity == null) { return null; }
        return entity.getCapability(COMMON_DATA_SYNC);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, Entity entity) {
        genericAttachCapability(event, new CommonData(entity), COMMON_DATA_SYNC, COMMON_DATA_SYNC_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(COMMON_DATA_CLASS);
        CapabilityAttacher.registerEntityAttacher(Entity.class, AttachCommonData::attacher, AttachCommonData::get, true);
    }
}
