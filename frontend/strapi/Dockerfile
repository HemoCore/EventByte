FROM node:22-alpine
RUN apk add --no-cache vips-dev && corepack enable pnpm && corepack prepare pnpm@9.15.9 --activate
WORKDIR /app
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --prod
COPY . .
RUN pnpm build
RUN chown -R node:node /app
USER node
EXPOSE 1337
CMD ["pnpm", "start"]
