package net.arkadiyhimself.fantazia.advanced.capability.entity.CommonData;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class DataGetter extends CapabilityAttacher {
    private static final Class<DataManager> DATA_CLASS = DataManager.class;
    public static final Capability<DataManager> DATA = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation DATA_RL = Fantazia.res("data");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static DataManager getUnwrap(LivingEntity entity) {
        return get(entity).orElse(null);
    }
    public static LazyOptional<DataManager> get(LivingEntity entity) {
        return entity.getCapability(DATA);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, LivingEntity livingEntity) {
        genericAttachCapability(event, new DataManager(livingEntity), DATA, DATA_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(DATA_CLASS);
        CapabilityAttacher.registerEntityAttacher(LivingEntity.class, DataGetter::attacher, DataGetter::get, true);
    }
}
