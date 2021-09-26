package http.client;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description okHttp实现http客户端
 * 支持类似sb 指定线程数量和执行持续时间
 */
public class OkHttpServerClient implements AutoCloseable {

    private final String url;
    private final OkHttpClient client;
    private final int threadCount;
    private final int seconds;

    private ExecutorService executorService;
    private final AtomicInteger requestCount = new AtomicInteger(0);

    public OkHttpServerClient(String url) {
        this(url, 1, 0);
    }

    public OkHttpServerClient(String url, int threadCount, int seconds) {
        this.url = url;
        this.threadCount = threadCount;
        this.seconds = seconds;
        this.client = buildClient();
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    private OkHttpClient buildClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        return okHttpClient;
    }

    public void request() {
        final long currentTime = System.currentTimeMillis();
        final long endTime = currentTime + seconds * 1000;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final Request request = new Request.Builder().url(url).method("get", null).build();
        try {
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    long _currentTime = currentTime;
                    while (_currentTime <= endTime) {
                        try {
                            Call call = client.newCall(request);
                            Response response = call.execute();
                            String body = response.body().string();
                            System.out.println("response = " + body);
                            requestCount.incrementAndGet();
                            response.close();
                            _currentTime = System.currentTimeMillis();
                        } catch (Exception e) {
                        }
                    }
                    latch.countDown();
                });
            }
            latch.await();
            if (seconds > 0) {
                System.out.println("Request count = " + requestCount.get());
                System.out.println(String.format("RPS = %.1f (requests/second)", (double) requestCount.get() / seconds * 1.0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        executorService.shutdownNow();
    }

    public static void main(String[] args) {
        final String url = "http://127.0.0.1:8803";
        try (OkHttpServerClient okHttpServerClient = new OkHttpServerClient(url, 2, 30)) {
            okHttpServerClient.request();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
