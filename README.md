##### customer-ms-quarkus

# Microservice Apps Integration with CouchDB Database and enabling OpenID Connect protection for APIs

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://cloudnativereference.dev/*

## Table of Contents

* [Introduction](#introduction)
    + [APIs](#apis)
* [Pre-requisites](#pre-requisites)
* [Running the application](#running-the-application)
    + [Get the Customer application](#get-the-customer-application)
    + [Run the CouchDB Docker Container](#run-the-couchdb-docker-container)
    + [Set Up Keycloak](#set-up-keycloak)
    + [Run the Jaeger Docker Container](#run-the-jaeger-docker-container)
    + [Run the SonarQube Docker Container](#run-the-sonarqube-docker-container)
    + [Run the Customer application](#run-the-customer-application)
    + [Validating the application](#validating-the-application)
    + [Exiting the application](#exiting-the-application)
* [Conclusion](#conclusion)
* [References](#references)

## Introduction

This project will demonstrate how to deploy a Quarkus application with a CouchDB database. This application provides basic operations of creating and querying customer profiles from [Apache's CouchDB](http://couchdb.apache.org/) NoSQL database as part of the Customer Profile function of Storefront.

![Application Architecture](static/customer.png?raw=true)

Here is an overview of the project's features:
- Leverages [`Quarkus`](https://quarkus.io/), the Supersonic Subatomic Java Framework.
- Persist Customer data in an [Apache's CouchDB](http://couchdb.apache.org/) NoSQL database using the official [Cloudant Java library](https://github.com/cloudant/java-cloudant).

### APIs

The Customer Microservice REST API is OAuth 2.0 protected. These APIs identifies and validates the caller using JWT tokens.

* `GET /micro/customer`

  Returns the customer profile information. The caller of this API must pass a valid OAuth token. The OAuth token is a JWT with the customer name of the caller encoded in the `user_name` claim. A JSON object array is returned consisting of customer details.

## Pre-requisites:

* [Java](https://www.java.com/en/)

## Running the application

### Get the Customer application

- Clone customer repository:

```bash
git clone https://github.com/ibm-garage-ref-storefront/customer-ms-openliberty.git
cd customer-ms-openliberty
```

### Run the CouchDB Docker Container

Run the below command to get CouchDB running via a Docker container.

```bash
# Start a CouchDB Container with a database user, and a password
docker run -d -e COUCHDB_USER='admin' -e COUCHDB_PASSWORD='password' -p 5984:5984 couchdb
```

If it is successfully deployed, you will see something like below.

```
$ docker ps
CONTAINER ID   IMAGE                              COMMAND                  CREATED             STATUS             PORTS                                        NAMES
832a4a2143a5   couchdb                            "tini -- /docker-ent…"   About an hour ago   Up About an hour   4369/tcp, 9100/tcp, 0.0.0.0:5984->5984/tcp   awesome_jackson
```

- Populate the database with user information.

```
cd populate
python3 populate.py localhost 5984
```

### Set Up Keycloak

In storefront, Keycloak is used for storing users and authenticating users. To configure it, refer [Keycloak - JWT token generation](https://cloudnativereference.dev/related-repositories/keycloak/).

### Run the Jaeger Docker Container

Set up Jaegar for opentracing. This enables distributed tracing in your application.

```
docker run -d -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 jaegertracing/all-in-one:latest
```

If it is successfully run, you will see something like this.

```
$ docker run -d -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 jaegertracing/all-in-one:latest
1c127fd5dfd1f4adaf892f041e4db19568ebfcc0b1961bec52a567f963014411
```

### Run the SonarQube Docker Container

Set up SonarQube for code quality analysis. This will allow you to detect bugs in the code automatically and alerts the developer to fix them.

```
docker run -d --name sonarqube -p 9000:9000 sonarqube
```

If it is successfully run, you will see something like this.

```
$ docker run -d --name sonarqube -p 9000:9000 sonarqube
1b4ca4e26ceaeacdfd1f4adaf892f041e4db19568ebfcc0b1961b4ca4e26ceae
```

### Run the Customer application

#### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev -Dibm.cn.application.couchdb.client.CouchDBClientService/mp-rest/url=http://localhost:5984 -Dcouchuser=admin -Dcouchpassword=password -Dquarkus.oidc.auth-server-url=http://localhost:8085/auth/realms/sfrealm -Dquarkus.oidc.client-id=bluecomputeweb -Dquarkus.oidc.credentials.secret=<replace_with_keycloak_client_secret> -DJAEGER_SERVICE_NAME=customer-ms-quarkus -DJAEGER_SAMPLER_TYPE=const -DJAEGER_SAMPLER_PARAM=1 -DJAEGER_AGENT_HOST=localhost -DJAEGER_AGENT_PORT=6831
```

If it is successful, you will see something like this.

```
[INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ customer-ms-quarkus ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- quarkus-maven-plugin:1.11.3.Final:dev (default-cli) @ customer-ms-quarkus ---
Listening for transport dt_socket at address: 5005
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2021-02-19 16:21:58,311 INFO  [io.quarkus] (Quarkus Main Thread) customer-ms-quarkus 1.0.0-SNAPSHOT on JVM (powered by Quarkus 1.11.3.Final) started in 1.780s. Listening on: http://localhost:8080
2021-02-19 16:21:58,314 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2021-02-19 16:21:58,314 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, oidc, rest-client, rest-client-jackson, resteasy, resteasy-jackson, security]
```

#### Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `customer-ms-quarkus-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using the below command.

```
java -jar -Dibm.cn.application.couchdb.client.CouchDBClientService/mp-rest/url=http://localhost:5984 -Dcouchuser=admin -Dcouchpassword=password -Dquarkus.oidc.auth-server-url=http://localhost:8085/auth/realms/sfrealm -Dquarkus.oidc.client-id=bluecomputeweb -Dquarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826 -jar target/customer-ms-quarkus-1.0.0-SNAPSHOT-runner.jar
```

If it is run successfully, you will see something like below.

```
$ java -jar -Dibm.cn.application.couchdb.client.CouchDBClientService/mp-rest/url=http://localhost:5984 -Dcouchuser=admin -Dcouchpassword=password -Dquarkus.oidc.auth-server-url=http://localhost:8085/auth/realms/sfrealm -Dquarkus.oidc.client-id=bluecomputeweb -Dquarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826 -jar target/customer-ms-quarkus-1.0.0-SNAPSHOT-runner.jar
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2021-02-19 16:24:51,920 INFO  [io.quarkus] (main) customer-ms-quarkus 1.0.0-SNAPSHOT on JVM (powered by Quarkus 1.11.3.Final) started in 6.014s. Listening on: http://0.0.0.0:8080
2021-02-19 16:24:51,922 INFO  [io.quarkus] (main) Profile prod activated.
2021-02-19 16:24:51,923 INFO  [io.quarkus] (main) Installed features: [cdi, oidc, rest-client, rest-client-jackson, resteasy, resteasy-jackson, security]
```

#### Creating a native executable

Note: In order to run the native executable, you need to install GraalVM. For instructions on how to install it, refer [this](https://quarkus.io/guides/building-native-image).

You can create a native executable using:
```shell script
./mvnw package -Pnative
```

Note: If you get errors while executing the above command, refer this [doc](https://quarkus.io/guides/building-native-image#configuring-graalvm).

You can then execute your native executable with the below command:

```
./target/customer-ms-quarkus-1.0.0-SNAPSHOT-runner -Dibm.cn.application.couchdb.client.CouchDBClientService/mp-rest/url=http://localhost:5984 -Dcouchuser=admin -Dcouchpassword=password -Dquarkus.oidc.auth-server-url=http://localhost:8085/auth/realms/sfrealm -Dquarkus.oidc.client-id=bluecomputeweb -Dquarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826
```

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

#### Running the application using docker

- Build the JVM docker image and run the application.

Package the application.
```shell script
./mvnw package -Dquarkus.native.container-build=true
```

Build the docker image using `Dockerfile.jvm`.
```shell script
docker build -f src/main/docker/Dockerfile.jvm -t customer-ms-quarkus .
```

Run the application.
```shell script
docker run -it -d --rm -e ibm.cn.application.couchdb.client.CouchDBClientService/mp-rest/url=http://host.docker.internal:5984 -e couchuser=admin -e couchpassword=password -e quarkus.oidc.auth-server-url=http://host.docker.internal:8085/auth/realms/sfrealm -e quarkus.oidc.client-id=bluecomputeweb -e quarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826 -p 8087:8080 customer-ms-quarkus
```

- Build the native docker image and run the application.

For native docker image, package the application using native profile.
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

Build the docker image using `Dockerfile.native`.
```shell script
docker build -f src/main/docker/Dockerfile.native -t customer-ms-quarkus-native .
```

Run the application.
```shell script
docker run -it -d --rm -e ibm.cn.application.couchdb.client.CouchDBClientService/mp-rest/url=http://host.docker.internal:5984 -e couchuser=admin -e couchpassword=password -e quarkus.oidc.auth-server-url=http://host.docker.internal:8085/auth/realms/sfrealm -e quarkus.oidc.client-id=bluecomputeweb -e quarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826 -p 8087:8080 customer-ms-quarkus-native
```

### Validating the application

- Now generate a JWT Token.

To do so, run the commands below:

```
export access_token=$(\
    curl -X POST http://<REPLACE_ME_WITH_KEYCLOAK_HOST_NAME>:<REPLACE_ME_WITH_KEYCLOAK_PORT>/auth/realms/<REPLACE_ME_WITH_REALM>/protocol/openid-connect/token \
    --user <REPLACE_ME_WITH_CLIENT_ID>:<REPLACE_ME_WITH_CLIENT_SECRET> \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=<REPLACE_ME_WITH_USERNAME>&password=<REPLACE_ME_WITH_PASSWORD>&grant_type=password' | jq --raw-output '.access_token' \
 )
```

If successful, you will see something like below.

```
$ export access_token=$(\
    curl -X POST http://localhost:8085/auth/realms/sfrealm/protocol/openid-connect/token \
    --user bluecomputeweb:a297757d-d2cc-4921-8e66-971432a68826 \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=user&password=password&grant_type=password' | jq --raw-output '.access_token' \
 )
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  2217  100  2166  100    51  15169    357 --:--:-- --:--:-- --:--:-- 15146
```

- To validate the token, you can print the `access_token`.

```
$ echo $access_token
eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJTNWNfNWFuT1dsb3RsaFlxSFJjY0l4d3ROa1dTcXpQVU1SWkliWWVaYm1ZIn0.eyJleHAiOjE2MTM2NTQ0NDIsImlhdCI6MTYxMzY1Mzg0MiwianRpIjoiZTYwZTdmZWUtNWM1MS00YTAxLWJlYjItNzQ4NmY0OGQ4OWM2IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg1L2F1dGgvcmVhbG1zL3NmcmVhbG0iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiMjJjMWYyNDAtMWNjZS00NjhlLWE0YjAtMWQ3NTFlYmNiOTM1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmx1ZWNvbXB1dGV3ZWIiLCJzZXNzaW9uX3N0YXRlIjoiOGRkZjhhM2MtNDlkYy00ZjgwLWIxOWYtMGNjMWU5MWZhMWI5IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiYWRtaW4iLCJ1bWFfYXV0aG9yaXphdGlvbiIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJ1c2VyIGxhc3QiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyIiwiZ2l2ZW5fbmFtZSI6InVzZXIiLCJmYW1pbHlfbmFtZSI6Imxhc3QiLCJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20ifQ.Feee9MFZDMFGu1RV8Kbx6ZLcsc6YPTLq4bGUkFAU7oCnkII7st6Ozo9nxEK2rj4cT_-fbzs_YBErLwdcA662qZfU868jL0q0SRvNoqCjU90hf552QhkP3UdlfDhYdM683UAe1x5Kww-mraqDi8tM1rIQ6XFXf6kvJEp7ij40lCX26_D7XDLiRtf-tQ_aLKb3R_FF5oVYo50KR4Au3UyA0lKoy8f8CCzVX7XmvPOzCS2wZErIdDCuYVSCCVawIvzYZbhIr977sL6mNftBStP4GC_BoGllDrEhis6wvqy9aQL8-xjQGnk_WQ83vh8zxRhaVu-D9Ol-93kjX8FUyrzT0w
```

- To get all orders, run the following to retrieve all orders for the given customerId. Be sure to export the JWT to `access_token` as shown earlier.

```
curl -v -X GET   http://<Customer_Service_Host>:<Customer_Service_Port>/micro/customer   -H "Authorization: Bearer "$access_token
```

If successfully created, you will see something like below.

```
$ curl -v -X GET   http://localhost:8087/micro/customer   -H "Authorization: Bearer "$access_token
Note: Unnecessary use of -X or --request, GET is already inferred.
*   Trying 127.0.0.1...
* TCP_NODELAY set
* Connected to localhost (127.0.0.1) port 8087 (#0)
> GET /micro/customer HTTP/1.1
> Host: localhost:8087
> User-Agent: curl/7.54.0
> Accept: */*
> Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJTNWNfNWFuT1dsb3RsaFlxSFJjY0l4d3ROa1dTcXpQVU1SWkliWWVaYm1ZIn0.eyJleHAiOjE2MTM5ODY5NzQsImlhdCI6MTYxMzk4NjM3NCwianRpIjoiMTQ1MDA0NWEtYjRiZi00MjhlLWEyYTAtOTRmNDBiMzk0NTNkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDg1L2F1dGgvcmVhbG1zL3NmcmVhbG0iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNjRlNWZkMDktOTI1OC00YTk0LWFlMmEtMDdlNzg2MTQ4MGJmIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmx1ZWNvbXB1dGV3ZWIiLCJzZXNzaW9uX3N0YXRlIjoiMjJmYjY3ZmYtODAwZC00ZmMxLTg4NDctNWE3NzY3MmYxNDU4IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODUiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiZm9vIGJhciIsInByZWZlcnJlZF91c2VybmFtZSI6ImZvbyIsImdpdmVuX25hbWUiOiJmb28iLCJmYW1pbHlfbmFtZSI6ImJhciIsImVtYWlsIjoiZm9vQGJhci5jb20ifQ.vano1bMUF4sVOhkVMf8bESSxDX6-smHghITuV7caC5i3TIc0IC66tvTkspH2X5PDSrKXK8G2DhRE_i6ntVlgCswZlM-ymfNRRtJuwkDAXsyWwbOid3f-dVutesWghKKiirA4ZFgagctz6-wINYPqk16HyPM69rBFIemXJ2oZaLcFZn3iz9HcZVMOuDKfx9c6Z2BluMmYpPC9oD0r8txAX-0qerAKn2yiTqe4_qHfvOqPERrh8K0sThdrtq9B37QZActL3T4xCdXjJVsgYESdHqEnTzUCaW1L_LnQswQ5bYNe_CMf7pKB_q69cU_C2iQBYjrGHJd8Fcbp2jzPezMLBg
>
< HTTP/1.1 200 OK
< Transfer-Encoding: chunked
< X-CouchDB-Body-Time: 0
< Cache-Control: must-revalidate
< Server: CouchDB/3.1.1 (Erlang OTP/20)
< Content-Length: 429
< X-Couch-Request-ID: ecbc4c70b2
< Date: Mon, 22 Feb 2021 09:34:49 GMT
< Content-Type: application/json
<
{"docs":[
{"_id":"bdc0b1a0665ea82b68a107dfad00047a","_rev":"1-83b2a6a2b4670ba7b27f41ba23dabe93","username":"foo","password":"bar","email":"foo@address.com","firstName":"foo","lastName":"fooLast","imageUrl":"image"}
],
"bookmark": "g1AAAABweJzLYWBgYMpgSmHgKy5JLCrJTq2MT8lPzkzJBYorJKUkGyQZJhqYmZmmJloYJZlZJBoamKekJaYYGBiYmCeC9HHA9BGlIwsAww8e-Q",
"warning": "No matching index found, create an index to optimize query time."}

* Connection #0 to host localhost left intact
```

- You can access the swagger api at http://localhost:8080/q/swagger-ui/

![Orders swagger api](static/orders_swagger_api.png?raw=true)

Note: If you are running using docker, use `8087` instead of `8080` as port.

- To access Jaeger UI, use http://localhost:16686/ and point the service to `customer-ms-quarkus` to access the traces.

![Customer Jaeger traces](static/customer_jaeger_traces.png?raw=true)

![Customer Jaeger trace details](static/customer_jaeger_trace_details.png?raw=true)

- To perform code quality checks, run the below commands.

Do a clean install to generate necessary artifacts.

```
./mvnw clean install
```

If it is successful, you will see something like this.

```
[INFO] --- maven-install-plugin:2.4:install (default-install) @ orders-ms-quarkus ---
[INFO] Installing /Users/Hemankita1/IBM/CN_Ref/Quarkus/orders-ms-quarkus/target/orders-ms-quarkus-1.0.0-SNAPSHOT.jar to /Users/Hemankita1/.m2/repository/ibm/cn/orders-ms-quarkus/1.0.0-SNAPSHOT/orders-ms-quarkus-1.0.0-SNAPSHOT.jar
[INFO] Installing /Users/Hemankita1/IBM/CN_Ref/Quarkus/orders-ms-quarkus/pom.xml to /Users/Hemankita1/.m2/repository/ibm/cn/orders-ms-quarkus/1.0.0-SNAPSHOT/orders-ms-quarkus-1.0.0-SNAPSHOT.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  32.747 s
[INFO] Finished at: 2021-03-26T17:04:15+05:30
[INFO] ------------------------------------------------------------------------
```

Now run sonar as follows.

```
./mvnw sonar:sonar -Dsonar.host.url=http://<sonarqube_host>:<sonarqube_port> -Dsonar.login=<sonarqube_access_token>
```

To get the sonarqube access token, login to the sonarqube ui. Then go to `User` > `My Account`. Now, select `Security` and then generate a token.

If it is successful, you will see something like this.

```
$ ./mvnw sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=19abfbce59f1f73b9471ab326163c0e45800a8f3
[INFO] Scanning for projects...
[INFO]
[INFO] ----------------------< ibm.cn:orders-ms-quarkus >----------------------
[INFO] Building orders-ms-quarkus 1.0.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- sonar-maven-plugin:3.7.0.1746:sonar (default-cli) @ orders-ms-quarkus ---
[INFO] User cache: /Users/Hemankita1/.sonar/cache
[INFO] SonarQube version: 8.7.1
..........
..........
[INFO] ANALYSIS SUCCESSFUL, you can browse http://localhost:9000/dashboard?id=ibm.cn%3Aorders-ms-quarkus
[INFO] Note that you will be able to access the updated dashboard once the server has processed the submitted analysis report
[INFO] More about the report processing at http://localhost:9000/api/ce/task?id=AXhuUPfKGlc8bxlXLNnl
[INFO] Analysis total time: 15.409 s
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  18.994 s
[INFO] Finished at: 2021-03-26T17:05:03+05:30
[INFO] ------------------------------------------------------------------------
```

- Now, access http://localhost:9000/, login using the credentials admin/admin, and then you will see something like below.

![Orders SonarQube](static/orders_sonarqube.png?raw=true)

![Orders SonarQube details](static/orders_sonarqube_details.png?raw=true)

### Exiting the application

To exit the application, just press `Ctrl+C`.

If using docker, use `docker stop <container_id>`

## Conclusion

You have successfully developed and deployed the Customer Microservice and a CouchDB database locally using Quarkus framework.

## References

- https://quarkus.io/guides/getting-started
- https://quarkus.io/guides/config
- https://quarkus.io/guides/building-native-image
