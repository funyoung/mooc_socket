import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
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
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        // 超时时间
        socket.setSoTimeout(3000);

        // 连接本地，端口2000；超时时间3000ms
        // 重要提示：如果服务器未先执行，则会抛出异常
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 2000), 3000);

        System.out.println("已发起服务器连接，并进入后续流程。输入任意字符[结束输入: bye]～");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort());

        try {
            // 发送接收数据
            todo(socket);
        } catch (Exception e) {
            System.out.println("异常关闭");
        }

        // 释放资源
        socket.close();
        System.out.println("客户端已退出～");

    }

    private static void todo(Socket client) throws IOException {
        // 构建键盘输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));


        // 得到Socket输出流，并转换为打印流
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);


        // 得到Socket输入流，并转换为BufferedReader
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream));

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

        // 资源释放
        socketPrintStream.close();
        socketBufferedReader.close();

    }


}
