FROM quay.io/keycloak/keycloak:25.0.1
ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin
COPY ./import/ /opt/keycloak/data/import/
COPY healthcheck-script.sh ./
CMD ["start-dev", "--import-realm", "--health-enabled=true", "--http-management-port=9090"]