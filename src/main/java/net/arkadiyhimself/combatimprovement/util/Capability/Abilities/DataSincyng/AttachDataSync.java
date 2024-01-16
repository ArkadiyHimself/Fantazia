package net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class AttachDataSync extends CapabilityAttacher {
    private static final Class<DataSync> DATA_SYNC_CLASS = DataSync.class;
    public static final Capability<DataSync> DATA_SYNC = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation DATA_SYNC_RL = new ResourceLocation(CombatImprovement.MODID, "datasync");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static DataSync getUnwrap(LivingEntity entity) {
        return get(entity).orElse(null);
    }
    public static LazyOptional<DataSync> get(@Nullable LivingEntity entity) {
        if (entity == null) { return null; }
        return entity.getCapability(DATA_SYNC);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, Player player) {
        genericAttachCapability(event, new DataSync(player), DATA_SYNC, DATA_SYNC_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(DATA_SYNC_CLASS);
        CapabilityAttacher.registerEntityAttacher(Player.class, AttachDataSync::attacher, AttachDataSync::get, true);
    }
}
