package net.arkadiyhimself.fantazia.datagen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class FantazicItemModelProvider extends ItemModelProvider {

    public FantazicItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (DeferredItem<?> simpleItem : FTZItems.SIMPLE_ITEMS) basicItem(simpleItem.asItem());
        for (DeferredItem<?> spellCasterItem : FTZItems.SPELL_CASTERS) simpleSpellCaster(spellCasterItem);
        for (DeferredItem<?> auraCasterItem : FTZItems.AURA_CASTERS) simpleAuraCaster(auraCasterItem);
        for (DeferredItem<?> dashStone : FTZItems.DASHSTONES) simpleDashStone(dashStone);
        for (DeferredItem<?> weapon : FTZItems.WEAPONS) simpleWeapon(weapon);

        basicItem(FTZItems.FANTAZIC_PAINTING.value());
    }

    private void simpleSpellCaster(DeferredItem<?> deferredItem) {
        ResourceLocation location = deferredItem.getId();
        getBuilder(location.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/spellcaster/" + location.getPath()));
    }

    private void simpleAuraCaster(DeferredItem<?> deferredItem) {
        ResourceLocation location = deferredItem.getId();
        getBuilder(location.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/auracaster/" + location.getPath()));
    }

    private void simpleDashStone(DeferredItem<?> deferredItem) {
        ResourceLocation location = deferredItem.getId();
        getBuilder(location.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/dashstone/" + location.getPath()));
    }

    private void simpleWeapon(DeferredItem<?> deferredItem) {
        ResourceLocation location = deferredItem.getId();
        getBuilder(location.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/weapons/" + location.getPath()));
    }
}
