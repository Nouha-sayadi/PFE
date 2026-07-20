# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Full-stack project management system (ST2I) with two separate applications:
- **`projet/`** — Angular 21 frontend (admin dashboard)
- **`st2i/`** — Spring Boot 4 backend (REST API)

Both are secured with Keycloak OAuth2/JWT. The system manages projects, users, deliverables, costs, KPIs, contracts, invoices, risks, and assignments.

## Development Setup Requirements

All four services must be running simultaneously:
- **Keycloak** at `http://localhost:8081` — realm: `st2i-realm`
- **MySQL** at `localhost:3306` — database: `st2i_db`
- **Backend** at `http://localhost:8080`
- **Frontend** at `http://localhost:4200`

## Frontend (`projet/`)

```bash
npm start          # Dev server on port 4200
npm run build      # Production build
npm run watch      # Build in watch mode
npm test           # Karma/Jasmine unit tests
```

Angular CLI 21.2.2, TypeScript 5.9.3 (strict mode). Uses standalone components (no NgModules).

## Backend (`st2i/`)

```bash
./mvnw spring-boot:run     # Dev server on port 8080
./mvnw test                # JUnit tests
./mvnw clean package       # Build JAR
```

Spring Boot 4.0.4, Java 17, Maven wrapper included.

## Architecture

```
Angular Frontend (projet/)
  └── HTTP + JWT interceptor
        └── REST API calls
Spring Boot Backend (st2i/)
  ├── SecurityConfig.java       OAuth2 JWT validation via Keycloak
  ├── RestController/           23+ controllers (one per domain entity)
  ├── Services/                 Business logic; KpiAutoCalculeService uses @Scheduled
  ├── Entities/                 18+ JPA entities mapped to MySQL tables
  ├── Repositories/             Spring Data JPA repos
  └── DTO/                      Request/Response DTOs separate from entities
MySQL (st2i_db, DDL auto-update enabled)
Keycloak (external auth server — manages users, roles, tokens)
```

## Frontend Structure

- `src/app/keycloak/` — auth guard, permission guard, HTTP interceptor (adds Bearer token), Keycloak init factory
- `src/app/services/` — typed Angular services, one per backend resource
- `src/app/pages/` — feature pages (dashboard, charts, tables, forms, auth-pages)
- `src/app/shared/` — layout shell and reusable dashboard components
- Routing: `app.routes.ts` (lazy-loaded feature routes, guarded by `auth.guard.ts` and `permission.guard.ts`)
- Styling: Tailwind CSS v4 via PostCSS (`postcss.rc.json`)

## Backend Structure

- `Config/SecurityConfig.java` — Spring Security OAuth2 resource server, JWT decoder pointed at Keycloak
- `Config/CorsConfig.java` — allows requests from `localhost:4200`
- `Services/KeycloakService.java` — Keycloak Admin Client for user/role management
- `Services/KpiAutoCalculeService.java` — scheduled KPI calculations
- `Services/PermissionService.java` — role-based permission checks
- `Exception/` — global exception handlers
- `enums/` — shared Java enums used across entities

## Key Domain Entities

`Projet`, `Utilisateur`, `Affectation`, `Livrable`, `Contrat`, `Facture`, `Risque`, `Action`, `KpiMensuelProjet`, `CoutPrev`, `CoutReel`, `Pointage`, `Avenant`, `TCC`, `Estimation`, `ModeleBusiness`, `Partenaire`, `Bailleur`, `Client`, `Devise`

## Configuration Files

- `st2i/src/main/resources/application.yaml` — DB URL, Keycloak issuer URI, JPA DDL mode, port
- `projet/src/app/app.config.ts` — Angular providers including Keycloak config (realm, client ID, URL)
- `projet/angular.json` — build targets, test runner config
- `projet/tsconfig.json` — strict TypeScript settings
