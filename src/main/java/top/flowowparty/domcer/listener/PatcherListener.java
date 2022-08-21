package top.flowowparty.domcer.listener;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import top.flowowparty.domcer.DoMCerPatcher;
import top.flowowparty.domcer.fake.FakeCustomSkinLoader;
import top.flowowparty.domcer.fake.FakeUView;

import java.nio.charset.StandardCharsets;

import java.util.UUID;

/**
 * @author ImFl0wow
 */
public class PatcherListener {

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.CustomPacketRegistrationEvent<NetHandlerPlayClient> event) {
        if ("REGISTER".equals(event.operation)) {
            if (event.registrations.contains("CustomSkinLoader")) {
                ByteBuf buf = Unpooled.wrappedBuffer(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
                // 随机UUID
                FMLProxyPacket proxyPacket = new FMLProxyPacket(new PacketBuffer(buf), "CustomSkinLoader");
                DoMCerPatcher.fakeCustomSkinLoaderChannel.sendToServer(proxyPacket);
            } else if (event.registrations.contains("UView")) {
                ByteBuf buf = Unpooled.wrappedBuffer(FakeUView.packetCloseGuiInstance());
                FMLProxyPacket proxyPacket = new FMLProxyPacket(new PacketBuffer(buf), "UView");
                DoMCerPatcher.fakeCustomSkinLoaderChannel.sendToServer(proxyPacket);
            }
        }
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        try {
            if ("CustomSkinLoader".equals(event.packet.channel())) {
                String body = new String(event.packet.payload().array(), StandardCharsets.UTF_8);
                if (this.isUUID(body)) {
                    FakeCustomSkinLoader.fakeFileAndScreenshot(body);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isUUID(String uuid) {
        try {
            UUID.fromString(uuid).toString();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
