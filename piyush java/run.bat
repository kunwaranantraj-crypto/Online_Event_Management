@echo off
echo Starting Event Management System...

REM Check if Maven is installed
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    pause
    exit /b 1
)

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java is not installed or not in PATH
    echo Please install Java 11 or higher and add it to your PATH
    pause
    exit /b 1
)

REM Compile and run the application
echo Compiling application...
mvn clean compile

if %errorlevel% neq 0 (
    echo Compilation failed
    pause
    exit /b 1
)

echo Running Event Management System...
mvn javafx:run

pause