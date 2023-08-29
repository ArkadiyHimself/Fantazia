package net.arkadiyhimself.combatimprovement.Networking.packets.KeyInputC2S;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.UsefulMethods;
import net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.SpellCasters;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class CastSpellC2S implements IPacket {
    private final int slot;
    public CastSpellC2S(int slot) {
        this.slot = slot;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) { return; }
            Optional<SlotResult> result = CuriosApi.getCuriosHelper().findCurio(player, "spellcaster", slot);
            if (result.isEmpty()) { return; }
            ItemStack spellCaster = result.get().stack();
            if (spellCaster.getItem() instanceof SpellCasters caster) {
                if (caster.type == SpellCasters.Ability.SELF) {
                    caster.activeAbility(player);
                } else if (caster.type == SpellCasters.Ability.TARGETED) {
                    LivingEntity target = UsefulMethods.Abilities.getClosestEntity(UsefulMethods.Abilities.getTargets(player, 1.5f, (int) caster.TARGET_CAST_RANGE), player);
                    caster.targetedAbility(player, target);
                }
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeInt(slot);
    }
    public static CastSpellC2S read(FriendlyByteBuf packetBuf) {
        return new CastSpellC2S(packetBuf.readInt());
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, CastSpellC2S.class, CastSpellC2S::read);
    }
}
