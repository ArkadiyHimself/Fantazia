package net.arkadiyhimself.fantazia.api.capability.entity.talent;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class TalentGetter extends CapabilityAttacher {
    private static final Class<TalentData> TALENT_DATA_CLASS = TalentData.class;
    public static final Capability<TalentData> TALENT_DATA = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation TALENT_DATA_RL = Fantazia.res("talent_data");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static TalentData getUnwrap(LivingEntity entity) {
        return get(entity).orElse(null);
    }
    public static LazyOptional<TalentData> get(@Nullable LivingEntity entity) {
        if (entity == null) { return null; }
        return entity.getCapability(TALENT_DATA);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, Player player) {
        genericAttachCapability(event, new TalentData(player), TALENT_DATA, TALENT_DATA_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(TALENT_DATA_CLASS);
        CapabilityAttacher.registerEntityAttacher(Player.class, TalentGetter::attacher, TalentGetter::get, true);
    }
}
