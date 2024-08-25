package net.arkadiyhimself.fantazia.util.library.hierarchy;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * {@link MonoHierarchy} is a unicellular Hierarchy consisting of one single element. This element does not have any «parents» or «children»
 */
public class MonoHierarchy<T> implements IHierarchy<T> {
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
}
