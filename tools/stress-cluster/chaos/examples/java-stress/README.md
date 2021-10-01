https://docs.spring.io/spring-boot/docs/2.3.0.M4/maven-plugin/reference/html/#repackage-layers

https://spring.io/blog/2020/01/27/creating-docker-images-with-spring-boot-2-3-0-m1

mvn clean package spring-boot:repackage -DserviceBusVersion=7.4.1

java -Djarmode=layertools -jar target/servicebus-long-running-0.1.jar list

mvn clean package spring-boot:repackage -DserviceBusVersion="7.4.1"

../../../../../../eng/common/scripts/stress-testing/deploy-stress-tests.ps1 `
-Login `
-PushImages `
-Repository anuchan `
-DeployId java-sb-stress-tag_2

kubectl get pods -n anuchan -l release=java-servicebus-stress-test

kubectl logs -n anuchan pod_name -c azure-deployer

kubectl logs -n anuchan pod_name

helm uninstall java-servicebus-stress-test -n anuchan