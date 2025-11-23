package net.arkadiyhimself.fantazia.client.renderers.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.api.data_component.HiddenPotentialComponent;
import net.arkadiyhimself.fantazia.common.api.data_component.WisdomTransferComponent;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.RenderTypeHelper;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.renderer.entity.ItemRenderer.getFoilBuffer;
import static net.minecraft.client.renderer.entity.ItemRenderer.getFoilBufferDirect;

@OnlyIn(Dist.CLIENT)
public class FantazicItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final ModelResourceLocation BLADE0 = Fantazia.modelLocation("item/fragile_blade/in_gui_level0");
    public static final ModelResourceLocation BLADE1 = Fantazia.modelLocation("item/fragile_blade/in_gui_level1");
    public static final ModelResourceLocation BLADE2 = Fantazia.modelLocation("item/fragile_blade/in_gui_level2");
    public static final ModelResourceLocation BLADE3 = Fantazia.modelLocation("item/fragile_blade/in_gui_level3");
    public static final ModelResourceLocation BLADE4 = Fantazia.modelLocation("item/fragile_blade/in_gui_level4");
    public static final ModelResourceLocation BLADE_MODEL = Fantazia.modelLocation("item/fragile_blade/in_hand");

    public static final ModelResourceLocation WISDOM_CATCHER_GUI_ABSORB = Fantazia.modelLocation("item/wisdom_catcher/in_gui_absorb");
    public static final ModelResourceLocation WISDOM_CATCHER_GUI_RELEASE = Fantazia.modelLocation("item/wisdom_catcher/in_gui_release");

    public static final ModelResourceLocation WISDOM_CATCHER_MODEL_ABSORB = Fantazia.modelLocation("item/wisdom_catcher/in_hand_stationary_absorb");
    public static final ModelResourceLocation WISDOM_CATCHER_MODEL_RELEASE = Fantazia.modelLocation("item/wisdom_catcher/in_hand_stationary_release");

    public static final ModelResourceLocation WISDOM_CATCHER_MODEL_USED_ABSORB = Fantazia.modelLocation("item/wisdom_catcher/in_hand_used_absorb");
    public static final ModelResourceLocation WISDOM_CATCHER_MODEL_USED_RELEASE = Fantazia.modelLocation("item/wisdom_catcher/in_hand_used_release");

    public static final ModelResourceLocation DASHSTONE1 = Fantazia.modelLocation("item/dashstone/level1");
    public static final ModelResourceLocation DASHSTONE2 = Fantazia.modelLocation("item/dashstone/level2");
    public static final ModelResourceLocation DASHSTONE3 = Fantazia.modelLocation("item/dashstone/level3");

    public FantazicItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack pStack, @NotNull ItemDisplayContext pDisplayContext, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Item item = pStack.getItem();
        if (item == FTZItems.RUNE_WIELDER.value()) renderRuneWielder(pStack, pDisplayContext, pBuffer, pPoseStack, pPackedLight, pPackedOverlay);
        else if (item == FTZItems.BLUEPRINT.value()) renderBlueprint(pStack, pDisplayContext, pBuffer, pPoseStack, pPackedLight, pPackedOverlay);
        else if (item == FTZItems.FRAGILE_BLADE.value()) renderFragileBlade(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        else if (item == FTZItems.WISDOM_CATCHER.value()) renderWisdomCatcher(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        else if (item == FTZItems.DASHSTONE.value()) renderDashStone(pStack, pDisplayContext, pBuffer, pPoseStack, pPackedLight, pPackedOverlay);
    }

    public static void renderRuneWielder(ItemStack stack, ItemDisplayContext context, MultiBufferSource pBuffer, PoseStack pPoseStack, int pPackedLight, int pPackedOverlay) {
        Holder<Rune> holder = stack.get(FTZDataComponentTypes.RUNE);
        if (holder == null) return;
        Rune rune = holder.value();
        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(rune.getIcon());
        RenderType type = RenderTypeHelper.getFallbackItemRenderType(stack, model, true);
        VertexConsumer vertexconsumer = getFoilBuffer(pBuffer, type, true, stack.hasFoil());

        pPoseStack.popPose();
        model = ClientHooks.handleCameraTransforms(pPoseStack, model, context, false);
        pPoseStack.translate(-0.5F, -0.5F, -0.5F);
        Minecraft.getInstance().getItemRenderer().renderModelLists(model, stack, pPackedLight, pPackedOverlay, pPoseStack, vertexconsumer);

        pPoseStack.pushPose();
    }

    public static void renderBlueprint(ItemStack stack, ItemDisplayContext context, MultiBufferSource pBuffer, PoseStack pPoseStack, int pPackedLight, int pPackedOverlay) {
        Holder<Blueprint> holder = stack.get(FTZDataComponentTypes.BLUEPRINT);
        if (holder == null) return;
        Blueprint blueprint = holder.value();
        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(blueprint.getIcon());
        RenderType type = RenderTypeHelper.getFallbackItemRenderType(stack, model, true);
        VertexConsumer vertexconsumer = getFoilBuffer(pBuffer, type, true, stack.hasFoil());

        pPoseStack.popPose();
        model = ClientHooks.handleCameraTransforms(pPoseStack, model, context, false);
        pPoseStack.translate(-0.5F, -0.5F, -0.5F);

        Minecraft.getInstance().getItemRenderer().renderModelLists(model, stack, pPackedLight, pPackedOverlay, pPoseStack, vertexconsumer);

        pPoseStack.pushPose();
    }

    public static void renderDashStone(ItemStack stack, ItemDisplayContext context, MultiBufferSource pBuffer, PoseStack pPoseStack, int pPackedLight, int pPackedOverlay) {
        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(getDashStoneModel(stack));
        RenderType type = RenderTypeHelper.getFallbackItemRenderType(stack, model, true);
        VertexConsumer vertexconsumer = getFoilBuffer(pBuffer, type, true, stack.hasFoil());

        pPoseStack.popPose();

        model = ClientHooks.handleCameraTransforms(pPoseStack, model, context, false);
        pPoseStack.translate(-0.5F, -0.5F, -0.5F);
        Minecraft.getInstance().getItemRenderer().renderModelLists(model, stack, pPackedLight, pPackedOverlay, pPoseStack, vertexconsumer);

        pPoseStack.pushPose();
    }

    private static void renderFragileBlade(ItemStack pStack, @NotNull ItemDisplayContext pDisplayContext, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        boolean gui = pDisplayContext == ItemDisplayContext.GUI || pDisplayContext == ItemDisplayContext.FIXED || pDisplayContext == ItemDisplayContext.GROUND;
        boolean leftHand = pDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || pDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        pPoseStack.pushPose();
        BakedModel model;
        if (gui) {
            model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(getFragBladeModel(pStack));
            RenderType type = RenderTypeHelper.getFallbackItemRenderType(pStack, model, true);
            VertexConsumer vertexconsumer = getFoilBufferDirect(pBuffer, type, true, pStack.hasFoil());
            Minecraft.getInstance().getItemRenderer().renderModelLists(model, pStack, pPackedLight, pPackedOverlay, pPoseStack, vertexconsumer);
        } else {
            model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(BLADE_MODEL);

            pPoseStack.translate(0.5f,0.5f,0.5f);
            Minecraft.getInstance().getItemRenderer().render(pStack, pDisplayContext, leftHand, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, model);
        }
        pPoseStack.popPose();
    }

    private static void renderWisdomCatcher(ItemStack pStack, @NotNull ItemDisplayContext pDisplayContext, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        boolean gui = pDisplayContext == ItemDisplayContext.GUI || pDisplayContext == ItemDisplayContext.FIXED;
        pPoseStack.pushPose();
        BakedModel model;
        if (gui) {
            WisdomTransferComponent component = pStack.get(FTZDataComponentTypes.WISDOM_TRANSFER);
            ModelResourceLocation location = component == WisdomTransferComponent.RELEASE ? WISDOM_CATCHER_GUI_RELEASE : WISDOM_CATCHER_GUI_ABSORB;
            model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(location);
            RenderType type = RenderTypeHelper.getFallbackItemRenderType(pStack, model, true);
            VertexConsumer vertexconsumer = getFoilBufferDirect(pBuffer, type, true, pStack.hasFoil());
            Minecraft.getInstance().getItemRenderer().renderModelLists(model, pStack, pPackedLight, pPackedOverlay, pPoseStack, vertexconsumer);
        } else {
            if (pDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || pDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) renderWisdomCatcherFirstPerson(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            else renderWisdomCatcherThirdPerson(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        }
        pPoseStack.popPose();
    }

    private static void renderWisdomCatcherFirstPerson(ItemStack pStack, @NotNull ItemDisplayContext pDisplayContext, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if (player == null || level == null) return;

        WisdomTransferComponent component = pStack.get(FTZDataComponentTypes.WISDOM_TRANSFER);
        boolean usingItem = player.isUsingItem() && player.getUseItem() == pStack;
        ModelResourceLocation location = usingItem ? WISDOM_CATCHER_MODEL_USED_ABSORB : WISDOM_CATCHER_MODEL_ABSORB;
        if (component == WisdomTransferComponent.RELEASE) {
            location = usingItem ? WISDOM_CATCHER_MODEL_USED_RELEASE : WISDOM_CATCHER_MODEL_RELEASE;
        }
        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(location);
        pPoseStack.translate(0.5f,0.5f,0.5f);
        Minecraft.getInstance().getItemRenderer().render(pStack, pDisplayContext, pDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, model);
    }

    private static void renderWisdomCatcherThirdPerson(ItemStack pStack, @NotNull ItemDisplayContext pDisplayContext, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        WisdomTransferComponent component = pStack.get(FTZDataComponentTypes.WISDOM_TRANSFER);
        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(component == WisdomTransferComponent.RELEASE ? WISDOM_CATCHER_MODEL_RELEASE : WISDOM_CATCHER_MODEL_ABSORB);
        pPoseStack.translate(0.5f,0.5f,0.5f);
        Minecraft.getInstance().getItemRenderer().render(pStack, pDisplayContext, pDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, model);
    }

    private static ModelResourceLocation getFragBladeModel(ItemStack stack) {
        HiddenPotentialComponent hiddenPotentialComponent = stack.get(FTZDataComponentTypes.HIDDEN_POTENTIAL);
        if (hiddenPotentialComponent == null) return BLADE0;
        else return switch (hiddenPotentialComponent.damageLevel()) {
            case STARTING -> BLADE0;
            case LOW -> BLADE1;
            case MEDIUM -> BLADE2;
            case HIGH -> BLADE3;
            case MAXIMUM -> BLADE4;
        };
    }

    private static ModelResourceLocation getDashStoneModel(ItemStack stack) {
        Integer level = stack.get(FTZDataComponentTypes.DASH_LEVEL);
        if (level == null || level <= 1) return DASHSTONE1;
        else if (level == 2) return DASHSTONE2;
        else return DASHSTONE3;
    }
}
