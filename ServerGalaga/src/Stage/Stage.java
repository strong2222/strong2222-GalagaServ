
package Stage;

import Actors.Bullet;
import Actors.Enemy;
import Actors.EnemyBullet;
import Actors.Player;
import Main.GamePanel;
import Main.Key;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Stage{
    
   public List<Enemy> enemies;
    GamePanel gp;
    public List<Bullet> bullets;
    Player player1;
    Player player2;
    private boolean lastFireState = false;
    Key keyHandler;
    
    
    private long lastFireTime; 
    private long fireCooldown = 500;

    private long lastAttackTime; // Track the time of the last attack
    private long attackInterval = 1000; // Interval between each random enemy attack in milliseconds
    
     BufferedImage bulletImage;
     static final int tileSize = 60;
    
    public Stage(GamePanel gp) {
   
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        
        this.player1 = player1;
        this.player2 = player2;
        this.gp = gp;
     
        
       
        
    }

    public void update() {
        
        for (Enemy enemy : enemies) {
            enemy.update();
        }
            
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAttackTime >= attackInterval) {
                AttackPattern(enemies); // Trigger a random enemy to shoot
                lastAttackTime = currentTime; // Update the last attack time
              } 
        }
    
    

    

   
   
    public void draw(Graphics2D g2d) {
   
    List<Enemy> enemiesCopy = new ArrayList<>(enemies);

     
    for (Enemy enemy : enemiesCopy) {
        enemy.draw(g2d);
    }

    for (Bullet bullet : bullets) {
        g2d.draw(bullet.colition);
        g2d.drawImage(bulletImage, bullet.x, bullet.y - 10, tileSize, tileSize, null);
    }
}

   

private void AttackPattern(List<Enemy> enemies) {
    if (!enemies.isEmpty()) {
        // Select a random enemy from the list
        Random random = new Random();
        int randomIndex = random.nextInt(enemies.size());
        Enemy attacker = enemies.get(randomIndex);

       
        attacker.shoot();
    }
}

   
   
  
    public boolean isCompleted() {
        
       if(enemies.isEmpty()){
           return true;
       }
       else{
           return false;
       }
    }

    public void reset() {
       this.player1 = player1;
       this.player2 = player2;
       enemies.clear();
        
    }
}

