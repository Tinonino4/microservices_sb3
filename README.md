docker build -t product-composite-service .
docker build -t product-service .
docker build -t recommendation-service .
docker build -t review-service .

for running one MS:
docker run -d -p8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" --name my-prd-srv product-service

for running dokcer compose without blocking terminal:
docker-compose up -d
docker-compose down