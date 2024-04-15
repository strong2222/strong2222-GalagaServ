/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Actors;

import java.awt.Rectangle;
import java.io.Serializable;

public class Bullet extends Character implements Serializable{
 
    //constructor
        public Bullet(int x , int y,int speed){
            this.x = x;
            this.y = y;
            this.speed = speed;
            colition = new Rectangle(this.x+25,this.y+5,14,23);
            
         }
    }

  
    
    
    
    



