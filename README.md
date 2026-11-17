# ✈️ Intelligent Travel Assistant

<p align="center">
  <strong>Your smart companion for planning better trips.</strong>
</p>

<p align="center">
  A full-stack travel planning application built with Vue.js 3 and Java Spring Boot.
</p>

---

## 📌 About the Project

Intelligent Travel Assistant is a web-based application designed to make travel planning easier, smarter and more convenient.

Users can select a destination, set their travel budget and choose the number of travel days. The system generates a personalized itinerary with daily activities, estimated expenses, travel recommendations and useful information.

The application also includes a travel chat assistant that helps users discover attractions, food recommendations and other city information.

## ✨ Features

### 🗺️ Personalized Travel Planning
- Select from multiple supported destinations.
- Customize your travel budget.
- Choose a trip duration from 1 to 7 days.
- Generate organized daily travel itineraries.
- View morning, afternoon and evening activities.

### 💰 Smart Budget Estimation
- Accommodation expenses.
- Food and restaurant expenses.
- Transportation costs.
- Attraction ticket prices.
- Additional expenses.
- Estimated total cost and budget warnings.

### 🎯 Two Travel Planning Strategies

**Standard Planner**
- Prioritizes popular attractions.
- Uses mid-range accommodation estimates.
- Provides a standard travel experience.

**Economy Planner**
- Prioritizes more affordable attractions.
- Uses budget accommodation estimates.
- Helps users reduce travel expenses.

The application compares estimated costs and can select a more economical plan when necessary.

### 💬 Travel Chat Assistant
- Ask questions about supported cities.
- Discover popular attractions.
- Get food recommendations.
- Ask about city weather information.
- Explore estimated travel budgets.
- Receive responses through Server-Sent Events (SSE).

The current assistant uses rule-based intent detection and locally stored travel information. It does not require an external generative AI API.

### 👤 Trip Management
- Save travel plans.
- View previously saved trips.
- Open individual trip details.
- Delete saved trips.

---

## 🏙️ Supported Destinations

The application currently includes travel information for eight Chinese cities:

| City | Province / Municipality |
|---|---|
| Beijing | Beijing |
| Shanghai | Shanghai |
| Guangzhou | Guangdong |
| Shenzhen | Guangdong |
| Chengdu | Sichuan |
| Hangzhou | Zhejiang |
| Xi'an | Shaanxi |
| Chongqing | Chongqing |

---

## 🛠️ Technologies Used

### Frontend

| Technology | Purpose |
|---|---|
| Vue.js 3 | User interface |
| JavaScript | Application logic |
| Vant 4 | UI components |
| Vue Router | Page navigation |
| Axios | HTTP requests |
| Vite | Development server and build tool |
| CSS | Styling and responsive layout |

### Backend

| Technology | Purpose |
|---|---|
| Java 17 | Backend programming language |
| Spring Boot 3.5 | Backend framework |
| Spring Web | REST API development |
| Maven | Dependency management and build |
| Lombok | Java boilerplate reduction |

---

## 🏗️ System Architecture

The project uses a frontend-backend architecture.

```text
          USER
           |
           v
   VUE.JS FRONTEND
       (Port 5173)
           |
           | HTTP / REST API
           v
   JAVA SPRING BOOT
       (Port 3200)
           |
     +-----+------+
     |            |
     v            v
  SERVICES     CONTROLLERS
     |
     v
  REPOSITORIES
     |
     v
  TRAVEL DATA
  (Text Files)
```

### Backend Architecture

The backend follows the MVC architectural approach and separates responsibilities into packages.

| Package | Responsibility |
|---|---|
| controller | Handles HTTP requests |
| service | Contains business logic |
| model | Defines domain objects |
| planner | Implements itinerary planning strategies |
| repository | Handles travel data and saved trips |
| vo | Defines request and response objects |
| config | Application configuration |
| exception | Error handling |

### Design Patterns

**Strategy Pattern**

Allows the application to use different itinerary planning strategies, such as Standard and Economy.

**Template Method Pattern**

Defines the common itinerary generation workflow while allowing different planners to customize attraction selection.

**Object-Oriented Programming**

Uses classes, inheritance, abstraction and polymorphism to organize the application.

---

## 📂 Project Structure

