@echo off
echo Parking Manager System - Source-based Build and Run Script
echo ===========================================================

REM Check if MySQL Connector JAR exists
set MYSQL_JAR=mysql-connector-j-9.4.0.jar

if not exist "%MYSQL_JAR%" (
    echo ERROR: MySQL Connector/J JAR file not found!
    echo Looking for: %MYSQL_JAR%
    echo.
    echo Please make sure the MySQL Connector JAR file is in this directory.
    echo You can copy it from the extracted folder or download from:
    echo https://dev.mysql.com/downloads/connector/j/
    pause
    exit /b 1
)

echo Found MySQL Connector: %MYSQL_JAR%
echo.

REM Create build directory if it doesn't exist
if not exist "build" mkdir build

REM Compile all Java files with proper classpath in dependency order
echo Compiling Java files from src directory...

echo Compiling model classes...
javac -cp ".;%MYSQL_JAR%" -sourcepath src -d build src/com/parkingmanager/model/*.java
if %errorlevel% neq 0 (
    echo ERROR: Model compilation failed!
    pause
    exit /b 1
)

echo Compiling utility classes...
javac -cp ".;%MYSQL_JAR%" -sourcepath src -d build src/com/parkingmanager/util/*.java
if %errorlevel% neq 0 (
    echo ERROR: Utility compilation failed!
    pause
    exit /b 1
)

echo Compiling DAO classes...
javac -cp "build;%MYSQL_JAR%" -sourcepath src -d build src/com/parkingmanager/dao/*.java
if %errorlevel% neq 0 (
    echo ERROR: DAO compilation failed!
    pause
    exit /b 1
)

echo Compiling service classes...
javac -cp "build;%MYSQL_JAR%" -sourcepath src -d build src/com/parkingmanager/service/*.java
if %errorlevel% neq 0 (
    echo ERROR: Service compilation failed!
    pause
    exit /b 1
)

echo Compiling GUI classes...
javac -cp "build;%MYSQL_JAR%" -sourcepath src -d build src/com/parkingmanager/gui/*.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    echo Make sure you have JDK installed and in your PATH.
    pause
    exit /b 1
)

echo Compilation successful!
echo.

REM Run the application
echo Starting Parking Manager System...
echo.
java -cp "build;%MYSQL_JAR%" com.parkingmanager.gui.ParkingManagerGUI

pause