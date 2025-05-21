package net.arkadiyhimself.fantazia.datagen.worldgen.tree;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.datagen.worldgen.FantazicConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class FTZTreeGrowers {

    public static final TreeGrower OBSCURE = new TreeGrower(Fantazia.MODID + ":obscure",0.225F,
            Optional.empty(),
            Optional.empty(),
            Optional.of(FantazicConfiguredFeatures.OBSCURE_TREE1),
            Optional.of(FantazicConfiguredFeatures.OBSCURE_TREE2),
            Optional.empty(),
            Optional.empty());

}
