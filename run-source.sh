#!/usr/bin/env bash
# Parking Manager System - Source-based Build and Run Script
# Usage: ./build-and-run.sh

set -euo pipefail

MYSQL_JAR="mysql-connector-j-9.4.0.jar"
BUILD_DIR="build"
SRC_DIR="src"

echo "Parking Manager System - Source-based Build and Run Script"
echo "==========================================================="

# Check MySQL Connector JAR
if [[ ! -f "$MYSQL_JAR" ]]; then
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
mkdir -p "$BUILD_DIR"

# Helper to compile and continue/exit on failure according to expected behavior
_compile() {
  local cp="$1"
  shift
  local sources=("$@")
  echo "javac -cp \"$cp\" --release 8 -Xlint:-options -sourcepath $SRC_DIR -d $BUILD_DIR ${sources[*]}"
  if ! javac -cp "$cp" --release 8 -Xlint:-options -sourcepath "$SRC_DIR" -d "$BUILD_DIR" "${sources[@]}"; then
    return 1
  fi
  return 0
}

echo "Compiling Java files from src directory..."
echo

# Compile packages in dependency order
echo "Compiling enum classes..."
if ! _compile ".:$MYSQL_JAR" "$SRC_DIR/com/parkinglotmanager/enums/"*.java; then
  echo "ERROR: Enum compilation failed!"
  exit 1
fi

echo "Compiling utility classes..."
if ! _compile "$BUILD_DIR:$MYSQL_JAR" "$SRC_DIR/com/parkinglotmanager/util/"*.java; then
  echo "ERROR: Utility compilation failed!"
  exit 1
fi

echo "Compiling model classes..."
if ! _compile "$BUILD_DIR:$MYSQL_JAR" "$SRC_DIR/com/parkinglotmanager/model/"*.java; then
  echo "ERROR: Model compilation failed!"
  exit 1
fi

echo "Compiling test classes..."
if ! _compile "$BUILD_DIR:$MYSQL_JAR" "$SRC_DIR/com/parkinglotmanager/test/"*.java; then
  echo "ERROR: Test compilation failed!"
  exit 1
fi

echo "Compiling GUI classes..."
if ! _compile "$BUILD_DIR:$MYSQL_JAR" "$SRC_DIR/com/parkinglotmanager/gui/"*.java; then
  echo "ERROR: GUI compilation failed!"
  exit 1
fi

echo
echo "Compiling OLD package (com.parkingmanager)..."
echo "Compiling old model classes..."
if ! _compile ".:$MYSQL_JAR" "$SRC_DIR/com/parkingmanager/model/"*.java; then
  echo "WARNING: Old model compilation failed (this is expected)"
fi

echo "Compiling old utility classes..."
if ! _compile ".:$MYSQL_JAR" "$SRC_DIR/com/parkingmanager/util/"*.java; then
  echo "WARNING: Old utility compilation failed"
fi

echo "Compiling old DAO classes..."
if ! _compile "$BUILD_DIR:$MYSQL_JAR" "$SRC_DIR/com/parkingmanager/dao/"*.java; then
  echo "WARNING: Old DAO compilation failed"
fi

echo "Compiling old service classes..."
if ! _compile "$BUILD_DIR:$MYSQL_JAR" "$SRC_DIR/com/parkingmanager/service/"*.java; then
  echo "WARNING: Old service compilation failed"
fi

echo "Compiling old GUI classes..."
if ! _compile "$BUILD_DIR:$MYSQL_JAR" "$SRC_DIR/com/parkingmanager/gui/"*.java; then
  echo "WARNING: Old GUI compilation failed (this is expected)"
fi

echo
echo "Compilation complete!"
echo

# Prompt for run choice
echo "Choose what to run:"
echo "1. Model Test (console)"
echo "2. GUI Application (window)"
echo

read -rp "Enter your choice (1 or 2): " choice || choice="1"

case "$choice" in
  1)
    echo "Starting Model Test..."
    echo
    java -cp "$BUILD_DIR:$MYSQL_JAR" com.parkinglotmanager.test.ModelTest
    ;;
  2)
    echo "Starting GUI Application..."
    echo
    java -cp "$BUILD_DIR:$MYSQL_JAR" com.parkinglotmanager.gui.ParkingLotManagerGUI
    ;;
  *)
    echo "Invalid choice. Starting Model Test by default..."
    echo
    java -cp "$BUILD_DIR:$MYSQL_JAR" com.parkinglotmanager.test.ModelTest
    ;;
esac

# Pause equivalent: wait for user to press Enter before exiting
echo
read -rp "Press Enter to exit..."
