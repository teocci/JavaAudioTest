import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

/**
 * Created by teocci on 8/10/16.
 */
public class MicBuffer {

    private int totalLength = 0;
    private final int chunkLength;
    private ByteArrayOutputStream byteArrayOutputStream;

    public MicBuffer(int chunkLgth) {
        byteArrayOutputStream = new ByteArrayOutputStream();
        chunkLength = chunkLgth;

    }

    public int fillBuffer(byte[] data, int off, int len, LinkedList<byte[]> AudioQueue) {
        totalLength += len;
        byteArrayOutputStream.write(data, off, len);

        if (totalLength == chunkLength) {

            synchronized (AudioQueue) {
                AudioQueue.add(byteArrayOutputStream.toByteArray());
                byteArrayOutputStream.reset();
            }

            totalLength = 0;
            System.out.println("received file");
        }

        return 0;
    }
}