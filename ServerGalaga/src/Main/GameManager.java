package Main;
import Actors.Player;
import Main.GamePanel;
import Main.Key;
import Stage.Stage;
import Stage.Stage1;
import Stage.Stage2;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    List<Stage> stages;
    private int currentStageIndex;

    public GameManager(Player player,Player play, GamePanel gp, Key key) {
        this.stages = new ArrayList<>();
        this.currentStageIndex = 0;
        
        // Add stages to the list
        stages.add(new Stage1(player,play, gp, key));
        stages.add(new Stage2(player,play, gp, key));
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

    public void update() {
        // Update the current stage
        stages.get(currentStageIndex).update();
    }

    public void draw(Graphics2D g2d) {
        // Draw the current stage
        stages.get(currentStageIndex).draw(g2d);
    }

    public boolean isCompleted() {
        // Check if the current stage is completed
        return stages.get(currentStageIndex).isCompleted();
    }
}
