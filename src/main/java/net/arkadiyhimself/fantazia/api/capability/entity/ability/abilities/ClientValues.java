package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ClientValues extends AbilityHolder implements ITicking {
    private static final String ID = "rendering_values:";
    public ClientValues(Player player) {
        super(player);
    }
    @Override
    public void tick() {
        if (showMirrorLayer) {
            mirrorLayerSize = Math.min(3f, mirrorLayerSize + 0.25f);
            mirrorLayerVis = Math.max(0, mirrorLayerSize - 0.05f);
        }
        if (mirrorLayerSize == 3f) {
            showMirrorLayer = false;
        }
        tauntTicks = Math.max(0, tauntTicks - 1);
    }
    @Override
    public void respawn() {
        tauntTicks = 0;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        tag.putDouble(ID + "dX", deltaMovement.x());
        tag.putDouble(ID + "dY", deltaMovement.y());
        tag.putDouble(ID + "dZ", deltaMovement.z());

        tag.putFloat(ID + "mirrorLayerSize", mirrorLayerSize);
        tag.putFloat(ID + "mirrorLayerVis", mirrorLayerVis);
        tag.putBoolean(ID + "showMirrorLayer", showMirrorLayer);

        tag.putInt(ID + "tauntTicks", tauntTicks);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag) {
        double dX = tag.contains(ID + "dX") ? tag.getDouble(ID + "dX") : 0;
        double dY = tag.contains(ID + "dY") ? tag.getDouble(ID + "dY") : 0;
        double dZ = tag.contains(ID + "dZ") ? tag.getDouble(ID + "dZ") : 0;
        deltaMovement = new Vec3(dX, dY, dZ);

        mirrorLayerSize = tag.contains(ID + "mirrorLayerSize") ? tag.getFloat(ID + "mirrorLayerSize") : 1f;
        mirrorLayerVis = tag.contains(ID + "mirrorLayerVis") ? tag.getFloat(ID + "mirrorLayerVis") : 1f;
        showMirrorLayer = tag.contains(ID + "showMirrorLayer") && tag.getBoolean(ID + "showMirrorLayer");

        tauntTicks = tag.contains(ID + "tauntTicks") ? tag.getInt(ID + "tauntTicks") : 0;
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
    public Vec3 deltaMovement = new Vec3(0,0,0);
    private int tauntTicks = 0;
    public void taunt() {
        tauntTicks = 30;
    }
    public boolean isTaunting() {
        return tauntTicks > 0;
    }
}
