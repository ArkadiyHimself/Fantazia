package net.arkadiyhimself.fantazia.packets.stuff;

import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.api.prompt.Prompt;
import net.arkadiyhimself.fantazia.api.prompt.PromptToast;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.renderers.PlayerAnimations;
import net.arkadiyhimself.fantazia.client.screen.AmplifyResource;
import net.arkadiyhimself.fantazia.entities.DashStone;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.arkadiyhimself.fantazia.world.inventory.AmplificationMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

interface StuffHandlers {

    static void addChasingParticle(List<ParticleOptions> options) {
        if (Minecraft.getInstance().level == null) return;
        for (ParticleOptions option : options) Minecraft.getInstance().level.addParticle(option, 0,0,0,0,0,0);
    }

    static void addDashStoneProtectors(int id, List<Integer> protIDS) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        Entity stone = level.getEntity(id);
        if (!(stone instanceof DashStone dashStone)) return;
        dashStone.addProtectorsClient(protIDS);
    }

    static void addParticleOnEntity(int id, ParticleOptions particle, ParticleMovement movement, int amount, float range) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel == null) return;
        Entity entity = clientLevel.getEntity(id);
        if (entity == null) return;

        float radius = entity.getBbWidth() * (float) 0.7;
        float height = entity.getBbHeight();

        // the resulting position is supposed to be on a "cylinder" around entity, not sphere, which is why y coordinate is taken separately
        for (int i = 0; i < amount; i++) {
            Vec3 vec3 = RandomUtil.randomHorizontalVec3().normalize().scale(radius).scale(range);
            double x = vec3.x();
            double z = vec3.z();
            double y = RandomUtil.nextDouble(height * 0.1, height * 0.8);

            double x0 = entity.getX() + x;
            double y0 = entity.getY() + y;
            double z0 = entity.getZ() + z;

            Vec3 delta = movement.modify(new Vec3(x0, y0, z0), entity.getDeltaMovement());

            clientLevel.addParticle(particle, x0, y0, z0, delta.x, delta.y, delta.z);
        }
    }

    static void amplificationMenuEnoughResources(AmplifyResource enoughWisdom, AmplifyResource enoughSubstance) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !(player.containerMenu instanceof AmplificationMenu menu)) return;
        menu.setEnoughWisdom(enoughWisdom);
        menu.setEnoughSubstance(enoughSubstance);
    }

    static void animatePlayer(String anim, int id) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        Entity entity = level.getEntity(id);
        if (entity instanceof LocalPlayer localPlayer) PlayerAnimations.animatePlayer(localPlayer, anim);
    }

    static void interruptPlayer() {
        if (Minecraft.getInstance().player == null) return;
        if (!((Minecraft.getInstance().screen) instanceof ChatScreen)) Minecraft.getInstance().player.clientSideCloseContainer();
        Minecraft.getInstance().player.stopUsingItem();
        Minecraft.getInstance().player.stopSleeping();
        Minecraft.getInstance().options.keyAttack.setDown(false);
    }

    static void keyInput(IPayloadContext context, KeyInputC2S.INPUT input, int action) {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
        input.consumer().accept(serverPlayer, action);
    }

    static void playSoundForUI(SoundEvent soundEvent) {
        if (soundEvent == null) return;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, 1f, 1f));
    }

    static void promptPlayer(Prompt prompt) {
        ToastComponent gui = Minecraft.getInstance().getToasts();
        if (gui.getToast(PromptToast.class, prompt) == null) gui.addToast(new PromptToast(prompt));
    }

    static void swingHand(InteractionHand hand) {
        if (Minecraft.getInstance().player != null) Minecraft.getInstance().player.swing(hand);
    }

    static void usedPrompt(ServerPlayer serverPlayer, Prompt prompt) {
        prompt.noLongerNeeded(serverPlayer);
    }
}
