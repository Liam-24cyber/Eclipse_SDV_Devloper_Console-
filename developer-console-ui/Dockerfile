FROM node:18-alpine AS builder
WORKDIR /app

# Install dependencies
COPY developer-console-ui/app/package.json developer-console-ui/app/yarn.lock ./ 
COPY developer-console-ui/app/dco-sdv-ui-1.120.0.tgz ./dco-sdv-ui-1.120.0.tgz
RUN yarn install --frozen-lockfile

# Build the Next.js app
COPY developer-console-ui/app/ .
RUN yarn build

FROM node:18-alpine AS runner
RUN addgroup -g 1001 -S nodejs && adduser -u 1001 -S nextjs
WORKDIR /app

ENV NODE_ENV=production

# Copy the built app and runtime assets
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/package.json ./package.json
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/next.config.js ./next.config.js

USER nextjs
EXPOSE 3000
CMD ["yarn", "start"]
