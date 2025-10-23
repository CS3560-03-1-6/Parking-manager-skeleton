# Parking Manager System - Updated Project Structure

A comprehensive parking management system built with Java Swing and MySQL database, now organized with proper package structure.

New:

![alt text](image.png)
![alt text](image-1.png)



Old:
<img width="982" height="689" alt="image" src="https://github.com/user-attachments/assets/0bec96de-a7a3-4a98-bdc4-27f28c8b4f65" />

## **How to Run the Application**

## **Setup Steps**

1. **Database Setup:**

   ```sql
   mysql -u root -p < database_setup.sql
   ```

2. **Update Database Credentials:**
   Edit `src/com/parkingmanager/util/DatabaseConnection.java`:

   ```java
   private static final String USER = "your_mysql_username";
   private static final String PASSWORD = "your_mysql_password";
   ```

3. **Build and Run: for testing**
   `powershell`
   .\run-source.bat <!-- for testing -->
   .\run-source.bat <!-- for quick GUI  -->

## **Usage Features**

- **Park vehicles** - Select available spots and enter vehicle details
- **Calculate fees** - Automatic hourly rate calculation with minimum 1-hour charge
- **Real-time stats** - Available spots, occupied spots, total revenue
- **View data** - Tabular display of all spots and parked vehicles
- **Persistent storage** - All data saved to MySQL database

### **Method 1: Using the Source Build Script (Recommended)**

1. **Navigate to project directory:**

   ```powershell
   cd "d:\My projects2\parking manager"
   ```

2. **Run the source build script:**
   ```powershell
   .\run-source.bat
   ```
