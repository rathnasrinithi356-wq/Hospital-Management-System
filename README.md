# Apex Diagnostic Laboratory Management System

A Java 17 + Spring Boot + MySQL deployment package based on the uploaded Apex Diagnostic Laboratory Management System frontend.

## Included modules

- Dashboard and summary cards
- Patient management
- Diagnostic test orders (Blood, Urine and Imaging categories)
- Sample collection
- Test-processing simulation and OOP showcase
- Result management
- Lab report generation and printing
- Spring Boot REST API for shared application data
- MySQL-backed persistence, Docker deployment and Render Blueprint configuration

The original HTML/CSS/JavaScript interface is kept under `src/main/resources/static`. Its data manager now loads and saves the five collections through the Spring Boot API, so records can be shared by users on different devices when the app is connected to the same hosted MySQL database. Browser LocalStorage remains as a local fallback/cache when the API cannot be reached.

## Technology stack

- Java 17
- Spring Boot 3.4.5
- Spring Web
- Spring Data JPA / Hibernate
- MySQL 8+
- Maven
- Docker
- Render

## Project structure

```text
diagnostic-lab-spring/
├── src/main/java/com/apex/diagnostics/
│   ├── DiagnosticLaboratoryApplication.java
│   ├── controller/LabStateController.java
│   ├── entity/LabState.java
│   ├── repository/LabStateRepository.java
│   └── service/LabStateService.java
├── src/main/resources/
│   ├── application.properties
│   └── static/
│       ├── index.html
│       ├── style.css
│       └── script.js
├── Dockerfile
├── docker-compose.yml
├── render.yaml
├── pom.xml
└── database-schema.sql
```

## Run locally with Docker (recommended)

1. Install Docker Desktop and start it.
2. Open a terminal in the extracted project folder.
3. Run:

   ```bash
   docker compose up --build
   ```

4. Open `http://localhost:8080`.
5. Check the API at `http://localhost:8080/api/health`.
6. Stop the application with `Ctrl+C`, then run `docker compose down` when needed. The named MySQL volume keeps the database data between container restarts. To delete that local data as well, use `docker compose down -v`.

The local Compose configuration uses a development-only MySQL password. Do not reuse it for a public deployment.

## Run locally without Docker

1. Install JDK 17 and Maven 3.9+.
2. Install MySQL 8+ and create a database named `diagnostic_lab` (or let the JDBC URL create it if the MySQL user has permission).
3. Set environment variables before starting the application:

   **Windows PowerShell**

   ```powershell
   $env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/diagnostic_lab?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
   $env:SPRING_DATASOURCE_USERNAME="root"
   $env:SPRING_DATASOURCE_PASSWORD="YOUR_MYSQL_PASSWORD"
   mvn spring-boot:run
   ```

   **macOS/Linux**

   ```bash
   export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/diagnostic_lab?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
   export SPRING_DATASOURCE_USERNAME='root'
   export SPRING_DATASOURCE_PASSWORD='YOUR_MYSQL_PASSWORD'
   mvn spring-boot:run
   ```

4. Open `http://localhost:8080`.

## API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Health check for Render |
| GET | `/api/state` | Read patients, tests, samples, results and reports |
| PUT | `/api/state` | Save the application state to MySQL |

The frontend calls these endpoints automatically. The current implementation stores the five serialized collections as one JSON document in the MySQL `lab_state` table. This keeps the existing frontend model and OOP demo intact while adding shared cloud persistence. It is a student/demo architecture, not a normalized clinical production database; for production, replace the snapshot with separate relational tables, add authentication/authorization, audit logging, backups and stricter validation.

## Deploy to GitHub

1. Extract this ZIP.
2. Create a new empty GitHub repository, for example `diagnostic-laboratory-management`.
3. Upload the **contents** of the extracted folder (so `pom.xml`, `Dockerfile`, `render.yaml` and `src` are at the repository root), or use Git:

   ```bash
   git init
   git add .
   git commit -m "Initial Spring Boot diagnostic lab system"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/diagnostic-laboratory-management.git
   git push -u origin main
   ```

4. Never commit database passwords or a real `.env` file. `.gitignore` excludes `.env` files.

## Deploy to Render

