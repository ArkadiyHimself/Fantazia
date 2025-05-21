package net.arkadiyhimself.fantazia.api.custom_events;

import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.MeleeBlockHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Children of {@link BlockingEvent} are fired when an event involving blocking attacks occurs <br>
 * All the events here are fired in {@link MeleeBlockHolder} <br>
 * <br>
 * {@link  #itemStack} contains the ItemStack with the weapon the player uses for blocking <br>
 * <br>
 * The events are fired on the {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS}.
 */
public abstract class BlockingEvent extends PlayerEvent {
    private ItemStack itemStack;
    public BlockingEvent(Player player, ItemStack itemStack) {
        super(player);
        this.itemStack = itemStack;
    }
    public ItemStack getItemStack() {
        return this.itemStack;
    }
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * {@link Start} is fired when a player attempts to start blocking attacks using their weapon. <br>
     * <br>
     * This event is {@link ICancellableEvent}.<br>
     * If it is canceled, the player does not start blocking attacks
     */
    public static class Start extends BlockingEvent implements ICancellableEvent {
        public Start(Player player, ItemStack itemStack) {
            super(player, itemStack);
        }
    }

    /**
     * {@link Expired} is fired when player's blocking ticks get to 0 after the player started blocking attacks.<br>
     * <br>
     * This event is not {@link ICancellableEvent}.<br>
     */
    public static class Expired extends BlockingEvent {
        public Expired(Player player, ItemStack itemStack) {
            super(player, itemStack);
        }
    }

    /**
     * {@link ParryDecision} is fired when player blocks an attack and the game decides <br>
     * whether it should be a parry or a regular block.<br>
     * <br>
     * {@link #attackerDamage} contains the amount of damage that was blocked <br>
     * {@link #attacker} contains the entity who dealt the damage <br>
     * <br>
     * This event {@link Result has a result}<br>
     * <br>
     * {@link Result#DO_PARRY PARRY} will force the parry to happen no matter the circumstances. <br>
     * {@link Result#DO_NOT_PARRY DO_NOT_PARRY} will deny the parry no matter the circumstances. <br>
     * {@link Result#DEFAULT DEFAULT} will run the mod's regular logic to determine if parry should happen or not <br>
     */
    public static class ParryDecision extends BlockingEvent {
        private Result result;
        private final float attackerDamage;
        private final LivingEntity attacker;
        public float getAttackerDamage() {
            return attackerDamage;
        }
        public LivingEntity getAttacker() {
            return attacker;
        }
        public ParryDecision(Player player, ItemStack itemStack, float attackerDamage, LivingEntity attacker) {
            super(player, itemStack);
            this.attackerDamage = attackerDamage;
            this.attacker = attacker;
            this.result = Result.DEFAULT;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public enum Result {
            DO_PARRY,
            DEFAULT,
            DO_NOT_PARRY;

            Result() {
            }
        }
    }
    
    /**
     * {@link Block} is fired when player successfully blocks an attack but doesn't parry it <br>
     * <br>
     * {@link #attackerDamage} contains the amount of damage that was blocked <br>
     * {@link #attacker} contains the entity who dealt the damage <br>
     * <br>
     * This event is {@link ICancellableEvent}.<br>
     * If it is canceled, the player does not block the attack
     */
    public static class Block extends BlockingEvent implements ICancellableEvent{

        private final float attackerDamage;
        private final LivingEntity attacker;

        public Block(Player player, ItemStack itemStack, float attackerDamage, LivingEntity attacker) {
            super(player, itemStack);
            this.attackerDamage = attackerDamage;
            this.attacker = attacker;
        }

        public float getAttackerDamage() {
            return attackerDamage;
        }

        public LivingEntity getAttacker() {
            return attacker;
        }

    }
    /**
     * {@link Parry} is fired when player successfully parries an attack <br>
     * <br>
     * {@link #attackerDamage} contains the amount of damage that was blocked <br>
     * {@link #attacker} contains the entity who dealt the damage <br>
     * {@link #parryDamage} contains the amount of damage that will be dealt to {@link #attacker} <br>
     * <br>
     * This event is {@link ICancellableEvent}.<br>
     * If it is canceled, the player does not parry an attack
     */
    public static class Parry extends BlockingEvent implements ICancellableEvent {
        private final float attackerDamage;
        private final LivingEntity attacker;
        private float parryDamage;
        public Parry(Player player, ItemStack itemStack, float attackerDamage, LivingEntity attacker, float parryDamage) {
            super(player, itemStack);
            this.attackerDamage = attackerDamage;
            this.attacker = attacker;
            this.parryDamage = parryDamage;
        }
        public float getAttackerDamage() {
            return attackerDamage;
        }
        public float getParryDamage() {
            return parryDamage;
        }
        public void setParryDamage(float parryDamage) {
            this.parryDamage = parryDamage;
        }
        public LivingEntity getAttacker() {
            return attacker;
        }
    }
}
