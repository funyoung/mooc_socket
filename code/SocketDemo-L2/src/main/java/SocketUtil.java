import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;

public class SocketUtil {
    private static final String TOKEN_EXIT = "bye";

    private SocketUtil() {}

    public static ServerSocket initSocketServer(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);

        System.out.println("服务器准备就绪～");
        System.out.println("服务器信息：" + server.getInetAddress() + " P:" + server.getLocalPort());

        return server;
    }

    // 循环从输入中读取字符串行，直到内容为TOKEN_EXIT时，结束返回。若读到其他字符串时，回送读到的字符串长度信息给输出流。
    public static void serverResponseLoop(BufferedReader socketInput, PrintStream socketOutput) throws IOException {
        boolean flag = true;
        do {
            // 客户端拿到一条数据
            String str = socketInput.readLine();
            if (TOKEN_EXIT.equalsIgnoreCase(str)) {
                flag = false;
                // 回送
                socketOutput.println(TOKEN_EXIT);
            } else {
                // 打印到屏幕。并回送数据长度
                System.out.println(str);
                socketOutput.println("回送：" + str.length());
            }
        } while (flag);
    }

    public static Socket initClientSocket(int serverPort, int connectTimeout, int socketTimeout) throws IOException {
        Socket socket = new Socket();
        // 超时时间
        socket.setSoTimeout(socketTimeout);

        // 连接本地，端口2000；超时时间3000ms
        // 重要提示：如果服务器未先执行，则会抛出异常
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), serverPort), connectTimeout);

        System.out.println("已发起服务器连接，并进入后续流程。输入任意字符[结束输入: bye]～");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort());

        return socket;
    }

    public static void clientRequestLoop(BufferedReader input, BufferedReader socketBufferedReader, PrintStream socketPrintStream) throws IOException {
        boolean flag = true;
        do {
            // 键盘读取一行
            String str = input.readLine();
            // 发送到服务器
            socketPrintStream.println(str);


            // 从服务器读取一行
            String echo = socketBufferedReader.readLine();
            if ("bye".equalsIgnoreCase(echo)) {
                flag = false;
            }else {
                System.out.println(echo);
            }
        }while (flag);
    }
}