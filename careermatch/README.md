# Profile persistence fix — summary

## Exact root causes (found in the files you sent)

1. **Endpoint mismatch (the main bug).**
   `UserProfileController` is mapped at `/api/profile/me`, but `client.js`
   was calling `/api/users/me`:
   ```js
   getProfile: () => api.get('/users/me'),
   updateProfile: (data) => api.put('/users/me', data),
   ```
   A comment in your controller even says *"Changed from /api/users to
   /api/profile"* — the rename was never propagated to the frontend. If
   `/api/users/me` is handled by some other (basic account) controller in
   your project, that explains why the save *looked* successful (200 OK)
   while `UserProfileService` was never actually invoked, so nothing was
   written to `user_profiles`. **Fixed in `client.js`.**

2. **Resume was never actually uploaded.**
   `handleResume()` in `Profile.jsx` only sent `{ name: file.name }` in the
   JSON body — the PDF/DOCX bytes never left the browser. There was nothing
   on the server to fetch back after refresh, even once bug #1 is fixed.
   **Fixed:** real `multipart/form-data` upload to a new endpoint that
   stores the file and returns its URL.

3. **Profile picture was Base64, not a file.**
   `handleProfileImage()` read the image as a Base64 data URL and pushed it
   into the JSON body, stored in a `LONGTEXT` column. This technically
   "worked" but violates your requirement of file storage + DB path, bloats
   the DB, and is slow. **Fixed:** real multipart upload, file saved to
   disk, only the URL is stored in MySQL.

4. **No file storage / static serving existed at all.** There was no
   upload directory, no `FileStorageService`, and no static resource
   mapping to serve saved files back to the browser. **Added.**

5. **Certificates only ever had metadata (name/issuer/date), never an
   actual document.** There was no upload input or backend support for a
   certificate *file*. **Added** as an additive feature (your existing
   certification-metadata cards are untouched).

## Files changed / added

**Backend** (`/mnt/user-data/outputs/backend/`)
| File | Change |
|---|---|
| `FileStorageService.java` | **New.** Saves files to disk under `uploads/{userId}/{picture|resume|certificates}/...`, returns a public URL. |
| `FileStorageConfig.java` | **New.** Serves that folder as static resources at `/uploads/**`. |
| `UserProfileController.java` | Fixed mapping cleanup; added `POST /api/profile/me/picture`, `POST /api/profile/me/resume`, `POST /api/profile/me/certificates`. |
| `UserProfileService.java` | Added `saveProfilePicture`, `saveResume`, `addCertificateFiles`; DTO mapping extended. |
| `UserProfile.java` | Added `resumeUrl`, `certificateFilesJson` columns; `profileImage` now holds a URL, not Base64. |
| `UserProfileDto.java` | Added `resumeUrl`, `certificateFiles` (+ nested `CertificateFile`). |
| `application.yml` | Added `app.upload.dir` / `app.upload.base-url`. |
| `UserProfileRepository.java` | **Unchanged** — already correct. |

**Frontend** (`/mnt/user-data/outputs/frontend/`)
| File | Change |
|---|---|
| `client.js` | Fixed `/users/me` → `/profile/me`; added `uploadProfilePicture`, `uploadResume`, `uploadCertificateFiles`; added `resolveFileUrl()` helper. |
| `Profile.jsx` | `handleProfileImage` and `handleResume` now do real multipart uploads instead of Base64/filename-only; added `handleCertificateFiles` + a "Certificate Documents" section; resume/picture rendering now resolves the backend file URL. |

## ⚠️ One thing I could not see/fix (please check)

You didn't upload your **SecurityConfig / JWT filter**. Two things to verify there:
1. `principal.getName()` in the controller must return the same `userId`
   your `UserProfile.userId` column expects (e.g. the username/email you put
   as the JWT subject). If profile data still doesn't survive refresh after
   this fix, this is the next place to look.
2. Add `.requestMatchers("/uploads/**").permitAll()` (GET only) to your
   security filter chain — `<img src>` and file-download requests won't
   carry an `Authorization` header, so the static files will 404/403 if
   this route requires auth.

If you can share `SecurityConfig.java` / `JwtAuthenticationFilter.java`, I'll verify this piece too.

## Database changes

None manual — with `ddl-auto: update` (already set in your `application.yml`),
Hibernate will auto-add the new columns on next boot:
- `user_profiles.resume_url` (VARCHAR)
- `user_profiles.certificate_files_json` (LONGTEXT)
- `user_profiles.profile_image` stays the same column, just holds a shorter URL string going forward (old Base64 rows, if any, will simply be ignored/overwritten next time the user re-uploads a picture)

For production (`ddl-auto: validate`), run:
```sql
ALTER TABLE user_profiles ADD COLUMN resume_url VARCHAR(500);
ALTER TABLE user_profiles ADD COLUMN certificate_files_json LONGTEXT;
```

## API endpoints (new/changed)

| Method | Path | Body | Purpose |
|---|---|---|---|
| GET | `/api/profile/me` | — | Fetch the logged-in user's full profile (unchanged) |
| PUT | `/api/profile/me` | JSON `UserProfileDto` | Save text fields / skills / education / etc. (unchanged, now actually reachable) |
| POST | `/api/profile/me/picture` | multipart `file` | **New** — upload profile picture |
| POST | `/api/profile/me/resume` | multipart `file` | **New** — upload resume |
| POST | `/api/profile/me/certificates` | multipart `files` (repeatable) | **New** — upload one or more certificate documents |

## Data flow (now correct end-to-end)

```
React (Profile.jsx)
  → Axios (client.js, correct /api/profile/... paths, JWT in header)
    → Spring Controller (UserProfileController)
      → Service (UserProfileService)
        → Repository (UserProfileRepository) → MySQL (user_profiles table)
        → FileStorageService → disk (uploads/{userId}/...)
      ← returns UserProfileDto with URLs, not raw bytes
  ← React stores response in state, renders <img>/links against resolveFileUrl(url)
On refresh: useEffect → loadProfile() → GET /api/profile/me → same data, same files
```

## Testing steps

1. **Backend**: drop the new files into your project at the matching
   packages, restart Spring Boot. Confirm no startup errors and that
   `user_profiles` gets the two new columns (check logs / `DESCRIBE user_profiles;`).
2. **Frontend**: replace `client.js` and `Profile.jsx`, restart the dev server.
3. Log in, go to Profile.
4. Upload a profile picture → confirm toast success → **refresh the page** → picture still shows.
5. Upload a resume → refresh → resume card still shows, click it → file opens/downloads.
6. Upload a certificate document → refresh → still listed, link works.
7. Edit basic info (name, headline, about) → refresh → still there.
8. Add a skill, an education entry, an experience entry → refresh → all still there.
9. Restart the whole backend (`ctrl+C`, re-run) → repeat step 3 → everything should still be there, since it now all lives in MySQL + the `uploads/` folder, not memory/browser storage.
10. Open browser DevTools → Network tab while doing step 4-6 and confirm requests go to `/api/profile/me/picture` etc. and return `200` with a JSON body containing a `profileImage`/`resumeUrl` string starting with `/uploads/...`.
