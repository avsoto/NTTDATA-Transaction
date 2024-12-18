# NTTDATA Bootcamp Tech Girls Power - Project III

## Project description

This project focuses on developing a **microservices system** to manage the **transaction history** in the banking context, using **Spring Boot**, **MongoDB**, **Spring Webflux**, and **Lombok**. The system is designed to record and manage **Deposit**, **Withdrawal**, and **Transfer** transactions, allowing the querying of transaction history.

## Project Reports

### SOLID Principles Report
This report provides a detailed analysis of how the SOLID principles have been applied throughout the project. It includes explanations of each principle and examples of how they were implemented in the code to enhance maintainability, scalability, and readability.
[SOLID Principles Report](https://docs.google.com/document/d/1Vb0nyg2rv5LZgPepVVbFnu4QTl3xN5a5/edit?usp=sharing&ouid=111308656360819493585&rtpof=true&sd=true)

### Code Coverage Report
The code coverage report outlines the extent of testing performed in the project, including unit tests and integration tests. It provides insights into the percentage of code covered by tests, highlighting areas that need additional testing to ensure robustness and reliability.
[Code Coverage Report](https://docs.google.com/document/d/1QFdVu0-4UuzyyBM5JlH1KOFuXmEouJuZ/edit?usp=sharing&ouid=111308656360819493585&rtpof=true&sd=true)

### REDIS-Kafka contribution
The Redis-Kafka Contribution report explains how Redis and Kafka enhance microservices development. It covers a real-world use case demonstrating their benefits in a distributed system and a hypothetical scenario showcasing their application across the three developed microservices, highlighting improvements in data management and inter-service communication.
[REDIS-Kafka contribution](https://docs.google.com/document/d/1outrwoejPRtHxRdbWWM2boxIcfXBIMvq4y4MJqsI5Tg/edit?usp=sharing)

## System architecture

The system is based on **microservices** and follows a reactive, decoupled architecture where each microservice is responsible for a specific part of the banking process.

**Component Diagram**:  
The system consists of the following microservices:
- **Transaction Service**: Manages banking transactions.
- **Account Service**: Manages customer bank accounts.

![Component Diagram](https://raw.githubusercontent.com/avsoto/NTTDATA-Transaction/refs/heads/main/diagram/componentDiagram.jpg)

**Sequence Diagram**:  
Each microservice interacts reactively and asynchronously to process banking transactions.

![Sequence Diagram](https://raw.githubusercontent.com/avsoto/NTTDATA-Transaction/refs/heads/main/diagram/transferSecuence.jpg)

## Business rules

1. **Transaction Validations**:
   - Deposits and withdrawals are applied to specific accounts.
   - Transfers require both the destination account and amount to be specified.
   - Withdrawals or transfers cannot occur if the available balance is insufficient.

## System requirements

- **Spring Boot** to create microservices.
- **MongoDB** as a non-relational database to store transaction history.
- **Spring Webflux** for reactive programming.
- **Lombok** to reduce boilerplate code.
- **Use of Stream APIs** for efficient collection handling.

## Implementation and Connection with other microservices

The **Transaction Management** microservice is integrated with other microservices:

- **Account Service**: Connected via **RestTemplate** for balance validation in deposits, withdrawals, and transfers.  
  Repository: [NTTDATA-AccountMS](https://github.com/avsoto/NTTDATA-AccountMS)

- **Customer Service**: Connected via **WebClient** to access customer and account information.  
  Repository: [NTTDATA-CustomerMS](https://github.com/avsoto/NTTDATA-CustomerMS)

## Usage and functionality verification

The system does not have a graphical user interface. The functionalities can be verified using **Postman** by sending HTTP requests to the following endpoints:

- **POST** `/transactions/deposit`: To register a deposit.
- **POST** `/transactions/withdrawal`: To register a withdrawal.
- **POST** `/transactions/transferTo`: To register a transfer.
- **GET** `/transactions/history`: To retrieve the transaction history.

## Deliverables

- **Source Code**: All the source code for the project is available in this repository, with the microservices implemented and optimized following **SOLID principles** and **design patterns**.
- **API Documentation**: The API documentation is provided using the **Contract First** approach with **OpenAPI**, integrated with **Swagger** for interactive API exploration. It describes the interfaces and interactions between microservices, as well as the banking operations handled by the transactions microservice.
- **Diagrams**: Sequence and **component diagrams** are included, detailing the architecture of the system and the communication flow between the microservices, as well as interactions in processing banking transactions.
- **Postman Test**: A set of **Postman tests** for the APIs of the microservices, validating the functionality of operations such as deposits, withdrawals, and transfers. These tests ensure that the APIs function correctly under different conditions.
- **Unit Tests**: **Unit and reactive tests** implemented for the transactions microservice using **JUnit** and **Mockito**. A coverage report generated with **JaCoCo** and **SonarLint** is included, showing the code coverage achieved and areas for improvement.
- **Code Quality Reports**: A code quality report generated by **Checkstyle**, ensuring that the code adheres to style guidelines and best development practices.
- **Redis & Kafka Contribution Report**: An explanatory document on the contribution of **Redis** and **Kafka** to the system, with a real use case for both technologies, as well as a hypothetical use case for their application in the three developed microservices.

## Execution instructions

1. **Clone the repository**:

   ```bash
   git clone https://github.com/your-user/NTTDATA-TransactionMS.git
   cd NTTDATA-TransactionMS
   ```

2. **Install the dependencies**:

   If using Maven:

   ```bash
   mvn install
   ```

3. **Run the project**:

   To run the microservice, simply execute:

   ```bash
   mvn spring-boot:run
   ```

4. **Testing**:
    - Use Postman to test the provided endpoints.

## Swagger/OpenAPI

The Swagger/OpenAPI documentation offers a comprehensive overview of each endpoint, detailing the parameters, request bodies, and response formats.

1. To access this documentation, ensure that the service is running locally: http://localhost:8082.
2. Once confirmed, open a web browser and navigate to the following URL: http://localhost:8082/docs/swagger-ui.html.
