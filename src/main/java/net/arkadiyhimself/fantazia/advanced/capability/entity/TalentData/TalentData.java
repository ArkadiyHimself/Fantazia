package net.arkadiyhimself.fantazia.advanced.capability.entity.TalentData;

import com.google.common.collect.Maps;
import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.advanced.capacity.AbilityProviding.Talent;
import net.arkadiyhimself.fantazia.util.library.hierarchy.HierarchyManager;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;

public class TalentData extends PlayerCapability {
    private final HierarchyManager<Talent> HIERARCHY = new HierarchyManager<>();
    private final List<Talent> ALL = Lists.newArrayList();
    private final List<Talent> UNLOCKED = Lists.newArrayList();
    private final HashMap<Talent, Integer> PRICES = Maps.newHashMap();
    private int WISDOM;
    public TalentData(Player player) {
        super(player);
    }
    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.player.getId(), TalentGetter.TALENT_DATA_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }
    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();

        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {

    }
    public int getWisdom() {
        return WISDOM;
    }

    private List<Talent> allTalents() {
        return HIERARCHY.getAllItems();
    }
    public void mainTalent(Talent main) {
        HIERARCHY.putMainElement(main);
        ALL.add(main);
    }
    public void secondaryElement(Talent secondary, Talent main) {
        HIERARCHY.putElement(secondary, main);
        ALL.add(secondary);
    }
    public boolean unlocked(Talent talent) {
        return UNLOCKED.contains(talent);
    }
    public boolean unlockable(Talent talent) {
        return UNLOCKED.contains(HIERARCHY.getParent(talent)) && enoughWisdom(talent);
    }
    public void unlock(Talent talent) {
        if (!ALL.contains(talent)) return;
        if (!UNLOCKED.contains(talent)) UNLOCKED.add(talent);
    }
    public boolean enoughWisdom(Talent talent) {
        return !PRICES.containsKey(talent) || PRICES.get(talent) <= WISDOM;
    }
    public void buyTalent(Talent talent) {
        if (!PRICES.containsKey(talent) || unlocked(talent)) return;
        WISDOM -= PRICES.get(talent);
        unlock(talent);
    }
}
