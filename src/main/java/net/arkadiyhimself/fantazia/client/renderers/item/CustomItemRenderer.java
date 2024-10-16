package net.arkadiyhimself.fantazia.client.renderers.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.data_component.HiddenPotentialHolder;
import net.arkadiyhimself.fantazia.items.weapons.Melee.FragileBladeItem;
import net.arkadiyhimself.fantazia.registries.FTZDataComponentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.RenderTypeHelper;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.renderer.entity.ItemRenderer.getFoilBufferDirect;

@OnlyIn(Dist.CLIENT)
public class CustomItemRenderer extends BlockEntityWithoutLevelRenderer {
    public static final ModelResourceLocation BLADE0 = Fantazia.modelRes("item/fragile_blade/fragile_blade_in_hand0");
    public static final ModelResourceLocation BLADE1 = Fantazia.modelRes("item/fragile_blade/fragile_blade_in_hand1");
    public static final ModelResourceLocation BLADE2 = Fantazia.modelRes("item/fragile_blade/fragile_blade_in_hand2");
    public static final ModelResourceLocation BLADE3 = Fantazia.modelRes("item/fragile_blade/fragile_blade_in_hand3");
    public static final ModelResourceLocation BLADE4 = Fantazia.modelRes("item/fragile_blade/fragile_blade_in_hand4");
    public static final ModelResourceLocation BLADE_MODEL = Fantazia.modelRes("item/fragile_blade/fragile_blade_model");
    public CustomItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }
    public static ModelResourceLocation getFragBladeModel(ItemStack item) {
        HiddenPotentialHolder hiddenPotentialHolder = item.get(FTZDataComponentTypes.HIDDEN_POTENTIAL);
        if (hiddenPotentialHolder == null) return BLADE0;
        else return switch (hiddenPotentialHolder.damageLevel()) {
                case STARTING -> BLADE0;
                case LOW -> BLADE1;
                case MEDIUM -> BLADE2;
                case HIGH -> BLADE3;
                case MAXIMUM -> BLADE4;
        };
    }
    @Override
    public void onResourceManagerReload(@NotNull ResourceManager pResourceManager) {
        super.onResourceManagerReload(pResourceManager);
    }

    @Override
    public void renderByItem(ItemStack pStack, @NotNull ItemDisplayContext pDisplayContext, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        boolean gui = pDisplayContext == ItemDisplayContext.GUI || pDisplayContext == ItemDisplayContext.FIXED || pDisplayContext == ItemDisplayContext.GROUND;
        boolean firstPerson = pDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || pDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        boolean leftHand = pDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || pDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
        Item item = pStack.getItem();
        if (gui) {
            RenderSystem.setShaderColor(1f,1f,1f,1f);
            pPoseStack.pushPose();
            BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(getFragBladeModel(pStack));
            RenderType type = RenderTypeHelper.getFallbackItemRenderType(pStack, model, true);
            VertexConsumer vertexconsumer = getFoilBufferDirect(pBuffer, type, true, pStack.hasFoil());

            Minecraft.getInstance().getItemRenderer().renderModelLists(model, pStack, pPackedLight, pPackedOverlay, pPoseStack, vertexconsumer);
            pPoseStack.popPose();
        } else {
            if (item instanceof FragileBladeItem) {
                pPoseStack.pushPose();
                BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(BLADE_MODEL);
                pPoseStack.translate(0.5f,0.5f,0.5f);
                Minecraft.getInstance().getItemRenderer().render(pStack, pDisplayContext, leftHand, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, model);
                pPoseStack.popPose();
            }
        }
    }
}
