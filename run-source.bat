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

REM Copy config file to build directory for runtime access
if exist "config.properties" (
    copy /Y "config.properties" "build\config.properties" >nul
    echo Copied config.properties to build directory
)

REM Compile all Java files with proper classpath in dependency order
echo Compiling Java files from src directory...

echo Compiling enum classes...
javac -cp ".;%MYSQL_JAR%" --release 8 -Xlint:-options -sourcepath src -d build src/com/parkinglotmanager/enums/*.java
if %errorlevel% neq 0 (
    echo ERROR: Enum compilation failed!
    pause
    exit /b 1
)

echo Compiling utility classes...
javac -cp "build;%MYSQL_JAR%" --release 8 -Xlint:-options -sourcepath src -d build src/com/parkinglotmanager/util/*.java
if %errorlevel% neq 0 (
    echo ERROR: Utility compilation failed!
    pause
    exit /b 1
)

echo Compiling model classes...
javac -cp "build;%MYSQL_JAR%" --release 8 -Xlint:-options -sourcepath src -d build src/com/parkinglotmanager/model/*.java
if %errorlevel% neq 0 (
    echo ERROR: Model compilation failed!
    pause
    exit /b 1
)

echo Compiling test classes...
javac -cp "build;%MYSQL_JAR%" --release 8 -Xlint:-options -sourcepath src -d build src/com/parkinglotmanager/test/*.java
if %errorlevel% neq 0 (
    echo ERROR: Test compilation failed!
    pause
    exit /b 1
)

echo Compiling GUI classes...
javac -cp "build;%MYSQL_JAR%" --release 8 -Xlint:-options -sourcepath src -d build src/com/parkinglotmanager/gui/*.java
if %errorlevel% neq 0 (
    echo ERROR: GUI compilation failed!
    pause
    exit /b 1
)

echo.
echo Compiling OLD package (com.parkingmanager)...
echo Compiling old model classes...
javac -cp ".;%MYSQL_JAR%" --release 8 -Xlint:-options -sourcepath src -d build src/com/parkingmanager/model/*.java
if %errorlevel% neq 0 (
    echo WARNING: Old model compilation failed (this is expected)
)

echo Compiling old utility classes...
javac -cp ".;%MYSQL_JAR%" --release 8 -Xlint:-options -sourcepath src -d build src/com/parkingmanager/util/*.java
if %errorlevel% neq 0 (
    echo WARNING: Old utility compilation failed
)

echo Compiling old DAO classes...
javac -cp "build;%MYSQL_JAR%" --release 8 -Xlint:-options -sourcepath src -d build src/com/parkingmanager/dao/*.java
if %errorlevel% neq 0 (
    echo WARNING: Old DAO compilation failed
)

echo Compiling old service classes...
javac -cp "build;%MYSQL_JAR%" --release 8 -Xlint:-options -sourcepath src -d build src/com/parkingmanager/service/*.java
if %errorlevel% neq 0 (
    echo WARNING: Old service compilation failed
)

echo Compiling old GUI classes...
javac -cp "build;%MYSQL_JAR%" --release 8 -Xlint:-options -sourcepath src -d build src/com/parkingmanager/gui/*.java
if %errorlevel% neq 0 (
    echo WARNING: Old GUI compilation failed (this is expected)
)

echo.
echo Compilation complete!
echo.
echo Choose what to run:
echo 1. Model Test (console)
echo 2. GUI Application (window)
echo.
set /p choice="Enter your choice (1 or 2): "

if "%choice%"=="1" (
    echo Starting Model Test...
    echo.
    java -cp "build;%MYSQL_JAR%" com.parkinglotmanager.test.ModelTest
) else if "%choice%"=="2" (
    echo Starting GUI Application...
    echo.
    java -cp "build;%MYSQL_JAR%" com.parkinglotmanager.gui.ParkingLotManagerGUI
) else (
    echo Invalid choice. Starting Model Test by default...
    echo.
    java -cp "build;%MYSQL_JAR%" com.parkinglotmanager.test.ModelTest
)

pause