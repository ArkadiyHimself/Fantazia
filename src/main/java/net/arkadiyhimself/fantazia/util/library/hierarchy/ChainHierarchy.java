package net.arkadiyhimself.fantazia.util.library.hierarchy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * {@link ChainHierarchy} is a multicellular hierarchy consisting of one or more elements.
 * <br>
 * Each element can only have up to one parent and up to one child, like chains going one after another
 */
public class ChainHierarchy<T> extends MonoHierarchy<T> {

    public static <T> Codec<ChainHierarchy<T>> chainHierarchyCodec(Codec<T> tCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                tCodec.listOf().fieldOf("elements").forGetter(ChainHierarchy::getElements)
        ).apply(instance, ChainHierarchy::of));
    }

    /**
     * An ordered collection of elements of hierarchy, where the first element is the main one
     * <br>
     * Each element is a «parent» of the next one and a «child» of the previous one
     */
    private final List<T> ELEMENTS = Lists.newArrayList();

    public ChainHierarchy(@NotNull T MAIN) {
        super(MAIN);
        if (this instanceof ChaoticHierarchy<T>) return;
        ELEMENTS.add(MAIN);
    }

    public static <M> ChainHierarchy<M> of(List<M> elements) throws HierarchyException {
        if (elements.isEmpty()) throw new HierarchyException("Hierarchy can not be empty!");
        ChainHierarchy<M> hierarchy = new ChainHierarchy<>(elements.getFirst());
        if (elements.size() <= 1) return hierarchy;
        for (int i = 1; i < elements.size(); i++) hierarchy.addElement(elements.get(i));
        return hierarchy;
    }

    public static <M> ChainHierarchy<M> of(M[] elements) {
        return of(Arrays.stream(elements).toList());
    }
    @Override
    public @Nullable T getParent(T element) throws HierarchyException {
        if (!contains(element)) throw new HierarchyException("The hierarchy does not contain this element: " + element);
        int i = ELEMENTS.indexOf(element);
        return i <= 0 ? null : ELEMENTS.get(i - 1);
    }

    @Override
    public @Nullable T getChild(T element) {
        if (!contains(element)) throw new HierarchyException("The hierarchy does not contain this element: " + element);
        int i = ELEMENTS.indexOf(element);
        return i >= getSize() - 1 ? null : ELEMENTS.get(i + 1);
    }

    @Override
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean contains(T t) {
        return ELEMENTS.contains(t);
    }

    @Override
    public <M> ChainHierarchy<M> transform(@NotNull Function<T, M> transformer) throws HierarchyException {
        List<M> newList = Lists.newArrayList();
        for (T element : ELEMENTS) {
            M newElement = transformer.apply(element);
            if (newElement == null) throw new HierarchyException("Hierarchy's element can not be null");
            if (newList.contains(newElement)) throw new HierarchyException("The hierarchy already contains this element: " + newElement);
            newList.add(newElement);
        }
        return of(newList);
    }

    @Override
    public ImmutableList<T> getElements() {
        return ImmutableList.copyOf(ELEMENTS);
    }

    @Override
    public HierarchyType getType() {
        return HierarchyType.CHAIN;
    }

    public List<T> toList() {
        return List.copyOf(ELEMENTS);
    }

    public int getSize() {
        return ELEMENTS.size();
    }

    public void addElement(T t) {
        ELEMENTS.add(t);
    }

    public static <T> Builder<T> builder(T main) {
        return new Builder<>(main);
    }

    public static class Builder<T> {

        private final List<T> tList = Lists.newArrayList();

        private Builder(T main) {
            tList.add(main);
        }

        public Builder<T> addElement(T element) {
            tList.add(element);
            return this;
        }

        public ChainHierarchy<T> build() {
            return ChainHierarchy.of(tList);
        }
    }
}
