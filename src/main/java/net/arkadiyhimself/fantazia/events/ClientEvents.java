package net.arkadiyhimself.fantazia.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.KeyBinding;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.AncientFlameTicksHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.*;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.client.gui.FantazicGui;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.client.render.bars.*;
import net.arkadiyhimself.fantazia.client.screen.TalentsScreen;
import net.arkadiyhimself.fantazia.items.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.items.weapons.Melee.FragileBladeItem;
import net.arkadiyhimself.fantazia.items.weapons.Melee.MurasamaItem;
import net.arkadiyhimself.fantazia.networking.packets.stuff.KeyInputC2S;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.tags.FTZSoundEventTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT, modid = Fantazia.MODID)
public class ClientEvents {

    @SubscribeEvent
    public static void fovModifier(ComputeFovModifierEvent event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(FTZMobEffects.FURY)) event.setNewFovModifier(event.getNewFovModifier() * 1.1f);
    }

    @SubscribeEvent
    public static void renderGuiPre(RenderGuiLayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = event.getGuiGraphics().pose();
        ResourceLocation overlay = event.getName();


        FrozenEffect frozenEffect = LivingEffectGetter.takeHolder(player, FrozenEffect.class);
        if (frozenEffect != null) {
            float effPer = frozenEffect.effectPercent();
            float frePer = player.getPercentFrozen();
            if (effPer > frePer && overlay.equals(VanillaGuiLayers.FOOD_LEVEL)) event.setCanceled(true);
        }

        if (overlay.equals(VanillaGuiLayers.SUBTITLE_OVERLAY) && !player.shouldShowDeathScreen()) poseStack.translate(0,-48,0);
        if (overlay.equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
            int x = event.getGuiGraphics().guiWidth() / 2 - 91;
            int y = event.getGuiGraphics().guiHeight() - 29;

            StunEffect stunEffect = LivingEffectGetter.takeHolder(player, StunEffect.class);
            BarrierEffect barrierEffect = LivingEffectGetter.takeHolder(player, BarrierEffect.class);

            poseStack.pushPose();
            if (FantazicGui.renderStunBar(stunEffect, guiGraphics, x, y)) event.setCanceled(true);
            else if (FantazicGui.renderBarrierBar(barrierEffect, guiGraphics, x, y)) event.setCanceled(true);
            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void renderGuiPost(RenderGuiLayerEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = event.getGuiGraphics().pose();
        ResourceLocation overlay = event.getName();
        if (overlay.equals(VanillaGuiLayers.SUBTITLE_OVERLAY) && !player.shouldShowDeathScreen()) poseStack.translate(0,48,0);
    }

    @SubscribeEvent
    public static void renderBackground(ContainerScreenEvent.Render.Background event) {
        Screen screen = event.getContainerScreen();
        if (!(screen instanceof EffectRenderingInventoryScreen<?>)) return;
        FantazicGui.renderAurasInventory(event.getGuiGraphics());
    }

    @SubscribeEvent
    public static void renderPlayerPre(RenderPlayerEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        Quaternionf cameraOrientation = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        MultiBufferSource buffers = event.getMultiBufferSource();

        if (entity instanceof Player player && player.isSpectator()) return;
        if (!entity.getPassengers().isEmpty()) return;

        AncientFlameTicksHolder ancientFlameTicksHolder = LivingDataGetter.takeHolder(entity, AncientFlameTicksHolder.class);
        float yOffset = -1f;
        if (ancientFlameTicksHolder != null && ancientFlameTicksHolder.isBurning()) VisualHelper.renderAncientFlame(poseStack, entity, buffers);
        if (entity == Minecraft.getInstance().player) return;

        poseStack.pushPose();

        poseStack.translate(0, entity.getBbHeight() + 0.75, 0);
        poseStack.mulPose(cameraOrientation);

        poseStack.scale(1,-1,1);

        StunEffect stunEffect = LivingEffectGetter.takeHolder(entity, StunEffect.class);
        if (stunEffect != null && stunEffect.renderBar()) {
            yOffset = -1.45f;
            StunBarType.render(stunEffect, poseStack, buffers);
        }

        CursedMarkEffect cursedMarkEffect = LivingEffectGetter.takeHolder(entity, CursedMarkEffect.class);
        DisarmEffect disarmEffect = LivingEffectGetter.takeHolder(entity, DisarmEffect.class);
        FrozenEffect frozenEffect = LivingEffectGetter.takeHolder(entity, FrozenEffect.class);
        DeafenedEffect deafenedEffect = LivingEffectGetter.takeHolder(entity, DeafenedEffect.class);

        if (cursedMarkEffect != null && cursedMarkEffect.isMarked()) CursedMarkType.render(poseStack, buffers, yOffset);
        else if (disarmEffect != null && disarmEffect.renderDisarm()) DisarmedSwordType.render(poseStack, buffers, yOffset);
        else if (frozenEffect != null && frozenEffect.renderFreeze()) SnowCrystalType.render(frozenEffect, poseStack, buffers, yOffset);
        else if (deafenedEffect != null && deafenedEffect.renderDeaf()) DeafeningType.render(deafenedEffect, poseStack, buffers, yOffset);

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void renderPlayerPost(RenderPlayerEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();
        PlayerRenderer renderer = event.getRenderer();
        int packedLight = event.getPackedLight();
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(player, 0);

        EvasionHolder evasionHolder = LivingDataGetter.takeHolder(player, EvasionHolder.class);
        if (evasionHolder != null && evasionHolder.getIFrames() > 0) VisualHelper.renderEvasionPlayer(player, renderer, poseStack, buffers, packedLight, packedOverlay);
    }

    @SubscribeEvent
    public static <T extends LivingEntity, M extends EntityModel<T>> void renderLivingPre(RenderLivingEvent.Pre<T, M> event) {
        assert Minecraft.getInstance().player != null;
        LivingEntity entity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        Quaternionf cameraOrientation = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        MultiBufferSource buffers = event.getMultiBufferSource();

        if (entity instanceof Player player && player.isSpectator()) return;
        if (!entity.getPassengers().isEmpty()) return;

        AncientFlameTicksHolder ancientFlameTicksHolder = LivingDataGetter.takeHolder(entity, AncientFlameTicksHolder.class);
        float yOffset = -1f;
        if (ancientFlameTicksHolder != null && ancientFlameTicksHolder.isBurning()) VisualHelper.renderAncientFlame(poseStack, entity, buffers);
        if (entity == Minecraft.getInstance().player) return;

        poseStack.pushPose();

        poseStack.translate(0, entity.getBbHeight() + 0.75, 0);
        poseStack.mulPose(cameraOrientation);

        poseStack.scale(1,-1,1);

        StunEffect stunEffect = LivingEffectGetter.takeHolder(entity, StunEffect.class);
        if (stunEffect != null && stunEffect.renderBar()) {
            yOffset = -1.45f;
            StunBarType.render(stunEffect, poseStack, buffers);
        }

        CursedMarkEffect cursedMarkEffect = LivingEffectGetter.takeHolder(entity, CursedMarkEffect.class);
        DisarmEffect disarmEffect = LivingEffectGetter.takeHolder(entity, DisarmEffect.class);
        FrozenEffect frozenEffect = LivingEffectGetter.takeHolder(entity, FrozenEffect.class);
        DeafenedEffect deafenedEffect = LivingEffectGetter.takeHolder(entity, DeafenedEffect.class);


        if (cursedMarkEffect != null && cursedMarkEffect.isMarked()) CursedMarkType.render(poseStack, buffers, yOffset);
        else if (disarmEffect != null && disarmEffect.renderDisarm()) DisarmedSwordType.render(poseStack, buffers, yOffset);
        else if (frozenEffect != null && frozenEffect.renderFreeze()) SnowCrystalType.render(frozenEffect, poseStack, buffers, yOffset);
        else if (deafenedEffect != null && deafenedEffect.renderDeaf()) DeafeningType.render(deafenedEffect, poseStack, buffers, yOffset);

        poseStack.popPose();
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity, M extends EntityModel<T>> void renderLivingPost(RenderLivingEvent.Post<T,M> event) {
        T entity = (T) event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();
        LivingEntityRenderer<T, M> renderer = event.getRenderer();
        int packedLight = event.getPackedLight();
        int packedOverlay = LivingEntityRenderer.getOverlayCoords(entity, 0);

        EvasionHolder evasionHolder = LivingDataGetter.takeHolder(entity, EvasionHolder.class);
        if (evasionHolder != null && evasionHolder.getIFrames() > 0) VisualHelper.renderEvasionEntity(entity, renderer, poseStack, buffers, packedLight, packedOverlay);
    }

    @SubscribeEvent
    public static void mouseScrolling(InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            StunEffect stunEffect = LivingEffectGetter.takeHolder(player, StunEffect.class);
            if (stunEffect != null && stunEffect.stunned()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void mouseInputs(InputEvent.InteractionKeyMappingTriggered event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (ActionsHelper.preventActions(player)) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
        if (player.hasEffect(FTZMobEffects.DISARM)) event.setSwingHand(false);
        if (KeyBinding.BLOCK.consumeClick() && player.getMainHandItem().is(FTZItemTags.MELEE_BLOCK)) PacketDistributor.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.BLOCK, 1));
    }

    @SubscribeEvent
    public static void input(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (ActionsHelper.preventActions(player)) return;

        if (KeyBinding.DASH.consumeClick()) PacketDistributor.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.DASH, 1));
        if (KeyBinding.SWORD_ABILITY.consumeClick()) PacketDistributor.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.WEAPON_ABILITY, 1));
        if (KeyBinding.SPELLCAST1.consumeClick()) PacketDistributor.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.SPELLCAST1, 1));
        if (KeyBinding.SPELLCAST2.consumeClick()) PacketDistributor.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.SPELLCAST2, 1));
        if (KeyBinding.SPELLCAST3.consumeClick()) PacketDistributor.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.SPELLCAST2, 1));

        TalentsHolder talentsHolder = PlayerAbilityGetter.takeHolder(player, TalentsHolder.class);
        if (KeyBinding.TALENTS.consumeClick() && talentsHolder != null) Minecraft.getInstance().setScreen(new TalentsScreen(talentsHolder));
        if (event.getKey() == Minecraft.getInstance().options.keyJump.getKey().getValue()) PacketDistributor.sendToServer(new KeyInputC2S(KeyInputC2S.INPUT.JUMP, event.getAction()));
    }

    // the event is used to remove vanilla's "Attack damage: ..." and "Attack speed: ..." lines
    @SubscribeEvent
    public static void itemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        if (stack.getItem() instanceof SpellCasterItem spellCasterItem) tooltip.addAll(spellCasterItem.buildTooltip());
        if (stack.getItem() instanceof AuraCasterItem auraCasterItem) tooltip.addAll(auraCasterItem.buildTooltip());
        if (stack.getItem() instanceof FragileBladeItem fragileBladeItem) {
            Component name = event.getToolTip().getFirst().copy();
            tooltip.clear();
            tooltip.add(GuiHelper.bakeComponent(name.getString(), null, null));
            tooltip.add(Component.literal(" "));
            tooltip.addAll(fragileBladeItem.itemTooltip(stack));
        }
        if (stack.getItem() instanceof MurasamaItem murasamaItem) {
            Component name = event.getToolTip().getFirst().copy();
            tooltip.clear();
            tooltip.add(GuiHelper.bakeComponent(name.getString(), null, null));
            tooltip.add(Component.literal(" "));
            tooltip.addAll(murasamaItem.itemTooltip(stack));
        }
    }

    @SubscribeEvent
    public static void playSound(PlaySoundEvent event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(FTZMobEffects.DEAFENED)) {
            Optional<Holder.Reference<SoundEvent>> soundEvent = BuiltInRegistries.SOUND_EVENT.getHolder(event.getOriginalSound().getLocation());
            if (soundEvent.isEmpty()) return;

            if (!FTZSoundEventTags.hasTag(soundEvent.get(), FTZSoundEventTags.NOT_MUTED)) event.setSound(null);
        }
    }

    @SubscribeEvent
    public static void onRenderFire(RenderBlockScreenEffectEvent event) {
        AncientFlameTicksHolder ancientFlameTicksHolder = LivingDataGetter.takeHolder(Minecraft.getInstance().player, AncientFlameTicksHolder.class);
        if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE && ancientFlameTicksHolder != null && ancientFlameTicksHolder.isBurning()) event.setCanceled(true);
    }

}
