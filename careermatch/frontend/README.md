# CareerMatch Frontend

React + Vite single-page app for the CareerMatch platform.

## Setup

```bash
cd frontend
npm install
npm run dev
```

Runs on `http://localhost:5173` and proxies `/api/*` requests to the backend at
`http://localhost:8080` (see `vite.config.js`).

## Structure

```
src/
  api/client.js         Axios instance + API call helpers, attaches JWT from localStorage
  context/AuthContext.jsx  Auth state (user, token, login/register/logout)
  components/
    Navbar.jsx           Top nav, shows auth state
    ProtectedRoute.jsx   Redirects to /login if not authenticated
  pages/
    Landing.jsx
    Login.jsx
    Register.jsx
    ResumeUpload.jsx
    Dashboard.jsx
    Jobs.jsx              Listing + search
    JobDetail.jsx
    SkillGap.jsx
    Applications.jsx
    Notifications.jsx
    Profile.jsx
  App.jsx                Routes
  main.jsx               Entry point
```

**Note:** `npm install` was not run in this environment (no build step performed here) —
run it locally before `npm run dev`.
