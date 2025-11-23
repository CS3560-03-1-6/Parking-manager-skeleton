#!/usr/bin/env bash
set -euo pipefail

echo "Parking Lot Manager - Quick Launch"
echo "===================================="

MYSQL_JAR="mysql-connector-j-9.4.0.jar"
if [[ ! -f "$MYSQL_JAR" ]]; then
  echo "WARNING: MySQL Connector JAR not found. Database features disabled."
  MYSQL_JAR="."
fi

echo
echo "Compiling new package (com.parkinglotmanager)..."

BUILD_DIR="build"
PKG_DIR="$BUILD_DIR/com/parkinglotmanager"
rm -rf "$PKG_DIR"
mkdir -p "$PKG_DIR"/{enums,model,test,gui,util}

# function to run javac with check
run_javac() {
  echo "$1"
  shift
  if ! javac "$@"; then
    echo "Compilation failed."
    exit 1
  fi
}

# Compile enums (explicit or glob)
run_javac "Compiling enums..." -cp ".:${MYSQL_JAR}" --release 8 -Xlint:-options -d "$BUILD_DIR" \
  src/com/parkinglotmanager/enums/*.java

# Compile utilities
run_javac "Compiling utilities..." -cp "${BUILD_DIR}:${MYSQL_JAR}" --release 8 -Xlint:-options -d "$BUILD_DIR" \
  src/com/parkinglotmanager/util/*.java

# Compile models (compile all model files together to ensure Notification is included)
run_javac "Compiling models..." -cp "${BUILD_DIR}:${MYSQL_JAR}" --release 8 -Xlint:-options -d "$BUILD_DIR" \
  src/com/parkinglotmanager/model/*.java

# Compile DAOs (compile all DAO files together)
run_javac "Compiling Data Access Objects..." -cp "${BUILD_DIR}:${MYSQL_JAR}" --release 8 -Xlint:-options -d "$BUILD_DIR" \
  src/com/parkinglotmanager/dao/*.java

# Compile GUI
run_javac "Compiling GUI..." -cp "${BUILD_DIR}:${MYSQL_JAR}" --release 8 -Xlint:-options -d "$BUILD_DIR" \
  src/com/parkinglotmanager/gui/*.java

# Compile test
run_javac "Compiling test..." -cp "${BUILD_DIR}:${MYSQL_JAR}" --release 8 -Xlint:-options -d "$BUILD_DIR" \
  src/com/parkinglotmanager/test/*.java

echo
echo "Compilation successful!"
echo
echo "Launching Login Screen..."
echo

java -cp "${BUILD_DIR}:${MYSQL_JAR}" com.parkinglotmanager.gui.LoginGUI

echo
read -r -p "Press Enter to exit..."
