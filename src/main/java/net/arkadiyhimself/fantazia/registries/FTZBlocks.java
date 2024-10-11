package net.arkadiyhimself.fantazia.registries;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.blocks.AncientFlameBlock;
import net.arkadiyhimself.fantazia.blocks.RegularBlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class FTZBlocks {
    private FTZBlocks() {}
    private static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(Fantazia.MODID);
    private static final Map<ResourceLocation, BlockItemSupplier> BLOCK_ITEMS = Maps.newHashMap();
    private static <T extends Block> DeferredHolder<Block, T> registerBlock(final String name, final Supplier<T> blockSupplier, final BlockItemSupplier sup) {
        registerItemBlock(name, sup);
        return REGISTER.register(name, blockSupplier);
    }
    private static void registerItemBlock(String name, BlockItemSupplier supplier) {
        BLOCK_ITEMS.put(Fantazia.res(name), supplier);
    }
    public static final DeferredHolder<Block, AncientFlameBlock> ANCIENT_FLAME = registerBlock("ancient_flame", () -> new AncientFlameBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FIRE)), RegularBlockItem::new);

    protected static Map<ResourceLocation, BlockItemSupplier> getBlockItems() {
        return Collections.unmodifiableMap(BLOCK_ITEMS);
    }
    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
    @FunctionalInterface
    protected interface BlockItemSupplier extends Function<Block, BlockItem> {
        @Override
        BlockItem apply(Block block);
    }
}
