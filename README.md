# **Health Clinic Appointment System** 🏥💻  
**A web-based appointment scheduling system for clinics, ensuring efficient patient-doctor interactions.**  

## 📌 **Project Overview**  
The **Health Clinic Appointment System** is designed to allow patients to book, cancel, and manage medical appointments while enabling doctors to set availability and track schedules. Administrators oversee user management, ensuring secure access and data integrity.  

## 🚀 **Features**  
✅ **User Authentication** – Patients, doctors, and admins can register and log in securely.  
✅ **Appointment Management** – Patients can book, reschedule, or cancel appointments.  
✅ **Doctor Availability** – Doctors can update their available slots.  
✅ **Email Notifications** – Automatic confirmations and reminders for appointments.  
✅ **Admin Controls** – Approve doctor registrations, manage users, and monitor system activity.  
✅ **Automated Reminders** – Sends notifications to patients and doctors for upcoming appointments.  

## 🛠 **Tech Stack**  
- **Backend:** Java, Spring Boot, Hibernate  
- **Frontend:** HTML, CSS, JavaScript  
- **Database:** MySQL  
- **Version Control:** Git & GitHub  
- **Task Management:** Trello  
- **Deployment:** TBD (Cloud / Local Server)  

## 📂 **Project Structure**  
```
📦 HealthClinicSystem  
 ┣ 📂 src  
 ┃ ┣ 📂 main  
 ┃ ┃ ┣ 📂 java/com/clinic  
 ┃ ┃ ┃ ┣ 📂 controllers  
 ┃ ┃ ┃ ┣ 📂 models  
 ┃ ┃ ┃ ┣ 📂 services  
 ┃ ┃ ┃ ┣ 📂 repositories  
 ┃ ┣ 📂 resources  
 ┣ 📂 docs  
 ┃ ┣ 📄 SRS.pdf  
 ┃ ┣ 📄 ClassDiagram.puml  
 ┣ 📄 README.md  
 ┣ 📄 .gitignore  
 ┗ 📄 pom.xml  
```

## 🔧 **Installation & Setup**  
### **1️⃣ Clone the Repository**  
```bash
git clone https://github.com/your-username/HealthClinicSystem.git
cd HealthClinicSystem
```

### **2️⃣ Set Up the Database**  
- Install Microsoft SQL server
- Create a database `health_clinic_db`  
- Configure **application.properties** for DB connection  

### **3️⃣ Build & Run the Application**  
```bash
mvn clean install
mvn spring-boot:run
```

### **4️⃣ Access the Application**  
- Open **http://localhost:8080/** in your browser.  
- Login with test credentials or register a new user.  

## 🏗 **System Architecture**  
![Class Diagram](docs/ClassDiagram.png)  

## 📜 **API Endpoints (Example)**  
| Method | Endpoint | Description |  
|--------|---------|-------------|  
| `POST` | `/register` | Registers a new user |  
| `POST` | `/login` | Authenticates a user |  
| `GET` | `/appointments` | Fetches all appointments |  
| `POST` | `/appointments/book` | Books an appointment |  
| `DELETE` | `/appointments/cancel/{id}` | Cancels an appointment |  

## 📌 **Project Management**  
🔹 **GitHub Repo:** [Link](https://github.com/i221158/HealthClinicSystem)  
🔹 **Trello Board:** [Link].(https://trello.com/b/o6fMUo68/sprint-1-backlog)

## 👥 **Contributors**  
- **[Your Name]** – UI Designer & Tester 🎨  
- **[Teammate 1]** – Backend Developer 💻  
- **[Teammate 2]** – Database & API Development 🛢  

## 📜 **License**  
This project is licensed under the **MIT License** – feel free to modify and use it.  

## 🎯 **Future Enhancements**  
🔹 Doctor profile & reviews ⭐  
🔹 Payment integration for consultations 💳  
🔹 AI-powered appointment suggestions 🤖  
