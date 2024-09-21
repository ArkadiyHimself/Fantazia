package net.arkadiyhimself.fantazia.data.criteritas;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class TalentPredicate {
    public static final TalentPredicate ANY = new TalentPredicate();
    @Nullable
    private final Set<ResourceLocation> talents;
    public TalentPredicate(@Nullable Set<ResourceLocation> talents) {
        this.talents = talents;
    }
    public TalentPredicate() {
        this.talents = null;
    }
    public boolean matches(BasicTalent talent) {
        if (this == ANY) return true;
        return this.talents != null && this.talents.contains(talent.getID());
    }
    public boolean matches(ResourceLocation talent) {
        if (this == ANY) return true;
        return this.talents != null && this.talents.contains(talent);
    }
    public static TalentPredicate fromJson(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) return ANY;
        JsonObject object = GsonHelper.convertToJsonObject(element, "fantazia:talent");
        Set<ResourceLocation> talents = null;
        JsonArray array = GsonHelper.getAsJsonArray(object, "talents", null);
        if (array != null) {
            ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

            for (JsonElement jsonElement : array) builder.add(new ResourceLocation(GsonHelper.convertToString(jsonElement, "talent")));

            talents = builder.build();
        }
        return new TalentPredicate(talents);
    }
    public JsonElement toJson() {
        if (this == ANY) return JsonNull.INSTANCE;
        JsonObject jsonobject = new JsonObject();
        if (this.talents != null) {
            JsonArray jsonarray = new JsonArray();

            for(ResourceLocation talent : this.talents) jsonarray.add(talent.toString());

            jsonobject.add("talent", jsonarray);
        }
        return jsonobject;
    }
    public static TalentPredicate[] fromJsonArray(@javax.annotation.Nullable JsonElement pJson) {
        if (pJson != null && !pJson.isJsonNull()) {
            JsonArray jsonarray = GsonHelper.convertToJsonArray(pJson, "talents");
            TalentPredicate[] talentPredicates = new TalentPredicate[jsonarray.size()];

            for(int i = 0; i < talentPredicates.length; ++i) talentPredicates[i] = fromJson(jsonarray.get(i));

            return talentPredicates;
        } else return new TalentPredicate[0];
    }

}
