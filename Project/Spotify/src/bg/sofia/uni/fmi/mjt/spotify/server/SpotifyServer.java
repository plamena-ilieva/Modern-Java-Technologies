package bg.sofia.uni.fmi.mjt.spotify.server;

import bg.sofia.uni.fmi.mjt.spotify.Spotify;
import bg.sofia.uni.fmi.mjt.spotify.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.spotify.command.CommandExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
public class SpotifyServer {
    private static final String STOP_COMMAND = "stop";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String SERVER_HOST = "localhost";
    private static final int PORT = 7777;
    private static final int BUFFER_SIZE = 1024;
    private final int port;
    private boolean isOperating;
    private Selector selector;
    private ByteBuffer buffer;
    private CommandExecutor commandExecutor;

    public SpotifyServer(int port, CommandExecutor commandExecutor) {
        this.port = port;

        this.commandExecutor = commandExecutor;
    }

    public void start() {

        try (ServerSocketChannel server = ServerSocketChannel.open()) {

            this.selector = Selector.open();

            server.bind(new InetSocketAddress(SERVER_HOST, this.port));
            server.configureBlocking(false);

            server.register(this.selector, SelectionKey.OP_ACCEPT);

            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            this.isOperating = true;

            while (this.isOperating) {
                int readyChannels = this.selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator = selectedKeys.iterator();

                while (selectionKeyIterator.hasNext()) {
                    SelectionKey key = selectionKeyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        buffer.clear();
                        int bytesRead = sc.read(buffer);
                        if (bytesRead < 0) {
                            sc.close();
                            break;
                        }

                        buffer.flip();
                        byte[] destination = new byte[buffer.remaining()];
                        buffer.get(destination);
                        String message = new String(destination, StandardCharsets.UTF_8);

                        String response = commandExecutor.execute(key, CommandCreator.newCommand(message));

                        System.out.println(message + ":\t" + response);
                        // sending a stop response to a client is pointless and can fail future song streaming
                        if (!message.startsWith(STOP_COMMAND)) {
                            sendResponse(buffer, sc, response);
                            //sc.close();
                        }

                        if (message.startsWith(DISCONNECT_COMMAND)) {
                            sc.close();
                        }
                    } else if (key.isAcceptable()) {
                        accept(key);
                    }

                    selectionKeyIterator.remove();
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendResponse(ByteBuffer buffer, SocketChannel sc, String response) throws IOException {
        buffer.clear();
        buffer.put(response.getBytes());
        buffer.flip();
        sc.write(buffer);
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    public void stop() {
        isOperating = false;
        selector.wakeup();
    }

    public static void main(String[] args) {
        Spotify spotify = new Spotify();
        CommandExecutor executor = new CommandExecutor(spotify);
        SpotifyServer spotifyServer = new SpotifyServer(PORT, executor);
        spotifyServer.start();

        /*SpotifyServer spotifyServer = new SpotifyServer(PORT);
        spotifyServer.start();*/
    }
}
/*public class SpotifyServer {
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";
    private static final int PORT = 7777;

    private static final String STOP = "stop";
    private static final String DISCONNECT = "disconnect";
    private final CommandExecutor commandExecutor;

    private final int port;
    private boolean isServerWorking;

    private ByteBuffer buffer;
    private Selector selector;

    public SpotifyServer(int port, CommandExecutor commandExecutor) {
        this.port = port;
        this.commandExecutor = commandExecutor;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking = true;
            while (isServerWorking) {
                processRequests();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to start server", e);
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void processRequests() {
        try {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                return;
            }

            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isReadable()) {
                    read(key);

                } else if (key.isAcceptable()) {
                    accept(key);
                }

                keyIterator.remove();
            }
        } catch (IOException e) {
            System.out.println("Error occurred while processing client request: " + e.getMessage());
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        String clientInput = getClientInput(clientChannel);

        if (clientInput == null) {
            return;
        }
        System.out.println(clientInput);

        String output = commandExecutor.execute(key, CommandCreator.newCommand(clientInput));

        if (!output.startsWith(STOP)) {
            writeClientOutput(clientChannel, output);
        }

        if (output.startsWith(DISCONNECT)) {
            stop();
        }
    }

    public static void main(String[] args) {
        Spotify spotify = new Spotify();
        CommandExecutor executor = new CommandExecutor(spotify);
        SpotifyServer spotifyServer = new SpotifyServer(PORT, executor);
        spotifyServer.start();
    }

    //TODO: logger
}*/