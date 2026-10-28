# Quick Testing Guide

Use this sample walkthrough after starting both the backend and frontend. It tests the full journey without needing any extra tool.

## Before testing

1. Start MySQL and make sure the `student_portal` database exists.
2. Start the backend from the `backend` folder:

   ```bash
   mvn spring-boot:run
   ```

3. Start the frontend from the `frontend` folder:

   ```bash
   npm install
   npm run dev
   ```

4. Open `http://localhost:5173` in your browser.

The first backend launch creates demo students, mentors, projects, teams, and milestones.

## Example full test

### 1. Check the dashboard

Expected:

- The dashboard shows counts for open projects, students, teams, join requests, and milestones.
- You should see the sample projects **Campus Carpool** and **Study Buddy**.
- On a smaller phone-sized browser window, the cards should stack instead of overflowing.

### 2. Search projects

1. Click **Explore Projects**.
2. In the search box, enter `React`.

Expected: **Campus Carpool** appears because its required skills include React.

3. Clear the search box and choose **Open** from the status filter.

Expected: only projects accepting team members are shown.

### 3. Post a new project idea

1. Click **Post an Idea**.
2. Enter these example values:

| Field | Example value |
| --- | --- |
| Project title | Smart Canteen Queue |
| What are you building? | A web application that lets students view canteen queues, pre-order meals, and reduce waiting time during lunch breaks. |
| Category | Web Development |
| Ideal team size | 4 |
| Skills needed | React, Java, Spring Boot, MySQL |
| Project owner | Aarav Sharma |

3. Click **Post project idea**.

Expected: a success message confirms that the idea was submitted for mentor approval. It is not yet visible in the student project list.

### 4. Approve the project as a mentor

1. Click **Approval Desk** in the top navigation.
2. Find **Smart Canteen Queue** in the pending list.
3. Click **Approve & publish**.

Expected: a success message appears. The project now becomes visible under **Explore Projects** and accepts join requests. You can instead click **Reject** to prevent it from being published.

### 5. Check form validation

On the same form, try to submit with a description like `App for queues`.

Expected: the form refuses it because the description must be at least 20 characters long.

Also try leaving the project owner blank.

Expected: a clear message asks you to choose an owner.

### 6. Send a team join request

1. Open **Smart Canteen Queue** from the project list.
2. In **Join this team**, enter:

| Field | Example value |
| --- | --- |
| Your name | Priya Mehta |
| Email | priya.mehta@example.com |
| Why are you interested? | I have experience designing React interfaces and would like to build the ordering screens. |

3. Click **Send join request**.

Expected: the page confirms that the request was sent. Go back to the dashboard and the join request count increases.

### 7. Get mentor recommendations

1. Stay on the project detail page.
2. Click **Find mentors**.

Expected: mentors such as **Dr. Neha Rao** and **Prof. Vikram Sen** appear because their expertise matches React, Java, Spring Boot, or MySQL.

### 8. Add and update a milestone

1. Under **Milestones**, enter:

| Field | Example value |
| --- | --- |
| Add a milestone | Finish homepage design |
| Due date | Choose any future date |

2. Click **Add**.

Expected: the milestone immediately appears in the list with status **Not Started**.

3. Change its status to **In Progress**, then **Completed**.

Expected: each status change stays visible after refreshing the page because it is saved in MySQL.

## Optional API check

With the backend running, open this URL in a browser:

`http://localhost:8080/api/dashboard`

Expected: JSON data containing project, student, team, request, and milestone counts. This confirms the React app is reading live REST API data rather than static values.

## If something does not work

- If the browser says it cannot reach the API, first start the backend and confirm `http://localhost:8080/api/dashboard` opens.
- If the backend reports a database error, verify the MySQL service is running and review the username/password in `application.properties`.
- If port 8080 is occupied, change `server.port` and follow the port-change instructions in `README.md`.
