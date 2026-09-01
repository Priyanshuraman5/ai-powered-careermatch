# 🚀 CareerMatch Production Deployment Guide

This guide provides step-by-step instructions for deploying the **CareerMatch** full-stack application (React Frontend + Spring Boot 3 Backend + MySQL Database + File Storage).

---

## 📑 Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Option 1: Free / Low-Cost Cloud PaaS (Recommended)](#option-1-free--low-cost-cloud-paas-recommended)
   - [Step 1: Cloud MySQL Database (Aiven / TiDB / Railway)](#step-1-create-a-free-cloud-mysql-database)
   - [Step 2: Deploy Spring Boot Backend (Render)](#step-2-deploy-backend-on-render)
   - [Step 3: Deploy React Frontend (Vercel / Netlify)](#step-3-deploy-frontend-on-vercel-or-netlify)
   - [Step 4: Connect CORS & Test](#step-4-connect-cors--verify)
3. [Option 2: Docker Compose on VPS / Cloud VM (AWS EC2 / DigitalOcean)](#option-2-docker-compose-on-vps--cloud-vm)
4. [Option 3: All-in-One Railway Deployment](#option-3-all-in-one-railway-deployment)
5. [Environment Variables Reference](#environment-variables-reference)
6. [Troubleshooting & FAQs](#troubleshooting--faqs)

---

## Architecture Overview

```
[ User Browser ]
       │
       ▼
[ Frontend: React / Vite ]  ─── (Vercel / Netlify / Nginx)
       │
       │ HTTP / JSON (Bearer JWT)
       ▼
[ Backend: Spring Boot 3 ]  ─── (Render / Railway / Docker)
       │
       ├──► [ Database: MySQL 8.0 ] (Aiven / TiDB / Cloud MySQL)
       │
       └──► [ Disk / Static Storage: uploads/ ] (Resumes, Pictures, Certificates)
```

---

## Option 1: Free / Low-Cost Cloud PaaS (Recommended)

This setup is 100% free or low-cost, handles SSL certificates automatically, and provides continuous deployment directly from your GitHub repository.

### Step 1: Create a Free Cloud MySQL Database

You can use any cloud MySQL provider. **Aiven.io** or **TiDB Cloud** offer generous free tiers:

#### Using Aiven (Free MySQL):
1. Sign up at [aiven.io](https://aiven.io/).
2. Click **Create Service** → select **MySQL** (Free Tier available).
3. Once provisioned, copy the connection details:
   - **Host** (e.g. `mysql-xyz.aivencloud.com`)
   - **Port** (e.g. `12345`)
   - **User** (e.g. `avnadmin`)
   - **Password** (e.g. `secretpassword`)
   - **Database Name** (default `defaultdb` or create `careermatch`)
4. Construct your JDBC URL:
   ```
   jdbc:mysql://<HOST>:<PORT>/<DATABASE_NAME>?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
   ```

---

### Step 2: Deploy Backend on Render

1. Sign up / Log in to [render.com](https://render.com/).
2. Click **New +** → **Web Service**.
3. Connect your GitHub repository: `https://github.com/Priyanshuraman5/ai-powered-careermatch`.
4. Configure the service:
   - **Name**: `careermatch-backend`
   - **Region**: Closest to your users (e.g., Singapore, Frankfurt, Oregon)
   - **Root Directory**: `careermatch/backend`
   - **Runtime**: **Docker** (or Java / Maven)
     - *If using Docker*: Leave build/start command empty (Render will use `Dockerfile` automatically).
     - *If using native Java*: 
       - Build Command: `mvn clean package -DskipTests`
       - Start Command: `java -jar target/career-match-backend-1.0.0.jar`
   - **Instance Type**: Free
5. In **Environment Variables**, add:
   | Variable | Value |
   |---|---|
   | `DATABASE_URL` | `jdbc:mysql://<HOST>:<PORT>/<DATABASE_NAME>?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
   | `DATABASE_USER` | `<your-db-user>` |
   | `DATABASE_PASSWORD` | `<your-db-password>` |
   | `JWT_SECRET` | `generate-a-secure-256-bit-random-string-like-abc123xyz789!!` |
   | `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,https://your-frontend.vercel.app` (you can update this once frontend is deployed) |
   | `SERPAPI_KEY` | `66b250f45f6b034af25c3960199782f2ea565276bc256f10d4b3cee5003ea64c` |
   | `UPLOAD_DIR` | `uploads` (or create a persistent disk on Render at `/var/data` and set `/var/data/uploads`) |
6. Click **Create Web Service**. Wait for the build to finish.
7. Note down your backend URL (e.g. `https://careermatch-backend.onrender.com`).

---

### Step 3: Deploy Frontend on Vercel or Netlify

#### Deploying on Vercel:
1. Log in to [vercel.com](https://vercel.com/) with GitHub.
2. Click **Add New...** → **Project**.
3. Import your GitHub repository `ai-powered-careermatch`.
4. Configure Project Settings:
   - **Framework Preset**: `Vite`
   - **Root Directory**: Click `Edit` and select `careermatch/frontend`.
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
5. Expand **Environment Variables** and add:
   | Variable | Value |
   |---|---|
   | `VITE_API_BASE_URL` | `https://careermatch-backend.onrender.com` (Your Render backend URL without trailing slash) |
6. Click **Deploy**.
7. Vercel will build and assign you a URL (e.g. `https://careermatch-frontend.vercel.app`).

#### Deploying on Netlify (Alternative):
1. Log in to [netlify.com](https://netlify.com/).
2. Click **Add new site** → **Import an existing project** → GitHub.
3. Select repo, set:
   - **Base directory**: `careermatch/frontend`
   - **Build command**: `npm run build`
   - **Publish directory**: `careermatch/frontend/dist`
4. Add environment variable `VITE_API_BASE_URL` = your backend URL.
5. Click **Deploy site**.

---

### Step 4: Connect CORS & Verify

1. Go back to Render → **careermatch-backend** → **Environment**.
2. Update `CORS_ALLOWED_ORIGINS` to include your live frontend URL:
   ```
   https://careermatch-frontend.vercel.app,http://localhost:5173
   ```
3. Save changes (Render will trigger a zero-downtime redeploy).
4. Open your frontend URL in your browser:
   - Test user registration and login.
   - Go to **Profile** → upload picture & resume → refresh page to verify persistence.
   - Go to **Jobs** → search for jobs → apply for a job.
   - Check **Skill Gap** and **Dashboard**.

---

## Option 2: Docker Compose on VPS / Cloud VM

For deploying on any Linux server (AWS EC2, DigitalOcean Droplet, Linode, Ubuntu VPS):

### 1. Install Docker & Docker Compose on your server
```bash
# Ubuntu / Debian
sudo apt update
sudo apt install -y docker.io docker-compose-plugin
sudo systemctl enable --now docker
```

### 2. Clone the repository
```bash
git clone https://github.com/Priyanshuraman5/ai-powered-careermatch.git
cd ai-powered-careermatch
```

### 3. Create `.env` file in root
```bash
nano .env
```
Paste the following:
```ini
MYSQL_ROOT_PASSWORD=strong_root_password_here
MYSQL_DATABASE=careermatch
MYSQL_USER=careermatch
MYSQL_PASSWORD=strong_user_password_here

JWT_SECRET=production_secret_key_at_least_256_bits_long_random_string!!
CORS_ALLOWED_ORIGINS=http://your-server-ip:3000,http://yourdomain.com
VITE_API_BASE_URL=http://your-server-ip:8080
SERPAPI_KEY=66b250f45f6b034af25c3960199782f2ea565276bc256f10d4b3cee5003ea64c
```

### 4. Launch Stack
```bash
docker compose up -d --build
```

### 5. Check container status & logs
```bash
docker compose ps
docker compose logs -f backend
```
- Your Frontend will be running on `http://<SERVER_IP>:3000`
- Your Backend will be running on `http://<SERVER_IP>:8080`

---

## Option 3: All-in-One Railway Deployment

1. Go to [railway.app](https://railway.app/).
2. Create **New Project** → **Provision MySQL**.
3. In the same project, click **New** → **GitHub Repo** → select `ai-powered-careermatch`:
   - Set root directory to `careermatch/backend`.
   - Add variables: `DATABASE_URL` = `${{MySQL.MYSQL_URL}}` (or standard JDBC format), `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`.
4. Click **New** → **GitHub Repo** → select `ai-powered-careermatch`:
   - Set root directory to `careermatch/frontend`.
   - Add variable: `VITE_API_BASE_URL` = backend URL generated by Railway.
5. Generate domains for both services.

---

## Environment Variables Reference

### Backend (`careermatch/backend`)
| Variable | Description | Default / Example |
|---|---|---|
| `PORT` | HTTP port for Spring Boot server | `8080` |
| `DATABASE_URL` | JDBC MySQL connection URL | `jdbc:mysql://localhost:3306/careermatch` |
| `DATABASE_USER` | MySQL database username | `root` |
| `DATABASE_PASSWORD` | MySQL database password | `password` |
| `JWT_SECRET` | Secret key used to sign and verify JWT tokens (min 256 bits) | (Required in prod) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed frontend origins or `*` | `http://localhost:5173,https://my-app.vercel.app` |
| `UPLOAD_DIR` | Server directory to store resumes, pictures, and certificates | `uploads` or `/app/uploads` |
| `SERPAPI_KEY` | API Key for external Google Jobs search integration | (SerpAPI key) |
| `JPA_DDL_AUTO` | Hibernate schema update mode | `update` |

### Frontend (`careermatch/frontend`)
| Variable | Description | Default / Example |
|---|---|---|
| `VITE_API_BASE_URL` | Base URL of deployed Spring Boot backend | Empty in dev, `https://careermatch-backend.onrender.com` in prod |

---

## Troubleshooting & FAQs

### Q: Why do I get a CORS error in the browser console?
- Make sure your backend environment variable `CORS_ALLOWED_ORIGINS` contains the exact URL of your frontend (e.g. `https://careermatch-frontend.vercel.app`) without trailing slashes.

### Q: Why do pages like `/jobs` or `/profile` return 404 when refreshed on Vercel/Netlify?
- Single Page Applications need all unknown paths redirected to `index.html`. We have added `vercel.json` and `public/_redirects` to handle this automatically.

### Q: Uploaded files (resumes, pictures) disappear after backend restarts on Render/Railway.
- Free-tier serverless containers have ephemeral filesystems. For permanent file storage across redeploys:
  1. Attach a **Persistent Disk** on Render (`/var/data`) and set `UPLOAD_DIR=/var/data/uploads`.
  2. Or use Docker Compose volume (`uploads_data:/app/uploads`) on a VPS.

---

🎉 **Your CareerMatch application is completely ready for deployment!**
