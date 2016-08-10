import javax.sound.sampled.AudioFormat;

/**
 * Created by teocci on 8/10/16.
 */
public interface DataListener {
    void onDirtyAudio(byte[] bufferedAudio, AudioFormat format);
}
