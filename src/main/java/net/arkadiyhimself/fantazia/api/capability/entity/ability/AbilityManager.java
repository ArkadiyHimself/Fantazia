package net.arkadiyhimself.fantazia.api.capability.entity.ability;

import com.google.common.collect.Lists;
import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.ITalentListener;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.*;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class AbilityManager extends PlayerCapability {
    private final List<AbilityHolder> abilityHolders = Lists.newArrayList();
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
    public CompoundTag serializeNBT(boolean toDisk) {
        CompoundTag tag = new CompoundTag();

        for (AbilityHolder holder : abilityHolders) if (holder.id() != null) tag.put(holder.id(), holder.serialize(toDisk));

        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag tag, boolean fromDisk) {
        for (AbilityHolder holder : abilityHolders) if (tag.contains(holder.id())) holder.deserialize(tag.getCompound(holder.id()), fromDisk);
    }
    public void tick() {
        abilityHolders.stream().filter(ITicking.class::isInstance).forEach(abilityHolder -> ((ITicking) abilityHolder).tick());
        updateTracking();
    }
    public void onHit(LivingAttackEvent event) {
        abilityHolders.stream().filter(IDamageReacting.class::isInstance).forEach(abilityHolder -> ((IDamageReacting)abilityHolder).onHit(event));
    }
    public void onHit(LivingHurtEvent event) {
        abilityHolders.stream().filter(IDamageReacting.class::isInstance).forEach(abilityHolder -> ((IDamageReacting)abilityHolder).onHit(event));
    }
    public void onHit(LivingDamageEvent event) {
        abilityHolders.stream().filter(IDamageReacting.class::isInstance).forEach(abilityHolder -> ((IDamageReacting)abilityHolder).onHit(event));
    }
    public void respawn() {
        abilityHolders.forEach(AbilityHolder::respawn);
    }
    public void talentUnlocked(BasicTalent talent) {
        abilityHolders.stream().filter(ITalentListener.class::isInstance).forEach(abilityHolder -> ((ITalentListener) abilityHolder).onTalentUnlock(talent));
    }
    public void talentRevoked(BasicTalent talent) {
        abilityHolders.stream().filter(ITalentListener.class::isInstance).forEach(abilityHolder -> ((ITalentListener) abilityHolder).onTalentRevoke(talent));
    }
    public void grantAbility(Function<Player, AbilityHolder> ability) {
        AbilityHolder abilityHolder = ability.apply(player);
        if (hasAbility(abilityHolder.getClass())) return;
        abilityHolders.add(abilityHolder);
    }
    public <T extends AbilityHolder> LazyOptional<T> getAbility(Class<T> tClass) {
        T ability = takeAbility(tClass);
        return ability == null ? LazyOptional.empty() : LazyOptional.of(() -> ability);
    }
    @Nullable
    public <T extends AbilityHolder> T takeAbility(Class<T> tClass) {
        for (AbilityHolder playerAbility : abilityHolders) if (tClass == playerAbility.getClass()) return tClass.cast(playerAbility);
        return null;
    }
    public <T extends AbilityHolder> boolean hasAbility(Class<T> tClass) {
        for (AbilityHolder playerAbility : abilityHolders) if (tClass.isInstance(playerAbility)) return true;
        return false;
    }
    private static class AbilityProvider {
        public static void provide(AbilityManager abilityManager) {
            abilityManager.grantAbility(Dash::new);
            abilityManager.grantAbility(DoubleJump::new);
            abilityManager.grantAbility(MeleeBlock::new);
            abilityManager.grantAbility(ClientValues::new);
            abilityManager.grantAbility(ManaData::new);
            abilityManager.grantAbility(StaminaData::new);
            abilityManager.grantAbility(VibrationListen::new);
            abilityManager.grantAbility(LootTableModifiersHolder::new);
            abilityManager.grantAbility(TalentsHolder::new);
        }
    }
}
