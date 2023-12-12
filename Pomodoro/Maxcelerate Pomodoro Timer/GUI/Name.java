import javax.swing.*;
import java.awt.*;

public class Name {

    public static void main(String[] args) {
        // Create MyFrame instance
        MyFrame frame = new MyFrame();

        // Define elements
        JLabel label = new JLabel("Welcome to FocusFlow, What is your name?");
        label.setForeground(new Color(33, 26, 29));
        label.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton submitButton = new JButton("Start");
        submitButton.setFont(new Font("Arial", Font.BOLD, 18));

        // Create content panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 201, 181)); // Set background color
        GridBagConstraints gbc = new GridBagConstraints();

        // Center panel vertically
        gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(Box.createVerticalGlue(), gbc);

        // Create inner panel for elements with some spacing
        JPanel innerPanel = new JPanel(new GridBagLayout());
        // Set inner panel background to transparent
        innerPanel.setOpaque(false);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        GridBagConstraints innerGbc = new GridBagConstraints();

        // Center label horizontally
        innerGbc.gridx = GridBagConstraints.RELATIVE;
        innerGbc.fill = GridBagConstraints.HORIZONTAL;
        innerPanel.add(label, innerGbc);

        // Add vertical spacing between label and text field
        innerGbc.gridy = 1;
        innerGbc.insets = new Insets(10, 0, 10, 0);
        innerGbc.fill = GridBagConstraints.BOTH;
        innerPanel.add(Box.createVerticalStrut(20), innerGbc);

        // Add text field
        innerGbc.gridy = 2;
        innerGbc.insets = new Insets(0, 0, 0, 0);
        innerGbc.fill = GridBagConstraints.HORIZONTAL;
        innerPanel.add(textField, innerGbc);

        // Add vertical spacing between text field and button
        innerGbc.gridy = 3;
        innerGbc.insets = new Insets(20, 0, 10, 0);
        innerGbc.fill = GridBagConstraints.BOTH;
        innerPanel.add(Box.createVerticalStrut(20), innerGbc);

        // Add button
        innerGbc.gridy = 4;
        innerGbc.insets = new Insets(0, 0, 0, 0);
        innerGbc.fill = GridBagConstraints.HORIZONTAL;
        innerPanel.add(submitButton, innerGbc);

        // Add inner panel to main panel
        gbc.weighty = 1.0; // Expand to fill remaining space vertically
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(innerPanel, gbc);

        frame.add(panel);
        frame.setVisible(true);
    }
}
