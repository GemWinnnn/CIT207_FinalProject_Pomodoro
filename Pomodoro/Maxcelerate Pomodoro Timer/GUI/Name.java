import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane; // Add this import
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Name {

    public static void main(String[] args) {

        JLabel label = new JLabel();
        label.setText("Welcome to FocusFlow, what is your name?");

        JTextField textField = new JTextField();
        textField.setBounds(50, 50, 200, 30); // Adjust the bounds as needed

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(50, 100, 100, 30); // Adjust the bounds as needed

        JFrame frame = new MyFrame(); // Use your MyFrame class

        frame.setLayout(null); // Set layout to null for absolute positioning

        frame.add(label);
        frame.add(textField);
        frame.add(submitButton);

    }
}
