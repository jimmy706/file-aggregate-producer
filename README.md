## Prerequire
1. Docker Desktop
2. Java 17
3. Maven build tool

## Initial set up
1. Run command `docker-compose up -d` to run kafka cluster on your local machine
2. To start this producer application on your local machine, run this command: `./mvnw spring-boot:run`

## How to test
All the sales file contain inside /resources/sales folder. You can add a new csv file to this folder to test the kafka producer
> **Note**: The csv file format should be like this:
> ```
> datetime,product,quantity,price
> 1/1/23 9:10 PM,Product Name,2,1000
> ```
---
You can also upload the sales file using this API request:
```curl
curl --location 'http://localhost:8081/sales-files' \
--header 'Cookie: csrftoken=yprQAO8C3jnb3FoC0DC355KahDDiFd5i' \
--form 'file=@"/C:/Users/dungdang/Downloads/sales.csv"'
```