@echo off
echo Parking Lot Manager - Quick Launch
echo ====================================

REM Check MySQL JAR
set MYSQL_JAR=mysql-connector-j-9.4.0.jar
if not exist "%MYSQL_JAR%" (
    echo WARNING: MySQL Connector JAR not found. Database features disabled.
    set MYSQL_JAR=.
)

echo.
echo Compiling new package (com.parkinglotmanager)...

REM Clean and create build directory
if exist "build\com\parkinglotmanager" rmdir /s /q "build\com\parkinglotmanager"
mkdir "build\com\parkinglotmanager\enums" 2>nul
mkdir "build\com\parkinglotmanager\model" 2>nul
mkdir "build\com\parkinglotmanager\test" 2>nul
mkdir "build\com\parkinglotmanager\gui" 2>nul
mkdir "build\com\parkinglotmanager\util" 2>nul

REM Compile enums
echo Compiling enums...
javac -cp ".;%MYSQL_JAR%" --release 8 -Xlint:-options -d build src\com\parkinglotmanager\enums\VehicleType.java src\com\parkinglotmanager\enums\SlotType.java src\com\parkinglotmanager\enums\VehicleMake.java src\com\parkinglotmanager\enums\LotType.java src\com\parkinglotmanager\enums\SensorType.java
if %errorlevel% neq 0 exit /b 1

REM Compile utilities
echo Compiling utilities...
javac -cp "build;%MYSQL_JAR%" --release 8 -Xlint:-options -d build src\com\parkinglotmanager\util\DatabaseConnection.java
if %errorlevel% neq 0 exit /b 1

REM Compile models
echo Compiling models...
javac -cp "build;%MYSQL_JAR%" --release 8 -Xlint:-options -d build src\com\parkinglotmanager\model\User.java src\com\parkinglotmanager\model\Admin.java src\com\parkinglotmanager\model\Client.java src\com\parkinglotmanager\model\Vehicle.java src\com\parkinglotmanager\model\ParkingLot.java src\com\parkinglotmanager\model\ParkingSlot.java src\com\parkinglotmanager\model\VehicleSession.java src\com\parkinglotmanager\model\UserReport.java src\com\parkinglotmanager\model\OccupancyReport.java src\com\parkinglotmanager\model\Sensor.java src\com\parkinglotmanager\model\SensorReading.java
if %errorlevel% neq 0 exit /b 1

REM Compile GUI
echo Compiling GUI...
javac -cp "build;%MYSQL_JAR%" --release 8 -Xlint:-options -d build src\com\parkinglotmanager\gui\ParkingLotManagerGUI.java src\com\parkinglotmanager\gui\LoginGUI.java
if %errorlevel% neq 0 exit /b 1

REM Compile test
echo Compiling test...
javac -cp "build;%MYSQL_JAR%" --release 8 -Xlint:-options -d build src\com\parkinglotmanager\test\ModelTest.java
if %errorlevel% neq 0 exit /b 1

echo.
echo Compilation successful!
echo.
echo Launching Login Screen...
echo.
java -cp "build;%MYSQL_JAR%" com.parkinglotmanager.gui.LoginGUI

pause
