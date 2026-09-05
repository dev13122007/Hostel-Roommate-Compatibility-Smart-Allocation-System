# 🏠 Hostel Roommate Compatibility & Smart Allocation System

*Find a roommate who fits your lifestyle, not just an empty bed.*

Getting a roommate is easy, but finding a *good* roommate is a completely different story. We’ve all heard the horror stories: one roommate wants to sleep in silence at 10 PM, while the other is hosting a gaming marathon at 2 AM. Traditional hostel systems just drop two random students into an empty room and hope for the best. 

This Core Java CLI application tackles the age-old campus problem of mismatched roommates by pairing students based on actual lifestyle compatibility *before* assigning them a room.

---

## ✨ Core Features

### 👨‍🎓 Student Management & Dashboard
* Secure registration and login.
* Comprehensive profile and lifestyle preference management.

### 🧩 Intelligent Compatibility Matching
Instead of a random lottery, the system calculates a weighted compatibility score based on real-life factors:

| Category | Weight |
| :--- | :--- |
| 😴 Sleep Schedule | 15% |
| 📚 Study Habit | 15% |
| 🧹 Cleanliness | 15% |
| 🔊 Noise Preference | 10% |
| 🗣️ Social Preference | 10% |
| 🍽️ Food Preference | 10% |
| 🌡️ Room Temperature | 10% |
| 👥 Guest Frequency | 5% |
| 📖 Study Environment | 5% |
| 🗓️ Weekend Routine | 5% |
| **Total** | **100%** |

#### 🍽️ Dietary Logic
Food preference is treated with special logic to prevent friction:
* `VEG` + `VEG` / `NON_VEG` + `NON_VEG` = ✅ Compatible
* `VEG` + `NON_VEG` = ❌ Conflict
* `ANY` + (`VEG` or `NON_VEG`) = ✅ Compatible

#### 💡 Match Explanation
A simple "87% Match" isn't enough if you don't know *why*. The system generates a transparent breakdown showing exactly where you align (e.g., similar cleanliness) and where you might clash (e.g., different guest frequencies).

### 📨 Request System & Smart Allocation
* **Roommate Requests:** Send, accept, decline, or cancel requests. Protects against duplicate/invalid requests.
* **Smart Allocation:** Admin oversight of rooms, tracking blocks, floors, capacity, and current occupants.

---

## 🛠️ Technology Stack & Under the Hood

This is a pure **Core Java** CLI application built to demonstrate robust software engineering principles without relying on external frameworks or databases.

* **Language:** Java (JDK 8+)
* **Architecture:** Object-Oriented Design (Interfaces, Encapsulation, Polymorphism)
* **Concurrency (Multithreading):** Safely handles simultaneous room bookings using thread `synchronization`, completely eliminating the risk of double-allocating a bed.
* **Storage:** Lightweight, file-based data persistence (saving directly to `.txt` files in a `data/` folder).
* **Reliability:** Comprehensive exception handling and input validation.

---

## 🏗️ Project Structure

```text
HostelRoommateCompatibilitySystem/
│
├── src/
│   ├── Main.java
│   ├── model/         (User, Student, Admin, Room, etc.)
│   ├── service/       (StudentService, AllocationService, etc.)
│   ├── interface/     (CompatibilityCalculator)
│   ├── exception/     (Custom Exception classes)
│   ├── util/          (FileManager, InputValidator, etc.)
│   └── thread/        (ConcurrentAllocationDemo)
│
├── data/              (.txt storage files)
├── docs/
├── tests/
└── README.md
```

## 🔄 How It Works

```text
 ┌──────────────────┐
 │ Student Registers│
 └────────┬─────────┘
          ↓
 ┌──────────────────┐
 │ Add Preferences  │
 └────────┬─────────┘
          ↓
 ┌──────────────────┐
 │ Match Finder     │
 └────────┬─────────┘
          ↓
 ┌──────────────────┐
 │ Send Request     │
 └────────┬─────────┘
          ↓
 ┌──────────────────┐
 │ Room Allocation  │
 └──────────────────┘
```

## ▶️ How to Run

1. Ensure Java is installed (`java -version`).
2. Compile the project from the root directory:
   ```bash
   javac -d out src/*.java src/model/*.java src/service/*.java src/interface/*.java src/exception/*.java src/util/*.java src/thread/*.java
   ```
3. Run the application:
   ```bash
   java -cp out Main
   ```

---

## 👨‍💻 Author
**Divyansh Gupta**  
B.Tech CSE (AI & ML)  
VIT Bhopal University  

*Good software doesn't always need to be complicated. Sometimes, it just needs to solve the right problem. 🏠✨*
