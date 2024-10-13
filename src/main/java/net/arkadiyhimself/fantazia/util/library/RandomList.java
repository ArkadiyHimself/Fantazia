package net.arkadiyhimself.fantazia.util.library;

import net.arkadiyhimself.fantazia.Fantazia;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RandomList<T> extends ArrayList<T> {

    public static <M> RandomList<M> emptyRandomList() {
        return new RandomList<>();
    }

    private RandomList() {}

    public RandomList(@NotNull Collection<? extends T> c) {
        super(c);
    }

    @Nullable
    public T random() {
        return this.isEmpty() ? null : this.get(Fantazia.RANDOM.nextInt(0, this.size()));
    }

    public static <M> RandomList<M> of(List<M> elements) {
        RandomList<M> list = new RandomList<>();
        list.addAll(elements);
        return list;
    }
}
