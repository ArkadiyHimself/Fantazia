package net.arkadiyhimself.fantazia.networking.packets.capabilityupdate;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.TalentsHolder;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.data.talents.TalentLoad;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class TalentBuyingC2S implements IPacket {
    private final ResourceLocation location;
    public TalentBuyingC2S(ResourceLocation location) {
        this.location = location;
    }
    public TalentBuyingC2S(BasicTalent talent) {
        this.location = talent.getID();
    }
    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer == null) return;
            AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
            if (abilityManager == null) return;
            TalentsHolder talentsHolder = abilityManager.takeAbility(TalentsHolder.class);
            if (talentsHolder == null) return;
            BasicTalent talent = TalentLoad.getTalents().get(location);
            if (talent == null) return;
            if (!talentsHolder.buyTalent(talent)) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.DENIED), serverPlayer);
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeResourceLocation(location);
    }
    public static TalentBuyingC2S read(FriendlyByteBuf packetBuf) {
        return new TalentBuyingC2S(packetBuf.readResourceLocation());
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, TalentBuyingC2S.class, TalentBuyingC2S::read);
    }
}
