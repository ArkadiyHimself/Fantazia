package net.arkadiyhimself.fantazia.data.datagen.model;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.registries.custom.Runes;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class FantazicRuneModelProvider extends ModelProvider<ItemModelBuilder> {

    public FantazicRuneModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Fantazia.MODID, "rune", ItemModelBuilder::new, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (DeferredHolder<Rune, ? extends Rune> rune : Runes.REGISTER.getEntries()) {
            ResourceLocation texture = rune.getId().withPrefix("rune/");
            getBuilder(rune.getId().toString()).texture("layer0", texture).parent(new ModelFile.ExistingModelFile(mcLoc("item/generated"), existingFileHelper));
        }
    }

    @Override
    public @NotNull String getName() {
        return "Rune model provider: Fantazia";
    }
}
