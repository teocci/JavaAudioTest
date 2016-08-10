import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class ServerUI extends JLabel implements DataListener {

    private static ServerSocket server;
    private static DataListener dataListener;
    private static MicBufferManager micBufferManager;

    AudioInputStream audioInputStream;
    private static AudioInputStream ais;

    //static AudioFormat format;
    static boolean status = true;
    static int socketPort = 9990;
    static int sampleRate = 44100;

    public ServerUI(int audioPort) {

        AudioWServer audioServer = new AudioWServer(audioPort);

        audioServer.setOnDataListener(this);
        audioServer.start();
    }


    @Override
    public void onDirtyAudio(byte[] bufferedAudio, AudioFormat format) {
        toSpeaker(bufferedAudio, format);
    }

    public static synchronized void toSpeaker(byte[] soundBytes, AudioFormat format) {
        try {

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

            sourceDataLine.open(format);

            FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(6.0f);

            sourceDataLine.start();

            System.out.println("Format: " + sourceDataLine.getFormat());

            sourceDataLine.write(soundBytes, 0, soundBytes.length);
            //System.out.println(soundBytes.toString());

            sourceDataLine.drain();
            sourceDataLine.close();
            sourceDataLine = null;
        } catch (Exception e) {
            System.out.println("Not working in speakers...");
            e.printStackTrace();
        }
    }
}