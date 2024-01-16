package net.arkadiyhimself.combatimprovement.client.Render.Models.Entity.NewEntitites.Hatchet;

import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.Registries.Entities.HatchetEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import software.bernie.geckolib.model.GeoModel;

public class HatchetModel extends GeoModel<HatchetEntity> {
    public HatchetModel() {
    }

    @Override
    public ResourceLocation getModelResource(HatchetEntity animatable) {
        return new ResourceLocation(CombatImprovement.MODID, "geo/hatchet.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(HatchetEntity animatable) {
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
    public ResourceLocation getAnimationResource(HatchetEntity animatable) {
        return new ResourceLocation(CombatImprovement.MODID, "animations/hatchet.json");
    }
}
