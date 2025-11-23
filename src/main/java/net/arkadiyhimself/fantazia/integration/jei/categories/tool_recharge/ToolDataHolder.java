package net.arkadiyhimself.fantazia.integration.jei.categories.tool_recharge;

import net.arkadiyhimself.fantazia.common.registries.FTZDataMapTypes;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.RechargeableToolData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;

public record ToolDataHolder(Item item, RechargeableToolData data) {

    public static List<ToolDataHolder> getAllRecipes() {
        List<ToolDataHolder> list = Lists.newArrayList();

        Map<ResourceKey<Item>, RechargeableToolData> map = BuiltInRegistries.ITEM.getDataMap(FTZDataMapTypes.RECHARGEABLE_TOOLS);

        for (Map.Entry<ResourceKey<Item>, RechargeableToolData> entry : map.entrySet()) {
            Item item = BuiltInRegistries.ITEM.get(entry.getKey());
            if (item == null) continue;

            ToolDataHolder holder = new ToolDataHolder(item, entry.getValue());
            list.add(holder);
        }
        return list;
    }
}
