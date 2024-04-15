package Main;

import Actors.Player;
import Actors.Player;
import Stage.Stage;
import Stage.Stage1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer {
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final int portNumber = 4444;
    private final Player player1;
    private Player player2;
   
    private static final int fps = 120;
    private final List<SocketChannel> clients = new ArrayList<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    int currenPlayerIndex=0;
    private SocketChannel player1Socket;
private SocketChannel player2Socket;
    public GameServer() {
        this.player1 = new Player(null, null, 200, 500);
        this.player2 = new Player(null, null, 700, 500);
       
    }

    public void start() {
        executorService.submit(this::serverCommunication);
    }
    private void serverCommunication() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(portNumber));
            System.out.println("Server running...");

            while (running.get()) {
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                System.out.println("Client connected");
                clients.add(clientSocketChannel);

                if (clients.size() == 1) { // First client is player1
                    player1Socket = clientSocketChannel;
                    System.out.println("Player 1 connected");
                    sendPlayerIdentity(player1Socket, 1);
                } else if (clients.size() == 2) { // Second client is player2
                    player2Socket = clientSocketChannel;
                    System.out.println("Player 2 connected");
                    sendPlayerIdentity(player2Socket, 2);
                }

                executorService.submit(() -> handleClient(clientSocketChannel));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendPlayerIdentity(SocketChannel socket, int playerNumber) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.putInt(playerNumber);
            buffer.flip();
            socket.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    


    private void handleClient(SocketChannel clientSocketChannel) {
    try {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (running.get() && clientSocketChannel.read(buffer) != -1) {
            buffer.flip();

            if (player1Socket == null) { // First client is player1
                player1Socket = clientSocketChannel;
                System.out.println("Player 1 connected");
            } else if (player2Socket == null) { // Second client is player2
                player2Socket = clientSocketChannel;
                System.out.println("Player 2 connected");
            } else { // Handle player position updates
                int senderIndex = clients.indexOf(clientSocketChannel);
                if (senderIndex != -1) {
                    int x = buffer.getInt();
                    int y = buffer.getInt();
                    buffer.clear();

                    if (clientSocketChannel == player1Socket) {
                        player1.x = x;
                        player1.y = y;
                        broadcastPlayerPosition(0); // Broadcast player1 position
                    } else if (clientSocketChannel == player2Socket) {
                        player2.x = x;
                        player2.y = y;
                        broadcastPlayerPosition(1); // Broadcast player2 position
                    }
                }
            }
            Thread.sleep(1000 / fps);
        }
    } catch (IOException | InterruptedException ex) {
        Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
        clients.remove(clientSocketChannel);
        if (clientSocketChannel == player1Socket) {
            player1Socket = null;
        } else if (clientSocketChannel == player2Socket) {
            player2Socket = null;
        }
        try {
            clientSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    private void broadcastPlayerPosition(int senderIndex) {
    ByteBuffer positionBuffer = ByteBuffer.allocate(8); // Integers x and y (4 bytes each)
    if (senderIndex == 0) { // Sender is player1
        positionBuffer.putInt(player1.x);
        positionBuffer.putInt(player1.y);
    } else { // Sender is player2
        positionBuffer.putInt(player2.x);
        positionBuffer.putInt(player2.y);
    }
    positionBuffer.flip();

    if (senderIndex == 0 && player2Socket != null) { // Send player1 position to player2
        try {
            player2Socket.write(positionBuffer.duplicate());
        } catch (IOException e) {
            e.printStackTrace();
        }
    } else if (senderIndex == 1 && player1Socket != null) { // Send player2 position to player1
        try {
            player1Socket.write(positionBuffer.duplicate());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    positionBuffer.rewind(); // Rewind buffer for next write
}


    public void stop() {
        running.set(false);
        executorService.shutdown();
    }

    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.start();
    }
}
