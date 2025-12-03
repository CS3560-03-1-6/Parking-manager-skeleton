# Parking Manager System - Cal Poly Pomona

A comprehensive parking management system built with Java Swing and MySQL database. Manages 17 parking lots across campus with user authentication, real-time availability tracking, and reporting features.

## Quick Start for Teammates

**First time setup? Run this:**

```powershell
.\verify-setup.bat
```

This will check if you have everything needed. Then follow the instructions in `SETUP_GUIDE.md`.

## Prerequisites

- ✅ **Java JDK 8+** (verify with `java -version`)
- ✅ **MySQL 5.7+** (verify with `mysql --version`)
- ✅ **MySQL Connector JAR** (must be in project root: `mysql-connector-j-9.4.0.jar`)

## Quick Run

1. **Edit `config.properties`** - Update with YOUR MySQL password
2. **Create database:**
   ```powershell
   mysql -u root -p -e "CREATE DATABASE parking_lot_manager_db;"
   Get-Content database\database_setup_v2.sql | mysql -u root -p parking_lot_manager_db
   ```
3. **Run application:**
   ```powershell
   .\run-gui.bat
   ```

## Default Login Credentials

- **Admin:** `admin` / `admin123`
- **Client:** `client` / `client123`

## Features

- 🚗 17 Cal Poly Pomona parking lots (6,320+ total spaces)
- 🔐 Secure user authentication (PBKDF2 password hashing)
- 📊 Real-time parking availability
- 👥 User management (Admin and Client roles)
- 💾 MySQL database with full CRUD operations
- 🎨 Java Swing GUI

## Parking Lots

| Lot            | Type        | Capacity | Location                 |
| -------------- | ----------- | -------- | ------------------------ |
| Structure 1    | Multi-level | 1,250    | Near Voorhis Alumni Park |
| Structure 2    | Multi-level | 850      | Near iPoly High School   |
| Lot B          | Surface     | 450      | East of Structure 2      |
| Lot K          | Surface     | 420      | Near iPoly High School   |
| + 13 more lots | -           | ~4,350   | Various locations        |

**Total Campus Parking:** 6,320 spaces

---

## Detailed Setup (For New Team Members)

See **`SETUP_GUIDE.md`** for complete instructions including:

- Troubleshooting common issues
- System requirements
- File structure explanation
- Development setup

---

## Project Structure

```
Parking-manager-skeleton/
├── config.properties           # Database credentials (EDIT THIS!)
├── mysql-connector-j-9.4.0.jar # MySQL driver (REQUIRED!)
├── run-gui.bat                 # Launch application
├── run-source.bat              # Development mode
├── verify-setup.bat            # Check environment
├── SETUP_GUIDE.md              # Detailed setup instructions
├── database/
│   └── database_setup_v2.sql   # Database schema
└── src/com/parkinglotmanager/
    ├── dao/                    # Database access
    ├── gui/                    # User interface
    ├── model/                  # Data models
    └── util/                   # Utilities
```

---

## How to Run the Application

### **Setup Steps**

### **1. Database Setup:**

Make sure MySQL is installed and running, then create the database:

```sql
mysql -u root -p < database/database_setup_v2.sql
```

### **2. Update Database Credentials:**

Edit `config.properties` file:

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=parking_lot_manager_db
DB_USER=root
DB_PASSWORD=your_mysql_password
```

### **3. Build and Run:**

For quick GUI launch (Windows):

```powershell
.\run-gui.bat
```

For quick GUI launch (Unix/Linux):

```bash
.\run-gui.sh
```

For testing/development (Windows):

```powershell
.\run-source.bat
```

For testing/development (Unix/Linux):

```bash
.\run-source.sh
```
