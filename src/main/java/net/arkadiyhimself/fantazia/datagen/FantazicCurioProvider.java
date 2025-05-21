package net.arkadiyhimself.fantazia.datagen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.curio.CurioValidator;
import net.arkadiyhimself.fantazia.api.curio.FTZSlots;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

public class FantazicCurioProvider extends CuriosDataProvider {

    public FantazicCurioProvider(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
        super(Fantazia.MODID, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider provider, ExistingFileHelper existingFileHelper) {
        simpleSlots(FTZSlots.ACTIVECASTER, 2, CurioValidator.FOR_ACTIVECASTER);
        simpleSlots(FTZSlots.PASSIVECASTER, 2, CurioValidator.FOR_PASSIVECASTER);
        simpleSlots(FTZSlots.DASHSTONE, 1, CurioValidator.FOR_DASHSTONE);
        simpleSlots(FTZSlots.RUNE, 1, CurioValidator.FOR_RUNE);

        createEntities("player").addEntities(EntityType.PLAYER).addSlots(FTZSlots.ACTIVECASTER, FTZSlots.PASSIVECASTER, FTZSlots.DASHSTONE, FTZSlots.RUNE);
    }

    private void simpleSlots(String name, int size, CurioValidator validator) {
        createSlot(name).icon(Fantazia.res("slot/" + name)).size(size).addValidator(validator.id());
    };
}
