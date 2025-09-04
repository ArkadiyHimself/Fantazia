package net.arkadiyhimself.fantazia.data.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.item.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.common.item.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.data.tags.FTZBlockTags;
import net.arkadiyhimself.fantazia.data.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
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
        tag(FTZItemTags.ANCIENT_FLAME_ENCHANTABLE).addTag(ItemTags.FIRE_ASPECT_ENCHANTABLE);
        tag(FTZItemTags.MELEE_BLOCK).add(FTZItems.FRAGILE_BLADE.asItem(), FTZItems.MURASAMA.asItem()).addTag(ItemTags.SWORDS);
        tag(FTZItemTags.NO_DISINTEGRATION).add(Items.NETHER_STAR);
        tag(FTZItemTags.FROM_OBSCURE_TREE).add(FTZItems.OBSCURE_SIGN.asItem(), FTZItems.OBSCURE_HANGING_SIGN.asItem(), FTZItems.OBSCURE_BOAT.asItem(), FTZItems.OBSCURE_CHEST_BOAT.asItem());
        tag(FTZItemTags.FROM_FANTAZIUM).add(FTZItems.FANTAZIUM_INGOT.asItem(), FTZItems.RAW_FANTAZIUM.asItem());
        tag(Tags.Items.RANGED_WEAPON_TOOLS).add(Items.CROSSBOW, Items.BOW, Items.TRIDENT).addTag(FTZItemTags.HATCHETS);
        tag(FTZItemTags.DISABLED_BY_DISARM).addTag(Tags.Items.RANGED_WEAPON_TOOLS);
        tag(FTZItemTags.SLOWED_BY_FROZEN).add(Items.BOW, Items.CROSSBOW, Items.BRUSH);
        tag(FTZItemTags.INGOTS_FANTAZIUM).add(FTZItems.FANTAZIUM_INGOT.value());
        tag(FTZItemTags.RAW_MATERIALS_FANTAZIUM).add(FTZItems.RAW_FANTAZIUM.value());

        IntrinsicTagAppender<Item> activecaster = tag(FTZItemTags.CURIOS_ACTIVECASTER);
        IntrinsicTagAppender<Item> passivecaster = tag(FTZItemTags.CURIOS_PASSIVECASTER);
        IntrinsicTagAppender<Item> caster = tag(FTZItemTags.CASTER_ENCHANTABLE);
        for (DeferredHolder<Item, ? extends Item> itemHolder : FTZItems.REGISTER.getEntries()) {
            Item item = itemHolder.value();
            if (FantazicUtil.isActiveCaster(item)) activecaster.add(item);
            if (FantazicUtil.isPassiveCaster(item)) passivecaster.add(item);
            if (item instanceof AuraCasterItem || item instanceof SpellCasterItem) caster.add(item);
        }

        // neo forge
        tag(Tags.Items.ENCHANTING_FUELS).add(
                FTZItems.OBSCURE_SUBSTANCE.asItem()
        );

        tag(Tags.Items.DUSTS).add(
                FTZItems.OBSCURE_SUBSTANCE.value()
        );

        tag(Tags.Items.FOODS).add(
                FTZItems.ARACHNID_EYE.value()
        );

        tag(Tags.Items.FOODS_FRUIT).add(
                FTZItems.VITALITY_FRUIT.value()
        );

        tag(Tags.Items.INGOTS).addTag(
                FTZItemTags.INGOTS_FANTAZIUM
        );

        tag(Tags.Items.RAW_MATERIALS).addTag(
                FTZItemTags.RAW_MATERIALS_FANTAZIUM
        );

        tag(Tags.Items.RANGED_WEAPON_TOOLS).addTag(
                FTZItemTags.HATCHETS
        );

        tag(Tags.Items.ENCHANTABLES).addTag(
                FTZItemTags.HATCHETS
        );

        // minecraft
        tag(ItemTags.BOATS).add(FTZItems.OBSCURE_BOAT.asItem());
        tag(ItemTags.CHEST_BOATS).add(FTZItems.OBSCURE_CHEST_BOAT.asItem());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(FTZItems.FANTAZIUM_INGOT.asItem());
        tag(ItemTags.DURABILITY_ENCHANTABLE).addTag(FTZItemTags.HATCHETS).add(
                FTZItems.FRAGILE_BLADE.asItem(),
                FTZItems.MURASAMA.asItem()
        );

        tag(FTZItemTags.AXE_ENCHANTABLE).addTag(ItemTags.AXES);
    }

    private void copyFromBlocks() {
        copy(FTZBlockTags.OBSCURE_LOGS, FTZItemTags.OBSCURE_LOGS);
        copy(FTZBlockTags.FANTAZIUM_ORES, FTZItemTags.FANTAZIUM_ORES);
        copy(FTZBlockTags.FROM_OBSCURE_TREE, FTZItemTags.FROM_OBSCURE_TREE);
        copy(FTZBlockTags.FROM_FANTAZIUM, FTZItemTags.FROM_FANTAZIUM);

        copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
        copy(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
        copy(Tags.Blocks.ORES, Tags.Items.ORES);

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
