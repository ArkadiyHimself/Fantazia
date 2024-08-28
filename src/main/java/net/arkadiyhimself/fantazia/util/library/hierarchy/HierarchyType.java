package net.arkadiyhimself.fantazia.util.library.hierarchy;

import net.arkadiyhimself.fantazia.data.talents.TalentDataException;
import org.jetbrains.annotations.NotNull;

public enum HierarchyType {
    MONO("mono"),
    CHAIN("chain"),
    CHAOTIC("chaotic"),
    COMPLEX("complex");
    private final String type;
    HierarchyType(String type) {
        this.type = type;
    }
    @NotNull
    public static HierarchyType typeFromString(String str) {
        for (HierarchyType hierarchyType : HierarchyType.values()) if (str.equals(hierarchyType.type)) return hierarchyType;
        throw new TalentDataException("This hierarchy type does not exist");
    }
}
