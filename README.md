# Spring Boot Lab

A multi-module Spring Boot project demonstrating REST, GraphQL, WebSocket, and Messaging patterns.  
This project is intended as a learning and experimentation platform for Spring Boot features.

---

## Table of Contents

- [Features](#features)
- [Modules](#modules)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Testing](#testing)

---

## Features

- Batch job example
- Entity and Repository
- GraphQL API with queries and mutations
- Messaging module (example: ActiveMQ)
- REST API with sample endpoints
- Security Auth for creating JWTs
- WebSocket server for notifications
- JGroups network notifications
- Resilience4j Transaction limiter
- Multi-module architecture for modular and reusable code
- AOT if required

---

## Modules

| Module         | Description                                       |
|----------------|---------------------------------------------------|
| `batchjobs`    | Batch jobs                                        |
| `core`         | Shared entities, DTOs, and services               |
| `coresecurity` | Shared security library                           |
| `graphql`      | GraphQL schema, resolvers, and DTO mappings       |
| `messaging`    | Messaging components (publish/subscribe examples) |
| `restapi`      | REST controllers and endpoints                    |
| `security`     | REST auth controller which yields JWTs            |
| `websocket`    | WebSocket server and client handling              |

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+

### Building the Project

Clone the repository:

```bash
git clone https://github.com/jono-coder/spring-boot-lab.git
cd spring-boot-lab
mvn clean install

# REST API
mvn spring-boot:run -pl restapi

# GraphQL API
mvn spring-boot:run -pl graphql

# WebSocket server
mvn spring-boot:run -pl websocket
