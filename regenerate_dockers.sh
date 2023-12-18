cd product-composite-service
mvn clean install
docker build -t product-composite-service .
cd ../product-service
mvn clean install
docker build -t product-service .
cd ../recommendation-service
mvn clean install
docker build -t recommendation-service .
cd ../review-service
mvn clean install
docker build -t review-service .
cd ../eureka-server
mvn clean install
docker build -t eureka-server .
cd ../gateway
mvn clean install
docker build -t gateway .
cd ../authorization-server
mvn clean install
docker build -t authorization-server .
cd ../config-server
mvn clean install
docker build -t config-server .
cd ..
docker-compose build