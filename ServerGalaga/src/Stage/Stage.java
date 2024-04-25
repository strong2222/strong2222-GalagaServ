
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



public class Stage{
    
   public List<Enemy> enemies;
    GamePanel gp;
    public List<Bullet> bullets;
    Player player1;
    Player player2;
    private boolean lastFireState = false;
    
    
    
    //private long lastFireTime; 
    private long fireCooldown = 500;

    private long lastAttackTime; 
    private long attackInterval = 1000;
    
     BufferedImage bulletImage;
     static final int tileSize = 60;
    
    public Stage(GamePanel gp) {
   
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        
        this.player1 = player1;
        this.player2 = player2;
        this.gp = gp;
     
        
       
        
    }

    public void update(int randomIndex) {
        
            for(Enemy enemy:enemies){
                enemy.update();
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAttackTime >= attackInterval) {
                AttackPattern(randomIndex); // Trigger a random enemy to shoot
                lastAttackTime = currentTime; // Update the last attack time
              } 
        }
    
    

    

   
   
    public void draw(Graphics2D g2d) {
   
    List<Enemy> enemiesCopy = new ArrayList<>(enemies);

     
    for (Enemy enemy : enemiesCopy) {
        enemy.draw(g2d);
    }

   
}

   

private void AttackPattern(int randomIndex) {
    if (!enemies.isEmpty()) {
        
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

