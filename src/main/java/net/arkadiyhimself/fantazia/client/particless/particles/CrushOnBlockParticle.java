package net.arkadiyhimself.fantazia.client.particless.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class CrushOnBlockParticle extends TextureSheetParticle {

    private final BlockPos pos;
    private final float uo;
    private final float vo;

    public CrushOnBlockParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, BlockState state) {
        this(level, x, y, z, xSpeed, ySpeed, zSpeed, state, BlockPos.containing(x, y, z));
    }

    public CrushOnBlockParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, BlockState state, BlockPos pos) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.pos = pos;
        this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(state));
        this.gravity = 1.0F;
        this.rCol = 0.6F;
        this.gCol = 0.6F;
        this.bCol = 0.6F;
        if (IClientBlockExtensions.of(state).areBreakingParticlesTinted(state, level, pos)) {
            int i = Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0);
            this.rCol *= (float)(i >> 16 & 255) / 255.0F;
            this.gCol *= (float)(i >> 8 & 255) / 255.0F;
            this.bCol *= (float)(i & 255) / 255.0F;
        }

        this.quadSize /= 2.0F;
        this.uo = this.random.nextFloat() * 3.0F;
        this.vo = this.random.nextFloat() * 3.0F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
    }

    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0F) / 4.0F);
    }

    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0F);
    }

    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0F);
    }

    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0F) / 4.0F);
    }

    public int getLightColor(float partialTick) {
        int i = super.getLightColor(partialTick);
        return i == 0 && this.level.hasChunkAt(this.pos) ? LevelRenderer.getLightColor(this.level, this.pos) : i;
    }

    @Nullable
    static CrushOnBlockParticle createParticle(BlockParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        BlockState blockstate = type.getState();
        return !blockstate.isAir() && !blockstate.is(Blocks.MOVING_PISTON) && blockstate.shouldSpawnTerrainParticles() ? (new CrushOnBlockParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, blockstate)).updateSprite(blockstate, type.getPos()) : null;
    }

    public CrushOnBlockParticle updateSprite(BlockState state, BlockPos pos) {
        if (pos != null) {
            this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(state, this.level, pos));
        }

        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public record Provider(SpriteSet spriteSet) implements ParticleProvider<BlockParticleOption> {

        @Nullable
        public Particle createParticle(@NotNull BlockParticleOption type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return CrushOnBlockParticle.createParticle(type, level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
