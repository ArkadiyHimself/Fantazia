package net.arkadiyhimself.fantazia.items.weapons.Melee;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.ClientValues;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.api.items.ITooltipBuilder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlayAnimationS2C;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class Murasama extends MeleeWeaponItem implements ITooltipBuilder {
    public Murasama() {
        super(new Properties().stacksTo(1).defaultDurability(512).fireResistant().rarity(Rarity.EPIC), 10, -2.3f, "murasama");
        this.attackDamage = 10;
        this.attackSpeedModifier = -2.3f;
    }
    @Override
    public boolean mineBlock(@NotNull ItemStack pStack, @NotNull Level pLevel, BlockState pState, @NotNull BlockPos pPos, @NotNull LivingEntity pEntityLiving) {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(2, pEntityLiving, (p_43276_) -> p_43276_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return true;
    }
    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return pBlock.is(Blocks.COBWEB);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }
    @Override
    public boolean hasActive() { return true; }
    @Override
    @SuppressWarnings("ConstantConditions")
    public void activeAbility(ServerPlayer player) {
        NetworkHandler.sendToPlayer(new PlayAnimationS2C("taunt"), player);
        ServerLevel level = (ServerLevel) player.level();
        AABB aabb = player.getBoundingBox().inflate(10);
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, aabb);
        mobs.removeIf(mob -> !mob.hasLineOfSight(player));
        for (Mob mob : mobs) {
            EffectHelper.makeFurious(mob, 300);
            mob.setTarget(player);
            if (player.isCreative() || player.isSpectator()) return;
            if (mob instanceof TamableAnimal animal && animal.getOwner() == player) continue;
            if (mob instanceof NeutralMob neutralMob) {
                neutralMob.setTarget(player);
            }
            if (mob instanceof Warden warden) {
                warden.increaseAngerAt(player, AngerLevel.ANGRY.getMinimumAnger() + 20, false);
                warden.setAttackTarget(player);
            }
        }
        AbilityGetter.abilityConsumer(player, ClientValues.class, ClientValues::taunt);
    }

    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot pEquipmentSlot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers = ImmutableMultimap.builder();
        modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        modifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", this.attackSpeedModifier, AttributeModifier.Operation.ADDITION));
        modifiers.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(UUID.fromString("d020cd5d-c050-49e4-a0ea-ef27adf7e6d0"), "Weapon modifier", 0.5, AttributeModifier.Operation.ADDITION));
        return pEquipmentSlot == EquipmentSlot.MAINHAND ? modifiers.build() : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }

    @Override
    public List<Component> buildItemTooltip(ItemStack stack) {
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

        components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.weapon", new ChatFormatting[]{ChatFormatting.RED}, new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD}, Component.translatable("weapon.fantazia.taunt.name").getString()));
        components.add(Component.translatable(" "));
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
