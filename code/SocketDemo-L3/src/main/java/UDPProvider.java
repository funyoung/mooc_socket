import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

/**
 * UDP 提供者，用于提供服务
 */
public class UDPProvider {
    public static void main(String[] args) {
        // 生成一份唯一标示
        Provider.runWith(UUID.randomUUID().toString());
    }

    private static class Provider extends Thread {
        private final String sn;
        private boolean done = false;
        private DatagramSocket ds = null;

        public Provider(String sn) {
            super();
            this.sn = sn;
        }

        public static void runWith(String sn) {
            Provider provider = new Provider(sn);
            provider.start();

            // 读取任意键盘信息后可以退出
            // noinspection ResultOfMethodCallIgnored
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("UDPProvider read std input stream exception: " + e.getMessage());
                // TODO: continue to exit or loop back to next read?
            }
            provider.exit();
        }

        @Override
        public void run() {
            super.run();

            System.out.println("UDPProvider Started.");

            try {
                // UDP监听端口
                ds = new DatagramSocket(UdpUtil.UDP_PROVIDER_PORT);

                while (!done) {
                    DatagramPacket receivePack = UdpUtil.fetchPack(ds);

                    int responsePort = UdpUtil.parsePackagePort(receivePack);
                    
                    if (responsePort != -1) {
                        DatagramPacket packet = UdpUtil.buildResponsePacket(sn, receivePack.getAddress(), responsePort);
                        ds.send(packet);
                    } else {
                        System.out.println("UDPProvider invalid port: " + responsePort);
                    }
                }

            } catch (Exception ignored) {
            } finally {
                close();
            }

            // 完成
            System.out.println("UDPProvider Finished.");
        }


        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        /**
         * 提供结束
         */
        void exit() {
            done = true;
            close();
        }

    }

}
