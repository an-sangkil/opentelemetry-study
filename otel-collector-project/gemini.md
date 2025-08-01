# OpenTelemetry Collector Project

This project demonstrates a microservices architecture using Spring Boot, with OpenTelemetry for observability.

## Sub-projects

### API Gateway (`api-gateway`)

*   **Description:** The entry point for all client requests, routing them to the appropriate downstream services.
*   **Dependencies:**
    *   `spring-cloud-starter-gateway-server-webmvc`
    *   `spring-boot-starter-test`
    *   `junit-platform-launcher`

### Campaign Service (`campaign-service`)

*   **Description:** Manages marketing campaigns.
*   **Dependencies:**
    *   `spring-boot-starter-actuator`
    *   `spring-boot-starter-web`
    *   `lombok`
    *   `spring-boot-devtools`
    *   `micrometer-registry-otlp`
    *   `micrometer-registry-prometheus`
    *   `spring-boot-starter-test`
    *   `junit-platform-launcher`

### Serving Service (`serving-service`)

*   **Description:** Serves campaign content to users.
*   **Dependencies:**
    *   `spring-boot-starter-actuator`
    *   `spring-boot-starter-web`
    *   `lombok`
    *   `spring-boot-devtools`
    *   `micrometer-registry-otlp`
    *   `micrometer-registry-prometheus`
    *   `spring-boot-starter-test`
    *   `junit-platform-launcher`
