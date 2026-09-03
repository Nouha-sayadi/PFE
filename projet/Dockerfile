
FROM node:20-alpine AS builder

# Métadonnées
LABEL stage=builder

# Définit le répertoire de travail
WORKDIR /app

# Copie package.json et package-lock.json
# (pour cache Docker)
COPY package*.json ./

# Installe les dépendances npm
# ci = clean install (plus fiable que npm install en CI/CD)
# --prefer-offline : utilise le cache si disponible
RUN npm ci --prefer-offline --no-audit

# Copie tout le code source
COPY ./ .

# Compile Angular en mode production
# --configuration production : optimisé, minifié, AOT
# --output-hashing=all : versioning des assets
RUN npm run build -- --configuration production


FROM nginx:alpine

# Métadonnées
LABEL maintainer="ST2I Development"
LABEL version="1.0"
LABEL description="Frontend Angular - ST2I PFE"

# Port exposé (80 = HTTP standard)
EXPOSE 80



# Copie la config Nginx personnalisée
COPY nginx.conf /etc/nginx/nginx.conf

# Copie SEULEMENT les fichiers compilés depuis le stage BUILD
# dist/ = output d'Angular build
COPY --from=builder /app/dist/projet /usr/share/nginx/html


HEALTHCHECK --interval=30s --timeout=10s --start-period=20s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost/index.html || exit 1



# Lance Nginx en foreground (PID 1 = signal Ctrl+C propre)
CMD ["nginx", "-g", "daemon off;"]

# 