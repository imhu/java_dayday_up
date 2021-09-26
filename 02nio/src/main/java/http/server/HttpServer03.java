package http.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Description 线程池BIO实现
 */
public class HttpServer03 {

    private static final int PORT = 8803;

    // 发现线程数太少，吞吐量还没02高，应该和本地机器cpu性能有关
    private static final ExecutorService executor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors() * 4  // 本地12 * 4
    );

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("server started at http://127.0.0.1:" + PORT);
        while (true) {
            Socket accept = serverSocket.accept();
            executor.execute(() -> {
                try {
                    service(accept);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
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
