package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataManager;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectManager;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.*;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributes;
import net.arkadiyhimself.fantazia.api.type.entity.*;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlayerAbilityManager implements IHolderManager<IPlayerAbility, Player> {

    private final Map<Class<? extends IPlayerAbility>, IPlayerAbility> holders = Maps.newHashMap();

    private final Player player;

    public PlayerAbilityManager(IAttachmentHolder holder) {
        this.player = holder instanceof Player plyr ? plyr : null;
        provide();
    }

    @Override
    public Player getOwner() {
        return this.player;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (IPlayerAbility holder : holders.values()) tag.put(holder.id().toString(), holder.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        for (IPlayerAbility holder : holders.values()) if (compoundTag.contains(holder.id().toString())) holder.deserializeNBT(provider, compoundTag.getCompound(holder.id().toString()));
    }

    @Override
    public <I extends IPlayerAbility> void putHolder(Function<Player, I> holder) {
        if (this.player == null) return;
        IPlayerAbility iPlayerAbility = holder.apply(this.player);
        if (hasHolder(iPlayerAbility.getClass())) return;
        holders.put(iPlayerAbility.getClass(), iPlayerAbility);
    }

    @Nullable
    public <T extends IPlayerAbility> T actualHolder(Class<T> tClass) {
        for (IPlayerAbility iPlayerAbility : holders.values()) if (tClass == iPlayerAbility.getClass()) return tClass.cast(iPlayerAbility);
        return null;
    }

    @Override
    public <I extends IPlayerAbility> Optional<I> optionalHolder(Class<I> iClass) {
        I ability = actualHolder(iClass);
        return ability == null ? Optional.empty() : Optional.of(ability);
    }

    @Override
    public <I extends IPlayerAbility> boolean hasHolder(Class<I> iClass) {
        return holders.containsKey(iClass);
    }

    @Override
    public CompoundTag syncSerialize() {
        CompoundTag tag = new CompoundTag();
        for (IPlayerAbility holder : holders.values()) tag.put(holder.id().toString(), holder.syncSerialize());
        return tag;
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        for (IPlayerAbility holder : holders.values()) if (tag.contains(holder.id().toString())) holder.syncDeserialize(tag.getCompound(holder.id().toString()));
    }

    public void tick() {
        holders.values().forEach(IBasicHolder::tick);
    }

    public void onHit(LivingIncomingDamageEvent event) {
        for (IPlayerAbility iPlayerAbility : holders.values()) if (iPlayerAbility instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Pre event) {
        for (IPlayerAbility iPlayerAbility : holders.values()) if (iPlayerAbility instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Post event) {
        for (IPlayerAbility iPlayerAbility : holders.values()) if (iPlayerAbility instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void respawn() {
        holders.values().forEach(IPlayerAbility::respawn);
    }

    public void talentUnlocked(BasicTalent talent) {
        for (IPlayerAbility iPlayerAbility : holders.values()) if (iPlayerAbility instanceof ITalentListener listener) listener.onTalentUnlock(talent);
    }

    public void talentRevoked(BasicTalent talent) {
        for (IPlayerAbility iPlayerAbility : holders.values()) if (iPlayerAbility instanceof ITalentListener listener) listener.onTalentRevoke(talent);
    }

    public void provide() {
        putHolder(DashHolder::new);
        putHolder(DoubleJumpHolder::new);
        putHolder(MeleeBlockHolder::new);
        putHolder(ClientValuesHolder::new);
        putHolder(ManaHolder::new);
        putHolder(StaminaHolder::new);
        putHolder(VibrationListenerHolder::new);
        putHolder(LootTableModifiersHolder::new);
        putHolder(TalentsHolder::new);
    }
}
