@echo off
echo ========================================
echo Password Generator - English Version
echo ========================================
echo.

echo Checking Java environment...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java not found! Please install JDK 8 or higher
    echo Download: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

echo Java environment check passed!
echo.

echo Cleaning old compiled files...
del *.class >nul 2>&1

echo Compiling Java files...
echo Compiling HashFunction.java...
javac HashFunction.java
if %errorlevel% neq 0 (
    echo HashFunction.java compilation failed!
    pause
    exit /b 1
)

echo Compiling PerformanceAnalyzerEN.java...
javac PerformanceAnalyzerEN.java
if %errorlevel% neq 0 (
    echo PerformanceAnalyzerEN.java compilation failed!
    pause
    exit /b 1
)

echo Compiling PasswordGeneratorEN.java...
javac PasswordGeneratorEN.java
if %errorlevel% neq 0 (
    echo PasswordGeneratorEN.java compilation failed!
    pause
    exit /b 1
)

echo All files compiled successfully!
echo.

echo Starting Password Generator (English Version)...
echo.
echo Tips:
echo - Main program will open GUI window
echo - Click "Performance Analysis" button to open analysis tool
echo - Close window to exit program
echo.

java PasswordGeneratorEN

echo.
echo Program ended
pause