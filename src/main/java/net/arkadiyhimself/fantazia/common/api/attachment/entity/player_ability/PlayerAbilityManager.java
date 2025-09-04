package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability;

import net.arkadiyhimself.fantazia.common.api.attachment.IBasicHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.IHolderManager;
import net.arkadiyhimself.fantazia.common.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.*;
import net.arkadiyhimself.fantazia.networking.attachment_syncing.IAttachmentSync;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class PlayerAbilityManager implements IHolderManager<IPlayerAbility, Player> {

    private final List<IPlayerAbility> playerAbilities = Lists.newArrayList();
    private final Player player;

    public PlayerAbilityManager(IAttachmentHolder holder) {
        this.player = holder instanceof Player plyr ? plyr : null;
        provide();
    }

    @Override
    public Player getEntity() {
        return this.player;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (IPlayerAbility holder : playerAbilities) tag.put(holder.id().toString(), holder.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        for (IPlayerAbility holder : playerAbilities) if (tag.contains(holder.id().toString())) holder.deserializeNBT(provider, tag.getCompound(holder.id().toString()));
    }

    @Override
    public <I extends IPlayerAbility> void putHolder(Function<Player, I> holder) {
        if (this.player == null) return;
        IPlayerAbility iPlayerAbility = holder.apply(this.player);
        if (!hasHolder(iPlayerAbility.getClass())) playerAbilities.add(iPlayerAbility);
    }

    @Nullable
    public <T extends IPlayerAbility> T actualHolder(Class<T> tClass) {
        for (IPlayerAbility iPlayerAbility : playerAbilities) if (tClass == iPlayerAbility.getClass()) return tClass.cast(iPlayerAbility);
        return null;
    }

    @Override
    public <I extends IPlayerAbility> Optional<I> optionalHolder(Class<I> iClass) {
        I ability = actualHolder(iClass);
        return ability == null ? Optional.empty() : Optional.of(ability);
    }

    @Override
    public <I extends IPlayerAbility> boolean hasHolder(Class<I> iClass) {
        return actualHolder(iClass) != null;
    }

    public CompoundTag serializeTick() {
        CompoundTag tag = new CompoundTag();
        for (IPlayerAbility holder : playerAbilities) if (holder instanceof ISyncEveryTick syncEveryTick) tag.put(holder.id().toString(), syncEveryTick.serializeTick());
        return tag;
    }

    public void deserializeTick(CompoundTag tag) {
        for (IPlayerAbility holder : playerAbilities) if (holder instanceof ISyncEveryTick syncEveryTick) syncEveryTick.deserializeTick(tag.getCompound(holder.id().toString()));
    }

    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        for (IPlayerAbility holder : playerAbilities) tag.put(holder.id().toString(), holder.serializeInitial());
        return tag;
    }

    public void deserializeInitial(CompoundTag tag) {
        for (IPlayerAbility holder : playerAbilities) if (tag.contains(holder.id().toString())) holder.deserializeInitial(tag.getCompound(holder.id().toString()));
    }

    public void tick() {
        if (getEntity().level().isClientSide()) playerAbilities.forEach(IBasicHolder::clientTick);
        else playerAbilities.forEach(IBasicHolder::serverTick);
    }

    public void onHit(LivingIncomingDamageEvent event) {
        for (IPlayerAbility iPlayerAbility : playerAbilities) if (iPlayerAbility instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Pre event) {
        for (IPlayerAbility iPlayerAbility : playerAbilities) if (iPlayerAbility instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onHit(LivingDamageEvent.Post event) {
        for (IPlayerAbility iPlayerAbility : playerAbilities) if (iPlayerAbility instanceof IDamageEventListener listener) listener.onHit(event);
    }

    public void onCurioEquip(ItemStack stack) {
        for (IPlayerAbility iPlayerAbility : playerAbilities) if (iPlayerAbility instanceof ICurioListener listener) listener.onCurioEquip(stack);
    }

    public void onCurioUnEquip(ItemStack stack) {
        for (IPlayerAbility iPlayerAbility : playerAbilities) if (iPlayerAbility instanceof ICurioListener listener) listener.onCurioUnEquip(stack);
    }

    public void onChangeDimension(ResourceKey<Level> from, ResourceKey<Level> to) {
        for (IPlayerAbility iPlayerAbility : playerAbilities) if (iPlayerAbility instanceof IDimensionChangeListener listener) listener.onChangeDimension(from, to);
    }

    public void respawn() {
        playerAbilities.forEach(IPlayerAbility::respawn);
        if (getEntity() instanceof ServerPlayer serverPlayer) IAttachmentSync.onEntityJoinLevel(serverPlayer);
    }

    private void provide() {
        putHolder(DashHolder::new);
        putHolder(DoubleJumpHolder::new);
        putHolder(MeleeBlockHolder::new);
        putHolder(ManaHolder::new);
        putHolder(StaminaHolder::new);
        putHolder(VibrationListenerHolder::new);
        putHolder(LootTableModifiersHolder::new);
        putHolder(TalentsHolder::new);
        putHolder(SpellInstancesHolder::new);
        putHolder(OwnedAurasHolder::new);
        putHolder(CustomCriteriaHolder::new);
        putHolder(EuphoriaHolder::new);
    }
}
