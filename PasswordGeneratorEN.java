import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.util.*;
import java.util.List;
import java.security.SecureRandom;

public class PasswordGeneratorEN extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // GUI Components
    private JTextField passwordField;
    private JSlider lengthSlider;
    private JLabel lengthLabel;
    private JCheckBox uppercaseBox, lowercaseBox, numbersBox, symbolsBox;
    private JTextArea historyArea;
    private JProgressBar strengthBar;
    private JLabel strengthLabel;
    private JComboBox<String> hashFunctionBox;
    private JButton generateButton, copyButton, clearHistoryButton;
    
    // Data Structures - Password History
    private List<String> passwordHistory;
    private Queue<String> recentPasswords;
    private static final int MAX_RECENT_PASSWORDS = 10;
    
    // Hash Functions for different generation strategies
    private HashFunction[] hashFunctions;
    private SecureRandom secureRandom;
    
    // Character sets
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    
    public PasswordGeneratorEN() {
        initializeDataStructures();
        initializeGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Password Generator - Data Structure Implementation");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initializeDataStructures() {
        // Using ArrayList for complete history
        passwordHistory = new ArrayList<>();
        
        // Using LinkedList as Queue for recent passwords
        recentPasswords = new LinkedList<>();
        
        // Initialize different Hash Functions
        hashFunctions = new HashFunction[]{
            new SimpleHashFunction(),
            new MultiplicationHashFunction(),
            new UniversalHashFunction()
        };
        
        secureRandom = new SecureRandom();
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Password display area
        createPasswordDisplayPanel(mainPanel, gbc);
        
        // Settings panel
        createSettingsPanel(mainPanel, gbc);
        
        // Button panel
        createButtonPanel(mainPanel, gbc);
        
        // Strength display panel
        createStrengthPanel(mainPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // History panel
        createHistoryPanel();
    }
    
    private void createPasswordDisplayPanel(JPanel parent, GridBagConstraints gbc) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Generated Password"));
        
        passwordField = new JTextField();
        passwordField.setFont(new Font("Courier New", Font.BOLD, 16));
        passwordField.setEditable(false);
        passwordField.setBackground(Color.WHITE);
        
        panel.add(passwordField, BorderLayout.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        parent.add(panel, gbc);
    }
    
    private void createSettingsPanel(JPanel parent, GridBagConstraints gbc) {
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(new TitledBorder("Password Settings"));
        GridBagConstraints sgbc = new GridBagConstraints();
        
        // Password length
        lengthLabel = new JLabel("Password Length: 12");
        lengthSlider = new JSlider(4, 50, 12);
        lengthSlider.addChangeListener(e -> 
            lengthLabel.setText("Password Length: " + lengthSlider.getValue())
        );
        
        sgbc.gridx = 0; sgbc.gridy = 0;
        sgbc.anchor = GridBagConstraints.WEST;
        sgbc.insets = new Insets(5, 5, 5, 5);
        settingsPanel.add(lengthLabel, sgbc);
        
        sgbc.gridx = 1; sgbc.gridy = 0;
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.weightx = 1.0;
        settingsPanel.add(lengthSlider, sgbc);
        
        // Character type selection
        uppercaseBox = new JCheckBox("Uppercase Letters (A-Z)", true);
        lowercaseBox = new JCheckBox("Lowercase Letters (a-z)", true);
        numbersBox = new JCheckBox("Numbers (0-9)", true);
        symbolsBox = new JCheckBox("Special Symbols", false);
        
        sgbc.gridx = 0; sgbc.gridy = 1; sgbc.gridwidth = 2;
        sgbc.weightx = 0;
        settingsPanel.add(uppercaseBox, sgbc);
        sgbc.gridy = 2;
        settingsPanel.add(lowercaseBox, sgbc);
        sgbc.gridy = 3;
        settingsPanel.add(numbersBox, sgbc);
        sgbc.gridy = 4;
        settingsPanel.add(symbolsBox, sgbc);
        
        // Hash Function selection
        JLabel hashLabel = new JLabel("Generation Algorithm:");
        hashFunctionBox = new JComboBox<>(new String[]{
            "Simple Hash Function",
            "Multiplication Hash Function", 
            "Universal Hash Function"
        });
        
        sgbc.gridx = 0; sgbc.gridy = 5; sgbc.gridwidth = 1;
        settingsPanel.add(hashLabel, sgbc);
        sgbc.gridx = 1;
        settingsPanel.add(hashFunctionBox, sgbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        parent.add(settingsPanel, gbc);
    }
    
    private void createButtonPanel(JPanel parent, GridBagConstraints gbc) {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        generateButton = new JButton("Generate Password");
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.addActionListener(new GeneratePasswordListener());
        
        copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener(new CopyPasswordListener());
        copyButton.setEnabled(false);
        
        clearHistoryButton = new JButton("Clear History");
        clearHistoryButton.addActionListener(e -> clearHistory());
        
        JButton analyzeButton = new JButton("Performance Analysis");
        analyzeButton.addActionListener(e -> openPerformanceAnalyzer());
        
        buttonPanel.add(generateButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(clearHistoryButton);
        buttonPanel.add(analyzeButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        parent.add(buttonPanel, gbc);
    }
    
    private void createStrengthPanel(JPanel parent, GridBagConstraints gbc) {
        JPanel strengthPanel = new JPanel(new BorderLayout());
        strengthPanel.setBorder(new TitledBorder("Password Strength"));
        
        strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(true);
        strengthBar.setString("0%");
        
        strengthLabel = new JLabel("Not Evaluated", SwingConstants.CENTER);
        
        strengthPanel.add(strengthBar, BorderLayout.CENTER);
        strengthPanel.add(strengthLabel, BorderLayout.SOUTH);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        parent.add(strengthPanel, gbc);
    }
    
    private void createHistoryPanel() {
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(new TitledBorder("Password History"));
        
        historyArea = new JTextArea(8, 30);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(historyArea);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(historyPanel, BorderLayout.SOUTH);
    }
    
    private class GeneratePasswordListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            generatePassword();
        }
    }
    
    private class CopyPasswordListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            copyToClipboard();
        }
    }
    
    private void generatePassword() {
        String charSet = buildCharacterSet();
        if (charSet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one character type!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int length = lengthSlider.getValue();
        int hashIndex = hashFunctionBox.getSelectedIndex();
        
        String password = hashFunctions[hashIndex].generatePassword(charSet, length);
        
        passwordField.setText(password);
        copyButton.setEnabled(true);
        
        // Update history (ArrayList)
        passwordHistory.add(password);
        
        // Update recent passwords queue
        if (recentPasswords.size() >= MAX_RECENT_PASSWORDS) {
            recentPasswords.poll(); // Remove oldest
        }
        recentPasswords.offer(password);
        
        updateHistoryDisplay();
        evaluatePasswordStrength(password);
    }
    
    private String buildCharacterSet() {
        StringBuilder charSet = new StringBuilder();
        if (uppercaseBox.isSelected()) charSet.append(UPPERCASE);
        if (lowercaseBox.isSelected()) charSet.append(LOWERCASE);
        if (numbersBox.isSelected()) charSet.append(NUMBERS);
        if (symbolsBox.isSelected()) charSet.append(SYMBOLS);
        return charSet.toString();
    }
    
    private void updateHistoryDisplay() {
        StringBuilder display = new StringBuilder();
        display.append("=== Recent ").append(recentPasswords.size()).append(" Passwords ===\n");
        
        int count = 1;
        for (String password : recentPasswords) {
            display.append(count++).append(". ").append(password).append("\n");
        }
        
        display.append("\n=== Total Generated: ").append(passwordHistory.size()).append(" Passwords ===");
        
        historyArea.setText(display.toString());
        historyArea.setCaretPosition(0);
    }
    
    private void evaluatePasswordStrength(String password) {
        int score = 0;
        String feedback = "";
        
        // Length scoring
        if (password.length() >= 12) score += 25;
        else if (password.length() >= 8) score += 15;
        else score += 5;
        
        // Character diversity scoring
        if (password.matches(".*[A-Z].*")) score += 15;
        if (password.matches(".*[a-z].*")) score += 15;
        if (password.matches(".*[0-9].*")) score += 15;
        if (password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|;:,.<>?].*")) score += 20;
        
        // Duplicate character check
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : password.toCharArray()) {
            uniqueChars.add(c);
        }
        if (uniqueChars.size() > password.length() * 0.8) score += 10;
        
        // Update display
        strengthBar.setValue(Math.min(score, 100));
        strengthBar.setString(score + "%");
        
        if (score >= 80) {
            feedback = "Very Strong";
            strengthBar.setForeground(Color.GREEN);
        } else if (score >= 60) {
            feedback = "Strong";
            strengthBar.setForeground(Color.ORANGE);
        } else if (score >= 40) {
            feedback = "Medium";
            strengthBar.setForeground(Color.YELLOW);
        } else {
            feedback = "Weak";
            strengthBar.setForeground(Color.RED);
        }
        
        strengthLabel.setText(feedback + " (Score: " + score + "/100)");
    }
    
    private void copyToClipboard() {
        String password = passwordField.getText();
        if (!password.isEmpty()) {
            StringSelection selection = new StringSelection(password);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            
            JOptionPane.showMessageDialog(this, "Password copied to clipboard!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearHistory() {
        passwordHistory.clear();
        recentPasswords.clear();
        updateHistoryDisplay();
        
        JOptionPane.showMessageDialog(this, "History cleared!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openPerformanceAnalyzer() {
        new PerformanceAnalyzerEN().setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PasswordGeneratorEN().setVisible(true);
        });
    }
}