```text
intelligent-travel-assistant/
│
├── travel-backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/travelbackend/
│   │   │   │       ├── config/
│   │   │   │       ├── controller/
│   │   │   │       ├── exception/
│   │   │   │       ├── model/
│   │   │   │       ├── planner/
│   │   │   │       ├── repository/
│   │   │   │       ├── service/
│   │   │   │       └── vo/
│   │   │   └── resources/
│   │   │       └── data/
│   │   └── test/
│   └── pom.xml
│
├── travel-h5/
│   ├── public/
│   ├── src/
│   │   ├── assets/
│   │   ├── components/
│   │   ├── router/
│   │   ├── utils/
│   │   └── views/
│   ├── package.json
│   └── vite.config.js
│
├── .gitignore
├── README.md
└── HOW_TO_RUN.md
```

---

## ⚙️ Installation and Setup

### Prerequisites

Make sure the following tools are installed:

- JDK 17
- Apache Maven 3.9.9 or newer
- Node.js 18 or newer
- Git
- Visual Studio Code (recommended)

### 1. Clone the Repository

```bash
git clone https://github.com/AbdulsamadMohamedHussein/intelligent-travel-assistant.git
```

```bash
cd intelligent-travel-assistant
```

### 2. Start the Backend

Open a terminal inside the `travel-backend` directory.

Run:

```bash
mvn "-Dmaven.compiler.release=17" "-Djava.version=17" spring-boot:run
```

The backend will start at:

http://localhost:3200

To verify that the backend is running, visit:

http://localhost:3200/api/travel/hello

### 3. Configure and Start the Frontend

Open another terminal inside the `travel-h5` directory.

Install dependencies:

```bash
npm install
```

Create your local environment file by copying the example:

**Windows PowerShell:**

```powershell
Copy-Item .env.example .env
```

Start the development server:

```bash
npm run dev
```

The frontend will usually be available at:

http://localhost:5173

Keep both the frontend and backend running while using the application.

---

## 🔌 REST API Endpoints

Base URL: `http://localhost:3200`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/travel/hello` | Backend health check |
| GET | `/api/travel/cities` | Get supported cities |
| POST | `/api/travel/recommend` | Generate a travel itinerary |
| POST | `/api/travel/chat` | Ask the travel assistant |
| POST | `/api/trips` | Save a travel plan |
| GET | `/api/trips` | Retrieve saved trips |
| GET | `/api/trips/{id}` | Retrieve a specific trip |
| DELETE | `/api/trips/{id}` | Delete a saved trip |

### Example: Generate a Travel Plan

**POST** `/api/travel/recommend`

Request body:

```json
{
  "city": "Beijing",
  "budget": 3000,
  "days": 3
}
```

The API returns a structured travel plan containing itinerary details, budget information, recommendations and travel tips.

### API Response Format

Responses use a common structure:

```json
{
  "success": true,
  "code": 200,
  "message": "success",
  "data": {}
}
```

---

## 📊 Important Implementation Notes

- Travel information and estimated prices are stored in local text data files.
- Attraction prices and travel expenses are approximate estimates, not live quotations.
- The chatbot uses rule-based intent detection rather than an external LLM.
- Saved trips are currently stored in an in-memory repository. They are cleared when the backend restarts.
- The frontend uses a browser-generated anonymous user ID to separate saved trips.

---

## 🚀 Future Improvements

Potential future enhancements include:

- Integration with a generative AI travel assistant.
- Database integration for persistent trip storage.
- Real-time weather API integration.
- Interactive maps and route recommendations.
- User registration and authentication.
- Hotel and transportation booking integrations.
- Multi-language support.
- More international destinations.

---

## 🎓 Project Purpose

This project was developed as a student software project to demonstrate practical knowledge of:

- Full-stack web development.
- Java and object-oriented programming.
- Vue.js frontend development.
- Spring Boot backend development.
- REST API communication.
- MVC architecture.
- Software design patterns.
- Budget calculation and itinerary planning.

---

## 👨‍💻 Author

**Abdulsamad Mohamed Hussein**

GitHub: [@AbdulsamadMohamedHussein](https://github.com/AbdulsamadMohamedHussein)

---

<p align="center">
  Made with ❤️ using Vue.js and Java Spring Boot.
</p>
