package net.arkadiyhimself.fantazia.advanced.capability.entity.ability;

import com.google.common.collect.Lists;
import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.abilities.*;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.util.interfaces.ITicking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class AbilityManager extends PlayerCapability {
    private final List<AbilityHolder> ABILITIES = Lists.newArrayList();
    public AbilityManager(Player player) {
        super(player);
        AbilityProvider.provide(this);
    }
    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.player.getId(), AbilityGetter.ABILITY_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }
    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        ABILITIES.forEach(ability -> tag.merge(ability.serialize()));
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        ABILITIES.forEach(ability -> ability.deserialize(nbt));
    }
    public void tick() {
        ABILITIES.forEach(abilityHolder -> {
            if (abilityHolder instanceof ITicking ticking) ticking.tick();
        });
        updateTracking();
    }
    public void respawn() {
        ABILITIES.forEach(AbilityHolder::respawn);
    }
    public void grantAbility(Function<Player, AbilityHolder> ability) {
        AbilityHolder abilityHolder = ability.apply(player);
        if (hasAbility(abilityHolder.getClass())) return;
        ABILITIES.add(abilityHolder);
    }
    public <T extends AbilityHolder> LazyOptional<T> getAbility(Class<T> tClass) {
        T ability = takeAbility(tClass);
        return ability == null ? LazyOptional.empty() : LazyOptional.of(() -> ability);
    }
    @Nullable
    public <T extends AbilityHolder> T takeAbility(Class<T> tClass) {
        for (AbilityHolder playerAbility : ABILITIES) if (tClass == playerAbility.getClass()) return tClass.cast(playerAbility);
        return null;
    }
    public <T extends AbilityHolder> boolean hasAbility(Class<T> tClass) {
        for (AbilityHolder playerAbility : ABILITIES) if (tClass.isInstance(playerAbility)) return true;
        return false;
    }
    private static class AbilityProvider {
        public static void provide(AbilityManager abilityManager) {
            abilityManager.grantAbility(Dash::new);
            abilityManager.grantAbility(DoubleJump::new);
            abilityManager.grantAbility(MeleeBlock::new);
            abilityManager.grantAbility(RenderingValues::new);
            abilityManager.grantAbility(ManaData::new);
            abilityManager.grantAbility(StaminaData::new);
            abilityManager.grantAbility(VibrationListen::new);
        }
    }
}
