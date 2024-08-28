package net.arkadiyhimself.fantazia.networking;

import com.google.common.collect.ImmutableList;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleLevelCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureGetter;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapGetter;
import net.arkadiyhimself.fantazia.networking.packets.*;
import net.arkadiyhimself.fantazia.networking.packets.capabilityupdate.EntityMadeSoundS2C;
import net.arkadiyhimself.fantazia.networking.packets.capabilityupdate.SoundExpiredS2C;
import net.arkadiyhimself.fantazia.networking.packets.capabilityupdate.TalentBuyingC2S;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.function.BiConsumer;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            Fantazia.res("main"),
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
                .add(SimpleLevelCapabilityStatusPacket::register)

                // stuff
                .add(KickOutOfGuiS2C::register)
                .add(AddParticleS2C::register)
                .add(KeyInputC2S::register)
                // player's animation
                .add(PlayAnimationS2C::register)
                .add(SwingHandS2C::register)

                // play sound
                .add(PlaySoundForUIS2C::register)

                // capability update
                .add(EntityMadeSoundS2C::register)
                .add(SoundExpiredS2C::register)
                .add(TalentBuyingC2S::register)

                .add(ResetFallDistanceC2S::register)
                .build();

        // abilities
        SimpleEntityCapabilityStatusPacket.registerRetriever(AbilityGetter.ABILITY_RL, AbilityGetter::getUnwrap);
        SimpleEntityCapabilityStatusPacket.registerRetriever(EffectGetter.EFFECT_RL, EffectGetter::getUnwrap);
        SimpleEntityCapabilityStatusPacket.registerRetriever(DataGetter.DATA_RL, DataGetter::getUnwrap);
        SimpleEntityCapabilityStatusPacket.registerRetriever(FeatureGetter.FEATURE_RL, FeatureGetter::getUnwrap);

        // level cap
        SimpleLevelCapabilityStatusPacket.registerRetriever(LevelCapGetter.LEVEL_CAP_RL, LevelCapGetter::getLevelCap);

        packets.forEach(consumer -> consumer.accept(INSTANCE, getNextId()));
    }
    public static <MSG> void sendToServer(MSG msg) {
        INSTANCE.sendToServer(msg);
    }
    public static <MSG> void sendToPlayer(MSG msg, Player player) {
        if (player instanceof ServerPlayer serverPlayer) INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
    }
    public static <MSG> void sendToPlayers(MSG msg, ServerLevel level) {
        for (ServerPlayer player : level.players()) INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
