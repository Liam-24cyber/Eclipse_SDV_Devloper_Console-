# SDV Developer Console

This repository contains the SDV Developer Console UI and supporting services used to manage and inspect simulation runs.

## 🎥 Demo Video
**Phase 1 Extension Implementation Demo:** [Watch on Google Drive](https://drive.google.com/file/d/12nKfTzmzRVWHeS9pZUhZa4PUaauLeYIs/view?usp=drive_link)


Key features
- Results page: paginated, sortable table of simulations
- Row actions: Results, View, Delete (confirmation + immediate UI update)
- Demo status progression: new simulations show "Running" for 10s then transition to "Done"
- Persistent deletion: deleted simulation IDs are stored in localStorage and filtered in the UI

Prerequisites
- macOS (instructions assume zsh)
- Node.js (LTS recommended, e.g. 18+)
- npm (bundled with Node)
- Java JDK 11+ and Maven (for backend services)
- Docker & Docker Compose (optional — for full-stack local run)
- git

Repository layout (top-level)
- developer-console-ui/ — Next.js frontend application (TypeScript + React)
- dco-gateway/, scenario-library-service/, tracks-management-service/, ... — backend services (Maven)
- docker-compose.yml — optional full-stack compose for local testing

Quick start — Frontend only (dev)
1. Open terminal (zsh)
2. Install and run frontend:
   - cd "developer-console-ui/app"
   - npm ci
   - npm run dev
3. Open browser: http://localhost:3000

Full stack (recommended for integration)
1. Ensure Docker is running
2. From repo root:
   - docker compose up --build
3. Wait for services to start, then open UI (usually http://localhost:3000)

Build & Production
- Frontend build:
  - cd developer-console-ui/app
  - npm ci
  - npm run build
  - npm run start
- Backend build (example):
  - mvn -f dco-gateway/pom.xml clean package

Configuration notes
- GraphQL endpoint: the frontend uses `Link` defined in `developer-console-ui/app/libs/apollo`. Ensure it points to your gateway (GraphQL) service.
- Auth: some calls read `token` from localStorage. For local testing you can set this value via the browser devtools or use the app login.
- Deleted simulations: UI persists deleted IDs in localStorage key `deletedSimulations` (demo persistence). If you want server-side deletion, ensure the GraphQL mutation `deleteSimulation` is supported by your backend.

Troubleshooting
- Delete fails: confirm `Link` endpoint is reachable and that the GraphQL server supports `deleteSimulation`. Check browser console and gateway logs.
- TypeScript/compile issues: cd developer-console-ui/app && npm ci && npm run build to show errors.
- Tests failing: some unit tests may require specific mocks or package versions; run frontend tests with `npm test` from `developer-console-ui/app`.

Development workflow
- Branch from `develop-results-page` or `develop` for feature work
- Commit messages: short subject + optional body (see examples used in repo)
- Open PR describing changes and testing steps

Useful scripts
- docker-compose.yml — start a local stack
- 10-build-script.sh, 20-deploy-script.sh, 30-destroy-script.sh — helper scripts

License
See LICENSE.md in repository root.

Contact
Open an issue in the repo for questions or assign to the repository owner for handover.

