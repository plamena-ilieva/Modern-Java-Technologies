package bg.sofia.uni.fmi.mjt.spotify.client;

import javax.sound.sampled.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Player extends Thread {
    private static final int BUFFER_SIZE = 4096;
    private static final int ENCODING_POSITION = 0;
    private static final int SAMPLE_RATE_POSITION = 1;
    private static final int SAMPLE_SIZE_POSITION = 2;
    private static final int CHANNELS_POSITION = 3;
    private static final int FRAME_SIZE_POSITION = 4;
    private static final int FRAME_RATE_POSITION = 5;
    private static final int ENDIAN_POSITION = 6;
    private static final String DONE = "Done";

    private final SocketChannel socketChannel;

    public Player(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            int bytesRead;
            byte[] byteBuffer = new byte[BUFFER_SIZE];
            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

            bytesRead = socketChannel.read(buffer);
            byte[] destination = new byte[bytesRead];
            buffer.flip();
            buffer.get(destination, 0, bytesRead);
            String contents = new String(destination);
            String[] arguments = contents.split("\\R");

            SourceDataLine audioLine = configureAudioLine(arguments);

            while ((bytesRead = socketChannel.read(buffer)) != -1) {
                buffer.flip();
                buffer.get(byteBuffer, 0, bytesRead);
                contents = new String(byteBuffer);
                if (contents.contains(DONE)) {
                    break;
                }

                audioLine.write(byteBuffer, 0, bytesRead);
                buffer.clear();
            }

            audioLine.drain();
            audioLine.close();

            System.out.println("Playing ended.");
        } catch (LineUnavailableException | IOException e) {
            System.out.println("Error while song streaming.");
        }
    }

    private SourceDataLine configureAudioLine(String[] arguments) throws LineUnavailableException {
        AudioFormat.Encoding encoding = new AudioFormat.Encoding(arguments[ENCODING_POSITION]);
        float sampleRate = Float.parseFloat(arguments[SAMPLE_RATE_POSITION]);
        int sampleSizeInBits = Integer.parseInt(arguments[SAMPLE_SIZE_POSITION]);
        int channels = Integer.parseInt(arguments[CHANNELS_POSITION]);
        int frameSize = Integer.parseInt(arguments[FRAME_SIZE_POSITION]);
        float frameRate = Float.parseFloat(arguments[FRAME_RATE_POSITION]);
        boolean bigEndian = Boolean.parseBoolean(arguments[ENDIAN_POSITION]);

        AudioFormat format = new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize,
                frameRate, bigEndian);

        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

        audioLine.open(format);
        audioLine.start();

        return audioLine;
    }
}