package Main;

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
    private Player player1;
    private Player player2;
    private boolean gameStarted = false;
    private static final int fps = 120;
    private final List<SocketChannel> clients = new ArrayList<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private int currentAttackerIndex = 0;
    private SocketChannel player1Socket;
    private SocketChannel player2Socket;
    private int attackerEnemyIndex = 0;
    private boolean player1EndGame = false;
private boolean player2EndGame = false;

    public GameServer() {
       
        
    }

    public void start() {
        executorService.submit(this::serverCommunication);
        executorService.submit(this::updateAttackerEnemyIndexThread);
    }

    private void serverCommunication() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(portNumber));
            System.out.println("Server running...");

            while (running.get()) {
                
                
                if(player1 == null || player2 == null){
                SocketChannel clientSocketChannel = serverSocketChannel.accept();
                System.out.println("Client connected");
                clients.add(clientSocketChannel);
                
                if (clients.size() == 1) {
                    player1Socket = clientSocketChannel;
                    System.out.println("Player 1 connected");
                     this.player1 = new Player(null, null, 200, 500);
                   
                    sendPlayerIdentity(player1Socket, 1);
                } else if (clients.size() == 2) {
                    player2Socket = clientSocketChannel;
                    System.out.println("Player 2 connected");
                    sendPlayerIdentity(player2Socket, 2);
                     this.player2 = new Player(null, null, 700, 500);
                    gameStarted = true;
                    sendGameStartedFlag(player1Socket);
                    sendGameStartedFlag(player2Socket);
                }

                executorService.submit(() -> handleClient(clientSocketChannel));
            }
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

    private void sendGameStartedFlag(SocketChannel socket) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put((byte) (gameStarted ? 1 : 0));
            buffer.flip();
            socket.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void handleClient(SocketChannel clientSocketChannel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1028); // Increased size to accommodate isFiring flag
            while (running.get() && clientSocketChannel.read(buffer) != -1 ) {
                buffer.flip();

                if (player1Socket == null) {
                    player1Socket = clientSocketChannel;
                    System.out.println("Player 1 connected");
                } else if (player2Socket == null) {
                    player2Socket = clientSocketChannel;
                    System.out.println("Player 2 connected");
                } else {
                    int senderIndex = clients.indexOf(clientSocketChannel);
                    if (senderIndex != -1) {
                        int x = buffer.getInt();
                        int y = buffer.getInt();
                        boolean isFiring = buffer.get() != 0; // Read isFiring flag

                        buffer.clear();

                        if (player1Socket != null && clientSocketChannel == player1Socket) {
                            player1.x = x;
                            player1.y = y;
                            player1.IsFiring = isFiring;
                             if (buffer.hasRemaining()) {
                                 player1EndGame = buffer.get() != 0; // Read endgame flag
                               
                             }
                             broadcastPlayerPosition(0);
                             

                        } else if (player2Socket != null && clientSocketChannel == player2Socket) {
                            player2.x = x;
                            player2.y = y;
                            player2.IsFiring = isFiring;
                            if (buffer.hasRemaining()) {
                                player2EndGame = buffer.get() != 0; 
                                 
                            }
                            broadcastPlayerPosition(1);
                            

                        }
                       
                        if( player1EndGame && player2EndGame){
                            player1= null;
                            player2 = null;
                            System.out.println("suga");
                        }
                        
                      
                        buffer.clear();
                        
                       
                    }
                }
                try {

                    Thread.sleep(1000 / fps);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {

        } finally {
            clients.remove(clientSocketChannel);
            if (clientSocketChannel == player1Socket) {
                player1EndGame = true;
                player1Socket = null;
                
            } else if (clientSocketChannel == player2Socket) {
                 player2EndGame = true;
               player2Socket = null;
              
            }
            try {
                clientSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAttackerEnemyIndexThread() {
        while (running.get()) {
            updateAttackerEnemyIndex();
            try {
                Thread.sleep(1000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAttackerEnemyIndex() {
        attackerEnemyIndex = (attackerEnemyIndex + 1) % 20; 
    }


    private void broadcastPlayerPosition(int senderIndex) {

        ByteBuffer positionBuffer = ByteBuffer.allocate(13); // Integers x, y (4 bytes each) and isFiring flag (1 byte)
        positionBuffer.putInt(attackerEnemyIndex);
        if (senderIndex == 0) { // Sender is player1
            positionBuffer.putInt(player1.x);
            positionBuffer.putInt(player1.y);

            positionBuffer.put((byte) (player1.IsFiring ? 1 : 0));

        } else { // Sender is player2
            positionBuffer.putInt(player2.x);
            positionBuffer.putInt(player2.y);

            positionBuffer.put((byte) (player2.IsFiring ? 1 : 0));

        }
        positionBuffer.flip();

        if (senderIndex == 0 && player2Socket != null) {
            try {
                player2Socket.write(positionBuffer.duplicate());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (senderIndex == 1 && player1Socket != null) {
            try {
                player1Socket.write(positionBuffer.duplicate());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        positionBuffer.rewind();
        //updateAttackerIndex();
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
