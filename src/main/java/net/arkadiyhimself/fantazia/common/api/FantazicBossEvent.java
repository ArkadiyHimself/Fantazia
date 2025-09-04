package net.arkadiyhimself.fantazia.common.api;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.arkadiyhimself.fantazia.networking.fantazic_boss_event.FantazicBossEventPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Function;

public class FantazicBossEvent extends ServerBossEvent {

    private float barrier = 0f;
    private final Set<ServerPlayer> players = Sets.newHashSet();

    public FantazicBossEvent(Component name, BossBarColor color, BossBarOverlay overlay) {
        super(name, color, overlay);
    }

    public float getBarrier() {
        return barrier;
    }

    public void setBarrier(float barrier) {
        this.barrier = barrier;
    }

    public void setProgress(float health, float barrier) {
        if (health != this.progress || barrier != this.barrier) {
            this.progress = health;
            this.barrier = barrier;
            this.broadcast(FantazicBossEventPacket::createUpdateProgressPacket);
        }
    }

    public void setColor(BossEvent.@NotNull BossBarColor color) {
        if (color != this.color) {
            super.setColor(color);
            this.broadcast(FantazicBossEventPacket::createUpdateStylePacket);
        }

    }

    public void setOverlay(BossEvent.@NotNull BossBarOverlay overlay) {
        if (overlay != this.overlay) {
            super.setOverlay(overlay);
            this.broadcast(FantazicBossEventPacket::createUpdateStylePacket);
        }

    }

    public @NotNull FantazicBossEvent setDarkenScreen(boolean darkenSky) {
        if (darkenSky != this.darkenScreen) {
            super.setDarkenScreen(darkenSky);
            this.broadcast(FantazicBossEventPacket::createUpdatePropertiesPacket);
        }

        return this;
    }

    public @NotNull FantazicBossEvent setPlayBossMusic(boolean playEndBossMusic) {
        if (playEndBossMusic != this.playBossMusic) {
            super.setPlayBossMusic(playEndBossMusic);
            this.broadcast(FantazicBossEventPacket::createUpdatePropertiesPacket);
        }

        return this;
    }

    public @NotNull FantazicBossEvent setCreateWorldFog(boolean createFog) {
        if (createFog != this.createWorldFog) {
            super.setCreateWorldFog(createFog);
            this.broadcast(FantazicBossEventPacket::createUpdatePropertiesPacket);
        }

        return this;
    }

    public void setName(@NotNull Component name) {
        if (!Objects.equal(name, this.name)) {
            super.setName(name);
            this.broadcast(FantazicBossEventPacket::createUpdateNamePacket);
        }

    }

    private void broadcast(Function<FantazicBossEvent, FantazicBossEventPacket> packetGetter) {
        if (isVisible()) {
            FantazicBossEventPacket packet = packetGetter.apply(this);

            for(ServerPlayer serverplayer : players) serverplayer.connection.send(packet);
        }
    }

    public void addPlayer(@NotNull ServerPlayer player) {
        if (players.add(player) && isVisible()) {
            player.connection.send(FantazicBossEventPacket.createAddPacket(this));
        }

    }

    public void removePlayer(@NotNull ServerPlayer player) {
        if (players.remove(player) && this.isVisible()) {
            player.connection.send(FantazicBossEventPacket.createRemovePacket(this.getId()));
        }

    }

    public void removeAllPlayers() {
        if (!players.isEmpty()) {
            for(ServerPlayer serverplayer : Lists.newArrayList(players)) this.removePlayer(serverplayer);
        }

    }
}
