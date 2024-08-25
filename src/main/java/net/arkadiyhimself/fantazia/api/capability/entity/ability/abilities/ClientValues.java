package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class ClientValues extends AbilityHolder implements ITicking {
    public ClientValues(Player player) {
        super(player);
    }
    @Override
    public String ID() {
        return "client_values";
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
    @Override
    public void respawn() {
        tauntTicks = 0;
    }

    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();

        // must be saved
        tag.putInt("tauntTicks", tauntTicks);

        // not so important
        if (toDisk) return tag;
        tag.putFloat("mirrorLayerSize", mirrorLayerSize);
        tag.putFloat("mirrorLayerVis", mirrorLayerVis);
        tag.putBoolean("showMirrorLayer", showMirrorLayer);

        tag.putInt("lastWisdom", lastWisdom);
        tag.putInt("wisdomTick", wisdomTick);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        mirrorLayerSize = tag.contains("mirrorLayerSize") ? tag.getFloat("mirrorLayerSize") : 1f;
        mirrorLayerVis = tag.contains("mirrorLayerVis") ? tag.getFloat("mirrorLayerVis") : 1f;
        showMirrorLayer = tag.contains("showMirrorLayer") && tag.getBoolean("showMirrorLayer");

        tauntTicks = tag.contains("tauntTicks") ? tag.getInt("tauntTicks") : 0;

        lastWisdom = tag.contains("lastWisdom") ? tag.getInt("lastWisdom") : 0;
        wisdomTick = tag.contains("wisdomTick") ? tag.getInt("wisdomTick") : 0;
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
