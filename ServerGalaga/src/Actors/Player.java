/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Actors;

import Main.Key;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import Main.GamePanel;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author dell
 */
public final class Player extends Character implements Serializable {

    GamePanel gp;
    Key keyHandler;
    public volatile boolean IsFiring = false;
    //private boolean isFiring = false;
private boolean lastFireState = false;
private long lastFiredTime = 0;
private final long firingDurationMs = 100;
    public static boolean isAlive;
    transient BufferedImage playerImages;
    public int lives = 3;
    private long lastCollisionDetectionTime;
    private long collisionCooldown = 3000;
    private boolean isBlinking = false;
    private long lastHitTime;
    private long blinkInterval = 3000;
    transient BufferedImage heart;
    static final int tileSize = 60;
      public List<Bullet> bullets;
      private long lastFireTime; 
    private long fireCooldown = 300;
    BufferedImage bulletImage;
      
      //private boolean lastFireState = false;
  
    public Player(GamePanel gp, Key keyhandler, int x, int y) {
        this.x = x;
        this.y = y;
        this.speed = 2;
        this.gp = gp;
        this.keyHandler = keyhandler;
        bullets = new ArrayList<>();

        if (gp != null) {
            this.colition = new Rectangle(x, y, tileSize, tileSize - 10);
        } else {
            // Handle the case where gp is null (e.g., throw an exception or log an error)
        }

        getImage();

    }
    

    public void update() {

        long elapsedTime = System.currentTimeMillis() - lastHitTime;
        if (elapsedTime < blinkInterval) {
            isBlinking = !isBlinking; // Toggle the blinking flag at each blink interval
        } else {
            isBlinking = false; // Stop blinking after the blink interval
        }
        
        if(keyHandler !=null){
        if (keyHandler.up == true) {
            y = y - speed;
            colition.y = y;
        } else if (keyHandler.down == true) {
            y = y + speed;
            colition.y = y;
        } else if (keyHandler.left == true) {
            x = x - speed;
            colition.x = x;
            try {
                playerImages = ImageIO.read(getClass().getResourceAsStream("/source/player/PlayerMain2.png"));
            } catch (IOException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (keyHandler.right == true) {
            x = x + speed;
            colition.x = x;
            try {
                playerImages = ImageIO.read(getClass().getResourceAsStream("/source/player/PlayerMain1.png"));
            } catch (IOException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                playerImages = ImageIO.read(getClass().getResourceAsStream("/source/player/PlayerMain.png"));
            } catch (IOException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (keyHandler.fire && !lastFireState) {
        IsFiring = true;
        lastFiredTime = System.currentTimeMillis();
        fireBullet();
    }

    if (IsFiring) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFiredTime >= firingDurationMs) {
            IsFiring = false;
        }
    }
        lastFireState = keyHandler.fire;
        }

        if (x > gp.getWidth() - 50) {
            x = gp.getWidth() - 50;
        }
        if (x < 0) {
            x = 0;
        }
        if (y > gp.getHeight() - 50) {
            y = gp.getHeight() - 50;
        }
        if (y < 0) {
            y = 0;
        }
        
       
       
        bulletUpdate();
    

    }
    public void bulletDraw(Graphics2D g2d){
        for(Bullet bullet:bullets){
            g2d.drawImage(bulletImage,bullet.x,bullet.y,75,75,null);
        }
    }
    public void bulletUpdate(){
         
        
       Iterator<Bullet> iterator = bullets.iterator();
        
           while (iterator.hasNext()) {
            
            Bullet bullet = iterator.next();
            bullet.colition.y = bullet.y;
            bullet.y -= bullet.speed;
            
             if(bullet.y<50){
                    iterator.remove();
                   
                }
            
           }
    }
    public synchronized void updatePosition(int x, int y, boolean isFiring) {
        this.x = x;
        this.y = y;
        this.IsFiring = isFiring;
    }

    public synchronized int getX() {
        return x;
    }

    public synchronized int getY() {
        return y;
    }

    public synchronized boolean isFiring() {
        return IsFiring;
    }
    
 
    public void checkEnemywithBullet(List<Enemy> enemies){
        
        Iterator<Bullet> iterator = bullets.iterator();
        
       
        while (iterator.hasNext()) {
            
           Bullet bullet = iterator.next();
          for(Enemy enemy:enemies){  
          if (enemy.colition != null && enemy.checkCollision(bullet.colition)) {
                    
                    iterator.remove();
                    
                    break;
                }
                if(bullet.y<50){
                    iterator.remove();
                    break;
                }
               
            }
        }
        }
    
    

    public void getImage() {
        try {
            heart = ImageIO.read(getClass().getResourceAsStream("/source/player/playerHearth.png"));
            playerImages = ImageIO.read(getClass().getResourceAsStream("/source/player/PlayerMain.png"));
            bulletImage = ImageIO.read(getClass().getResourceAsStream("/source/player/sum.png"));
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void draw(Graphics2D g2d) throws IOException {

        if (!isBlinking) {
            g2d.setColor(new Color(0, 0, 0, 0));
            g2d.drawImage(playerImages, x, y, tileSize, tileSize, null);
        }
        

        
    }
     public void fireBullet() {
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastFireTime >= fireCooldown) {
       
        Bullet bullet = new Bullet(x, y, 20);
        bullets.add(bullet);
        lastFireTime = currentTime;
    }
}

    public void checkCollisionWithEnemies(List<Enemy> enemies) throws IOException {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastCollisionDetectionTime >= collisionCooldown) {
            for(Enemy enemy:enemies){
            if (enemy.colition != null && this.colition.intersects(enemy.colition)) {
               
                lastCollisionDetectionTime = currentTime; // Update the last collision detection time
                startBlinking();
                 lives = lives - 1;
            }
            }
        }
    }

    private void startBlinking() {
        isBlinking = true; // Set the blinking flag to true
        lastHitTime = System.currentTimeMillis(); // Reset the last hit time
    }

    public Key getKeyHandler() {
        return keyHandler;
    }

    public boolean isAlive() {

        if (lives == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void liveDraw(Graphics2D g2d,int x,int y) {
        for (int i = 0; i < this.lives; i++) {
            g2d.drawImage(heart, x+i*50, y, 40, 40, null);
        }

    }


    public void reset() {
        this.x = 375;
        this.y = 600;
        this.colition = new Rectangle(x, y, tileSize, tileSize - 10);
        this.lives = 3;
        getImage();

    }


}
