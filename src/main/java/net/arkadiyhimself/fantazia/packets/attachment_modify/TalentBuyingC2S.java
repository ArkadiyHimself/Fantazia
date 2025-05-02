package net.arkadiyhimself.fantazia.packets.attachment_modify;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.data.talent.reload.TalentManager;
import net.arkadiyhimself.fantazia.data.talent.types.ITalent;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TalentBuyingC2S(ResourceLocation location) implements IPacket {

    public static final CustomPacketPayload.Type<TalentBuyingC2S> TYPE = new Type<>(Fantazia.res("data_attachment_modify.talent_buying"));

    public static final StreamCodec<ByteBuf, TalentBuyingC2S> CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, TalentBuyingC2S::location, TalentBuyingC2S::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;

            TalentsHolder talentsHolder = PlayerAbilityHelper.takeHolder(serverPlayer, TalentsHolder.class);
            if (talentsHolder == null) return;

            ITalent talent = TalentManager.getTalents().get(location);
            if (talent == null) return;

            if (!talentsHolder.buyTalent(talent)) IPacket.soundForUI(serverPlayer, FTZSoundEvents.DENIED.get());
        });
    }
}
