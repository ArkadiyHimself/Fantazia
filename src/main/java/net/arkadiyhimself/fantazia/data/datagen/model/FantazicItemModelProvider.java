package net.arkadiyhimself.fantazia.data.datagen.model;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.data_component.WisdomTransferComponent;
import net.arkadiyhimself.fantazia.common.item.EngineeringTableBlock;
import net.arkadiyhimself.fantazia.common.item.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.common.item.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.common.item.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class FantazicItemModelProvider extends ItemModelProvider {

    private final ModelFile generatedItem = new ModelFile.ExistingModelFile(mcLoc("item/generated"), existingFileHelper);
    private final ModelFile builtinEntity = new ModelFile.UncheckedModelFile(mcLoc("builtin/entity"));

    public FantazicItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // most basic models
        simpleSpellCaster(FTZItems.ABNORMAL_BEEHIVE);
        simpleAuraCaster(FTZItems.ACID_BOTTLE);
        simpleAuraCaster(FTZItems.AMPLIFIED_ICE);
        simpleExpendable(FTZItems.AMPLIFIER);
        simpleExpendable(FTZItems.ANCIENT_SPARK);
        simpleSpellCaster(FTZItems.ATHAME);
        simpleSpellCaster(FTZItems.BROKEN_STAFF);
        simpleSpellCaster(FTZItems.CAUGHT_THUNDER);
        simpleSpellCaster(FTZItems.CONTAINED_SOUND);
        basicItem(FTZItems.ENDER_POCKET);
        simpleSpellCaster(FTZItems.ENIGMATIC_CLOCK);
        simpleSpellCaster(FTZItems.ENTANGLER);
        basicItem(FTZItems.FANTAZIC_PAINTING);
        basicItem(FTZItems.FANTAZIUM_INGOT);
        simpleSpellCaster(FTZItems.HEART_OF_SCULK);
        simpleExpendable(FTZItems.INSIGHT_ESSENCE);
        simpleSpellCaster(FTZItems.MYSTIC_MIRROR);
        simpleAuraCaster(FTZItems.NECKLACE_OF_CLAIRVOYANCE);
        simpleAuraCaster(FTZItems.NETHER_HEART);
        simpleSpellCaster(FTZItems.NIMBLE_DAGGER);
        basicItem(FTZItems.OBSCURE_BOAT);
        basicItem(FTZItems.OBSCURE_CHEST_BOAT);
        basicItem(FTZBlocks.OBSCURE_DOOR.asItem());
        basicItem(FTZItems.OBSCURE_HANGING_SIGN);
        basicItem(FTZItems.OBSCURE_SIGN);
        simpleExpendable(FTZItems.OBSCURE_SUBSTANCE);
        simpleSpellCaster(FTZItems.OMINOUS_BELL);
        simpleAuraCaster(FTZItems.OPTICAL_LENS);
        basicItem(FTZItems.RAW_FANTAZIUM);
        simpleSpellCaster(FTZItems.RUSTY_RING);
        simpleSpellCaster(FTZItems.SANDMANS_DUST);
        simpleSpellCaster(FTZItems.SOUL_EATER);
        simpleAuraCaster(FTZItems.SPIRAL_NEMESIS);
        basicItem(FTZItems.THE_WORLDLINESS);
        simpleAuraCaster(FTZItems.TRANQUIL_HERB);
        simpleExpendable(FTZItems.UNFINISHED_WINGS);
        simpleExpendable(FTZItems.VITALITY_FRUIT);
        simpleSpellCaster(FTZItems.WITHERS_QUINTESSENCE);
        basicItem(FTZBlocks.OAK_ENGINEERING_TABLE.asItem());
        basicItem(FTZBlocks.SPRUCE_ENGINEERING_TABLE.asItem());
        basicItem(FTZBlocks.BIRCH_ENGINEERING_TABLE.asItem());
        basicItem(FTZBlocks.JUNGLE_ENGINEERING_TABLE.asItem());
        basicItem(FTZBlocks.ACACIA_ENGINEERING_TABLE.asItem());
        basicItem(FTZBlocks.CHERRY_ENGINEERING_TABLE.asItem());
        basicItem(FTZBlocks.DARK_OAK_ENGINEERING_TABLE.asItem());
        basicItem(FTZBlocks.MANGROVE_ENGINEERING_TABLE.asItem());
        basicItem(FTZBlocks.BAMBOO_ENGINEERING_TABLE.asItem());
        basicItem(FTZBlocks.CRIMSON_ENGINEERING_TABLE.asItem());
        basicItem(FTZBlocks.WARPED_ENGINEERING_TABLE.asItem());
        basicItem(FTZBlocks.OBSCURE_ENGINEERING_TABLE.asItem());

        // overrides or separate transforms
        compassModels(FTZItems.ROAMERS_COMPASS, "spellcaster");
        bloodLustAmulet();
        puppetDoll();
        cardDeck();
        leadersHorn();
        arachnidEye();

        // hatchet
        hatchet(FTZItems.WOODEN_HATCHET);
        hatchet(FTZItems.STONE_HATCHET);
        hatchet(FTZItems.IRON_HATCHET);
        hatchet(FTZItems.GOLDEN_HATCHET);
        hatchet(FTZItems.DIAMOND_HATCHET);
        hatchet(FTZItems.NETHERITE_HATCHET);

        // separate transforms, custom renderer
        textureInGUIModelOtherwise(FTZItems.MURASAMA, Fantazia.location("item/template_murasama"),"weapon");
        textureInGUIModelOtherwise(FTZItems.PIMPILLO, Fantazia.location("item/template_pimpillo"),"rechargeable_tool");
        textureInGUIModelOtherwise(FTZItems.BLOCK_FLY, Fantazia.location("item/template_block_fly"),"rechargeable_tool");
        textureInGUIModelOtherwise(FTZItems.THROWING_PIN, Fantazia.location("item/template_throwing_pin"),"rechargeable_tool");
        fragileBlade();
        dashstones();
        wisdomCatcher();
        customRenderer(FTZItems.BLUEPRINT);
        customRenderer(FTZItems.RUNE_WIELDER);
    }

    private void customRenderer(DeferredItem<?> item) {
        getBuilder(item.getId().toString()).parent(builtinEntity)
                .guiLight(BlockModel.GuiLight.FRONT);
    }

    private void basicItem(DeferredItem<?> item) {
        basicItem(item.asItem());
    }

    private void simpleItemWithTextureLocation(DeferredItem<?> item, String appendix) {
        ResourceLocation itemId = item.getId();
        getBuilder(itemId.toString()).parent(generatedItem)
                .texture("layer0", itemId.withPrefix("item/" + appendix + "/"));
    }

    private void simpleSpellCaster(DeferredItem<? extends SpellCasterItem> item) {
        simpleItemWithTextureLocation(item, "spellcaster");
    }

    private void simpleAuraCaster(DeferredItem<? extends AuraCasterItem> item) {
        simpleItemWithTextureLocation(item, "auracaster");
    }

    private void simpleExpendable(DeferredItem<?> item) {
        simpleItemWithTextureLocation(item, "expendable");
    }

    private void engineeringTable(DeferredBlock<? extends EngineeringTableBlock> block) {
        ResourceLocation itemId = block.getId();
        getBuilder(itemId.toString()).parent(generatedItem)
                .texture("layer0", itemId.withPrefix("item/engineering_table/"));
    }

    private void textureInGUIModelOtherwise(DeferredItem<?> item, ResourceLocation modelTemplate, @Nullable String guiFolder) {
        ResourceLocation baseId = item.getId();
        String modelTextureFolder = "item/models/";

        ResourceLocation model3D = baseId.withPrefix("item/").withSuffix("/model");
        getBuilder(model3D.toString())
                .parent(modelFile(modelTemplate))
                .texture("base", baseId.withPrefix(modelTextureFolder));

        String textureGui = "item/";
        if (guiFolder != null) textureGui += guiFolder + "/";
        ResourceLocation modelGui = baseId.withPrefix("item/").withSuffix("/gui");
        getBuilder(modelGui.toString())
                .parent(generatedItem)
                .texture("layer0", baseId.withPrefix(textureGui));

        ItemModelBuilder modelBuilder = factory.apply(modelGui).parent(modelFile(modelGui));
        getBuilder(baseId.toString()).parent(generatedItem)
                .customLoader(SeparateTransformsModelBuilder::begin)
                .base(factory.apply(model3D).parent(modelFile(model3D)))
                .perspective(ItemDisplayContext.GUI, modelBuilder)
                .perspective(ItemDisplayContext.GROUND, modelBuilder)
                .perspective(ItemDisplayContext.FIXED, modelBuilder);
    }

    private void compassModels(DeferredItem<?> item, @Nullable String textureFolder) {
        ResourceLocation id = item.getId();
        List<ResourceLocation> models = Lists.newArrayList();
        String texture = "item/";
        if (textureFolder != null) texture += textureFolder + "/";
        ResourceLocation baseTexture = id.withPrefix(texture);

        for (int i = 0; i < 32; i++) {
            if (i == 16) continue;
            ResourceLocation modelId = baseTexture.withSuffix(String.format(Locale.ROOT, "/%02d", i));
            models.add(modelId);

            getBuilder(id.withPrefix("item/").withSuffix(String.format(Locale.ROOT, "/%02d", i)).toString()).parent(generatedItem)
                    .texture("layer0", modelId);
        }

        ResourceLocation baseModel = id.withPrefix("item/");
        ItemModelBuilder builder = getBuilder(id.toString())
                .parent(generatedItem).texture("layer0", baseTexture.withSuffix("/16"));

        ResourceLocation angle = ResourceLocation.withDefaultNamespace("angle");
        for (float i = 0; i < 64; i++) {
            if (i == 0 || i == 63) {
                builder.override().predicate(angle, i / 64)
                        .model(modelFile(baseModel));
                continue;
            } else if ((i % 2) == 0) continue;
            float value = i / 64;
            int num = (int) (i * 0.5 + 16.5);
            num = num % 32;

            ResourceLocation model = baseModel.withSuffix(String.format(Locale.ROOT, "/%02d", num));
            builder.override().predicate(angle, value)
                    .model(modelFile(model));
        }
    }

    private void bloodLustAmulet() {
        ResourceLocation baseId = FTZItems.BLOODLUST_AMULET.getId();
        String folder = "item/spellcaster/";

        ResourceLocation baseFurious = baseId.withSuffix("_furious");
        getBuilder(baseFurious.toString())
                .parent(generatedItem)
                .texture("layer0", baseFurious.withPrefix(folder));

        getBuilder(baseId.toString())
                .parent(generatedItem)
                .texture("layer0", baseId.withPrefix(folder))
                .override()
                .predicate(Fantazia.location("furious"), 1.0F)
                .model(modelFile(baseFurious.withPrefix("item/")));
    }


    private void puppetDoll() {
        ResourceLocation baseId = FTZItems.PUPPET_DOLL.getId();
        String folder = "item/spellcaster/";

        ResourceLocation baseFurious = baseId.withSuffix("_puppeteered");
        getBuilder(baseFurious.toString())
                .parent(generatedItem)
                .texture("layer0", baseFurious.withPrefix(folder));

        getBuilder(baseId.toString())
                .parent(generatedItem)
                .texture("layer0", baseId.withPrefix(folder))
                .override()
                .predicate(Fantazia.location("has_puppet"), 1.0F)
                .model(modelFile(baseFurious.withPrefix("item/")));
    }

    private void cardDeck() {
        ResourceLocation baseId = FTZItems.CARD_DECK.getId();
        String folder = "item/spellcaster/";

        for (int i = 1; i <= 4; i++) {
            ResourceLocation model = baseId.withSuffix("/outcome" + i);
            getBuilder(model.withPrefix("item/").toString()).parent(generatedItem)
                    .texture("layer0", model.withPrefix(folder));
        }

        ItemModelBuilder builder = getBuilder(baseId.toString())
                .parent(generatedItem)
                .texture("layer0", baseId.withPrefix(folder).withSuffix("/default"));

        for (int i = 1; i <= 4; i++) {
            builder.override().predicate(Fantazia.location("excluded_outcome"), (float) i / 4)
                    .model(modelFile(baseId.withPrefix("item/").withSuffix("/outcome" + i)));
        }
    }

    private void hatchet(DeferredItem<? extends HatchetItem> item) {
        ResourceLocation id = item.getId();
        getBuilder(id.toString())
                .parent(generatedItem)
                .texture("layer0", id.withPrefix("item/weapon/"))
                .transforms()
                // ground
                .transform(ItemDisplayContext.GROUND)
                .scale(0.7f)
                .end()
                // third person right hand
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(0,90,45)
                .translation(0F,1F,0.5F)
                .scale(0.7F,0.7F,0.7F)
                .end()
                // third person left hand
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                .rotation(0,-90,-45)
                .translation(0F,1F,0.5F)
                .scale(0.7F,0.7F,0.7F)
                .end()
                // first person right hand
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0,90,30)
                .translation(0F,3.5F,0.75F)
                .scale(0.7F,0.7F,0.7F)
                .end()
                // first person left hand
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                .rotation(0,-90,-30)
                .translation(0F,3.5F,0.75F)
                .scale(0.7F,0.7F,0.7F)
                .end()
                .end();
    }

    private void leadersHorn() {
        ResourceLocation id = FTZItems.LEADERS_HORN.getId();
        ResourceLocation tooting = id.withPrefix("item/tooting_");
        ResourceLocation texture = id.withPrefix("item/auracaster/");

        getBuilder(tooting.toString()).parent(generatedItem)
                .texture("layer0", texture)
                .transforms()
                // third person right hand
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(0,-125,0)
                .translation(-1F,2F,2F)
                .scale(0.5F,0.5F,0.5F)
                .end()
                // third person left hand
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                .rotation(0,55,0)
                .translation(-1F,2F,2F)
                .scale(0.5F,0.5F,0.5F)
                .end()
                // first person right hand
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0,-55,-5)
                .translation(-1F,-2.5F,-7.5F)
                .end()
                // first person left hand
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                .rotation(0,115,5)
                .translation(-1F,-2.5F,-7.5F)
                .end()
                .end();

        getBuilder(id.toString()).parent(generatedItem)
                .texture("layer0", texture)
                .transforms()
                // third person right hand
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(0,180,0)
                .translation(0F,3F,1F)
                .scale(0.55F,0.55F,0.55F)
                .end()
                // third person left hand
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                .rotation(0,0,0)
                .translation(0F,3F,1F)
                .scale(0.55F,0.55F,0.55F)
                .end()
                // first person right hand
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0,-90,25)
                .translation(1.13F, 3.2F, 1.13F)
                .scale(0.68F,0.68F,0.68F)
                .end()
                // first person left hand
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                .rotation(0,90,-25)
                .translation(1.13F, 3.2F, 1.13F)
                .scale(0.68F,0.68F,0.68F)
                .end()
                .end()
                // override
                .override()
                .predicate(ResourceLocation.withDefaultNamespace("tooting"), 1F)
                .model(modelFile(tooting));
    }

    private void arachnidEye() {
        ResourceLocation base = FTZItems.ARACHNID_EYE.getId();
        String textureFolder = "item/expendable/";

        ResourceLocation sparkles = base.withSuffix("/sparkles");
        getBuilder(sparkles.withPrefix("item/").toString()).parent(generatedItem)
                .texture("layer0", sparkles.withPrefix(textureFolder));

        ResourceLocation no_sparkles = base.withSuffix("/no_sparkles");
        getBuilder(no_sparkles.withPrefix("item/").toString()).parent(generatedItem).texture("layer0", no_sparkles.withPrefix(textureFolder));

        getBuilder(base.toString()).parent(generatedItem)
                .texture("layer0", sparkles.withPrefix(textureFolder))
                .customLoader(SeparateTransformsModelBuilder::begin)
                .base(factory.apply(no_sparkles).parent(modelFile(no_sparkles.withPrefix("item/"))))
                .perspective(ItemDisplayContext.GUI, factory.apply(sparkles).parent(modelFile(sparkles.withPrefix("item/"))))
                .perspective(ItemDisplayContext.FIXED, factory.apply(sparkles).parent(modelFile(sparkles.withPrefix("item/"))))
                .end();
    }

    private void fragileBlade() {
        customRenderer(FTZItems.FRAGILE_BLADE);
        ResourceLocation baseId = FTZItems.FRAGILE_BLADE.getId();

        ResourceLocation modelBase = baseId.withPrefix("item/");
        for (int i = 0; i < 5; i++) {
            getBuilder(modelBase.withSuffix("/in_gui_level" + i).toString()).parent(generatedItem)
                    .texture("layer0", baseId.withPrefix("item/weapon/").withSuffix("/level" + i));
        }

        getBuilder(modelBase.withSuffix("/in_hand").toString())
                .parent(modelFile(Fantazia.location("item/template_fragile_blade")))
                .texture("base", baseId.withPrefix("item/models/"));
    }

    private void dashstones() {
        customRenderer(FTZItems.DASHSTONE);
        ResourceLocation baseId = FTZItems.DASHSTONE.getId().withPrefix("item/").withSuffix("/level");
        for (int i = 1; i < 4; i++) {
            ResourceLocation model = baseId.withSuffix(String.valueOf(i));
            getBuilder(model.toString()).parent(generatedItem)
                    .texture("layer0", model);
        }
    }

    private void wisdomCatcher() {
        customRenderer(FTZItems.WISDOM_CATCHER);
        ResourceLocation baseId = FTZItems.WISDOM_CATCHER.getId();
        ResourceLocation modelBase = baseId.withPrefix("item/");

        // in gui
        for (WisdomTransferComponent component : WisdomTransferComponent.values()) {
            String name = component.getSerializedName();
            // in gui
            getBuilder(modelBase.withSuffix("/in_gui_" + name).toString()).parent(generatedItem)
                    .texture("layer0", modelBase.withSuffix("/" + name));
            // in hand stationary
            getBuilder(modelBase.withSuffix("/in_hand_stationary_" + name).toString())
                    .parent(modelFile(Fantazia.location("item/template_wisdom_catcher")))
                    .texture("base", baseId.withPrefix("item/models/").withSuffix("/" + name));
            // in hand used
            getBuilder(modelBase.withSuffix("/in_hand_used_" + name).toString())
                    .parent(modelFile(Fantazia.location("item/template_wisdom_catcher_used")))
                    .texture("base", baseId.withPrefix("item/models/").withSuffix("/" + name));
        }
    }

    private ModelFile.ExistingModelFile modelFile(ResourceLocation model) {
        return new ModelFile.ExistingModelFile(model, existingFileHelper);
    }
}
