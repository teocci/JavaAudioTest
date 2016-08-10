import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.util.LinkedList;

/**
 * Created by teocci on 8/10/16.
 */
public class MicBufferManager  extends Thread {
    private MicBuffer[] bufferQueue;
    private static AudioFormat format;

    private int fillCount = 0;
    private final int chunkLength;
    private int remained = 0;
    private static final int MAX_BUFFER_COUNT = 2;
    private int channel, encoding, rate;
    private LinkedList<byte[]> AudioQueue = new LinkedList<>();
    private DataListener listener;

    public MicBufferManager(int chunkLgth, int ch, int enc, int sampleRate) {
        // TODO Auto-generated constructor stub
        channel = ch == 16 ? 1:2;
        encoding = enc == 2 ? 16:8;
        rate = sampleRate;
        chunkLength = chunkLgth;
        bufferQueue = new MicBuffer[MAX_BUFFER_COUNT];

        System.out.println(toString());

        format = new AudioFormat(rate, encoding, channel, true, false);

        for (int i = 0; i < MAX_BUFFER_COUNT; ++i) {
            bufferQueue[i] = new MicBuffer(chunkLength);
        }
    }

    public void fillBuffer(byte[] data, int len) {
        fillCount = fillCount % MAX_BUFFER_COUNT;
        if (remained != 0) {
            if (remained < len) {
                bufferQueue[fillCount].fillBuffer(data, 0, remained, AudioQueue);
                ++fillCount;
                if (fillCount == MAX_BUFFER_COUNT)
                    fillCount = 0;
                bufferQueue[fillCount].fillBuffer(data, remained, len - remained, AudioQueue);
                remained = chunkLength - len + remained;
            } else if (remained == len) {
                bufferQueue[fillCount].fillBuffer(data, 0, remained, AudioQueue);
                remained = 0;
                ++fillCount;
                if (fillCount == MAX_BUFFER_COUNT)
                    fillCount = 0;
            } else {
                bufferQueue[fillCount].fillBuffer(data, 0, len, AudioQueue);
                remained = remained - len;
            }
        } else {
            bufferQueue[fillCount].fillBuffer(data, 0, len, AudioQueue);

            if (len < chunkLength) {
                remained = chunkLength - len;
            } else {
                ++fillCount;
                if (fillCount == MAX_BUFFER_COUNT)
                    fillCount = 0;
            }
        }
    }

    public void setOnDataListener(DataListener dataListener) {
        listener = dataListener;
        start();
    }

    public void close() {
        interrupt();
        try {
            join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*public static void toSpeaker(byte[] soundBytes) {
        try {

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

            sourceDataLine.open(format);

            FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(6.0f);

            sourceDataLine.start();

            System.out.println("Format:" + sourceDataLine.getFormat());

            sourceDataLine.write(soundBytes, 0, soundBytes.length);
            //System.out.println(soundbytes.toString());

            sourceDataLine.drain();
            sourceDataLine.close();
            sourceDataLine = null;
        } catch (Exception e) {
            System.out.println("Not working in speakers...");
            e.printStackTrace();
        }
    }*/

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();

        while (!Thread.currentThread().isInterrupted()) {
            byte[] data;
            synchronized (AudioQueue) {
                data = AudioQueue.poll();

                if (data != null) {
                    long t = System.currentTimeMillis();

                    //toSpeaker(data);

                    listener.onDirtyAudio(data, format);
                    System.out.println("time cost = " + (System.currentTimeMillis() - t));
                }
            }
        }
    }

    public String toString()
    {
        return "Format:PCM_SIGNED " + rate + ".0 Hz, " + encoding + " bit, " + (channel == 1 ?
                "mono" : "stereo");
    }
}