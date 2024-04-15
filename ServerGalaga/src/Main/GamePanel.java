package Main;






import Actors.Enemy;
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
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
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
    static final int fps =120;
    
    private static String  levelText;
    
    Key keyHandler = new Key();
   public Player player1 ;
   public Player player2 ;
  
   int playerNumber =1;
    Socket ss;
    
   private int portNumber =4444;
   
   
    public final int orignalTileSize= 20;
    final int scale = 3;
    public final int tileSize = orignalTileSize * scale;
    
    final int maxScreenColumn = 16;
    final int maxScreenRow = 12;
    public final int screenColumn = tileSize * maxScreenColumn;
    public final int screenRow = tileSize * maxScreenRow;
    
    Enemy enm = new Enemy(this,20,20,"/source/player/e2.png","/source/player/e2i.png");
    
    Thread thread1;
    
    BufferedImage backGroundImage;
  
    //constructor
    public GamePanel(){
      
      this.setPreferredSize(new Dimension(screenColumn,screenRow));
      this.setBackground(Color.black);
      this.addKeyListener(keyHandler);
       this.setFocusable(true);
       this.setLayout(null);

        try {
            backGroundImage=ImageIO.read(getClass().getResourceAsStream("/source/player/R.png"));
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
            resetGame();
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
    
    
     levelText =  "Level-1";
     
        
    

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
    public void GameOver(Graphics2D g2d){
        
        try {
            
            g2d.drawImage(ImageIO.read(getClass().getResourceAsStream("/source/player/GameOver.png")),270,150,400,400,null);
            
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
           resetButton.setVisible(true);
           exitButton.setVisible(true);
            
        
    }
    //Winning
    public void YouWin(Graphics2D g2d){
        try {
            g2d.drawImage(ImageIO.read(getClass().getResourceAsStream("/source/player/YouWin.png")),270,150,400,400,null);
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
   private void intializePlayer(){
         if(playerNumber  ==1 ){
             player1 = new Player(this,keyHandler,200,500);
             player2 = new Player(this,null,500,500);
         }
         else{
             player1 = new Player(this,null,200,500);
             player2 = new Player(this,keyHandler,500,500);
         }
         
         
     }



public void startGameLoop(SocketChannel socketChannel) throws IOException {
    try {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        double drawInterval = 1000 / fps;
        double nextDrawTime = System.currentTimeMillis() + drawInterval;
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        while (!Thread.currentThread().isInterrupted() && player1.isAlive() && player2.isAlive()) {
            long currentTime = System.currentTimeMillis();

            if (currentTime >= nextDrawTime) {
                buffer.clear();
                if (playerNumber == 1) {
                    buffer.putInt(player1.x);
                    buffer.putInt(player1.y);
                    player1.update();
                } else {
                    buffer.putInt(player2.x);
                    buffer.putInt(player2.y);
                    player2.update();
                }
                buffer.flip();

                while (buffer.hasRemaining()) {
                    socketChannel.write(buffer);
                }

                nextDrawTime += drawInterval;
            }

            selector.selectNow();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

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
                        if (playerNumber == 1) {
                            player2.x = buffer.getInt();
                            player2.y = buffer.getInt();
                        } else {
                            player1.x = buffer.getInt();
                            player1.y = buffer.getInt();
                        }
                    }
                }
            }

            repaint();

            long remainingTime = (long) (nextDrawTime - System.currentTimeMillis());
            if (remainingTime > 0) {
                Thread.sleep(remainingTime);
            }
        }
    } catch (InterruptedException ex) {
        Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
    }
}


    
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(backGroundImage, 0, 50, this);
        
         if (player1 != null && player2 != null) {
        try {
            player1.draw(g2d);
            enm.draw(g2d);
            player2.draw(g2d);
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       
      
        g2d.setFont(new Font("Arial", Font.BOLD, 25));
        g2d.setColor(Color.white);
        g2d.drawString(levelText, 450, 30);
//        if(!player.isAlive()){
//            GameOver(g2d);
//        }
       
      
       g2d.dispose();
    }
    private void resetGame(){
        
      
//        player.reset(); 
//            levelText = "Level-1";
//        
//        resetButton.setVisible(false);
//        exitButton.setVisible(false);
    }

}
