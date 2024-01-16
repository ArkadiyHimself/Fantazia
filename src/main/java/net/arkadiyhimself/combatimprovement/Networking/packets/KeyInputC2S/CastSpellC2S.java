package net.arkadiyhimself.combatimprovement.Networking.packets.KeyInputC2S;

import dev._100media.capabilitysyncer.network.IPacket;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.combatimprovement.Registries.Items.MagicCasters.ActiveAndTargeted.SpellCaster;
import net.arkadiyhimself.combatimprovement.Registries.SoundRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
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
            if (spellCaster.getItem() instanceof SpellCaster caster) {
                if (caster.type == SpellCaster.Ability.SELF) {
                    if (caster.conditionNotMet(player) || caster.hasCooldown(player)) {
                        NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DENIED.get()), context.getSender());
                        return;
                    }
                    AttachDataSync.get(player).ifPresent(dataSync -> {
                        boolean use = dataSync.wasteMana(caster.MANACOST);
                        if (use) {
                            caster.activeAbility(player);
                        } else {
                            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DENIED.get()), player);
                        }
                    });
                } else if (caster.type == SpellCaster.Ability.TARGETED) {  // using else-if in case I add new types of abilities
                    float castRange = (float) (caster.getCastRange(context.getSender()));
                    LivingEntity target = WhereMagicHappens.Abilities.getClosestEntity(WhereMagicHappens.Abilities.getTargets(player, 1f, (int) castRange, caster.goThruWalls), player);
                    if (!caster.targetConditions(player, target) || caster.hasCooldown(player) || target == null) {
                        NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DENIED.get()), context.getSender());
                        return;
                    }
                    AttachDataSync.get(player).ifPresent(dataSync -> {
                        boolean use = dataSync.wasteMana(caster.MANACOST);
                        if (use) {
                            caster.targetedAbility(player, target, false);
                        } else {
                            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DENIED.get()), player);
                        }
                    });
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
