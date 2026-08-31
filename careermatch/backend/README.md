# CareerMatch Backend

Spring Boot 3 (Java 17) backend for the AI-powered career matching platform.

## Stack
- Spring Boot 3.3 (Web, Data JPA, Security, Validation)
- H2 in-memory DB for dev, PostgreSQL for prod (`prod` profile)
- JWT auth (jjwt)
- Apache PDFBox / POI for resume text extraction (PDF/DOCX)
- Custom TF-IDF cosine-similarity semantic matching engine (no external ML dependency)

## Architecture

```
controller/   REST API layer (thin — delegates to services)
service/      Business logic: Auth, User, Resume, Job, Application, Dashboard, Notification
matching/     NLP/semantic-matching abstraction layer (SemanticMatcher interface +
              TfIdfSemanticMatcher default implementation) — swappable for an
              embeddings-based matcher later without touching callers
security/     JWT generation/validation + request filter
model/entity  JPA entities
model/dto     Request/response records
repository/   Spring Data JPA repositories
exception/    Centralized error handling
```

### Semantic matching layer
`SemanticMatcher` is the core abstraction: given a candidate's resume text/skills and a
job's description/required skills, it returns a `MatchResult` with an overall score,
matched/missing skills, skill coverage, and free-text semantic similarity. The default
`TfIdfSemanticMatcher` combines:
- **Skill coverage (70% weight)** — exact overlap between candidate and required skills
- **Semantic similarity (30% weight)** — TF-IDF cosine similarity between resume and job text

This keeps the matching logic dependency-free for local dev, while the interface boundary
means it can be swapped for a real embeddings provider in production.

## Running locally

**Note:** this container has no network access to Maven Central, so `mvn` cannot resolve
dependencies here. To run this locally on a machine with normal internet access:

```bash
cd backend
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. H2 console is available at `/h2-console`
(JDBC URL: `jdbc:h2:mem:careermatch`, user `sa`, no password).

Seed data (skills taxonomy, 18 sample jobs, 4 sample users) loads automatically in the
`dev` profile (default) from `src/main/resources/db/seed.sql`.

**Demo login:** `demo@careermatch.dev` / `password123`

## API overview

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | /api/auth/register | — | Create account |
| POST | /api/auth/login | — | Login, returns JWT |
| GET/PUT | /api/users/me | ✓ | Get/update profile & skills |
| POST | /api/resumes/upload | ✓ | Upload resume (PDF/DOCX/TXT), triggers skill extraction |
| GET | /api/resumes | ✓ | Resume upload history |
| GET | /api/jobs | optional | List jobs (with match score if authenticated) |
| POST | /api/jobs/search | optional | Search/filter jobs |
| GET | /api/jobs/{id} | optional | Job detail with matched/missing skills |
| POST | /api/applications | ✓ | Apply to a job |
| GET | /api/applications | ✓ | List my applications |
| PATCH | /api/applications/{id}/status | ✓ | Update application status |
| GET | /api/dashboard | ✓ | Aggregated dashboard: recommendations, funnel, skill gaps |
| GET | /api/notifications | ✓ | List notifications |
| PATCH | /api/notifications/{id}/read | ✓ | Mark notification read |

Authenticated requests need `Authorization: Bearer <token>` from the login/register response.
