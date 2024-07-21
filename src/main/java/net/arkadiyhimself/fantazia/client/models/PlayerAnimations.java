package net.arkadiyhimself.fantazia.client.models;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.arkadiyhimself.fantazia.Fantazia;

public class PlayerAnimations {
    public static final KeyframeAnimationPlayer WINDUP_START() { return new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(Fantazia.res("windup_start"))); }
    public static final KeyframeAnimationPlayer WINDUP_CONTINUE = new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(Fantazia.res("windup_continue")));
}
