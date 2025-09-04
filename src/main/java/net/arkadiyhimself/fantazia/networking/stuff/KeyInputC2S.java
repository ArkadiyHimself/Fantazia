package net.arkadiyhimself.fantazia.networking.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.curio.FTZSlots;
import net.arkadiyhimself.fantazia.common.item.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.common.item.weapons.Melee.MeleeWeaponItem;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;
import java.util.function.BiConsumer;

public record KeyInputC2S(INPUT input, int action) implements IPacket {

    public static final CustomPacketPayload.Type<KeyInputC2S> TYPE = new Type<>(Fantazia.location("stuff.key_input"));

    public static final StreamCodec<FriendlyByteBuf, KeyInputC2S> CODEC = StreamCodec.composite(NeoForgeStreamCodecs.enumCodec(INPUT.class), KeyInputC2S::input, ByteBufCodecs.INT, KeyInputC2S::action, KeyInputC2S::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> StuffHandlers.keyInput(context, input, action));
    }

    public enum INPUT {
        WEAPON_ABILITY((serverPlayer, integer) -> {
            if (integer == 1 && serverPlayer.getMainHandItem().getItem() instanceof MeleeWeaponItem weapon && weapon.hasActive()) weapon.activeAbility(serverPlayer);
        }), // finished
        SPELLCAST1((serverPlayer, integer) -> {
            Optional<SlotResult> result = FantazicUtil.findCurio(serverPlayer, FTZSlots.ACTIVECASTER, 0);
            if (result.isEmpty()) return;
            Item item = result.get().stack().getItem();
            if (item instanceof SpellCasterItem spellCasterItem && !spellCasterItem.tryCast(serverPlayer).success()) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.DENIED.get());
        }), // finished
        SPELLCAST2((serverPlayer, integer) -> {
            Optional<SlotResult> result = FantazicUtil.findCurio(serverPlayer, FTZSlots.ACTIVECASTER, 1);
            if (result.isEmpty()) return;
            Item item = result.get().stack().getItem();
            if (item instanceof SpellCasterItem spellCasterItem && !spellCasterItem.tryCast(serverPlayer).success()) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.DENIED.get());
        }), // finished
        SPELLCAST3((serverPlayer, integer) -> {
            Optional<SlotResult> result = FantazicUtil.findCurio(serverPlayer, FTZSlots.ACTIVECASTER, 2);
            if (result.isEmpty()) return;
            Item item = result.get().stack().getItem();
            if (item instanceof SpellCasterItem spellCasterItem && !spellCasterItem.tryCast(serverPlayer).success()) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.DENIED.get());
        }); // finished

        private final BiConsumer<ServerPlayer, Integer> consumer;

        INPUT(BiConsumer<ServerPlayer, Integer> consumer) {
            this.consumer = consumer;
        }

        public BiConsumer<ServerPlayer, Integer> consumer() {
            return consumer;
        }
    }
}
