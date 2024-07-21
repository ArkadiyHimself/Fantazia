package net.arkadiyhimself.fantazia.networking.packets.keyinput;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.fantazia.Items.weapons.Melee.MeleeWeaponItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class WeaponAbilityC2S implements IPacket {
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() == null) { return; }
            if (context.getSender().getMainHandItem().getItem() instanceof MeleeWeaponItem weapon && weapon.hasActive()) {
                weapon.activeAbility(context.getSender());
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {

    }
    public static WeaponAbilityC2S read(FriendlyByteBuf packetBuf) {
        return new WeaponAbilityC2S();
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, WeaponAbilityC2S.class, WeaponAbilityC2S::read);
    }
}
