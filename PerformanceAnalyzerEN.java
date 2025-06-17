import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class PerformanceAnalyzerEN extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JTextArea resultArea;
    private JProgressBar progressBar;
    private JButton analyzeButton;
    private JComboBox<String> testTypeBox;
    private JSpinner iterationsSpinner;
    
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    
    public PerformanceAnalyzerEN() {
        initializeGUI();
        setTitle("Hash Function Performance Analyzer");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
        
        // Result display area
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(new TitledBorder("Analysis Results"));
        add(scrollPane, BorderLayout.CENTER);
        
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");
        add(progressBar, BorderLayout.SOUTH);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Test Settings"));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Test type selection
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("Test Type:"), gbc);
        
        testTypeBox = new JComboBox<>(new String[]{
            "Hash Function Performance",
            "Hash Function Distribution", 
            "Data Structure Performance",
            "Complete Performance Report"
        });
        gbc.gridx = 1;
        panel.add(testTypeBox, gbc);
        
        // Iterations setting
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Test Iterations:"), gbc);
        
        iterationsSpinner = new JSpinner(new SpinnerNumberModel(1000, 100, 100000, 500));
        gbc.gridx = 1;
        panel.add(iterationsSpinner, gbc);
        
        // Analyze button
        analyzeButton = new JButton("Start Analysis");
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
        progressBar.setString("Analyzing...");
        resultArea.setText("");
        
        SwingWorker<String, String> worker = new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() throws Exception {
                StringBuilder result = new StringBuilder();
                String testType = (String) testTypeBox.getSelectedItem();
                int iterations = (Integer) iterationsSpinner.getValue();
                
                switch (testType) {
                    case "Hash Function Performance":
                        result.append(analyzeHashFunctionPerformance(iterations));
                        break;
                    case "Hash Function Distribution":
                        result.append(analyzeHashFunctionDistribution(iterations));
                        break;
                    case "Data Structure Performance":
                        result.append(analyzeDataStructurePerformance(iterations));
                        break;
                    case "Complete Performance Report":
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
                    progressBar.setString("Analysis Complete");
                } catch (Exception ex) {
                    resultArea.setText("Error during analysis: " + ex.getMessage());
                    progressBar.setString("Error Occurred");
                }
                analyzeButton.setEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    private String analyzeHashFunctionPerformance(int iterations) {
        StringBuilder result = new StringBuilder();
        result.append("=".repeat(60)).append("\n");
        result.append("Hash Function Performance Analysis\n");
        result.append("=".repeat(60)).append("\n\n");
        
        HashFunction[] functions = {
            new SimpleHashFunction(),
            new MultiplicationHashFunction(),
            new UniversalHashFunction()
        };
        
        int[] passwordLengths = {8, 16, 32};
        
        for (int length : passwordLengths) {
            result.append(String.format("Password Length: %d characters\n", length));
            result.append("-".repeat(40)).append("\n");
            
            for (HashFunction func : functions) {
                long startTime = System.nanoTime();
                
                for (int i = 0; i < iterations; i++) {
                    func.generatePassword(CHARSET, length);
                }
                
                long endTime = System.nanoTime();
                double elapsedMs = (endTime - startTime) / 1_000_000.0;
                double avgMs = elapsedMs / iterations;
                
                result.append(String.format("%-25s: Total %8.2f ms, Average %8.4f ms\n",
                    func.getAlgorithmName(), elapsedMs, avgMs));
            }
            result.append("\n");
        }
        
        return result.toString();
    }
    
    private String analyzeHashFunctionDistribution(int iterations) {
        StringBuilder result = new StringBuilder();
        result.append("=".repeat(60)).append("\n");
        result.append("Hash Function Distribution Analysis\n");
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
            
            for (int i = 0; i < iterations; i++) {
                long input = Math.abs(random.nextLong());
                int bucket = func.hash(input, buckets);
                distribution[bucket]++;
            }
            
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
            
            result.append(String.format("Expected: %.2f\n", expected));
            result.append(String.format("Min: %d, Max: %d\n", min, max));
            result.append(String.format("Standard Deviation: %.2f\n", stdDev));
            result.append(String.format("Uniformity: %.2f%%\n", uniformity));
            result.append("\n");
        }
        
        return result.toString();
    }
    
    private String analyzeDataStructurePerformance(int iterations) {
        StringBuilder result = new StringBuilder();
        result.append("=".repeat(60)).append("\n");
        result.append("Data Structure Performance Analysis\n");
        result.append("=".repeat(60)).append("\n\n");
        
        result.append("1. ArrayList vs LinkedList Performance\n");
        result.append("-".repeat(40)).append("\n");
        
        List<String> arrayList = new ArrayList<>();
        List<String> linkedList = new LinkedList<>();
        
        // Test insertion performance
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
        
        result.append(String.format("Insert %d elements:\n", iterations));
        result.append(String.format("ArrayList: %.2f ms\n", arrayListInsertTime / 1_000_000.0));
        result.append(String.format("LinkedList: %.2f ms\n", linkedListInsertTime / 1_000_000.0));
        result.append("\n");
        
        // Test random access performance
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
        
        result.append(String.format("Random access %d times:\n", searchIterations));
        result.append(String.format("ArrayList: %.2f ms\n", arrayListAccessTime / 1_000_000.0));
        result.append(String.format("LinkedList: %.2f ms\n", linkedListAccessTime / 1_000_000.0));
        result.append("\n");
        
        return result.toString();
    }
    
    private String generateCompleteReport(int iterations) {
        StringBuilder report = new StringBuilder();
        
        report.append("=".repeat(80)).append("\n");
        report.append("Password Generator - Complete Performance Report\n");
        report.append("=".repeat(80)).append("\n\n");
        
        report.append("Test Configuration:\n");
        report.append(String.format("- Test Iterations: %d\n", iterations));
        report.append(String.format("- Character Set Size: %d\n", CHARSET.length()));
        report.append(String.format("- Test Time: %s\n\n", new Date()));
        
        report.append(analyzeHashFunctionPerformance(iterations));
        report.append("\n");
        report.append(analyzeHashFunctionDistribution(iterations));
        report.append("\n");
        report.append(analyzeDataStructurePerformance(iterations));
        
        return report.toString();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PerformanceAnalyzerEN().setVisible(true);
        });
    }
}