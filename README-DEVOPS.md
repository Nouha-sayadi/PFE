# 🚀 DOCUMENTATION DEVOPS - ST2I PFE

Guide complet pour la gestion DevOps du projet ST2I : GitLab CI/CD, Docker, Docker Compose.

---

## 📋 TABLE DES MATIÈRES

1. [Architecture Globale](#architecture-globale)
2. [Pré-requis](#pré-requis)
3. [Setup Initial](#setup-initial)
4. [Lancer l'Application Localement](#lancer-lapplication-localement)
5. [GitLab CI/CD Pipeline](#gitlab-cicd-pipeline)
6. [Build Docker Manuel](#build-docker-manuel)
7. [Troubleshooting](#troubleshooting)
8. [Sécurité](#sécurité)
9. [Commandes Utiles](#commandes-utiles)

---

## 🏗️ ARCHITECTURE GLOBALE

┌─────────────────────────────────────────────────────────────────┐
│ INTERNET (utilisateurs) │
└──────────────────────────┬──────────────────────────────────────┘
│ (port 80 / 443)
▼
┌─────────────────────────────────────────────────────────────────┐
│ NGINX - FRONTEND (Angular) │
│ - Serve les fichiers statiques (dist/) │
│ - Proxy /api/* vers Backend │
│ - Proxy /auth/* vers Keycloak │
│ - SPA routing (try_files) │
└──────────────────────────┬──────────────────────────────────────┘
│ (http://backend:8080)
▼
┌─────────────────────────────────────────────────────────────────┐
│ SPRING BOOT - BACKEND (Java 17 + Maven) │
│ - REST API │
│ - Business Logic │
│ - Database ORM (JPA/Hibernate) │
│ - OAuth2 Resource Server (Keycloak) │
└──────────────────────────┬──────────────────────────────────────┘
┌────────────────┼────────────────┐
│ │ │
(jdbc:mysql) (Keycloak) (PDF gen)
│ │ │
▼ ▼ ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ MySQL 8 │ │ Keycloak │ │ Flying Saucer│
│ Port 3306 │ │ Port 8080 │ │ (PDF engine)│
└──────────────┘ └──────────────┘ └──────────────┘

Network: st2i-network (Docker)
Services: mysql, keycloak, backend, frontend


---

## 📦 PRÉ-REQUIS

### Outils obligatoires :

- **Docker Desktop** (version 4.0+)
  - Inclut : Docker Engine + Docker Compose + CLI
  - Télécharge : https://www.docker.com/products/docker-desktop
  
- **Git** (pour cloner le repo)
  
- **Maven 3.9+** (optionnel : déjà dans Dockerfile)
  
- **Node.js 20+** (optionnel : déjà dans Dockerfile)

### Vérifier l'installation :

```bash
docker --version
docker-compose --version
git --version
```

---

## 🔧 SETUP INITIAL

### 1️⃣ Cloner le repo GitLab

```bash
git clone https://gitlab.hacloud.fr/snouha/pfe.git
cd pfe
```

### 2️⃣ Vérifier la structure des fichiers

pfe/
├── .gitlab-ci.yml ← Pipeline CI/CD GitLab
├── docker-compose.yml ← Orchestration Docker
├── README-DEVOPS.md ← Ce fichier
├── .env.example ← Variables d'environnement (template)
├── .env ← Variables d'environnement (local - JAMAIS commiter)
│
├── st2i/ ← Backend Spring Boot
│ ├── Dockerfile ← Image Docker backend
│ ├── .dockerignore
│ ├── src/
│ ├── pom.xml
│ └── ...
│
└── projet/ ← Frontend Angular
├── Dockerfile ← Image Docker frontend
├── .dockerignore
├── nginx.conf ← Config Nginx
├── src/
├── package.json
└── ...


### 3️⃣ Configuration des variables d'environnement

Les variables sont définies dans `.env` (créé à partir de `.env.example`) :

```bash
# JAMAIS commiter .env !
# .env contient tes secrets locaux
echo ".env" >> .gitignore
```

---

## 🚀 LANCER L'APPLICATION LOCALEMENT

### Option A : Avec Docker Compose (RECOMMANDÉ) ✅

```bash
# À la racine du projet

# 1. Build les images Docker localement
docker-compose build

# 2. Lance tous les services
docker-compose up -d

# 3. Affiche l'état
docker-compose ps

# 4. Affiche les logs en temps réel
docker-compose logs -f
```

**Accès** :
- 🌐 **Frontend** : http://localhost
- 🔧 **Backend API** : http://localhost:8081
- 🔐 **Keycloak** : http://localhost:8080
  - Admin : `admin` / `admin`
- 🗄️ **MySQL** : `localhost:3306`
  - Root : `root` / `root`
  - Database : `st2i_db`

### Option B : Build manuel (si tu veux comprendre)

```bash
# Backend
cd st2i
docker build -t st2i-backend:1.0 .
docker run -p 8081:8080 st2i-backend:1.0

# Frontend
cd ../projet
docker build -t st2i-frontend:1.0 .
docker run -p 80:80 st2i-frontend:1.0
```

### Arrêter tout

```bash
# Arrête les services
docker-compose stop

# Arrête + supprime les conteneurs
docker-compose down

# Arrête + supprime les données (volumes MySQL, etc.)
docker-compose down -v
```

---

## 📊 GITLAB CI/CD PIPELINE

### Comment ça marche ?

**Quand tu fais un `git push` :**

1. **GitLab reçoit le push**
2. **Lance automatiquement le pipeline** (défini dans `.gitlab-ci.yml`)
3. **Exécute les stages en ordre** :

BUILD (parallèle)
├─ Backend : Maven compile → JAR
└─ Frontend : npm build → dist/
↓
TEST (parallèle)
├─ Backend : JUnit + jacoco
└─ Frontend : Karma/Jasmine
↓
DOCKER (parallèle)
├─ Backend : Dockerfile → Image → Push registry
└─ Frontend : Dockerfile → Image → Push registry
↓
DEPLOY
└─ docker-compose up


### Pipeline en détail

#### Stage 1 : BUILD ⚙️

```yaml
build:backend:
  - cd st2i && mvn clean package -DskipTests
  → Crée st2i/target/*.jar

build:frontend:
  - cd projet && npm ci && npm run build
  → Crée projet/dist/
```

#### Stage 2 : TEST 🧪

```yaml
test:backend:
  - cd st2i && mvn test
  → Génère rapports JUnit + jacoco

test:frontend:
  - cd projet && npm run test -- --watch=false
  → Rapports Karma/Jasmine
```

#### Stage 3 : DOCKER 🐳

```yaml
docker:build:backend:
  - docker build -t gitlab.hacloud.fr/snouha/pfe/backend:SHA st2i/
  - docker push ...

docker:build:frontend:
  - docker build -t gitlab.hacloud.fr/snouha/pfe/frontend:SHA projet/
  - docker push ...
```

#### Stage 4 : DEPLOY 🚀

```yaml
deploy:local:
  - docker-compose up -d
  → Lance les services
```

### Voir le statut du pipeline

1. Va sur : https://gitlab.hacloud.fr/snouha/pfe
2. Clique sur "CI/CD" → "Pipelines"
3. Vois l'état de chaque stage (✅ ou ❌)
4. Clique pour voir les logs détaillés

---

## 🐳 BUILD DOCKER MANUEL

### Build une image localement

```bash
# Backend
cd st2i
docker build -t st2i-backend:latest .
docker run -p 8081:8080 st2i-backend:latest

# Frontend
cd ../projet
docker build -t st2i-frontend:latest .
docker run -p 80:80 st2i-frontend:latest
```

### Push vers le registry GitLab

```bash
# Login
docker login gitlab.hacloud.fr
# Username : ton username GitLab
# Password : access token GitLab

# Tag l'image
docker tag st2i-backend:latest gitlab.hacloud.fr/snouha/pfe/backend:latest

# Push
docker push gitlab.hacloud.fr/snouha/pfe/backend:latest
```

### Voir les images

```bash
docker images
docker ps
```

---

## 🔧 TROUBLESHOOTING

### ❌ Problem : "Port 80 already in use"

```bash
# Vérifie quel process utilise le port 80
# Windows :
netstat -ano | findstr :80

# Tue le process
taskkill /PID <PID> /F

# Ou change le port dans docker-compose.yml
ports:
  - "8000:80"  # accès sur localhost:8000 au lieu de 80
```

### ❌ Problem : "Cannot connect to MySQL"

```bash
# Vérifie que MySQL est up
docker-compose logs mysql

# MySQL met 30s à démarrer, attends un peu
# Puis relance
docker-compose restart backend
```

### ❌ Problem : "Cannot access Frontend"

```bash
# Vérifie que Nginx est en running
docker-compose logs frontend

# Probable : frontend image pas build correctement
docker-compose build --no-cache frontend
docker-compose up frontend
```

### ❌ Problem : "Backend API 502 Bad Gateway"

```bash
# Vérifie que le backend est healthy
docker-compose logs backend

# Attends 60s (Spring Boot met du temps à démarrer)
# Puis test : curl http://localhost:8081/actuator/health
```

### ❌ Problem : "Keycloak won't start"

```bash
# Vérifi la configuration Keycloak
docker-compose logs keycloak

# Probable : MySQL n'est pas ready
# Keycloak a besoin de MySQL !
docker-compose up mysql  # attends healthcheck OK
docker-compose up keycloak
```

### ❌ Problem : Tests échouent en CI/CD

```bash
# Vérifie les logs du pipeline GitLab
# Cherche "FAILED" dans les logs

# Probable causes :
# 1. Dépendances manquantes → ajoute au pom.xml ou package.json
# 2. Port déjà utilisé en CI → change les ports dans tests
# 3. Base de données pas ready → ajoute health checks

# Re-trigger le pipeline manuellement
# GitLab > CI/CD > Pipelines > (clic sur pipeline)
```

---

## 🔐 SÉCURITÉ

### 🚫 JAMAIS mettre de secrets en dur

❌ **BAD** :
```yaml
MYSQL_PASSWORD: st2i123  # Visible dans Git !
KEYCLOAK_ADMIN_PASSWORD: admin
```

✅ **GOOD** :
```yaml
# .env (JAMAIS commiter)
MYSQL_PASSWORD=${MYSQL_PASSWORD}
```

Puis dans GitLab :
- Va sur "Settings" → "CI/CD" → "Variables"
- Ajoute les secrets
- Mark as "Protected" + "Masked"

### Accès à Keycloak (production)

```yaml
# docker-compose.yml (dev)
KEYCLOAK_ADMIN: admin
KEYCLOAK_ADMIN_PASSWORD: admin

# .gitlab-ci.yml (prod)
KEYCLOAK_ADMIN: ${KEYCLOAK_ADMIN_USER}
KEYCLOAK_ADMIN_PASSWORD: ${KEYCLOAK_ADMIN_PASSWORD}
# → défini dans GitLab Variables (protégées)
```

### Accès à MySQL

Même chose : utilise des variables d'environnement protégées !

---

## 📚 COMMANDES UTILES

### Docker Compose

```bash
# Lance tout
docker-compose up -d

# Arrête
docker-compose down

# Arrête + supprime volumes
docker-compose down -v

# Logs
docker-compose logs              # Tous les logs
docker-compose logs -f           # En temps réel
docker-compose logs backend      # D'un service spécifique
docker-compose logs --tail=50 backend  # Dernières 50 lignes

# État
docker-compose ps                # État des services
docker-compose ps -a             # Incluant arrêtés

# Redémarrage
docker-compose restart backend    # Redémarre un service
docker-compose restart            # Redémarre tout

# Build
docker-compose build              # Rebuild les images
docker-compose build --no-cache   # Rebuild sans cache

# Exec (commandes dans le conteneur)
docker-compose exec backend bash  # Shell dans le backend
docker-compose exec mysql mysql -u root -p  # MySQL CLI
```

### Docker

```bash
# Images
docker images                     # Liste les images
docker rmi <image_id>            # Supprime une image

# Conteneurs
docker ps                         # Conteneurs en cours
docker ps -a                      # Tous les conteneurs
docker logs <container_id>        # Logs d'un conteneur
docker exec -it <container_id> bash  # Terminal dans conteneur

# Network
docker network ls                 # Liste les networks
docker network inspect st2i-network  # Détails d'un network

# Registry
docker login gitlab.hacloud.fr    # Login au registry
docker push <image>              # Push une image
docker pull <image>              # Pull une image
```

### Git

```bash
# Clone
git clone https://gitlab.hacloud.fr/snouha/pfe.git

# Commit + push (trigger le pipeline CI/CD)
git add .
git commit -m "Add DevOps files"
git push origin main

# Voir l'historique
git log --oneline
git log --graph --all --decorate
```

### Maven (local, sans Docker)

```bash
cd st2i
mvn clean package          # Compile
mvn test                   # Tests
mvn clean package -DskipTests  # Compile sans tests
mvn dependency:tree        # Dépendances
```

### npm (local, sans Docker)

```bash
cd projet
npm install                # Install dépendances
npm run build              # Build production
npm start                  # Dev server
npm test                   # Tests
```

---

## 🎯 RÉSUMÉ RAPIDE

**1. Développer localement :**
```bash
docker-compose up -d
# Edite le code, hot-reload automatique
```

**2. Commit + Push (CI/CD automatique) :**
```bash
git add .
git commit -m "feature: xyz"
git push origin main
# GitLab pipeline s'exécute automatiquement
```

**3. Vérifier l'état du pipeline :**
https://gitlab.hacloud.fr/snouha/pfe/pipelines

**4. En production :**
- Le pipeline pushes les images Docker au registry
- Kubernetes/Swarm peut les pull et déployer
- Ou utilise `docker-compose pull && docker-compose up -d`

---

## 📞 SUPPORT

Pour des questions :
1. Vérifie les logs : `docker-compose logs -f`
2. Cherche dans "Troubleshooting" ci-dessus
3. Consulte la doc officielle :
   - Docker : https://docs.docker.com
   - Spring Boot : https://spring.io/guides
   - Angular : https://angular.io/docs
   - Keycloak : https://www.keycloak.org/documentation

---

**Dernière mise à jour :** Août 2026  
**Version :** 1.0  
**Status :** ✅ En production