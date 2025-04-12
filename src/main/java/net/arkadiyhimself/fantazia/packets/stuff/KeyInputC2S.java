package net.arkadiyhimself.fantazia.packets.stuff;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DoubleJumpHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.MeleeBlockHolder;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.items.weapons.Melee.MeleeWeaponItem;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;
import java.util.function.BiConsumer;

public record KeyInputC2S(INPUT input, int action) implements IPacket {

    public static final CustomPacketPayload.Type<KeyInputC2S> TYPE = new Type<>(Fantazia.res("stuff.key_input"));

    public static final StreamCodec<FriendlyByteBuf, KeyInputC2S> CODEC = StreamCodec.composite(NeoForgeStreamCodecs.enumCodec(INPUT.class), KeyInputC2S::input, ByteBufCodecs.INT, KeyInputC2S::action, KeyInputC2S::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            input.consumer.accept(serverPlayer, action);
        });
    }

    public enum INPUT {
        DASH((serverPlayer, integer)-> {
            if (integer != 1) return;
            Vec3 vec3 = PlayerAbilityHelper.dashDeltaMovement(serverPlayer, 1.8f, true);
            PlayerAbilityGetter.acceptConsumer(serverPlayer, DashHolder.class, dash -> dash.beginDash(vec3));
            LivingEffectHelper.unDisguise(serverPlayer);
        }), // finished
        BLOCK((serverPlayer, integer) -> {
            if (integer != 1) return;
            PlayerAbilityGetter.acceptConsumer(serverPlayer, MeleeBlockHolder.class, MeleeBlockHolder::startBlocking);
            LivingEffectHelper.unDisguise(serverPlayer);
        }), // finished
        JUMP((serverPlayer, integer) -> {
            if (integer == 0) PlayerAbilityGetter.acceptConsumer(serverPlayer, DoubleJumpHolder.class, DoubleJumpHolder::buttonRelease);
            else if (integer == 1) PlayerAbilityGetter.acceptConsumer(serverPlayer, DoubleJumpHolder.class, DoubleJumpHolder::tryToJump);
        }), // finished
        WEAPON_ABILITY((serverPlayer, integer) -> {
            if (integer == 1 && serverPlayer.getMainHandItem().getItem() instanceof MeleeWeaponItem weapon && weapon.hasActive()) weapon.activeAbility(serverPlayer);
        }), // finished
        SPELLCAST1((serverPlayer, integer) -> {
            Optional<SlotResult> result = InventoryHelper.findCurio(serverPlayer, "spellcaster", 0);
            if (result.isEmpty()) return;
            Item spellCaster = result.get().stack().getItem();
            if (spellCaster instanceof SpellCasterItem selfCaster && !selfCaster.tryCast(serverPlayer)) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.DENIED.get()));
        }), // finished
        SPELLCAST2((serverPlayer, integer) -> {
            Optional<SlotResult> result = InventoryHelper.findCurio(serverPlayer, "spellcaster", 1);
            if (result.isEmpty()) return;
            Item spellCaster = result.get().stack().getItem();
            if (spellCaster instanceof SpellCasterItem selfCaster && !selfCaster.tryCast(serverPlayer)) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.DENIED.get()));
        }), // finished
        SPELLCAST3((serverPlayer, integer) -> {
            Optional<SlotResult> result = InventoryHelper.findCurio(serverPlayer, "spellcaster", 2);
            if (result.isEmpty()) return;
            Item spellCaster = result.get().stack().getItem();
            if (spellCaster instanceof SpellCasterItem selfCaster && !selfCaster.tryCast(serverPlayer)) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.DENIED.get()));
        }); // finished
        private final BiConsumer<ServerPlayer, Integer> consumer;
        INPUT(BiConsumer<ServerPlayer, Integer> consumer) {
            this.consumer = consumer;
        }
    }
}
