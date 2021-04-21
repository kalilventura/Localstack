# Localstack

Alguns comandos para ajudar nos testes:

Criar o bucket:
aws --endpoint-url=http://localhost:4566 s3api create-bucket --bucket events

Listar os buckets criados:
aws --endpoint-url=http://localhost:4566 s3api list-buckets

Listar os objetos:
aws --endpoint-url=http://localhost:4566 s3api list-objects --bucket events

Criar uma tabela no Dynamo:
aws --endpoint-url=http://localhost:4566 dynamodb create-table --table-name events --attribute-definitions AttributeName=id,AttributeType=S AttributeName=name,AttributeType=S --key-schema AttributeName=id,KeyType=HASH AttributeName=name,KeyType=RANGE --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5

Descrever a tabela no Dynamo:
aws dynamodb --endpoint-url=http://localhost:4566 describe-table --table-name events

Listar os objetos na tabela:
aws dynamodb --endpoint-url=http://localhost:4566 scan --table-name events
