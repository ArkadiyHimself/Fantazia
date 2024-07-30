package net.arkadiyhimself.fantazia.api.capability.level;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class LevelCapGetter extends CapabilityAttacher {
    private static final Class<LevelCap> LEVEL_CAP_CLASS = LevelCap.class;
    public static final Capability<LevelCap> LEVEL_CAP = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation LEVEL_CAP_RL = Fantazia.res("level_capability");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static LevelCap getLevelCap(Level level) {
        return get(level).orElse(null);
    }
    public static LazyOptional<LevelCap> get(Level level) {
        if (level == null) return LazyOptional.empty();
        return level.getCapability(LEVEL_CAP);
    }
    private static void attach(AttachCapabilitiesEvent<Level> event, Level level) {
        genericAttachCapability(event, new LevelCap(level), LEVEL_CAP, LEVEL_CAP_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(LEVEL_CAP_CLASS);
        CapabilityAttacher.registerLevelAttacher(LevelCapGetter::attach, LevelCapGetter::get);
    }
}
