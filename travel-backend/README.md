# Travel Backend (Spring Boot, MVC)

REST API for the Travel Assistant H5 app. Java 25 (LTS), Maven, Spring Boot, Lombok. Runs on **port 3200**.

## Run
1. Open this folder in IntelliJ IDEA (File > Open > pom.xml > Open as Project) and wait for Maven to sync.
   - If you created your own project with Spring Initializr (as in the course guide, Spring Boot 4.x), keep **your** `pom.xml`
     and copy only `src/main/java`, `src/main/resources` and `src/test` from here into your project.
2. Run `TravelBackendApplication`.
3. Open http://localhost:3200/api/travel/hello - you should see a JSON result with "Hello World!".

Tests: `mvn test` (or right-click `src/test` > Run Tests in IntelliJ). No Spring context is needed for them.

## Package structure (MVC)
| Package | Role |
|---|---|
| `controller` | HTTP layer only: `TravelController`, `TripController` |
| `service` | Business logic: `TripPlanService`, `BudgetCalculator`, `ChatService`, `TripService` |
| `model` | Domain objects: abstract `Place` -> `Attraction`, `FoodSpot`, `FreeTimePlace`; `City`, `DayItinerary` |
| `planner` | Strategy + Template Method: `ItineraryPlanner`, `AbstractItineraryPlanner`, `StandardPlanner`, `EconomyPlanner` |
| `repository` | Data access: `CityRepository` (text files), `TripRepository` (in memory) |
| `vo` | Data sent to / from the app: `Result`, `TripPlan`, `DayPlan`, `SpotSlot`, `BudgetBreakdown`, ... |
| `exception`, `config` | `BusinessException`, `GlobalExceptionHandler`, CORS |

City and place data lives in `src/main/resources/data/cities.txt` and `places.txt` (pipe-separated). Prices are approximate.

## API
All responses use `Result`: `{ "success": true, "code": 200, "message": "success", "data": ... }`

| Method | Path | Body / header | Description |
|---|---|---|---|
| GET | `/api/travel/hello` | - | Health check |
| GET | `/api/travel/cities` | - | Supported cities |
| POST | `/api/travel/recommend` | `{"city":"Beijing","budget":3000,"days":3}` | Day-by-day plan with budget breakdown |
| POST | `/api/travel/chat` | `{"message":"Best place to visit in Beijing","city":""}` | Streaming answer (Server-Sent Events) |
| POST | `/api/trips` | same as recommend + header `X-User-Id` | Save a trip |
| GET | `/api/trips` | header `X-User-Id` | List saved trips (newest first) |
| GET | `/api/trips/{id}` | header `X-User-Id` | One saved trip |
| DELETE | `/api/trips/{id}` | header `X-User-Id` | Delete a saved trip |

Validation errors return `success:false` with a readable `message`, e.g. days outside 1-7, unknown city, budget <= 0.

## Test with API Fox (base URL `http://127.0.0.1:3200`)
1. `GET /api/travel/hello` -> 200, `data` = "Hello World!"
2. `POST /api/travel/recommend` body `{"city":"Beijing","budget":3000,"days":3}` -> `data.dailyItinerary` has 3 days
3. Same with `"budget":500` -> `data.strategy` = "Economy" and a warning about the budget
4. `POST /api/travel/recommend` body `{"city":"Beijing","budget":3000,"days":9}` -> `success:false`, message about days
5. `POST /api/travel/chat` body `{"message":"Guangzhou's tallest building"}` -> a stream of `data:{"type":"chunk",...}` ending in `data:{"done":true}`
6. `POST /api/trips` with header `X-User-Id: test1` and the recommend body, then `GET /api/trips` with the same header
