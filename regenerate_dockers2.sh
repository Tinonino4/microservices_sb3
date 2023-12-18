mvn clean install
cd product-composite-service
docker build -t product-composite-service .
cd ../product-service
docker build -t product-service .
cd ../recommendation-service
docker build -t recommendation-service .
cd ../review-service
docker build -t review-service .
cd ../eureka-server
docker build -t eureka-server .
cd ../gateway
docker build -t gateway .
cd ../authorization-server
docker build -t authorization-server .
cd ../config-server
docker build -t config-server .
cd ..
docker-compose build