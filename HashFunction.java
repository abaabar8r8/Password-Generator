import java.security.SecureRandom;
import java.util.Random;

/**
 * 修正版 Hash Function 實作
 * 解決了 Multiplication Hash Function 的問題
 */
abstract class HashFunction {
    protected SecureRandom secureRandom;
    protected Random pseudoRandom;
    
    public HashFunction() {
        this.secureRandom = new SecureRandom();
        this.pseudoRandom = new Random();
    }
    
    public abstract String generatePassword(String charSet, int length);
    protected abstract int hash(long input, int mod);
    public abstract String getAlgorithmName();
}

/**
 * 簡單除法 Hash Function (修正版)
 */
class SimpleHashFunction extends HashFunction {
    
    @Override
    public String generatePassword(String charSet, int length) {
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            // 使用更強的隨機性
            long input = System.nanoTime() + i * 1000L + secureRandom.nextInt(1000000);
            int index = hash(input, charSet.length());
            password.append(charSet.charAt(index));
        }
        
        return password.toString();
    }
    
    @Override
    protected int hash(long input, int mod) {
        return (int) Math.abs(input % mod);
    }
    
    @Override
    public String getAlgorithmName() {
        return "Simple Hash (Division Method)";
    }
}

/**
 * 乘法 Hash Function (修正版)
 * 使用整數運算避免浮點精度問題
 */
class MultiplicationHashFunction extends HashFunction {
    // 使用 Knuth 建議的乘數：A = 2^32 / φ (黃金比例)
    private static final long A = 2654435769L; 
    private static final long MASK = 0xFFFFFFFFL; // 2^32 - 1
    
    @Override
    public String generatePassword(String charSet, int length) {
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            // 每次產生新的隨機輸入
            long input = secureRandom.nextLong() + i * 1009L + System.nanoTime();
            int index = hash(Math.abs(input), charSet.length());
            password.append(charSet.charAt(index));
        }
        
        return password.toString();
    }
    
    @Override
    protected int hash(long input, int mod) {
        // 修正的乘法 hash：避免浮點運算
        // 1. 取低32位避免溢出
        long k = input & MASK;
        // 2. 乘以黃金比例常數
        long product = k * A;
        // 3. 取高32位作為hash值
        long hashValue = (product >> 32) % mod;
        return (int) Math.abs(hashValue);
    }
    
    @Override
    public String getAlgorithmName() {
        return "Multiplication Hash (Fixed)";
    }
}

/**
 * 通用 Hash Function (改進版)
 */
class UniversalHashFunction extends HashFunction {
    private static final long[] LARGE_PRIMES = {
        1000000007L, 1000000009L, 1000000021L, 1000000033L, 1000000087L
    };
    
    private long a, b, p;
    
    public UniversalHashFunction() {
        super();
        // 每次實例化時重新選擇參數
        this.p = LARGE_PRIMES[secureRandom.nextInt(LARGE_PRIMES.length)];
        this.a = secureRandom.nextLong() % (p - 1) + 1;
        this.b = secureRandom.nextLong() % p;
        if (this.a < 0) this.a += (p - 1);
        if (this.b < 0) this.b += p;
    }
    
    @Override
    public String generatePassword(String charSet, int length) {
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            // 使用強隨機源
            long input = secureRandom.nextLong() + i * 1009L + System.nanoTime();
            int index = hash(Math.abs(input), charSet.length());
            password.append(charSet.charAt(index));
        }
        
        return password.toString();
    }
    
    @Override
    protected int hash(long input, int mod) {
        // 通用 hash: h(k) = ((a*k + b) mod p) mod m
        // 使用模運算避免溢出
        long ak = ((a % p) * (input % p)) % p;
        long hashValue = ((ak + b) % p) % mod;
        return (int) Math.abs(hashValue);
    }
    
    @Override
    public String getAlgorithmName() {
        return "Universal Hash";
    }
    
    public String getParameters() {
        return String.format("a=%d, b=%d, p=%d", a, b, p);
    }
}

/**
 * Hash Function 測試和驗證工具
 */
class HashFunctionDebugger {
    
    public static void testHashFunction(HashFunction func, String charSet, int length) {
        System.out.println("=== " + func.getAlgorithmName() + " Debug Test ===");
        
        // 測試多次產生是否有變化
        System.out.println("Testing 10 password generations:");
        for (int i = 0; i < 10; i++) {
            String password = func.generatePassword(charSet, length);
            System.out.println((i+1) + ". " + password);
        }
        
        // 測試hash值分布
        System.out.println("\nTesting hash distribution (first 20 values):");
        for (int i = 0; i < 20; i++) {
            long input = System.nanoTime() + i * 1000;
            int hashValue = func.hash(input, charSet.length());
            char selectedChar = charSet.charAt(hashValue);
            System.out.printf("Input: %d -> Hash: %d -> Char: %c\n", 
                input, hashValue, selectedChar);
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        String testCharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        HashFunction[] functions = {
            new SimpleHashFunction(),
            new MultiplicationHashFunction(),
            new UniversalHashFunction()
        };
        
        for (HashFunction func : functions) {
            testHashFunction(func, testCharSet, 12);
        }
    }
}