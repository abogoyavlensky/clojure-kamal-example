FROM eclipse-temurin:21.0.2_13-jre-jammy AS build

WORKDIR /app
# System deps
ENV PATH="/root/.local/bin:/root/.local/share/mise/shims:$PATH"
RUN apt update && apt install git -y && curl https://mise.run | sh
COPY .tool-versions /app/
RUN mise install -y node clojure

# Node deps
COPY package.json package-lock.json /app/
RUN npm i

# Clojure deps
COPY deps.edn  /app/
RUN clojure -P -X:cljs:shadow

# Build frontend and jar
COPY . /app
RUN clojure -M:cljs:shadow release app
RUN clojure -T:build build

# Result image
FROM eclipse-temurin:21.0.2_13-jre-jammy
LABEL org.opencontainers.image.source=https://github.com/abogoyavlensky/clojure-kamal-example

WORKDIR /app
COPY --from=build /app/target/standalone.jar /app/standalone.jar

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
