package net.arkadiyhimself.fantazia.world.gen.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class StructureHelper {
    private StructureHelper() {}
    public static Optional<Integer> getSuitableNetherYLevel(Structure.GenerationContext context, BlockPos pos) {
        NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());
        List<Integer> suitableYLevels = new ArrayList<>();

        for (int y = 127; y > context.chunkGenerator().getSeaLevel(); y--) {
            if (column.getBlock(y - 1).canOcclude() && column.getBlock(y).isAir() && column.getBlock(y + 4).isAir()) {
                suitableYLevels.add(y);
            }
        }

        if (suitableYLevels.isEmpty())
            return Optional.empty();

        return Optional.of(suitableYLevels.get(new Random(context.seed()).nextInt(suitableYLevels.size())));
    }
    public static boolean checkLandAtHeight(Structure.GenerationContext context, BlockPos pos, int heightTolerance) {
        NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());

        for (int y = pos.getY() - heightTolerance; y <= pos.getZ() + heightTolerance; y++) {
            if (column.getBlock(y).canOcclude() && column.getBlock(y + 1).isAir())
                return true;
        }

        return false;
    }
}
