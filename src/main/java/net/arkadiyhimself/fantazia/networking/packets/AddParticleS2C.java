package net.arkadiyhimself.fantazia.networking.packets;

import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class AddParticleS2C implements IPacket {
    private final Vec3 position;
    private final Vec3 delta;
    private final ParticleOptions particle;
    public AddParticleS2C(Vec3 position, Vec3 delta, ParticleOptions particle) {
        this.position = position;
        this.delta = delta;
        this.particle = particle;
    }
    @Override
    public void handle(NetworkEvent.Context context) {
        if (Minecraft.getInstance().level == null) return;
        context.enqueueWork(() -> {
            Minecraft.getInstance().level.addParticle(particle, position.x(), position.y(), position.z(), delta.x(), delta.y(), delta.z());
        });
        context.setPacketHandled(true);
    }
    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeId(BuiltInRegistries.PARTICLE_TYPE, particle.getType());
        packetBuf.writeDouble(position.x());
        packetBuf.writeDouble(position.y());
        packetBuf.writeDouble(position.z());
        packetBuf.writeDouble(delta.x());
        packetBuf.writeDouble(delta.y());
        packetBuf.writeDouble(delta.z());
    }
    private static  <T extends ParticleOptions> T readParticle(FriendlyByteBuf pBuffer, ParticleType<T> pParticleType) {
        return pParticleType.getDeserializer().fromNetwork(pParticleType, pBuffer);
    }
    public static AddParticleS2C read(FriendlyByteBuf packetBuf) {
        ParticleType<?> particletype = packetBuf.readById(BuiltInRegistries.PARTICLE_TYPE);
        double x0 = packetBuf.readDouble();
        double y0 = packetBuf.readDouble();
        double z0 = packetBuf.readDouble();
        double dx = packetBuf.readDouble();
        double dy = packetBuf.readDouble();
        double dz = packetBuf.readDouble();
        return new AddParticleS2C(new Vec3(x0, y0, z0), new Vec3(dx, dy, dz), readParticle(packetBuf, particletype));
    }
    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, AddParticleS2C.class, AddParticleS2C::read);
    }
}
