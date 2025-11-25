# Conversor de Moedas — Spring Boot + Streamlit + Threads Assíncronas

Este projeto implementa um sistema distribuído composto por:

* API em Spring Boot
* Cliente em Streamlit
* Execução assíncrona usando threads
* Requisições simultâneas para conversão de moedas
* Comunicação entre serviços via Docker Compose

O objetivo é demonstrar chamadas concorrentes e não bloqueantes, onde cada thread realiza uma conversão monetária em paralelo consultando uma API externa de taxas de câmbio.

---

## Funcionamento Geral

### API (Spring Boot)

A API possui três endpoints principais:

### `GET /convert/usd?amount=100`

Retorna a conversão de USD para BRL.

### `GET /convert/eur?amount=100`

Retorna a conversão de EUR para BRL.

### `GET /convert/both?amount=100`

Executa duas threads simultâneas:

* Uma thread realiza a conversão USD → BRL
* Outra thread realiza a conversão EUR → BRL

Cada thread consulta a taxa atual em:

```
https://api.exchangerate-api.com/v4/latest/USD
https://api.exchangerate-api.com/v4/latest/EUR
```

As respostas são agregadas e retornadas ao cliente.

---

## Cliente (Streamlit)

O cliente:

* Lê o valor digitado pelo usuário
* Dispara duas threads paralelas usando ThreadPoolExecutor
* Cada thread faz uma chamada para a API:

  * `/convert/usd`
  * `/convert/eur`
* Exibe o resultado assim que cada thread retorna
* Mantém a interface responsiva e não bloqueante

---

## Threads Utilizadas

### Na API (Java)

As classes `USDtoBRLConverter` e `EURtoBRLConverter` possuem métodos assíncronos usando `@Async` e `CompletableFuture`.

O executor é configurado em `App.java`.

### No Cliente (Python)

Usa `ThreadPoolExecutor(max_workers=2)` para executar duas chamadas HTTP simultâneas.

---

## Fluxo Completo

1. O usuário informa um valor no cliente Streamlit.
2. O cliente dispara duas threads paralelas.
3. A API recebe ambas requisições em suas próprias threads internas.
4. Cada conversor consulta a taxa em tempo real.
5. O cliente exibe os valores convertidos individualmente assim que cada resposta chega.

---

## Como Executar com Docker

Pré-requisitos:

* Docker
* Docker Compose

Na raiz do projeto, executar:

```bash
docker-compose up --build
```

Quando os serviços estiverem no ar:

### Cliente (frontend)

[http://localhost:8501](http://localhost:8501)

### API (backend)

Exemplos de testes diretos:

```
http://localhost:8080/convert/usd?amount=10
http://localhost:8080/convert/eur?amount=10
http://localhost:8080/convert/both?amount=10
```

---

## Exemplos de Retorno

### Para `/convert/usd?amount=10`

```json
{
  "from": "USD",
  "to": "BRL",
  "rate": 5.4,
  "converted": 54.0
}
```

### Para `/convert/both?amount=10`

```json
{
  "usd_to_brl": {
    "from": "USD",
    "to": "BRL",
    "rate": 5.4,
    "converted": 54.0
  },
  "eur_to_brl": {
    "from": "EUR",
    "to": "BRL",
    "rate": 6.22,
    "converted": 62.2
  }
}
```

---

## Tecnologias Utilizadas

* Java 17
* Spring Boot 3
* Python 3.11
* Streamlit
* CompletableFuture e @Async (Java)
* ThreadPoolExecutor (Python)
* Docker e Docker Compose
* API externa ExchangeRate-API

