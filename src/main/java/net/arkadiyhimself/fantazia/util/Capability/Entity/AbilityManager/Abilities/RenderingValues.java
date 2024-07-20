package net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Networking.NetworkHandler;
import net.arkadiyhimself.fantazia.Networking.packets.CapabilityUpdate.SoundExpiredS2C;
import net.arkadiyhimself.fantazia.util.Interfaces.INBTsaver;
import net.arkadiyhimself.fantazia.util.Interfaces.IPlayerAbility;
import net.arkadiyhimself.fantazia.util.Interfaces.ITicking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;

public class RenderingValues implements IPlayerAbility, INBTsaver, ITicking {
    private static final String ID = "rendering_values:";
    private final Player owner;
    public RenderingValues(Player owner) {
        this.owner = owner;
    }

    // glowing entities with sculk heart
    private final HashMap<LivingEntity, Integer> madeSound = Maps.newHashMap();
    public int vibr_cd = 0;
    public void madeSound(LivingEntity entity) {
        madeSound.put(entity, 80);
    }
    public List<LivingEntity> emittedSound() {
        List<LivingEntity> entities = new java.util.ArrayList<>(madeSound.keySet().stream().toList());
        entities.removeIf(entity -> madeSound.containsKey(entity) && madeSound.get(entity) <= 0);
        return ImmutableList.copyOf(entities);
    }
    public void soundExpired(LivingEntity entity) {
        madeSound.remove(entity);
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

    // fury heartbeat syncing
    private float vein = 0;
    private float back = 0;
    public int heartbeat = 0;
    public float getBack() {
        return back;
    }
    public void setBack(float allTR) {
        this.back = allTR;
    }
    public float getVein() {
        return vein;
    }
    public void setVein(float veinTR) {
        this.vein = veinTR;
    }

    // stuff
    public Vec3 deltaMovement = new Vec3(0,0,0);
    public int tauntTicks = 0;
    public void taunt() {
        tauntTicks = 20;
    }
    @Override
    public void tick() {
        madeSound.forEach((livingEntity, integer) -> madeSound.replace(livingEntity, Math.max(0, integer - 1)));
        if (madeSound.containsValue(0)) {
            HashMap<LivingEntity, Integer> newMap = Maps.newHashMap();
            madeSound.forEach(((entity, integer) -> {
                if (integer > 0) newMap.put(entity, integer);
                else NetworkHandler.sendToPlayer(new SoundExpiredS2C(entity), owner);
            }));
            madeSound.clear();
            madeSound.putAll(newMap);
        }
        vibr_cd = Math.max(0, vibr_cd - 1);
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
    public Player getOwner() {
        return owner;
    }
    @Override
    public void respawn() {
        madeSound.forEach((entity, integer) -> NetworkHandler.sendToPlayer(new SoundExpiredS2C(entity), owner));
        madeSound.clear();
        tauntTicks = 0;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        tag.putFloat(ID + "vein", vein);
        tag.putFloat(ID + "back", back);

        tag.putInt(ID + "heartbeat", heartbeat);

        tag.putDouble(ID + "dX", deltaMovement.x());
        tag.putDouble(ID + "dY", deltaMovement.y());
        tag.putDouble(ID + "dZ", deltaMovement.z());

        tag.putFloat(ID + "mirrorLayerSize", mirrorLayerSize);
        tag.putFloat(ID + "mirrorLayerVis", mirrorLayerVis);
        tag.putBoolean(ID + "showMirrorLayer", showMirrorLayer);

        tag.putInt(ID + "vibr_cd", vibr_cd);

        tag.putInt(ID + "tauntTicks", tauntTicks);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag) {
        vein = tag.contains(ID + "vein") ? tag.getFloat(ID + "vein") : 0;
        back = tag.contains(ID + "back") ? tag.getFloat(ID + "back") : 1;

        heartbeat = tag.contains(ID + "heartbeat") ? tag.getInt(ID + "heartbeat") : 0;

        double dX = tag.contains(ID + "dX") ? tag.getDouble(ID + "dX") : 0;
        double dY = tag.contains(ID + "dY") ? tag.getDouble(ID + "dY") : 0;
        double dZ = tag.contains(ID + "dZ") ? tag.getDouble(ID + "dZ") : 0;
        deltaMovement = new Vec3(dX, dY, dZ);

        mirrorLayerSize = tag.contains(ID + "mirrorLayerSize") ? tag.getFloat(ID + "mirrorLayerSize") : 1f;
        mirrorLayerVis = tag.contains(ID + "mirrorLayerVis") ? tag.getFloat(ID + "mirrorLayerVis") : 1f;
        showMirrorLayer = tag.contains(ID + "showMirrorLayer") && tag.getBoolean(ID + "showMirrorLayer");

        vibr_cd = tag.contains(ID + "vibr_cd") ? tag.getInt(ID + "vibr_cd") : 0;

        tauntTicks = tag.contains(ID + "tauntTicks") ? tag.getInt(ID + "tauntTicks") : 0;
    }
}
