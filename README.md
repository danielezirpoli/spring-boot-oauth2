# Reference documentation
- How to
  - [How to integrate Spring Boot 3, Spring Security, and Keycloak (developers.redhat.com)](https://developers.redhat.com/articles/2023/07/24/how-integrate-spring-boot-3-spring-security-and-keycloak)
- Spring Security
  - [Spring Security::OAuth2 Client::Access Protected Resources for the Current User (docs.spring.io)](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html#oauth2-client-access-protected-resources-current-user)
  - [Spring Security::OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html#oauth2-resource-server)
  - [Spring Security::OAuth2 Resource Server::Bearer Tokens::Bearer Token Resolution (docs.spring.io)](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/bearer-tokens.html#oauth2resourceserver-bearertoken-resolver)
  - [Spring Security::OAuth2 Resource Server::JWT (docs.spring.io)](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
- Keycloak
  - [Keycloak::Importing and Exporting Realms (keycloak.org)](https://www.keycloak.org/server/importExport)
  - [Keycloak::Running Keycloak in a container (keycloak.org)](https://www.keycloak.org/server/containers)

# Keycloak
## Start Keycloak with the default image
```shell
$ podman run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin keycloak/keycloak:25.0.1 start-dev
```
http://localhost:8080/
username: admin
password: admin

## Start Keycloak with Dockerfile
```shell
$ podman build --no-cache --progress=plain -t keycloak-exercise ./keycloak
$ podman run -d --publish 8080:8080 --name my-keycloak-exercise keycloak-exercise
```
http://localhost:8080/
username: admin
password: admin

## Export/Import Keycloak realm
Once started a Keycloak container with the default image and manually configured a new realm, 
to reuse same configuration through Dockerfile, I exported the realm configuration to files stored ./keycloak/import of this project
1. execute an interactive sh shell on the container
2. run the export command in the container
3. copy export files to the host (with another shell - don't stop the sh shell)
```shell
$ podman container exec -it my-keycloak-exercise sh
$ /opt/keycloak/bin/kc.sh export --verbose --http-management-port 8085 --realm External --dir /tmp/keycloak-export --users same_file
```
```shell
$ podman machine ssh "podman cp my-keycloak-exercise:/tmp/keycloak-export /mnt/c/Users/daniele.zirpoli/tmp"
```
To apply the configuration to the image:
1. copy files from /mnt/c/Users/daniele.zirpoli/tmp to ./keycloak/import
2. build the image

## Run Docker Compose
Docker runs in a [Multipass](https://multipass.run/) VM reachable via a private IP.
In the docker-compose.yml the AUTH_SERVER_URL environment variable points to that VM private IP because it will be used to redirect the user to the login page in the authentication process.
Then before building the compose you need to update the private IP with the one actually in use.
### Build compose
This will build services
```shell
$ docker compose -f /home/ubuntu/workspace/spring-boot-oauth2/docker-compose.yml build
```
### Start compose
This will create and start containers
```shell
$ docker compose -f /home/ubuntu/workspace/spring-boot-oauth2/docker-compose.yml up
```
### Stop compose
This will stop and remove containers, networks
```shell
$ docker compose -f /home/ubuntu/workspace/spring-boot-oauth2/docker-compose.yml down
```

## Test
1. Once the compose is up, with any internet browser go to any resource exposed by the client
(eg. http://172.19.228.54:8081/v1/client/permissions/check/feign).
2. It will redirect to the Keycloak login page.
3. The user enter login credentials:
   1. username danzir (password: danzir) -> my-role-1 and my-role-2
   2. username marros (password: marros) -> only my-role-1
4. The client call the backend and returns a response
