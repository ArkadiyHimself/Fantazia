package net.arkadiyhimself.fantazia.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.tags.FTZBlockTags;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FantazicItemTagsProvider extends ItemTagsProvider {


    public FantazicItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        // fantazia
        copyFromBlocks();
        tag(FTZItemTags.CURIOS_RUNE).add(FTZItems.RUNE_WIELDER.asItem());
        tag(FTZItemTags.CURIOS_DASHSTONE).add(FTZItems.DASHSTONE.asItem());
        tag(FTZItemTags.HATCHETS).add(FTZItems.WOODEN_HATCHET.asItem(), FTZItems.STONE_HATCHET.asItem(), FTZItems.GOLDEN_HATCHET.asItem(), FTZItems.IRON_HATCHET.asItem(), FTZItems.DIAMOND_HATCHET.asItem(), FTZItems.NETHERITE_HATCHET.asItem());
        tag(FTZItemTags.HATCHET_ENCHANTABLE).addTag(FTZItemTags.HATCHETS);
        tag(FTZItemTags.MELEE_BLOCK).add(FTZItems.FRAGILE_BLADE.asItem(), FTZItems.MURASAMA.asItem()).addTag(ItemTags.SWORDS);
        tag(FTZItemTags.NO_DISINTEGRATION).add(Items.NETHER_STAR);
        tag(FTZItemTags.FROM_OBSCURE_TREE).add(FTZItems.OBSCURE_SIGN.asItem(), FTZItems.OBSCURE_HANGING_SIGN.asItem(), FTZItems.OBSCURE_BOAT.asItem(), FTZItems.OBSCURE_CHEST_BOAT.asItem());
        tag(FTZItemTags.FROM_FANTAZIUM).add(FTZItems.FANTAZIUM_INGOT.asItem(), FTZItems.RAW_FANTAZIUM.asItem());
        tag(FTZItemTags.RANGED_WEAPON).add(Items.CROSSBOW, Items.BOW, Items.TRIDENT).addTag(FTZItemTags.HATCHETS);

        IntrinsicTagAppender<Item> activecaster = tag(FTZItemTags.CURIOS_ACTIVECASTER);
        IntrinsicTagAppender<Item> passivecaster = tag(FTZItemTags.CURIOS_PASSIVECASTER);
        for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
            if (FantazicUtil.isActiveCaster(item)) activecaster.add(item);
            if (FantazicUtil.isPassiveCaster(item)) passivecaster.add(item);
        }

        // minecraft
        tag(ItemTags.BOATS).add(FTZItems.OBSCURE_BOAT.asItem());
        tag(ItemTags.CHEST_BOATS).add(FTZItems.OBSCURE_CHEST_BOAT.asItem());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(FTZItems.FANTAZIUM_INGOT.asItem());
    }

    private void copyFromBlocks() {
        copy(FTZBlockTags.OBSCURE_LOGS, FTZItemTags.OBSCURE_LOGS);
        copy(FTZBlockTags.FANTAZIUM_ORES, FTZItemTags.FANTAZIUM_ORES);
        copy(FTZBlockTags.FROM_OBSCURE_TREE, FTZItemTags.FROM_OBSCURE_TREE);
        copy(FTZBlockTags.FROM_FANTAZIUM, FTZItemTags.FROM_FANTAZIUM);

        copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_SLABS , ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
    }
}
