# Orion

ORION is a Java-based Bank Management System 🏦 with a user-friendly GUI (Swing) interface and a PostgreSQL database.  
It provides core banking operations such as account creation, deposits, withdrawals, fund transfers, and transaction history tracking.  

This project was built to simulate real-world banking activities while keeping the UI aesthetic and responsive.


# ✨ Features
- 🔐 User Authentication (Signup, Login, Logout)
- 👤 Profile Management (Update personal info, Upload profile picture)
- 💰 Account Operations
- Deposit / Withdraw
- Fund Transfer
- Auto-refreshing Balance Display ⚡
- 📜 Transaction History (view all past operations)
- 🎨 Modern GUI Design (Java Swing)
- 🗄️ Database Integration (PostgreSQL)


# 🏗️ Project Structure

orion/
├── src/                Java source files
│                       GUI classes (Swing)
│                       Database connection & queries
│                       Entity classes (User, Account, Transaction, etc.)
│                       Helper functions
├                       Database schema & sample data
├── resources/          Images, icons, etc.
└── README.md           Project documentation



# ⚙️ Installation & Setup

1️⃣ Clone the Repository
git clone https://github.com/your-username/orion.git
cd orion


2️⃣ Setup Database
- Install PostgreSQL
- Create a database (e.g., `orion_db`)

3️⃣ Configure Database Connection
Edit the database config file in:
src/DatabaseConnection.java
and update with your PostgreSQL credentials:

java
String url = "jdbc:postgresql://localhost:5432/orion_db";
String user = "your_username";
String password = "your_password";


4️⃣ Run the Application
Compile and run the project:
bash
javac -d bin src/*.java
java -cp bin Main


# 📂 Tech Stack
- Language: Java ☕  
- GUI: Swing 🎨  
- Database: PostgreSQL 🐘  
- Architecture: 3-Tier (Presentation ➝ Business Logic ➝ Database)


# 🚀 Future Improvements
- 📱 Mobile-friendly interface
- 🌐 Web integration
- 🧾 Advanced reporting (monthly statements, PDF export)
- 🔔 Notifications & Alerts


# 🤝 Contribution
Contributions are welcome!  
- Fork the repo  
- Create a new branch (`feature-xyz`)  
- Commit changes  
- Open a Pull Request 🎉  


# Developed by Reverence Anietie
