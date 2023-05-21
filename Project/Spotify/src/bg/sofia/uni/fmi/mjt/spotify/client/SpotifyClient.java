package bg.sofia.uni.fmi.mjt.spotify.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SpotifyClient {
    private static final String PLAY_COMMAND = "play";
    private static final String STOP_COMMAND = "stop";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String LOCALHOST = "localhost";
    private static final String ADDRESS = "localhost";

    private static final String MENU = """
            register <email> <password>
            login <email> <password>
            disconnect
            search <words>
            top <number>
            create-playlist <name_of_the_playlist>
            add-song-to <name_of_the_playlist> <song>
            show-playlist <name_of_the_playlist>
            play <song>
            stop""";
     private static final int SERVER_PORT = 7777;

    public static void main(String[] args) {
        displayMenu();

        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer =
                     new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(ADDRESS, SERVER_PORT));
            Player mediaPlayer = null;

            while (true) {
                String request = scanner.nextLine();

                if (mediaPlayer != null && mediaPlayer.isAlive()) {
                    if (request.equals(STOP_COMMAND)) {
                        writer.println(request);
                        mediaPlayer.join();
                    } else {
                        System.out.println("You have to stop playing first.");
                    }
                } else {
                    if (request.startsWith(PLAY_COMMAND)) {
                        writer.println(request);
                        if (isResponseReceived(reader)) {
                            try {
                                mediaPlayer = new Player(socketChannel);
                                mediaPlayer.start();
                            } catch (Exception e) {
                                System.out.println("Error while playing.");
                            }
                        }
                    } else if (request.startsWith(STOP_COMMAND)) {
                        System.out.println("No song is playing.");
                    } else {
                        writer.println(request);
                        //isResponseReceived(reader);
                        String response = reader.readLine();
                        System.out.println(response);
                        if (request.equals(DISCONNECT_COMMAND)) {
                            break;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            // no action required
        } catch (IOException e) {
            System.out.println("Error with the network communication");
            //e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unknown error");
        }
    }

    private static boolean isResponseReceived(BufferedReader reader) throws IOException {
        String response;
        do {
        //while ((response = reader.readLine()) != null)
            response = reader.readLine();
            System.out.println(response);
        } while (reader.ready());

        return response != null && !response.startsWith("Error");
    }

    private static void displayMenu() {
        System.out.println("Welcome to Spotify! Here are your options: " + System.lineSeparator()
                + MENU + System.lineSeparator()
                + "Press Enter key to continue...");

        new Scanner(System.in).nextLine();
    }
}