# Project P6 — Grievance Priority Queue
**Soft Polynomials Pvt. Ltd. | Sumeet**

---

## Project Structure

```
GrievanceSystem/
├── sql/
│   └── schema.sql              ← Run this first in MySQL
├── src/
│   ├── Main.java               ← Entry point (console menu)
│   ├── model/
│   │   ├── Grievance.java
│   │   ├── Category.java
│   │   ├── Citizen.java
│   │   ├── Officer.java
│   │   └── StatusHistory.java
│   ├── dao/
│   │   ├── GrievanceDAO.java
│   │   ├── CategoryDAO.java
│   │   ├── CitizenDAO.java
│   │   ├── OfficerDAO.java
│   │   └── StatusHistoryDAO.java
│   ├── algorithm/
│   │   └── GrievanceMinHeap.java   ← Heap from scratch (Sumeet)
│   ├── service/
│   │   └── GrievanceService.java   ← Business logic
│   ├── html/
│   │   └── HTMLGenerator.java      ← Writes HTML output
│   └── util/
│       └── DBConnection.java
└── README.md
```

---

## Setup Steps

### Step 1 — MySQL
```sql
-- Open MySQL Workbench or terminal
mysql -u root -p
source path/to/sql/schema.sql
```

### Step 2 — Add JDBC JAR
Download: https://dev.mysql.com/downloads/connector/j/
- In IntelliJ: File → Project Structure → Libraries → Add JAR
- In Eclipse: Right-click project → Build Path → Add External JARs

### Step 3 — Update Password
In `src/util/DBConnection.java`, change:
```java
private static final String PASSWORD = "root"; // ← your MySQL password
```

### Step 4 — Compile & Run
**IntelliJ:** Right-click `Main.java` → Run

**Command line:**
```bash
# From GrievanceSystem/ folder
javac -cp .;mysql-connector.jar -d out src/**/*.java src/Main.java
java  -cp out;mysql-connector.jar Main
```

---

## Priority Formula 

```
Priority Score = (categoryWeight × 10) + ageInDays + agingBonus

Where:
  categoryWeight  = Water:3, Road:2, Garbage:2, Lighting:1, Other:1
  ageInDays       = days since submitted
  agingBonus      = extra points if ageInDays > 7 days (+1 per extra day)
```

**Example:**
| Grievance | Category | Weight | Age | Bonus | Score |
|-----------|----------|--------|-----|-------|-------|
| G1        | Water    | 3      | 2   | 0     | 32    |
| G2        | Lighting | 1      | 15  | 8     | 33    |
← G2 beats G1 because it's old! (aging algorithm working)

---

## Heap Operations 

**Insert:** Add to end → percolateUp
**ExtractMin:** Remove root → move last to root → percolateDown

**Array indices:**
- Parent of i  = (i-1) / 2
- Left child   = 2i + 1
- Right child  = 2i + 2

---

## HTML Output Files
After running menu option 2 or 5:
- `priority_dashboard.html` — open in browser
- `analytics.html` — open in browser
