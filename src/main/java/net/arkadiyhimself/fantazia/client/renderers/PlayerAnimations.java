package net.arkadiyhimself.fantazia.client.renderers;

import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PlayerAnimations {

    @SuppressWarnings("ConstantConditions")
    public static KeyframeAnimationPlayer WINDUP_START() {
        Map<ResourceLocation, IPlayable> animations = PlayerAnimationRegistry.getAnimations();
        return new KeyframeAnimationPlayer((KeyframeAnimation)
                PlayerAnimationRegistry.getAnimation(Fantazia.location("windup.start")));
    }

    @SuppressWarnings("ConstantConditions")
    public static final KeyframeAnimationPlayer WINDUP_CONTINUE =
            new KeyframeAnimationPlayer((KeyframeAnimation) PlayerAnimationRegistry.getAnimation(Fantazia.location("windup.continue")));

    @SuppressWarnings("unchecked")
    public static void animatePlayer(AbstractClientPlayer player, @Nullable String name) {
        ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(Fantazia.location("animation"));
        if (animation != null) {
            if (name != null && !name.isEmpty()) {
                @Nullable KeyframeAnimation keyframeAnimation = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(Fantazia.location(name));
                if (keyframeAnimation != null) animation.setAnimation(new KeyframeAnimationPlayer(keyframeAnimation));
            } else animation.setAnimation(null);
        }
    }

    @SuppressWarnings("unchecked")
    public static void animatePlayer(AbstractClientPlayer player, @Nullable IAnimation animation) {
        Map<ResourceLocation, IPlayable> animations = PlayerAnimationRegistry.getAnimations();
        ModifierLayer<IAnimation> animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(Fantazia.location("animation"));
        if (animationLayer != null) animationLayer.setAnimation(animation);
    }

    @SuppressWarnings("unchecked")
    public static @Nullable IAnimation getAnimation(AbstractClientPlayer player) {
        ModifierLayer<IAnimation> animationLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(player).get(Fantazia.location("animation"));
        if (animationLayer != null) return animationLayer.getAnimation();
        else return null;
    }

}
