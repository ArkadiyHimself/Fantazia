package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability;

import net.arkadiyhimself.fantazia.api.type.entity.IPlayerAbility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerAbilityHolder implements IPlayerAbility {
    private final @NotNull Player player;
    private final @NotNull ResourceLocation id;
    protected PlayerAbilityHolder(@NotNull Player player, @NotNull ResourceLocation id) {
        this.player = player;
        this.id = id;
    }
    @Override
    public final @NotNull ResourceLocation id() {
        return this.id;
    }

    @Override
    public final @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public void respawn() {
    }

    @Override
    public CompoundTag syncSerialize() {
        return serializeNBT(getPlayer().registryAccess());
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        deserializeNBT(getPlayer().registryAccess(), tag);
    }
}
