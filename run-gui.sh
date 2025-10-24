#!/bin/bash

echo "Parking Lot Manager - Quick Launch"
echo "===================================="

# Check MySQL JAR
MYSQL_JAR="mysql-connector-j-9.4.0.jar"
if [ ! -f "$MYSQL_JAR" ]; then
    echo "WARNING: MySQL Connector JAR not found. Database features disabled."
    MYSQL_JAR="."
fi

echo
echo "Compiling new package (com.parkinglotmanager)..."

# Clean and create build directory
BUILD_DIR="build/com/parkinglotmanager"
if [ -d "$BUILD_DIR" ]; then
    rm -rf "$BUILD_DIR" # Remove existing build directory
fi
mkdir -p "$BUILD_DIR/enums" "$BUILD_DIR/model" "$BUILD_DIR/test" "$BUILD_DIR/gui" "$BUILD_DIR/util"

# Compile enums
echo "Compiling enums..."
javac -cp ".:$MYSQL_JAR" --release 8 -Xlint:-options -d "$BUILD_DIR" src/com/parkinglotmanager/enums/*.java
if [ $? -ne 0 ]; then
    exit 1
fi

# Compile utilities
echo "Compiling utilities..."
javac -cp "$BUILD_DIR:$MYSQL_JAR" --release 8 -Xlint:-options -d "$BUILD_DIR" src/com/parkinglotmanager/util/DatabaseConnection.java
if [ $? -ne 0 ]; then
    exit 1
fi

# Compile models
echo "Compiling models..."
javac -cp "$BUILD_DIR:$MYSQL_JAR" --release 8 -Xlint:-options -d "$BUILD_DIR" src/com/parkinglotmanager/model/*.java
if [ $? -ne 0 ]; then
    exit 1
fi

# Compile GUI
echo "Compiling GUI..."
javac -cp "$BUILD_DIR:$MYSQL_JAR" --release 8 -Xlint:-options -d "$BUILD_DIR" src/com/parkinglotmanager/gui/*.java
if [ $? -ne 0 ]; then
    exit 1
fi

# Compile test
echo "Compiling test..."
javac -cp "$BUILD_DIR:$MYSQL_JAR" --release 8 -Xlint:-options -d "$BUILD_DIR" src/com/parkinglotmanager/test/*.java
if [ $? -ne 0 ]; then
    exit 1
fi

echo
echo "Compilation successful!"
echo
echo "Launching Login Screen..."
echo
java -cp "$BUILD_DIR:$MYSQL_JAR" com.parkinglotmanager.gui.LoginGUI

