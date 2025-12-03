# MySQL Workbench Navigation Guide

## Viewing Parking Manager Project Data

### Step 1: Open MySQL Workbench and Connect

1. **Launch MySQL Workbench** from your Start menu
2. **Create/Select Connection:**

   - You should see a connection tile on the home screen
   - If not, click the **"+" icon** next to "MySQL Connections"
   - Enter connection details:
     - **Connection Name:** `Parking Manager Local`
     - **Hostname:** `localhost`
     - **Port:** `3306`
     - **Username:** `root`
     - **Password:** Click "Store in Vault" and enter your MySQL password
   - Click **"Test Connection"** to verify
   - Click **"OK"** to save

3. **Double-click the connection** to connect

---

### Step 2: Select the Parking Database

#### Method 1: Using the Navigator Panel (Left Side)

1. Look at the **SCHEMAS** panel on the left
2. Find **`parking_lot_manager_db`**
3. Click on it to expand
4. You'll see all tables listed under **Tables**

#### Method 2: Using SQL Command

1. In the **Query tab** (center panel), type:
   ```sql
   USE parking_lot_manager_db;
   ```
2. Click the **lightning bolt icon** ⚡ or press `Ctrl+Enter` to execute

---

### Step 3: View All Tables

**In the Query tab, run:**

```sql
SHOW TABLES;
```

**You should see:**

- `User`
- `Lot`
- `Vehicle`
- `ParkingReport`
- `UserPreference`
- `Notification`
- `ModerationAction`
- `Log`

---

### Step 4: View Data in Each Table

#### Option A: Using the GUI (Recommended for Beginners)

1. In the **SCHEMAS** panel, expand **`parking_lot_manager_db`**
2. Expand **Tables**
3. **Right-click** on any table (e.g., `User`)
4. Select **"Select Rows - Limit 1000"**
5. Data appears in the **Result Grid** below

#### Option B: Using SQL Queries

Run these queries in the Query tab:

**View All Users:**

```sql
SELECT * FROM User;
```

**View Parking Lots with Capacity:**

```sql
SELECT lotID, lotName, capacity, status
FROM Lot
ORDER BY capacity DESC;
```

**View Vehicles:**

```sql
SELECT * FROM Vehicle;
```

**View Parking Reports:**

```sql
SELECT pr.*, l.lotName, u.userName
FROM ParkingReport pr
JOIN Lot l ON pr.lotID = l.lotID
JOIN User u ON pr.userID = u.userID
ORDER BY pr.reportTime DESC;
```

**View User Preferences:**

```sql
SELECT up.*, u.userName, l.lotName as preferredLot
FROM UserPreference up
JOIN User u ON up.userID = u.userID
LEFT JOIN Lot l ON up.preferredLotID = l.lotID;
```

**View Notifications:**

```sql
SELECT n.*, u.userName as recipient
FROM Notification n
JOIN User u ON n.recipientID = u.userID
ORDER BY n.sentTime DESC;
```

---

### Step 5: View Table Structure

**To see how a table is structured:**

#### Option A: GUI

1. Right-click the table in SCHEMAS
2. Select **"Table Inspector"**
3. Click **"Columns"** tab to see all fields and data types

#### Option B: SQL

```sql
DESCRIBE User;
-- or
SHOW CREATE TABLE User;
```

---

### Step 6: Common Useful Queries for Your Project

#### 1. Count Users by Type

```sql
SELECT
    privilege,
    COUNT(*) as count
FROM User
GROUP BY privilege;
```

#### 2. Check Total Parking Capacity

```sql
SELECT
    COUNT(*) as total_lots,
    SUM(capacity) as total_spaces,
    SUM(CASE WHEN status = 'Available' THEN capacity ELSE 0 END) as available_capacity
FROM Lot;
```

#### 3. View Lot Details with Full Info

```sql
SELECT
    lotID,
    lotName,
    capacity,
    status,
    CASE
        WHEN capacity > 800 THEN 'Large Structure'
        WHEN capacity > 400 THEN 'Large Lot'
        WHEN capacity > 250 THEN 'Medium Lot'
        ELSE 'Small Lot'
    END as size_category
FROM Lot
ORDER BY capacity DESC;
```

#### 4. Recent Activity Log

```sql
SELECT * FROM Log
ORDER BY logTime DESC
LIMIT 10;
```

