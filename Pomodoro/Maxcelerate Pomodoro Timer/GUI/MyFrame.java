import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.awt.Color;

public class MyFrame extends JFrame{

    MyFrame() {
            this.setTitle("Pomodoro");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setResizable(false);
            this.setSize(900, 900);
            this.setVisible(true);
    
            ImageIcon image = new ImageIcon("logo.jpg");
            this.setIconImage(image.getImage());
            this.getContentPane().setBackground(new Color(255, 201,181));
    }
}