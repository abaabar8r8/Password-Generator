@echo off
chcp 65001 >nul 2>&1
echo ========================================
echo 密碼產生器 - 資料結構實作專案
echo ========================================
echo.

echo 檢查Java環境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 錯誤: 找不到Java！請確認已安裝JDK 8或以上版本
    echo 下載地址: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

echo Java環境檢查通過！
echo.

echo 清理舊的編譯檔案...
del *.class >nul 2>&1

echo 編譯Java檔案 (使用UTF-8編碼)...
echo 編譯 HashFunction.java...
javac -encoding UTF-8 HashFunction.java
if %errorlevel% neq 0 (
    echo HashFunction.java 編譯失敗！
    pause
    exit /b 1
)

echo 編譯 PerformanceAnalyzer.java...
javac -encoding UTF-8 PerformanceAnalyzer.java
if %errorlevel% neq 0 (
    echo PerformanceAnalyzer.java 編譯失敗！
    pause
    exit /b 1
)

echo 編譯 PasswordGenerator.java...
javac -encoding UTF-8 PasswordGenerator.java
if %errorlevel% neq 0 (
    echo PasswordGenerator.java 編譯失敗！
    pause
    exit /b 1
)

echo 所有檔案編譯成功！
echo.

echo 啟動密碼產生器 (使用UTF-8編碼)...
echo.
echo 提示：
echo - 主程式將會開啟GUI視窗
echo - 可以點擊效能分析按鈕開啟分析工具
echo - 關閉視窗即可結束程式
echo.

java -Dfile.encoding=UTF-8 PasswordGenerator

echo.
echo 程式已結束
pause