package net.arkadiyhimself.fantazia.packets.fantazic_boss_event;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.FantazicClientBossEvent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

import java.util.UUID;

public class FantazicBossEventHandler {

    public static void add(UUID uuid, Component component, float progress, float barrier, BossEvent.BossBarColor bossBarColor, BossEvent.BossBarOverlay overlay, boolean darkenScreen, boolean music, boolean fog) {
        Fantazia.getBossBarOverlay().events().put(uuid, new FantazicClientBossEvent(uuid, component, progress, barrier, bossBarColor, overlay, darkenScreen, music, fog));
    }

    public static void remove(UUID uuid) {
        Fantazia.getBossBarOverlay().events().remove(uuid);
    }

    public static void updateProgress(UUID uuid, float progress, float barrier) {
        LerpingBossEvent bossEvent = Fantazia.getBossBarOverlay().events().get(uuid);
        if (bossEvent instanceof FantazicClientBossEvent clientBossEvent) clientBossEvent.setProgress(progress, barrier);
    }

    public static void updateName(UUID uuid, Component component) {
        Fantazia.getBossBarOverlay().events().get(uuid).setName(component);
    }

    public static void updateStyle(UUID uuid, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
        LerpingBossEvent lerpingbossevent = Fantazia.getBossBarOverlay().events().get(uuid);
        lerpingbossevent.setColor(color);
        lerpingbossevent.setOverlay(overlay);
    }

    public static void updateProperties(UUID uuid, boolean darkenScreen, boolean playBossMusic, boolean worldFog) {
        LerpingBossEvent lerpingbossevent = Fantazia.getBossBarOverlay().events().get(uuid);
        lerpingbossevent.setDarkenScreen(darkenScreen);
        lerpingbossevent.setPlayBossMusic(playBossMusic);
        lerpingbossevent.setCreateWorldFog(worldFog);
    }
}
