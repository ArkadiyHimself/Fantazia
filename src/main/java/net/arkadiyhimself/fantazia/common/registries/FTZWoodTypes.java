package net.arkadiyhimself.fantazia.common.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public interface FTZWoodTypes {
    WoodType OBSCURE = WoodType.register(new WoodType(Fantazia.MODID + ":obscure", BlockSetType.OAK));
}