**Important:** Render hosts the Spring Boot web service, but this project uses MySQL. Create a MySQL database with an external managed database provider first. Render's native managed database offering is PostgreSQL, not MySQL. Make sure the database provider allows connections from your Render service (or supports public TLS connections as required).

1. Create a hosted MySQL 8 database with your chosen provider.
2. Copy its hostname, port, database name, username and password.
3. Build a JDBC URL in this format (use the provider's SSL requirements):

   ```text
   jdbc:mysql://DB_HOST:DB_PORT/DB_NAME?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
   ```

   If your provider requires a specific CA certificate or TLS mode, follow its JDBC instructions rather than blindly using the example query parameters.
4. Sign in to Render and connect your GitHub account.
5. Choose **New + → Blueprint**, then select the GitHub repository containing this project. Render reads `render.yaml` and creates the web service using the included Dockerfile.
6. In the service's Environment settings, add the values requested by the Blueprint:
   - `SPRING_DATASOURCE_URL` = your complete JDBC URL
   - `SPRING_DATASOURCE_USERNAME` = hosted MySQL username
   - `SPRING_DATASOURCE_PASSWORD` = hosted MySQL password
7. Save the environment settings and deploy. Render builds the Docker image and starts the Spring Boot application.
8. When deployment succeeds, open the `onrender.com` URL shown in the Render dashboard. Check `/api/health` to verify the service is responding.

You can alternatively create a **Web Service** manually and select the Docker runtime; the included `Dockerfile` is configured to build and run the app. Keep the database credentials in Render Environment settings, not in source code.

## Data and safety notes

- The first browser that opens a new empty deployment seeds the current demo records into the shared database. After that, browsers load the saved cloud state.
- Changes are synchronized shortly after the user performs an action. Because the current student version saves a whole-state snapshot, simultaneous edits from multiple users can overwrite one another. It is suitable for a project demonstration, not concurrent clinical operations.
- Browser LocalStorage is used as a fallback if the API is unavailable. In that situation, changes may remain only in that browser until the cloud connection is restored.
- This is an educational demonstration. Do not enter real patient/medical information. There is no user login, role-based access, encryption policy, audit trail, clinical validation, or production-grade privacy/security configuration in this version.
- Render free web services may sleep when idle; the first request after inactivity can take longer.

## Troubleshooting

- **Render cannot connect to MySQL:** Check JDBC URL, hostname, port, database name, username/password, provider IP allowlist and SSL requirements.
- **Application exits during startup:** Open Render Logs. A database connection failure is a common cause; verify the three datasource environment variables.
- **404 at the root URL:** Ensure `src/main/resources/static/index.html` exists in the GitHub repository.
- **Cloud database not syncing:** Open browser developer tools and check Network requests to `/api/state`; also check Render logs and the MySQL provider status.
- **Local Docker port conflict:** Change the host-side port mapping in `docker-compose.yml` if port 8080 or 3306 is already in use.

## License / project use

Prepared as an educational project starter. Review, test and adapt it to your institution's requirements before demonstration or further development.

## OOP concepts implemented in the Java backend

This project demonstrates three OOP concepts through laboratory test processing:

1. **Inheritance:** `BloodTest` and `UrineTest` extend the abstract `LabTest` base class and reuse its patient/sample data.
2. **Polymorphism:** `LabTestProcessingService` stores either child object in a `LabTest` reference and calls the overridden `getProcessingProcedure()` method. The correct procedure is selected at runtime.
3. **Encapsulation:** `LabTest` keeps `patientName` and `sampleId` private and final. They are initialized through a validating constructor and exposed through getters, preventing direct outside modification.

### Demonstrate the OOP implementation

After starting the Spring Boot application, send a POST request to `/api/lab-tests/process` with JSON:

```json
{
  "testType": "blood",
  "patientName": "Anita Kumar",
  "sampleId": "SMP-1001"
}
```

Change `testType` to `urine` to see a different processing procedure returned by the same endpoint. This demonstrates runtime polymorphism.

Example using curl:

```bash
curl -X POST http://localhost:8080/api/lab-tests/process \\
  -H "Content-Type: application/json" \\
  -d '{"testType":"urine","patientName":"Anita Kumar","sampleId":"SMP-1002"}'
```
