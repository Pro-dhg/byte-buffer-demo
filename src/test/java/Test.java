import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author duanRui
 * @date 2024/3/14 20:11
 * 小段要每天快乐呀
 * @description:
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        buffer.put("hello world".getBytes(StandardCharsets.UTF_8));

        System.out.println(buffer.position());
        buffer.clear();

        buffer.put("hello world".getBytes(StandardCharsets.UTF_8));

        System.out.println(buffer.position());
        buffer.clear();

        buffer.put("hello world".getBytes(StandardCharsets.UTF_8));

        System.out.println(buffer.position());
        buffer.clear();

        long start = System.currentTimeMillis();

        Thread.sleep(1000L);

        long end = System.currentTimeMillis();

        System.out.println(end-start);
    }
}
