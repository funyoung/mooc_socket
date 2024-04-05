import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Socket客户度端套接字基本Demo
 * 1. 创建客户端socket对象，设置超时时间间隔为3000毫秒，然后连接服务器2000端口且以3000毫秒作为连接超时时限，
 * 并打印链接就绪状态信息和客户端，服务器端的地址/端口信息。
 * 2. 客户端交互
 *    a) 从键盘读取输入字符串，通过socket发给服务器，并从socket读出服务器的回送字符串。
 *    b) 根据从socket读入的内容为"bye"时，结束运行，否则
 *    c) 打印服务器回送的数据。
 * 3. 关闭socket并打印退出提示
 */
public class Client {
    private static final int SERVER_PORT = 2000;
    private static final int CONNECT_TIMEOUT = 3000;
    private static final int SOCKET_TIMEOUT = 3000;

    public static void main(String[] args) {
        // 发送接收数据
        // 构建键盘输入流
        // 得到Socket输出流，并转换为打印流
        // 得到Socket输入流，并转换为BufferedReader
        InputStream in = System.in;
        try (BufferedReader input = new BufferedReader(new InputStreamReader(in));
             Socket socket = SocketUtil.initClientSocket(SERVER_PORT, CONNECT_TIMEOUT, SOCKET_TIMEOUT);
             PrintStream socketPrintStream = new PrintStream(socket.getOutputStream());
             BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            SocketUtil.clientRequestLoop(input, socketBufferedReader, socketPrintStream);
        } catch (Exception e) {
            System.out.println("异常关闭: " + e.getMessage());
        }
        System.out.println("客户端已退出～");

    }
}
