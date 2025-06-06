package net.arkadiyhimself.fantazia.entities;

import com.google.common.collect.Lists;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.FantazicBossEvent;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.api.curio.FTZSlots;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ApplyEffect;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DashStone extends Entity {

    private static final EntityDataAccessor<ItemStack> DASHSTONE = SynchedEntityData.defineId(DashStone.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Float> PROGRESS = SynchedEntityData.defineId(DashStone.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> Y_ROT = SynchedEntityData.defineId(DashStone.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> OWNER = SynchedEntityData.defineId(DashStone.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> UNPICKABLE = SynchedEntityData.defineId(DashStone.class, EntityDataSerializers.BOOLEAN);

    private final Component dashStoneFight = Component.translatable("fantazia.boss_event.dashstone_fight").withStyle(ChatFormatting.RED);
    private final Component dashStoneFightEnded = Component.translatable("fantazia.boss_event.dashstone_fight_ended").withStyle(ChatFormatting.BLUE);

    private final FantazicBossEvent serverBossEvent = new FantazicBossEvent(
            dashStoneFight,
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private float bossHealthBarInitial = 0f;
    private float bossBarrierBarInitial = 0f;

    private @Nullable UUID ownerUUID = null;
    private @Nullable ServerPlayer cachedOwner = null;
    private int level;
    private int soundRecharge = 0;
    private int cooldown = 0;

    public float yRot0 = 0f;
    public float yRot1 = 0;

    private final List<UUID> protectorsServer = Lists.newArrayList();
    private final List<Integer> protectorsClient = Lists.newArrayList();

    public DashStone(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.level = 0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        pBuilder.define(DASHSTONE, ItemStack.EMPTY);
        pBuilder.define(PROGRESS, 0f);
        pBuilder.define(Y_ROT, 0f);
        pBuilder.define(OWNER, -1);
        pBuilder.define(UNPICKABLE, false);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (ownerUUID != null) pCompound.putUUID("owner", ownerUUID);
        ItemStack stack = entityData.get(DASHSTONE);
        if (!stack.isEmpty()) pCompound.put("dashstone", stack.save(this.registryAccess()));
        pCompound.putInt("level", level);

        ListTag listTag = new ListTag();
        for (UUID protector : protectorsServer) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("protector", protector);
            listTag.add(entry);
        }
        pCompound.put("protectors", listTag);

        pCompound.putFloat("bossHealthBarInitial", bossHealthBarInitial);
        pCompound.putFloat("bossBarrierBarInitial", bossBarrierBarInitial);
        pCompound.putBoolean("unpickable", entityData.get(UNPICKABLE));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        if (pCompound.contains("owner")) this.ownerUUID = pCompound.getUUID("owner");
        if (pCompound.contains("dashstone")) this.entityData.set(DASHSTONE, ItemStack.parse(registryAccess(), pCompound.getCompound("dashstone")).orElse(ItemStack.EMPTY));
        if (pCompound.contains("level")) this.level = pCompound.getInt("level");

        ListTag listTag = pCompound.getList("protectors", Tag.TAG_COMPOUND);
        for (int i = 0; i <= listTag.size(); i++) {
            try {
                CompoundTag entry = listTag.getCompound(i);
                UUID protector = entry.getUUID("protector");
                protectorsServer.add(protector);
            } catch (Exception ignored) {}
        }

        bossHealthBarInitial = pCompound.getFloat("bossHealthBarInitial");
        bossBarrierBarInitial = pCompound.getFloat("bossBarrierBarInitial");
        entityData.set(UNPICKABLE, pCompound.getBoolean("unpickable"));
    }

    @Override
    public void playerTouch(@NotNull Player pPlayer) {
        if (level().isClientSide() || !pPlayer.getUUID().equals(ownerUUID) || !protectorsServer.isEmpty()) return;

        ICuriosItemHandler curiosItemHandler = CuriosApi.getCuriosInventory(pPlayer).orElse(null);
        if (curiosItemHandler == null) return;

        Optional<SlotResult> slotResult = curiosItemHandler.findCurio(FTZSlots.DASHSTONE, 0);
        if (slotResult.isEmpty()) return;
        ItemStack item = slotResult.get().stack();
        Integer dashLevel = item.get(FTZDataComponentTypes.DASH_LEVEL);

        if (dashLevel == null || level < dashLevel) return;

        curiosItemHandler.setEquippedCurio("dashstone", 0, entityData.get(DASHSTONE));
        VisualHelper.circleOfParticles(ParticleTypes.PORTAL, this.position());
        reset();
        PlayerAbilityHelper.acceptConsumer(pPlayer, DashHolder.class, DashHolder::dashStoneEntityTouched);
    }

    @Override
    public void tick() {
        super.tick();

        if (cooldown > 0) cooldown--;
        if (level().isClientSide()) visualTick();

        if (serverBossEvent.getPlayers().isEmpty()) {
            ServerPlayer serverPlayer = getOwner();
            if (serverPlayer != null && serverPlayer.distanceTo(this) <= 32) serverBossEvent.addPlayer(serverPlayer);
        } else for (ServerPlayer player : serverBossEvent.getPlayers()) if (player.distanceTo(this) > 32) serverBossEvent.removePlayer(player);

        if (level() instanceof ServerLevel serverLevel) {
            if (getOwner() != null) {
                this.soundRecharge--;
                if (soundRecharge <= 0) {
                    soundRecharge = RandomUtil.nextInt(25, 30);
                    serverLevel.playSound(null, blockPosition(), FTZSoundEvents.DASHSTONE_WIND.value(), SoundSource.AMBIENT, 0.06f, 1f);
                }
            }

            protectorsServer.removeIf(protId -> {
                Entity entity = serverLevel.getEntity(protId);
                return entity != null && !entity.isAlive();
            });

            ServerPlayer owner = getOwner();
            if (owner != null) entityData.set(OWNER, owner.getId());
            boolean prevPick = entityData.get(UNPICKABLE);
            entityData.set(UNPICKABLE, !protectorsServer.isEmpty());
            if (prevPick && !entityData.get(UNPICKABLE) && owner != null) {
                IPacket.playSoundForUI(owner, FTZSoundEvents.DASHSTONE_READY.value());
            }

            if (ownerUUID == null && cooldown <= 0) tryAdaptToPlayer(serverLevel);
            protectorsClient.clear();
            for (UUID protID : protectorsServer) {
                Entity entity = serverLevel.getEntity(protID);
                if (entity == null) return;
                protectorsClient.add(entity.getId());
            }
            IPacket.addDashStoneProtectors(this, protectorsClient);

            float healthBarCurrent = 0f;
            float barrierBarCurrent = 0f;
            for (UUID protID : protectorsServer) {
                Entity entity = serverLevel.getEntity(protID);
                if (!(entity instanceof LivingEntity livingEntity)) return;
                healthBarCurrent += livingEntity.getHealth();

                barrierBarCurrent += entity.getData(FTZAttachmentTypes.BARRIER_HEALTH);
            }


            float progress = Mth.clamp(healthBarCurrent / Math.max(bossHealthBarInitial, 1f), 0f, 1f);
            float barrier = Mth.clamp(barrierBarCurrent / Math.max(bossBarrierBarInitial, 1f), 0f, 1f);
            serverBossEvent.setProgress(progress, barrier);
            if (progress == 0) {
                serverBossEvent.setColor(BossEvent.BossBarColor.BLUE);
                serverBossEvent.setName(dashStoneFightEnded);
            }
            entityData.set(PROGRESS, progress);

            Difficulty difficulty = serverLevel.getDifficulty();
            if (difficulty.getId() >= 2) {
                List<LivingEntity> protectors = getProtectors();
                boolean heal = false;
                for (LivingEntity livingEntity : protectors) if (livingEntity.hasEffect(FTZMobEffects.BARRIER)) heal = true;
                if (heal) protectors.stream().filter(LivingEntity::isAlive).forEach(livingEntity -> {
                    livingEntity.heal(difficulty.getId() == 3 ? 0.75f : 0.35f);
                    LivingEffectHelper.healStunPoints(livingEntity, difficulty.getId() + 5,true);

                    MobEffectInstance stun = livingEntity.getEffect(FTZMobEffects.STUN);
                    if (stun != null) stun.tick(livingEntity, () -> {});
                });
            }
        } else {
            protectorsClient.removeIf(protID -> {
                Entity entity = level().getEntity(protID);
                return entity == null;
            });
        }
    }

    @Override
    public int getTeamColor() {
        DashHolder dashHolder = PlayerAbilityHelper.takeHolder(Minecraft.getInstance().player, DashHolder.class);
        if (dashHolder == null) return super.getTeamColor();
        DashStone dashStone = dashHolder.getDashstoneEntity(Minecraft.getInstance().level);
        float progress = entityData.get(PROGRESS);
        int red = Mth.clamp((int) (195 + progress * 60), 0, 255);
        int blue = Mth.clamp((int) (250 - progress * 200), 0, 255);
        return dashStone == this ? new Color(red,25, blue).getRGB() : super.getTeamColor();
    }

    public @Nullable ServerPlayer getOwner() {
        if (cachedOwner == null && ownerUUID != null && level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(ownerUUID);
            if (entity instanceof ServerPlayer serverPlayer) this.cachedOwner = serverPlayer;
        }
        return cachedOwner;
    }

    private void visualTick() {
        if (entityData.get(OWNER) == -1) return;

        double dist = 0;
        int ownerId = entityData.get(OWNER);
        Entity entity = level().getEntity(ownerId);
        if (entity != null) dist = this.distanceTo(entity);
        float rotSpeed = (float) Math.exp(-0.2 * (dist)) * 35f;

        this.yRot0 = yRot1;
        this.yRot1 -= Math.min(rotSpeed, 25);
    }

    public ItemStack getDashstone() {
        return entityData.get(DASHSTONE);
    }

    public void reset() {
        this.ownerUUID = null;
        this.level = 0;
        this.soundRecharge = 0;
        entityData.set(DASHSTONE, ItemStack.EMPTY);
        entityData.set(OWNER, -1);
        cooldown = 10;
        serverBossEvent.removeAllPlayers();
        ownerUUID = null;
        cachedOwner = null;
        bossHealthBarInitial = 0f;
        bossBarrierBarInitial = 0f;
    }

    private void tryAdaptToPlayer(ServerLevel serverLevel) {
        List<? extends ServerPlayer> players = serverLevel.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(this.position(), 16,16,16));
        for (ServerPlayer player : players) {
            DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.getLevel() == 1 && dashHolder.getDashstoneEntity(serverLevel) == null) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.1,0,0.1,0);
                this.playSound(FTZSoundEvents.DASHSTONE_APPEARED.value());
                dashHolder.setDashstoneEntityServer(this);
                this.ownerUUID = player.getUUID();
                this.level = 2;
                this.soundRecharge = 0;
                entityData.set(DASHSTONE, FantazicUtil.dashStone(2));
                entityData.set(OWNER, player.getId());

                summonProtector(-2.5, -2.5, Items.IRON_SWORD);
                summonProtector(-2.5, +2.5, Items.BOW);
                summonProtector(+2.5, +2.5, Items.IRON_SWORD);
                summonProtector(+2.5, -2.5, Items.BOW);

                bossHealthBarInitial = Math.max(bossHealthBarInitial, 1f);
                bossBarrierBarInitial = Math.max(bossBarrierBarInitial, 1f);

                for (UUID protID : protectorsServer) {
                    Entity entity = serverLevel.getEntity(protID);
                    if (entity != null) protectorsClient.add(entity.getId());
                }
                IPacket.addDashStoneProtectors(this, protectorsClient);

                LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
                if (lightningBolt != null) {
                    lightningBolt.setPos(this.getX(), this.getY() - 3, this.getZ());
                    serverLevel.addFreshEntity(lightningBolt);
                }
                serverBossEvent.setColor(BossEvent.BossBarColor.RED);
                serverBossEvent.setName(dashStoneFight);
                entityData.set(UNPICKABLE, true);
                return;
            }
        }
    }

    private void summonProtector(double xOff, double zOff, Item weapon) {
        WitherSkeleton protector = EntityType.WITHER_SKELETON.create(this.level());
        if (protector == null) return;
        protector.setPos(getX() + xOff, getY() + (double) -1, getZ() + zOff);
        int barr = 20 + 5 * level().getDifficulty().getId();
        ApplyEffect.giveAbsoluteBarrier(protector,30);
        protector.addEffect(new MobEffectInstance(FTZMobEffects.BARRIER, -1, barr - 1, true, true));
        protectorsServer.add(protector.getUUID());
        level().addFreshEntity(protector);
        bossHealthBarInitial += protector.getMaxHealth();
        bossBarrierBarInitial += barr;
        protector.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapon));
        protector.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        protector.setData(FTZAttachmentTypes.DASHSTONE_MINION, true);
    }

    public void addProtectorsClient(List<Integer> ids) {
        protectorsClient.clear();
        protectorsClient.addAll(ids);
    }

    public boolean isProtectorClient(Entity entity) {
        return protectorsClient.contains(entity.getId());
    }

    public List<LivingEntity> getProtectors() {
        List<LivingEntity> protectors = Lists.newArrayList();
        if (level() instanceof ServerLevel serverLevel) {
            for (UUID protID : protectorsServer) {
                Entity entity = serverLevel.getEntity(protID);
                if (entity instanceof LivingEntity livingEntity) protectors.add(livingEntity);
            }
        } else {
            for (Integer protID : protectorsClient) {
                Entity entity = level().getEntity(protID);
                if (entity instanceof LivingEntity livingEntity) protectors.add(livingEntity);
            }
        }
        return protectors;
    }
}
