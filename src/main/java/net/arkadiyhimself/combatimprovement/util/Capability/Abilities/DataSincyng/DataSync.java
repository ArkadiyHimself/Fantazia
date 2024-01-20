package net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng;

import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.api.AttributeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;

public class DataSync extends PlayerCapability {
    public DataSync(Player player) { super(player); }
    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(livingEntity.getId(), AttachDataSync.DATA_SYNC_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() { return NetworkHandler.INSTANCE; }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("veinTR", veinTR);
        tag.putFloat("allTR", allTR);

        tag.putInt("heartbeat", heartbeat);

        tag.putDouble("dX", dX);
        tag.putDouble("dY", dY);
        tag.putDouble("dZ", dZ);

        tag.putFloat("mana", mana);

        tag.putFloat("stamina", stamina);

        tag.putBoolean("philStoned", philStoned);

        tag.putFloat("mirrorLayerSize", mirrorLayerSize);
        tag.putFloat("mirrorLayerVis", mirrorLayerVis);
        tag.putBoolean("showMirrorLayer", showMirrorLayer);
        tag.putInt("vibrationCooldown", vibrationCooldown);

        tag.putInt("tauntTicks", tauntTicks);
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        veinTR = nbt.contains("veinTR") ? nbt.getFloat("veinTR") : 0;
        allTR = nbt.contains("allTR") ? nbt.getFloat("allTR") : 1;

        heartbeat = nbt.contains("heartbeat") ? nbt.getInt("heartbeat") : 0;

        dX = nbt.contains("dX") ? nbt.getDouble("dX") : 0;
        dY = nbt.contains("dY") ? nbt.getDouble("dY") : 0;
        dZ = nbt.contains("dZ") ? nbt.getDouble("dZ") : 0;

        mana = nbt.contains("mana") ? nbt.getFloat("mana") : 0;

        stamina = nbt.contains("stamina") ? nbt.getFloat("stamina") : 0;

        philStoned = nbt.contains("philStoned") ? nbt.getBoolean("philStoned") : false;

        mirrorLayerSize = nbt.contains("mirrorLayerSize") ? nbt.getFloat("mirrorLayerSize") : 1f;
        mirrorLayerVis = nbt.contains("mirrorLayerVis") ? nbt.getFloat("mirrorLayerVis") : 1f;
        showMirrorLayer = nbt.contains("showMirrorLayer") ? nbt.getBoolean("showMirrorLayer") : false;

        vibrationCooldown = nbt.contains("vibrationCooldown") ? nbt.getInt("vibrationCooldown") : 0;

        tauntTicks = nbt.contains("tauntTicks") ? nbt.getInt("tauntTicks") : 0;
    }
    public final HashMap<LivingEntity, Integer> madeSound = new HashMap<>() {
    };
    public void tick() {
        if (player.level().isClientSide()) {
            madeSound.forEach((livingEntity, integer) -> {
                madeSound.replace(livingEntity, Math.max(0, integer - 1));
            });
        } else {
            vibrationCooldown = Math.max(0, vibrationCooldown - 1);
            mana = Math.min(getMaxMana(), mana + getManaRegen());
            if (!player.isSprinting()) {
                stRegenDelay = Math.max(0, stRegenDelay - 1);
            }
            if (stRegenDelay <= 0) {
                stamina = Math.min(getMaxStamina(), stamina + getStaminaRegen());
            }
            if (showMirrorLayer) {
                mirrorLayerSize = Math.min(3f, mirrorLayerSize + 0.25f);
                mirrorLayerVis = Math.max(0, mirrorLayerSize - 0.05f);
            }
            if (mirrorLayerSize == 3f) {
                showMirrorLayer = false;
            }
            tauntTicks = Math.max(0, tauntTicks - 1);
        }
        updateTracking();
    }
    public void entityMadeSound(LivingEntity entity) {
        madeSound.put(entity, 80);
    }
    public void entitySoundExpired(LivingEntity entity) {
        madeSound.remove(entity);
        updateTracking();
    }
    public void onRespawn() {
        mana = getMaxMana();
        stamina = getMaxStamina();
        updateTracking();
    }
    public void onMirrorActivation() {
        showMirrorLayer = true;
        mirrorLayerSize = 1f;
        mirrorLayerVis = 1f;
        updateTracking();
    }
    // fury heartbeat syncing
    private float veinTR = 0;
    private float allTR = 0;
    public int heartbeat = 0;
    public float getAllTR() { return allTR; }
    public void setAllTR(float allTR) { this.allTR = allTR; }
    public float getVeinTR() { return veinTR; }
    public void setVeinTR(float veinTR) { this.veinTR = veinTR; }

    // delta movement syncing
    public Vec3 getDeltaMovement() {
        return new Vec3(dX, dY, dZ);
    }
    public void setDeltaMovement(Vec3 movement) {
        dX = movement.x();
        dY = movement.y();
        dZ = movement.z();
        updateTracking();
    }
    public double dX;
    public double dY;
    public double dZ;

    // mana
    public float mana;
    public float manaRegenBasic = 0.00575f;
    public boolean wasteMana(float amount) {
        if (player.isCreative()) { return true; }
        float newAmount = mana - amount;
        if (newAmount >= 0) {
            mana = newAmount;
        } else {
            return false;
        }
        updateTracking();
        return true;
    }
    public float getMaxMana() {
        return (float) player.getAttributeValue(AttributeRegistry.MAX_MANA.get());
    }
    public float getManaRegen() {
        float regen = manaRegenBasic;
        FoodData data = player.getFoodData();
        if (data.getFoodLevel() == 20) {
            if (data.getSaturationLevel() >= 10) {
                regen *= 1.45f;
            } else {
                regen *= 1.25f;
            }
        } else if (data.getFoodLevel() <= 7.5f) {
            regen *= 0.675f;
            if (data.getFoodLevel() <= 3f) {
                regen *= 0.5f;
            }
        }
        return regen;
    }
    // stamina
    public final float STAMINA_REGEN_DELAY = 40;
    public float stamina;
    public float stRegenBasic = 0.1125f;
    public float stRegenDelay = 60;
    public void wasteStamina(float cost, boolean addDelay) {
        wasteStamina(cost, addDelay, STAMINA_REGEN_DELAY);
    }
    public void wasteStamina(float cost, boolean addDelay, float delay) {
        if (player.isCreative()) { return; }
        stamina = Math.max(0, stamina - cost);
        if (addDelay) {
            stRegenDelay = Math.max(stRegenDelay, delay);
        }
    }
    public float getMaxStamina() {
        return (float) player.getAttributeValue(AttributeRegistry.MAX_STAMINA.get());
    }
    public float getStaminaRegen() {
        float regen = stRegenBasic;
        FoodData data = player.getFoodData();
        if (data.getFoodLevel() >= 17.5f) {
            if (data.getSaturationLevel() >= 10) {
                regen *= 1.45f;
            } else {
                regen *= 1.25f;
            }
        } else if (data.getFoodLevel() <= 7.5f) {
            regen *= 0.675f;
            if (data.getFoodLevel() <= 3f) {
                regen *= 0.5f;
            }
        }
        if (dX == 0 && dZ == 0) {
            regen += 0.125f;
        }
        regen *= player.getAttributeValue(AttributeRegistry.STAMINA_REGEN_MULTIPLIER.get());
        return regen;
    }
    // stuff
    public boolean philStoned = true;
    public float mirrorLayerSize = 1f;
    public float mirrorLayerVis = 1f;
    public boolean showMirrorLayer = false;
    public int vibrationCooldown = 0;
    public int tauntTicks = 0;
}
