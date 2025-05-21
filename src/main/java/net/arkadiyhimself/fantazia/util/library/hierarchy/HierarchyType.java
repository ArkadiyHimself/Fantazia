package net.arkadiyhimself.fantazia.util.library.hierarchy;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.data.talent.TalentDataException;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum HierarchyType implements StringRepresentable {

    MONO("mono"),
    CHAIN("chain"),
    CHAOTIC("chaotic"),
    COMPLEX("complex");

    public static final Codec<HierarchyType> CODEC = StringRepresentable.fromEnum(HierarchyType::values);

    private final String name;

    HierarchyType(String name) {
        this.name = name;
    }

    @NotNull
    public static HierarchyType typeFromString(String str) {
        for (HierarchyType hierarchyType : HierarchyType.values()) if (str.equals(hierarchyType.name)) return hierarchyType;
        throw new TalentDataException("This hierarchy type does not exist");
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }

    public <T, M extends IHierarchy<T>> Codec<M> getCodec(Codec<T> tCodec) {
        return switch (this) {
            case MONO -> (Codec<M>) MonoHierarchy.monoHierarchyCodec(tCodec);
            case CHAIN -> (Codec<M>) ChainHierarchy.chainHierarchyCodec(tCodec);
            case CHAOTIC -> (Codec<M>) ChaoticHierarchy.chaoticHierarchyCodec(tCodec);
            case COMPLEX -> (Codec<M>) ComplexHierarchy.complexHierarchyCodec(tCodec);
        };
    }
}
