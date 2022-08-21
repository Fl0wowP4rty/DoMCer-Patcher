package top.flowowparty.domcer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author DoMCer
 */
public class StreamUtils {
    public static void writeString(OutputStream out, String str) throws IOException {
        if (str == null) {
            StreamUtils.writeInt(out, -1);
        } else if (str.isEmpty()) {
            StreamUtils.writeInt(out, 0);
        } else {
            byte[] bs = str.getBytes(StandardCharsets.UTF_8);
            StreamUtils.writeInt(out, bs.length);
            out.write(bs);
        }
    }

    public static String readString(InputStream in) throws IOException {
        int length = StreamUtils.readInt(in);
        if (length < 0) {
            return null;
        }
        if (length == 0) {
            return "";
        }
        byte[] value = new byte[length];
        in.read(value);
        return new String(value, StandardCharsets.UTF_8);
    }

    public static void writeInt(OutputStream out, int i) throws IOException {
        out.write((byte) (i >> 24));
        out.write((byte) (i >> 16));
        out.write((byte) (i >> 8));
        out.write(i);
    }

    public static int readInt(InputStream in) throws IOException {
        return ((byte) in.read() & 0xFF) << 24 | ((byte) in.read() & 0xFF) << 16 | ((byte) in.read() & 0xFF) << 8 | (byte) in.read() & 0xFF;
    }

    public static void writeByteArray(OutputStream out, byte[] bytes) throws IOException {
        StreamUtils.writeInt(out, bytes.length);
        out.write(bytes);
    }

    public static byte[] readByteArray(InputStream in) throws IOException {
        byte[] bytes = new byte[StreamUtils.readInt(in)];
        in.read(bytes);
        return bytes;
    }

    public static byte[] readBytes(InputStream in) throws IOException {
        byte[] temp = new byte[in.available()];
        byte[] result = new byte[]{};
        int size = 0;
        while ((size = in.read(temp)) != -1) {
            byte[] readBytes = new byte[size];
            System.arraycopy(temp, 0, readBytes, 0, size);
            result = StreamUtils.mergeArray(result, readBytes);
        }
        return result;
    }

    public static byte[] mergeArray(byte[]... a) {
        int index = 0;
        int sum = 0;
        for (int i = 0; i < a.length; ++i) {
            sum += a[i].length;
        }
        byte[] result = new byte[sum];
        for (int i = 0; i < a.length; ++i) {
            int lengthOne = a[i].length;
            if (lengthOne == 0) continue;
            System.arraycopy(a[i], 0, result, index, lengthOne);
            index += lengthOne;
        }
        return result;
    }
}
