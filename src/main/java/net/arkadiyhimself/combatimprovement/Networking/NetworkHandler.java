package net.arkadiyhimself.combatimprovement.Networking;

import com.google.common.collect.ImmutableList;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.Networking.packets.*;
import net.arkadiyhimself.combatimprovement.Networking.packets.CapabilityUpdate.*;
import net.arkadiyhimself.combatimprovement.Networking.packets.KeyInputC2S.CastSpellC2S;
import net.arkadiyhimself.combatimprovement.Networking.packets.KeyInputC2S.WeaponAbilityC2S;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Blocking.AttachBlocking;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.AttachDJump;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.AbsoluteBarrier.AbsoluteBarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect.BarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.StunEffect.StunEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.function.BiConsumer;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CombatImprovement.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int packetID = 0;
    private static int getNextId() {
        return packetID++;
    }
    public static void register() {
        List<BiConsumer<SimpleChannel, Integer>> packets = ImmutableList.<BiConsumer<SimpleChannel, Integer>>builder()
                .add(SimpleEntityCapabilityStatusPacket::register)

                // stuff
                .add(KickOutOfGuiS2CPacket::register
                )
                // player's animation
                .add(PlayAnimationS2C::register)

                // use new items
                .add(WeaponAbilityC2S::register)
                .add(CastSpellC2S::register)

                // play sound
                .add(PlaySoundForUIS2C::register)

                // capability update
                .add(StartedBlockingC2S::register)
                .add(StartedDashingC2S::register)
                .add(JustDJumpedC2S::register)
                .add(DJumpStartTickC2S::register)
                .add(DeltaMovementC2S::register)
                .add(DoubleJumpC2S::register)

                .add(ResetFallDistanceC2S::register)
                .build();

        // abilities
        SimpleEntityCapabilityStatusPacket.registerRetriever(AttachDataSync.DATA_SYNC_RL, AttachDataSync::getUnwrap);
        SimpleEntityCapabilityStatusPacket.registerRetriever(AttachBlocking.BLOCKING_RL, AttachBlocking::getUnwrap);
        SimpleEntityCapabilityStatusPacket.registerRetriever(AttachDash.DASH_RL, AttachDash::getUnwrap);
        SimpleEntityCapabilityStatusPacket.registerRetriever(AttachDJump.DJUMP_RL, AttachDJump::getUnwrap);

        // mob effect caps
        SimpleEntityCapabilityStatusPacket.registerRetriever(StunEffect.STUN_EEFFECT_RL, StunEffect::getUnwrap);
        SimpleEntityCapabilityStatusPacket.registerRetriever(BarrierEffect.BARRIER_EEFFECT_RL, BarrierEffect::getUnwrap);
        SimpleEntityCapabilityStatusPacket.registerRetriever(LayeredBarrierEffect.LAYERED_BARRIER_EEFFECT_RL, LayeredBarrierEffect::getUnwrap);
        SimpleEntityCapabilityStatusPacket.registerRetriever(AbsoluteBarrierEffect.ABSOLUTE_BARRIER_EEFFECT_RL, AbsoluteBarrierEffect::getUnwrap);

        packets.forEach(consumer -> consumer.accept(INSTANCE, getNextId()));
    }

    public static <MSG> void sendToServer(MSG msg) {
        INSTANCE.sendToServer(msg);
    }

    public static <MSG> void sendToPlayer(MSG msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
