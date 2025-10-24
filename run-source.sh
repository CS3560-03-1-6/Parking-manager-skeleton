#!/bin/bash

echo "Parking Manager System - Source-based Build and Run Script"
echo "==========================================================="

# Check if MySQL Connector JAR exists
MYSQL_JAR="mysql-connector-j-9.4.0.jar"

if [ ! -f "$MYSQL_JAR" ]; then
    echo "ERROR: MySQL Connector/J JAR file not found!"
    echo "Looking for: $MYSQL_JAR"
    echo
    echo "Please make sure the MySQL Connector JAR file is in this directory."
    echo "You can copy it from the extracted folder or download from:"
    echo "https://dev.mysql.com/downloads/connector/j/"
    exit 1
fi

echo "Found MySQL Connector: $MYSQL_JAR"
echo

# Create build directory if it doesn't exist
if [ ! -d "build" ]; then
    mkdir build
fi

# Function to compile Java files
compile_java() {
    local path=$1
    local classpath=$2
    local src_path=$3
    echo "Compiling classes from $src_path..."
    javac -cp "$classpath" --release 8 -Xlint:-options -sourcepath "$src_path" -d build "$src_path"/*.java
    if [ $? -ne 0 ]; then
        echo "ERROR: Compilation failed for $src_path!"
        exit 1
    fi
}

# Compile all Java files with proper classpath in dependency order
compile_java "enums" ".:$MYSQL_JAR" "src/com/parkinglotmanager/enums"
compile_java "utils" "build:$MYSQL_JAR" "src/com/parkinglotmanager/util"
compile_java "model" "build:$MYSQL_JAR" "src/com/parkinglotmanager/model"
compile_java "test" "build:$MYSQL_JAR" "src/com/parkinglotmanager/test"
compile_java "gui" "build:$MYSQL_JAR" "src/com/parkinglotmanager/gui"

echo
echo "Compiling OLD package (com.parkingmanager)..."

# Compiling old classes for backward compatibility
OLD_CLASSES=("model" "util" "dao" "service" "gui")

for class in "${OLD_CLASSES[@]}"; do
    compile_java "$class" ".:$MYSQL_JAR" "src/com/parkingmanager/$class"
    echo "WARNING: Old $class compilation failed (this is expected)"
done

echo
echo "Compilation complete!"
echo
echo "Choose what to run:"
echo "1. Model Test (console)"
echo "2. GUI Application (window)"
echo

read -p "Enter your choice (1 or 2): " choice

if [ "$choice" == "1" ]; then
    echo "Starting Model Test..."
    echo
    java -cp "build:$MYSQL_JAR" com.parkinglotmanager.test.ModelTest
elif [ "$choice" == "2" ]; then
    echo "Starting GUI Application..."
    echo
    java -cp "build:$MYSQL_JAR" com.parkinglotmanager.gui.ParkingLotManagerGUI
else
    echo "Invalid choice. Starting Model Test by default..."
    echo
    java -cp "build:$MYSQL_JAR" com.parkinglotmanager.test.ModelTest
fi
