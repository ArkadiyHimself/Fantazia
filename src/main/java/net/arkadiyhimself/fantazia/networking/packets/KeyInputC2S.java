package net.arkadiyhimself.fantazia.networking.packets;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.DoubleJump;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.MeleeBlock;
import net.arkadiyhimself.fantazia.items.casters.SpellCaster;
import net.arkadiyhimself.fantazia.items.weapons.Melee.MeleeWeaponItem;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;
import java.util.function.BiConsumer;

public class KeyInputC2S implements IPacket {
    private final INPUT input;
    private final int action;
    public KeyInputC2S(INPUT input, int action) {
        this.input = input;
        this.action = action;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer != null) input.consumer.accept(serverPlayer, action);
        });
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeEnum(input);
        packetBuf.writeInt(action);
    }
    public static KeyInputC2S read(FriendlyByteBuf packetBuf) {
        return new KeyInputC2S(packetBuf.readEnum(INPUT.class), packetBuf.readInt());
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, KeyInputC2S.class, KeyInputC2S::read);
    }
    public enum INPUT {
        DASH((serverPlayer, integer)-> {
            if (integer != 1) return;
            Vec3 vec3 = AbilityHelper.dashDeltaMovement(serverPlayer, 1.8f, true);
            AbilityGetter.abilityConsumer(serverPlayer, Dash.class, dash -> dash.beginDash(vec3));
        }), // finished
        BLOCK((serverPlayer, integer) -> {
            if (integer != 1) return;
            AbilityGetter.abilityConsumer(serverPlayer, MeleeBlock.class, MeleeBlock::startBlocking);
        }), // finished
        JUMP((serverPlayer, integer) -> {
            if (integer == 0) AbilityGetter.abilityConsumer(serverPlayer, DoubleJump.class, DoubleJump::buttonRelease);
            else if (integer == 1) AbilityGetter.abilityConsumer(serverPlayer, DoubleJump.class, DoubleJump::tryToJump);
        }), // finished
        WEAPON_ABILITY((serverPlayer, integer) -> {
            if (integer == 1 && serverPlayer.getMainHandItem().getItem() instanceof MeleeWeaponItem weapon && weapon.hasActive()) weapon.activeAbility(serverPlayer);
        }), // finished
        SPELLCAST1((serverPlayer, integer) -> {
            Optional<SlotResult> result = InventoryHelper.findCurio(serverPlayer, "spellcaster", 0);
            if (result.isEmpty()) return;
            Item spellCaster = result.get().stack().getItem();
            if (spellCaster instanceof SpellCaster selfCaster && !selfCaster.tryCast(serverPlayer)) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.DENIED.get()), serverPlayer);
        }), // finished
        SPELLCAST2((serverPlayer, integer) -> {
            Optional<SlotResult> result = InventoryHelper.findCurio(serverPlayer, "spellcaster", 1);
            if (result.isEmpty()) return;
            Item spellCaster = result.get().stack().getItem();
            if (spellCaster instanceof SpellCaster selfCaster && !selfCaster.tryCast(serverPlayer)) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.DENIED.get()), serverPlayer);
        }); // finished
        private final BiConsumer<ServerPlayer, Integer> consumer;
        INPUT(BiConsumer<ServerPlayer, Integer> consumer) {
            this.consumer = consumer;
        }
    }
}
