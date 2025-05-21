package net.arkadiyhimself.fantazia.util.library.hierarchy;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * {@link MonoHierarchy} is a unicellular Hierarchy consisting of one single element. This element does not have any «parents» or «children»
 */
public class MonoHierarchy<T> implements IHierarchy<T> {

    public static <T> Codec<MonoHierarchy<T>> monoHierarchyCodec(Codec<T> tCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                tCodec.fieldOf("main").forGetter(MonoHierarchy::getMainElement)
        ).apply(instance, MonoHierarchy::new));
    }

    private final @NotNull T MAIN;
    public MonoHierarchy(@NotNull T MAIN) {
        this.MAIN = MAIN;
    }

    public static <M> MonoHierarchy<M> of(M main) {
        return new MonoHierarchy<>(main);
    }

    @Override
    public final @NotNull T getMainElement() {
        return MAIN;
    }

    @Override
    public @Nullable T getParent(T t) {
        return null;
    }

    @Override
    public @Nullable T getChild(T t) {
        return null;
    }

    @Override
    public boolean contains(T t) {
        return t.equals(MAIN);
    }

    @Override
    public <M> MonoHierarchy<M> transform(@NotNull Function<T, M> transformer) throws HierarchyException {
        if (transformer.apply(MAIN) == null) throw new HierarchyException("Hierarchy's element can not be null");
        return new MonoHierarchy<>(transformer.apply(MAIN));
    }

    @Override
    public ImmutableList<T> getElements() {
        return ImmutableList.of(MAIN);
    }

    @Override
    public HierarchyType getType() {
        return HierarchyType.MONO;
    }
}
