FROM openjdk:17 AS build
COPY . /app
WORKDIR /app
RUN ./gradlew installDist

FROM openjdk:17
COPY --from=build /app/build/install/metal-unifi-bgp-sync /app
ENTRYPOINT /app/bin/metal-unifi-bgp-sync