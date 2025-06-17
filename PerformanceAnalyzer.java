import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * 效能分析器 - 分析不同資料結構和Hash Function的效能
 */
public class PerformanceAnalyzer extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JTextArea resultArea;
    private JProgressBar progressBar;
    private JButton analyzeButton;
    private JComboBox<String> testTypeBox;
    private JSpinner iterationsSpinner;
    
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    
    public PerformanceAnalyzer() {
        initializeGUI();
        setTitle("Hash Function 效能分析器");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // 控制面板
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
        
        // 結果顯示區域
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(new TitledBorder("分析結果"));
        add(scrollPane, BorderLayout.CENTER);
        
        // 進度條
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("準備就緒");
        add(progressBar, BorderLayout.SOUTH);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("測試設定"));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // 測試類型選擇
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("測試類型:"), gbc);
        
        testTypeBox = new JComboBox<>(new String[]{
            "Hash Function 效能比較",
            "Hash Function 分布分析", 
            "資料結構效能比較",
            "完整效能報告"
        });
        gbc.gridx = 1;
        panel.add(testTypeBox, gbc);
        
        // 迭代次數設定
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("測試次數:"), gbc);
        
        iterationsSpinner = new JSpinner(new SpinnerNumberModel(1000, 100, 100000, 500));
        gbc.gridx = 1;
        panel.add(iterationsSpinner, gbc);
        
        // 分析按鈕
        analyzeButton = new JButton("開始分析");
        analyzeButton.addActionListener(new AnalyzeListener());
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(analyzeButton, gbc);
        
        return panel;
    }
    
    private class AnalyzeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            runAnalysis();
        }
    }
    
    private void runAnalysis() {
        analyzeButton.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setString("分析中...");
        resultArea.setText("");
        
        // 使用 SwingWorker 避免凍結UI
        SwingWorker<String, String> worker = new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() throws Exception {
                StringBuilder result = new StringBuilder();
                String testType = (String) testTypeBox.getSelectedItem();
                int iterations = (Integer) iterationsSpinner.getValue();
                
                switch (testType) {
                    case "Hash Function 效能比較":
                        result.append(analyzeHashFunctionPerformance(iterations));
                        break;
                    case "Hash Function 分布分析":
                        result.append(analyzeHashFunctionDistribution(iterations));
                        break;
                    case "資料結構效能比較":
                        result.append(analyzeDataStructurePerformance(iterations));
                        break;
                    case "完整效能報告":
                        result.append(generateCompleteReport(iterations));
                        break;
                }
                
                return result.toString();
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    resultArea.append(chunk);
                    resultArea.setCaretPosition(resultArea.getDocument().getLength());
                }
            }
            
            @Override
            protected void done() {
                try {
                    String result = get();
                    resultArea.setText(result);
                    progressBar.setValue(100);
                    progressBar.setString("分析完成");
                } catch (Exception ex) {
                    resultArea.setText("分析時發生錯誤: " + ex.getMessage());
                    progressBar.setString("發生錯誤");
                }
                analyzeButton.setEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    private String analyzeHashFunctionPerformance(int iterations) {
        StringBuilder result = new StringBuilder();
        result.append("=".repeat(60)).append("\n");
        result.append("Hash Function 效能比較分析\n");
        result.append("=".repeat(60)).append("\n\n");
        
        HashFunction[] functions = {
            new SimpleHashFunction(),
            new MultiplicationHashFunction(),
            new UniversalHashFunction()
        };
        
        int[] passwordLengths = {8, 16, 32};
        
        for (int length : passwordLengths) {
            result.append(String.format("密碼長度: %d 字符\n", length));
            result.append("-".repeat(40)).append("\n");
            
            for (HashFunction func : functions) {
                long startTime = System.nanoTime();
                
                for (int i = 0; i < iterations; i++) {
                    func.generatePassword(CHARSET, length);
                }
                
                long endTime = System.nanoTime();
                double elapsedMs = (endTime - startTime) / 1_000_000.0;
                double avgMs = elapsedMs / iterations;
                
                result.append(String.format("%-25s: 總時間 %8.2f ms, 平均 %8.4f ms\n",
                    func.getAlgorithmName(), elapsedMs, avgMs));
            }
            result.append("\n");
        }
        
        return result.toString();
    }
    
    private String analyzeHashFunctionDistribution(int iterations) {
        StringBuilder result = new StringBuilder();
        result.append("=".repeat(60)).append("\n");
        result.append("Hash Function 分布均勻性分析\n");
        result.append("=".repeat(60)).append("\n\n");
        
        HashFunction[] functions = {
            new SimpleHashFunction(),
            new MultiplicationHashFunction(),
            new UniversalHashFunction()
        };
        
        int buckets = 100;
        Random random = new Random();
        
        for (HashFunction func : functions) {
            result.append(func.getAlgorithmName()).append("\n");
            result.append("-".repeat(40)).append("\n");
            
            int[] distribution = new int[buckets];
            
            // 收集分布數據
            for (int i = 0; i < iterations; i++) {
                long input = Math.abs(random.nextLong());
                int bucket = func.hash(input, buckets);
                distribution[bucket]++;
            }
            
            // 計算統計
            double expected = (double) iterations / buckets;
            double variance = 0;
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            
            for (int count : distribution) {
                variance += Math.pow(count - expected, 2);
                min = Math.min(min, count);
                max = Math.max(max, count);
            }
            
            variance /= buckets;
            double stdDev = Math.sqrt(variance);
            double uniformity = 100.0 * (1.0 - stdDev / expected);
            
            result.append(String.format("期望值: %.2f\n", expected));
            result.append(String.format("最小值: %d, 最大值: %d\n", min, max));
            result.append(String.format("標準差: %.2f\n", stdDev));
            result.append(String.format("均勻性: %.2f%%\n", uniformity));
            result.append("\n");
        }
        
        return result.toString();
    }
    
    private String analyzeDataStructurePerformance(int iterations) {
        StringBuilder result = new StringBuilder();
        result.append("=".repeat(60)).append("\n");
        result.append("資料結構效能比較分析\n");
        result.append("=".repeat(60)).append("\n\n");
        
        // ArrayList vs LinkedList 效能比較
        result.append("1. ArrayList vs LinkedList 效能比較\n");
        result.append("-".repeat(40)).append("\n");
        
        List<String> arrayList = new ArrayList<>();
        List<String> linkedList = new LinkedList<>();
        
        // 測試插入效能
        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            arrayList.add("password" + i);
        }
        long arrayListInsertTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            linkedList.add("password" + i);
        }
        long linkedListInsertTime = System.nanoTime() - startTime;
        
        result.append(String.format("插入 %d 個元素:\n", iterations));
        result.append(String.format("ArrayList: %.2f ms\n", arrayListInsertTime / 1_000_000.0));
        result.append(String.format("LinkedList: %.2f ms\n", linkedListInsertTime / 1_000_000.0));
        result.append("\n");
        
        // 測試隨機存取效能
        Random random = new Random();
        int searchIterations = Math.min(iterations, 1000);
        
        startTime = System.nanoTime();
        for (int i = 0; i < searchIterations; i++) {
            int index = random.nextInt(arrayList.size());
            arrayList.get(index);
        }
        long arrayListAccessTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < searchIterations; i++) {
            int index = random.nextInt(linkedList.size());
            ((LinkedList<String>) linkedList).get(index);
        }
        long linkedListAccessTime = System.nanoTime() - startTime;
        
        result.append(String.format("隨機存取 %d 次:\n", searchIterations));
        result.append(String.format("ArrayList: %.2f ms\n", arrayListAccessTime / 1_000_000.0));
        result.append(String.format("LinkedList: %.2f ms\n", linkedListAccessTime / 1_000_000.0));
        result.append("\n");
        
        // HashMap vs TreeMap 效能比較
        result.append("2. HashMap vs TreeMap 效能比較\n");
        result.append("-".repeat(40)).append("\n");
        
        Map<String, String> hashMap = new HashMap<>();
        Map<String, String> treeMap = new TreeMap<>();
        
        // 測試插入效能
        startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            hashMap.put("key" + i, "password" + i);
        }
        long hashMapInsertTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            treeMap.put("key" + i, "password" + i);
        }
        long treeMapInsertTime = System.nanoTime() - startTime;
        
        result.append(String.format("插入 %d 個鍵值對:\n", iterations));
        result.append(String.format("HashMap: %.2f ms\n", hashMapInsertTime / 1_000_000.0));
        result.append(String.format("TreeMap: %.2f ms\n", treeMapInsertTime / 1_000_000.0));
        result.append("\n");
        
        // 測試查找效能
        startTime = System.nanoTime();
        for (int i = 0; i < searchIterations; i++) {
            int key = random.nextInt(iterations);
            hashMap.get("key" + key);
        }
        long hashMapSearchTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < searchIterations; i++) {
            int key = random.nextInt(iterations);
            treeMap.get("key" + key);
        }
        long treeMapSearchTime = System.nanoTime() - startTime;
        
        result.append(String.format("隨機查找 %d 次:\n", searchIterations));
        result.append(String.format("HashMap: %.2f ms\n", hashMapSearchTime / 1_000_000.0));
        result.append(String.format("TreeMap: %.2f ms\n", treeMapSearchTime / 1_000_000.0));
        
        return result.toString();
    }
    
    private String generateCompleteReport(int iterations) {
        StringBuilder report = new StringBuilder();
        
        report.append("=".repeat(80)).append("\n");
        report.append("密碼產生器 - 完整效能分析報告\n");
        report.append("=".repeat(80)).append("\n\n");
        
        report.append("測試設定:\n");
        report.append(String.format("- 測試次數: %d\n", iterations));
        report.append(String.format("- 字符集大小: %d\n", CHARSET.length()));
        report.append(String.format("- 測試時間: %s\n\n", new Date()));
        
        report.append(analyzeHashFunctionPerformance(iterations));
        report.append("\n");
        report.append(analyzeHashFunctionDistribution(iterations));
        report.append("\n");
        report.append(analyzeDataStructurePerformance(iterations));
        
        report.append("\n").append("=".repeat(80)).append("\n");
        report.append("結論與建議:\n");
        report.append("=".repeat(80)).append("\n");
        report.append("1. Hash Function 效能: 簡單Hash通常最快，但分布可能不夠均勻\n");
        report.append("2. 通用Hash提供最佳的安全性和分布均勻性\n");
        report.append("3. ArrayList 在隨機存取上優於 LinkedList\n");
        report.append("4. HashMap 在一般查找上優於 TreeMap\n");
        report.append("5. 實際應用應根據安全需求選擇適當的演算法\n");
        
        return report.toString();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 直接啟動程式，使用預設外觀
            new PerformanceAnalyzer().setVisible(true);
        });
    }
}