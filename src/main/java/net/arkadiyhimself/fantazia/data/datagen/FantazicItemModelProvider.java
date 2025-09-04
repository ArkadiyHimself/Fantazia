package net.arkadiyhimself.fantazia.data.datagen;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class FantazicItemModelProvider extends ItemModelProvider {

    Map<ResourceLocation, Supplier<JsonElement>> map1 = Maps.newHashMap();

    private final BiConsumer<ResourceLocation, Supplier<JsonElement>> output = (p_339374_, p_339375_) -> {
        Supplier<JsonElement> supplier = map1.put(p_339374_, p_339375_);
        if (supplier != null) {
            throw new IllegalStateException("Duplicate model definition for " + p_339374_);
        }
    };



    public FantazicItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        /*
        for (DeferredItem<?> simpleItem : FTZItems.SIMPLE_ITEMS) basicItem(simpleItem.asItem());
        //for (DeferredItem<?> spellCasterItem : FTZItems.SPELL_CASTERS) simpleSpellCaster(spellCasterItem);
        for (DeferredItem<?> auraCasterItem : FTZItems.AURA_CASTERS) simpleAuraCaster(auraCasterItem);
        for (DeferredItem<?> weapon : FTZItems.WEAPONS) simpleWeapon(weapon);

        basicItem(FTZItems.FANTAZIC_PAINTING.value());
        dashStone();
        roamersCompass();
         */
    }

    private void simpleSpellCaster(DeferredItem<?> deferredItem) {
        ResourceLocation location = deferredItem.getId();
        getBuilder(location.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", Fantazia.location("item/spellcaster/" + location.getPath()));
    }

    private void simpleAuraCaster(DeferredItem<?> deferredItem) {
        ResourceLocation location = deferredItem.getId();
        getBuilder(location.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", Fantazia.location("item/auracaster/" + location.getPath()));
    }

    private void simpleWeapon(DeferredItem<?> deferredItem) {
        ResourceLocation location = deferredItem.getId();
        getBuilder(location.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", Fantazia.location("item/weapons/" + location.getPath()));
    }

    private void dashStone() {
        ResourceLocation location = FTZItems.DASHSTONE.getId();
        getBuilder(location.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"));
    }

    private void roamersCompass() {
        for (int i = 0; i < 32; i++) {
            if (i != 16) {
                generateFlatItem(FTZItems.ROAMERS_COMPASS.value(), String.format(Locale.ROOT, "_%02d", i), ModelTemplates.FLAT_ITEM);
            }
        }
    }

    private void generateFlatItem(Item item, String modelLocationSuffix, ModelTemplate modelTemplate) {
        modelTemplate.create(
                ModelLocationUtils.getModelLocation(item, modelLocationSuffix), TextureMapping.layer0(TextureMapping.getItemTexture(item, modelLocationSuffix)), this.output
        );
    }
}
