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
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(2000);


        System.out.println("服务器准备就绪～");
        System.out.println("服务器信息：" + server.getInetAddress() + " P:" + server.getLocalPort());


        // 等待客户端连接
        for (; ; ) {
            // 得到客户端
            Socket client = server.accept();
            // 客户端构建异步线程
            ClientHandler clientHandler = new ClientHandler(client);
            // 启动线程
            clientHandler.start();
        }

    }

    /**
     * 客户端消息处理
     */
    private static class ClientHandler extends Thread {
        private Socket socket;
        private boolean flag = true;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接：" + socket.getInetAddress() +
                    " P:" + socket.getPort());

            try {
                // 得到打印流，用于数据输出；服务器回送数据使用
                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                // 得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));

                do {
                    // 客户端拿到一条数据
                    String str = socketInput.readLine();
                    if ("bye".equalsIgnoreCase(str)) {
                        flag = false;
                        // 回送
                        socketOutput.println("bye");
                    } else {
                        // 打印到屏幕。并回送数据长度
                        System.out.println(str);
                        socketOutput.println("回送：" + str.length());
                    }

                } while (flag);

                socketInput.close();
                socketOutput.close();

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
