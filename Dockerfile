FROM --platform=linux/amd64 clojure:temurin-21-tools-deps-1.11.3.1456-alpine AS build

WORKDIR /app

# Install npm
RUN echo "http://dl-cdn.alpinelinux.org/alpine/v3.20/community" >> /etc/apk/repositories
RUN apk add --update --no-cache npm=10.8.0-r0

## Node deps
COPY package.json package-lock.json /app/
RUN npm i

# Clojure deps
COPY deps.edn  /app/
RUN clojure -P -X:cljs:shadow

# Build ui and uberjar
COPY . /app
RUN npx tailwindcss -i ./resources/public/css/input.css -o ./resources/public/css/output-prod.css --minify \
    && clojure -M:dev:cljs:shadow release app \
    && clojure -T:build build


FROM --platform=linux/amd64 eclipse-temurin:21.0.2_13-jre-alpine
LABEL org.opencontainers.image.source=https://github.com/abogoyavlensky/clojure-kamal-example

WORKDIR /app
COPY --from=build /app/target/standalone.jar /app/standalone.jar
RUN apk add --no-cache curl

EXPOSE 80
CMD ["java", "-Xms64m", "-Xmx256m", "-jar", "standalone.jar"]
