package net.arkadiyhimself.fantazia.client.renderers.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.entity.CustomBoat;
import net.arkadiyhimself.fantazia.common.entity.CustomChestBoat;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Stream;

public class CustomBoatRenderer extends BoatRenderer {

    public static final ModelLayerLocation OBSCURE_BOAT_LAYER = new ModelLayerLocation(Fantazia.location("boat/obscure"), "main");
    public static final ModelLayerLocation OBSCURE_CHEST_BOAT_LAYER = new ModelLayerLocation(Fantazia.location("chest_boat/obscure"), "main");
    private final Map<CustomBoat.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    public CustomBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context, chestBoat);
        this.boatResources = Stream.of(CustomBoat.Type.values()).collect(ImmutableMap.toImmutableMap(type -> type,
                type -> Pair.of(Fantazia.location(getTextureLocation(type, chestBoat)), this.createBoatModel(context, type, chestBoat))));

    }

    private static String getTextureLocation(CustomBoat.Type pType, boolean chestBoat) {
        return chestBoat ? "textures/entity/chest_boat/" + pType.getName() + ".png" : "textures/entity/boat/" + pType.getName() + ".png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context pContext, CustomBoat.Type pType, boolean pChestBoat) {
        ModelLayerLocation modellayerlocation = pChestBoat ? createChestBoatModelName(pType) : createBoatModelName(pType);
        ModelPart modelpart = pContext.bakeLayer(modellayerlocation);
        return pChestBoat ? new ChestBoatModel(modelpart) : new BoatModel(modelpart);
    }

    public static ModelLayerLocation createBoatModelName(CustomBoat.Type pType) {
        return createLocation("boat/" + pType.getName());
    }

    public static ModelLayerLocation createChestBoatModelName(CustomBoat.Type pType) {
        return createLocation("chest_boat/" + pType.getName());
    }

    private static ModelLayerLocation createLocation(String pPath) {
        return new ModelLayerLocation(Fantazia.location(pPath), "main");
    }

    public @NotNull Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(@NotNull Boat boat) {
        if(boat instanceof CustomBoat modBoat) return this.boatResources.get(modBoat.getCustomVariant());
        else if (boat instanceof CustomChestBoat modChestBoatEntity) return this.boatResources.get(modChestBoatEntity.getCustomVariant());
        else return null;
    }
}
