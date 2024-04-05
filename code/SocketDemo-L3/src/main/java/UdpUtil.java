import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UdpUtil {
    public static final int UDP_PROVIDER_PORT = 20000;

    private UdpUtil() {}

    public static void close(Closeable ds) throws IOException {
        if (ds != null) {
            ds.close();
        }
    }

    public static DatagramPacket fetchPack(DatagramSocket ds) throws IOException {
        // 构建接收实体
        final byte[] buf = new byte[512];
        DatagramPacket receivePack = new DatagramPacket(buf, buf.length);

        // 接收
        ds.receive(receivePack);
        return receivePack;
    }

    public static int parsePackagePort(DatagramPacket receivePack) {
        // 打印接收到的信息与发送者的信息
        // 发送者的IP地址
        String ip = receivePack.getAddress().getHostAddress();
        int port = receivePack.getPort();
        int dataLen = receivePack.getLength();
        String data = new String(receivePack.getData(), 0, dataLen);
        System.out.println("UDPProvider receive form ip:" + ip
                + "\tport:" + port + "\tdata:" + data);

        // 解析端口号
        int responsePort = MessageCreator.parsePort(data);
        return responsePort;
    }

    public static DatagramPacket buildResponsePacket(String sn, InetAddress address, int responsePort) {
        // 构建一份回送数据
        String responseData = MessageCreator.buildWithSn(sn);
        byte[] responseDataBytes = responseData.getBytes();
        // 直接根据发送者构建一份回送信息
        return new DatagramPacket(responseDataBytes,
                responseDataBytes.length,
                address,
                responsePort);
    }

    public static DatagramPacket buildBroadcastPacket(String broadcastName, int udpProviderPort, int listenPort) throws UnknownHostException {
        // 构建一份请求数据
        String requestData = MessageCreator.buildWithPort(listenPort);
        byte[] requestDataBytes = requestData.getBytes();
        // 直接构建packet
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes,
                requestDataBytes.length);
        // 20000端口, 广播地址
        requestPacket.setAddress(InetAddress.getByName(broadcastName));
        requestPacket.setPort(udpProviderPort);
        return requestPacket;
    }

    public static Device parseDevice(DatagramPacket receivePack) {
        // 发送者的IP地址
        String ip = receivePack.getAddress().getHostAddress();
        int port = receivePack.getPort();
        int dataLen = receivePack.getLength();
        String data = new String(receivePack.getData(), 0, dataLen);
        System.out.println("UDPSearcher receive form ip:" + ip
                + "\tport:" + port + "\tdata:" + data);

        String sn = MessageCreator.parseSn(data);
        if (sn != null) {
            return new Device(port, ip, sn);
        }

        return null;
    }
}
