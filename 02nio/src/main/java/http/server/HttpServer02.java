package http.server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @Description 每个请求创建一个线程BIO实现
 * 注意：
 *  1.创建和销毁线程的开销
 *  2.线程上下文切换的开销
 *  3.fd文件描述符数量的限制 - 大量请求进来，如果业务处理比较久的话（httpserver03同样有这个问题）
 */
public class HttpServer02 {

    private static final int PORT = 8802;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("server started at http://127.0.0.1:" + PORT);
        while (true) {
            Socket accept = serverSocket.accept();
            new Thread(() -> {
                try {
                    service(accept);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private static void service(Socket socket) throws Exception {
        // TimeUnit.MILLISECONDS.sleep(20);
        final String body = "hello";
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        printWriter.println("HTTP/1.1 200 OK");
        printWriter.println("Content-Type:text/html;charset=UTF-8");
        printWriter.println("Content-Length:" + body.getBytes().length);
        printWriter.println();
        printWriter.print(body);
        printWriter.close();
        socket.close();
    }

}
