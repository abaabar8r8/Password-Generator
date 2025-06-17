import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class PasswordGenerator extends JFrame {
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
    
    // 資料結構 - 密碼歷史紀錄
    private List<String> passwordHistory;
    private Queue<String> recentPasswords;
    private static final int MAX_RECENT_PASSWORDS = 10;
    
    // Hash Functions for different generation strategies
    private HashFunction[] hashFunctions;
    private SecureRandom secureRandom;
    
    // 字符集定義
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    
    public PasswordGenerator() {
        initializeDataStructures();
        initializeGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("密碼產生器 - 資料結構實作");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initializeDataStructures() {
        // 使用 ArrayList 儲存完整歷史
        passwordHistory = new ArrayList<>();
        
        // 使用 LinkedList 實作的 Queue 儲存最近密碼
        recentPasswords = new LinkedList<>();
        
        // 初始化不同的 Hash Function
        hashFunctions = new HashFunction[]{
            new SimpleHashFunction(),
            new MultiplicationHashFunction(),
            new UniversalHashFunction()
        };
        
        secureRandom = new SecureRandom();
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // 密碼顯示區域
        createPasswordDisplayPanel(mainPanel, gbc);
        
        // 設定面板
        createSettingsPanel(mainPanel, gbc);
        
        // 按鈕面板
        createButtonPanel(mainPanel, gbc);
        
        // 強度顯示面板
        createStrengthPanel(mainPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 歷史紀錄面板
        createHistoryPanel();
    }
    
    private void createPasswordDisplayPanel(JPanel parent, GridBagConstraints gbc) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("產生的密碼"));
        
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
        settingsPanel.setBorder(new TitledBorder("密碼設定"));
        GridBagConstraints sgbc = new GridBagConstraints();
        
        // 密碼長度
        lengthLabel = new JLabel("密碼長度: 12");
        lengthSlider = new JSlider(4, 50, 12);
        lengthSlider.addChangeListener(e -> 
            lengthLabel.setText("密碼長度: " + lengthSlider.getValue())
        );
        
        sgbc.gridx = 0; sgbc.gridy = 0;
        sgbc.anchor = GridBagConstraints.WEST;
        sgbc.insets = new Insets(5, 5, 5, 5);
        settingsPanel.add(lengthLabel, sgbc);
        
        sgbc.gridx = 1; sgbc.gridy = 0;
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.weightx = 1.0;
        settingsPanel.add(lengthSlider, sgbc);
        
        // 字符類型選擇
        uppercaseBox = new JCheckBox("大寫字母 (A-Z)", true);
        lowercaseBox = new JCheckBox("小寫字母 (a-z)", true);
        numbersBox = new JCheckBox("數字 (0-9)", true);
        symbolsBox = new JCheckBox("特殊符號", false);
        
        sgbc.gridx = 0; sgbc.gridy = 1; sgbc.gridwidth = 2;
        sgbc.weightx = 0;
        settingsPanel.add(uppercaseBox, sgbc);
        sgbc.gridy = 2;
        settingsPanel.add(lowercaseBox, sgbc);
        sgbc.gridy = 3;
        settingsPanel.add(numbersBox, sgbc);
        sgbc.gridy = 4;
        settingsPanel.add(symbolsBox, sgbc);
        
        // Hash Function 選擇
        JLabel hashLabel = new JLabel("產生演算法:");
        hashFunctionBox = new JComboBox<>(new String[]{
            "簡單 Hash Function",
            "乘法 Hash Function", 
            "通用 Hash Function"
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
        
        generateButton = new JButton("產生密碼");
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.addActionListener(new GeneratePasswordListener());
        
        copyButton = new JButton("複製到剪貼簿");
        copyButton.addActionListener(new CopyPasswordListener());
        copyButton.setEnabled(false);
        
        clearHistoryButton = new JButton("清除歷史");
        clearHistoryButton.addActionListener(e -> clearHistory());
        
        JButton analyzeButton = new JButton("效能分析");
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
        strengthPanel.setBorder(new TitledBorder("密碼強度"));
        
        strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(true);
        strengthBar.setString("0%");
        
        strengthLabel = new JLabel("未評估", SwingConstants.CENTER);
        
        strengthPanel.add(strengthBar, BorderLayout.CENTER);
        strengthPanel.add(strengthLabel, BorderLayout.SOUTH);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        parent.add(strengthPanel, gbc);
    }
    
    private void createHistoryPanel() {
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(new TitledBorder("密碼歷史紀錄"));
        
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
            JOptionPane.showMessageDialog(this, "請至少選擇一種字符類型！", 
                "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int length = lengthSlider.getValue();
        int hashIndex = hashFunctionBox.getSelectedIndex();
        
        String password = hashFunctions[hashIndex].generatePassword(charSet, length);
        
        passwordField.setText(password);
        copyButton.setEnabled(true);
        
        // 更新歷史紀錄 (ArrayList)
        passwordHistory.add(password);
        
        // 更新最近密碼佇列 (Queue)
        if (recentPasswords.size() >= MAX_RECENT_PASSWORDS) {
            recentPasswords.poll(); // 移除最舊的
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
        display.append("=== 最近 ").append(recentPasswords.size()).append(" 個密碼 ===\n");
        
        int count = 1;
        for (String password : recentPasswords) {
            display.append(count++).append(". ").append(password).append("\n");
        }
        
        display.append("\n=== 總共產生了 ").append(passwordHistory.size()).append(" 個密碼 ===");
        
        historyArea.setText(display.toString());
        historyArea.setCaretPosition(0);
    }
    
    private void evaluatePasswordStrength(String password) {
        int score = 0;
        String feedback = "";
        
        // 長度評分
        if (password.length() >= 12) score += 25;
        else if (password.length() >= 8) score += 15;
        else score += 5;
        
        // 字符多樣性評分
        if (password.matches(".*[A-Z].*")) score += 15;
        if (password.matches(".*[a-z].*")) score += 15;
        if (password.matches(".*[0-9].*")) score += 15;
        if (password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|;:,.<>?].*")) score += 20;
        
        // 重複字符檢查
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : password.toCharArray()) {
            uniqueChars.add(c);
        }
        if (uniqueChars.size() > password.length() * 0.8) score += 10;
        
        // 更新顯示
        strengthBar.setValue(Math.min(score, 100));
        strengthBar.setString(score + "%");
        
        if (score >= 80) {
            feedback = "非常強";
            strengthBar.setForeground(Color.GREEN);
        } else if (score >= 60) {
            feedback = "強";
            strengthBar.setForeground(Color.ORANGE);
        } else if (score >= 40) {
            feedback = "中等";
            strengthBar.setForeground(Color.YELLOW);
        } else {
            feedback = "弱";
            strengthBar.setForeground(Color.RED);
        }
        
        strengthLabel.setText(feedback + " (評分: " + score + "/100)");
    }
    
    private void copyToClipboard() {
        String password = passwordField.getText();
        if (!password.isEmpty()) {
            StringSelection selection = new StringSelection(password);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            
            JOptionPane.showMessageDialog(this, "密碼已複製到剪貼簿！", 
                "成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearHistory() {
        passwordHistory.clear();
        recentPasswords.clear();
        updateHistoryDisplay();
        
        JOptionPane.showMessageDialog(this, "歷史紀錄已清除！", 
            "成功", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openPerformanceAnalyzer() {
        new PerformanceAnalyzer().setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 直接啟動程式，使用預設外觀
            new PasswordGenerator().setVisible(true);
        });
    }
}