package jvm.loader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * @Description xlass类加载器
 */
public class XlassLoader extends ClassLoader {

    private static final int BASE = 0xFF; // 255
    private static final String SUFFIX = ".xlass"; // 后缀

    public static void main(String[] args) throws Exception {
        XlassLoader xlassLoader = new XlassLoader();
        // 调用loadClass来加载类
        Class<?> clazz = xlassLoader.loadClass("Hello");
        // 创建Hello实例
        Object hello = clazz.newInstance();
        // 反射调用hello方法
        Method helloMethod = clazz.getDeclaredMethod("hello");
        helloMethod.invoke(hello);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 加载xlass文件
        byte[] bytes = loadClassByte(name);
        // 解码
        bytes = decode(bytes);
        Class<?> result = defineClass(name, bytes, 0, bytes.length);
        if (null == result) {
            throw new ClassNotFoundException(name);
        }
        return result;
    }

    /**
     * 加载Xlass字节码文件
     *
     * @return
     */
    private byte[] loadClassByte(String name) throws ClassNotFoundException {
        String path = name.replace(".", "/").concat(SUFFIX);
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
        try {
            int length = in.available();
            byte[] bytes = new byte[length];
            in.read(bytes);
            return bytes;
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Xlass字节码文件解码
     *
     * @param encodeBytes
     * @return
     */
    private byte[] decode(byte[] encodeBytes) {
        for (int i = 0; i < encodeBytes.length; i++) {
            encodeBytes[i] = (byte) (BASE - encodeBytes[i]);
        }
        return encodeBytes;
    }
}
