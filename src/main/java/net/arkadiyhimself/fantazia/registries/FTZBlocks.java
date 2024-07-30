package net.arkadiyhimself.fantazia.registries;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.blocks.AncientFlameBlock;
import net.arkadiyhimself.fantazia.blocks.RegularBlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class FTZBlocks extends FTZRegistry<Block> {
    private static final Map<ResourceLocation, BlockItemSupplier> BLOCK_ITEMS = Maps.newHashMap();
    private static final FTZBlocks INSTANCE = new FTZBlocks();
    private final DeferredRegister<Item> BLOCK_ITEM = DeferredRegister.create(ForgeRegistries.ITEMS, Fantazia.MODID);
    private void registerBlock(final String name, final Supplier<Block> blockSupplier, final BlockItemSupplier sup) {
        this.register(name, blockSupplier);
        registerItemBlock(name, sup);
    }
    private <T extends Block> void registerItemBlock(String name, BlockItemSupplier supplier) {
        BLOCK_ITEMS.put(Fantazia.res(name), supplier);
    }

    @ObjectHolder(value = Fantazia.MODID + ":ancient_flame", registryName = "block")
    public static final AncientFlameBlock ANCIENT_FLAME = null;

    public FTZBlocks() {
        super(ForgeRegistries.BLOCKS);

        this.registerBlock("ancient_flame", AncientFlameBlock::new, RegularBlockItem::new);
    }
    protected static Map<ResourceLocation, BlockItemSupplier> getBlockItems() {
        return Collections.unmodifiableMap(BLOCK_ITEMS);
    }
    @FunctionalInterface
    protected interface BlockItemSupplier extends Function<Block, BlockItem> {
        @Override
        BlockItem apply(Block block);
    }

}
