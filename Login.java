import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// PILLAR 1: ENCAPSULATION
class User {
    private String username; // Encapsulated data
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter - controlled access (Encapsulation)
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

// PILLAR 4: ABSTRACTION
interface Authenticatable {
    boolean authenticate(User user);
}

// PILLAR 2 & 3: INHERITANCE & POLYMORPHISM
class AuthService implements Authenticatable {
    private User validUser;

    public AuthService() {
        this.validUser = new User("admin", "1234");
    }

    @Override
    public boolean authenticate(User user) {
        return user.getUsername().equals(validUser.getUsername()) && 
               user.getPassword().equals(validUser.getPassword());
    }
}

public class Login extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private AuthService authService;

    public Login() {
        authService = new AuthService();
        initUI();
    }

    private void initUI() {
        setTitle("File Encryption - Login");
        setSize(380, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Secure Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        userField = new JTextField();
        passField = new JPasswordField();
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(33, 150, 243));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 12));

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(new JLabel(""));
        panel.add(loginBtn);
        add(panel, BorderLayout.CENTER);

        passField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) loginBtn.doClick();
            }
        });

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            User inputUser = new User(username, password);

            if (authService.authenticate(inputUser)) {
                // Connected to Main
                JOptionPane.showMessageDialog(this, "Welcome to File Encryption System!");
                new Main().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Error", JOptionPane.ERROR_MESSAGE);
                passField.setText("");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}