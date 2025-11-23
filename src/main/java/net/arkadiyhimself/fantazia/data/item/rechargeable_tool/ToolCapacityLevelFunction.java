package net.arkadiyhimself.fantazia.data.item.rechargeable_tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;

import java.util.function.IntFunction;

public interface ToolCapacityLevelFunction extends IntFunction<Integer> {
    
    Codec<ToolCapacityLevelFunction> DISPATCH_CODEC = FantazicRegistries.TOOL_CAPACITY_LEVEL_FUNCTIONS.byNameCodec()
            .dispatch(ToolCapacityLevelFunction::codec, codec -> codec);

    MapCodec<? extends ToolCapacityLevelFunction> codec();

    @Override
    Integer apply(int value);

    static Linear baseAndPerLevel(int baseValue, int perLevel) {
        return new Linear(baseValue, perLevel);
    }

    static Constant constant(int value) {
        return new Constant(value);
    }

    record Linear(int baseValue, int perLevel) implements ToolCapacityLevelFunction {

        public static final MapCodec<Linear> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("base_value").forGetter(Linear::baseValue),
                Codec.INT.fieldOf("per_level").forGetter(Linear::perLevel)
        ).apply(instance, Linear::new));


        @Override
        public Integer apply(int level) {
            return this.baseValue + this.perLevel * level;
        }

        @Override
        public MapCodec<Linear> codec() {
            return CODEC;
        }

        @Override
        public String toString() {
            return baseValue + " + (level * " + perLevel + ")";
        }
    }

    record Constant(int value) implements ToolCapacityLevelFunction {

        public static final MapCodec<Constant> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.fieldOf("value").forGetter(Constant::value)
        ).apply(instance, Constant::new));

        @Override
        public Integer apply(int level) {
            return this.value;
        }

        @Override
        public MapCodec<Constant> codec() {
            return CODEC;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

}
