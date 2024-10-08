package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class ClientValuesHolder extends PlayerAbilityHolder {
    public ClientValuesHolder(Player player) {
        super(player, Fantazia.res("client_values"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        tag.putInt("tauntTicks", tauntTicks);

        tag.putFloat("mirrorLayerSize", mirrorLayerSize);
        tag.putFloat("mirrorLayerVis", mirrorLayerVis);
        tag.putBoolean("showMirrorLayer", showMirrorLayer);

        tag.putInt("lastWisdom", lastWisdom);
        tag.putInt("wisdomTick", wisdomTick);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        mirrorLayerSize = compoundTag.contains("mirrorLayerSize") ? compoundTag.getFloat("mirrorLayerSize") : 1f;
        mirrorLayerVis = compoundTag.contains("mirrorLayerVis") ? compoundTag.getFloat("mirrorLayerVis") : 1f;
        showMirrorLayer = compoundTag.contains("showMirrorLayer") && compoundTag.getBoolean("showMirrorLayer");

        tauntTicks = compoundTag.contains("tauntTicks") ? compoundTag.getInt("tauntTicks") : 0;

        lastWisdom = compoundTag.contains("lastWisdom") ? compoundTag.getInt("lastWisdom") : 0;
        wisdomTick = compoundTag.contains("wisdomTick") ? compoundTag.getInt("wisdomTick") : 0;
    }

    @Override
    public CompoundTag syncSerialize() {
        return serializeNBT(getPlayer().registryAccess());
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        deserializeNBT(getPlayer().registryAccess(), tag);
    }

    @Override
    public void respawn() {
        tauntTicks = 0;
    }

    @Override
    public void tick() {
        if (showMirrorLayer) {
            mirrorLayerSize = Math.min(3f, mirrorLayerSize + 0.25f);
            mirrorLayerVis = Math.max(0, mirrorLayerSize - 0.05f);
        }
        if (mirrorLayerSize == 3f) showMirrorLayer = false;
        if (tauntTicks > 0) tauntTicks--;
        if (wisdomTick > 0) wisdomTick--;
    }

    // mystic mirror layer
    public float mirrorLayerSize = 1f;
    public float mirrorLayerVis = 1f;
    public boolean showMirrorLayer = false;
    public void onMirrorActivation() {
        mirrorLayerSize = 1f;
        mirrorLayerVis = 1f;
        showMirrorLayer = true;
    }

    // stuff
    private int tauntTicks = 0;
    public void taunt() {
        tauntTicks = 30;
    }
    public boolean isTaunting() {
        return tauntTicks > 0;
    }
    // wisdom obtained
    private int lastWisdom = 0;
    private int wisdomTick = 0;
    public void obtainedWisdom(int value) {
        lastWisdom = value;
        if (value > 0) wisdomTick = 60;
    }
    public int getLastWisdom() {
        return lastWisdom;
    }
    public int getWisdomTick() {
        return wisdomTick;
    }
}
