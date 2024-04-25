package Actors;

import Main.GamePanel;
import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Enemy extends Character {
    
    GamePanel gp;
    BufferedImage[] enemyImages; 
    int currentAnimationFrame; 
    long lastFrameChangeTime; 
    long frameChangeDelay = 200; 
    static final int tileSize = 60;
    
    public boolean isDestroyed = false;
    int idleMovement =x+20;
    int idleSpeed = 20;
    
//    private String imagePathBase;
//    private String imagePathIdle;
    // Bullets fired by the enemy
    ArrayList<EnemyBullet> bullets;
    BufferedImage bulletImage;

    public Enemy(GamePanel gp, int Xenemy, int Yenemy,String imagePathIdle,String imagePathBase) {
        this.gp = gp;
        this.x = Xenemy;
        this.y = Yenemy;
        this.colition = new Rectangle(x, y, tileSize - 10, tileSize - 20);
        this.speed = 10;
        loadImages(imagePathBase,imagePathIdle);
        
        this.bullets = new ArrayList<>();
    }
    
    protected void loadImages(String imagePathBase,String imagePathIdle ) {
        try {
            
            
            
            enemyImages = new BufferedImage[2]; // Assuming two frames for the animation
            enemyImages[0] = ImageIO.read(getClass().getResourceAsStream(imagePathBase));
            enemyImages[1] = ImageIO.read(getClass().getResourceAsStream(imagePathIdle));
            bulletImage =  ImageIO.read(getClass().getResourceAsStream("/source/player/enemyBullet.png"));
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void draw(Graphics2D g2d) {
        if (!isDestroyed) {
            // Calculate which frame to draw based on elapsed time
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameChangeTime > frameChangeDelay) {
                
                currentAnimationFrame = (currentAnimationFrame + 1) % enemyImages.length;
                
                lastFrameChangeTime = currentTime;
                
            }
           
            // Draw the current frame
            g2d.setColor(new Color(0,0,0,0));
            g2d.drawImage(enemyImages[currentAnimationFrame], x, y, tileSize, tileSize, null);
            
            // Draw bullets
            for (Bullet bullet : bullets) {
                g2d.drawImage(bulletImage,bullet.x,bullet.y,75,75 ,null);
             
            }
        }
       
    }
    
    public void update() {
        if (!isDestroyed) {
            // Move bullets
            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                
                bullet.y += bullet.speed;
                bullet.colition.y += bullet.speed;
                if (bullet.colition!=null&&bullet.y > 650) {
                    bullets.remove(i);
                   

                    i--;
                }
            } 
            

//            if(this.colition != null){
//                colition.x = x;
//                colition.y = y;
//            }
            
            
                
                //idleLoop();
                
           
           
            
        }
    }
    
    public boolean checkCollision(Rectangle rect) {
        if (!isDestroyed && this.colition != null) { 
            if (rect.intersects(this.colition)) {
                isDestroyed = true;
                this.colition = null;
                return true; 
            }
        }
        return false; 
    }
   
    private void idleLoop(){
        x = x + idleSpeed;
        if (x > idleMovement || x < 0) { 
            idleSpeed = -idleSpeed;
        }
    }
    
   
    public void shoot() {
        
        EnemyBullet bullet = new EnemyBullet(x + (tileSize / 2), y + (tileSize / 2),5);
        bullets.add(bullet);
    }
    public ArrayList<EnemyBullet> getEnemyBullet(){
        return bullets;
    }
}
