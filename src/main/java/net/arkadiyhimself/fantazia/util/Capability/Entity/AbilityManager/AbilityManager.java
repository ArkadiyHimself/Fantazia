package net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager;

import com.google.common.collect.Lists;
import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.Networking.NetworkHandler;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.*;
import net.arkadiyhimself.fantazia.util.Interfaces.INBTsaver;
import net.arkadiyhimself.fantazia.util.Interfaces.IPlayerAbility;
import net.arkadiyhimself.fantazia.util.Interfaces.ITicking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class AbilityManager extends PlayerCapability {
    private final List<IPlayerAbility> ABILITIES = Lists.newArrayList();
    private final List<INBTsaver> NBT_SAVERS = Lists.newArrayList();
    private final List<ITicking> TICKING = Lists.newArrayList();
    public AbilityManager(Player player) {
        super(player);
        AbilityProvider.provide(this);
    }
    public void tick() {
        TICKING.forEach(ITicking::tick);
        updateTracking();
    }
    public void respawn() {
        ABILITIES.forEach(IPlayerAbility::respawn);
    }
    public void grantAbility(Function<Player, IPlayerAbility> ability) {
        IPlayerAbility playerOwned = ability.apply(player);
        if (hasAbility(playerOwned.getClass())) return;
        ABILITIES.add(playerOwned);
        if (playerOwned instanceof INBTsaver NBTsaver) NBT_SAVERS.add(NBTsaver);
        if (playerOwned instanceof ITicking iTicking) TICKING.add(iTicking);
    }
    public <T extends IPlayerAbility> LazyOptional<T> getAbility(Class<T> tClass) {
        T ability = takeAbility(tClass);
        return ability == null ? LazyOptional.empty() : LazyOptional.of(() -> ability);
    }
    @Nullable
    public <T extends IPlayerAbility> T takeAbility(Class<T> tClass) {
        T object = null;
        for (IPlayerAbility playerAbility : ABILITIES) {
            if (tClass == playerAbility.getClass()) {
                object = tClass.cast(playerAbility);
                break;
            }
        }
        return object;
    }
    public <T extends IPlayerAbility> boolean hasAbility(Class<T> tClass) {
        for (IPlayerAbility playerAbility : ABILITIES) {
            if (tClass.isInstance(playerAbility)) return true;
        }
        return false;
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
        NBT_SAVERS.forEach(inbTsaver -> tag.merge(inbTsaver.serialize()));
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        NBT_SAVERS.forEach(inbTsaver -> inbTsaver.deserialize(nbt));
    }
    private static class AbilityProvider {
        public static void provide(AbilityManager abilityManager) {
            abilityManager.grantAbility(Dash::new);
            abilityManager.grantAbility(DoubleJump::new);
            abilityManager.grantAbility(AttackBlock::new);
            abilityManager.grantAbility(RenderingValues::new);
            abilityManager.grantAbility(ManaData::new);
            abilityManager.grantAbility(StaminaData::new);
        }
    }
}
