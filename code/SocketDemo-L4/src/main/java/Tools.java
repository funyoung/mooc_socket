import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Tools {
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static void response(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[256];
        int readCount = inputStream.read(buffer);
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, readCount);

        // byte
        byte be = byteBuffer.get();

        // char
        char c = byteBuffer.getChar();

        // int
        int i = byteBuffer.getInt();

        // bool
        boolean b = byteBuffer.get() == 1;

        // Long
        long l = byteBuffer.getLong();

        // float
        float f = byteBuffer.getFloat();

        // double
        double d = byteBuffer.getDouble();

        // String
        int pos = byteBuffer.position();
        String str = new String(buffer, pos, readCount - pos - 1);

        System.out.println("收到数量：" + readCount + " 数据："
                + be + "\n"
                + c + "\n"
                + i + "\n"
                + b + "\n"
                + l + "\n"
                + f + "\n"
                + d + "\n"
                + str + "\n");

        outputStream.write(buffer, 0, readCount);
    }

    public static void close(Closeable socket) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
