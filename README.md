docker build -t product-composite-service .
docker build -t product-service .
docker build -t recommendation-service .
docker build -t review-service .

for running one MS:
docker run -d -p8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" --name my-prd-srv product-service

for running dokcer compose without blocking terminal:
docker-compose up -d
docker-compose down

docker-compose exec mongodb mongosh product-db --quiet --eval "db.products.find()"
docker-compose exec mysql mysql -uuser -p review-db -e "select * from reviews"

Body example:
{                                                                                        
"productId": 8,
"Name": "product name A",
"weight":100,
"recommendations":[
{"recommendationId":1,"author":"author 1","rate":1,"content":"content 1"},
{"recommendationId":2,"author":"author 2","rate":2,"content":"content 2"},
{"recommendationId":3,"author":"author 3","rate":3,"content":"content 3"}
],
"reviews":[
{"reviewId":1,"author":"author 1","subject":"subject 1","content":"content 1"},
{"reviewId":2,"author":"author 2","subject":"subject 2","content":"content 2"},
{"reviewId":3,"author":"author 3","subject":"subject 3","content":"content 3"}
]
}

curl -X POST localhost:8080/product-composite -H "Content-Type:application/json" --data "$body"
curl -s localhost:8080/product-composite/8
curl -X DELETE localhost:8080/product-composite/8

Test with docker-compose.yml:

./regenerate_dockers.sh
docker-compose build && docker-compose up -d
http://localhost:15672/#/queues
Execute some curls
docker-compose down

Test with docker-compose-partitions.yml:

export COMPOSE_FILE=docker-compose-partitions.yml
docker-compose build && docker-compose up -d
http://localhost:15672/#/queues
Execute some curls
docker-compose down
unset COMPOSE_FILE

Test with docker-compose-kafka.yml:

export COMPOSE_FILE=docker-compose-kafka.yml
docker-compose build && docker-compose up -d
Execute some curls
docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list
docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic products
docker-compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic products --from-beginning --timeout-ms 1000 --partition 1

EUREKA:http://localhost:8761/



---
Security things:

Acquiring access tokens
- Using the client credentials grant flow

curl -k https://writer:secret-writer@localhost:8443/oauth2/token -d grant_type=client_credentials -d scope="product:read product:write" -s | jq
curl -k https://reader:secret-reader@localhost:8443/oauth2/token -d grant_type=client_credentials -d scope="product:read" -s | jq

Response:
  NTEsInNjb3BlIjpbInByb2R1Y3Q6d3JpdGUiLCJwcm9kdWN0OnJlYWQiXSwiaXNzIjoiaHR0cDovL2F1dGgtc2VydmVyOjk5OTkiLCJleHAiOjE3MDE1MTg3NTEsImlhdCI6MTcwMTUxNTE1MX0.TSNMW_-gtUYONmelU73MamXlIHZMKKtWgkmHK3yTftAtIVckOMUpSk4pT-mv9YIViX6jDb6yXuzLF7kCE8MaFeg_T8TDJQYHfD4bejUaNGfcPG4T2M0Aay6Oa9w0i8-qZJJETgayaE6IQlAh1L20SZdnt6JfRh_r5wzQdTF0NMjckVgSUpVQIYAR7ZowBT4X8_brQp1n6QwnCVcJfqGLDBvoEBPwA7bD-2o1N-puIeoL8-aTXEyZPJKx46NiMFWZABTQ6FuiyYSXokQ92J2-NVnOaWKVzmXUYFaTcxQL_vCtj2sZPUR8boBef_XdaDlAhPtyBEYLlPJJKp6n2TVy-A",
  "scope": "product:write product:read",
  "token_type": "Bearer",
  "expires_in": 3599
  }

- Using the authorization code grant flow
1. Go to the url: https://localhost:8443/oauth2/authorize?response_type=code&client_id=reader&redirect_uri=https://my.redirect.uri&scope=product:read&state=35725
2. curl -k https://reader:secret-reader@localhost:8443/oauth2/token \
-d grant_type=authorization_code \
-d client_id=reader \
-d redirect_uri=https://my.redirect.uri \
-d code=$CODE -s | jq .


- Calling protected APIs using access tokens
ACCESS_TOKEN=an-invalid-token
curl https://localhost:8443/product-composite/1 -k -H"Authorization: Bearer $ACCESS_TOKEN" -i