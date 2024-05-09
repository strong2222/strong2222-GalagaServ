
package Stage;

import Actors.Enemy;
import Main.GamePanel;

/**
 *
 * @author dell
 */
public class Stage1 extends Stage{
    
    
    
    public Stage1(GamePanel gp){
       super(gp);
        initialize();
    }
    
       public void initialize() {
       
        int numEnemies = 5; 

        for(int i = 1; i <= numEnemies; i++) {
            enemies.add(new Enemy(null,150+i*100, 100,"/source/player/e2.png","/source/player/e2i.png")); 
            
        }
        for(int i = 1; i <= numEnemies+2; i++) {
            enemies.add(new Enemy(null, 50+i*102, 200,"/source/player/e1.png","/source/player/e1i.png")); 
        }
        for(int i = 1; i <= numEnemies+2; i++) {
            enemies.add(new Enemy(null,48+i*100, 300,"/source/player/e1.png","/source/player/e1i.png")); 
        }
        for(int i = 1; i <= numEnemies; i++) {
            enemies.add(new Enemy(null,150+i*106, 400,"/source/player/e1.png","/source/player/e1i.png"));
        }
    
        
    }
   
    public void reset(){
        super.reset();
        this.initialize();
    }
    public boolean isCompleted(){
        return super.isCompleted();
    }
    
    
}
