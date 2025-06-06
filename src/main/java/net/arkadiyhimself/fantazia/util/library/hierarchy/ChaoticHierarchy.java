package net.arkadiyhimself.fantazia.util.library.hierarchy;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.Fantazia;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * {@link ChaoticHierarchy} is a more casual version of the {@link ChainHierarchy}, where elements are not supposed to be structured
 * <br>
 * Created for the sole purpose of being a «placeholder» for an unorganized list in case an object of hierarchy's subclass is required
 * <br>
 * Considering the fact that this hierarchy is supposed to be unordered and not used for more serious tasks, it has been made more forgiving in terms of causing errors
 */
public class ChaoticHierarchy<T> extends ChainHierarchy<T> {

    public static <T> Codec<ChaoticHierarchy<T>> chaoticHierarchyCodec(Codec<T> tCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                tCodec.listOf().fieldOf("elements").forGetter(ChaoticHierarchy::getElements)
        ).apply(instance, ChaoticHierarchy::of));
    }

    @SuppressWarnings("ConstantConditions")
    public ChaoticHierarchy() {
        super(null);
    }

    public static <M> ChaoticHierarchy<M> of(List<M> elements) throws HierarchyException {
        if (elements.isEmpty()) return new ChaoticHierarchy<>();
        ChaoticHierarchy<M> hierarchy = new ChaoticHierarchy<>();
        if (elements.isEmpty()) return hierarchy;
        for (M element : elements) hierarchy.addElement(element);
        return hierarchy;
    }

    @SafeVarargs
    public static <M> ChaoticHierarchy<M> of(M... elements) {
        return of(Arrays.stream(elements).toList());
    }
    public static <M> ChaoticHierarchy<M> of(ChainHierarchy<M> elements) {
        return of(elements.getElements());
    }

    /**
     * Since this hierarchy is not supposed to have an order, duplicating and null values provided by transformer are ignored and do not cause an error to occur
     */
    @Override
    public <M> ChaoticHierarchy<M> transform(@NotNull Function<T, M> transformer) {
        List<M> newList = Lists.newArrayList();
        for (T element : getElements()) {
            M newElement = transformer.apply(element);
            if (newElement == null) {
                Fantazia.LOGGER.info("Chaotic Hierarchy transforming: one of elements was skipped due to being null");
                continue;
            }
            if (newList.contains(newElement)) {
                Fantazia.LOGGER.info("Chaotic Hierarchy transforming: one of elements was skipped due to duplication");
                continue;
            }
            newList.add(newElement);
        }
        return of(newList);
    }

    @Override
    public HierarchyType getType() {
        return HierarchyType.CHAOTIC;
    }

    /**
     * Since this hierarchy is not supposed to have an order, both methods related to structuration are redundant
     */
    @Deprecated
    @Nullable
    @Override
    public T getParent(T element) {
        return null;
    }
    @Deprecated
    @Nullable
    @Override
    public T getChild(T element) {
        return null;
    }
}
