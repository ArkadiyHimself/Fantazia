package net.arkadiyhimself.fantazia.client.models;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.client.player.AbstractClientPlayer;
import org.jetbrains.annotations.Nullable;

public class PlayerAnimations {

    @SuppressWarnings("ConstantConditions")
    public static KeyframeAnimationPlayer WINDUP_START() {
        return new KeyframeAnimationPlayer((KeyframeAnimation) PlayerAnimationRegistry.getAnimation(Fantazia.res("windup_start")));
    }

    @SuppressWarnings("ConstantConditions")
    public static final KeyframeAnimationPlayer WINDUP_CONTINUE = new KeyframeAnimationPlayer((KeyframeAnimation) PlayerAnimationRegistry.getAnimation(Fantazia.res("windup_continue")));

    @SuppressWarnings("unchecked")
    public static void animatePlayer(AbstractClientPlayer player, @Nullable String name) {
        ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(Fantazia.res("animation"));
        if (animation != null) {
            if (name != null) {
                @Nullable KeyframeAnimation keyframeAnimation = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(Fantazia.res(name));
                if (keyframeAnimation != null) animation.setAnimation(new KeyframeAnimationPlayer(keyframeAnimation));
            } else animation.setAnimation(null);
        }
    }

    @SuppressWarnings("unchecked")
    public static void animatePlayer(AbstractClientPlayer player, @Nullable IAnimation animation) {
        ModifierLayer<IAnimation> animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(Fantazia.res("animation"));
        if (animationLayer != null) animationLayer.setAnimation(animation);
    }

    @SuppressWarnings("unchecked")
    public static @Nullable IAnimation getAnimation(AbstractClientPlayer player) {
        ModifierLayer<IAnimation> animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(Fantazia.res("animation"));
        if (animationLayer != null) return animationLayer.getAnimation();
        else return null;
    }

}
