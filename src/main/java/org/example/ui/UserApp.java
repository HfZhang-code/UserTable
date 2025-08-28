package org.example.ui;

import org.example.InMemoryUserRepository;
import org.example.User;
import org.example.UserRepository;

import javax.swing.*;
import java.awt.*;

public class UserApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Repository and initial demo data
            UserRepository repo = new InMemoryUserRepository();
            repo.create(new User("Alice", 30));
            repo.create(new User("Bob", 22));

            // Frame + panel
            JFrame frame = new JFrame("Users");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new UserPanel(repo));
            frame.setSize(new Dimension(500, 350));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

