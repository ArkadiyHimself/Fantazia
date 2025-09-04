package net.arkadiyhimself.fantazia.common.item.weapons.Melee;

import com.google.common.collect.Lists;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ApplyEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MurasamaItem extends MeleeWeaponItem {

    public MurasamaItem() {
        super(new Properties().stacksTo(1).durability(512).fireResistant().rarity(Rarity.EPIC), 10, -2.3f, "murasama");
        this.attackDamage = 10;
        this.attackSpeedModifier = -2.3f;
    }

    @Override
    public boolean hasActive() {
        return true;
    }

    @Override
    public void activeAbility(ServerPlayer player) {
        player.getData(FTZAttachmentTypes.MURASAMA_TAUNT_TICKS).set(30);
        IPacket.animatePlayer(player,"taunt");
        if (player.hasEffect(FTZMobEffects.DISGUISED)) return;
        ServerLevel level = (ServerLevel) player.level();
        AABB aabb = player.getBoundingBox().inflate(10);
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, aabb);
        mobs.removeIf(mob -> !mob.hasLineOfSight(player));
        for (Mob mob : mobs) {
            ApplyEffect.makeFurious(mob, 300);
            if (player.isCreative() || player.isSpectator()) continue;
            mob.setTarget(player);
            if (mob instanceof TamableAnimal animal && animal.getOwner() == player) continue;
            if (mob instanceof NeutralMob neutralMob) neutralMob.setTarget(player);
            if (mob instanceof Warden warden) {
                warden.increaseAngerAt(player, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
                warden.setAttackTarget(player);
            }
        }
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Fantazia.location("item.murasama"), this.attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(Fantazia.location("item.murasama"), this.attackSpeedModifier, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(Fantazia.location("item.murasama"), 0.5F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        return builder.build();
    }

    @Override
    public List<Component> itemTooltip(ItemStack stack) {
        List<Component> components = Lists.newArrayList();
        String basicPath = "weapon.fantazia.taunt";
        int lines;

        if (!Screen.hasShiftDown()) {
            String desc = Component.translatable(basicPath + ".desc.lines").getString();
            try {
                lines = Integer.parseInt(desc);
            } catch (NumberFormatException ignored) {
                return components;
            }

            ChatFormatting[] noShift = new ChatFormatting[]{ChatFormatting.RED};
            for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + ".desc." + i, noShift, null));

            return components;
        }

        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.weapon.ability", new ChatFormatting[]{ChatFormatting.RED}, new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD}, Component.translatable("weapon.fantazia.taunt.name").getString()));
        components.add(Component.literal(" "));
        String text = Component.translatable(basicPath + ".lines").getString();

        try {
            lines = Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return components;
        }

        ChatFormatting[] main = new ChatFormatting[]{ChatFormatting.GOLD};
        for (int i = 1; i <= lines; i++) components.add(GuiHelper.bakeComponent(basicPath + "." + i, main, null));

        return components;
    }
}
