package net.arkadiyhimself.fantazia.util.library.hierarchy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * {@link ComplexHierarchy}, as the name suggests, is by far the most complicated type of hierarchy, where each element can have several «children»
 * <br>
 * Each element of the hierarchy has its own «rank» which depends on how deep is the element in the hierarchy:
 * <br>
 * For example, the main element's rank is always 0, and the children of main element will have their rank being 1, while the children of those children will have their rank being 2, and so on
 */
public class ComplexHierarchy<T> extends MonoHierarchy<T> {

    public static <T> Codec<ComplexHierarchy<T>> complexHierarchyCodec(Codec<T> tCodec) {
        return null;
    }

    /**
     * An unorganized collection which contains all elements of the hierarchy with no particular order
     */
    private final Collection<T> ALL = Lists.newArrayList();
    /**
     * A hashmap containing elements of hierarchy as keys and lists of children of respective elements as values
     */
    private final HashMap<T, Collection<T>> ParentToChildren = Maps.newHashMap();
    /**
     * A hashmap containing elements of hierarchy as keys and their respective parents as values
     */
    private final HashMap<T, T> ChildToParent = Maps.newHashMap();
    /**
     * An organized list of lists of elements of hierarchy
     * <br>
     * The reason why {@link List} has been chosen instead of a {@link HashMap} with {@link Integer} and {@link List} as respective types is the way the list is naturally organized, having each index go from 0 to list's size minus 1
     * <br>
     * The index of a sub-list contained by this list is a «rank» of all elements the sub-list contains
     */
    private final List<List<T>> Ranks = Lists.newArrayList();

    public ComplexHierarchy(@NotNull T MAIN) {
        super(MAIN);
        ALL.add(MAIN);
        getOrCreateRanks(0).add(MAIN);
        getOrCreateChildren(MAIN);
    }

    @Override
    public @Nullable T getParent(T t) {
        return ChildToParent.get(t);
    }

    @Override
    public boolean contains(T t) {
        return ALL.contains(t);
    }

    @Override
    public <M> ComplexHierarchy<M> transform(@NotNull Function<T, M> transformer) throws HierarchyException {
        M main = transformer.apply(getMainElement());
        if (main == null) throw new HierarchyException("Hierarchy's element can not be null");

        HashMap<T, M> newElements = Maps.newHashMap();
        for (T t : ALL) {
            M m = transformer.apply(t);
            if (m == null) throw new HierarchyException("Hierarchy's element can not be null");
            else if (newElements.containsValue(m))
                throw new HierarchyException("The hierarchy does not contain this element");
            newElements.put(t, m);
        }

        ComplexHierarchy<M> complexHierarchy = new ComplexHierarchy<>(main);
        if (Ranks.size() <= 1) return complexHierarchy;

        for (int i = 1; i < Ranks.size(); i++) {
            for (T ij : Ranks.get(i)) {
                M IJ = newElements.get(ij);
                M parent = newElements.get(getParent(ij));
                complexHierarchy.addElement(parent, IJ);
            }
        }

        return complexHierarchy;
    }

    @Override
    public HierarchyType getType() {
        return HierarchyType.COMPLEX;
    }

    /**
     * Returns the children of respective element from {@link ComplexHierarchy#ParentToChildren}
     * <br>
     * If the hashmap does not contain the element as key, a new entry consisting of the element and an empty list is put and the same empty list is returned
     * @param element element of hierarchy
     * @return the list of element's «children»
     */
    private Collection<T> getOrCreateChildren(@NotNull T element) {
        if (!contains(element)) throw new HierarchyException("The hierarchy does not contain this element");
        if (!ParentToChildren.containsKey(element)) ParentToChildren.put(element, Lists.newArrayList());
        return ParentToChildren.get(element);
    }

    /**
     * Returns the list of sub-lists containing the elements of hierarchy, where index of each sub-list equals the «rank» of all elements within the sublist
     * <br>
     * If the list does not have the «sublist» at specified index, a new empty sub-list is put inside the main list and then returned
     * @param rank the index of sublist
     * @return the sublist of respective rank
     */
    private List<T> getOrCreateRanks(int rank) {
        int i = Math.min(rank, Ranks.size());
        if (Ranks.size() - 1 < i) Ranks.add(Lists.newArrayList());
        return Ranks.get(i);
    }

    public void addElement(T parent, T element) {
        if (!contains(parent)) throw new HierarchyException("The hierarchy does not contain this element");
        if (contains(element)) throw new HierarchyException("The hierarchy already contains this element");
        ALL.add(element);
        ChildToParent.put(element, parent);
        getOrCreateRanks(getRank(parent) + 1).add(element);
        getOrCreateChildren(parent).add(element);
    }

    public int getRanks() {
        return Ranks.size();
    }

    public int getRank(T element) {
        if (!contains(element)) throw new HierarchyException("The hierarchy does not contain this element");
        for (int i = 0; i < Ranks.size(); i++) if (Ranks.get(i).contains(element)) return i;
        throw new HierarchyException("The element is not ranked...");
    }

    public ImmutableList<T> getRankedList(int rank) {
        return ImmutableList.copyOf(Ranks.get(rank));
    }

    public int size() {
        return ALL.size();
    }

    /**
     * The {@link IHierarchy#getChild(Object)} method has been replaced with this one, since in this hierarchy each element may have several children
     * @param element the element of hierarchy
     * @return the list of children of respective element
     */
    public ImmutableList<T> getChildren(T element) {
        return ImmutableList.copyOf(getOrCreateChildren(element));
    }

    /**
     * This method is redundant, since each element can have several children
     */
    @Deprecated
    @Override
    public @Nullable T getChild(T t) {
        return null;
    }
}
