/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Stage;

import Actors.Enemy;
import Actors.Player;
import Main.GamePanel;
import Main.Key;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author dell
 */
public class Stage2 extends Stage{
        public Stage2(Player player,Player play,GamePanel gp,Key keyHandler){
        super(player,play ,gp, keyHandler);
        initialize();
    }
    public void update(){
        super.update();
        isCompleted();
    }
    public void initialize() {
       
        int numEnemies = 5; 

        for(int i = 1; i <= numEnemies+2; i++) {
            enemies.add(new Enemy(gp, 50+i*100, 100,"/source/player/e2.png","/source/player/e2i.png")); 
        }
        for(int i = 1; i <= numEnemies-2; i++) {
            enemies.add(new Enemy(gp, 250+i*100, 150,"/source/player/e2.png","/source/player/e2i.png")); 
        }
        for(int i = 1; i <= numEnemies+2; i++) {
            enemies.add(new Enemy(gp, 50+i*100, 200,"/source/player/e1.png","/source/player/e1i.png")); 
        }
        for(int i = 1; i <= numEnemies; i++) {
            enemies.add(new Enemy(gp, 150+i*100, 250,"/source/player/e1.png","/source/player/e1i.png"));
        }
        for(int i = 1; i <= numEnemies-2; i++) {
            enemies.add(new Enemy(gp, 250+i*100, 300,"/source/player/e2.png","/source/player/e2i.png")); 
        }
            enemies.add(new Enemy(gp, 450, 350,"/source/player/e1.png","/source/player/e1i.png"));
        
    
        try {
            bulletImage = ImageIO.read(getClass().getResourceAsStream("/source/player/sum.png"));
            
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void draw(Graphics2D g2d){
        super.draw(g2d);
    }
    public void reset(){
        super.reset();
        this.initialize();
    }
    public boolean isCompleted(){
        return super.isCompleted();
    }
}
