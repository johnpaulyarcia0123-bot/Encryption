import java.awt.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;

// PILLAR 4: ABSTRACTION
interface Encryptable {
    String encrypt(String data, int key);
    String decrypt(String data, int key);
}

// PILLAR 2 & 3: INHERITANCE & POLYMORPHISM
class CaesarEncryption implements Encryptable {
    @Override
    public String encrypt(String data, int key) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : data.toCharArray()) {
            if (c >= 32 && c <= 126) {
                int shifted = c + key;
                if (shifted > 126) shifted = 32 + (shifted - 127);
                encrypted.append((char) shifted);
            } else {
                encrypted.append(c);
            }
        }
        return encrypted.toString();
    }

    @Override
    public String decrypt(String data, int key) {
        StringBuilder decrypted = new StringBuilder();
        for (char c : data.toCharArray()) {
            if (c >= 32 && c <= 126) {
                int shifted = c - key;
                if (shifted < 32) shifted = 127 - (32 - shifted);
                decrypted.append((char) shifted);
            } else {
                decrypted.append(c);
            }
        }
        return decrypted.toString();
    }
}

// PILLAR 1: ENCAPSULATION
class FileManager {
    private String filePath; // Encapsulated

    public FileManager(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() { return filePath; }

    public String readFile() throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            throw new FileNotFoundException("File not found!");
        }
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public void writeFile(String content, String newPath) throws IOException {
        Files.write(Paths.get(newPath), content.getBytes());
    }
}

public class Main extends JFrame {
    private JTextField filePathField;
    private JTextField keyField;
    private JTextArea resultArea;
    private CaesarEncryption encryptionService;
    private String loggedUser; // To store who logged in

    // Constructor now accepts username - connected from Login
    public Main(String username) {
        this.loggedUser = username;
        encryptionService = new CaesarEncryption();
        initUI();
    }

    // Default constructor if run directly
    public Main() {
        this("Guest");
    }

    private void initUI() {
        setTitle("File Encryption & Decryption System - User: " + loggedUser);
        setSize(650, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // WELCOME PANEL - This is what you requested
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(33, 150, 243));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        JLabel welcomeLabel = new JLabel("WELCOME TO FILE ENCRYPTION & DECRYPTION SYSTEM", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel userLabel = new JLabel("Logged in as: " + loggedUser, SwingConstants.CENTER);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setForeground(Color.WHITE);
        
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        welcomePanel.add(userLabel, BorderLayout.SOUTH);
        add(welcomePanel, BorderLayout.NORTH);

        // TOP PANEL FOR FILE SELECTION
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(new JLabel("Select File:"));
        JPanel filePanel = new JPanel(new BorderLayout());
        filePathField = new JTextField();
        filePathField.setEditable(false);
        JButton browseBtn = new JButton("Browse");
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(browseBtn, BorderLayout.EAST);
        topPanel.add(filePanel);
        topPanel.add(new JLabel("Encryption Key (1-25):"));
        keyField = new JTextField("3");
        topPanel.add(keyField);

        // Combine welcome + top panel
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(welcomePanel, BorderLayout.NORTH);
        northContainer.add(topPanel, BorderLayout.CENTER);
        add(northContainer, BorderLayout.NORTH);

        // CENTER
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBorder(BorderFactory.createTitledBorder("File Content Preview"));
        add(scroll, BorderLayout.CENTER);

        // BOTTOM
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton encryptBtn = new JButton("ENCRYPT FILE");
        JButton decryptBtn = new JButton("DECRYPT FILE");
        JButton logoutBtn = new JButton("LOGOUT");
        encryptBtn.setBackground(new Color(76, 175, 80));
        encryptBtn.setForeground(Color.WHITE);
        decryptBtn.setBackground(new Color(33, 150, 243));
        decryptBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(244, 67, 54));
        logoutBtn.setForeground(Color.WHITE);
        bottomPanel.add(encryptBtn);
        bottomPanel.add(decryptBtn);
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        browseBtn.addActionListener(e -> browseFile());
        encryptBtn.addActionListener(e -> processFile(true));
        decryptBtn.addActionListener(e -> processFile(false));
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                new Login().setVisible(true);
                dispose();
            }
        });
    }

    private void browseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
            try {
                FileManager fm = new FileManager(selectedFile.getAbsolutePath());
                resultArea.setText(fm.readFile());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
            }
        }
    }

    private void processFile(boolean isEncrypt) {
        String path = filePathField.getText();
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a file first!");
            return;
        }
        try {
            int key = Integer.parseInt(keyField.getText());
            if (key < 1 || key > 25) {
                JOptionPane.showMessageDialog(this, "Key must be between 1 and 25!");
                return;
            }
            FileManager fm = new FileManager(path);
            String content = fm.readFile();
            String processedContent;
            String newPath;

            if (isEncrypt) {
                processedContent = encryptionService.encrypt(content, key);
                newPath = path.replace(".txt", "_encrypted.txt");
                JOptionPane.showMessageDialog(this, "File Encrypted Successfully!\nSaved as: " + new File(newPath).getName());
            } else {
                processedContent = encryptionService.decrypt(content, key);
                newPath = path.contains("_encrypted") ? path.replace("_encrypted.txt", "_decrypted.txt") : path.replace(".txt", "_decrypted.txt");
                JOptionPane.showMessageDialog(this, "File Decrypted Successfully!\nSaved as: " + new File(newPath).getName());
            }
            fm.writeFile(processedContent, newPath);
            resultArea.setText(processedContent);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}