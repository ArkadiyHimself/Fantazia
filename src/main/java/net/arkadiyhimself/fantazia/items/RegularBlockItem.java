package net.arkadiyhimself.fantazia.items;

import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;

public class RegularBlockItem extends BlockItem {
    public RegularBlockItem(Block pBlock) {
        super(pBlock, RegularBlockItem.getDefaultProperties());
    }

    public static Properties getDefaultProperties() {
        Properties props = new Properties();

        props.stacksTo(64);
        props.rarity(Rarity.COMMON);

        return props;
    }
}
