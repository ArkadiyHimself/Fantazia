package net.arkadiyhimself.fantazia.data.datagen.model;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.arkadiyhimself.fantazia.common.registries.custom.Blueprints;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class FantazicBlueprintModelProvider extends ModelProvider<ItemModelBuilder> {

    public FantazicBlueprintModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Fantazia.MODID, "blueprint", ItemModelBuilder::new, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (DeferredHolder<Blueprint, ? extends Blueprint> blueprint : Blueprints.REGISTER.getEntries()) {
            ResourceLocation texture = blueprint.getId().withPrefix("blueprint/");
            getBuilder(blueprint.getId().toString()).texture("layer0", texture).parent(new ModelFile.ExistingModelFile(mcLoc("item/generated"), existingFileHelper));
        }
    }

    @Override
    public @NotNull String getName() {
        return "Blueprint model provider: Fantazia";
    }
}
