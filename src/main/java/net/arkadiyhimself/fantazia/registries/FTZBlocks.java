package net.arkadiyhimself.fantazia.registries;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.blocks.AncientFlameBlock;
import net.arkadiyhimself.fantazia.blocks.RegularBlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class FTZBlocks {
    private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Fantazia.MODID);
    private static final Map<ResourceLocation, BlockItemSupplier> BLOCK_ITEMS = Maps.newHashMap();
    private static RegistryObject<Block> registerBlock(final String name, final Supplier<Block> blockSupplier, final BlockItemSupplier sup) {
        registerItemBlock(name, sup);
        return REGISTER.register(name, blockSupplier);
    }
    private static void registerItemBlock(String name, BlockItemSupplier supplier) {
        BLOCK_ITEMS.put(Fantazia.res(name), supplier);
    }
    public static final RegistryObject<Block> ANCIENT_FLAME = registerBlock("ancient_flame", AncientFlameBlock::new, RegularBlockItem::new);

    protected static Map<ResourceLocation, BlockItemSupplier> getBlockItems() {
        return Collections.unmodifiableMap(BLOCK_ITEMS);
    }
    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    @FunctionalInterface
    protected interface BlockItemSupplier extends Function<Block, BlockItem> {
        @Override
        BlockItem apply(Block block);
    }
}
