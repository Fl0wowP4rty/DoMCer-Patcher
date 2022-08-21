package top.flowowparty.domcer.fake;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import top.flowowparty.domcer.DoMCerPatcher;
import top.flowowparty.domcer.utils.HttpUtils;

public class FakeCustomSkinLoader {

    private static Executor executor = Executors.newCachedThreadPool();

    /**
     * 目前的发送图片仅为发送固定图片至DoMCer服务器
     * 您可以修改此方法实现在截图时自动隐藏GUI
     *
     * @author ImFl0wow
     */
    public static void fakeFileAndScreenshot(String id) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            executor.execute(() -> {
                try {
                    byte[] bytes = IOUtils.toByteArray(Objects.requireNonNull(FakeCustomSkinLoader.class.getResourceAsStream("/NICEPIC.jpg")));
                    /*
                     读取NICEPIC.jpg
                     请注意，该文件格式一定为jpg
                    */
                    String url = "https://upload.server.domcer.com:25566/uploadJpg?key=0949a0d0-bc98-4535-9f5e-086835123f75&type=" + id;
                    HashMap<String, byte[]> map = new HashMap<String, byte[]>();
                    map.put("file", bytes);
                    map.put("check", IOUtils.toByteArray(Objects.requireNonNull(FakeCustomSkinLoader.class.getResourceAsStream("/fileCheck.txt"))));
                    /*
                     读取md5文件
                     @see README.md
                    */
                    HttpUtils.HttpResponse response = HttpUtils.postFormData(url, map, null, null);
                    String result = response.getContent();
                    ByteBuf buf = Unpooled.wrappedBuffer((id + ":" + ((JsonObject) new Gson().fromJson(result, JsonObject.class)).get("data").getAsString()).getBytes());
                    FMLProxyPacket proxyPacket = new FMLProxyPacket(new PacketBuffer(buf), "CustomSkinLoader");
                    DoMCerPatcher.fakeCustomSkinLoaderChannel.sendToServer(proxyPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
