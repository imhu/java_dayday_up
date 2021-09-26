package http.server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @Description 单线程BIO实现
 */
public class HttpServer01 {

    private static final int PORT = 8801;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("server started at http://127.0.0.1:" + PORT);
        while (true) {
            Socket accept = serverSocket.accept();
            service(accept);
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
