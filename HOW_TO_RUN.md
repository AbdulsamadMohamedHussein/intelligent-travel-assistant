# How to run Travel Assistant (2 parts, both must run)

## Install once
- JDK 25 (LTS)  (check: `java -version`)
- Node.js 18 or newer  (check: `node -v`)
- VS Code  (for the website)  and  IntelliJ IDEA  (for the Java server, as in your course guide)

## Part 1 - Server (travel-backend)  -> keep it running
1. Open IntelliJ IDEA > File > Open > choose the `travel-backend` folder (select pom.xml > Open as Project).
2. Wait until Maven finishes loading (bottom bar).
3. Open `src/main/java/com/example/travelbackend/TravelBackendApplication.java` and click the green Run button.
4. Check in a browser: http://localhost:3200/api/travel/hello  -> you should see "Hello World!" inside JSON.

## Part 2 - Website (travel-h5)
1. Open the `travel-h5` folder in VS Code (File > Open Folder).
2. Open the terminal in VS Code (Terminal > New Terminal) and run:
       npm install
       npm run dev
3. Open the address shown (usually http://localhost:5173). Best viewed in the browser's phone mode (F12 > phone icon).

## If something fails
- "Cannot reach the server": Part 1 is not running, or is not on port 3200.
- Port 3200 busy: close the other program, or change `server.port` in `src/main/resources/application.yml` AND `VITE_API_BASE` in `travel-h5/.env`.
- Maven error about wrapper version: in `.mvn/wrapper/maven-wrapper.properties` use Maven 3.9.9 (course guide step 8), or use IntelliJ's bundled Maven.
