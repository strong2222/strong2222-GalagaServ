package Main;

import Actors.Enemy;
import Actors.EnemyBullet;
import Actors.Player;
import Stage.Stage;
import Stage.Stage1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.image.BufferedImage;
import java.io.IOException;

import java.net.InetSocketAddress;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import javax.swing.JPanel;

public class GamePanel extends JPanel {

    JButton resetButton;
    JButton exitButton;
    JButton nextButton;
    static final int fps = 120;

    private static String levelText;

    Key keyHandler = new Key();
    public Player player1;
    public Player player2;

    int playerNumber = 1;
    

    private int portNumber = 4444;
    private boolean gameEnd = false;
    public final int orignalTileSize = 20;
    final int scale = 3;
    public final int tileSize = orignalTileSize * scale;

    final int maxScreenColumn = 16;
    final int maxScreenRow = 12;
    public final int screenColumn = tileSize * maxScreenColumn;
    public final int screenRow = tileSize * maxScreenRow;
   // Enemy enemy = new Enemy(null, 150, 100, "/source/player/e2.png", "/source/player/e2i.png");
    boolean gameEndFlagSent = false;
    
    GameManager gm;

    Thread thread1;

    BufferedImage backGroundImage;

    //constructor
    public GamePanel() {

        this.setPreferredSize(new Dimension(screenColumn, screenRow));
        this.setBackground(Color.black);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        this.setLayout(null);

        try {
            backGroundImage = ImageIO.read(getClass().getResourceAsStream("/source/player/R.png"));
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Inside the GamePanel constructor
        // Other initialization...
        resetButton = new JButton("Reset");
        resetButton.setBounds(350, 440, 80, 30);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              //  resetGame();
            }
        });

        exitButton = new JButton("Exit");
        exitButton.setBounds(470, 440, 80, 30);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        nextButton = new JButton("next");
        nextButton.setBounds(590, 440, 80, 30);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                levelText = "Level-2";
                resetButton.setVisible(false);
                exitButton.setVisible(false);
                nextButton.setVisible(false);

            }
        });

        // Initially
        resetButton.setVisible(false);
        exitButton.setVisible(false);
        nextButton.setVisible(false);

        levelText = "Level-1";

        this.add(resetButton);
        this.add(exitButton);
        this.add(nextButton);

        new Thread(this::serverCommunication).start();

        JFrame frame = new JFrame("My Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    // GameOver
    public void GameOver(Graphics2D g2d) {

        try {

            g2d.drawImage(ImageIO.read(getClass().getResourceAsStream("/source/player/GameOver.png")), 270, 150, 400, 400, null);

        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        resetButton.setVisible(true);
        exitButton.setVisible(true);

    }

    //Winning
    public void YouWin(Graphics2D g2d) {
        try {
            g2d.drawImage(ImageIO.read(getClass().getResourceAsStream("/source/player/YouWin.png")), 270, 150, 400, 400, null);
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        exitButton.setVisible(true);
        resetButton.setVisible(true);
        nextButton.setVisible(true);
    }

    private void serverCommunication() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", portNumber));
            System.out.println("connected");
            readPlayerNumber(socketChannel);
            intializePlayer();
            startGameLoop(socketChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readPlayerNumber(SocketChannel socketChannel) {
        try {
            ByteBuffer numberBuffer = ByteBuffer.allocate(4);
            socketChannel.read(numberBuffer);
            numberBuffer.flip();
            playerNumber = numberBuffer.getInt();
            System.out.println("Player " + playerNumber);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void intializePlayer() {
        if (playerNumber == 1) {
            player1 = new Player(this, keyHandler, 200, 500);
            player2 = new Player(this, null, 500, 500);
           
        } else {
            player1 = new Player(this, null, 200, 500);
            player2 = new Player(this, keyHandler, 500, 500);
        }
         gm = new GameManager(player1, player2, this);

    }

    public void startGameLoop(SocketChannel socketChannel) throws IOException {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1025); // Increased size to accommodate isFiring flag

            double drawInterval = 1000 / fps;
            double nextDrawTime = System.currentTimeMillis() + drawInterval;
            Selector selector = Selector.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

            
            ByteBuffer gameStartedBuffer = ByteBuffer.allocate(1);
            while (socketChannel.read(gameStartedBuffer) == 0 ) {
                
            }
            gameStartedBuffer.flip();
            boolean gameStarted = gameStartedBuffer.get() != 0;

            while (!Thread.currentThread().isInterrupted() && !gameEndFlagSent ) {
                long currentTime = System.currentTimeMillis();
                 if (gameEnd) {
                        sendGameEndFlag(socketChannel);
                        gameEndFlagSent = true;
                  }

                if (currentTime >= nextDrawTime) {
                    buffer.clear();
                    if (playerNumber == 1) {
                        buffer.putInt(player1.x);
                        buffer.putInt(player1.y);
                        buffer.put((byte) (player1.IsFiring ? 1 : 0)); 
                        player1.update();
                    } else {
                        buffer.putInt(player2.x);
                        buffer.putInt(player2.y);
                        buffer.put((byte) (player2.IsFiring ? 1 : 0)); 
                        player2.update();
                    }
                    buffer.flip();

                    while (buffer.hasRemaining()) {
                        socketChannel.write(buffer);
                    }

                    buffer.clear();
                   
                    nextDrawTime += drawInterval;

                    selector.selectNow();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                    if (gameStarted) {
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            keyIterator.remove();

                            if (!key.isValid()) {
                                continue;
                            }

                            if (key.isReadable()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                buffer.clear();
                                int numBytesRead = channel.read(buffer);
                                if (numBytesRead == -1) {
                                    // Handle disconnection
                                } else {
                                    buffer.flip();
                                    if (buffer.remaining() >= 4) {
                                        int attacker = buffer.getInt();
                                       gm.update(attacker);
                                        System.out.println(attacker);
                                    }

                                    if (playerNumber == 1) {

                                        player2.x = buffer.getInt();
                                        player2.y = buffer.getInt();

                                        player2.colition.y = player2.y;
                                        player2.colition.x = player2.x;
                                        boolean otherPlayerIsFiring = buffer.get() != 0; 

                                        if (otherPlayerIsFiring) {
                                            player2.fireBullet();
                                        }
                                        player2.update();

                                    } else if (playerNumber == 2) {

                                        player1.x = buffer.getInt();
                                        player1.y = buffer.getInt();

                                        player1.colition.y = player1.y;
                                        player1.colition.x = player1.x;

                                        boolean otherPlayerIsFiring = buffer.get() != 0; 
                                        if (otherPlayerIsFiring) {
                                            player1.fireBullet();
                                            
                                        }
                                        player1.update();
                                    }
                                    buffer.clear();
                                }
                            }
                        }

                        

                        if (!player1.isAlive() && !player2.isAlive()) {
                            gameEnd = true;
                        } else if (!player1.isAlive()) {
                            // Player 1 is dead, disable controls for them
                            player1.keyHandler = null;
                            //player1 = null;
                        } else if (!player2.isAlive()) {
                            // Player 2 is dead, disable controls for them
                            player2.keyHandler = null;
                           // player2 = null;
                        }
                           gm.colisionDetect();
                            gm. checkEnemyBulletToPlayer();
                        repaint();
                    }
                    if(gm.isCompleted()){
                        gm.changeStage(1);
                        
                    }
                    long remainingTime = (long) (nextDrawTime - System.currentTimeMillis());
                    if (remainingTime > 0) {
                        Thread.sleep(remainingTime);
                    }
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(backGroundImage, 0, 50, this);

        if (player1 != null && player2 != null) {
            try {
                if(player1.isAlive()){
                player1.draw(g2d);
                player1.bulletDraw(g2d);
                }
                if(player2.isAlive()){
                player2.draw(g2d);
                player2.bulletDraw(g2d);
                }
            } catch (IOException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            player1.liveDraw(g2d, 50, 0);
            player2.liveDraw(g2d, 740, 0);
            gm.draw(g2d);

        }
        if(gameEnd){
            GameOver(g2d);
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 25));
        g2d.setColor(Color.white);
       
        if (player1 != null && player2 != null) {
            g2d.drawString(levelText, 450, 30);
            g2d.drawString(Integer.toString(player1.point), 100, 50);
            g2d.drawString(Integer.toString(player2.point), 700, 50);
        }


        g2d.dispose();
    }
    private void sendGameEndFlag(SocketChannel socketChannel) {
    try {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) (gameEnd ? 1 : 0));
        buffer.flip();
        socketChannel.write(buffer);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

   


// private void resetGame() {
//    // Stop or interrupt the existing serverCommunication thread
//    if (thread1 != null && thread1.isAlive()) {
//        thread1.interrupt();
//        try {
//            thread1.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    player1.reset();
//    player2.reset();
//    levelText = "Level-1";
//    resetButton.setVisible(false);
//    exitButton.setVisible(false);
//
//    // Restart the serverCommunication in the same thread
//    serverCommunication();
//}
}
