//package Actors;
//
//import Main.GamePanel;
//import java.awt.Graphics2D;
//
//
//
//
//
//
///**
// *
// * @author dell
// */
//public class Stinger extends Enemy{
//    
//    String enemyImageBase = "/source/player/enemy1.png";
//    String enemyImageIdle = "/source/player/enemy1idle.png";
//    
//   
//    int StartX;
//    int StartY;
//   
//    Graphics2D g2d;
//    
//  
//        
//    public Stinger(GamePanel gp, int Xenemy, int Yenemy) {
//        super(gp, Xenemy, Yenemy);
//        loadImage();
//        
//    }
//    public void loadImage(){
//        super.loadImages(enemyImageBase, enemyImageIdle);
//    }
//    public void draw(){
//        super.draw(g2d);
//        
//        
//    }
//    public void update(int endx,int endy){
//       super.update("left");
//       
//       
//          
//       
//     }
//       
//    
//    public boolean checkCollision(){
//        return super.checkCollision(this.colition);
//    }
//    protected void doFormation(int endx,int endy){
//       
//    
//        if(x != endx && y!=endx){
//            x=x+3;
//            y=y-3;
//            
//            if (x > endx && y<endy) {
//                x = endx;  
//                
//            }
//            
//        }
//    }
//    public void attack(){
//        
//    }
//   
//}
//    
//
//    
//
//
//    
//    
//    
//
