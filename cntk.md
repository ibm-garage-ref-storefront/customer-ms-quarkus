## Deploying the app on Openshift using Cloudnative toolkit

### Pre-requisites

- [SetUp Cloudnative toolkit](https://cloudnativetoolkit.dev/workshop/setup#3.-setup-ibm-cloud-native-toolkit-workshop)
- [Setup CLI and Terminal Shell](https://cloudnativetoolkit.dev/workshop/setup#4-setup-cli-and-terminal-shell)

## Deploying the app

- Login into the cluster using `oc login`.

- Create a new project.

```
oc sync project1
```

- Clone the `customer-ms-quarkus` repo.

```bash
git clone https://github.com/ibm-garage-ref-storefront/customer-ms-quarkus.git
cd customer-ms-quarkus
```

- Setup the database.

```bash
cd database_setup
./setup_database.sh project1
cd ..
```

- To trigger the pipeline, run the below command.

```
oc pipeline --tekton
```

  - Enter git credentials.
  - Use down/up arrow and select `ibm-java-maven`.
  - Hit Enter to enable image scanning.
  - Open the url to see the pipeline running in the OpenShift Console.

- Verify that Pipeline Run completed successfully.

- Grab the route.

```
oc get route customer-ms-quarkus --template='{{.spec.host}}'
```

You will see something like below.

```
$ oc get route customer-ms-quarkus --template='{{.spec.host}}'
customer-ms-quarkus-sf-quarkus-dev.mq-devops-6ccd7f378ae819553d37d5f2ee142bd6-0000.par01.containers.appdomain.cloud
```

- Now access the endpoint using `http://<route_url>/health`.

For instance if using the above route, it will be https://customer-ms-quarkus-sf-quarkus-dev.mq-devops-6ccd7f378ae819553d37d5f2ee142bd6-0000.par01.containers.appdomain.cloud/health.
