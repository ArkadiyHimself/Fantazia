package net.arkadiyhimself.fantazia.integration.jei;

import java.util.Arrays;

public class JEIHelper {

    public static void tryOpenAllWisdomRewards() {
        if (FantazicJEIPlugin.RUNTIME != null)
            FantazicJEIPlugin.RUNTIME.getRecipesGui().showTypes(Arrays.asList(FantazicJEIPlugin.getWisdomPairs()));    }
}
