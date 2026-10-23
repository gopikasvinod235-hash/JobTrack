# JobTrack Full-Stack College Project

This project connects the JobTrack frontend to a Java Spring Boot backend.

## Requirements
- Java 17 or newer
- Maven 3.6+ (or use a Java/Maven-enabled cloud workspace)

## Run
From the project folder, run:

```bash
mvn spring-boot:run
```

Then open http://localhost:8080 in the same browser/device running the server.

## How it works
- Frontend: `src/main/resources/static/index.html` (HTML/CSS/JavaScript)
- Backend: Spring Boot REST API under `/api`
- OOP model: `Application.java`
- Operations + persistence + activity log: `ApplicationTracker.java`
- REST endpoints: `ApplicationController.java`
- Saved records: `data/applications.json`
- Activity log: `data/application_logs.txt`

API routes:
- `GET /api/applications` list records
- `GET /api/applications?company=google` search by company
- `POST /api/applications` create record
- `PUT /api/applications/{id}` edit record/status
- `DELETE /api/applications/{id}` delete record
- `GET /api/logs` activity log

This uses a JSON file for persistence rather than a separate SQL database, keeping it lightweight for a college OOP project. The frontend and backend are served from the same Spring Boot app, so the browser calls the Java API on the same origin.

## Public sharing
This is source code, not yet a deployed public website. To share a live URL, deploy the Spring Boot application to a Java-capable host. A static host alone cannot run this Java backend.
