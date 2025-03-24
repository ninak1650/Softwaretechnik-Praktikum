# compile vue app
FROM node:22-alpine AS builder
WORKDIR /app
COPY . .
ENV VITE_API_BASE_URL="/api"
RUN npm ci
RUN npm run build

# build final image
FROM caddy:2.9-alpine AS final
COPY Caddyfile /etc/caddy/Caddyfile
COPY --from=builder /app/dist /srv
