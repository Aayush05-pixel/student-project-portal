# Student Project & Team Formation Portal

A beginner-friendly full-stack portal for students to share project ideas, form teams, get mentor suggestions, and track milestones.

## What it includes

- Dashboard with project, student, team, request, and milestone summaries
- Searchable project directory with skill and status filters
- Project idea form with browser and server-side validation
- Project detail view, team join requests, and mentor recommendations
- Milestone creation and progress tracking
- Responsive blue-and-white React interface
- Spring Boot REST API with MySQL persistence and seeded demo data

## Prerequisites

- Java 17 or newer
- Node.js 18 or newer (includes npm)
- MySQL 8.0 or newer

## 1. Set up the database

Open MySQL Workbench (or the MySQL command line) and run:

```sql
CREATE DATABASE student_portal;
```

The tables are created automatically when the backend starts. If your MySQL user has a password, update `backend/src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

## 2. Start the backend

Open a terminal in the `backend` folder:

```bash
mvn spring-boot:run
```

The API starts at `http://localhost:8080`. The first run inserts a few students, projects, and mentors.

> If `mvn` is not recognized, install Maven or open the backend folder in IntelliJ IDEA and run `PortalApplication.java`.

## 3. Start the frontend

Open a second terminal in the `frontend` folder:

```bash
npm install
npm run dev
```

Open the address shown by Vite, normally `http://localhost:5173`.

## Common fixes

- **Cannot connect to MySQL:** ensure the MySQL service is running and the username/password in `application.properties` are correct.
- **Port 8080 already in use:** change `server.port=8081` in `application.properties`, then set `VITE_API_URL=http://localhost:8081/api` in a `frontend/.env` file.
- **Empty dashboard:** confirm the backend is running before opening the frontend.

## Project layout

```
Student-Project-Portal/
├── backend/   Spring Boot REST API + MySQL entities
└── frontend/  React + Vite responsive interface
```

## API overview

| Endpoint | Purpose |
| --- | --- |
| `GET /api/dashboard` | Dashboard numbers and recent projects |
| `GET /api/projects` | List/search/filter projects |
| `POST /api/projects` | Post a project idea |
| `GET /api/projects/{id}` | Project details |
| `POST /api/join-requests` | Request to join a project |
| `GET /api/mentors/recommendations?skills=React,Java` | Find matching mentors |
| `POST /api/milestones` | Add a milestone |
| `PATCH /api/milestones/{id}/status` | Update milestone status |

