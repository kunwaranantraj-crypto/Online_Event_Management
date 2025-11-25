@echo off
echo Setting up Event Management System Database...

REM Check if MySQL is accessible
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo MySQL is not installed or not in PATH
    echo Please install MySQL Server and add it to your PATH
    echo Or run the SQL commands manually in MySQL Workbench
    pause
    exit /b 1
)

echo Creating database and tables...

REM Create database and run schema
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS event_management;"
if %errorlevel% neq 0 (
    echo Failed to create database. Please check your MySQL credentials.
    pause
    exit /b 1
)

echo Running schema script...
mysql -u root -p event_management < database/schema.sql
if %errorlevel% neq 0 (
    echo Failed to create tables. Please check the schema file.
    pause
    exit /b 1
)

echo Database setup completed successfully!
echo You can now run the application using run.bat

pause