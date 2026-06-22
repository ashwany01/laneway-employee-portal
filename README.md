# Laneway Employee Management Portal

Frontend repository: https://github.com/ashwany01/laneway-employee-portal-ui

A full-stack internal portal for managing employees, departments, projects, and staffing allocation across Laneway's distributed team (Kottayam HQ, Bangalore, and Remote).

## Tech Stack

**Backend:** Java 21, Spring Boot, Spring Data JPA, Spring Security, JWT (JJWT library), MySQL 8, Maven
**Frontend:** React 19 (Vite), React Router DOM, plain CSS (inline styles)
**Testing:** JUnit 5, RestTemplate-based integration tests

## Setup & Run Instructions

### Backend
1. Create a MySQL database named `laneway_db`.
2. In `src/main/resources/application.properties`, set your MySQL username/password.
3. Run the application (`EmployeePortalApplication.java`) via your IDE or `mvn spring-boot:run`. Tables are auto-created on startup (`ddl-auto=update`).
4. Backend runs on `http://localhost:8080`.

### Frontend
1. Navigate to the `employee-portal-ui` folder (separate repository, linked above).
2. Run `npm install` then `npm run dev`.
3. Frontend runs on `http://localhost:5173`.
4. Backend CORS is configured to allow requests from this address.

### Test Login
- Email: `rahul@laneway.com` / Password: `rahul123` (Manager)
- Email: `admin@laneway.com` / Password: `admin123` (Admin)

## Key Design Decisions

- **JWT over session-based auth**: chosen for statelessness — fits a remote-first, multi-device team better than server-side sessions, and avoids session storage overhead.
- **Manual getters/setters instead of Lombok**: switched after encountering IDE/annotation-processor friction in Spring Tool Suite; prioritized reliability over brevity.
- **No separate Location entity**: work location (Kottayam HQ / Bangalore / Remote) is modeled as an enum + timezone field directly on Employee, since locations have no independent attributes of their own in this scope.
- **Allocation overlap assumption**: project date ranges are compared as plain calendar dates, ignoring per-employee timezone differences, for simplicity. A project with no end date is treated as ongoing indefinitely.
- **Soft delete for Employees**: exited employees are flagged (`deleted=true`, status=EXITED) rather than removed, preserving historical records. Departments/Projects use hard delete or no delete, as they are simpler reference data.
- **Password security**: passwords are hashed with BCrypt before storage; login returns a generic "Invalid email or password" message for both unknown emails and wrong passwords, to avoid leaking which emails are registered.

## Known Trade-offs / Limitations

- **Manager-scoped record access** is partially implemented. Admin-only restriction is enforced on Department management; full enforcement of "Manager can only view/edit their own direct reports" would require additional per-request hierarchy checks not yet implemented due to time constraints.
- **JWT signing key** is generated fresh on each application restart (not externally configured), so tokens are invalidated on restart. In production this would be a fixed value loaded from environment configuration.
- **Hierarchy endpoint** returns full nested Employee objects rather than trimmed DTOs, resulting in larger-than-necessary payloads for deep hierarchies.
- **Automated tests** run against the live MySQL dev database rather than an isolated test database (e.g., H2), due to a dependency resolution issue with Spring's `TestRestTemplate` encountered in this environment; tests use a plain `RestTemplate` against an already-running instance instead.
- **Rate limiting** on auth/CSV endpoints was not implemented due to time constraints.

## What I'd Do With More Time

- Implement full Manager-scoped record access at the data layer.
- Move JWT signing key to environment configuration for persistence across restarts.
- Trim hierarchy/profile API responses to lightweight DTOs to avoid deep nesting and reduce payload size.
- Set up an isolated H2 test profile for fully self-contained automated tests.
- Add audit logging for employee record changes and allocation changes.
- Add Dockerized setup (Dockerfile + docker-compose) for one-command local startup.