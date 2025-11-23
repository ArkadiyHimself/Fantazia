package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.common.registries.custom.Spells;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;
import java.util.UUID;

public class VibrationListenerHolder extends PlayerAbilityHolder {

    private final Map<Integer, Integer> REVEALED_CLIENT = Maps.newHashMap();
    private final Map<UUID, Integer> REVEALED_SERVER = Maps.newHashMap();
    private int delay = 0;

    public VibrationListenerHolder(Player player) {
        super(player, Fantazia.location("vibration_listener"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("delay", delay);

        ListTag listTag = new ListTag();
        for (Map.Entry<UUID, Integer> entry : REVEALED_SERVER.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("id", entry.getKey());
            entryTag.putInt("ticks", entry.getValue());
            listTag.add(entryTag);
        }
        tag.put("revealed", listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        this.delay = tag.getInt("delay");

        REVEALED_SERVER.clear();
        ListTag listTag = tag.getList("revealed", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag entryTag = listTag.getCompound(i);
            REVEALED_SERVER.put(entryTag.getUUID("id"), entryTag.getInt("ticks"));
        }
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("delay", delay);

        ListTag listTag = new ListTag();
        if (!(getPlayer().level() instanceof ServerLevel serverLevel)) return tag;
        for (Map.Entry<UUID, Integer> entry : REVEALED_SERVER.entrySet()) {
            Entity entity = serverLevel.getEntity(entry.getKey());
            if (!(entity instanceof LivingEntity livingEntity)) continue;

            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("id", livingEntity.getId());
            entryTag.putInt("ticks", entry.getValue());
            listTag.add(entryTag);
        }
        tag.put("revealed", listTag);
        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        this.delay = tag.getInt("delay");

        REVEALED_CLIENT.clear();
        ListTag listTag = tag.getList("revealed", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag entryTag = listTag.getCompound(i);
            REVEALED_CLIENT.put(entryTag.getInt("id"), entryTag.getInt("ticks"));
        }
    }

    @Override
    public void respawn() {
        REVEALED_SERVER.clear();
    }

    @Override
    public void serverTick() {
        REVEALED_SERVER.forEach((livingEntity, integer) -> REVEALED_SERVER.replace(livingEntity, Math.max(0, integer - 1)));
        if (delay > 0) delay--;
    }

    @Override
    public void clientTick() {
        REVEALED_CLIENT.forEach((livingEntity, integer) -> REVEALED_CLIENT.replace(livingEntity, Math.max(0, integer - 1)));
        if (delay > 0) delay--;
    }

    public void madeSound(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            REVEALED_CLIENT.remove(entity.getId());
            REVEALED_CLIENT.put(entity.getId(), 75);
        } else {
            REVEALED_SERVER.remove(entity.getUUID());
            REVEALED_SERVER.put(entity.getUUID(), 75);
        }
        AttributeInstance instance = getPlayer().getAttribute(FTZAttributes.RECHARGE_MULTIPLIER);
        delay = (int) (40f * (instance == null ? 1f : instance.getValue() / 100));
        if (Fantazia.DEVELOPER_MODE) Fantazia.LOGGER.info("Vibration listen cooldown: " + delay + ", " + entity.level().isClientSide());

        PositionSource listenerSource = new EntityPositionSource(getPlayer(), 1.5f);
        Vec3 pPos = entity.position();
        int travelTimeInTicks = Mth.floor(getPlayer().distanceToSqr(pPos)) / 4;
        if (!(getPlayer().level() instanceof ServerLevel pLevel)) return;
        pLevel.sendParticles(new VibrationParticleOption(listenerSource, travelTimeInTicks), pPos.x, pPos.y, pPos.z, 3, 0.0D, 0.0D, 0.0D, 0.0D);
        pLevel.playSound(null, pPos.x() + 0.5D, pPos.y() + 0.5D, pPos.z() + 0.5D, FTZSoundEvents.SONIC_BOOM_CLICKING.value(), SoundSource.BLOCKS, 1.0F, pLevel.random.nextFloat() * 0.2F + 0.8F);

        if (getPlayer() instanceof ServerPlayer serverPlayer) IPacket.entityMadeSound(serverPlayer, entity);
    }

    public boolean isRevealed(LivingEntity entity) {
        return REVEALED_CLIENT.getOrDefault(entity.getId(), 0) != 0;
    }

    public boolean listen() {
        if (delay > 0) return false;
        return SpellHelper.spellAvailable(getPlayer(), Spells.SONIC_BOOM);
    }
}
