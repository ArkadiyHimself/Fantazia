package net.arkadiyhimself.fantazia.Datagen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ItemModelGeneration extends ItemModelProvider {
    public ItemModelGeneration(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

    }
    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Fantazia.MODID, "item/" + item.getId().getPath()));
    }
}
