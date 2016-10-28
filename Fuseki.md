In order to run Fuseki with docker, run:

docker run -p 3030:3030 -e JVM_ARGS=-Xmx8g -e ADMIN_PASSWORD=pw123 -it stain/jena-fuseki