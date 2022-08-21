package top.flowowparty.domcer.fake;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.SerializationException;
import top.flowowparty.domcer.utils.StreamUtils;

public class FakeUView
{
    /**
     * 在UView进入世界时会自动发送该命令
     * DoMCer以此来判断是否安装UView
     *
     * @author ImFl0wow
     */
    public static byte[] packetCloseGuiInstance()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        try
        {
            StreamUtils.writeString(out, "PacketCloseGui");
            return out.toByteArray();
        }
        catch (IOException ex)
        {
            throw new SerializationException(ex.getMessage(), ex);
        }
    }
}
