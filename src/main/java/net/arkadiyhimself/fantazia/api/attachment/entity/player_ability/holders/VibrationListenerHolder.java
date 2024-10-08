package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.networking.packets.attachment_modify.EntityMadeSoundS2C;
import net.arkadiyhimself.fantazia.networking.packets.attachment_modify.SoundExpiredS2C;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.List;

public class VibrationListenerHolder extends PlayerAbilityHolder {
    private final HashMap<LivingEntity, Integer> REVEAL = Maps.newHashMap();
    private int delay = 0;
    public VibrationListenerHolder(Player player) {
        super(player, Fantazia.res("vibration_listener"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {

    }

    @Override
    public void respawn() {
        REVEAL.clear();
    }

    @Override
    public void tick() {
        REVEAL.forEach((livingEntity, integer) -> REVEAL.replace(livingEntity, Math.max(0, integer - 1)));
        if (REVEAL.containsValue(0)) {
            HashMap<LivingEntity, Integer> newMap = Maps.newHashMap();
            REVEAL.forEach(((entity, integer) -> {
                if (integer > 0) newMap.put(entity, integer);
                else if (getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new SoundExpiredS2C(entity.getId()));
            }));
            REVEAL.clear();
            REVEAL.putAll(newMap);
        }
        if (delay > 0) delay--;
    }



    public void madeSound(LivingEntity entity) {
        REVEAL.put(entity, 80);
        delay = 40;

        PositionSource listenerSource = new EntityPositionSource(getPlayer(), 1.5f);
        Vec3 pPos = entity.position();
        int travelTimeInTicks = Mth.floor(getPlayer().distanceToSqr(pPos)) / 4;
        if (!(getPlayer().level() instanceof ServerLevel pLevel)) return;
        pLevel.sendParticles(new VibrationParticleOption(listenerSource, travelTimeInTicks), pPos.x, pPos.y, pPos.z, 3, 0.0D, 0.0D, 0.0D, 0.0D);
        pLevel.playSound(null, pPos.x() + 0.5D, pPos.y() + 0.5D, pPos.z() + 0.5D, SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS, 1.0F, pLevel.random.nextFloat() * 0.2F + 0.8F);

        if (getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new EntityMadeSoundS2C(entity.getId()));
    }
    public List<LivingEntity> revealed() {
        List<LivingEntity> entities = Lists.newArrayList(REVEAL.keySet().iterator());

        entities.removeIf(entity -> REVEAL.containsKey(entity) && REVEAL.get(entity) <= 0 || entity == null);
        return List.copyOf(entities);
    }
    public boolean listen() {
        if (delay > 0) return false;
        return SpellHelper.hasSpell(getPlayer(), FTZSpells.SONIC_BOOM.get());
    }
    public void soundExpired(LivingEntity entity) {
        REVEAL.remove(entity);
    }
}
