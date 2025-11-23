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
    public static final DeferredBlueprint<Blueprint> MINERS_PRINT = register("miners_print");

    private static DeferredBlueprint<Blueprint> register(String name) {
        ResourceLocation id = Fantazia.location(name);
        ModelResourceLocation icon = Fantazia.modelLocation(id.withPrefix("blueprint/"));
        String ident = Util.makeDescriptionId("blueprint", id);
        Blueprint.Builder builder = Blueprint.builder().icon(icon).ident(ident);
        return REGISTER.register(name, builder);
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
