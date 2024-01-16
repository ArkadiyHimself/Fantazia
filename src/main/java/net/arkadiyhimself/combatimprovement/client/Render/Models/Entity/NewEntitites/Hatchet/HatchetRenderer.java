package net.arkadiyhimself.combatimprovement.client.Render.Models.Entity.NewEntitites.Hatchet;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.Registries.Entities.HatchetEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class HatchetRenderer extends GeoEntityRenderer<HatchetEntity> {
    public static final ResourceLocation WOODEN = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/wooden_hatchet.png");
    public static final ResourceLocation STONE = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/stone_hatchet.png");
    public static final ResourceLocation GOLD = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/gold_hatchet.png");
    public static final ResourceLocation IRON = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/iron_hatchet.png");
    public static final ResourceLocation DIAMOND = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/diamond_hatchet.png");
    public static final ResourceLocation NETHERITE = new ResourceLocation(CombatImprovement.MODID, "textures/entity/hatchet/netherite_hatchet.png");

    public HatchetRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HatchetModel());
    }
    @Override
    public ResourceLocation getTextureLocation(HatchetEntity animatable) {
        if (animatable.hatchetItem == null || !(animatable.hatchetItem.getItem() instanceof TieredItem item)) {
            return HatchetRenderer.WOODEN;
        }
        Tier tier = item.getTier();
        ResourceLocation res;
        if (tier == Tiers.STONE) {
            res = HatchetRenderer.STONE;
        } else if (tier == Tiers.IRON) {
            res = HatchetRenderer.IRON;
        } else if (tier == Tiers.GOLD) {
            res = HatchetRenderer.GOLD;
        } else if (tier == Tiers.DIAMOND) {
            res = HatchetRenderer.DIAMOND;
        } else if (tier == Tiers.NETHERITE) {
            res = HatchetRenderer.NETHERITE;
        } else {
            return HatchetRenderer.WOODEN;
        }
        return res;
    }

    @Override
    public void render(HatchetEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
