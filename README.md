# Conversor de Moedas Distribuído — Spring Boot + Streamlit

Este projeto demonstra um sistema distribuído para conversão de moedas utilizando múltiplos serviços independentes:

* **Serviço USD → BRL (Spring Boot)**
* **Serviço EUR → BRL (Spring Boot)**
* **Cliente em Streamlit (Python)**
* **Comunicação entre containers via Docker Compose**

O objetivo é mostrar como o cliente consulta **dois serviços distintos** e exibe os resultados individualmente.

---


```mermaid
sequenceDiagram
    autonumber

    participant U as Usuário
    participant C as Cliente (Streamlit)
    participant USD as usd-service (Spring Boot)
    participant EUR as eur-service (Spring Boot)
    participant API_USD as ExchangeRate-API (latest/USD)
    participant API_EUR as ExchangeRate-API (latest/EUR)

    U->>C: Informa amount

    C->>C: Formata e valida o valor

    Note over C,USD: Chamada 1 — USD → BRL
    C->>USD: GET /convert/usd?amount=X
    USD->>API_USD: Consulta taxa USD
    API_USD-->>USD: Retorna rate USD→BRL
    USD-->>C: Envia resultado USD
    C->>C: Exibe resultado USD

    Note over C,EUR: Chamada 2 — EUR → BRL
    C->>EUR: GET /convert/eur?amount=X
    EUR->>API_EUR: Consulta taxa EUR
    API_EUR-->>EUR: Retorna rate EUR→BRL
    EUR-->>C: Envia resultado EUR
    C->>C: Exibe resultado EUR

    C-->>U: Mostra ambos os resultados na tela
```



## Visão Geral

O sistema é composto por **três containers**:

1. **usd-service**
   Serviço em Spring Boot que converte USD → BRL.

2. **eur-service**
   Serviço em Spring Boot que converte EUR → BRL.

3. **cliente**
   Aplicação Streamlit que recebe o valor digitado pelo usuário e faz duas chamadas independentes:

   * `/convert/usd` no usd-service
   * `/convert/eur` no eur-service

Não existe endpoint combinado. Cada serviço é totalmente isolado.

---

## API — Estrutura dos Serviços

Cada serviço possui seu próprio endpoint:

### Serviço USD

```
GET /convert/usd?amount=100
```

Exemplo de resposta:

```json
{
  "from": "USD",
  "to": "BRL",
  "rate": 5.40,
  "converted": 540.00
}
```

### Serviço EUR

```
GET /convert/eur?amount=100
```

Exemplo de resposta:

```json
{
  "from": "EUR",
  "to": "BRL",
  "rate": 6.22,
  "converted": 622.00
}
```

### Funcionamento Interno dos Serviços

Cada serviço:

* Consulta sua própria fonte de taxas:

  * `latest/USD`
  * `latest/EUR`
* Executa a lógica de conversão
* Retorna o valor já convertido
* Pode usar `@Async` + `CompletableFuture` internamente (dependendo da implementação)
  mas isso é interno ao serviço, o cliente não utiliza threads.

---

## Cliente (Streamlit)

O arquivo `cliente/app.py`:

* Recebe o valor digitado pelo usuário
* Formata e valida o input
* Faz duas requisições HTTP separadas, uma para cada serviço:

  * `USD_URL` (usd-service)
  * `EUR_URL` (eur-service)
* Exibe cada resultado em sua coluna individual
* Mostra erros específicos caso algum serviço falhe

### URLs utilizadas pelo cliente

```python
USD_URL = "http://usd-service:8081/convert/usd"
EUR_URL = "http://eur-service:8082/convert/eur"
```

As requisições são feitas com `requests.get()`, sem uso de threads, pools ou concorrência explícita. Cada chamada é independente.

---

## Fluxo Completo

1. O usuário digita um valor no Streamlit.
2. O cliente chama primeiro o serviço USD.
3. Exibe o resultado USD (ou erro).
4. O cliente chama o serviço EUR.
5. Exibe o resultado EUR (ou erro).
6. Cada serviço retorna seu cálculo de forma isolada.

---

## Como Executar com Docker

Pré-requisitos:

* Docker
* Docker Compose

Rodar na raiz do projeto:

```bash
docker-compose up --build
```

### Acessos

**Cliente (frontend)**
[http://localhost:8501](http://localhost:8501)

**Serviço USD**
[http://localhost:8081/convert/usd?amount=10](http://localhost:8081/convert/usd?amount=10)

**Serviço EUR**
[http://localhost:8082/convert/eur?amount=10](http://localhost:8082/convert/eur?amount=10)

---

## Exemplos de Uso

### Conversão USD → BRL

```
http://localhost:8081/convert/usd?amount=50
```

### Conversão EUR → BRL

```
http://localhost:8082/convert/eur?amount=50
```

---

## Tecnologias Utilizadas

* Java 17
* Spring Boot 3
* Python 3.11
* Streamlit
* Requests (HTTP client)
* Docker & Docker Compose
* ExchangeRate-API

---
