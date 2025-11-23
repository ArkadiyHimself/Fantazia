package net.arkadiyhimself.fantazia.data.datagen.recipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.item.EngineeringTableBlock;
import net.arkadiyhimself.fantazia.common.item.RuneWielderItem;
import net.arkadiyhimself.fantazia.common.item.TheWorldlinessItem;
import net.arkadiyhimself.fantazia.common.item.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.arkadiyhimself.fantazia.common.registries.FTZEnchantments;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.common.registries.custom.Runes;
import net.arkadiyhimself.fantazia.data.recipes.AmplificationRecipeBuilder;
import net.arkadiyhimself.fantazia.data.recipes.EnchantmentReplaceRecipeBuilder;
import net.arkadiyhimself.fantazia.data.recipes.RuneCarvingRecipeBuilder;
import net.arkadiyhimself.fantazia.data.tags.FTZItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

public class FantazicRecipeProvider extends RecipeProvider {

    public static final ImmutableList<ItemLike> FANTAZIUM_SMELTABLES;
    private static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> SHAPE_BUILDERS;
    private static final BlockFamily OBSCURE_PLANKS;

    private final CompletableFuture<HolderLookup.Provider> registries;

    public FantazicRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
        this.registries = registries;
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // fantazium family
        oreSmelting(recipeOutput, FANTAZIUM_SMELTABLES, RecipeCategory.MISC, FTZItems.FANTAZIUM_INGOT,1.5f,200,"fantazium_ingot");
        oreBlasting(recipeOutput, FANTAZIUM_SMELTABLES, RecipeCategory.MISC, FTZItems.FANTAZIUM_INGOT,2F,100,"fantazium_ingot");
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, FTZItems.RAW_FANTAZIUM, RecipeCategory.BUILDING_BLOCKS, FTZBlocks.RAW_FANTAZIUM_BLOCK);
        nineBlockStorageRecipesRecipesWithCustomUnpacking(
                recipeOutput,
                RecipeCategory.MISC,
                FTZItems.FANTAZIUM_INGOT,
                RecipeCategory.BUILDING_BLOCKS,
                FTZBlocks.FANTAZIUM_BLOCK,
                "fantazium_ingot_from_fantazium_block",
                "fantazium_ingot");

        // obscure tree family
        woodenBoat(recipeOutput, FTZItems.OBSCURE_BOAT, FTZBlocks.OBSCURE_PLANKS);
        chestBoat(recipeOutput, FTZItems.OBSCURE_CHEST_BOAT, FTZItems.OBSCURE_BOAT);
        generateRecipes(recipeOutput, OBSCURE_PLANKS);
        hangingSign(recipeOutput, FTZItems.OBSCURE_HANGING_SIGN, FTZBlocks.STRIPPED_OBSCURE_LOG);
        woodFromLogs(recipeOutput, FTZBlocks.OBSCURE_WOOD, FTZBlocks.OBSCURE_LOG);
        woodFromLogs(recipeOutput, FTZBlocks.STRIPPED_OBSCURE_WOOD, FTZBlocks.STRIPPED_OBSCURE_LOG);
        planksFromLogs(recipeOutput, FTZBlocks.OBSCURE_PLANKS, FTZItemTags.OBSCURE_LOGS, 4);

        // expendables
        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.OBSCURE_SUBSTANCE)
                .pattern("##")
                .pattern("##")
                .define('#', FTZBlocks.OBSCURE_LEAVES)
                .save(recipeOutput, Fantazia.location("obscure_leaves_into_obscure_substance"));

        // weapons
        hatchetRecipe(FTZItems.WOODEN_HATCHET, recipeOutput);
        hatchetRecipe(FTZItems.STONE_HATCHET, recipeOutput);
        hatchetRecipe(FTZItems.IRON_HATCHET, recipeOutput);
        hatchetRecipe(FTZItems.GOLDEN_HATCHET, recipeOutput);
        hatchetRecipe(FTZItems.DIAMOND_HATCHET, recipeOutput);
        netheriteSmithingNoAdvancement(recipeOutput, FTZItems.DIAMOND_HATCHET.asItem(), FTZItems.NETHERITE_HATCHET.asItem());
        ShapedRecipeNoAdvancement.shaped(RecipeCategory.COMBAT, FTZItems.FRAGILE_BLADE)
                .pattern("X#X")
                .pattern("X#X")
                .pattern("BHB")
                .define('#', Items.GLASS)
                .define('X', FTZItems.OBSCURE_SUBSTANCE)
                .define('H', Items.NETHERITE_INGOT)
                .define('B', FTZItems.FANTAZIUM_INGOT)
                .save(recipeOutput);

        // stuff
        runeCarving(recipeOutput, registries);
        try {
            amplification(recipeOutput, registries);
            enchantmentReplace(recipeOutput, registries);
        } catch (ExecutionException | InterruptedException ignored) {}
        ShapedRecipeNoAdvancement.shaped(RecipeCategory.DECORATIONS, RuneWielderItem.rune(Runes.EMPTY))
                .pattern("O#O")
                .define('O', FTZItems.OBSCURE_SUBSTANCE)
                .define('#', Items.STONE)
                .save(recipeOutput);
        ShapedRecipeNoAdvancement.shaped(RecipeCategory.DECORATIONS, FTZBlocks.AMPLIFICATION_BENCH)
                .pattern("OTO")
                .pattern("O#O")
                .define('O', Items.OBSIDIAN)
                .define('T', FTZItems.FANTAZIUM_INGOT)
                .define('#', FTZBlocks.OBSCURE_PLANKS)
                .save(recipeOutput);
        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.AMPLIFIER)
                .pattern("YH")
                .pattern("HY")
                .define('Y', FTZItems.OBSCURE_SUBSTANCE)
                .define('H', Items.COPPER_INGOT)
                .save(recipeOutput);

        engineeringTable(recipeOutput, FTZBlocks.OAK_ENGINEERING_TABLE, Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG, BlockFamilies.OAK_PLANKS);
        engineeringTable(recipeOutput, FTZBlocks.SPRUCE_ENGINEERING_TABLE, Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG, BlockFamilies.SPRUCE_PLANKS);
        engineeringTable(recipeOutput, FTZBlocks.BIRCH_ENGINEERING_TABLE, Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG, BlockFamilies.BIRCH_PLANKS);
        engineeringTable(recipeOutput, FTZBlocks.JUNGLE_ENGINEERING_TABLE, Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG, BlockFamilies.JUNGLE_PLANKS);
        engineeringTable(recipeOutput, FTZBlocks.ACACIA_ENGINEERING_TABLE, Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG, BlockFamilies.ACACIA_PLANKS);
        engineeringTable(recipeOutput, FTZBlocks.CHERRY_ENGINEERING_TABLE, Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG, BlockFamilies.CHERRY_PLANKS);
        engineeringTable(recipeOutput, FTZBlocks.DARK_OAK_ENGINEERING_TABLE, Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG, BlockFamilies.DARK_OAK_PLANKS);
        engineeringTable(recipeOutput, FTZBlocks.MANGROVE_ENGINEERING_TABLE, Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG, BlockFamilies.MANGROVE_PLANKS);
        engineeringTable(recipeOutput, FTZBlocks.BAMBOO_ENGINEERING_TABLE, Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK, BlockFamilies.BAMBOO_PLANKS);
        engineeringTable(recipeOutput, FTZBlocks.CRIMSON_ENGINEERING_TABLE, Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM, BlockFamilies.CRIMSON_PLANKS);
        engineeringTable(recipeOutput, FTZBlocks.WARPED_ENGINEERING_TABLE, Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM, BlockFamilies.WARPED_PLANKS);
        engineeringTable(recipeOutput, FTZBlocks.OBSCURE_ENGINEERING_TABLE, FTZBlocks.OBSCURE_LOG.value(), FTZBlocks.STRIPPED_OBSCURE_LOG.value(), OBSCURE_PLANKS);

        // artifacts
        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.AMPLIFIED_ICE)
                .pattern(" Y ")
                .pattern("H#H")
                .pattern(" Y ")
                .define('#', Items.ICE)
                .define('H', FTZItems.OBSCURE_SUBSTANCE)
                .define('Y', Items.SNOWBALL)
                .save(recipeOutput);

        ShapelessRecipeNoAdvancement.shapeless(RecipeCategory.MISC, FTZItems.ANCIENT_SPARK, 3)
                .requires(Items.GUNPOWDER)
                .requires(FTZItems.OBSCURE_SUBSTANCE)
                .requires(Ingredient.of(Items.COAL, Items.CHARCOAL))
                .save(recipeOutput);

        ShapelessRecipeNoAdvancement.shapeless(RecipeCategory.MISC, FTZItems.ARACHNID_EYE)
                .requires(Items.SPIDER_EYE)
                .requires(Items.COBWEB)
                .requires(FTZItems.OBSCURE_SUBSTANCE)
                .save(recipeOutput);

        ShapelessRecipeNoAdvancement.shapeless(RecipeCategory.MISC, FTZItems.CARD_DECK)
                .requires(Ingredient.of(Items.INK_SAC, Items.GLOW_INK_SAC))
                .requires(Items.PAPER)
                .requires(FTZItems.OBSCURE_SUBSTANCE)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.CONTAINED_SOUND)
                .pattern("YHY")
                .pattern("X#X")
                .pattern("YHY")
                .define('#', Items.SCULK_SHRIEKER)
                .define('H', FTZItems.OBSCURE_SUBSTANCE)
                .define('X', Items.GLASS)
                .define('Y', Items.IRON_NUGGET)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.ENIGMATIC_CLOCK)
                .pattern("Y#Y")
                .pattern("#X#")
                .pattern("Y#Y")
                .define('X', Items.CLOCK)
                .define('#', FTZItems.FANTAZIUM_INGOT)
                .define('Y', FTZItems.OBSCURE_SUBSTANCE)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.ENTANGLER)
                .pattern("HYH")
                .pattern("X#X")
                .pattern("H H")
                .define('#', Items.TOTEM_OF_UNDYING)
                .define('X', FTZItems.OBSCURE_SUBSTANCE)
                .define('H', Items.COPPER_INGOT)
                .define('Y', FTZItems.FANTAZIUM_INGOT)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FTZItems.FANTAZIC_PAINTING)
                .pattern("Y#Y")
                .pattern("#B#")
                .pattern("Y#Y")
                .define('B', Items.PAPER)
                .define('#', FTZBlocks.OBSCURE_PLANKS)
                .define('Y', Items.STICK)
                .unlockedBy("has_planks", has(FTZBlocks.OBSCURE_PLANKS)).save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.MYSTIC_MIRROR)
                .pattern(" XX")
                .pattern("H#X")
                .pattern("YH ")
                .define('#', Items.GLASS)
                .define('H', FTZItems.OBSCURE_SUBSTANCE)
                .define('X', FTZItems.FANTAZIUM_INGOT)
                .define('Y', Items.DIAMOND)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.NECKLACE_OF_CLAIRVOYANCE)
                .pattern("XXX")
                .pattern("X X")
                .pattern(" H ")
                .define('X', Items.IRON_NUGGET)
                .define('H', FTZItems.FANTAZIUM_INGOT)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.NIMBLE_DAGGER)
                .pattern(" W ")
                .pattern("RHR")
                .pattern(" # ")
                .define('H', FTZItems.FANTAZIUM_INGOT)
                .define('#', Items.STICK)
                .define('W', Items.ENDER_PEARL)
                .define('R', FTZItems.OBSCURE_SUBSTANCE)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.OPTICAL_LENS)
                .pattern(" HX")
                .pattern("Y#Y")
                .pattern("XH ")
                .define('#', Items.GLASS_PANE)
                .define('H', FTZItems.OBSCURE_SUBSTANCE)
                .define('X', FTZItems.FANTAZIUM_INGOT)
                .define('Y', Items.COPPER_INGOT)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.PUPPET_DOLL)
                .pattern(" YH")
                .pattern("H#H")
                .pattern("HY ")
                .define('#', Items.ARMOR_STAND)
                .define('H', FTZItems.OBSCURE_SUBSTANCE)
                .define('Y', FTZItems.FANTAZIUM_INGOT)
                .save(recipeOutput);

        ShapelessRecipeNoAdvancement.shapeless(RecipeCategory.MISC, FTZItems.ROAMERS_COMPASS)
                .requires(FTZItems.OBSCURE_SUBSTANCE)
                .requires(Items.COMPASS)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.RUSTY_RING)
                .pattern(" W ")
                .pattern("HRH")
                .pattern(" H ")
                .define('H', Items.IRON_NUGGET)
                .define('W', Items.COPPER_INGOT)
                .define('R', FTZItems.OBSCURE_SUBSTANCE)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.SANDMANS_DUST)
                .pattern(" Y ")
                .pattern("H#H")
                .pattern(" Y ")
                .define('#', Items.SAND)
                .define('H', FTZItems.OBSCURE_SUBSTANCE)
                .define('Y', FTZItems.FANTAZIUM_INGOT)
                .save(recipeOutput);

        ShapelessRecipeNoAdvancement.shapeless(RecipeCategory.MISC, TheWorldlinessItem.itemStack())
                .requires(FTZItems.OBSCURE_SUBSTANCE)
                .requires(Items.BOOK)
                .save(recipeOutput, FTZItems.THE_WORLDLINESS.getId().toString());

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.UNFINISHED_WINGS)
                .pattern("XXX")
                .pattern("#YX")
                .pattern("Y#X")
                .define('X', Items.FEATHER)
                .define('#', Items.PHANTOM_MEMBRANE)
                .define('Y', FTZItems.OBSCURE_SUBSTANCE)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.WISDOM_CATCHER)
                .pattern("#B#")
                .pattern("#B#")
                .pattern(" H ")
                .define('B', FTZItems.FANTAZIUM_INGOT)
                .define('#', Items.GOLD_INGOT)
                .define('H', Items.GLASS_BOTTLE)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.ENDER_POCKET)
                .pattern(" U ")
                .pattern("UHU")
                .pattern(" U ")
                .define('U', FTZItems.FANTAZIUM_INGOT)
                .define('H', Items.ENDER_CHEST)
                .save(recipeOutput);

        ShapelessRecipeNoAdvancement.shapeless(RecipeCategory.MISC, FTZItems.OMINOUS_BELL)
                .requires(Items.BELL)
                .requires(Items.BONE_MEAL)
                .requires(FTZItems.OBSCURE_SUBSTANCE)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.ABNORMAL_BEEHIVE)
                .pattern("U#U")
                .pattern("UYU")
                .define('Y', FTZItems.OBSCURE_SUBSTANCE)
                .define('U', Items.IRON_NUGGET)
                .define('#', Items.BEE_NEST)
                .save(recipeOutput);

        // rechargeable tools
        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.PIMPILLO)
                .pattern(" Y ")
                .pattern("YXY")
                .pattern("#H#")
                .define('X', Items.GUNPOWDER)
                .define('Y', Items.STRING)
                .define('H', Items.LEATHER)
                .define('#', FTZItems.OBSCURE_SUBSTANCE)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.THROWING_PIN)
                .pattern(" X ")
                .pattern("XYX")
                .pattern("XHX")
                .define('X', Items.IRON_NUGGET)
                .define('Y', Items.STICK)
                .define('H', FTZItems.FANTAZIUM_INGOT)
                .save(recipeOutput);

        ShapedRecipeNoAdvancement.shaped(RecipeCategory.MISC, FTZItems.BLOCK_FLY)
                .pattern("JXJ")
                .pattern("HUH")
                .define('H', Items.COPPER_INGOT)
                .define('U', Items.REDSTONE)
                .define('J', FTZItems.FANTAZIUM_INGOT)
                .define('X', Items.SOUL_TORCH)
                .save(recipeOutput);
    }

    protected static void oreSmelting(@NotNull RecipeOutput recipeOutput, List<ItemLike> pIngredients, @NotNull RecipeCategory pCategory,
                                      @NotNull ItemLike pResult, float pExperience, int pCookingTIme, @NotNull String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients,
                pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(@NotNull RecipeOutput recipeOutput, List<ItemLike> pIngredients, @NotNull RecipeCategory pCategory,
                                      @NotNull ItemLike pResult, float pExperience, int pCookingTime, @NotNull String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients,
                pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(@NotNull RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer,
                                                                       AbstractCookingRecipe.@NotNull Factory<T> factory, List<ItemLike> pIngredients,
                                                                       @NotNull RecipeCategory pCategory, @NotNull ItemLike pResult, float pExperience,
                                                                       int pCookingTime, @NotNull String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, Fantazia.MODID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }

    protected static void nineBlockStorageRecipes(@NotNull RecipeOutput recipeOutput, @NotNull RecipeCategory unpackedCategory, ItemLike unpacked, @NotNull RecipeCategory packedCategory, ItemLike packed) {
        nineBlockStorageRecipes(recipeOutput, unpackedCategory, unpacked, packedCategory, packed, getSimpleName(packed), null, getSimpleName(unpacked), null);
    }

    protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(@NotNull RecipeOutput recipeOutput, @NotNull RecipeCategory unpackedCategory, ItemLike unpacked, @NotNull RecipeCategory packedCategory, ItemLike packed, String unpackedName, @NotNull String unpackedGroup) {
        nineBlockStorageRecipes(recipeOutput, unpackedCategory, unpacked, packedCategory, packed, getSimpleName(packed), getSimpleName(packed) ,Fantazia.MODID + ":" + unpackedName,Fantazia.MODID + ":" + unpackedGroup);
    }

    private static void hatchetRecipe(DeferredItem<HatchetItem> hatchetItem, RecipeOutput recipeOutput) {
        ShapedRecipeNoAdvancement.shaped(RecipeCategory.COMBAT, hatchetItem)
                .pattern("X ")
                .pattern("X#")
                .pattern(" #")
                .define('#', Items.STICK)
                .define('X', hatchetItem.value().getTier().getRepairIngredient())
                .save(recipeOutput);
    }

    protected static void netheriteSmithingNoAdvancement(@NotNull RecipeOutput recipeOutput, @NotNull Item ingredientItem, Item resultItem) {
        SmithingTransformRecipeNoAdvancement.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ingredientItem), Ingredient.of(Items.NETHERITE_INGOT), resultItem).save(recipeOutput, Fantazia.location(getItemName(resultItem) + "_smithing"));
    }

    private static String getSimpleName(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).toString();
    }

    private static BlockFamily.Builder familyBuilder(Block baseBlock) {
        return new BlockFamily.Builder(baseBlock);
    }

    protected static void generateRecipes(@NotNull RecipeOutput recipeOutput, BlockFamily blockFamily) {
        blockFamily.getVariants().forEach((variant, block) -> {
            BiFunction<ItemLike, ItemLike, RecipeBuilder> bifunction = SHAPE_BUILDERS.get(variant);
            ItemLike itemlike = getBaseBlock(blockFamily, variant);
            if (bifunction != null) {
                RecipeBuilder recipebuilder = bifunction.apply(block, itemlike);
                blockFamily.getRecipeGroupPrefix().ifPresent(string -> recipebuilder.group(string + (variant == BlockFamily.Variant.CUT ? "" : "_" + variant.getRecipeGroup())));
                recipebuilder.unlockedBy(blockFamily.getRecipeUnlockedBy().orElseGet(() -> getHasName(itemlike)), has(itemlike));
                recipebuilder.save(recipeOutput);
            }

            if (variant == BlockFamily.Variant.CRACKED) {
                smeltingResultFromBase(recipeOutput, block, itemlike);
            }
        });
    }

    private static void runeCarving(RecipeOutput output, CompletableFuture<HolderLookup.Provider> registries){
        RuneCarvingRecipeBuilder.carving(Runes.NOISELESS, 9, 35)
                .requires(Items.SCULK_SHRIEKER)
                .save(output);

        RuneCarvingRecipeBuilder.carving(Runes.PROSPERITY, 20, 70)
                .requires(Items.DIAMOND, 3)
                .requires(Items.GOLD_INGOT, 3)
                .save(output);

        RuneCarvingRecipeBuilder.carving(Runes.PIERCER, 13, 50)
                .requires(Items.CROSSBOW)
                .save(output);

        RuneCarvingRecipeBuilder.carving(Runes.PURE_VESSEL, 10, 30)
                .requires(Items.WIND_CHARGE)
                .save(output);

        RuneCarvingRecipeBuilder.carving(Runes.EXTENSION, 15, 45)
                .requires(Items.REDSTONE, 6)
                .save(output);

        RuneCarvingRecipeBuilder.carving(Runes.AEROBAT, 10, 25)
                .requires(Items.FEATHER, 2)
                .requires(Items.PHANTOM_MEMBRANE, 2)
                .save(output);

        RuneCarvingRecipeBuilder.carving(Runes.OMNIDIRECTIONAL, 16, 45)
                .requires(Items.GHAST_TEAR, 2)
                .requires(Items.FEATHER, 2)
                .save(output);

        RuneCarvingRecipeBuilder.carving(Runes.METICULOUS, 18, 55)
                .requires(FTZItems.AMPLIFIER)
                .requires(Items.IRON_SWORD)
                .save(output);

        RuneCarvingRecipeBuilder.carving(Runes.AQUATIC, 15, 35)
                .requires(Items.HEART_OF_THE_SEA)
                .save(output);
    }

    private static void amplification(RecipeOutput output, CompletableFuture<HolderLookup.Provider> registries) throws ExecutionException, InterruptedException {
        HolderLookup.Provider provider = registries.get();

        AmplificationRecipeBuilder.amplification(
                provider.holderOrThrow(FTZEnchantments.AMPLIFICATION), 15, 30
                )
                .requires(FTZItems.AMPLIFIER)
                .save(output);

        AmplificationRecipeBuilder.amplification(
                provider.holderOrThrow(Enchantments.LOOTING), 20, 45, 5
                )
                .requires(FTZItems.AMPLIFIER)
                .requires(Items.ROTTEN_FLESH)
                .requires(Items.BONE)
                .requires(Items.GUNPOWDER)
                .requires(Items.SPIDER_EYE)
                .requires(Items.ENDER_PEARL)
                .requires(Items.GHAST_TEAR)
                .requires(Items.BLAZE_ROD)
                .requires(Items.PHANTOM_MEMBRANE)
                .save(output, true);

        AmplificationRecipeBuilder.amplification(
                provider.holderOrThrow(Enchantments.FORTUNE), 20, 45, 5
                )
                .requires(FTZItems.AMPLIFIER)
                .requires(new ItemStack(Items.RAW_IRON), new ItemStack(Items.IRON_INGOT))
                .requires(new ItemStack(Items.RAW_GOLD), new ItemStack(Items.GOLD_INGOT))
                .requires(new ItemStack(Items.RAW_COPPER), new ItemStack(Items.COPPER_INGOT))
                .requires(Items.DIAMOND)
                .requires(Items.REDSTONE)
                .requires(Items.LAPIS_LAZULI)
                .requires(Items.QUARTZ)
                .requires(Items.COAL)
                .save(output, true);


        AmplificationRecipeBuilder.amplification(
                provider.holderOrThrow(Enchantments.UNBREAKING), 25, 50, 5
                )
                .requires(FTZItems.AMPLIFIER)
                .requires(Items.DIAMOND, 2)
                .requires(FTZItems.FANTAZIUM_INGOT, 2)
                .requires(Items.OBSIDIAN, 2)
                .save(output, true);

        AmplificationRecipeBuilder.amplification(
                provider.holderOrThrow(FTZEnchantments.DISINTEGRATION), 20, 65, 5
                )
                .requires(Items.EXPERIENCE_BOTTLE, 3)
                .requires(Items.LAPIS_LAZULI, 3)
                .requires(FTZItems.AMPLIFIER)
                .save(output, true);

        AmplificationRecipeBuilder.amplification(
                provider.holderOrThrow(FTZEnchantments.EXECUTIONER), 25, 90
                )
                .requires(FTZItems.AMPLIFIER)
                .requires(ItemTags.SKULLS)
                .save(output, true);
    }

    private static void enchantmentReplace(RecipeOutput output, CompletableFuture<HolderLookup.Provider> registries) throws ExecutionException, InterruptedException {
        HolderLookup.Provider provider = registries.get();

        EnchantmentReplaceRecipeBuilder.enchantmentReplace(
                provider.holderOrThrow(Enchantments.FIRE_ASPECT),
                        provider.holderOrThrow(FTZEnchantments.SCORCHED_EARTH),
                        25, 75
                )
                .requires(FTZItems.ANCIENT_SPARK, 3)
                .save(output, "ancient_flame_swords");

        EnchantmentReplaceRecipeBuilder.enchantmentReplace(
                provider.holderOrThrow(Enchantments.FLAME),
                provider.holderOrThrow(FTZEnchantments.INCINERATION),
                25,75
                )
                .requires(FTZItems.ANCIENT_SPARK, 3)
                .save(output, "ancient_flame_bows");
    }

    private static void engineeringTable(
            RecipeOutput output,
            DeferredBlock<EngineeringTableBlock> block,
            Block log,
            Block strippedLog,
            BlockFamily family
    ) {
        Map<BlockFamily.Variant, Block> variants = family.getVariants();
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, block)
                .unlockedBy("has_wood", has(family.getBaseBlock()))
                .unlockedBy("has_iron_block", has(Items.IRON_BLOCK))
                .pattern(" Y ")
                .pattern("UHU")
                .pattern("XXX")
                .define('Y', variants.get(BlockFamily.Variant.SIGN))
                .define('H', strippedLog)
                .define('U', log)
                .define('X', variants.get(BlockFamily.Variant.SLAB))
                .save(output);
    }

    static {
        FANTAZIUM_SMELTABLES =
                ImmutableList.of(
                        FTZBlocks.FANTAZIUM_ORE,
                        FTZBlocks.DEEPSLATE_FANTAZIUM_ORE,
                        FTZItems.RAW_FANTAZIUM
                );

        SHAPE_BUILDERS = ImmutableMap.<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>>builder()
                .put(BlockFamily.Variant.BUTTON, (button, p_176734_) -> buttonBuilder(button, Ingredient.of(p_176734_)))
                .put(BlockFamily.Variant.CHISELED, (p_248037_, p_248038_) -> chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, p_248037_, Ingredient.of(p_248038_)))
                .put(BlockFamily.Variant.CUT, (p_248026_, p_248027_) -> cutBuilder(RecipeCategory.BUILDING_BLOCKS, p_248026_, Ingredient.of(p_248027_)))
                .put(BlockFamily.Variant.DOOR, (p_176714_, p_176715_) -> doorBuilder(p_176714_, Ingredient.of(p_176715_)))
                .put(BlockFamily.Variant.CUSTOM_FENCE, (p_176708_, p_176709_) -> fenceBuilder(p_176708_, Ingredient.of(p_176709_)))
                .put(BlockFamily.Variant.FENCE, (p_248031_, p_248032_) -> fenceBuilder(p_248031_, Ingredient.of(p_248032_)))
                .put(BlockFamily.Variant.CUSTOM_FENCE_GATE, (p_176698_, p_176699_) -> fenceGateBuilder(p_176698_, Ingredient.of(p_176699_)))
                .put(BlockFamily.Variant.FENCE_GATE, (p_248035_, p_248036_) -> fenceGateBuilder(p_248035_, Ingredient.of(p_248036_)))
                .put(BlockFamily.Variant.SIGN, (p_176688_, p_176689_) -> signBuilder(p_176688_, Ingredient.of(p_176689_)))
                .put(BlockFamily.Variant.SLAB, (p_248017_, p_248018_) -> slabBuilder(RecipeCategory.BUILDING_BLOCKS, p_248017_, Ingredient.of(p_248018_)))
                .put(BlockFamily.Variant.STAIRS, (p_176674_, p_176675_) -> stairBuilder(p_176674_, Ingredient.of(p_176675_)))
                .put(BlockFamily.Variant.PRESSURE_PLATE, (p_248039_, p_248040_) -> pressurePlateBuilder(RecipeCategory.REDSTONE, p_248039_, Ingredient.of(p_248040_)))
                .put(BlockFamily.Variant.POLISHED, (p_248019_, p_248020_) -> polishedBuilder(RecipeCategory.BUILDING_BLOCKS, p_248019_, Ingredient.of(p_248020_)))
                .put(BlockFamily.Variant.TRAPDOOR, (p_176638_, p_176639_) -> trapdoorBuilder(p_176638_, Ingredient.of(p_176639_)))
                .put(BlockFamily.Variant.WALL, (p_248024_, p_248025_) -> wallBuilder(RecipeCategory.DECORATIONS, p_248024_, Ingredient.of(p_248025_))).build();

        OBSCURE_PLANKS = familyBuilder(FTZBlocks.OBSCURE_PLANKS.value())
                .button(FTZBlocks.OBSCURE_BUTTON.value())
                .fence(FTZBlocks.OBSCURE_FENCE.value())
                .fenceGate(FTZBlocks.OBSCURE_FENCE_GATE.value())
                .pressurePlate(FTZBlocks.OBSCURE_PRESSURE_PLATE.value())
                .sign(FTZBlocks.OBSCURE_SIGN.value(), FTZBlocks.OBSCURE_WALL_SIGN.value())
                .slab(FTZBlocks.OBSCURE_SLAB.value())
                .stairs(FTZBlocks.OBSCURE_STAIRS.value())
                .door(FTZBlocks.OBSCURE_DOOR.value())
                .trapdoor(FTZBlocks.OBSCURE_TRAPDOOR.value())
                .recipeGroupPrefix("wooden")
                .recipeUnlockedBy("has_planks")
                .getFamily();
    }
}
