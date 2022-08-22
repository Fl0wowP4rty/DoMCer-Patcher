package top.flowowparty.domcer.fake;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import top.flowowparty.domcer.DoMCerPatcher;
import top.flowowparty.domcer.utils.HttpUtils;

import javax.imageio.ImageIO;

public class FakeCustomSkinLoader {

    public enum Type {
        LOCAL, RUNTIME;
    }

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
                DoMCerPatcher.beforeScreenshot();
                try {
                    byte[] bytes = getJPG(Type.RUNTIME);
                    // Local: 读取本地图片
                    // Runtime: 实时截图
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
                DoMCerPatcher.afterScreenshot();
            });
        });
    }

    private static IntBuffer pixelBuffer;
    private static int[] pixelValues;

    private static byte[] getJPG(Type type) throws IOException {
        switch (type) {
            case LOCAL:
                return IOUtils.toByteArray(Objects.requireNonNull(FakeCustomSkinLoader.class.getResourceAsStream("/NICEPIC.jpg")));
            // 读取NICEPIC.jpg
            // 请注意，该文件格式一定为jpg
            case RUNTIME:
                Framebuffer buffer = Minecraft.getMinecraft().getFramebuffer();
                int width = Minecraft.getMinecraft().displayWidth;
                int height = Minecraft.getMinecraft().displayHeight;
                if (OpenGlHelper.isFramebufferEnabled()) {
                    width = buffer.framebufferTextureWidth;
                    height = buffer.framebufferTextureHeight;
                }
                int i = width * height;
                if (pixelBuffer == null || pixelBuffer.capacity() < i) {
                    pixelBuffer = BufferUtils.createIntBuffer(i);
                    pixelValues = new int[i];
                }
                GL11.glPixelStorei(3333, 1);
                GL11.glPixelStorei(3317, 1);
                pixelBuffer.clear();
                if (OpenGlHelper.isFramebufferEnabled()) {
                    GlStateManager.bindTexture(buffer.framebufferTexture);
                    GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
                } else {
                    GL11.glReadPixels(0, 0, width, height, 32993, 33639, pixelBuffer);
                }
                pixelBuffer.get(pixelValues);
                TextureUtil.processPixelValues(pixelValues, width, height);
                BufferedImage bufferedimage = null;
                if (OpenGlHelper.isFramebufferEnabled()) {
                    int j;
                    bufferedimage = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
                    for (int k = j = buffer.framebufferTextureHeight - buffer.framebufferHeight; k < buffer.framebufferTextureHeight; ++k) {
                        for (int l = 0; l < buffer.framebufferWidth; ++l) {
                            bufferedimage.setRGB(l, k - j, pixelValues[k * buffer.framebufferTextureWidth + l]);
                        }
                    }
                } else {
                    bufferedimage = new BufferedImage(width, height, 1);
                    bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
                }
                BufferedImage finalBufferedimage = bufferedimage;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                boolean foundWriter = ImageIO.write((RenderedImage) finalBufferedimage, "jpg", baos);
                assert (foundWriter);
                return baos.toByteArray();
        }
        throw new UnexpectedException("Unknown Type");
    }
}
