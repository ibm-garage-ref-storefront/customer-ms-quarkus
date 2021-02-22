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

### Run the Customer application

#### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev -Dibm.cn.application.couchdb.client.CouchDBClientService/mp-rest/url=http://localhost:5984 -Dcouchuser=admin -Dcouchpassword=password -Dquarkus.oidc.auth-server-url=http://localhost:8085/auth/realms/sfrealm -Dquarkus.oidc.client-id=bluecomputeweb -Dquarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826
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

TBD

```
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.jboss.resteasy.microprofile.client.header.ComputedHeaderValueFiller (file:/Users/Hemankita1/IBM/CN_Ref/Quarkus/customer-ms-quarkus/target/customer-ms-quarkus-1.0.0-SNAPSHOT-runner.jar) to constructor java.lang.invoke.MethodHandles$Lookup(java.lang.Class)
WARNING: Please consider reporting this to the maintainers of org.jboss.resteasy.microprofile.client.header.ComputedHeaderValueFiller
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
```

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
docker run -it -d --rm -e ibm.cn.application.couchdb.client.CouchDBClientService/mp-rest/url=http://host.docker.internal:5984 -e quarkus.datasource.username=dbuser -e couchuser=admin -e couchpassword=password -e quarkus.oidc.auth-server-url=http://host.docker.internal:8085/auth/realms/sfrealm -e quarkus.oidc.client-id=bluecomputeweb -e quarkus.oidc.credentials.secret=a297757d-d2cc-4921-8e66-971432a68826 -p 8087:8080 customer-ms-quarkus
```

- Build the native docker image and run the application.

TBD

```
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.jboss.resteasy.microprofile.client.header.ComputedHeaderValueFiller (file:/Users/Hemankita1/IBM/CN_Ref/Quarkus/customer-ms-quarkus/target/customer-ms-quarkus-1.0.0-SNAPSHOT-runner.jar) to constructor java.lang.invoke.MethodHandles$Lookup(java.lang.Class)
WARNING: Please consider reporting this to the maintainers of org.jboss.resteasy.microprofile.client.header.ComputedHeaderValueFiller
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
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

### Exiting the application

To exit the application, just press `Ctrl+C`.

If using docker, use `docker stop <container_id>`

## Conclusion

You have successfully developed and deployed the Customer Microservice and a CouchDB database locally using Quarkus framework.

## References

- https://quarkus.io/guides/getting-started
- https://quarkus.io/guides/config
- https://quarkus.io/guides/building-native-image
