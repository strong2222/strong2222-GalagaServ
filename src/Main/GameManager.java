package Main;
import Actors.Enemy;
import Actors.Player;
import Main.GamePanel;
import Main.Key;
import Stage.Stage;
import Stage.Stage1;
import Stage.Stage2;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameManager {
    List<Stage> stages;
    private int currentStageIndex;
    Player player1;
    Player player2;

    public GameManager(Player player,Player play, GamePanel gp) {
        this.stages = new ArrayList<>();
        this.currentStageIndex = 0;
        this.player1 = player;
        this.player2 =play;
        // Add stages to the list
        stages.add(new Stage1(gp));
        stages.add(new Stage2(gp));
    }
    
    // Method to change the current stage
    public void changeStage(int stageIndex) {
        // Check if the stage index is valid
        if (stageIndex >= 0 && stageIndex < stages.size()) {
            currentStageIndex = stageIndex;
             
        } else {
            System.err.println("Invalid stage index.");
        }
    }

    public void update(int randomIndex) {
        // Update the current stage
         
        stages.get(currentStageIndex).update(randomIndex);
    }

    public void draw(Graphics2D g2d) {
        // Draw the current stage
        stages.get(currentStageIndex).draw(g2d);
    }
    
     public void colisionDetect() {

        try {
            player1.checkCollisionWithEnemies(stages.get(currentStageIndex).enemies);
            player2.checkCollisionWithEnemies(stages.get(currentStageIndex).enemies);

            player1.checkEnemywithBullet(stages.get(currentStageIndex).enemies);
            player2.checkEnemywithBullet(stages.get(currentStageIndex).enemies);
        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
     public void checkEnemyBulletToPlayer() {

        for (Enemy enemy : stages.get(currentStageIndex).enemies) {
            player1.checkCollisionWithBullets(enemy.bullets);
            player2.checkCollisionWithBullets(enemy.bullets);
        }

    }
     public void addLive(){
      
        if(player1.lives == 0){
            player1.lives++;
        }
        if(player2.lives == 0){
            player2.lives ++;
        }
      
     }

    public boolean isCompleted() {
       
        
      
        return stages.get(currentStageIndex).isCompleted();
    }
    public boolean isEnded(){
       return stages.get(stages.size()-1).isCompleted();
    }
}

