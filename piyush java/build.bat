@echo off
echo Building Event Management System...

REM Check if Maven is installed
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    pause
    exit /b 1
)

echo Cleaning previous builds...
mvn clean

echo Compiling application...
mvn compile

if %errorlevel% neq 0 (
    echo Compilation failed
    pause
    exit /b 1
)

echo Creating executable JAR...
mvn package

if %errorlevel% neq 0 (
    echo Packaging failed
    pause
    exit /b 1
)

echo Build completed successfully!
echo Executable JAR created in target/ directory
echo You can run it with: java -jar target/event-management-system-1.0.0.jar

pause