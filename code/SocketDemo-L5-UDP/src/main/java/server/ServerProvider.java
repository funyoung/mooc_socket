package server;

import clink.net.qiujuer.clink.utils.ByteUtils;
import constants.UDPConstants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.UUID;

class ServerProvider {
    private static Provider PROVIDER_INSTANCE;

    static void start(int port) {
        stop();
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn, port);
        provider.start();
        PROVIDER_INSTANCE = provider;
    }

    static void stop() {
        if (PROVIDER_INSTANCE != null) {
            PROVIDER_INSTANCE.exit();
            PROVIDER_INSTANCE = null;
        }
    }

    private static class Provider extends Thread {
        private final byte[] sn;
        private final int port;
        private boolean done = false;
        private DatagramSocket ds = null;
        // 存储消息的Buffer
        final byte[] buffer = new byte[128];

        Provider(String sn, int port) {
            super();
            this.sn = sn.getBytes();
            this.port = port;
        }

        @Override
        public void run() {
            super.run();

            System.out.println("UDPProvider Started.");

            try {
                // 监听端口
                ds = new DatagramSocket(UDPConstants.PORT_SERVER);
                // 接收消息的Packet
                DatagramPacket receivePack = new DatagramPacket(buffer, buffer.length);

                while (!done) {

                    // 接收
                    ds.receive(receivePack);

                    DatagramPacket responsePacket = processPacket(receivePack);
                    if (null != responsePacket) {
                        ds.send(responsePacket);
                    }
                }
            } catch (Exception ignored) {
            } finally {
                close();
            }

            // 完成
            System.out.println("UDPProvider Finished.");
        }

        // 解析收到的有效数据包则创建回包给扫描搜素的客户端，否则返回空，调用的线程继续执行监听
        private DatagramPacket processPacket(DatagramPacket receivePack) {
            // 打印接收到的信息与发送者的信息
            // 发送者的IP地址
            String clientIp = receivePack.getAddress().getHostAddress();
            int clientPort = receivePack.getPort();
            int clientDataLen = receivePack.getLength();
            byte[] clientData = receivePack.getData();
            boolean isValid = clientDataLen >= (UDPConstants.HEADER.length + 2 + 4)
                    && ByteUtils.startsWith(clientData, UDPConstants.HEADER);

            System.out.println("ServerProvider receive form ip:" + clientIp
                    + "\tport:" + clientPort + "\tdataValid:" + isValid);

            if (isValid) {
                // 无效继续
                return createResponsePacket(clientIp, receivePack.getAddress(), clientData);
            }

            return null;
        }

        private DatagramPacket createResponsePacket(String clientIp, InetAddress address, byte[] clientData) {
            // 解析命令与回送端口
            int index = UDPConstants.HEADER.length;
            short cmd = (short) ((clientData[index++] << 8) | (clientData[index++] & 0xff));
            int responsePort = (((clientData[index++]) << 24) |
                    ((clientData[index++] & 0xff) << 16) |
                    ((clientData[index++] & 0xff) << 8) |
                    ((clientData[index] & 0xff)));

            // 判断合法性
            if (cmd == 1 && responsePort > 0) {
                // 构建一份回送数据
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                byteBuffer.put(UDPConstants.HEADER);
                byteBuffer.putShort((short) 2);
                byteBuffer.putInt(port);
                byteBuffer.put(sn);
                int len = byteBuffer.position();
                // 直接根据发送者构建一份回送信息
                DatagramPacket responsePacket = new DatagramPacket(buffer,
                        len,
                        address,
                        responsePort);
                System.out.println("ServerProvider response to:" + clientIp + "\tport:" + responsePort + "\tdataLen:" + len);

                return responsePacket;
            } else {
                System.out.println("ServerProvider receive cmd nonsupport; cmd:" + cmd + "\tport:" + port);
                return null;
            }
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
