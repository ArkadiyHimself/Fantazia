package net.arkadiyhimself.fantazia.data.talent;

import com.mojang.datafixers.util.Pair;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;

import java.util.Optional;

public record TalentHierarchy(
        IHierarchy<Talent> hierarchy,
        boolean drawLine,
        Optional<Pair<Integer, Integer>> lineColor
) {
}
