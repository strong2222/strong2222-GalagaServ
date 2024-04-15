package Main;





import javax.swing.JFrame;

public class GameFrame extends JFrame{
    
    
    
    public GameFrame(GamePanel gp) {
        
        this.setResizable(false);
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(gp);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        

    }
   
    
}
