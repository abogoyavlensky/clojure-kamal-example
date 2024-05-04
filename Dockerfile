ARG CLJ_VERSION=1.11.3.1463
# TODO: combine front and back build to single step!
# TODO: run on amd64
FROM clojure:temurin-21-tools-deps-${CLJ_VERSION}-jammy AS build-js

WORKDIR /app
# System deps
ENV PATH="/root/.local/bin:/root/.local/share/mise/shims:$PATH"
RUN apt update && apt install git -y && curl https://mise.run | sh
COPY .mise.toml /app/
RUN mise install -y node

# Node deps
COPY package.json package-lock.json /app/
RUN npm i

# Clojure deps
COPY deps.edn  /app/
RUN clojure -P -X:cljs:shadow

# Build forntend
COPY . /app
RUN clojure -M:cljs:shadow release app


# Build uberjar
FROM clojure:temurin-21-tools-deps-${CLJ_VERSION}-jammy AS build-jar

WORKDIR /app

COPY deps.edn  /app/
RUN clojure -P

COPY . /app
COPY --from=build-js /app/resources/public/. /app/resources/public
RUN clojure -T:build build


# Final image
FROM eclipse-temurin:21.0.2_13-jre-jammy
LABEL org.opencontainers.image.source=https://github.com/abogoyavlensky/clojure-kamal-example

WORKDIR /app
COPY --from=build-jar /app/target/standalone.jar /app/standalone.jar

# Run application
EXPOSE 80
CMD ["java", \
     "-Xms64m", \
     "-Xmx256m", \
     # Configure jvm to use 80% of all requested memory for the app needs
     "-XX:InitialRAMPercentage=80.0", \
     "-XX:MaxRAMPercentage=80.0", \
     "-jar", \
     "standalone.jar"]
