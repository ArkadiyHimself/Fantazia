package net.arkadiyhimself.fantazia.datagen.tag_providers;

import com.google.common.collect.Lists;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.PassiveSpell;
import net.arkadiyhimself.fantazia.items.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.tags.FTZBlockTags;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FantazicItemTagsProvider extends ItemTagsProvider {

    private static final TagKey<Item> CURIOS_DASHSTONE = TagKey.create(Registries.ITEM, ResourceLocation.parse("curios:dashstone"));
    private static final TagKey<Item> CURIOS_SPELL_CASTER = TagKey.create(Registries.ITEM, ResourceLocation.parse("curios:spellcaster"));
    private static final TagKey<Item> CURIOS_PASSIVE_CASTER = TagKey.create(Registries.ITEM, ResourceLocation.parse("curios:passivecaster"));

    public FantazicItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        // fantazia
        copyFromBlocks();

        List<Item> dashstones = Lists.newArrayList();
        List<Item> spellcasters = Lists.newArrayList();
        List<Item> passivecasters = Lists.newArrayList();
        List<Item> hatchets = Lists.newArrayList();

        for (DeferredItem<?> deferredItem : FTZItems.DASHSTONES) dashstones.add(deferredItem.asItem());
        for (DeferredItem<SpellCasterItem> deferredItem : FTZItems.SPELL_CASTERS) {
            SpellCasterItem spellCaster = deferredItem.get();
            AbstractSpell spell = spellCaster.getSpell().value();
            if (spell instanceof PassiveSpell) passivecasters.add(spellCaster);
            else spellcasters.add(spellCaster);
        }
        for (DeferredItem<AuraCasterItem> deferredItem : FTZItems.AURA_CASTERS) passivecasters.add(deferredItem.asItem());
        for (DeferredItem<HatchetItem> deferredItem : FTZItems.HATCHETS) hatchets.add(deferredItem.asItem());

        IntrinsicTagAppender<Item> dashStoneTag = tag(CURIOS_DASHSTONE).replace();
        for (Item item : dashstones) dashStoneTag.add(item);
        IntrinsicTagAppender<Item> spellCasterTag = tag(CURIOS_SPELL_CASTER).replace();
        for (Item item : spellcasters) spellCasterTag.add(item);
        IntrinsicTagAppender<Item> passiveCasterTag = tag(CURIOS_PASSIVE_CASTER).replace();
        for (Item item : passivecasters) passiveCasterTag.add(item);
        IntrinsicTagAppender<Item> hatchetTag = tag(FTZItemTags.HATCHETS).replace();
        for (Item item : hatchets) hatchetTag.add(item);

        tag(FTZItemTags.HATCHET_ENCHANTABLE).addTag(FTZItemTags.HATCHETS);
        tag(FTZItemTags.MELEE_BLOCK).add(FTZItems.FRAGILE_BLADE.value(), FTZItems.MURASAMA.value()).addTag(ItemTags.SWORDS);
        tag(FTZItemTags.NO_DISINTEGRATION).add(Items.NETHER_STAR);

        // minecraft
        tag(ItemTags.BOATS).add(FTZItems.OBSCURE_BOAT.value());
        tag(ItemTags.CHEST_BOATS).add(FTZItems.OBSCURE_CHEST_BOAT.value());
        tag(ItemTags.BEACON_PAYMENT_ITEMS).add(FTZItems.FANTAZIUM_INGOT.value());
    }

    private void copyFromBlocks() {
        copy(FTZBlockTags.OBSCURE_LOGS, FTZItemTags.OBSCURE_LOGS);
        copy(FTZBlockTags.FANTAZIUM_ORES, FTZItemTags.FANTAZIUM_ORES);
    }
}
