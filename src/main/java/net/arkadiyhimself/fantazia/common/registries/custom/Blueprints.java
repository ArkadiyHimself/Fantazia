package net.arkadiyhimself.fantazia.common.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.arkadiyhimself.fantazia.common.api.custom_registry.DeferredBlueprint;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

public class Blueprints {

    public static final FantazicRegistries.Blueprints REGISTER = FantazicRegistries.createBlueprints(Fantazia.MODID);

    public static final DeferredBlueprint<Blueprint> EMPTY = register("empty");
    public static final DeferredBlueprint<Blueprint> MINERS_CONSTRUCT = register("miners_construct");
    public static final DeferredBlueprint<Blueprint> SCULK_CONSTRUCT = register("sculk_construct");
    public static final DeferredBlueprint<Blueprint> END_CONSTRUCT = register("end_construct");
    public static final DeferredBlueprint<Blueprint> HEAT_TREATMENT_INSTRUCTIONS = register("heat_treatment_instructions");
    public static final DeferredBlueprint<Blueprint> BOOBY_TRAPS = register("booby_traps");
    public static final DeferredBlueprint<Blueprint> LATEST_DEVELOPMENT = register("latest_development");

    private static DeferredBlueprint<Blueprint> register(String name) {
        ResourceLocation id = Fantazia.location(name);
        ModelResourceLocation icon = Fantazia.modelLocation(id.withPrefix("blueprint/"));
        String ident = Util.makeDescriptionId("blueprint", id);
        ResourceLocation blueprintTexture = id.withPrefix("textures/blueprint/").withSuffix(".png");
        Blueprint.Builder builder = Blueprint.builder().icon(icon).ident(ident).blueprintTexture(blueprintTexture);
        return REGISTER.register(name, builder);
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
