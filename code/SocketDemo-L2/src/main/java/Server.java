import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket服务器端套接字基本Demo
 * 1. 创建服务端socket对象，使用端口号2000，然后打印两行就绪状态信息，阻塞等待，直到有客户端连接。
 * 2. 循环等待客户端连接后，以socket对象作为参数，构造客户端Handler异步线程对象，并启动之。
 *    a) 客户端Handler线程从socket逐行读入流，根据读入内容为"bye"时回送原文给客户端，并结束运行，否则
 *    b) 回送读到字符串长度给客户端后等待接收下一行新数据流。
 */
public class Server {
    private static final int PORT = 2000;

    // 指定端口号初始化服务端Socket后，一直监听并处理客户端的连接请求。
    public static void main(String[] args) throws IOException {
        try (ServerSocket server = SocketUtil.initSocketServer(PORT)) {
            for ( ; ; ) {
                listeningForClient(server);
            }
        }
    }

    // 监听接收客户端的连接请求，并运行独立的handler线程响应每个客户端请求。
    private static void listeningForClient(ServerSocket server) throws IOException {
        // 得到客户端
        Socket client = server.accept();
        // 客户端构建异步线程
        ClientHandler clientHandler = new ClientHandler(client);
        // 启动线程
        clientHandler.start();
    }

    /**
     * 客户端消息处理，每个客户端运行在一个独立的线程中。
     */
    private static class ClientHandler extends Thread {
        private Socket socket;
        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接：" + socket.getInetAddress() +
                    " P:" + socket.getPort());

            // 得到打印流，用于数据输出；服务器回送数据使用
            // 得到输入流，用于接收数据
            try (PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                 BufferedReader socketInput = new BufferedReader(new InputStreamReader(
                         socket.getInputStream()))) {
                SocketUtil.serverResponseLoop(socketInput, socketOutput);
            } catch (Exception e) {
                System.out.println("连接异常断开");
            } finally {
                // 连接关闭
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("客户端已退出：" + socket.getInetAddress() +
                    " P:" + socket.getPort());

        }
    }
}
