package net.arkadiyhimself.fantazia.data.item.rechargeable_tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;

import java.util.function.IntFunction;

public interface ToolDamageLevelFunction extends IntFunction<Float> {

    Codec<ToolDamageLevelFunction> DISPATCH_CODEC = FantazicRegistries.TOOL_DAMAGE_LEVEL_FUNCTIONS.byNameCodec()
            .dispatch(ToolDamageLevelFunction::codec, codec -> codec);

    @Override
    Float apply(int value);

    MapCodec<? extends ToolDamageLevelFunction> codec();

    static Linear baseAndPerLevel(float baseValue, float perLevel) {
        return new Linear(baseValue, perLevel);
    }

    static Constant constant(float value) {
        return new Constant(value);
    }

    record Linear(float baseValue, float perLevel) implements ToolDamageLevelFunction {

        public static final MapCodec<Linear> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("base_value").forGetter(Linear::baseValue),
                Codec.FLOAT.fieldOf("per_level").forGetter(Linear::perLevel)
        ).apply(instance, Linear::new));


        @Override
        public Float apply(int level) {
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

    record Constant(float value) implements ToolDamageLevelFunction {

        public static final MapCodec<Constant> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("value").forGetter(Constant::value)
        ).apply(instance, Constant::new));

        @Override
        public Float apply(int level) {
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
