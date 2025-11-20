# Conversor de Moedas Assíncrono (Java + Python + RabbitMQ + Docker)
## Descrição do Projeto

Este projeto implementa um sistema de conversão de moedas assíncrono, dividido em três partes:

- API Java

Envia solicitações de conversão para o RabbitMQ

Recebe resultados de conversão (opcional)

- Cliente Python (Streamlit)

Interface gráfica simples para o usuário

Envia mensagens para o RabbitMQ solicitando a conversão

- RabbitMQ

Atua como intermediário entre os serviços

Gerencia filas de mensagens

Toda a aplicação pode ser executada com Docker e Docker Compose.