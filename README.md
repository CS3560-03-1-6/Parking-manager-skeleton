# Parking Manager System - Updated Project Structure

A comprehensive parking management system built with Java Swing and MySQL database, now organized with proper package structure.

New:

![alt text](image.png)
![alt text](image-1.png)



Old:
<img width="982" height="689" alt="image" src="https://github.com/user-attachments/assets/0bec96de-a7a3-4a98-bdc4-27f28c8b4f65" />

## **How to Run the Application**

## **Setup Steps**

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

