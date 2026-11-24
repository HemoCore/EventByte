FROM node:22-alpine
RUN corepack enable && corepack prepare pnpm@9.15.9 --activate
WORKDIR /app
COPY . .
RUN pnpm install
EXPOSE 5173
CMD ["pnpm", "dev", "--host"]
