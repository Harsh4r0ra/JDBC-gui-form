import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserForm extends JFrame {
    // Fields for Sign-Up / Sign-In
    private JTextField nameField;
    private JTextField usernameField;
    private JComboBox<String> difficultyBox;
    private JPasswordField passwordField;

    // Database Connection
    private Connection conn;

    // Constructor for creating the GUI and initializing the database
    public UserForm() {
        // Set up the database
        initDatabase();

        // Create UI elements
        createSignupForm();

        // Set frame properties
        setTitle("User Data Entry Form");
        setSize(300, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Initialize SQLite Database
    private void initDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:user_data.db");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "difficulty TEXT NOT NULL, " +
                    "password TEXT NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create Sign Up form UI
    private void createSignupForm() {
        // Clear the content pane
        getContentPane().removeAll();

        // Panel to hold the components
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(15);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);

        JLabel difficultyLabel = new JLabel("Game Difficulty:");
        String[] difficulties = {"Easy", "Medium", "Hard"};
        difficultyBox = new JComboBox<>(difficulties);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        JButton signUpButton = new JButton("Sign Up");
        JButton switchToSignInButton = new JButton("Already have an account? Sign In");

        // Add action listeners
        signUpButton.addActionListener(new SignUpAction());
        switchToSignInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSignInForm();
            }
        });

        // Add components to the panel
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(difficultyLabel);
        panel.add(difficultyBox);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(signUpButton);
        panel.add(switchToSignInButton);

        // Add panel to frame
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    // Create Sign In form UI
    private void createSignInForm() {
        // Clear the content pane
        getContentPane().removeAll();

        // Panel to hold the components
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        JButton signInButton = new JButton("Sign In");
        JButton switchToSignUpButton = new JButton("Don't have an account? Sign Up");

        // Add action listeners
        signInButton.addActionListener(new SignInAction());
        switchToSignUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createSignupForm();
            }
        });

        // Add components to the panel
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(signInButton);
        panel.add(switchToSignUpButton);

        // Add panel to frame
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    // Action for sign-up
    class SignUpAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String username = usernameField.getText();
            String difficulty = (String) difficultyBox.getSelectedItem();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO users (name, username, difficulty, password) VALUES (?, ?, ?, ?)");
                    ps.setString(1, name);
                    ps.setString(2, username);
                    ps.setString(3, difficulty);
                    ps.setString(4, password);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Sign-up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    createSignInForm();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Username already exists. Please choose another.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Action for sign-in
    class SignInAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Both fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Welcome, " + rs.getString("name") + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        new UserForm();
    }
}
