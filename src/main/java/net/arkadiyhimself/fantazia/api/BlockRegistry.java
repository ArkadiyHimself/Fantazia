package net.arkadiyhimself.fantazia.api;

import net.arkadiyhimself.fantazia.Blocks.AncientFlameBlock;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockRegistry {
    public static BlockBehaviour.Properties getProperties(Block block) {
        return BlockBehaviour.Properties.copy(block);
    }
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Fantazia.MODID);
    private static <T extends Block> RegistryObject<T> registerBlock(final String name, final Supplier<T> sup) {
        RegistryObject<T> value = BLOCKS.register(name, sup);
        registerItemBlock(name, value);
        return value;
    }
    private static<T extends Block> RegistryObject<Item> registerItemBlock(String name, RegistryObject<T> object) {
        return ItemRegistry.ITEMS.register(name, () -> new BlockItem(object.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) { BLOCKS.register(eventBus); }
    public static final RegistryObject<Block> ANCIENT_FLAME; // finished
    static {
        ANCIENT_FLAME = registerBlock("ancient_flame", AncientFlameBlock::new);
    }
}
