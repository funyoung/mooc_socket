import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * UDP 搜索者，用于搜索服务支持方
 */
public class UDPSearcher {
    private static final String BROADCAST_NAME = "255.255.255.255";
    private static final int LISTEN_PORT = 30000;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("UDPSearcher Started.");

        Listener listener = listen();
        sendBroadcast();

        // 读取任意键盘信息后可以退出
        System.out.println("\n【广播发送完毕，下面实时刷新收到的回包信息】按下回车键打印全部搜索结果并退出...\n");
        //noinspection ResultOfMethodCallIgnored
        System.in.read();

        List<Device> devices = listener.getDevicesAndClose();

        for (Device device : devices) {
            System.out.println("Device:" + device.toString());
        }

        // 完成
        System.out.println("UDPSearcher Finished.");
    }

    // 监听端口号LISTEN_PORT，并使用CountDownLatch等待监听任务线程执行完成后再返回。
    private static Listener listen() throws InterruptedException {
        System.out.println("UDPSearcher start listen.");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, countDownLatch);
        listener.start();

        countDownLatch.await();
        return listener;
    }

    private static void sendBroadcast() throws IOException {
        System.out.println("UDPSearcher sendBroadcast started.");

        // 作为搜索方，让系统自动分配端口, 创建广播数据报文后，发送出去。
        try(DatagramSocket ds = new DatagramSocket()) {
            DatagramPacket requestPacket = UdpUtil.buildBroadcastPacket(BROADCAST_NAME, UdpUtil.UDP_PROVIDER_PORT, LISTEN_PORT);
            ds.send(requestPacket);
        }

        // 完成
        System.out.println("UDPSearcher sendBroadcast finished.");
    }

    private static class Listener extends Thread {
        private final int listenPort;
        private final CountDownLatch countDownLatch;
        private final List<Device> devices = new ArrayList<>();
        private boolean done = false;
        private DatagramSocket ds = null;


        public Listener(int listenPort, CountDownLatch countDownLatch) {
            super();
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            super.run();

            // 通知已启动
            countDownLatch.countDown();
            try {
                // 监听回送端口
                ds = new DatagramSocket(listenPort);

                while (!done) {
                    // 构建接收实体
                    DatagramPacket receivePack = UdpUtil.fetchPack(ds);

                    // 打印接收到的信息与发送者的信息
                    Device device = UdpUtil.parseDevice(receivePack);
                    if (null != device) {
                        devices.add(device);
                    }
                }
            } catch (Exception ignored) {

            } finally {
                close();
            }
            System.out.println("UDPSearcher listener finished.");

        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        List<Device> getDevicesAndClose() {
            done = true;
            close();
            return devices;
        }
    }
}
