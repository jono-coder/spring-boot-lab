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

- Entity and Repository
- REST API with sample endpoints
- GraphQL API with queries and mutations
- WebSocket server for push notifications
- Messaging module (example: ActiveMQ)
- JGroups network notifications
- Multi-module architecture for modular and reusable code

---

## Modules

| Module      | Description                                           |
|------------|-------------------------------------------------------|
| `core`     | Shared entities, DTOs, and services                  |
| `restapi`  | REST controllers and endpoints                        |
| `graphql`  | GraphQL schema, resolvers, and DTO mappings          |
| `messaging`| Messaging components (publish/subscribe examples)    |
| `websocket`| WebSocket server and client handling                 |

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
