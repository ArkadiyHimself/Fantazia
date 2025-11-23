package net.arkadiyhimself.fantazia.common.item.skong;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.client.gui.TextComponents;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.ToolUtilisationHolder;
import net.arkadiyhimself.fantazia.common.item.ITooltipBuilder;
import net.arkadiyhimself.fantazia.common.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.RechargeableToolData;
import net.arkadiyhimself.fantazia.data.tags.FTZBlockTags;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class RechargeableToolItem extends Item implements ITooltipBuilder {

    private final ChatFormatting[] descForm;
    private final int cooldown;

    public RechargeableToolItem(ChatFormatting[] descForm, int cooldown) {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
        this.descForm = descForm;
        this.cooldown = cooldown;
    }

    protected abstract void successfulUse(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand, float damage);

    public abstract RechargeableToolData defaultData();

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        boolean flag = player.hasInfiniteMaterials();
        ToolUtilisationHolder holder = PlayerAbilityHelper.takeHolder(player, ToolUtilisationHolder.class);
        RechargeableToolData data = RechargeableToolData.getToolData(this);
        if (holder == null || data == null || !holder.canUse(stack.getItem()) && !flag) {
            if (player instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.DENIED.value());
            return InteractionResultHolder.fail(stack);
        }
        if (!flag) holder.consume(stack.getItem());

        int cd = cooldown;
        AttributeInstance recharge = player.getAttribute(FTZAttributes.RECHARGE_MULTIPLIER);
        if (recharge != null)
            cd = Math.round((float) ((recharge.getValue() / 100f) * cd));

        player.getCooldowns().addCooldown(this, flag ? 2 : cd);
        successfulUse(level, player, usedHand, data.damage().apply(holder.getDamageUpgrades()));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();

        ChatFormatting[] head = new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE};
        ChatFormatting[] value = new ChatFormatting[]{ChatFormatting.DARK_PURPLE};
        String basicString = getDescriptionId();

        if (!Screen.hasShiftDown()) {
            String recharge = String.format("%.1f", ((float) cooldown) / 20);
            components.add(GuiHelper.bakeComponent("tooltip.fantazia.common.rechargeable_tool.cooldown", head, value, recharge));
            components.add(Component.literal(" "));
            components.add(TextComponents.HOLD_SHIFT_TO_SEE_MORE_COMPONENT);
            return components;
        }

        int lines = 0;
        String descStr = Component.translatable(basicString + ".lines").getString();
        try {
            lines = Integer.parseInt(descStr);
        } catch (NumberFormatException ignored) {}
        if (lines <= 0) return components;

        for (int i = 1; i <= lines; i++) {
            components.add(GuiHelper.bakeComponent(basicString + "." + i, descForm, null));
        }

        if (Fantazia.DEVELOPER_MODE) {
            components.add(Component.literal(" "));
            ToolUtilisationHolder holder = PlayerAbilityHelper.takeHolder(Minecraft.getInstance().player, ToolUtilisationHolder.class);
            RechargeableToolData data = RechargeableToolData.getToolData(this);
            if (data == null || holder == null) return components;

            components.add(Component.literal("Capacity: " + data.capacity().toString() + ", current value: " + data.capacity().apply(holder.getCapacityUpgrades())));
            components.add(Component.literal("Capacity upgrades: " + holder.getCapacityUpgrades()));
            components.add(Component.literal("Initial value: " + data.getInitialAmount()));
            components.add(Component.literal("Damage: " + data.damage().toString() + ", current value: " + data.damage().apply(holder.getDamageUpgrades())));
            components.add(Component.literal("Damage upgrades: " + holder.getDamageUpgrades()));
        }

        return components;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos blockPos = context.getClickedPos();

        BlockState state = level.getBlockState(blockPos);
        if (!state.is(FTZBlockTags.ENGINEERING_TABLES)) return InteractionResult.PASS;

        ToolUtilisationHolder holder = PlayerAbilityHelper.takeHolder(player, ToolUtilisationHolder.class);
        if (holder == null || !holder.tryRecharge(this)) {
            if (player instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.DENIED.value());
            return InteractionResult.FAIL;
        } else return InteractionResult.SUCCESS;
    }
}
