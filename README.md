# Rock-Paper-Scissors WebApp

This project implements a **centralized Rock–Paper–Scissors web application** using **Scala 3**, **Scala.js**, and the **cs214.webapp** framework.  
The server stores the full game state, while browser clients interact through serialized events and views.

---

## 🚀 Features

- Two-player Rock–Paper–Scissors match  
- Fully centralized server-side state machine  
- Real-time view updates for each client  
- Two UI options:
  - ✔ HTML UI  
  - ✔ Text-based UI  
- Built on a clean Model–View architecture  
- Automatic serialization/deserialization of events and views  

---

## 📁 Project Structure

```
apps/
├── js/        # Browser UIs (HTML + Text)
├── jvm/       # Server logic (State, transition, projection)
└── shared/    # Shared types, events, views, serializers

build.sbt
```



---

## ▶️ How to Run

### 1. Start SBT
Use the special command required by the project:

```bash
sbt --client
sbt -Djline.terminal=none --client

Inside SBT:

run

🌐 Open the App in Your Browser

Go to:

http://localhost:8080


Then:

Select "Rock-Paper-Scissors"

Enter two user IDs (e.g. me and friend)

Pick your UI (HTML or Text)

Select your user

Play 🎮
