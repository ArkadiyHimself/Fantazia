package net.arkadiyhimself.fantazia.util.library.hierarchy;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Hierarchy is a system of elements of the same type which are organized in a specific way by ranks, importance, order, etc.
 * <br>
 * Each element usually has a «parent» or a «child», or both.
 * <br>
 * Every hierarchy has a «main» element, after which all other elements come.
 * @param <T> type of elements in hierarchy.
 */
public interface IHierarchy<T> {

    /**
     * @return the «main» element of hierarchy which is always present, hence why {@link NotNull} is applied
     */
    @NotNull T getMainElement();

    /**
     * @param t the element whose parent is returned
     * @return the parent of an element, if it has one, otherwise null
     * @throws HierarchyException if the element is not present in the hierarchy
     */

    @Nullable T getParent(T t) throws HierarchyException;
    /**
     * @param t the element whose child is returned
     * @return the child of an element, if it has one, otherwise null
     * @throws HierarchyException if the element is not present in the hierarchy
     */
    @Nullable T getChild(T t) throws HierarchyException;

    /**
     * Checks whether an element is present within hierarchy
     * @param t the element that is being checked on
     * @return true if the hierarchy contains the element, otherwise false
     */
    boolean contains(T t);

    /**
     * @return the list of all elements
     */
    ImmutableList<T> getElements();

    /**
     * «Transforms» the hierarchy to a new one, replacing its own elements with elements of new type with respect to transformer
     * <br>
     * In most subclasses the returned hierarchy will have the same amount of elements as original, as well as the same structure
     * @param transformer the function which defies correspondence between elements of hierarchy and new hierarchy.
     * @return the new Hierarchy with elements of other type
     * @param <M> the type of elements of the new hierarchy
     * @throws HierarchyException in most subclasses if transformer provides duplicating values or a null value at some point
     */
    <M> IHierarchy<M> transform(@NotNull Function<T, M> transformer) throws HierarchyException;

}
