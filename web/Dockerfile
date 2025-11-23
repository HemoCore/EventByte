FROM node:22-alpine
RUN corepack enable
WORKDIR /app
COPY . .
RUN pnpm install
EXPOSE 5173
CMD ["pnpm", "dev"]