#### 5. Users and Their Vehicles

```sql
SELECT
    u.userName,
    u.userEmail,
    v.plate,
    v.make,
    v.model,
    v.color
FROM User u
LEFT JOIN Vehicle v ON u.userID = v.userID
ORDER BY u.userName;
```

---

### Step 7: Filter and Search Data

#### Search for Specific User

```sql
SELECT * FROM User
WHERE userName LIKE '%admin%';
```

#### Find Lots by Capacity

```sql
SELECT lotName, capacity FROM Lot
WHERE capacity >= 300
ORDER BY capacity DESC;
```

#### Check Specific Lot Details

```sql
SELECT * FROM Lot
WHERE lotName = 'Structure 1';
```

---

### Step 8: Export Data

**To export query results:**

1. Run your query
2. In the **Result Grid**, click the **Export icon** (floppy disk)
3. Choose format:
   - **CSV** for Excel
   - **JSON** for web apps
   - **SQL** for backup
4. Choose location and save

---

### Step 9: Visual Data Inspection

#### View ER Diagram (Database Structure)

1. Go to **Database** → **Reverse Engineer**
2. Select your connection
3. Click **Next** → **Next**
4. Select **`parking_lot_manager_db`**
5. Click **Execute**
6. You'll see a visual diagram of all tables and their relationships

---

### Step 10: Quick Reference - Table Descriptions

| Table                | Description                         | Key Columns                                        |
| -------------------- | ----------------------------------- | -------------------------------------------------- |
| **User**             | All system users (admin & clients)  | `userID`, `userName`, `userEmail`, `privilege`     |
| **Lot**              | Parking lot locations               | `lotID`, `lotName`, `capacity`, `status`           |
| **Vehicle**          | User-registered vehicles            | `vehicleID`, `userID`, `plate`, `make`, `model`    |
| **ParkingReport**    | User-submitted lot fullness reports | `lotID`, `userID`, `fullness`, `reportTime`        |
| **UserPreference**   | User parking preferences            | `userID`, `preferredLotID`, `preferredArrivalTime` |
| **Notification**     | System notifications to users       | `recipientID`, `message`, `notificationType`       |
| **ModerationAction** | Admin moderation actions            | `modID`, `actionType`, `message`                   |
| **Log**              | System activity logs                | `logType`, `logMessage`, `userID`                  |

---

### Tips for MySQL Workbench

1. **Multiple Queries:** Separate queries with `;` and highlight one to run only that query
2. **Auto-complete:** Press `Ctrl+Space` for SQL keyword suggestions
3. **Format SQL:** Right-click query → **Beautify Query**
4. **Save Queries:** Click **File** → **Save SQL Script** for reusable queries
5. **Limit Results:** Always use `LIMIT` when viewing large tables:
   ```sql
   SELECT * FROM User LIMIT 100;
   ```

---

### Troubleshooting

**Can't see database?**

- Click the **refresh icon** 🔄 in SCHEMAS panel
- Make sure you ran the database setup script

**Connection failed?**

- Verify MySQL service is running (search "Services" in Windows)
- Check username/password in connection settings

**Query errors?**

- Make sure database is selected: `USE parking_lot_manager_db;`
- Check for typos in table/column names
- Table names are case-sensitive on some systems

---

### Quick Test After Setup

Run this complete test query:

```sql
USE parking_lot_manager_db;

-- Show all tables
SHOW TABLES;

-- Count records in each table
SELECT 'Users' as Table_Name, COUNT(*) as Count FROM User
UNION ALL
SELECT 'Lots', COUNT(*) FROM Lot
UNION ALL
SELECT 'Vehicles', COUNT(*) FROM Vehicle
UNION ALL
SELECT 'Reports', COUNT(*) FROM ParkingReport
UNION ALL
SELECT 'Notifications', COUNT(*) FROM Notification;

-- Show sample data
SELECT userName, userEmail, privilege FROM User LIMIT 5;
SELECT lotName, capacity FROM Lot LIMIT 5;
```

This will give you a quick overview of your database state!

---

## Next Steps

- Explore the **Performance** tab to monitor queries
- Use **Favorites** to save frequently-used queries
- Try the **Visual Explain** feature to optimize slow queries
- Experiment with **joins** to combine data from multiple tables

Happy querying! 🎉
