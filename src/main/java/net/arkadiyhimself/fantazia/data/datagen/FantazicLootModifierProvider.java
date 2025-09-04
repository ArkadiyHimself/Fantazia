package net.arkadiyhimself.fantazia.data.datagen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.loot.FantazicLootModifier;
import net.arkadiyhimself.fantazia.common.registries.FTZLootModifiers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class FantazicLootModifierProvider extends GlobalLootModifierProvider {

    public FantazicLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Fantazia.MODID);
    }

    @Override
    protected void start() {
        add(FTZLootModifiers.FANTAZIC_LOOT_MODIFIER.getId().getPath(), new FantazicLootModifier(new LootItemCondition[]{}));
    }
}
