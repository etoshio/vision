# Especificação Técnica para Avaliação

Projeto multi-módulo Maven com **API** e **Worker** que consome Kafka.

## Stack
- Java 23
- Spring Boot 3.4.5
- Apache Kafka 
- Docker & Docker Compose

## Como executar
```bash
# 1) Build do projeto (gera dois JARs em api/target e worker/target)
mvn package

# 2) Limpar containers antigos
docker compose down --remove-orphans

# 3) Subir Docker
docker compose up --build
```

## Testar
```bash
# Criar usuário (gera mensagem Kafka)
curl -X POST http://localhost:8080/api/users   -H "Content-Type: application/json"   -d '{"name":"Maria Silva","email":"maria@example.com"}'

# Busque o ID no retorno e consulte:
curl http://localhost:8080/api/users/{ID}

# Atualizar dados
curl -X PUT http://localhost:8080/api/users/{ID}   -H "Content-Type: application/json"   -d '{"name":"Maria S.","email":"maria.s@example.com"}'

# Excluir
curl -X DELETE http://localhost:8080/api/users/{ID}
```
O **Worker** transforma o `name` para maiúsculas, faz append em `/data/users-processed.log` e chama `PUT /api/users/{id}/status` com `{"status":"PROCESSED","processedName":"<NOME>"}`.
