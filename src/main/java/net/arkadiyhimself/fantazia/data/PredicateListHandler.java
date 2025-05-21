package net.arkadiyhimself.fantazia.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiPredicate;

public enum PredicateListHandler implements StringRepresentable {

    AND("and"),
    OR("or");

    public static final Codec<PredicateListHandler> CODEC = StringRepresentable.fromEnum(PredicateListHandler::values);

    private final String name;

    PredicateListHandler(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }

    public <T, M> boolean handle(List<T> predicates, M instance, BiPredicate<T, M> test) {
        if (this == OR) {
            for (T t : predicates) if (test.test(t, instance)) return true;
            return predicates.isEmpty();
        } else {
            for (T t : predicates) if (!test.test(t, instance)) return false;
            return true;
        }
    }
}
