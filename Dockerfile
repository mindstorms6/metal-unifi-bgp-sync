FROM amazoncorretto:17-al2-full AS build
COPY . /app
WORKDIR /app
RUN ./gradlew installDist

FROM amazoncorretto:17-alpine
COPY --from=build /app/build/install/metal-unifi-bgp-sync /app
ENTRYPOINT /app/bin/metal-unifi-bgp-sync