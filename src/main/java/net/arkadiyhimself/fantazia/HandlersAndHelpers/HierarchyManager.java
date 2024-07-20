package net.arkadiyhimself.fantazia.HandlersAndHelpers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.Exceptions.HierarchyException;
import org.checkerframework.checker.units.qual.A;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class HierarchyManager<T> {
    private final List<T> ALL_ITEMS = Lists.newArrayList();
    private final Map<T, Integer> LEVELS = Maps.newHashMap();
    private final Map<T, T> PARENTS = Maps.newHashMap();
    private final Map<T, List<T>> CHILDREN = Maps.newHashMap();
    private final List<T> MAIN = Lists.newArrayList();
    private int maxLevel = 0;
    public List<T> getAllItems() {
        return ImmutableList.copyOf(ALL_ITEMS);
    }
    public void putMainElement(T element) {
        if (ALL_ITEMS.contains(element)) throw new HierarchyException("The hierarchy already contains the element");
        ALL_ITEMS.add(element);
        MAIN.add(element);
        CHILDREN.put(element, Lists.newArrayList());
        LEVELS.put(element, 0);
    }
    public List<T> getMain() {
        return MAIN;
    }
    public boolean hasElement(T element) {
        return ALL_ITEMS.contains(element);
    }
    public int getLevel(T element) {
        if (!ALL_ITEMS.contains(element) || !LEVELS.containsKey(element)) throw new HierarchyException("The hierarchy does not contain this element");
        return LEVELS.get(element);
    }
    public void putElement(T element, T parent) {
        if (!ALL_ITEMS.contains(parent)) throw new HierarchyException("The hierarchy does not contain this element");
        if (ALL_ITEMS.contains(element)) throw new HierarchyException("The hierarchy already contains the element");
        if (element == parent) throw new HierarchyException("An element can not be its own parent!");
        this.ALL_ITEMS.add(element);
        this.PARENTS.put(element, parent);
        int level = LEVELS.get(parent) + 1;
        if (level > maxLevel) maxLevel = level;
        this.LEVELS.put(element, level);
        this.CHILDREN.put(element, Lists.newArrayList());
        this.CHILDREN.get(parent).add(element);
    }
    public @Nullable T getParent(T element) {
        if (element == MAIN) return null;
        if (!ALL_ITEMS.contains(element) || !PARENTS.containsKey(element)) throw new HierarchyException("The hierarchy does not contain this element");
        return PARENTS.get(element);
    }
    public List<T> getChildren(T element) {
        if (!ALL_ITEMS.contains(element) || !CHILDREN.containsKey(element)) throw new HierarchyException("The hierarchy does not contain this element");
        return CHILDREN.get(element);
    }
}
